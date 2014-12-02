package org.peg4d.data;


public class Coordinate {
	private int ltpos = -1;
	private int rtpos = -1;

	public Coordinate() {

	}

	public final void setLpos(int ltpos) {
		this.ltpos = ltpos;
	}

	public final void setRpos(int rtpos) {
		this.rtpos = rtpos;
	}

	public final int getLtpos() {
		return this.ltpos;
	}

	public final int getRtpos() {
		return this.rtpos;
	}

	public final int getRange() {
		return this.rtpos - this.ltpos;
	}

	public final boolean isChildNode(Coordinate other) {
		return this.getLtpos() < other.getLtpos() && other.getRtpos() < this.getRtpos();
	}
}
