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
		return coefficient > 0.5;
	}

	private boolean calcSetRelation(ArrayList<SubNodeDataSet> list, SubNodeDataSet datasetX, SubNodeDataSet datasetY) {
		final double coefficient = this.calcCoefficient(datasetX, datasetY);
		if (this.checkThreshhold(coefficient)) {
			this.nominateSchema(datasetX, datasetY);
			return true;
		}
		return false;
	}

	private boolean checkRemoveSet(ArrayList<SubNodeDataSet> list, int pos) {
		final SubNodeDataSet dataset = list.get(pos);
		if (dataset.isRemoveSet()) {
			dataset.softRemoveChild();
			list.remove(pos);
			return true;
		}
		return false;
	}

	private boolean isNominatableSet(ArrayList<SubNodeDataSet>list, int i, int j) {
		final SubNodeDataSet datasetX = list.get(i);
		final SubNodeDataSet datasetY = list.get(j);
		if (this.isTargetSet(datasetX, datasetY) && this.calcSetRelation(list, datasetX, datasetY)) {
			datasetX.softRemoveChild();
			datasetY.softRemoveChild();
			list.remove(j);
			return true;
		}
		return false;
	}

	private void filter(ArrayList<SubNodeDataSet> list) {
		for (int i = 0; i < list.size(); i++) {
			if (this.checkRemoveSet(list, i)) {
				i -= 1;
				continue;
			}
			for (int j = i + 1; j < list.size(); j++) {
				if (this.checkRemoveSet(list, j)) {
					j -= 1;
					continue;
				}
				j -= this.isNominatableSet(list, i, j) ? 1 : 0;
			}
		}
	}

	public void nominate() {
		final ArrayList<SubNodeDataSet> list = this.relationbuilder.getSubNodeDataSetList();
		list.sort(new SubNodeDataSet());
		this.filter(list);
	}
}