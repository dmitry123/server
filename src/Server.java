import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

	public static final int PORT = 9999;
	public static final int CHUNK = 4096;

	/**
	 * Construct server
	 */
	public Server() {
		try {
			configLoader = new ConfigLoader("server");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			Socket client;

			while ((client = serverSocket.accept()) != null) {
				new Thread(new Session(this, client)).start();
			}

			configLoader.synchronize();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get server's configuration loader
	 * @return - Loader for server's configuration
	 */
	public ConfigLoader getConfigLoader() {
		return configLoader;
	}

	private ConfigLoader configLoader;
}
