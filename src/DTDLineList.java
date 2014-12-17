package org.peg4d.data;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

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
}
