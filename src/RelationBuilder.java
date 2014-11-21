package org.peg4d.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.peg4d.ParsingObject;

public class RelationBuilder {
	private ParsingObject root         = null;
	private int           segmentidpos = 0;
	private ArrayList<SubNodeDataSet> allsubnodesetlist = null;
	public RelationBuilder(ParsingObject root) {
		this.root = root;
		this.segmentidpos++;
		this.allsubnodesetlist = new ArrayList<SubNodeDataSet>();
//		if (false) {
//		System.out.println("wait");
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		}
	}

	public ArrayList<SubNodeDataSet> getSubNodeDataSetList() {
		return this.allsubnodesetlist;
	}

	static public boolean isNumber(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void setAllSubNodeSetList(LappingObject node, String tablename, int id) {
		SubNodeDataSet subnodeset = new SubNodeDataSet(node, tablename, id);
		subnodeset.buildAssumedColumnSet();
		node.setSubNodeDataSet(subnodeset);
		if (subnodeset.getAssumedColumnSet().size() > 1) {
			this.allsubnodesetlist.add(subnodeset);
		}
	}

	private void collectListSubNode(LappingObject node) {
		LappingObject assumedtablenode = node.getParent().get(0);
		String tablename = assumedtablenode.getText();
		for (int i = 0; i < node.size(); i++) {
			this.setAllSubNodeSetList(node.get(i), tablename, assumedtablenode.getObjectId());
		}
	}

	private void collectNormSubNode(LappingObject node) {
		LappingObject assumedtablenode = node.get(0);
		String tablename = assumedtablenode.getText();
		if (!RelationBuilder.isNumber(tablename)) {
			this.setAllSubNodeSetList(node, tablename, assumedtablenode.getObjectId());
		}
	}

	private void collectAllSubNode(LappingObject node) {
		if (node == null) {
			return;
		}
		if (node.getTag().toString().equals("List")) {
			this.collectListSubNode(node);
		} else if (node.size() != 0 && node.get(0).size() == 0) {
			this.collectNormSubNode(node);
		}
		for (int i = 0; i < node.size(); i++) {
			this.collectAllSubNode(node.get(i));
		}
	}

	private void buildLappingTree(ParsingObject node, LappingObject lappingnode) {
		if (node == null) {
			return;
		}
		lappingnode.getCoord().setLpos(this.segmentidpos++);
		int size = node.size();
		if (size > 0) {
			LappingObject[] AST = new LappingObject[size];
			for (int i = 0; i < node.size(); i++) {
				AST[i] = new LappingObject(node.get(i));
				AST[i].setParent(lappingnode);
				this.buildLappingTree(node.get(i), AST[i]);
			}
			lappingnode.setAST(AST);
		}
		lappingnode.getCoord().setRpos(this.segmentidpos++);
	}

	void linkAllSubNodeDataSet(LappingObject node) {
		if(node == null) {
			return;
		}
		SubNodeDataSet dataset = node.getSubNodeDataSet();
		for(int i = 0; i < node.size(); i++) {
			LappingObject obj = node.get(i);
			SubNodeDataSet set = obj.getSubNodeDataSet();
			if (set != null) {
				dataset.children.add(set);
			}
			linkAllSubNodeDataSet(obj);
		}
	}
	

	private LappingObject preprocessing() {
		LappingObject lappingrootnode = new LappingObject(this.root);
		this.buildLappingTree(this.root, lappingrootnode);
		this.collectAllSubNode(lappingrootnode);
		this.linkAllSubNodeDataSet(lappingrootnode);
		return lappingrootnode;
	}

	private void buildInferSchema(LappingObject lappingrootnode) {
		SchemaNominator preschema = new SchemaNominator(this);
		preschema.nominating();
		SchemaDecider defineschema = new SchemaDecider(preschema, lappingrootnode);
		Map<String, SubNodeDataSet> definedschema = defineschema.define();
		Matcher matcher = new SchemaMatcher(definedschema);
		matcher.match(lappingrootnode);
	}

	private void buildFixedSchema(LappingObject lappingrootnode) {
		TreeTypeChecker checker = new TreeTypeChecker();
		Map<String, Set<String>> definedschema = checker.check(lappingrootnode);
		Matcher matcher = new FixedSchemaMatcher(definedschema);
		matcher.match(lappingrootnode);
	}

	public void build(Boolean infer) {
		LappingObject lappingrootnode = this.preprocessing();
		if(infer) {
			this.buildInferSchema(lappingrootnode);
		}
		else {
			this.buildFixedSchema(lappingrootnode);
		}
	}
}
