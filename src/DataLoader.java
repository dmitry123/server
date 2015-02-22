import java.io.*;
import java.util.Map;

public class DataLoader extends AbstractLoader implements Serializable {

	/**
	 * Default constructor with configuration initializer and loader, it
	 * will load configuration file (if last exists) and move it to self
	 * instance (HashMap)
	 * @param fileName - Name of configuration file in configuration folder
	 * @throws Exception - If configuration file hasn't been created
	 */
	public DataLoader(String fileName) throws Exception {
		super(fileName);
	}

	/**
	 * Override that method to return folder with files for this loader
	 * @return - Folder's name
	 */
	@Override
	protected String getFolder() {
		return "data";
	}

	/**
	 * Override that method to return type of file's extension that will be automatically added to the end of file's
	 * name
	 * @return - Extension name
	 */
	@Override
	protected String getExtension() {
		return "data";
	}

	/**
	 * Load json data from configuration file and append to self
	 * instance for next actions
	 * @throws Exception - If file's data hasn't been read
	 */
	@SuppressWarnings("unchecked")
	protected synchronized void load(Map<String, Object> map) throws Exception {

		FileInputStream stream = new FileInputStream(getFile());
		ObjectInputStream object = new ObjectInputStream(stream);

		map.putAll(((Map<String, Object>) object.readObject()));
	}

	/**
	 * Write json data to configuration file and flush it's stream
	 * @throws Exception - File operations exceptions (IOException)
	 */
	@SuppressWarnings("unchecked")
	protected synchronized void save(Map<String, Object> map) throws Exception {

		FileOutputStream stream = new FileOutputStream(getFile());
		ObjectOutputStream object = new ObjectOutputStream(stream);

		object.writeObject(map);
		object.flush();
		object.close();
	}
}
