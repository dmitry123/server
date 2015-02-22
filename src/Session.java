import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Session implements Runnable {

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
		try {
			InputStream stream = socket.getInputStream();

			byte[] chunk = new byte[
					getServer().getConfigLoader().getDefault("chunk", Server.CHUNK)
				];
			int bytes;

			StringBuilder builder = new StringBuilder();

			while ((bytes = stream.read(chunk)) == chunk.length) {
				builder.append(new String(chunk, 0, bytes));
			}

			if (bytes > 0) {
				builder.append(new String(chunk, 0, bytes));
			}

			String[] header = builder.toString().split("\r\n");

			if (header.length == 0) {
				return ;
			}

			request = new Request(header);

		} catch (IOException e) {
			e.printStackTrace();
		}
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
