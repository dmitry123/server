package Server;

import Core.VelocityRender;

import java.io.FileInputStream;
import java.util.HashMap;

public class DefaultListener implements SessionListener {

	/**
	 * Override that method to provide request actions with server
	 * @param session - Current session instance
	 * @return - Response message
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	public Response process(Session session) throws Exception {

		String folder = session.getServer().getDocumentRoot();
		java.io.File file = new java.io.File(folder + session.getRequest().getPath());

		if (!file.exists()) {
			return new Response(ResponseCode.NOT_FOUND, "Can't find file \"" + file.getName() + "\"");
		}

		FileInputStream stream = new FileInputStream(file);

		byte[] buffer = new byte[
				(int) file.length()
			];

		stream.read(buffer);
		stream.close();

		ContentType contentType = ContentType.findByExtension(getFileExtension(file.getName()));

		for (File f : session.getRequest().getPostFiles()) {
			f.moveTo("files");
		}

		if (getFileExtension(file.getName()).equals(".vm")) {
			buffer = getVmFileContent(session, file.getName(), null);
		}

		// URLConnection.guessContentTypeFromName(file.getName());

		if (contentType != null) {
			return new Response(ResponseCode.OK, contentType, buffer, null);
		} else {
			return new Response(ResponseCode.UNSUPPORTED_MEDIA_TYPE, "");
		}
	}

	/**
	 * Get rendered content of template velocity file
	 * @param session - Current session
	 * @param fileName - Name of velocity file
	 * @return - Byte buffer with rendered template
	 * @throws Exception
	 */
	public static byte[] getVmFileContent(Session session, String fileName, final String stackTrace) throws Exception {
		return new VelocityRender(session.getNullEnvironment()).render(fileName,
			new HashMap<String, Object>() {{
				if (stackTrace != null) {
					put("trace", stackTrace);
				}
				put("request", session.getRequest());
			}}
		).getBytes();
	}

	/**
	 * Get file's extension
	 * @param fileName - File's name
	 * @return - File's extension
	 */
	private String getFileExtension(String fileName) {
		int lastIndexOf;
		if ((lastIndexOf = fileName.lastIndexOf(".")) == -1) {
			return "";
		}
		return fileName.substring(lastIndexOf);
	}
}
