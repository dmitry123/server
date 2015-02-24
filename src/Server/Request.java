package Server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Request {

	public enum Method {

//		DELETE,
		PUT,
		POST,
		GET;
//		HEAD,
//		OPTIONS;

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
		int i;
		parseEntry(header[0]);
		for (i = 1; i < header.length; i++) {
			if (header[i].length() == 0) {
				++i; break;
			}
			parseRow(header[i]);
		}
		if (getHeader().containsKey("X-Requested-With")) {
			isAjax = getHeader().get("X-Requested-With").equals("XMLHttpRequest");
		}
	}

	/**
	 * Check is request sent via XMLHttpRequest
	 * @return - True is request is ajax
	 */
	public boolean isAjax() {
		return isAjax;
	}

	private boolean isAjax = false;

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

		method = Method.find(array[0]);
		path = array[1];
		protocol = array[2];

		path = parseQuery(path);
	}

	/**
	 * Parse query response from path's URL
	 * @param path - Query url path
	 * @return - Path without GET parameters
	 */
	private String parseQuery(String path) {
		int index;
		if ((index = path.indexOf("?")) == -1) {
			return path;
		}
		parseLink(queryParameters, path.substring(index + 1));
		return path.substring(0, index);
	}

	/**
	 * Parse post body and store it in post
	 * parameters hash map
	 */
	public void parsePost(String body) {
		parseLink(postParameters, body);
	}

	/**
	 * Parse link and put received values to map
	 * @param map - Map to store data (query or post)
	 * @param link - String with parameters to parse
	 */
	private static void parseLink(Map<String, String> map, String link) {
		int sign;
		for (String s : decode(link).split("&")) {
			if ((sign = s.indexOf('=')) != -1) {
				map.put(s.substring(0, sign), s.substring(sign +  1));
			} else {
				map.put(s, null);
			}
		}
	}

	/**
	 * Decode url value
	 * @param url - Url to decode
	 * @return - Decoded url
	 */
	private static String decode(String url) {
		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException ignored) {
		}
		return url;
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

		header.put(key, body);
	}

	/**
	 * Get request content length
	 * @return - Length of request content
	 */
	public int getContentLength() {
		if (getHeader().containsKey("Content-Length")) {
			return Integer.parseInt(getHeader().get("Content-Length"));
		} else {
			return -1;
		}
	}

	/**
	 * Get parsed header information
	 * @return - Map with parsed headers
	 */
	public Map<String, String> getHeader() {
		return header;
	}

	private Map<String, String> header
		= new HashMap<>();

	/**
	 * Get query parameter from request URL
	 * @param key - Parameter key
	 * @return - Parameter value or null
	 */
	public String getQuery(String key) {
		return queryParameters.get(key);
	}

	/**
	 * Get post parameter from request URL
	 * @param key - Parameter key
	 * @return - Parameter value or null
	 */
	public String getPost(String key) {
		return postParameters.get(key);
	}

	/**
	 * Get parameters sent via GET method
	 * @return - Query parameters
	 */
	public Map<String, String> getQueryParameters() {
		return queryParameters;
	}

	/**
	 * Get parameters sent via POST method
	 * @return - Post parameters
	 */
	public Map<String, String> getPostParameters() {
		return postParameters;
	}

	/**
	 * Get files received from client side
	 * @return - Map with server files
	 */
	public Set<File> getPostFiles() {
		return postFiles;
	}

	/**
	 * Get parameters sent via GET method
	 * @return - Query parameters
	 */
	public Map<String, Object> getQueryParametersEx() {
		Map<String, Object> map = new HashMap<>();
		for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	/**
	 * Get parameters sent via GET method
	 * @return - Query parameters
	 */
	public Map<String, Object> getPostParametersEx() {
		Map<String, Object> map = new HashMap<>();
		for (Map.Entry<String, String> entry : postParameters.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

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
	 * Get content type with mime type name and boundary
	 * @return - Content type
	 */
	public ContentType getContentType() {
		return ((ContentType) parsers.get("Content-Type"));
	}

	/**
	 * Get sending method type
	 * @return - Method type
	 */
	public Method getMethod() {
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

	private Map<String, String> queryParameters
			= new HashMap<>();

	private Map<String, String> postParameters
			= new HashMap<>();

	private Set<File> postFiles
			= new HashSet<>();

	private Method method;
	private String path;
	private String protocol;

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
				s = s.trim();
				index = s.indexOf("=");
				if (index != -1) {
					put(s.substring(0, index), s.substring(index + 1));
				} else {
					put(s, null);
				}
			}
		}
	}

	public static class ContentType extends HashMap<String, String> implements Parser {

		/**
		 * Override that method to parser header's row
		 * @param body - String with header row's body
		 */
		@Override
		public void parse(String body) {
			String[] list = body.split(";");
			if (list.length > 0) {
				name = list[0];
			}
			for (int i = 1; i < list.length; i++) {
				String[] split = list[i].trim().split("=");
				if (split.length >= 2) {
					put(split[0], split[1]);
				} else {
					put(split[0], null);
				}
			}
		}

		/**
		 * Get content type name
		 * @return - Content type name
		 */
		public String getName() {
			return name;
		}

		private String name = null;
	}

	public static class Accept extends HashSet<String> implements Parser {

		/**
		 * That method will parse and store Accept header's row
		 * @param body - String with header row's body
		 */
		@Override
		public void parse(String body) {
			for (String s : body.split(",")) {
				add(s.trim());
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
				s = s.trim();
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
		put("Content-Type", new ContentType());
	}};
}
