package Server;

import java.text.SimpleDateFormat;
import java.util.*;

public class Cookie {

	public static class Node {

		private String name;
		private String value;
		private String expires;
		private String path;

		public Node(String name, String value, String expires, String path) {
			this.name = name;
			this.value = value;
			this.expires = expires;
			this.path = path;
		}

		/**
		 * Format cookie parameter to http header
		 * string
		 * @return - Formatted cookie parameter
		 */
		public String format() {
			return String.format("%s=%s; expires=%s; path=%s",
				name, value, expires, path
			);
		}
	}

	/**
	 * Set new cookie parameter to response http
	 * header
	 * @param name - Name of cookie parameter
	 * @param value - Parameter value
	 * @param expires - Days to expire
	 * @param path - Server working path
	 */
	public void set(String name, String value, int expires, String path) {
		set.add(new Node(name, value, getHttpTime(expires), path));
	}

	/**
	 * Delete cookie from client side
	 * @param name - Name of cookie
	 */
	public void delete(String name) {
		set.add(new Node(name, "null", getHttpTime(-30), "/"));
	}

	/**
	 * Get hash set with cookies to set
	 * @return - Map with cookies
	 */
	public HashSet<Node> getSet() {
		return set;
	}

	/**
	 * Get hash set with cookies to delete
	 * @return - Set with cookies names
	 */
	public HashSet<Node> getDelete() {
		return delete;
	}

	/**
	 * Convert expire days to http time format based on current time
	 * @param days - Expire days to destroy cookie
	 * @return - String with time for http header
	 */
	private static String getHttpTime(int days) {

		Calendar calendar = Calendar.getInstance();

		SimpleDateFormat dateFormat = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss z", Locale.US
		);

		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.add(Calendar.DAY_OF_MONTH, days);

		return dateFormat.format(calendar.getTime());
	}

	private HashSet<Node> delete = new HashSet<Node>();
	private HashSet<Node> set = new HashSet<Node>();
}
