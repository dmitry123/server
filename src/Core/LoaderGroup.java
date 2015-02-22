package Core;

import java.util.HashSet;

public class LoaderGroup extends HashSet<Loader> implements Loader {

	/**
	 * Override that method to synchronize memory and filesystem stuff
	 * @throws Exception - IO exceptions
	 */
	@Override
	public void synchronize() throws Exception {
		for (Loader loader : this) {
			loader.synchronize();
		}
	}

	/**
	 * Override that method to save configuration stuff to filesystem
	 * @throws Exception - IO exceptions
	 */
	@Override
	public void save() throws Exception {
		for (Loader loader : this) {
			loader.save();
		}
	}

	/**
	 * Override that method to load configuration stuff from filesystem
	 * @throws Exception - IO exceptions
	 */
	@Override
	public void load() throws Exception {
		for (Loader loader : this) {
			loader.load();
		}
	}
}
