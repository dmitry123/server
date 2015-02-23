package Server;

import Core.EnvironmentManager;
import Core.VelocityRender;

import java.io.File;
import java.io.FileInputStream;

public class DefaultListener implements SessionListener {

	/**
	 * Override that method to provide request actions with server
	 * @param session - Current session instance
	 * @return - Response message
	 */
	@Override
	public Response process(Session session) throws Exception {

		String folder = session.getServer().getConfigLoader().getDefault("root", System.getProperty("user.dir"));
		File file = new File(folder + session.getRequest().getPath());

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

		if (getFileExtension(file.getName()).equals(".vm")) {
			buffer = new VelocityRender(session.getNullEnvironment()).render(file.getName()).getBytes();
		}

		if (contentType != null) {
			return new Response(ResponseCode.OK, contentType, buffer, null);
		} else {
			return new Response(ResponseCode.UNSUPPORTED_MEDIA_TYPE, "");
		}
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
