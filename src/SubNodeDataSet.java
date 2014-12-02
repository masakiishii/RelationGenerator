package org.peg4d.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SubNodeDataSet implements Comparator<SubNodeDataSet> {
	private WrapperObject             subNode            = null;
	private String                    assumedTableName   = null;
	private Set<String>               assumedColumnSet   = null;
	private Set<String>               finalColumnSet     = null;
	private int                       assumedTableNodeId = -1;
	private ArrayList<SubNodeDataSet> children           = null;
	private boolean                   removed            = false;

	public SubNodeDataSet(WrapperObject subNode, String assumedTableName, int assumedTableId) {
		this.subNode            = subNode;
		this.assumedTableName   = assumedTableName;
		this.assumedColumnSet   = new LinkedHashSet<String>();
		this.finalColumnSet     = new LinkedHashSet<String>();
		this.assumedTableNodeId = assumedTableId;
		this.children           = new ArrayList<SubNodeDataSet>();
	}
	public SubNodeDataSet(WrapperObject subNode) {
		this.subNode            = subNode;
		this.assumedColumnSet   = new LinkedHashSet<String>();
		this.finalColumnSet     = new LinkedHashSet<String>();
		this.children           = new ArrayList<SubNodeDataSet>();
	}
	public SubNodeDataSet() {
		this.assumedColumnSet   = new LinkedHashSet<String>();
		this.finalColumnSet     = new LinkedHashSet<String>();
		this.children           = new ArrayList<SubNodeDataSet>();
	}

	@Override
	public int compare(SubNodeDataSet o1, SubNodeDataSet o2) {
		final Coordinate p1 = o1.subNode.getCoord();
		final Coordinate p2 = o2.subNode.getCoord();
		return p2.getRange() - p1.getRange();
	}

	private boolean isTableNode(WrapperObject node) {
		return node.get(0).getObjectId() == this.assumedTableNodeId;
	}

	private boolean checkNodeRel(WrapperObject node) {
		return !node.isTerminal() && node.get(0).isTerminal();
	}

	private boolean isAssumedColumn(WrapperObject node) {
		return this.checkNodeRel(node) && !this.isTableNode(node);
	}

	private void setAssumedColumnSet(WrapperObject node) {
		if(this.isAssumedColumn(node)) {
			this.assumedColumnSet.add(node.get(0).getText());
		}
	}

	public void buildAssumedColumnSet() {
		final Queue<WrapperObject> queue = new LinkedList<WrapperObject>();
		queue.offer(this.subNode);
		while(!queue.isEmpty()) {
			final WrapperObject node = queue.poll();
			this.setAssumedColumnSet(node);
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
	}

	public WrapperObject getSubNode() {
		return this.subNode;
	}

	public String getAssumedTableName() {
		return this.assumedTableName;
	}

	public Set<String> getAssumedColumnSet() {
		return this.assumedColumnSet;
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

	public ArrayList<SubNodeDataSet> getChildren() {
		return this.children;
	}

	public boolean isRemoveSet() {
		return this.removed ? true : false;
	}

	public void softRemoveChild() {
		for (final SubNodeDataSet child : this.children) {
			if (child != null) {
				child.removed = true;
			}
		}
	}
}
