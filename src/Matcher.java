package org.peg4d.data;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


public abstract class Matcher {

	abstract public Set<String> getSchema(String tablename);

	public void insertDelimiter(WrapperObject node, StringBuffer sbuf, int index) {
		if (index != node.size() - 1) {
			sbuf.append("|");
		}
	}

	public Map<String, ArrayList<ArrayList<String>>> getTable() {
		return null;
	}

	public String getColumnData(WrapperObject subnode, WrapperObject tablenode, String column) {
		return null;
	}

	public void getTupleData(WrapperObject subnode, WrapperObject tablenode, String tablename, SubNodeDataSet columns) {

	}

	public void getTupleListData(WrapperObject subnode, WrapperObject tablenode, String tablename, SubNodeDataSet columns) {

	}

	public boolean isTableName(String value) {
		return false;
	}

	public void matching(WrapperObject root) {
		
	}

	public void match(WrapperObject root) {
		
	}
}
