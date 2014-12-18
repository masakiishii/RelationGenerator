package org.peg4d.data;

import java.util.Map;

public class DTDAllList {
	private DTDLineList[] dtdall      = null;
	private int           tablenumber = -1;
	public DTDAllList(int tablenumber) {
		this.dtdall      = new DTDLineList[tablenumber];
		this.tablenumber = tablenumber;
	}

	public void setDTDAllList(DTDLineList dtdlinelist, int index) {
		this.dtdall[index] = dtdlinelist;
	}

	public DTDLineList[] getDTDAllList() {
		return this.dtdall;
	}

	private void emitPreDocType() {
		DTDLine rootline = this.dtdall[0].getDTDLine()[0];
		System.out.println("<!DOCTYPE " + rootline.getColumn() + " [");
	}

	private void emitPostDocType() {
		System.out.println("]>");
	}

	public void emitDTDFormat(Map<String, Map<String, WrapperObject>> elementtypemap) {
		this.emitPreDocType();
		for(int i = 0; i < this.tablenumber; i++) {
			dtdall[i].emitDTDFormat(elementtypemap);
		}
		this.emitPostDocType();
	}
}
