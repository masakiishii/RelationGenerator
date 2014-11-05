package org.peg4d.data;

public class FixedSchemaMatcher extends Matcher {

	@Override
	public String getColumnData(LappingObject subnode, LappingObject tablenode, String column) {
		return null;
	}

	@Override
	public void getTupleData(LappingObject subnode, LappingObject tablenode, String tablename, SubNodeDataSet columns) {

	}

	@Override
	public void getTupleListData(LappingObject subnode, LappingObject tablenode, String tablename, SubNodeDataSet columns) {

	}

	@Override
	public boolean isTableName(String value) {
	
		return false;
	}

	@Override
	public void matching(LappingObject root) {
		
	}

	@Override
	public void match(LappingObject root) {
		
	}

}
