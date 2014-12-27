package org.peg4d.data;

import java.util.Map;

public abstract class Generator {

	abstract public void generate(Matcher matcher);

	abstract public void generate(Matcher matcher, Map<String, String> table);
}
