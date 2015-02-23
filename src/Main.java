import Server.*;
import java.io.*;

public class Main implements SessionListener {

	public static void main(String[] arguments) throws Exception {
		Server server = new Server(null, "server");
		server.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException ignored) {
		}
		server.getServerTerminal().getMachine().register(
			"terminal", server.getServerTerminal()
		);
		server.getServerTerminal().getMachine().start();
		server.interrupt();
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
