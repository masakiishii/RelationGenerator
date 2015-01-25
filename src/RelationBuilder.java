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
		if (Utils.UseFastParseNumber) {
			return Utils.isNumber(value);
		} else {
			try {
				Double.parseDouble(value);
				return true;
			} catch (final NumberFormatException e) {
				return false;
			}
		}
	}

	private void setAllSubNodeSetList(WrapperObject node, String tablename, int id) {
		final SubNodeDataSet subnodeset = new SubNodeDataSet(node, tablename, id);
		subnodeset.buildAssumedColumnSet();
		node.setSubNodeDataSet(subnodeset);
		if (subnodeset.getAssumedColumnSet().size() > 1) {
			this.allsubnodesetlist.add(subnodeset);
		}
	}

	private void collectListSubNode(WrapperObject node) {
		final WrapperObject assumedtablenode = node.getParent().get(0);
		final String tablename = assumedtablenode.getText();
		for (int i = 0; i < node.size(); i++) {
			this.setAllSubNodeSetList(node.get(i), tablename, assumedtablenode.getObjectId());
		}
	}

	private void collectNormSubNode(WrapperObject node) {
		final WrapperObject assumedtablenode = node.get(0);
		final String tablename = assumedtablenode.getText();
		if (!RelationBuilder.isNumber(tablename)) {
			this.setAllSubNodeSetList(node, tablename, assumedtablenode.getObjectId());
		}
	}

	private boolean checkListType(WrapperObject node) {
		WrapperObject child = node.get(0);
		if(!child.isTerminal()) {
			WrapperObject grandchild = child.get(0);
			if(!grandchild.isTerminal()) {
				WrapperObject greatgrandchild = grandchild.get(0);
				return greatgrandchild.isTerminal() ? true : false;
			}
		}
		return false;
	}

	private void collectAllSubNode(WrapperObject node) {
		if (node == null) {
			return;
		}
		if(!node.isTerminal() && this.checkListType(node)) {
			node.setListType();
			this.collectListSubNode(node);
		}
		else if (!node.isTerminal() && node.get(0).isTerminal()) {
			this.collectNormSubNode(node);
		}
		for (int i = 0; i < node.size(); i++) {
			this.collectAllSubNode(node.get(i));
		}
	}

	private void buildWrappingTree(ParsingObject node, WrapperObject wrappernode) {
		if (node == null) {
			return;
		}
		wrappernode.setLpos(this.segmentidpos++);
		int size = node.size();
		if (size > 0) {
			final WrapperObject[] AST = new WrapperObject[size];
			for (int i = 0; i < node.size(); i++) {
				AST[i] = new WrapperObject(node.get(i));
				AST[i].setParent(wrappernode);
				this.buildWrappingTree(node.get(i), AST[i]);
			}
			wrappernode.setAST(AST);
		}
		wrappernode.setRpos(this.segmentidpos++);
	}

	void linkAllSubNodeDataSet(WrapperObject node) {
		if (node == null) {
			return;
		}
		final SubNodeDataSet dataset = node.getSubNodeDataSet();
		for (int i = 0; i < node.size(); i++) {
			final WrapperObject  obj = node.get(i);
			final SubNodeDataSet set = obj.getSubNodeDataSet();
			if (set != null && dataset != null) {
				dataset.getChildren().add(set);
			}
			this.linkAllSubNodeDataSet(obj);
		}
	}

	private WrapperObject preprocessing() {
		final WrapperObject wrapperrootnode = new WrapperObject(this.root);
		this.buildWrappingTree(this.root, wrapperrootnode);
		this.collectAllSubNode(wrapperrootnode);
		this.linkAllSubNodeDataSet(wrapperrootnode);
		return wrapperrootnode;
	}

	public void buildInferSchema() {
		final WrapperObject wrapperrootnode = this.preprocessing();
		final SchemaNominator preschema = new SchemaNominator(this);
		preschema.nominate();
		final SchemaDecider defineschema = new SchemaDecider(preschema, wrapperrootnode);
		final Map<String, SubNodeDataSet> definedschema = defineschema.define();
		final Matcher matcher = new SchemaMatcher(definedschema);
		matcher.match(wrapperrootnode);
	}

	public void buildFixedSchema() {
		final WrapperObject wrapperrootnode = this.preprocessing();
		final TreeTypeChecker checker = new TreeTypeChecker();
		final Map<String, Set<String>> definedschema = checker.check(wrapperrootnode);
		final Matcher matcher = new FixedSchemaMatcher(definedschema);
		matcher.match(wrapperrootnode);
	}

	public void buildDTD() {
		final WrapperObject wrapperrootnode = this.preprocessing();
		final SchemaNominator preschema = new SchemaNominator(this);
		preschema.nominate();
		final SchemaDecider defineschema = new SchemaDecider(preschema, wrapperrootnode);
		final Map<String, SubNodeDataSet> definedschema = defineschema.define();
		final Matcher matcher = new DTDMatcher(definedschema);
		matcher.match(wrapperrootnode);
	}
}
