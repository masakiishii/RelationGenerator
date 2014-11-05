package org.peg4d.data;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.peg4d.ParsingObject;

public class TreeTypeChecker {
	private Map<String, SubNodeDataSet> schema = null;
	public TreeTypeChecker() {
		schema = new LinkedHashMap<String, SubNodeDataSet>();
	}
	
	public void checking(ParsingObject node) {
		if(node == null) {
			return;
		}
		String parenttag = node.getTag().toString();
		if(node.size() > 0 && !this.schema.containsKey(parenttag)) {
			Set<String> schemaset = new LinkedHashSet<String>();
			SubNodeDataSet subnodedataset = new SubNodeDataSet();
			for(int i = 0; i < node.size(); i++) {
				schemaset.add(node.get(i).getTag().toString());
			}
			subnodedataset.setFinalColumnSet(schemaset);
			schema.put(parenttag, subnodedataset);
		}
		for(int i = 0; i < node.size(); i++) {
			this.checking(node.get(i));
		}
	}

	public Map<String, SubNodeDataSet> check(ParsingObject root) {
		this.checking(root);
		return this.schema;
	}
}
