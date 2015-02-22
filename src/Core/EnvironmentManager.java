package Core;

import Sql.Connection;
import Server.Server;

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
				for (Map.Entry<String, Environment> i : EnvironmentManager.this.getHashMap().entrySet()) {
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
		Environment e = new Environment(server, new Connection(), name);
		// Restore all saved sessions
		e.getSessionManager().load();
		// Put to hash map
		hashMap.put(name, e);
		// Return instance
		return e;
	}

	/**
	 * Get map with declared environments
	 * @return - Map with environments
	 */
	public HashMap<String, Environment> getHashMap() {
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
