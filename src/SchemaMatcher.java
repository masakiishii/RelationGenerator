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
	private CSVGenerator                              generator = null;
	private RootTableBuilder                          builder   = null;
	public SchemaMatcher(Map<String, SubNodeDataSet> schema) {
		this.schema    = schema;
		this.generator = new CSVGenerator();
		this.builder   = new RootTableBuilder();
		this.initTable();
	}

	private void initTable() {
		this.table = new HashMap<String, ArrayList<ArrayList<String>>>();
		for(String column : this.schema.keySet()) {
			this.table.put(column, new ArrayList<ArrayList<String>>());
		}
	}

	public Map<String, ArrayList<ArrayList<String>>> getTable() {
		return this.table;
	}

	public Set<String> getSchema(String tablename) {
		return this.schema.get(tablename).getFinalColumnSet();
	}

	private void insertDelimiter(LappingObject node, StringBuffer sbuf, int index) {
		if (index != node.size() - 1) {
			sbuf.append("|");
		}
	}

	private String escapeData(String data) {
		return data.replace("\n", "\\n").replace("\t", "\\t");
	}

	private void appendNonTermnialData(LappingObject node, int index, StringBuffer sbuf) {
		sbuf.append(this.escapeData(node.get(index).getText()));
		sbuf.append(":");
		sbuf.append(node.getObjectId());
	}

	private void appendNonTerminalListData(LappingObject node, StringBuffer sbuf) {
		LappingObject child = node.get(0);
		for(int i = 0; i < child.size(); i++) {
			this.appendNonTermnialData(child.get(i), 0, sbuf);
		}
	}

	private void getListData(LappingObject node, StringBuffer sbuf) {
		for (int i = 0; i < node.size(); i++) {
			node.get(i).visited();
			if(node.get(i).isTerminal()) {
				sbuf.append(node.get(i).getText().toString());
			}
			else {
				this.appendNonTerminalListData(node, sbuf);
			}
			this.insertDelimiter(node, sbuf, i);
		}
	}

	private void getSiblListData(LappingObject node, StringBuffer sbuf) {
		for (int i = 0; i < node.size(); i++) {
			LappingObject child = node.get(i);
			if (child.get(0).isTerminal()) {
				this.appendNonTermnialData(child, 0, sbuf);
			}
			this.insertDelimiter(node, sbuf, i);
		}
	}

	private void getSiblData(LappingObject node, StringBuffer sbuf) {
		node.get(0).visited();
		if (node.get(0).isTerminal()) {
			this.appendNonTermnialData(node, 0, sbuf);
		} else {
			this.getSiblListData(node, sbuf);
		}
	}

	private void travaseSubTree(LappingObject node, StringBuffer sbuf) {
		if(node.getTag().toString().equals("List")) { //FIXME
			this.getListData(node, sbuf);
		}
		else {
			this.getSiblData(node, sbuf);
		}
	}

	private void checkingSubNodeType(LappingObject node, StringBuffer sbuf) {
		node.visited();
		if(node.isTerminal()) {
			sbuf.append(this.escapeData(node.getText()));
		}
		else {
			this.travaseSubTree(node, sbuf);
		}
	}

	private void matchingSubNode(LappingObject node, StringBuffer sbuf) {
		node.visited();
		LappingObject parent = node.getParent();
		for(int i = 1; i < parent.size(); i++) {
			LappingObject sibling = parent.get(i);
			this.checkingSubNodeType(sibling, sbuf);
			this.insertDelimiter(parent, sbuf, i);
		}
	}

	private String getColumnString(StringBuffer sbuf) {
		return sbuf.length() > 0 ? sbuf.toString() : null;
	}

	private void checkMatchingSubNode(LappingObject node, String column, StringBuffer sbuf) {
		if(node.getText().equals(column)) {
			this.matchingSubNode(node, sbuf);
		}
	}

	@Override
	public String getColumnData(LappingObject subnode, LappingObject tablenode, String column) {
		StringBuffer sbuf = new StringBuffer();
		Queue<LappingObject> queue = new LinkedList<LappingObject>();
		queue.offer(subnode);
		while(!queue.isEmpty()) {
			LappingObject node = queue.poll();
			this.checkMatchingSubNode(node, column, sbuf);
			for(int index = 0; index < node.size(); index++) {
				if(!node.equals(tablenode)) {
					queue.offer(node.get(index));
				}
			}
		}
		return this.getColumnString(sbuf);
	}

	private void getFieldData(String column, ArrayList<String> columndata, LappingObject subnode, LappingObject tablenode) {
		if(column.equals("OBJECTID")) {
			columndata.add(String.valueOf(subnode.getObjectId()));
		}
		else {
			String data = this.getColumnData(subnode, tablenode, column);
			columndata.add(data);
		}
	}

	@Override
	public void getTupleData(LappingObject subnode, LappingObject tablenode, String tablename, SubNodeDataSet columns) {
		ArrayList<ArrayList<String>> tabledata = this.table.get(tablename);
		ArrayList<String> columndata = new ArrayList<String>();
		for(String column : columns.getFinalColumnSet()) {
			this.getFieldData(column, columndata, subnode, tablenode);
		}
		tabledata.add(columndata);
	}

	@Override
	public void getTupleListData(LappingObject subnode, LappingObject tablenode, String tablename, SubNodeDataSet columns) {
		LappingObject listnode = subnode.get(1);
		for (int i = 0; i < listnode.size(); i++) {
			this.getTupleData(listnode.get(i), tablenode, tablename, columns);
		}
	}

	@Override
	public boolean isTableName(String value) {
		return this.schema.containsKey(value) ? true : false;
	}

	private void checkTreeType(LappingObject parent, LappingObject child) {
		child.visited();
		parent.visited();
		String tablename = child.getText();
		if (parent.get(1).getTag().toString().equals("List")) {
			this.getTupleListData(parent, child, tablename, this.schema.get(tablename));
		} else {
			this.getTupleData(parent, child, tablename, this.schema.get(tablename));
		}
	}

	@Override
	public void matching(LappingObject root) {
		Queue<LappingObject> queue = new LinkedList<LappingObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			LappingObject parent = queue.poll();
			if(parent.isTerminal()) {
				continue;
			}
			LappingObject child = parent.get(0);
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
	public void match(LappingObject root) {
		this.matching(root);
		this.builder.build(root);
		this.generator.generate(this);
	}
}
