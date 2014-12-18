package org.peg4d.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class DTDMatcher extends Matcher {
	private Map<String, SubNodeDataSet>               schema         = null;
	private Map<String, ArrayList<ArrayList<String>>> table          = null;
	private Generator                                 generator      = null;
	private TableBuilder                              builder        = null;
	private Map<String, Map<String, String>>          elementtypemap = null;

	public DTDMatcher(Map<String, SubNodeDataSet> schema) {
		this.schema         = schema;
		this.generator      = new DTDGenerator();
		this.builder        = new DTDRootBuilder();
		this.elementtypemap = new LinkedHashMap<String, Map<String,String>>();
		this.initTable();
	}

	private void initTable() {
		this.table = new HashMap<String, ArrayList<ArrayList<String>>>();
		for(final String column : this.schema.keySet()) {
			this.table.put(column, new ArrayList<ArrayList<String>>());
		}
	}

	public Map<String, Map<String, String>> getElementTypeMap() {
		return this.elementtypemap;
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
		this.getSiblData(node, buffer);
	}

	private void checkingSubNodeType(WrapperObject node, StringBuilder buffer) {
		node.visited();
		if(node.isTerminal()) {
			buffer.append(node.getTag());
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

	private String getTerminalString(StringBuilder buffer) {
		return buffer.length() > 0 ? buffer.toString() : null;
	}

	private void checkMatchingSubNode(WrapperObject node, String column, StringBuilder buffer, Map<String, String> columntypemap) {
		if(node.getText().equals(column)) {
			columntypemap.put(column, node.getParent().getTag().toString());
			this.matchingSubNode(node, buffer);
		}
	}

	@Override
	public String getColumnData(WrapperObject subnode, WrapperObject tablenode, String column) {
		return null;
	}


	public String getColumnData(WrapperObject subnode, WrapperObject tablenode, String column, Map<String, String> columntypemap) {
		final StringBuilder buffer = new StringBuilder();
		final Queue<WrapperObject> queue = new LinkedList<WrapperObject>();
		queue.offer(subnode);
		while(!queue.isEmpty()) {
			final WrapperObject node = queue.poll();
			this.checkMatchingSubNode(node, column, buffer, columntypemap);
			for(int index = 0; index < node.size(); index++) {
				if(!node.equals(tablenode)) {
					queue.offer(node.get(index));
				}
			}
		}
		return this.getTerminalString(buffer);
	}

	private void getFieldData(String column, ArrayList<String> columndata, WrapperObject subnode, WrapperObject tablenode, Map<String, String> columntypemap) {
		if(column.equals("OBJECTID")) {
			columndata.add(String.valueOf(subnode.getObjectId()));
		}
		else {
			final String data = this.getColumnData(subnode, tablenode, column, columntypemap);
			columndata.add(data);
		}
	}

	@Override
	public void getTupleData(WrapperObject subnode, WrapperObject tablenode, String tablename, SubNodeDataSet columns) {
		final ArrayList<ArrayList<String>> tabledata = this.table.get(tablename);
		final ArrayList<String> columndata = new ArrayList<String>();
		final Map<String, String> columntypemap = new LinkedHashMap<String, String>();
		for(final String column : columns.getFinalColumnSet()) {
			this.getFieldData(column, columndata, subnode, tablenode, columntypemap);
			this.elementtypemap.put(tablename, columntypemap);
		}
		this.elementtypemap.put("root", null);
		tabledata.add(columndata);
	}

	@Override
	public boolean isTableName(String value) {
		return this.schema.containsKey(value) ? true : false;
	}

	private void getData(WrapperObject parent, WrapperObject child) {
		child.visited();
		parent.visited();
		final String tablename = child.getText();
		this.getTupleData(parent, child, tablename, this.schema.get(tablename));
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
				this.getData(parent, child);
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
		this.generator.generate(this, this.builder.getRootTable(root));
	}
}
