
public enum ContentType {

	IMAGE_GIF       ("image/gif", ".gif"),
	IMAGE_JPEG      ("image/jpeg", ".jpeg", ".jpg"),
	IMAGE_PJPEG     ("image/pjpeg"),
	IMAGE_PNG       ("image/png", ".png"),
	IMAGE_SVG_XML   ("image/svg+xml"),
	IMAGE_TIFF      ("image/tiff", ".tiff"),
	IMAGE_ICO       ("image/vnd.microsoft.icon", ".ico"),
	IMAGE_WBMP      ("image/vnd.wap.wbmp"),

	TEXT_CMD        ("text/cmd"),
	TEXT_CSS        ("text/css", ".css"),
	TEXT_CSV        ("text/csv", ".csv"),
	TEXT_HTML       ("text/html", ".html"),
	TEXT_JAVASCRIPT ("text/javascript", ".js"),
	TEXT_PLAIN      ("text/plain"),
	TEXT_PHP        ("text/php", ".php"),
	TEXT_XML        ("text/xml", ".xml"),

	APPLICATION_OCTET_STREAM ("application/octet-stream", ".map", ".ttf", ".eot", ".woff"),
	APPLICATION_JSON ("application/json ", ".json");

	/**
	 * Construct content type with mime name and list with extensions
	 * @param mime - Mime name's name
	 * @param extensions - Extensions arguments
	 */
	private ContentType(String mime, String... extensions) {
		this.name = mime;
		this.extensions = extensions;
	}

	/**
	 * Find name enumeration by it's extension
	 *
	 * @param extension - Extension format
	 * @return - Found name or null
	 */
	static public ContentType findByExtension(String extension) {
		for (ContentType m : ContentType.values()) {
			for (String s : m.getExtensions()) {
				if (s.equals(extension)) {
					return m;
				}
			}
		}
		return null;
	}

	/**
	 * Check is content type image
	 * @return - True if content type looks like image
	 */
	public boolean isImage() {
		return name.startsWith("image");
	}

	/**
	 * Check is content type text
	 * @return - True if content type looks like text
	 */
	public boolean isText() {
		return name.startsWith("text");
	}

	/**
	 * Check is content type application
	 * @return - True if content type looks like application type
	 */
	public boolean isApplication() {
		return name.startsWith("application");
	}

	/**
	 * Get content type name
	 * @return - Type's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get array with supported content type extensions
	 * @return - Type's extensions
	 */
	public String[] getExtensions() {
		return extensions;
	}

	private String name = null;
	private String[] extensions = null;
}
