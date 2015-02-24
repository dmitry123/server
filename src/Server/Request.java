package Server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

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

		if (key.equalsIgnoreCase("Content-Type")) {
			StringTokenizer tokenizer = new StringTokenizer(body, ";");
			String str; int index;
			if (tokenizer.hasMoreTokens()) {
				contentType.name = tokenizer.nextToken().trim();
			}
			while (tokenizer.hasMoreTokens()) {
				if ((index = (str = tokenizer.nextToken().trim()).indexOf('=')) != -1) {
					contentType.put(str.substring(0, index), str.substring(index + 1));
				}
			}
		} else if (key.equalsIgnoreCase("Cookie")) {
			StringTokenizer tokenizer = new StringTokenizer(body, ";");
			String str; int index;
			while (tokenizer.hasMoreTokens()) {
				if ((index = (str = tokenizer.nextToken().trim()).indexOf('=')) != -1) {
					cookie.put(str.substring(0, index), str.substring(index + 1));
				}
			}
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

	/**
	 * That class is extension of hash map with
	 * received content type name
	 */
	public static class ContentType extends HashMap<String, String> {

		/**
		 * Get mime type of received content
		 * @return - Received content mime type
		 */
		public String getName() {
			return name;
		}

		private String name;
	}

	/**
	 * Get collection with header's content type
	 * @return - Content type parameters
	 */
	public ContentType getContentType() {
		return contentType;
	}

	/**
	 * Get collection with cookies
	 * @return - Cookie parser
	 */
	public Map<String, String> getCookie() {
		return cookie;
	}

	/**
	 * Check is request sent via XMLHttpRequest
	 * @return - True is request is ajax
	 */
	public boolean isAjax() {
		return isAjax;
	}

	private boolean isAjax = false;

	private Map<String, String> queryParameters
			= new HashMap<>();

	private Map<String, String> postParameters
			= new HashMap<>();

	private Set<File> postFiles
			= new HashSet<>();

	private ContentType contentType
			= new ContentType();

	private Map<String, String> cookie
			= new HashMap<>();

	private Method method;
	private String path;
	private String protocol;
}
