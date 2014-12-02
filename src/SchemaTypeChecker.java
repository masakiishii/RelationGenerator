package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class SchemaTypeChecker {
	private int ltdomain = -1;
	private int rtdomain = -1;

	public SchemaTypeChecker() {
		this.ltdomain = 0;
		this.rtdomain = 1;
	}

	private ArrayList<int[]> getParsingObjectDomainList(WrapperObject root, SubNodeDataSet subnodedatasetX) {
		final ArrayList<int[]> domainlist = new ArrayList<int[]>();
		final String tablename = subnodedatasetX.getAssumedTableName();
		final Queue<WrapperObject> queue = new LinkedList<WrapperObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			final WrapperObject node = queue.poll();
			if(node.size() == 0 && node.getText().toString().equals(tablename)) {
				final WrapperObject target = node.getParent();
				int[] domain = new int[2];
				domain[this.ltdomain] = target.getLtpos();
				domain[this.rtdomain] = target.getRtpos();
				domainlist.add(domain);
			}
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
		return domainlist;
	}

	public boolean inList(ArrayList<int[]> list, SubNodeDataSet subnodedatasetY) {
		final int lpos = subnodedatasetY.getSubNode().getLtpos();
		final int rpos = subnodedatasetY.getSubNode().getRtpos();
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i)[0] < lpos && rpos < list.get(i)[1]) {
				return true;
			}
		}
		return false;
	}

	public boolean check(WrapperObject root, SubNodeDataSet subnodedatasetX, SubNodeDataSet subnodedatasetY) {
		final ArrayList<int[]> list = this.getParsingObjectDomainList(root, subnodedatasetX);
		return this.inList(list, subnodedatasetY);
	}
}
