package org.peg4d.data;

import org.peg4d.ParsingObject;
import org.peg4d.writer.ParsingWriter;

public class RelFixedWriter extends ParsingWriter {

	static {
		ParsingWriter.registerExtension("relfixed", RelFixedWriter.class);
	}

	@Override
	protected void write(ParsingObject po) {
		RelationBuilder rbuilder = new RelationBuilder(po);
		rbuilder.buildFixedSchema();
	}
}
