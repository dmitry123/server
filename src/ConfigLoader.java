import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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
		return "config";
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

		JSONObject node = new JSONObject(new String(buffer));

		for (String key : node.keySet()) {
			map.put(key, node.get(key));
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
