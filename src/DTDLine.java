package org.peg4d.data;

import java.util.Map;


public class DTDLine {
	private String                 column        = null;
	private Map<String, DTDObject> dtdobjectmap  = null;
	
	public DTDLine(String column, Map<String, DTDObject> dtdobjectmap) {
		this.column       = column;
		this.dtdobjectmap = dtdobjectmap;
	}
	
	public String getColumn() {
		return this.column;
	}

	public Map<String, DTDObject> getDTDObjectMap() {
		return this.dtdobjectmap;
	}

	
}
