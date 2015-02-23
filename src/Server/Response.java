package Server;

import java.text.SimpleDateFormat;
import java.util.*;

public class Response {

	/**
	 * Construct response with only response code for error or something else
	 * @param responseCode - Server response code
	 */
	public Response(ResponseCode responseCode) {
		this(responseCode, "");
	}

	/**
	 * Construct response with response code and html text
	 * @param responseCode - Server response code
	 * @param responseMessage - Response message
	 */
	public Response(ResponseCode responseCode, String responseMessage) {
		this(responseCode, ContentType.TEXT_HTML,
			responseMessage != null ? responseMessage.getBytes() : "null".getBytes(), null);
	}

	/**
	 * Construct response with response code, byte array and headers
	 * @param responseCode - Server response code
	 * @param contentType - Type of sending content
	 * @param responseData - Response stuff data
	 * @param header - Map with extra headers for response
	 */
	public Response(ResponseCode responseCode, ContentType contentType, byte[] responseData, Map<String, String> header) {

		this.responseCode = responseCode;
		this.contentType = contentType;
		this.responseData = responseData;

		if (header != null && header.size() > 0) {
			header.putAll(header);
		}
	}

	/**
	 * Get response result header with hypertext
	 * body or binary stuff data
	 * @return - String with response header and body
	 */
	public byte[] getResult() {

		StringBuilder builder = new StringBuilder("HTTP/1.1 "
			+ getResponseCode().getCode() + " "
			+ getResponseCode().getDescription() + "\r\n");

		prepare();

		for (Map.Entry<String, String> entry : getHeader().entrySet()) {
			builder.append(entry.getKey()).append(":").append(entry.getValue()).append("\r\n");
		}
		builder.append("\r\n");

		byte[] buffer = new byte[builder.length() + getResponseData().length];

		System.arraycopy(builder.toString().getBytes(), 0, buffer, 0, builder.toString().length());
		System.arraycopy(getResponseData(), 0, buffer, builder.toString().length(), getResponseData().length);

//		System.out.println("------------------------------------");
//		System.out.println(new String(buffer));
//		System.out.println("------------------------------------");

		return buffer;
	}

	/**
	 * Prepare response headers based on content
	 * type, response code etc
	 */
	private void prepare() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US
		);

		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		header.put("Content-Length", Integer.toString(getResponseData().length));
		header.put("Content-Type", getContentType().getName());
		header.put("Date", simpleDateFormat.format(new Date()));

		if (!header.containsKey("Connection")) {
			header.put("Connection", "close");
		}

		for (Cookie.Node node : cookie.getSet()) {
			header.put("Set-Cookie", node.format());
		}

		for (Cookie.Node node : cookie.getDelete()) {
			header.put("Set-Cookie", node.format());
		}

		header.put("Server", "Jaw-Server");
	}

	/**
	 * Get map with response headers
	 * @return - Response headers
	 */
	public Map<String, String> getHeader() {
		return header;
	}

	/**
	 * Get enum with response code and it's description
	 * @return - Response code
	 */
	public ResponseCode getResponseCode() {
		return responseCode;
	}

	/**
	 * Get response content type
	 * @return - Response content type
	 */
	public ContentType getContentType() {
		return contentType;
	}

	/**
	 * Get response body data
	 * @return - response data
	 */
	public byte[] getResponseData() {
		return responseData;
	}

	/**
	 * Get response cookie
	 * @return - Response cookie
	 */
	public Cookie getCookie() {
		return cookie;
	}

	private byte[] responseData;

	private Cookie cookie = new Cookie();

	private Map<String, String> header
		= new LinkedHashMap<String, String>();

	private ContentType contentType;
	private ResponseCode responseCode;
}
