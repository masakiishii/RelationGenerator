package org.peg4d.data;

import org.peg4d.Grammar;
import org.peg4d.ParsingRule;

public class PegSchemaGenerator {
	public PegSchemaGenerator() {
		
	}
	
	private void generating(ParsingRule rule) {
	}
	
	public void generate(Grammar peg) {
		ParsingRule start = peg.getRule("File");
		generating(start);
	}
}
