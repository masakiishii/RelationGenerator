package org.peg4d.data;

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

	private void setAllSubNodeSetList(WrapperObject node, String tablename, int id) {
		SubNodeDataSet subnodeset = new SubNodeDataSet(node, tablename, id);
		subnodeset.buildAssumedColumnSet();
		if (subnodeset.getAssumedColumnSet().size() > 1) {
			this.allsubnodesetlist.add(subnodeset);
		}
	}

	private void collectListSubNode(WrapperObject node) {
		WrapperObject assumedtablenode = node.getParent().get(0);
		String tablename = assumedtablenode.getText();
		for (int i = 0; i < node.size(); i++) {
			this.setAllSubNodeSetList(node.get(i), tablename, assumedtablenode.getObjectId());
		}
	}

	private void collectNormSubNode(WrapperObject node) {
		WrapperObject assumedtablenode = node.get(0);
		String tablename = assumedtablenode.getText();
		if (!RelationBuilder.isNumber(tablename)) {
			this.setAllSubNodeSetList(node, tablename, assumedtablenode.getObjectId());
		}
	}

	private void collectAllSubNode(WrapperObject node) {
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

	private void buildLappingTree(ParsingObject node, WrapperObject lappingnode) {
		if (node == null) {
			return;
		}
		lappingnode.getCoord().setLpos(this.segmentidpos++);
		int size = node.size();
		if (size > 0) {
			WrapperObject[] AST = new WrapperObject[size];
			for (int i = 0; i < node.size(); i++) {
				AST[i] = new WrapperObject(node.get(i));
				AST[i].setParent(lappingnode);
				this.buildLappingTree(node.get(i), AST[i]);
			}
			lappingnode.setAST(AST);
		}
		lappingnode.getCoord().setRpos(this.segmentidpos++);
	}

	private WrapperObject preprocessing() {
		WrapperObject lappingrootnode = new WrapperObject(this.root);
		this.buildLappingTree(this.root, lappingrootnode);
		this.collectAllSubNode(lappingrootnode);
		return lappingrootnode;
	}

	private void buildInferSchema(WrapperObject lappingrootnode) {
		SchemaNominator preschema = new SchemaNominator(this);
		preschema.nominating();
		SchemaDecider defineschema = new SchemaDecider(preschema, lappingrootnode);
		Map<String, SubNodeDataSet> definedschema = defineschema.define();
		Matcher matcher = new SchemaMatcher(definedschema);
		matcher.match(lappingrootnode);
	}

	private void buildFixedSchema(WrapperObject lappingrootnode) {
		TreeTypeChecker checker = new TreeTypeChecker();
		Map<String, Set<String>> definedschema = checker.check(lappingrootnode);
		Matcher matcher = new FixedSchemaMatcher(definedschema);
		matcher.match(lappingrootnode);
	}

	public void build(Boolean infer) {
		WrapperObject lappingrootnode = this.preprocessing();
		if(infer) {
			this.buildInferSchema(lappingrootnode);
		}
		else {
			this.buildFixedSchema(lappingrootnode);
		}
	}
}
