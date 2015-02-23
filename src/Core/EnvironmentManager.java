package Core;

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
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			for (Map.Entry<String, Environment> i : EnvironmentManager.this.getMap().entrySet()) {
				try {
					i.getValue().getSessionManager().save();
				} catch (Exception ignored) {
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
		if (hashMap.containsKey(name)) {
			return hashMap.get(name);
		}
		Environment environment = new Environment(
			server, null, name
		);
		environment.getSessionManager().load();
		hashMap.put(name, environment);
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
}
