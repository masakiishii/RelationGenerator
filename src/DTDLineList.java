package org.peg4d.data;

import java.util.Map;


public class DTDLineList {
	private String    tablename   = null;
	private DTDLine[] dtdlinelist = null;
	private int       listsize    = -1;
	public DTDLineList(String tablename, int listsize) {
		this.tablename   = tablename;
		this.dtdlinelist = new DTDLine[listsize];
		this.listsize = listsize;
	}

	public void setDTDLine(DTDLine dtdline, int index) {
		this.dtdlinelist[index] = dtdline;
	}

	public DTDLine[] getDTDLine() {
		return this.dtdlinelist;
	}

	public void emitDTDFormat(Map<String, Map<String, WrapperObject>> elementtypemap) {
		System.out.println("\t<!-- tablename: " + this.tablename + "   -->");
		System.out.println();
		for(int i = 0; i < this.listsize; i++) {
			this.dtdlinelist[i].emitDTDFormat(elementtypemap.get(tablename));
		}
		System.out.println();
		System.out.println();
	}
}
