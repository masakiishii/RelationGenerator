package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


public class DTDGenerator extends Generator {
	private Map<String, SubNodeDataSet> definedschema = null;
	private ArrayList<String>           rootlist      = null;
	
	public DTDGenerator(Map<String, SubNodeDataSet> definedschema) {
		this.definedschema = definedschema;
		this.rootlist      = new ArrayList<String>();
	}
	
	public DTDGenerator() {
		this.rootlist = new ArrayList<String>();
	}

	private void initializeDTDObject(Set<String> columnset, int tuplesize, Map<String, DTDObject> dtdobjectmap) {
		for(String element : columnset) {
			DTDObject dtdobject = new DTDObject(element, tuplesize);
			dtdobjectmap.put(element, dtdobject);
		}
	}

	private Set<String> getColumnElementSet(String tablename, Matcher matcher, int index) {
		final Set<String> columnset = new LinkedHashSet<String>();
		final ArrayList<ArrayList<String>> datalist = matcher.getTable().get(tablename);
		final int tuplesize = datalist.size();
		for(int i = 0; i < tuplesize; i++) {
			final ArrayList<String> line = datalist.get(i);
			final String data = line.get(index);
			if(data == null) {
				continue;
			}
			String[] linedata = data.split(",");
			for(int j = 0; j < linedata.length; j++) {
				columnset.add(linedata[j]);
			}
		}
		return columnset;
	}

	private ElementType fixMetaSymbol(boolean zero, boolean one, boolean more) {
		if(!zero && one && !more) {
			return ElementType.One;
		}
		if(zero && more) {
			return ElementType.More;
		}
		else if(zero && one && !more) {
			return ElementType.Optional;
		}
		else if(!zero && (one || more)) {
			return ElementType.Required;
		}
		else {
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

	private void countColumnElement(String tablename, Matcher matcher, int index, Map<String, DTDObject> dtdobjectmap) {
		final ArrayList<ArrayList<String>> datalist = matcher.getTable().get(tablename);
		final int tuplesize = datalist.size();
		DTDObject obj;
		for(int i = 0; i < tuplesize; i++) {
			final ArrayList<String> line = datalist.get(i);
			final String data = line.get(index);
			if(data == null) {
				continue;
			}
			String[] linedata = data.split(",");
			for(int j = 0; j < linedata.length; j++) {
				obj = dtdobjectmap.get(linedata[j]);
				obj.getCountList()[i]++;
			}
		}
		for(String key : dtdobjectmap.keySet()) {
			DTDObject dtdobject = dtdobjectmap.get(key);
			this.typeCheck(dtdobject);
		}
		for(String key : dtdobjectmap.keySet()) {
			DTDObject dtdobject = dtdobjectmap.get(key);
		}
	}

	private Map<String, DTDObject> getDTDObjectMap(String tablename, Matcher matcher, int index) {
		final int tuplesize = matcher.getTable().get(tablename).size();
		final Map<String, DTDObject> dtdobjectmap  = new LinkedHashMap<String, DTDObject>();
		final Set<String> columnset = this.getColumnElementSet(tablename, matcher, index);
		this.initializeDTDObject(columnset, tuplesize, dtdobjectmap);
		this.countColumnElement(tablename, matcher, index, dtdobjectmap);
		return dtdobjectmap;
	}

	private DTDLineList getDTDLineList(String tablename, Matcher matcher) {
		final Set<String> columns = matcher.getSchema(tablename);
		final DTDLineList dtdlinelist = new DTDLineList(tablename, columns.size());
		int index = 0;
		for(String column : columns) {
			if(index != 0) {
				Map<String, DTDObject> dtdobjectmap = this.getDTDObjectMap(tablename, matcher, index);
				DTDLine dtdline = new DTDLine(column, dtdobjectmap);
				dtdlinelist.setDTDLine(dtdline, index - 1);
			}
			index++;
		}
		return dtdlinelist;
	}

	@Override
	public void generate(Matcher matcher) {
		final Map<String, ArrayList<ArrayList<String>>> table = matcher.getTable();
		for(final String tablename : table.keySet()) {
			if(tablename.equals("open_auction")) {
				System.out.println("tablename: " + tablename);
				System.out.println("-------------------------------");
				DTDLineList dtdlinelist = this.getDTDLineList(tablename, matcher);
			}
		}
	}
}
