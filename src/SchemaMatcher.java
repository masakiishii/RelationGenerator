package org.peg4d.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class SchemaMatcher extends Matcher {
	private Map<String, SubNodeDataSet>               schema    = null;
	private Map<String, ArrayList<ArrayList<String>>> table     = null;
	private Generator                                 generator = null;
	private RootTableBuilder                          builder   = null;
	public SchemaMatcher(Map<String, SubNodeDataSet> schema) {
		this.schema    = schema;
		this.generator = new CSVGenerator();
		this.builder   = new RootTableBuilder();
		this.initTable();
	}

	private void initTable() {
		this.table = new HashMap<String, ArrayList<ArrayList<String>>>();
		for(final String column : this.schema.keySet()) {
			this.table.put(column, new ArrayList<ArrayList<String>>());
		}
	}

	public Map<String, ArrayList<ArrayList<String>>> getTable() {
		return this.table;
	}

	public Set<String> getSchema(String tablename) {
		return this.schema.get(tablename).getFinalColumnSet();
	}

	private String escapeData(String data) {
		return data.replace("\n", "\\n").replace("\t", "\\t");
	}

	private void appendNonTermnialData(WrapperObject node, int index, StringBuilder buffer) {
		buffer.append(this.escapeData(node.get(index).getText()));
		buffer.append(":");
		buffer.append(node.getObjectId());
	}

	private void appendNonTerminalListData(WrapperObject node, StringBuilder buffer) {
		final WrapperObject child = node.get(0);
		for(int i = 0; i < child.size(); i++) {
			this.appendNonTermnialData(child.get(i), 0, buffer);
		}
	}

	private void getListData(WrapperObject node, StringBuilder buffer) {
		for (int i = 0; i < node.size(); i++) {
			node.get(i).visited();
			if(node.get(i).isTerminal()) {
				buffer.append(node.get(i).getText().toString());
			}
			else {
				this.appendNonTerminalListData(node, buffer);
			}
			this.insertDelimiter(node, buffer, i);
		}
	}

	private void getSiblListData(WrapperObject node, StringBuilder buffer) {
		for (int i = 0; i < node.size(); i++) {
			final WrapperObject child = node.get(i);
			if (child.get(0).isTerminal()) {
				this.appendNonTermnialData(child, 0, buffer);
			}
			this.insertDelimiter(node, buffer, i);
		}
	}

	private void getSiblData(WrapperObject node, StringBuilder buffer) {
		node.get(0).visited();
		if (node.get(0).isTerminal()) {
			this.appendNonTermnialData(node, 0, buffer);
		} else {
			this.getSiblListData(node, buffer);
		}
	}

	private void travaseSubTree(WrapperObject node, StringBuilder buffer) {
		if(node.getTag().toString().equals("List")) { //FIXME
			this.getListData(node, buffer);
		}
		else {
			this.getSiblData(node, buffer);
		}
	}

	private void checkingSubNodeType(WrapperObject node, StringBuilder buffer) {
		node.visited();
		if(node.isTerminal()) {
			buffer.append(this.escapeData(node.getText()));
		}
		else {
			this.travaseSubTree(node, buffer);
		}
	}

	private void matchingSubNode(WrapperObject node, StringBuilder buffer) {
		node.visited();
		final WrapperObject parent = node.getParent();
		for(int i = 1; i < parent.size(); i++) {
			final WrapperObject sibling = parent.get(i);
			this.checkingSubNodeType(sibling, buffer);
			this.insertDelimiter(parent, buffer, i);
		}
	}

	private String getColumnString(StringBuilder buffer) {
		return buffer.length() > 0 ? buffer.toString() : null;
	}

	private void checkMatchingSubNode(WrapperObject node, String column, StringBuilder buffer) {
		if(node.getText().equals(column)) {
			this.matchingSubNode(node, buffer);
		}
	}

	@Override
	public String getColumnData(WrapperObject subnode, WrapperObject tablenode, String column) {
		final StringBuilder buffer = new StringBuilder();
		final Queue<WrapperObject> queue = new LinkedList<WrapperObject>();
		queue.offer(subnode);
		while(!queue.isEmpty()) {
			final WrapperObject node = queue.poll();
			this.checkMatchingSubNode(node, column, buffer);
			for(int index = 0; index < node.size(); index++) {
				if(!node.equals(tablenode)) {
					queue.offer(node.get(index));
				}
			}
		}
		return this.getColumnString(buffer);
	}

	private void getFieldData(String column, ArrayList<String> columndata, WrapperObject subnode, WrapperObject tablenode) {
		if(column.equals("OBJECTID")) {
			columndata.add(String.valueOf(subnode.getObjectId()));
		}
		else {
			final String data = this.getColumnData(subnode, tablenode, column);
			columndata.add(data);
		}
	}

	@Override
	public void getTupleData(WrapperObject subnode, WrapperObject tablenode, String tablename, SubNodeDataSet columns) {
		final ArrayList<ArrayList<String>> tabledata = this.table.get(tablename);
		final ArrayList<String> columndata = new ArrayList<String>();
		for(final String column : columns.getFinalColumnSet()) {
			this.getFieldData(column, columndata, subnode, tablenode);
		}
		tabledata.add(columndata);
	}

	@Override
	public void getTupleListData(WrapperObject subnode, WrapperObject tablenode, String tablename, SubNodeDataSet columns) {
		final WrapperObject listnode = subnode.get(1);
		for (int i = 0; i < listnode.size(); i++) {
			this.getTupleData(listnode.get(i), tablenode, tablename, columns);
		}
	}

	@Override
	public boolean isTableName(String value) {
		return this.schema.containsKey(value) ? true : false;
	}

	private void checkTreeType(WrapperObject parent, WrapperObject child) {
		child.visited();
		parent.visited();
		final String tablename = child.getText();
		if (parent.get(1).getTag().toString().equals("List")) {
			this.getTupleListData(parent, child, tablename, this.schema.get(tablename));
		} else {
			this.getTupleData(parent, child, tablename, this.schema.get(tablename));
		}
	}

	@Override
	public void matching(WrapperObject root) {
		final Queue<WrapperObject> queue = new LinkedList<WrapperObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			final WrapperObject parent = queue.poll();
			if(parent.isTerminal()) {
				continue;
			}
			final WrapperObject child = parent.get(0);
			if(child.isTerminal() && this.isTableName(child.getText())) {
				this.checkTreeType(parent, child);
				continue;
			}
			for(int index = 0; index < parent.size(); index++) {
				queue.offer(parent.get(index));
			}
		}
	}

	@Override
	public void match(WrapperObject root) {
		this.matching(root);
		this.builder.build(root);
		this.generator.generate(this);
	}
}
