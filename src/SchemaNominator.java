package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.omg.CORBA.PRIVATE_MEMBER;

public class SchemaNominator {
	private RelationBuilder relationbuilder    = null;
	private Map<String, SubNodeDataSet> schema = null;

	public SchemaNominator(RelationBuilder relationbuilder) {
		this.relationbuilder = relationbuilder;
		this.schema = new LinkedHashMap<String, SubNodeDataSet>();
	}

	private Set<String> calcIntersection(Set<String> setX, Set<String> setY) {
		final Set<String> intersection = new LinkedHashSet<String>(setX);
		intersection.retainAll(setY);
		return intersection;
	}

	private Set<String> calcUnion(Set<String> setX, Set<String> setY) {
		final Set<String> union = new LinkedHashSet<String>(setX);
		union.addAll(setY);
		return union;
	}

	private double calcCoefficient(SubNodeDataSet datasetX, SubNodeDataSet datasetY) {
		final Set<String> setX         = datasetX.getAssumedColumnSet();
		final Set<String> setY         = datasetY.getAssumedColumnSet();
		final Set<String> intersection = this.calcIntersection(setX, setY);
		final Set<String> union        = this.calcUnion(setX, setY);
		return (double) intersection.size() / union.size(); // coefficient
	}

	private void nominateSchema(SubNodeDataSet nodeX, SubNodeDataSet nodeY) {
		final String tablename = nodeX.getAssumedTableName();
		final Set<String> setX = nodeX.getAssumedColumnSet();
		final Set<String> setY = nodeY.getAssumedColumnSet();
		if(this.schema.containsKey(tablename)) {
			this.schema.get(tablename).getAssumedColumnSet().addAll(setX);
			this.schema.get(tablename).getAssumedColumnSet().addAll(setY);
		}
		else {
			nodeX.getAssumedColumnSet().addAll(setY);
			this.schema.put(tablename, nodeX);
		}
	}

	public Map<String, SubNodeDataSet> getSchema() {
		return this.schema;
	}

	private boolean isTargetSet(SubNodeDataSet datasetX, SubNodeDataSet datasetY) {
		final Set<String> setX  = datasetX.getAssumedColumnSet();
		final Set<String> setY  = datasetY.getAssumedColumnSet();
		final String tablenameX = datasetX.getAssumedTableName();
		final String tablenameY = datasetY.getAssumedTableName();
		return tablenameX.equals(tablenameY) && setX.size() > 0 && setY.size() > 0;
	}

	private boolean checkThreshhold(double coefficient) {
		return coefficient > 0.5 && coefficient <= 1.0;
	}

	private boolean isSubNode(Coordinate parentcoord, Coordinate subnodecoord) {
		return Coordinate.checkLtpos(parentcoord, subnodecoord) && Coordinate.checkRtpos(parentcoord, subnodecoord);
	}

	private void removing(ArrayList<SubNodeDataSet> list, Coordinate parentcoord, Coordinate subnodecoord, int pos) {
		if (this.isSubNode(parentcoord, subnodecoord)) {
			list.remove(pos);
		}
	}

	private void removeSubNodeinRemoveList(ArrayList<SubNodeDataSet> list, ArrayList<SubNodeDataSet> removelist) {
		for (int i = 0; i < removelist.size(); i++) {
			final Coordinate parentcoord = removelist.get(i).getSubNode().getCoord();
			for (int j = list.size() - 1; j >= 0; j--) {
				final Coordinate subnodecoord = list.get(j).getSubNode().getCoord();
				this.removing(list, parentcoord, subnodecoord, j);
			}
		}
		removelist.clear();
	}

	private void removeSubNodeinList(ArrayList<SubNodeDataSet> list, int pos) {
		final Coordinate parentcoord = list.get(pos).getSubNode().getCoord();
		for (int i = list.size() - 1; i >= pos; i--) {
			final Coordinate subnodecoord = list.get(i).getSubNode().getCoord();
			this.removing(list, parentcoord, subnodecoord, i);
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

	private boolean calcSetRelation(ArrayList<SubNodeDataSet> list, SubNodeDataSet datasetX, SubNodeDataSet datasetY) {
		final double coefficient = this.calcCoefficient(datasetX, datasetY);
		if (this.checkThreshhold(coefficient)) {
			this.nominateSchema(datasetX, datasetY);
			return true;
		}
		return false;
	}

//	private void calcSetRelation(ArrayList<SubNodeDataSet> list, SubNodeDataSet datasetX, SubNodeDataSet datasetY, int pos) {
//		final String setXname  = datasetX.getAssumedTableName();
//		final double coefficient = this.calculatingCoefficient(datasetX, datasetY);
//		if (this.checkThreshhold(coefficient)) {
//			this.nominateSchema(setXname, datasetX, datasetY);
//			this.removeSubNodeinList(list, pos);
//		}
//	}

	private ArrayList<SubNodeDataSet> collectRemoveList(ArrayList<SubNodeDataSet> list, int i) {
		final ArrayList<SubNodeDataSet> removelist = new ArrayList<SubNodeDataSet>();
		for(int j = i + 1; j < list.size(); j++) {
			final SubNodeDataSet datasetX = list.get(i);
			final SubNodeDataSet datasetY = list.get(j);
			if (this.isTargetSet(datasetX, datasetY) ) {
				//this.calcSetRelation(list, datasetX, datasetY, i);
				j = this.removeList(list, removelist, j);
			}
		}
		return removelist;
	}

	private void filter(ArrayList<SubNodeDataSet> list) {
		for (int i = 0; i < list.size(); i++) {
			final SubNodeDataSet x = list.get(i);
			if (x.removed) {
				x.softRemoveChild();
				list.remove(i);
				i -= 1;
				continue;
			}
			for (int j = i + 1; j < list.size(); j++) {
				final SubNodeDataSet y = list.get(j);
				if (y.removed) {
					y.softRemoveChild();
					list.remove(j);
					j -= 1;
					continue;
				}
				if (this.isTargetSet(x, y)) {
					if (this.calcSetRelation(list, x, y)) {
						x.softRemoveChild();
						y.softRemoveChild();
						list.remove(j);
						j -= 1;
					}
				}
			}
		}
	}
	
	//	private void filter(ArrayList<SubNodeDataSet> list) {
//		for(int i = 0; i < list.size(); i++) {
//			final ArrayList<SubNodeDataSet> removelist = this.collectRemoveList(list, i);
//			this.removeSubNodeinRemoveList(list, removelist);
//		}
//	}

	public void nominate() {
		final ArrayList<SubNodeDataSet> list = this.relationbuilder.getSubNodeDataSetList();
		list.sort(new SubNodeDataSet());
		this.filter(list);
	}
}