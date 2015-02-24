import Server.*;

public class Main implements SessionListener {

	/**
	 * Application entry point
	 * @param arguments - Argument list
	 * @throws Exception
	 */
	@SuppressWarnings({"unchecked", "ConstantConditions"})
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
		return new Response(ResponseCode.OK, "<h1>Not-Implemented</h1>");
	}
}
