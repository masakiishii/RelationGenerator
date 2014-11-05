package org.peg4d.data;

import java.util.ArrayList;
import java.util.Map;


public abstract class Matcher {

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
