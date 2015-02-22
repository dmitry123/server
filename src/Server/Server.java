package Server;

import Core.AbstractLoader;
import Core.ConfigLoader;
import Core.EnvironmentManager;

import java.net.ServerSocket;

public class Server extends Thread implements Runnable {

	public static final int PORT = 9999;

	/**
	 * Construct server
	 */
	public Server(SessionListener listener, String config) {
		try {
			configLoader = new ConfigLoader(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		sessionListener = listener;
		try {
			serverTerminal = new ServerTerminal(getEnvironmentManager(), null);
		} catch (Exception ignored) {
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

			System.out.format("Starting Server At %d Port", serverSocket.getLocalPort());

			do {
				new Thread(new Session(this, serverSocket.accept())).start();
			} while (!serverSocket.isClosed());

			configLoader.synchronize(AbstractLoader.Precedence.FILE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get server's environment manager
	 * @return - Server's environment manager
	 */
	public EnvironmentManager getEnvironmentManager() {
		return environmentManager;
	}

	private EnvironmentManager environmentManager
		= new EnvironmentManager(this);

	/**
	 * Get server's request listener
	 * @return - Request listener
	 */
	public SessionListener getSessionListener() {
		return sessionListener;
	}

	/**
	 * Get server's configuration loader
	 * @return - Loader for server's configuration
	 */
	public ConfigLoader getConfigLoader() {
		return configLoader;
	}

	/**
	 * Get default server's terminal manager
	 * @return - Server's terminal
	 */
	public ServerTerminal getServerTerminal() {
		return serverTerminal;
	}

	private SessionListener sessionListener;
	private ConfigLoader configLoader;
	private ServerTerminal serverTerminal;
}
