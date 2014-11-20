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

	public Map<String, SubNodeDataSet> getSchema() {
		return this.schema;
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

	private double calculatiingCoefficient(SubNodeDataSet x, SubNodeDataSet y) {
		Set<String> setX = x.getAssumedColumnSet();
		Set<String> setY = y.getAssumedColumnSet();
		Set<String> intersection = this.calcIntersection(setX, setY);
		Set<String> union        = this.calcUnion(setX, setY);
		return (double) intersection.size() / union.size(); // coefficient
	}

//	private double calculatiingCoefficient(Set<String> setX, Set<String> setY) {
//		Set<String> intersection = this.calcIntersection(setX, setY);
//		Set<String> union        = this.calcUnion(setX, setY);
//		return (double) intersection.size() / union.size(); // coefficient
//	}

	private void nominateSchema(SubNodeDataSet nodeX, SubNodeDataSet nodeY, double coefficient) {
		String tablename  = nodeX.getAssumedTableName();
		Set<String> setX = nodeX.getAssumedColumnSet();
		Set<String> setY = nodeY.getAssumedColumnSet();
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

	private boolean isTargetSet(SubNodeDataSet x, SubNodeDataSet y) {
		Set<String> xset = x.getAssumedColumnSet();
		Set<String> yset = y.getAssumedColumnSet();
		String xname  = x.getAssumedTableName();
		String yname  = y.getAssumedTableName();
		return xname.equals(yname) && xset.size() > 0 && yset.size() > 0;
	}

//	private boolean isTargetSet(Set<String> setX, Set<String> setY, String setXname, String setYname) {
//		return setXname.equals(setYname) && setX.size() > 0 && setY.size() > 0;
//	}

	private boolean isNominatable(double coefficient) {
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

	private void removeSubNodeinList(ArrayList<SubNodeDataSet> list, SubNodeDataSet x) {
		Coordinate parentpoint = x.getSubNode().getCoord();
		for (int i = list.size() - 1; i >= 0; i--) {
			Coordinate subnodepoint = list.get(i).getSubNode().getCoord();
			this.removing(list, parentpoint, subnodepoint, i);
		}
	}
	
	private int removeList(ArrayList<SubNodeDataSet> list, ArrayList<SubNodeDataSet> removelist, int pos) {
		removelist.add(list.get(pos));
		list.remove(pos);
		return pos - 1;
	}
	
	private void calcSetRelation(ArrayList<SubNodeDataSet> list, SubNodeDataSet x, SubNodeDataSet y) {
		double coefficient = this.calculatiingCoefficient(x, y);
		if (this.isNominatable(coefficient)) {
			this.nominateSchema(x, y, coefficient);
			for (SubNodeDataSet child : x.children) {
				child.removed = true;
			}
			//this.removeSubNodeinList(list, x);
		}
	}

	private void filter(ArrayList<SubNodeDataSet> list) {
		ArrayList<SubNodeDataSet> removelist = new ArrayList<SubNodeDataSet>();
		for(int i = 0; i < list.size(); i++) {
			SubNodeDataSet x = list.get(i);
			x.findChildren(list);
		}
		for(int i = 0; i < list.size(); i++) {
			SubNodeDataSet x = list.get(i);
			if (x.removed) {
				for (SubNodeDataSet child : x.children) {
					child.removed = true;
				}
				list.remove(i); i -= 1;
				continue;
			}
			for(int j = i + 1; j < list.size(); j++) {
				SubNodeDataSet y = list.get(j);
				if (y.removed) {
					for (SubNodeDataSet child : y.children) {
						child.removed = true;
					}
					list.remove(j); j -= 1;
					continue;
				}
				if (this.isTargetSet(x, y)) {
					this.calcSetRelation(list, x, y);
					j = this.removeList(list, removelist, j);
				}
			}
			this.removeSubNodeinRemoveList(list, removelist);
		}
	}
//	private void calcSetRelation(ArrayList<SubNodeDataSet> list, SubNodeDataSet x, SubNodeDataSet y) {
//		double coefficient = this.calculatiingCoefficient(x, y);
//		if (this.isNominatable(coefficient)) {
//			this.nominateSchema(x, y, coefficient);
//			this.removeSubNodeinList(list, x);
//		}
//	}
//
//	private void filter(ArrayList<SubNodeDataSet> list) {
//		ArrayList<SubNodeDataSet> removelist = new ArrayList<SubNodeDataSet>();
//		for(int i = 0; i < list.size(); i++) {
//			SubNodeDataSet x = list.get(i);
//			for(int j = i + 1; j < list.size(); j++) {
//				SubNodeDataSet y = list.get(j);
//				if (this.isTargetSet(x, y)) {
//					this.calcSetRelation(list, x, y);
//					j = this.removeList(list, removelist, j);
//				}
//			}
//			this.removeSubNodeinRemoveList(list, removelist);
//		}
//	}

	public void nominating() {
		ArrayList<SubNodeDataSet> list = this.relationbuilder.getSubNodeDataSetList();
		list.sort(new SubNodeDataSet());
		this.filter(list);
		for(String key : this.schema.keySet()) {
			System.out.println(this.schema.get(key).getAssumedTableName());
			System.out.println(this.schema.get(key).getAssumedColumnSet());
			System.out.println("-------------------------------");
		}
	}
}