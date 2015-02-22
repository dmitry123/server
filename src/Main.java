import java.io.File;
import java.io.FileInputStream;

public class Main implements RequestListener {

	public static void main(String[] arguments) throws Exception {
		new Server(new Main(), "server").run();
	}

	/**
	 * Override that method to provide request actions with server
	 * @param request - Client's request
	 * @return - Response message
	 */
	@Override
	public Response process(Server server, Request request) throws Exception {

		File file = new File(server.getConfigLoader().getDefault("htdocs", System.getProperty("user.dir"))
			+ request.getPath()
		);

		if (!file.exists()) {
			return new Response(ResponseCode.NOT_FOUND, "Can't find file \"" + file.getPath() + "\"");
		}

		FileInputStream stream = new FileInputStream(file);

		byte[] buffer = new byte[
				(int) file.length()
			];

		stream.read(buffer);
		stream.close();

		return new Response(ResponseCode.OK, ContentType.TEXT_HTML, buffer, null);
	}
}
