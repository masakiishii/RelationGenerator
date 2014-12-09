package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
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

	private void generateData(String tablename, Matcher matcher) {
		final ArrayList<ArrayList<String>> datalist = matcher.getTable().get(tablename);
		final StringBuilder buffer = new StringBuilder();
		for(int i = 0; i < datalist.size(); i++) {
			final ArrayList<String> line = datalist.get(i);
			for(int j = 0; j < line.size(); j++) {
				buffer.append(line.get(j));
				buffer.append("\t");
			}
			buffer.append("\n");
		}
		System.out.println(buffer.toString());
		System.out.println();
	}

	private void generateColumns(String tablename, Matcher matcher) {
		final Set<String>  columns = matcher.getSchema(tablename);
		final StringBuilder buffer  = new StringBuilder();
		for(final String column : columns) {
			buffer.append(column);
			buffer.append("\t");
		}
		System.out.println(buffer.toString());
		System.out.println("---------------------------------------");
	}

	public void generate(Matcher matcher) {
		final Map<String, ArrayList<ArrayList<String>>> table = matcher.getTable();
		for(final String tablename : table.keySet()) {
			System.out.println("tablename: " + tablename);
			System.out.println("-------------------------------");
			this.generateColumns(tablename, matcher);
			this.generateData(tablename, matcher);
		}
	}
}
