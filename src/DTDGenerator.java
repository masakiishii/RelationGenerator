package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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

	private void generateData(String tablename, Matcher matcher, int index) {
		final ArrayList<ArrayList<String>> datalist = matcher.getTable().get(tablename);
		final StringBuilder builder = new StringBuilder();
		final Set<String> columnset = new LinkedHashSet<String>();
		for(int i = 0; i < datalist.size(); i++) {
			final ArrayList<String> line = datalist.get(i);
			String[] linedata = line.get(index).split(",");
			for(int j = 0; j < linedata.length; j++) {
				System.out.print(linedata[j] + " ");
				columnset.add(linedata[j]);
			}
			System.out.println();
			builder.append(line.get(index));
			builder.append("\n");
		}
		System.out.println();
		System.out.println();
		for(String s : columnset) {
			System.out.print(s + " ");
		}
		//System.out.println(builder.toString());
		System.out.println();
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
//			buffer.append(column);
//			buffer.append("\t");
		}
		return -1;
		//System.out.println(buffer.toString());
		//System.out.println("---------------------------------------");
	}

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
//			System.out.println("tablename: " + tablename);
//			System.out.println("-------------------------------");
//			this.generateColumns(tablename, matcher);
//			this.generateData(tablename, matcher);
		}
	}
}
