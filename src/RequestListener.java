
public interface RequestListener {

	/**
	 * Override that method to provide request
	 * actions with server
	 * @param request - Client's request
	 * @return - Response message
	 */
	public Response process(Request request) throws Exception;
}
