package Server;

public interface SessionListener {

	/**
	 * Override that method to provide request
	 * actions with server
	 * @param session - Current session instance
	 * @return - Response message
	 */
	public Response process(Session session) throws Exception;
}
