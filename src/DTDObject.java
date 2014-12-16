package org.peg4d.data;

public class DTDObject {
	private String eletype   = null;
	private String element   = null;
	private String parentele = null;
	private int    tuplesize = -1;
	private int[]  countlist = null;

	public DTDObject(String element, int tuplesize) {
		this.element   = element;
		this.tuplesize = tuplesize;
		this.countlist = new int[tuplesize];
	}

	public String getElement() {
		return this.element;
	}

	public int[] getCountList() {
		return this.countlist;
	}

	public void setCountList(int count, int index) {
		this.countlist[index] = count;
	}
}
