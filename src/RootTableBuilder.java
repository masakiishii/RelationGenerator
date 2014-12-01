package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RootTableBuilder {
	private ArrayList<String>   schema = null;
	private Map<String, String> table  = null;
	public RootTableBuilder() {
		this.schema = new ArrayList<String>();
		this.table  = new LinkedHashMap<String, String>();
		this.initSchema();
	}

	private void initSchema() {
		this.schema.add("OBJECTID");
		this.schema.add("COLUMN");
		this.schema.add("VALUE");
	}

	private void insertDelimiter(WrapperObject node, StringBuilder builder, int index) {
		if (index != node.size() - 1) {
			builder.append("|");
		}
	}

	private void getListData(WrapperObject sibling, StringBuilder builder) {
		for(int i = 0; i < sibling.size(); i++) {
			if(sibling.get(i).isTerminal()) {
				sibling.get(i).visited();
				builder.append(sibling.get(i).getText());
			}
			else {
				builder.append(sibling.get(i).getTag().toString());
				builder.append(":");
				builder.append(sibling.get(i).getObjectId());
			}
			this.insertDelimiter(sibling, builder, i);
		}
	}

	private void getTerminalData(WrapperObject sibling, StringBuilder builder) {
		builder.append(sibling.getText());
		sibling.visited();
	}

	private void getNonTerminalData(WrapperObject sibling, StringBuilder builder) {
		final WrapperObject grandchild = sibling.get(0);
		if(grandchild.isTerminal()) {
			builder.append(grandchild.getText());
		}
		else {
			builder.append(sibling.getTag().toString());
		}
		builder.append(":");
		builder.append(sibling.getObjectId());
	}
	
	private void settingData(WrapperObject parent, StringBuilder builder) {
		for(int i = 1; i < parent.size(); i++) {
			final WrapperObject sibling = parent.get(i);
			if(sibling.isTerminal()) {
				this.getTerminalData(sibling, builder);
			}
			else if(sibling.getTag().toString().equals("List")) {
				this.getListData(sibling, builder);
			}
			else {
				this.getNonTerminalData(sibling, builder);
			}
			this.insertDelimiter(parent, builder, i);
		}
	}
	
	private void setTableData(WrapperObject node) {
		final WrapperObject parent  = node.getParent();
		final String        column  = node.getText();
		final String        key     = String.valueOf(parent.getObjectId());
		final StringBuilder builder = new StringBuilder();
		builder.append(column);
		builder.append("\t");
		this.settingData(parent, builder);
		this.table.put(key, builder.toString());
	}

	private void buildRootTable(WrapperObject node) {
		if(node == null) {
			return;
		}
		if(node.isVisitedNode()) {
			return;
		}
		if(node.isTerminal() && !node.isVisitedNode()) {
			this.setTableData(node);
		}
		for(int i = 0; i < node.size(); i++) {
			this.buildRootTable(node.get(i));
		}
	}

	private void generateRootColumns() {
		for(int i = 0; i < this.schema.size(); i++) {
			System.out.print(this.schema.get(i) + "\t");
		}
		System.out.println();
	}

	public void build(WrapperObject node) {
		this.generateRootColumns();
		this.buildRootTable(node);
		for(final String key : this.table.keySet()) {
			System.out.println(key + "\t" + this.table.get(key));
		}
		System.out.println("----------------------------------");
		System.out.println();
		System.out.println();
		System.out.println();
	}
}
