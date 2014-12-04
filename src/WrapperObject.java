package org.peg4d.data;

import org.peg4d.ParsingObject;
import org.peg4d.ParsingTag;

public class WrapperObject extends Coordinate {
	private static int     idCount  = 0;

	private ParsingObject  node     = null;
	private int            objectId = -1;
	private int            size     = -1;
	private boolean        visited  = false;
	private WrapperObject  AST[]    = null;
	private WrapperObject  parent   = null;
	private SubNodeDataSet dataset  = null;

	public WrapperObject(ParsingObject node) {
		super();
		this.objectId = idCount++;
		this.node     = node;
		this.size     = node.size();
	}

	public void setSubNodeDataSet(SubNodeDataSet dataset) {
		this.dataset = dataset;
	}

	public SubNodeDataSet getSubNodeDataSet() {
		return this.dataset;
	}

	public ParsingObject getParsingObject() {
		return this.node;
	}

	public int getObjectId() {
		return this.objectId;
	}

	public void setParent(WrapperObject parent) {
		this.parent = parent;
	}

	public WrapperObject getParent() {
		return this.parent;
	}

	public WrapperObject[] getAST() {
		return this.AST;
	}

	public void setAST(WrapperObject[] AST) {
		this.AST = AST;

	}

	public WrapperObject get(int index) {
		return this.AST[index];
	}

	public int size() {
		return this.size;
	}

	public void visited() {
		this.visited = true;
	}

	public boolean isVisitedNode() {
		return this.visited;
	}

	public ParsingTag getTag() {
		return this.node.getTag();
	}

	public String getText() {
		return this.node.getText();
	}

	public boolean isTerminal() {
		return this.size == 0;
	}
}
