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

	private String emitDTDPrefix() {
		return "\t<!Element ";
	}

	private String emitDTDPostfix() {
		return ">";
	}
	
	public void emitDTDFormat() {
		System.out.print(this.emitDTDPrefix() + this.column + " ");
		for(String data : this.dtdobjectmap.keySet()) {
			System.out.print(this.dtdobjectmap.get(data).getElementFormat() + " ");
		}
		System.out.println(this.emitDTDPostfix());
	}
}
