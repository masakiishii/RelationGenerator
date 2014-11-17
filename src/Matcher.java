package org.peg4d.data;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


public abstract class Matcher {

	abstract public Set<String> getSchema(String tablename);

	public void insertDelimiter(LappingObject node, StringBuffer sbuf, int index) {
		if (index != node.size() - 1) {
			sbuf.append("|");
		}
	}

	public Map<String, ArrayList<ArrayList<String>>> getTable() {
		return null;
	}

	public String getColumnData(LappingObject subnode, LappingObject tablenode, String column) {
		return null;
	}

	public void getTupleData(LappingObject subnode, LappingObject tablenode, String tablename, SubNodeDataSet columns) {

	}

	public void getTupleListData(LappingObject subnode, LappingObject tablenode, String tablename, SubNodeDataSet columns) {

	}

	public boolean isTableName(String value) {
		return false;
	}

	public void matching(LappingObject root) {
		
	}

	public void match(LappingObject root) {
		
	}
}
