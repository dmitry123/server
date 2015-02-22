
public interface RequestListener {

	/**
	 * Override that method to provide request
	 * actions with server
	 * @param server - Server instance
	 * @param request - Client's request
	 * @return - Response message
	 */
	public Response process(Server server, Request request) throws Exception;
}
