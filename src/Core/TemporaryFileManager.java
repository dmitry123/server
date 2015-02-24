package Core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TemporaryFileManager implements FileManager {

	/**
	 * Construct temporary file manager
	 */
	public TemporaryFileManager() {
		managers.add(this);
	}

	/**
	 * Get path to temporary directory
	 * @return - Path to temporary folder
	 */
	@Override
	public String getDirectory() {
		return System.getProperty("java.io.tmpdir");
	}

	/**
	 * Create temporary file in temporary directory folder
	 * @return - Handle of just created file
	 * @throws Exception
	 */
	public synchronized File create() throws Exception {
		File file = File.createTempFile("Jaw", "", new File(getDirectory()));
		files.add(file);
		return file;
	}

	/**
	 * Cleanup temporary file manager. It will remove all created
	 * temporary files after program exit
	 */
	public void cleanup() {
		getFiles().forEach(File::delete);
	}

	/**
	 * Get list with temporary files
	 * @return - List with files
	 */
	public List<File> getFiles() {
		return files;
	}

	/**
	 * Get default temporary file manager
	 * @return - Temporary file manager
	 */
	public static TemporaryFileManager getDefaultManager() {
		return defaultManager;
	}

	static private Set<TemporaryFileManager> managers
			= new HashSet<>();

	static private TemporaryFileManager defaultManager
			= new TemporaryFileManager();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			managers.forEach(Core.TemporaryFileManager::cleanup);
		}));
	}

	private List<File> files
		= new ArrayList<>();
}
