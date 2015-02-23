package Server;

import Console.Machine;
import Core.AbstractLoader;
import Core.ConfigLoader;
import Core.EnvironmentManager;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.SocketException;

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
		if (listener == null) {
			sessionListener = new DefaultListener();
		} else {
			sessionListener = listener;
		}
		try {
			serverTerminal = new ServerTerminal(getEnvironmentManager(),
				new Machine()
			);
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
			serverSocket = new ServerSocket(
				configLoader.getDefault("port", PORT)
			);

			System.out.format("Starting Server At %d Port\n", serverSocket.getLocalPort());

			if (serverSocket.isClosed()) {
				throw new Exception("Can't open server socket");
			}

			do {
				try {
					new Thread(new Session(this, serverSocket.accept())).start();
				} catch (InterruptedIOException ignored) {
					break;
				} catch (SocketException ignored) {
					break;
				}
			} while (!serverSocket.isClosed());

			configLoader.synchronize(AbstractLoader.Precedence.FILE);

			System.out.println("Closing Server");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Interrupt server working process and terminate it's
	 * thread
	 */
	@Override
	public void interrupt() {
		try {
			getServerSocket().close();
		} catch (IOException ignored) {
		}
		super.interrupt();
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

	/**
	 * Get server's socket
	 * @return - Server's socket
	 */
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	private SessionListener sessionListener;
	private ConfigLoader configLoader;
	private ServerTerminal serverTerminal;
	private ServerSocket serverSocket;
}
