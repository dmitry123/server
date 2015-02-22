package Server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Request {

	public enum Method {

		DELETE,
		PUT,
		POST,
		GET,
		HEAD,
		OPTIONS;

		/**
		 * Find method by it's name
		 * @param method - Method name
		 * @return - Method index or null
		 */
		static Method find(String method) {
			for (Method m : Method.values()) {
				if (m.toString().equalsIgnoreCase(method)) {
					return m;
				}
			}
			return null;
		}
	}

	/**
	 * Construct request with header array, it will parse all rows
	 * and initialize request for current header
	 * @param header - Array with received split header string via LF
	 */
	public Request(String[] header) {

		parseEntry(header[0]);

		for (int i = 1; i < header.length; i++) {
			parseRow(header[i]);
		}
	}

	public static interface Parser extends Cloneable {

		/**
		 * Override that method to parser header's
		 * row
		 * @param body - String with header row's body
		 */
		public void parse(String body);
	}

	public static class CacheControl extends HashMap<String, String> implements Parser {

		/**
		 * That method will parse and store Cache-Control header's row
		 * @param body - String with header row's body
		 */
		@Override
		public void parse(String body) {
			int index;
			for (String s : body.split(",")) {
				index = s.indexOf("=");
				if (index != -1) {
					put(s.substring(0, index), s.substring(index + 1));
				} else {
					put(s, null);
				}
			}
		}
	}

	public static class Accept extends HashSet<String> implements Parser {

		/**
		 * That method will parse and store Accept header's row
		 * @param body - String with header row's body
		 */
		@Override
		public void parse(String body) {
			for (String s : body.split(",")) {
				add(s);
			}
		}
	}

	public static class AcceptEncoding extends HashSet<String> implements Parser {

		/**
		 * That method will parse and store Accept-Encoding header's row
		 * @param body - String with header row's body
		 */
		@Override
		public void parse(String body) {
			for (String s : body.split(",")) {
				add(s.trim());
			}
		}
	}

	public static class AcceptLanguage extends HashSet<AcceptLanguage> implements Parser {

		/**
		 * That method will parser and store in map Accept-Language header's row
		 * @param body - String with header row's body
		 */
		@Override
		public void parse(String body) {
			int index;
			for (String s : body.split(",")) {
				index = s.indexOf(";");
				AcceptLanguage language = new AcceptLanguage();
				if (index != -1) {
					language.name = s.substring(0, index);
					language.range = Integer.parseInt(s.substring(index + 1));
				} else {
					language.name = s;
					language.range = 0;
				}
				add(language);
			}
		}

		/**
		 * Get parsed language's name (for sub-objects)
		 * @return - Language's alias
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get language's range. Default is 0
		 * @return - Language range
		 */
		public int getRange() {
			return range;
		}

		private String name;
		private int range;
	}

	public static class Cookie extends HashMap<String, String> implements Parser {

		/**
		 * That method will parse cookies and store in itself
		 * @param body - String with header row's body
		 */
		@Override
		public void parse(String body) {
			int index;
			for (String s : body.split(";")) {
				s = s.trim();
				index = s.indexOf('=');
				if (index != -1) {
					put(s.substring(0, index), s.substring(index + 1));
				} else {
					put(s, null);
				}
			}
		}
	}

	private Map<String, Parser> parsers = new HashMap<String, Parser>() {{
		put("Cache-Control", new CacheControl());
		put("Accept", new Accept());
		put("Accept-Encoding", new AcceptEncoding());
		put("Accept-Language", new AcceptLanguage());
		put("Cookie", new Cookie());
	}};

	/**
	 * Get collection with cache control header
	 * @return - Cache control parser
	 */
	public CacheControl getCacheControl() {
		return ((CacheControl) parsers.get("Cache-Control"));
	}

	/**
	 * Get collection with accept header
	 * @return - Accept parser
	 */
	public Accept getAccept() {
		return ((Accept) parsers.get("Accept"));
	}

	/**
	 * Get collection with accept encodings
	 * @return - Accept encoding parser
	 */
	public AcceptEncoding getAcceptEncoding() {
		return ((AcceptEncoding) parsers.get("Accept-Encoding"));
	}

	/**
	 * Get collection with accept languages
	 * @return - Accept language parser
	 */
	public AcceptLanguage getAcceptLanguage() {
		return ((AcceptLanguage) parsers.get("Accept-Language"));
	}

	/**
	 * Get collection with cookies
	 * @return - Cookie parser
	 */
	public Cookie getCookie() {
		return ((Cookie) parsers.get("Cookie"));
	}

	/**
	 * Get special parser for specific
	 * @param key - Header row's key
	 * @return - Row's parser
	 * @throws Exception
	 */
	public Parser getParser(String key) throws Exception {
		if (parsers.containsKey(key)) {
			return parsers.get(key);
		}
		throw new Exception("Unresolved header's row parser \"" + key + "\"");
	}

	/**
	 * Parse initial header entry with path, HTTP protocol
	 * and data send method
	 * @param string - Entry header's row
	 */
	private void parseEntry(String string) {

		String[] array = string.split(" ");

		method = array[0];
		path = array[1];
		protocol = array[2];
	}

	/**
	 * Parse other rows like cookie, host etc and put
	 * it to map with headers, also it will invoke sub-parsers, which
	 * will be parse specific row's information
	 * @param string - String with header's row
	 */
	private void parseRow(String string) {

		int colon = string.indexOf(":");

		String key = string.substring(0, colon).trim();
		String body = string.substring(colon + 1).trim();

		try {
			getParser(key).parse(body);
		} catch (Exception ignored) {
		}
	}

	/**
	 * Get sending method type
	 * @return - Method type
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Get request path
	 * @return - Request path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Get working request protocol
	 * @return - Request protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	private String method;
	private String path;
	private String protocol;
}
