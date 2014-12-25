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

	private String emitDTDPrefix(WrapperObject node) {
		if(node == null) {
			return "\t<!ELEMENT " + this.column;
		}
		String parenttag = node.getParent().getTag().toString();
		if(parenttag.equals("Element")) {
			return "\t<!ELEMENT " + this.column;
		}
		else if(parenttag.equals("Attr")) {
			return "\t<!ATTLIST " + node.getParent().getParent().get(0).getText() + " " + this.column;
		}
		return null;
	}

	private String emitDTDPostfix(WrapperObject node) {
		if(node == null) {
			return ">";
		}
		String parenttag = node.getParent().getTag().toString();
		if(parenttag.equals("Element")) {
			return ">";
		}
		else if(parenttag.equals("Attr")) {
			return " #REQUIRED >";
		}
		return null;
	}
	
	public void emitDTDFormat(Map<String, WrapperObject> typemap) {
		if(typemap == null) {
			System.out.print(this.emitDTDPrefix(null) + " ");
		}
		else {
			System.out.print(this.emitDTDPrefix(typemap.get(this.column)) + " ");
		}
		for(String data : this.dtdobjectmap.keySet()) {
			if(typemap != null && typemap.containsKey(data) && typemap.get(data).getParent().getTag().toString().equals("Attr")) {
				System.out.print("Empty ");
				continue;
			}
			System.out.print(this.dtdobjectmap.get(data).getElementFormat() + " ");
		}
		if(typemap != null) {
			System.out.println(this.emitDTDPostfix(typemap.get(this.column)));
		}
		else {
			System.out.println(this.emitDTDPostfix(null));
		}
	}
}
