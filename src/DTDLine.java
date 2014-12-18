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

	private String emitDTDPrefix(String type) {
		if(type == null || type.equals("Element")) {
			return "\t<!ELEMENT ";
		}
		else if(type.equals("Attr")) {
			return "\t<!ATTLIST ";
		}
		return null;
	}

	private String emitDTDPostfix() {
		return ">";
	}
	
	public void emitDTDFormat(Map<String, String> typemap) {
		if(typemap == null) {
			System.out.print(this.emitDTDPrefix(null) + this.column + " ");
		}
		else {
			System.out.print(this.emitDTDPrefix(typemap.get(this.column)) + this.column + " ");
		}
		for(String data : this.dtdobjectmap.keySet()) {
			System.out.print(this.dtdobjectmap.get(data).getElementFormat() + " ");
		}
		System.out.println(this.emitDTDPostfix());
	}
}
