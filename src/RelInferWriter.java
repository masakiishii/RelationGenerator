package org.peg4d.data;

import java.util.LinkedList;
import java.util.Queue;

import org.peg4d.ParsingObject;
import org.peg4d.million.SourceGenerator;
import org.peg4d.million.SweetJSGenerator;
import org.peg4d.million.SweetJSWriter;
import org.peg4d.writer.ParsingWriter;

public class RelInferWriter extends ParsingWriter {

	static {
		ParsingWriter.registerExtension("relinfer", RelInferWriter.class);
	}

	@Override
	protected void write(ParsingObject po) {
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.add(po);
		int nodecounter = 0;
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			nodecounter++;
			for(int i = 0; i < node.size(); i++) {
				queue.add(node.get(i));
			}
		}
		System.out.println(nodecounter);
		//RelationBuilder rbuilder = new RelationBuilder(po);
		//rbuilder.buildInferSchema();
	}
}
