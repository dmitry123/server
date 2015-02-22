
public interface Loader {

	/**
	 * Override that method to run file synchronization
	 * process between memory and filesystem
	 * @throws Exception - IO exceptions
	 */
	public void synchronize() throws Exception;

	/**
	 * Override that method to provide file loading
	 * process from filesystem to memory
	 * @throws Exception - IO exceptions
	 */
	public void load() throws Exception;

	/**
	 * Override that method to provide file saving
	 * process from memory to filesystem
	 * @throws Exception - IO exceptions
	 */
	public void save() throws Exception;
}
