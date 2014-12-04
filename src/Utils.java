package org.peg4d.data;

public class Utils {
	public static final boolean UseFastParseNumber = !false;
	public static final boolean UseFastEscapeData = false;

	public static boolean isNumber(String str) {
		int i = 0;
		final int len = str.length();
		char c = str.charAt(i++);
		if (c == '-' || c == '+') {
			c = str.charAt(i++);
		}
		switch (c) {
		case 'i':
			return str == "inf";
		case 'I':
			return str == "Inf";
		case 'n':
			return str == "nan";
		case 'N':
			return str == "Nan";
		case '0':
			c = str.charAt(i++);
			break;
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			for (; '0' <= c && c <= '9' && i < len; c = str.charAt(i++)) {
			}
			break;
		}
		if (c != '.' && c != 'e' && c != 'E') {
			return i == len;
		}
		if (c == '.') {
			for (c = str.charAt(++i); '0' <= c && c <= '9' && i < len; c = str.charAt(i++)) {
			}
		}
		if (c == 'e' || c == 'E') {
			c = str.charAt(i++);
			if (c == '+' || c == '-') {
				c = str.charAt(i++);
			}
			if (!('0' <= c && c <= '9')) {
				return false;
			}

			for (; '0' <= c && c <= '9' && i < len; c = str.charAt(i++)) {
			}
		}
		return i == len;
	}

	public static void escapeData(StringBuilder sbuf, String str) {
		final int len = str.length();
		for (int i = 0; i < len; i++) {
			final char ch = str.charAt(i);
			if (ch == '\n') {
				sbuf.append("\\n");
			} else if (ch == '\t') {
				sbuf.append("\\t");
			} else {
				sbuf.append(ch);
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(isNumber("3.14"));
		System.out.println(isNumber("+3"));
		System.out.println(isNumber("-.14"));
		System.out.println(isNumber("1e10"));
	}
}
