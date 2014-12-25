package org.peg4d.data;

public class DTDObject {
	private ElementType eletype      = null;
	private String      element      = null;
	private String      parentele    = null;
	private int         tuplesize    = -1;
	private int[]       countlist    = null;
	private int         countforroot = -1;

	public DTDObject(String element, int tuplesize) {
		this.element   = element;
		this.tuplesize = tuplesize;
		this.countlist = new int[tuplesize];
		this.countforroot   = 0;
	}

	public DTDObject() {
		
	}

	public void incrementCount() {
		this.countforroot++;
	}

	public int getCounterforRoot() {
		return this.countforroot;
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

	public void setElementType(ElementType eletype) {
		this.eletype = eletype;
	}

	public ElementType getElementType() {
		return this.eletype;
	}

	private String getStringType() {
		if(this.element.equals("Value") || this.element.equals("Text")) {
			return "#PCDATA";
		}
		return this.element;
	}

	public String getElementFormat() {
		switch (this.eletype) {
		case One:
			return "(" + this.getStringType() + ")";
		case More:
			return "(" + this.getStringType() + "*" + ")";
		case Optional:
			return "(" + this.getStringType() + "?" + ")";
		case Required:
			return "(" + this.getStringType() + "+" + ")";
		default:
			return null;
		}
	}
}

enum ElementType {
	One,
	More,
	Optional,
	Required
}