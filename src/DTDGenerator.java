package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
//				System.out.print(linedata[j] + " ");
				columnset.add(linedata[j]);
			}
//			System.out.println();
		}
		return columnset;
	}

	private ElementType fixMetaSymbol(boolean zero, boolean one, boolean more) {
		if(zero && more) {
			System.out.println("---<< more >>---");
			return ElementType.More;
		}
		else if(zero && one && !more) {
			System.out.println("---<< optional >>---");
			return ElementType.Optional;
		}
		else if(!zero && (one || more)) {
			System.out.println("---<< oneOrmore >>---");
			return ElementType.OneOrMore;
		}
		else {
			System.out.println("error");
			return null;
		}
	}

	private void typeCheck(DTDObject dtdobject) {
		boolean zero = false;
		boolean one  = false;
		boolean more = false;
		int number;
		for(int i = 0; i < dtdobject.getCountList().length; i++) {
			number = dtdobject.getCountList()[i];
			switch (number) {
			case 0:
				zero = true; break;
			case 1:
				one  = true; break;
			default:
				more = true; break;
			}
		}
		ElementType eletype = this.fixMetaSymbol(zero, one, more);
		dtdobject.setElementType(eletype);
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
		//System.out.println("column: text");
		System.out.println("column: description");
		for(String key : this.dtdobjectmap.keySet()) {
			DTDObject dtdobject = this.dtdobjectmap.get(key);
			System.out.println(dtdobject.getElement());
			for(int i = 0; i < dtdobject.getCountList().length; i++) {
				System.out.print(dtdobject.getCountList()[i] + ", ");;
			}
			System.out.println();
			this.typeCheck(dtdobject);
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
//			if(column.equals("text")) {
			if(column.equals("description")) {
				return index;
			}
			index++;
		}
		return -1;
	}

	@Override
	public void generate(Matcher matcher) {
		final Map<String, ArrayList<ArrayList<String>>> table = matcher.getTable();
		int index = -1;
		for(final String tablename : table.keySet()) {
//			if(tablename.equals("category")) {
			if(tablename.equals("open_auction")) {
				System.out.println("tablename: " + tablename);
				System.out.println("-------------------------------");
				index = this.generateColumns(tablename, matcher);
				this.generateData(tablename, matcher, index);
			}
		}
	}
}
