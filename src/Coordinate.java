package org.peg4d.data;


public class Coordinate {
	private int ltpos = -1;
	private int rtpos = -1;

	public Coordinate() {

	}

	public void setLpos(int ltpos) {
		this.ltpos = ltpos;
	}

	public void setRpos(int rtpos) {
		this.rtpos = rtpos;
	}

	public int getLtpos() {
		return this.ltpos;
	}

	public int getRtpos() {
		return this.rtpos;
	}

	public int getRange() {
		return this.rtpos - this.ltpos;
	}
}
