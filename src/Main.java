import Server.Response;
import Server.ResponseCode;
import Server.Server;
import Server.Session;
import Server.SessionListener;
import Server.ContentType;

import java.io.File;
import java.io.FileInputStream;

public class Main implements SessionListener {

	public static void main(String[] arguments) throws Exception {
		Server server = new Server(new Main(), "server");
		server.start();
		server.getServerTerminal().work();
	}

	/**
	 * Override that method to provide request
	 * actions with server
	 * @param session - Current session instance
	 * @return - Response message
	 */
	@Override
	public Response process(Session session) throws Exception {

		String folder = session.getServer().getConfigLoader().getDefault("root", System.getProperty("user.dir"));
		File file = new File(folder + session.getRequest().getPath());

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
