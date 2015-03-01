package Core;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader extends AbstractLoader {

	/**
	 * Default constructor with configuration initializer and loader, it
	 * will load configuration file (if last exists) and move it to self
	 * instance (HashMap)
	 * @param fileName - Name of configuration file in configuration folder
	 * @throws Exception - If configuration file hasn't been created
	 */
	public ConfigLoader(String fileName) throws Exception {
		super(fileName);
	}

	/**
	 * Get get for current operating system, you can simply
	 * declare field as object with keys associated with
	 * operating system name
	 * @param key - Configuration key to find
	 * @return - Object associated with that key or null
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSystemSpecific(String key) {
		return getSystemSpecific(key, null);
	}

	/**
	 * Get get for current operating system, you can simply
	 * declare field as object with keys associated with
	 * operating system name, where:
	 *  + windows - Microsoft Windows
	 *  + linux - Other
	 * @param key - Configuration key to find
 	 * @param defaultValue - Default value if last is null
	 * @return - Object associated with that key or null
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSystemSpecific(String key, T defaultValue) {
		Object object = getOrDefault(key, defaultValue);
		if (object instanceof Map) {
			Map<String, Object> map = ((Map) object);
			if (System.getProperty("os.name").contains("Windows")) {
				return (T) map.getOrDefault("windows", defaultValue);
			} else {
				return (T) map.getOrDefault("linux", defaultValue);
			}
		} else {
			return (T) object;
		}
	}

	/**
	 * Override that method to return folder with files for this loader
	 * @return - Folder's name
	 */
	@Override
	protected String getFolder() {
		return "config";
	}

	/**
	 * Override that method to return type of file's extension that will be automatically added to the end of file's
	 * name
	 * @return - Extension name
	 */
	@Override
	protected String getExtension() {
		return "json";
	}

	/**
	 * Load json data from configuration file and append to self
	 * instance for next actions
	 * @throws Exception - If file's data hasn't been read
	 */
	protected synchronized void load(Map<String, Object> map) throws Exception {

		FileInputStream stream = new FileInputStream(getFile());

		if (getFile().length() < 1) {
			return ;
		}

		byte[] buffer = new byte[
				(int) getFile().length()
			];

		if (stream.read(buffer) == -1) {
			throw new Exception("End of configuration file reached");
		}

		stream.close();

		buildJsonTree(map, new JSONObject(
			new String(buffer)
		));
	}

	/**
	 * Move all nodes from json object to hash map
	 * @param destination - Destination map
	 * @param source - Source node
	 */
	private void buildJsonTree(Map<String, Object> destination, JSONObject source) {
		for (String key : source.keySet()) {
			Object object = source.get(key);
			if (object instanceof JSONObject) {
				Map<String, Object> temp = new HashMap<>();
				buildJsonTree(temp, (JSONObject) object);
				destination.put(key, temp);
			} else {
				destination.put(key, source.get(key));
			}
		}
	}

	/**
	 * Write json data to configuration file and flush it's stream
	 * @throws Exception - File operations exceptions (IOException)
	 */
	protected synchronized void save(Map<String, Object> map) throws Exception {

		FileOutputStream stream = new FileOutputStream(getFile());
		JSONObject node = new JSONObject(map);

		stream.write(node.toString(2).getBytes());
		stream.flush();
		stream.close();
	}
}
