import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractLoader extends HashMap<String, Object> implements Loader {

	/**
	 * Default constructor with configuration initializer and loader, it
	 * will load configuration file (if last exists) and move it to self
	 * instance (HashMap)
	 * @param fileName - Name of configuration file in configuration folder
	 * @throws Exception - If configuration file hasn't been created
	 */
	public AbstractLoader(String fileName) throws Exception {

		String extension;

		if (getExtension().startsWith(".")) {
			extension = getExtension();
		} else {
			extension = "." + getExtension();
		}

		if (!fileName.endsWith(extension)) {
			fileName = fileName + extension;
		}

		File folder = new File(getFolder());

		if (!folder.exists() && !folder.mkdirs()) {
			throw new Exception("Can't create configuration folder");
		}

		file = new File(getFolder() + File.separator + fileName);

		if (file.exists()) {
			load(this);
		} else if (!file.createNewFile()) {
			throw new Exception("Can't create configuration file");
		}
	}

	/**
	 * Priority for configuration synchronization
	 */
	public enum Precedence {
		MEMORY, FILE
	}

	/**
	 * It will return default value if last not contains in this
	 * hash map and put it to itself
	 * @param key - Value's key
	 * @param value - Default value to set
	 * @param <T> - Type of set value
	 * @return - Key's value from map or default value
	 */
	@SuppressWarnings("unchecked")
	public <T> T getDefault(String key, T value) {
		if (containsKey(key)) {
			return ((T) get(key));
		} else {
			put(key, value);
		}
		return value;
	}

	/**
	 * Override that method to return folder with files
	 * for this loader
	 * @return - Folder's name
	 */
	protected abstract String getFolder();

	/**
	 * Override that method to return type of file's
	 * extension that will be automatically added to
	 * the end of file's name
	 * @return - Extension name
	 */
	protected abstract String getExtension();

	/**
	 * Synchronize configuration with memory priority data from memory and
	 * configuration file from filesystem, it will compare it's values and try to merge
	 * @throws Exception
	 */
	@Override
	public void synchronize() throws Exception {
		synchronize(Precedence.MEMORY);
	}

	/**
	 * Synchronize configuration data from memory and
	 * configuration file from filesystem, it will compare
	 * it's values and try to merge
	 * @param precedence - Synchronization priority
	 * @throws Exception
	 */
	public final synchronized void synchronize(Precedence precedence) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

		load(map);

		if (precedence == Precedence.FILE) {
			putAll(map);
			map = this;
		} else {
			map.putAll(this);
		}

		save(map);
	}

	/**
	 * Read map's entry from configuration file
	 * @throws Exception - IO exceptions
	 */
	@Override
	public void load() throws Exception {
		load(this);
	}

	/**
	 * Write configuration to file for current map
	 * @throws Exception - IO exceptions
	 */
	@Override
	public void save() throws Exception {
		save(this);
	}

	/**
	 * Load json data from configuration file and append to self
	 * instance for next actions
	 * @throws Exception - If file's data hasn't been read
	 */
	protected abstract void load(Map<String, Object> map) throws Exception;

	/**
	 * Write json data to configuration file and flush it's stream
	 * @throws Exception - File operations exceptions (IOException)
	 */
	protected abstract void save(Map<String, Object> map) throws Exception;

	/**
	 * Get handle of configuration file
	 * @return - File's handle
	 */
	public File getFile() {
		return file;
	}

	private File file;
}
