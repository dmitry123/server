import java.net.ServerSocket;

public class Server implements Runnable {

	public static final int PORT = 9999;
	public static final int CHUNK = 4096;

	/**
	 * Construct server
	 */
	public Server(RequestListener listener, String config) {
		try {
			configLoader = new ConfigLoader(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		requestListener = listener;
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
			ServerSocket serverSocket = new ServerSocket(
				configLoader.getDefault("port", PORT)
			);

			do {
				new Thread(new Session(this, serverSocket.accept())).start();
			} while (!serverSocket.isClosed());

			configLoader.synchronize(AbstractLoader.Precedence.FILE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get server's request listener
	 * @return - Request listener
	 */
	public RequestListener getRequestListener() {
		return requestListener;
	}

	/**
	 * Get server's configuration loader
	 * @return - Loader for server's configuration
	 */
	public ConfigLoader getConfigLoader() {
		return configLoader;
	}

	private RequestListener requestListener;
	private ConfigLoader configLoader;
}
