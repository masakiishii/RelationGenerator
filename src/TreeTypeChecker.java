package org.peg4d.data;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.peg4d.ParsingObject;

public class TreeTypeChecker {
	private Map<String, Set<String>> treetype = null;
	public TreeTypeChecker() {
		treetype = new LinkedHashMap<String, Set<String>>();
	}
	
	public void checking(ParsingObject node) {
		if(node == null) {
			return;
		}
		String parenttag = node.getTag().toString();
		if(node.size() > 0 && !this.treetype.containsKey(parenttag)) {
			Set<String> schemaset = new LinkedHashSet<String>();
			for(int i = 0; i < node.size(); i++) {
				schemaset.add(node.get(i).getTag().toString());
			}
			treetype.put(parenttag, schemaset);
		}
		for(int i = 0; i < node.size(); i++) {
			this.checking(node.get(i));
		}
	}
	
	public void check(ParsingObject root) {
		this.checking(root);
		for(String s : this.treetype.keySet()) {
			System.out.println("parent tag: " + s);
			Set<String> set = this.treetype.get(s);
			System.out.println("------------------------------");
			for(String tag : set) {
				System.out.println("child tag: " + tag);
			}
			System.out.println();
			System.out.println();
			System.out.println();
		}
		System.out.println("------------------------------");
	}
}
