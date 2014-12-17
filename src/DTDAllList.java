package org.peg4d.data;

public class DTDAllList {
	private DTDLineList[] dtdalllist  = null;
	private int           tablenumber = -1;
	public DTDAllList(int tablenumber) {
		this.dtdalllist  = new DTDLineList[tablenumber];
		this.tablenumber = tablenumber;
	}

	public void setDTDAllList(DTDLineList dtdlinelist, int index) {
		this.dtdalllist[index] = dtdlinelist;
	}

	public DTDLineList[] getDTDAllList() {
		return this.dtdalllist;
	}
}
