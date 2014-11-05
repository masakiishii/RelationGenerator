package org.peg4d.data;

import org.peg4d.ParsingObject;

public class TreeTypeChecker {
	public TreeTypeChecker() {
		
	}
	
	public void checking(ParsingObject node) {
		if(node == null) {
			return;
		}
		if(node.size() > 0) {
			System.out.println("Parent Tag: " + node.getTag().toString());
			for(int i = 0; i < node.size(); i++) {
				System.out.println("Children Tag: " + node.get(i).getTag().toString());
			}
			System.out.println("------------------------------");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
		}
		for(int i = 0; i < node.size(); i++) {
			this.checking(node.get(i));
		}
	}
	
	public void check(ParsingObject root) {
		this.check(root);
		System.out.println("------------------------------");
	}
}
