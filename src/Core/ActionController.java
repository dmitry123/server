package Core;

public abstract class ActionController extends Controller {

	/**
	 * @param environment - Every core's extension must have environment
	 * with predeclared extensions
	 */
	public ActionController(Environment environment) {
		super(environment);
	}

	/**
	 * Override that method to catch controller's actions
	 * @param action - Caught action's name
	 * @throws Exception
	 */
	public abstract void catchAction(String action) throws Exception;
}
