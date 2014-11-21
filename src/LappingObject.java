package org.peg4d.data;

import java.util.Set;

import org.peg4d.ParsingObject;
import org.peg4d.ParsingTag;

public class LappingObject {
	private ParsingObject node     = null;
	private static int    idCount  = 0;
	private int           objectId = -1;
	private Coordinate    coord    = null;
	private boolean       visited  = false;
	private LappingObject AST[]    = null;
	private LappingObject parent   = null;
	private String        value    = null;
	private int           size     = -1;
	
	private SubNodeDataSet dataset = null;

	public LappingObject(ParsingObject node) {
		this.objectId = idCount++;
		this.node     = node;
		this.coord    = new Coordinate();
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

	public Coordinate getCoord() {
		return this.coord;
	}

	public void setParent(LappingObject parent) {
		this.parent = parent;
	}

	public LappingObject getParent() {
		return this.parent;
	}

	public LappingObject[] getAST() {
		return this.AST;
	}

	public void setAST(LappingObject[] AST) {
		this.AST = AST;

	}

	public LappingObject get(int index) {
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
		if(this.value == null) {
			this.value = this.node.getText();
		}
		return this.value;
	}

	public boolean isTerminal() {
		return this.size == 0;
	}
}
