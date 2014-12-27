package org.peg4d.data;

import java.util.Map;


public abstract class TableBuilder {

	abstract public void initSchema();

	public void insertDelimiter(WrapperObject node, StringBuilder builder, int index) {
		if(node.size() - 1 != 0) {
			builder.append(",");
		}
	}

	public void getListData(WrapperObject sibling, StringBuilder builder) {
		
	}

	abstract public void getTerminalData(WrapperObject sibling, StringBuilder builder);

	abstract public void getNonTerminalData(WrapperObject sibling, StringBuilder builder);

	abstract public void settingData(WrapperObject parent, StringBuilder builder);

	abstract public void setTableData(WrapperObject node);

	abstract public void buildRootTable(WrapperObject node);

	abstract public void generateRootColumns();

	abstract public void build(WrapperObject node);

	public Map<String, String> getRootTable(WrapperObject node) {
		return null;
	}
}
