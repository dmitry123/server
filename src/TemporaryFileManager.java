import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TemporaryFileManager implements FileManager {

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
		File file = File.createTempFile("Temporary", "File", new File(getDirectory()));
		files.add(file);
		return file;
	}

	/**
	 * Cleanup temporary file manager. It will remove all created
	 * temporary files after program exit
	 */
	public void cleanup() {
		for (File file : getFiles()) {
			file.delete();
		}
	}

	/**
	 * Get list with temporary files
	 * @return - List with files
	 */
	public List<File> getFiles() {
		return files;
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				for (TemporaryFileManager manager : managers) {
					manager.cleanup();
				}
			}
		}));
	}

	static private Set<TemporaryFileManager> managers = new HashSet<TemporaryFileManager>();

	private List<File> files
		= new ArrayList<File>();
}
