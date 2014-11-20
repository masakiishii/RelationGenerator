package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SchemaNominator {
	private RelationBuilder relationbuilder    = null;
	private Map<String, SubNodeDataSet> schema = null;
	public SchemaNominator(RelationBuilder relationbuilder) {
		this.relationbuilder = relationbuilder;
		this.schema = new LinkedHashMap<String, SubNodeDataSet>();
	}

	private Set<String> calcIntersection(Set<String> setX, Set<String> setY) {
		Set<String> intersection = new LinkedHashSet<String>(setX);
		intersection.retainAll(setY);
		return intersection;
	}

	private Set<String> calcUnion(Set<String> setX, Set<String> setY) {
		Set<String> union = new LinkedHashSet<String>(setX);
		union.addAll(setY);
		return union;
	}

	private double calculatiingCoefficient(Set<String> setX, Set<String> setY) {
		Set<String> intersection = this.calcIntersection(setX, setY);
		Set<String> union        = this.calcUnion(setX, setY);
		return (double) intersection.size() / union.size(); // coefficient
	}

	private void nominateSchema(String tablename, SubNodeDataSet nodeX, SubNodeDataSet nodeY, double coefficient) {
		Set<String> setX = nodeX.getAssumedColumnSet();
		Set<String> setY = nodeY.getAssumedColumnSet();
		nodeX.setCoefficient(coefficient);
		nodeY.setCoefficient(coefficient);
		if(this.schema.containsKey(tablename)) {
			this.schema.get(tablename).getAssumedColumnSet().addAll(setX);
			this.schema.get(tablename).getAssumedColumnSet().addAll(setY);
			return;
		}
		else {
			nodeX.getAssumedColumnSet().addAll(setY);
			this.schema.put(tablename, nodeX);
		}
	}

	public Map<String, SubNodeDataSet> getSchema() {
		return this.schema;
	}

	//private boolean isNominatableSet(Set<String> setX, Set<String> setY, String setXname, String setYname) {
	private boolean isNominatableSet(SubNodeDataSet datasetX, SubNodeDataSet datasetY) {
		Set<String> setX  = datasetX.getAssumedColumnSet();
		Set<String> setY  = datasetY.getAssumedColumnSet();
		String tablenameX = datasetX.getAssumedTableName();
		String tablenameY = datasetY.getAssumedTableName();
		return tablenameX.equals(tablenameY) && setX.size() > 0 && setY.size() > 0;
	}

	private boolean checkThreshhold(double coefficient) {
		return coefficient > 0.5 && coefficient <= 1.0;
	}

	private boolean isSubNode(Coordinate parentpoint, Coordinate subnodepoint) {
		return Coordinate.checkLtpos(parentpoint, subnodepoint) && Coordinate.checkRtpos(parentpoint, subnodepoint);
	}

	private void removing(ArrayList<SubNodeDataSet> list, Coordinate parentpoint, Coordinate subnodepoint, int pos) {
		if (this.isSubNode(parentpoint, subnodepoint)) {
			list.remove(pos);
		}
	}
	
	private void removeSubNodeinRemoveList(ArrayList<SubNodeDataSet> list, ArrayList<SubNodeDataSet> removelist) {
		for (int i = 0; i < removelist.size(); i++) {
			Coordinate parentpoint = removelist.get(i).getSubNode().getCoord();
			for (int j = list.size() - 1; j >= 0; j--) {
				Coordinate subnodepoint = list.get(j).getSubNode().getCoord();
				this.removing(list, parentpoint, subnodepoint, j);
			}
		}
		removelist.clear();
	}

	private void removeSubNodeinList(ArrayList<SubNodeDataSet> list, int pos) {
		Coordinate parentpoint = list.get(pos).getSubNode().getCoord();
		for (int i = list.size() - 1; i >= 0; i--) {
			Coordinate subnodepoint = list.get(i).getSubNode().getCoord();
			this.removing(list, parentpoint, subnodepoint, i);
		}
	}
	
	private int removeList(ArrayList<SubNodeDataSet> list, ArrayList<SubNodeDataSet> removelist, int pos) {
		if (list.size() > 2) {
			removelist.add(list.get(pos));
			list.remove(pos);
			return pos - 1;
		}
		return pos;
	}
	
	private void checkSubNodeinList(ArrayList<SubNodeDataSet> list, int i) {
		this.removeSubNodeinList(list, i);
	}
	
	private void calcSetRelation(ArrayList<SubNodeDataSet> list, int i, int j) {
		Set<String> setX = list.get(i).getAssumedColumnSet();
		Set<String> setY = list.get(j).getAssumedColumnSet();
		String setXname  = list.get(i).getAssumedTableName();
		double coefficient = this.calculatiingCoefficient(setX, setY);
		if (this.checkThreshhold(coefficient)) {
			this.nominateSchema(setXname, list.get(i), list.get(j), coefficient);
			this.checkSubNodeinList(list, i);
		}
	}
	
	private ArrayList<SubNodeDataSet> collectRemoveList(ArrayList<SubNodeDataSet> list, int i) {
		ArrayList<SubNodeDataSet> removelist = new ArrayList<SubNodeDataSet>();
		for(int j = i + 1; j < list.size(); j++) {
			if (this.isNominatableSet(list.get(i), list.get(j))) {
				this.calcSetRelation(list, i, j);
				j = this.removeList(list, removelist, j);
			}
		}
		return removelist;
	}

	private void filtering(ArrayList<SubNodeDataSet> list) {
		for(int i = 0; i < list.size(); i++) {
			ArrayList<SubNodeDataSet> removelist = this.collectRemoveList(list, i);
			this.removeSubNodeinRemoveList(list, removelist);
		}
	}

	public void nominating() {
		ArrayList<SubNodeDataSet> list = this.relationbuilder.getSubNodeDataSetList();
		list.sort(new SubNodeDataSet());
		this.filtering(list);
	}
}