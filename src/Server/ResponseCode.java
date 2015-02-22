package Server;

public enum ResponseCode {

	CONTINUE(100, "Continue"),
	SWITCHING_PROTOCOLS(101, "Switching Protocols"),
	PROCESSING(102, "Processing"),
	NAME_NOT_RESOLVED(105, "Name Not Resolved"),
	OK(200, "OK"),
	CREATED(201, "Created"),
	ACCEPTED(202, "Accepted"),
	NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
	NO_CONTENT(204, "No Content"),
	RESET_CONTENT(205, "Reset Content"),
	PARTIAL_CONTENT(206, "Partial Content"),
	MULTI_STATUS(207, "Multi-Status"),
	IM_USED(226, "IM Used"),
	MULTIPLE_CHOICES(300, "Multiple Choices"),
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	MOVED_TEMPORARILY(302, "Moved Temporarily"),
	FOUND(302, "Found"),
	SEE_OTHER(303, "See Other"),
	NOT_MODIFIED(304, "Not Modified"),
	USE_PROXY(305, "Use Proxy"),
	TEMPORARY_REDIRECT(307, "Temporary Redirect"),
	BAD_REQUEST(400, "Bad Request"),
	UNAUTHORIZED(401, "Unauthorized"),
	PAYMENT_REQUIRED(402, "Payment Required"),
	FORBIDDEN(403, "Forbidden"),
	NOT_FOUND(404, "Not Found"),
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
	NOT_ACCEPTABLE(406, "Not Acceptable"),
	PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
	REQUEST_TIMEOUT(408, "Request Timeout"),
	CONFLICT(409, "Conflict"),
	GONE(410, "Gone"),
	LENGTH_REQUIRED(411, "Length Required"),
	PRECONDITION_FAILED(412, "Precondition Failed"),
	REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
	REQUEST_URI_TOO_LARGE(414, "Request URI Too Large"),
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	UNREQUESTED_RANGE_NOT_SATISFIABLE(416, "Unrequested Range Not Satisfiable"),
	EXPECTATION_FAILED(417, "Expectation Failed"),
	IM_A_TEAPOT(418, "I'm a teapot"),
	UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
	LOCKED(423, "Locked"),
	FAILED_DEPENDENCY(424, "Failed Dependency"),
	UNORDERED_COLLECTION(425, "Unordered Collection"),
	UPGRADE_REQUIRED(426, "Upgrade Required"),
	PRECONDITION_REQUIRED(428, "Precondition Required"),
	TOO_MANY_REQUESTS(429, "Too Many Requests"),
	REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
	REQUESTED_HOST_UNAVAILABLE(434, "Requested Host Unavailable"),
	RETRY_WITH(449, "Retry With"),
	UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),
	UNRECOVERABLE_ERROR(456, "Unrecoverable Error"),
	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	NOT_IMPLEMENTED(501, "Not Implemented"),
	BAD_GATEWAY(502, "Bad Gateway"),
	SERVICE_UNAVAILABLE(503, "Service Unavailable"),
	GATEWAY_TIMEOUT(504, "Gateway Timeout"),
	HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
	VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
	INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
	LOOP_DETECTED(508, "Loop Detected"),
	BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded"),
	NOT_EXTENDED(510, "Not Extended"),
	NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");

	/**
	 * Construct response code with it's value and description
	 * @param code - Response code
	 * @param description - Code description
	 */
	private ResponseCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Get response code enum by it's code
	 * @param code - Response code
	 * @return - Response code object instance
	 */
	static ResponseCode find(int code) {
		for (ResponseCode rc : values()) {
			if (rc.getCode() == code) {
				return rc;
			}
		}
		return null;
	}

	/**
	 * Get response code value (3 digits)
	 * @return - Response code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Get response code description based on IETF
	 * @return - Response code description
	 */
	public String getDescription() {
		return description;
	}

	private int code;
	private String description;
}
