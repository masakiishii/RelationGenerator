package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DTDRootBuilder extends TableBuilder {
	public ArrayList<String>   schema = null;
	public Map<String, String> table  = null;
	public DTDRootBuilder() {
		this.schema = new ArrayList<String>();
		this.table  = new LinkedHashMap<String, String>();
		this.initSchema();
	}

	@Override
	public void initSchema() {
		this.schema.add("OBJECTID");
		this.schema.add("COLUMN");
		this.schema.add("VALUE");
	}

	@Override
	public void getTerminalData(WrapperObject sibling, StringBuilder builder) {
		builder.append(sibling.getText());
		sibling.visited();
	}

	@Override
	public void getNonTerminalData(WrapperObject sibling, StringBuilder builder) {
		final WrapperObject grandchild = sibling.get(0);
		if(grandchild.isTerminal()) {
			builder.append(grandchild.getText());
		}
		else {
			builder.append(sibling.getTag().toString());
		}
	}

	@Override
	public void settingData(WrapperObject parent, StringBuilder builder) {
		for(int i = 1; i < parent.size(); i++) {
			final WrapperObject sibling = parent.get(i);
			if(sibling.isTerminal()) {
				this.getTerminalData(sibling, builder);
			}
			else {
				this.getNonTerminalData(sibling, builder);
			}
			this.insertDelimiter(parent, builder, i);
		}
	}

	@Override
	public void setTableData(WrapperObject node) {
		final WrapperObject parent  = node.getParent();
		final String        column  = node.getText();
		final String        key     = String.valueOf(parent.getObjectId());
		final StringBuilder builder = new StringBuilder();
		builder.append(column);
		builder.append("\t");
		this.settingData(parent, builder);
		this.table.put(key, builder.toString());
	}

	@Override
	public void buildRootTable(WrapperObject node) {
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

	@Override
	public void generateRootColumns() {
		for(int i = 0; i < this.schema.size(); i++) {
			System.out.print(this.schema.get(i) + "\t");
		}
		System.out.println();
	}

	@Override
	public Map<String, String> getRootTable(WrapperObject node) {
		this.buildRootTable(node);
		return this.table;
	}

	@Override
	public void build(WrapperObject node) {
		this.generateRootColumns();
		this.buildRootTable(node);
		for(final String key : this.table.keySet()) {
			System.out.println(key + "\t" + this.table.get(key));
		}
	}
}
