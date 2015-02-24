package Server;

import Core.Environment;
import Core.TemporaryFileManager;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Session implements Runnable {

	public static final int CHUNK = 8192;

	/**
	 * Construct session with it's socket
	 * @param socket - Client's socket
	 */
	public Session(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	/**
	 * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
	 * causes the object's <code>run</code> method to be called in that separately executing thread.
	 * <p/>
	 * The general contract of the method <code>run</code> is that it may take any action whatsoever.
	 * @see Thread#run()
	 */
	@Override
	public void run() {

		OutputStream output = null;
		Response response;
		InputStream input = null;

		try {
			input = socket.getInputStream();
			output = socket.getOutputStream();

			byte[] chunk = new byte[CHUNK];
			int read;

			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			while ((read = input.read(chunk)) == chunk.length) {
				stream.write(chunk, 0, read);
			}
			if (read > 0) {
				stream.write(chunk, 0, read);
			}

			byte[] buffer = stream.toByteArray();
			byte[] header = getByteArray(buffer, 0, findEntry(buffer, "\r\n\r\n".getBytes()));

			response = parseHeader(new String(header),
				getByteArray(buffer, header.length, buffer.length - header.length)
			);

			if (response == null) {
				response = getServer().getSessionListener().process(this);
			}

			output.write(response.getResult());

		} catch (Exception e) {
			try {
				StringWriter writer = new StringWriter();
				PrintWriter printer = new PrintWriter(writer);
				e.printStackTrace(printer);
				if ((output = output != null ? output : socket.getOutputStream()) != null) {
					output.write(new Response(ResponseCode.INTERNAL_SERVER_ERROR,
						"<pre>" + writer.toString() + "</pre>").getResult()
					);
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}

		try {
			if (input != null) {
				input.close();
			}
			if (output != null) {
				output.close();
			}
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find sequence entry in byte array
	 * @param bytes - Source array with bytes
	 * @param pattern - Sequence to seek
	 * @return - Sequence's entry position or -1
	 */
	public static int findEntry(byte[] bytes, byte[] pattern) {
		return findEntry(bytes, pattern, 0);
	}

	/**
	 * Find sequence entry in byte array
	 * @param bytes - Source array with bytes
	 * @param pattern - Sequence to seek
	 * @return - Sequence's entry position or -1
	 */
	public static int findEntry(byte[] bytes, byte[] pattern, int offset) {
		int[] failure = computeFailure(pattern);
		for (int i = offset, j = 0; i < bytes.length; i++) {
			while (j > 0 && pattern[j] != bytes[i]) {
				j = failure[j - 1];
			}
			if (pattern[j] == bytes[i]) {
				j++;
			}
			if (j == pattern.length) {
				return i - pattern.length + 1;
			}
		}
		return -1;
	}

	/**
	 * Cut sequence from byte array buffer
	 * @param array - Default array with bytes
	 * @param offset - Array offset
	 * @param limit - New array length limit
	 * @return - Part of byte array
	 */
	public static byte[] getByteArray(byte[] array, int offset, int limit) {
		byte[] bytes = new byte[limit];
		System.arraycopy(array, offset, bytes, 0, limit);
		return bytes;
	}

	/**
	 * Computes the failure function using a boot-strapping process,
	 * where the pattern is matched against itself.
	 */
	private static int[] computeFailure(byte[] pattern) {
		int[] failure = new int[pattern.length];
		int j = 0;
		for (int i = 1; i < pattern.length; i++) {
			while (j > 0 && pattern[j] != pattern[i]) {
				j = failure[j - 1];
			}
			if (pattern[j] == pattern[i]) {
				j++;
			}
			failure[i] = j;
		}
		return failure;
	}

	/**
	 * Parse received HTTP header message and body (for POST/PUT request methods)
	 * @param message - Message headers
	 * @param body - Body headers
	 * @return - Null on ok or response with error message
	 * @throws Exception
	 */
	private Response parseHeader(String message, byte[] body) throws Exception {

		System.out.println("-----------------------");
		System.out.println(message);
		System.out.println("-----------------------");

		if (message.equals("")) {
			return new Response(ResponseCode.NO_CONTENT, "");
		}

		request = new Request(message.split("\r\n"));

		if (request.getMethod() == null) {
			return new Response(ResponseCode.METHOD_NOT_ALLOWED);
		}

		if (!request.getProtocol().equalsIgnoreCase("HTTP/1.1") &&
			!request.getProtocol().equalsIgnoreCase("HTTP/1.0")
		) {
			return new Response(ResponseCode.HTTP_VERSION_NOT_SUPPORTED);
		}

		if (Request.Method.POST.equals(request.getMethod()) ||
			Request.Method.PUT.equals(request.getMethod())
		) {
			if (request.getContentType().getName().equalsIgnoreCase("multipart/form-data")) {
				if (request.getContentType().containsKey("boundary")) {
					parseMultipartData(body, request.getContentType().get("boundary"));
				} else {
					return new Response(ResponseCode.BAD_REQUEST, "Can't find boundary for multipart data");
				}
			} else {
				request.parsePost(new String(body));
			}
		}

		return null;
	}

	/**
	 * Read row from header array
	 * @param header - Array with headers
	 * @param key - Header's key
	 * @return - Found header's row for key
	 */
	private String readHeaderRow(String[] header, String key) {
		for (String h : header) {
			String[] list = h.split(":");
			if (list.length != 2) {
				continue;
			}
			if (list[0].trim().equalsIgnoreCase(key)) {
				return list[1];
			}
		}
		return null;
	}

	/**
	 * Parse header and get it's map with semicolon
	 * as delimiter
	 * @param source - Source header string
	 * @return - Map with header parameters
	 */
	private Map<String, String> parseHeaderString(String source) {
		return parseHeaderString(source, ";");
	}

	/**
	 * Parse header and get hash map with parsed values separated
	 * by delimiter
	 * @param source - Source header string
	 * @param delimiter - Parameter delimiter
	 * @return - Map with parsed parameters
	 */
	private Map<String, String> parseHeaderString(String source, String delimiter) {
		Map<String, String> header = new HashMap<>();
		StringTokenizer tokenizer = new StringTokenizer(source, delimiter);
		int index;
		while (tokenizer.hasMoreTokens()) {
			String str = tokenizer.nextToken().trim();
			if ((index = str.indexOf('=')) != -1) {
				header.put(str.substring(0, index), str.substring(index + 1));
			} else {
				header.put(str, null);
			}
		}
		return header;
	}

	/**
	 * Write file's content to temporary file and store in
	 * request map with received post files
	 * @param fileName - File's name
	 * @param type - Type of file's content
	 * @param content - Array with file's data
	 * @return - Temporary file's name
	 */
	private String writeDataToTemporaryFile(String fileName, String type, byte[] content) {
		if (fileName.startsWith("\"")) {
			fileName = fileName.substring(1, fileName.length() - 1);
		}
		File file;
		try {
			java.io.File handle = TemporaryFileManager.getDefaultManager().create();
			FileOutputStream stream = new FileOutputStream(handle);
			stream.write(content);
			stream.flush();
			stream.close();
			file = new File(server, fileName, handle.getAbsolutePath(), type);
		} catch (Exception e) {
			file = new File(server, fileName, null, type, e);
		}
		request.getPostFiles().add(file);
		return file.getTemporaryName();
	}

	/**
	 * Parse multipart data
	 * @param buffer - Header's body
	 */
	private void parseMultipartData(byte[] buffer, String boundary) {

		int offset = -1, index;

		while ((offset = findEntry(buffer, boundary.getBytes(), offset + 1)) != -1) {

			if ((index = findEntry(buffer, boundary.getBytes(), offset + 1)) == -1) {
				break;
			}

			// 6 = 2 (CRLF) + 2 ("--") + 2 (CRLF)
			byte[] data = getByteArray(buffer,
				offset + boundary.length() + 2, index - offset - boundary.length() - 6
			);

			String header = new String(getByteArray(data, 0,
				findEntry(data, "\r\n\r\n".getBytes())
			));
			String[] headers = header.split("\r\n");

			System.out.println(header);
			System.out.println("+++++++++++++++++++");

			Map<String, String> disposition = parseHeaderString(
				readHeaderRow(headers, "Content-Disposition")
			);

			String contentType = readHeaderRow(headers, "Content-Type");

			if (!disposition.containsKey("form-data")) {
				continue;
			}

			byte[] content = getByteArray(data,
				header.length() + 4, data.length - header.length() - 4
			);

			if (disposition.containsKey("filename")) {
				request.getPostParameters().put(disposition.get("name"),
					writeDataToTemporaryFile(disposition.get("filename"), contentType, content)
				);
			} else {
				request.getPostParameters().put(disposition.get("name"),
					new String(content)
				);
			}
		}
	}

	/**
	 * Get null environment
	 * @return - Null environment
	 */
	public Environment getNullEnvironment() {
		try {
			return getServer().getEnvironmentManager().get("<null>");
		} catch (Exception ignored) {
		}
		return null;
	}

	/**
	 * Get client's request
	 * @return - Request
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * Get server class with socket and configuration
	 * @return - Server class with thread
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Get client's socket for current session
	 * @return - Client's socket
	 */
	public Socket getSocket() {
		return socket;
	}

	private Request request;
	private Server server;
	private Socket socket;
}
