package org.peg4d.data;

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
		RelationBuilder rbuilder = new RelationBuilder(po);
		rbuilder.buildInferSchema();
		this.out.println("RelInferWriter");
	}
}
