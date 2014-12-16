package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class DTDGenerator extends Generator {
	private Map<String, SubNodeDataSet> definedschema = null;
	private ArrayList<String>           rootlist      = null;
	private Map<String, DTDObject>      dtdobjectmap  = null;
	
	public DTDGenerator(Map<String, SubNodeDataSet> definedschema) {
		this.definedschema = definedschema;
		this.rootlist      = new ArrayList<String>();
		this.dtdobjectmap  = new LinkedHashMap<String, DTDObject>();
	}
	
	public DTDGenerator() {
		this.rootlist = new ArrayList<String>();
		this.dtdobjectmap  = new LinkedHashMap<String, DTDObject>();
	}

	private void initializeDTDObject(Set<String> columnset, int tuplesize) {
		for(String element : columnset) {
			DTDObject dtdobject = new DTDObject(element, tuplesize);
			this.dtdobjectmap.put(element, dtdobject);
		}
		for(String key : this.dtdobjectmap.keySet()) {
			System.out.println(this.dtdobjectmap.get(key).getElement());
		}
	}

	private Set<String> getColumnElementSet(String tablename, Matcher matcher, int index) {
		final Set<String> columnset = new LinkedHashSet<String>();
		final ArrayList<ArrayList<String>> datalist = matcher.getTable().get(tablename);
		final int tuplesize = datalist.size();
		for(int i = 0; i < tuplesize; i++) {
			final ArrayList<String> line = datalist.get(i);
			String[] linedata = line.get(index).split(",");
			for(int j = 0; j < linedata.length; j++) {
				System.out.print(linedata[j] + " ");
				columnset.add(linedata[j]);
			}
			System.out.println();
		}
		return columnset;
	}

	private void countColumnElement(String tablename, Matcher matcher, int index) {
		final ArrayList<ArrayList<String>> datalist = matcher.getTable().get(tablename);
		final int tuplesize = datalist.size();
		DTDObject obj;
		for(int i = 0; i < tuplesize; i++) {
			final ArrayList<String> line = datalist.get(i);
			String[] linedata = line.get(index).split(",");
			for(int j = 0; j < linedata.length; j++) {
				obj = this.dtdobjectmap.get(linedata[j]);
				obj.getCountList()[i]++;
			}
		}
		System.out.println("-------------------------------");
		System.out.println();
		for(String key : this.dtdobjectmap.keySet()) {
			DTDObject dtdobject = this.dtdobjectmap.get(key);
			System.out.println(dtdobject.getElement());
			for(int i = 0; i < dtdobject.getCountList().length; i++) {
				System.out.print(dtdobject.getCountList()[i] + ", ");;
			}
			System.out.println();
		}
	}

	private void generateData(String tablename, Matcher matcher, int index) {
		final int tuplesize = matcher.getTable().get(tablename).size();
		final Set<String> columnset = this.getColumnElementSet(tablename, matcher, index);
		this.initializeDTDObject(columnset, tuplesize);
		this.countColumnElement(tablename, matcher, index);
	}

	private int generateColumns(String tablename, Matcher matcher) {
		final Set<String>  columns = matcher.getSchema(tablename);
		final StringBuilder buffer  = new StringBuilder();
		int index = 0;
		for(final String column : columns) {
			if(column.equals("text")) {
				System.out.println(index);
				buffer.append(column);
				buffer.append("\t");
				System.out.println(buffer.toString());
				System.out.println("---------------------------------------");
				return index;
			}
			index++;
		}
		return -1;
	}

//	private void emitAttribute(WrapperObject node) {
//		for(int i = 0; i < node.size(); i++) {
//			WrapperObject child = node.get(i);
//			if(child.getTag().toString().equals("Attr")) {
//				int index = i;
//				WrapperObject sibling = child.getParent().get(index - 1);
//				System.out.println("<!ATTLIST " + sibling.getText() + " " + child.get(0).getText() + " CDATA #REQUIRED>");
//			}
//			if(child.getTag().toString().equals("Element")) {
//				System.out.println("<!Element " + child.get(0).getText() + " ");
//			}
//		}
//	}

//	public void generate(WrapperObject node) {
//		if(node == null) {
//			return;
//		}
//		if(node.isTerminal() && node.getText().equals("categories")) {
//			WrapperObject parent = node.getParent();
//			for(int i = 0; i < parent.size(); i++) {
//				WrapperObject child = parent.get(i);
//				
//			}
//		}
//		for(int i = 0; i < node.size(); i++) {
//			this.generate(node.get(i));;
//		}
//	}

	@Override
	public void generate(Matcher matcher) {
		final Map<String, ArrayList<ArrayList<String>>> table = matcher.getTable();
		int index = -1;
		for(final String tablename : table.keySet()) {
			if(tablename.equals("category")) {
				System.out.println("tablename: " + tablename);
				System.out.println("-------------------------------");
				index = this.generateColumns(tablename, matcher);
				this.generateData(tablename, matcher, index);
			}
		}
	}
}
