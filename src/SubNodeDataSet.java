package org.peg4d.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SubNodeDataSet implements Comparator<SubNodeDataSet> {
	private LappingObject subNode            = null;
	private String        assumedTableName   = null;
	private Set<String>   assumedColumnSet   = null;
	private Set<String>   finalColumnSet     = null;
	private int           assumedTableNodeId = -1;
	private double        Coefficient        = -1;
	public ArrayList<SubNodeDataSet> children = null;
	public boolean removed = false;

	public SubNodeDataSet(LappingObject subNode, String assumedTableName, int assumedTableId) {
		this.subNode            = subNode;
		this.assumedTableName   = assumedTableName;
		this.assumedColumnSet   = new LinkedHashSet<String>();
		this.finalColumnSet     = new LinkedHashSet<String>();
		this.assumedTableNodeId = assumedTableId;
		this.children = new ArrayList<SubNodeDataSet>();
	}
	public SubNodeDataSet() {
		this.assumedColumnSet   = new LinkedHashSet<String>();
		this.finalColumnSet     = new LinkedHashSet<String>();
	}

	@Override
	public int compare(SubNodeDataSet o1, SubNodeDataSet o2) {
		Coordinate p1 = o1.subNode.getCoord();
		Coordinate p2 = o2.subNode.getCoord();
		return p2.getRange() - p1.getRange();
	}

	private boolean isTableNode(LappingObject node) {
		return node.get(0).getObjectId() == this.assumedTableNodeId;
	}

	private boolean checkNodeRel(LappingObject node) {
		return !node.isTerminal() && node.get(0).isTerminal();
	}

	private boolean isAssumedColumn(LappingObject node) {
		return this.checkNodeRel(node) && !this.isTableNode(node);
	}

	private void setAssumedColumnSet(LappingObject node) {
		if(this.isAssumedColumn(node)) {
			this.assumedColumnSet.add(node.get(0).getText());
		}
	}

	public void buildAssumedColumnSet() {
		Queue<LappingObject> queue = new LinkedList<LappingObject>();
		queue.offer(this.subNode);
		while(!queue.isEmpty()) {
			LappingObject node = queue.poll();
			this.setAssumedColumnSet(node);
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
	}

	public LappingObject getSubNode() {
		return this.subNode;
	}

	public String getAssumedTableName() {
		return this.assumedTableName;
	}

	public Set<String> getAssumedColumnSet() {
		return this.assumedColumnSet;
	}

	public void setCoefficient(double coefficient) {
		this.Coefficient = coefficient;
	}

	public double getCoefficient() {
		return this.Coefficient;
	}

	public void setFinalColumnSet(String headcolumn) {
		this.finalColumnSet.add(headcolumn);
	}

	public void setFinalColumnSet(Set<String> set) {
		this.finalColumnSet.addAll(set);
	}

	public Set<String> getFinalColumnSet() {
		return this.finalColumnSet;
	}
	private boolean isSubNode(SubNodeDataSet that) {
		Coordinate c1 = this.getSubNode().getCoord();
		Coordinate c2 = that.getSubNode().getCoord();
		return Coordinate.checkLtpos(c1, c2) && Coordinate.checkRtpos(c1, c2);
	}

	public void findChildren(ArrayList<SubNodeDataSet> list) {
		for (int j = list.size() - 1; j >= 0; j--) {
			SubNodeDataSet y = list.get(j);
			if (isSubNode(y)) {
				this.children.add(y);
			}
		}
	}
}