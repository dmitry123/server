package Core;

import Sql.Connection;
import Server.Server;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitry on 03.12.14
 */
public class EnvironmentManager {

	/**
	 * Locked environment manager constructor, only
	 * singleton instance
	 */
	public EnvironmentManager(Server server) {
		// Add shutdown hook to save all active sessions
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				for (Map.Entry<String, Environment> i : EnvironmentManager.this.getMap().entrySet()) {
					try {
						i.getValue().getSessionManager().save();
					} catch (Exception ignored) {
					}
				}
			}
		}));
		this.server = server;
	}

	/**
	 * Get environment by it's name
	 * @param name - Name of environment
	 * @return - Environment instance
	 * @throws Exception
	 */
	public Environment get(String name) throws Exception {
		// Find cashed environment
		if (hashMap.containsKey(name)) {
			return hashMap.get(name);
		}
		// Create new environment
		Environment environment = new Environment(
			server, null, name
		);
		// Restore all saved sessions
		environment.getSessionManager().load();
		// Put to hash map
		hashMap.put(name, environment);
		// Return instance
		return environment;
	}

	/**
	 * Get map with declared environments
	 * @return - Map with environments
	 */
	public Map<String, Environment> getMap() {
		return hashMap;
	}

	private HashMap<String, Environment> hashMap
			= new HashMap<String, Environment>();

	private Server server;

	/**
	 * Get environment manager singleton instance
	 * @return - Environment manager instance
	 */
//	public static EnvironmentManager getInstance() {
//		return environmentManager;
//	}
//
//	private static EnvironmentManager environmentManager
//			= new EnvironmentManager();
}
