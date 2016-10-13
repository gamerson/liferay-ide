package com.liferay.ide.project.core.modules.templates  ;

import java.util.Properties;

@SuppressWarnings( "unused" )
final class BndPropertiesParser {
	private final char[]		source;
	private final int			length;
	private final String		file;
	private static final char	MIN_DELIMETER	= '\t';
	private static final char	MAX_DELIMETER	= '\\';
	private final static byte[]	INFO			= new byte[MAX_DELIMETER + 1];
	private final static byte	WS				= 1;
	private final static byte	KEY				= 2;
	private final static byte	LINE			= 4;
	private final static byte	NOKEY			= 8;

	static {
		INFO['\t'] = KEY + WS;
		INFO['\n'] = KEY + LINE;
		INFO['\f'] = KEY + WS;
		INFO[' '] = KEY + WS;
		INFO[','] = NOKEY;
		INFO[';'] = NOKEY;
		INFO['!'] = NOKEY;
		INFO['\''] = NOKEY;
		INFO['"'] = NOKEY;
		INFO['#'] = NOKEY;
		INFO['('] = NOKEY;
		INFO[')'] = NOKEY;
		INFO[':'] = KEY;
		INFO['='] = KEY;
		INFO['\\'] = NOKEY;
	}

	private int			n		= 0;
    private int			line	= 0;
	private int			pos		= -1;
	private int			marker	= 0;
	private char		current;
	private Properties	properties;
	private boolean		validKey;

	BndPropertiesParser(String source, String file, Properties properties) {
		this.source = source.toCharArray();
		this.file = file;
		this.length = this.source.length;
		this.properties = properties;
	}

	boolean hasNext() {
		return n < length;
	}

	char next() {
		if (n >= length)
			return current = '\n';

		current = source[n++];
		try {
			switch (current) {
				case '\\' :
					if (peek() == '\r' || peek() == '\n') {
						next(); // skip line ending
						next(); // first character on new line
						skipWhitespace();
						return current;
					} else
						return '\\';

				case '\r' :
					current = '\n';
					if (peek() == '\n') {
						n++;
					}
					line++;
					pos = -1;
					return current;

				case '\n' :
					if (peek() == '\r') {
						// a bit weird, catches \n\r
						n++;
					}
					line++;
					pos = -1;
					return current;

				case '\t' :
				case '\f' :
					return current;

				default :
					if (current < ' ') {
						return current = '?';
					}
					return current;
			}
		} finally {
			pos++;
		}
	}

	void skip(byte delimeters) {
		while (isIn(delimeters)) {
			next();
		}
	}

	char peek() {
		if (hasNext())
			return source[n];
		else
			return '\n';
	}

	void parse() {

		while (hasNext()) {
			marker = n;
			next();
			skipWhitespace();

			if (isEmptyOrComment(current)) {
				skipLine();
				continue;
			}

			this.validKey = true;
			String key = key();

			if (!validKey) {
				//error("Invalid property key: `%s`", key);
			}

			skipWhitespace();

			if (current == ':' || current == '=') {
				next();
				skipWhitespace();
				if (current == '\n') {
					properties.put(key, "");
					continue;
				}
			}

			if (current != '\n') {

				String value = token(LINE);
				properties.put(key, value);

			} else {
				//error("No value specified for key: %s. An empty value should be specified as '%<s:' or '%<s='", key);
				properties.put(key, "");
				continue;
			}
			assert current == '\n';
		}
	}

	private void skipWhitespace() {
		skip(WS);
	}

	public boolean isEmptyOrComment(char c) {
		return c == '\n' || c == '#' || c == '!';
	}

	public void skipLine() {
		while (!isIn(LINE))
			next();
	}

	private final String token(byte delimeters) {
		StringBuilder sb = new StringBuilder();
		while (!isIn(delimeters)) {
			char tmp = current;
			if (tmp == '\\') {
				tmp = backslash();

				if (tmp == 0) // we hit \\n\n
					break;
			}
			sb.append(tmp);
			next();
		}
		return sb.toString();
	}

	private final String key() {
		StringBuilder sb = new StringBuilder();
		while (!isIn(KEY)) {
			if (isIn(NOKEY))
				validKey = false;

			char tmp = current;
			if (tmp == '\\') {
				tmp = backslash();

				if (tmp == 0) // we hit \\n\n
					break;
			}
			sb.append(tmp);
			next();
		}
		return sb.toString();
	}

	private final boolean isIn(byte delimeters) {
		if (current < MIN_DELIMETER || current > MAX_DELIMETER)
			return false;
		return (INFO[current] & delimeters) != 0;
	}

	private final char backslash() {
		char c;
		c = next();
		switch (c) {
			case '\n' :
				return 0;

			case 'u' :
				StringBuilder sb = new StringBuilder();
				c = 0;
				for (int i = 0; i < 4; i++) {
					sb.append(next());
				}
				String unicode = sb.toString();
				if (!Hex.isHex(unicode)) {
					//error("Invalid unicode string \\u%s", sb);
					return '?';
				} else {
					return (char) Integer.parseInt(unicode, 16);
				}

			case ':' :
			case '=' :
				return c;
			case 't' :
				return '\t';
			case 'f' :
				return '\f';
			case 'r' :
				return '\r';
			case 'n' :
				return '\n';
			case '\\' :
				return '\\';

			case '\f' :
			case '\t' :
			case ' ' :
				//error("Found \\<whitespace>. This is allowed in a properties file but not in bnd to prevent mistakes");
				return c;

			default :
				return c;
		}
	}

	private String context() {
		int loc = n;
		while (loc < length && source[loc] != '\n')
			loc++;
		return new String(source, marker, loc - marker);
	}

}