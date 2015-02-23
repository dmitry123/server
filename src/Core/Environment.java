package Core;

import Sql.Connection;
import java.io.File;
import java.sql.SQLException;

import Server.Server;

/**
 * Created by Savonin on 2014-11-08
 */
public class Environment {

	/**
	 * Construct environment
	 * @param connection - Database connection
	 * @param projectName - Project's folder name
	 * @throws Exception
	 */
	public Environment(Server server, Connection connection, String projectName) throws Exception {

		try {
			if (connection == null || connection.isClosed()) {
				connection = new Connection();
			}
		} catch (SQLException ignored) {
			connection = null;
		}

		if (projectName.endsWith("\\") || projectName.endsWith("/")) {
			projectName = projectName.substring(0, projectName.length() - 1);
		}

		this.server = server;
		this.connection = connection;
		this.projectName = projectName;

		if (!projectName.endsWith(File.separator)) {
			projectName += File.separator;
		}

		this.projectPath = Config.PROJECT_PATH + projectName;
	}

	/**
	 * @return - Collection with all managers
	 */
	public ManagerCollection getManagerCollection() {
		return managerCollection;
	}

	private ManagerCollection managerCollection
		= new ManagerCollection(this);

	/**
	 * @return - Project's model manager
	 */
	public AbstractManager<Model> getModelManager() {
		return modelManager;
	}

	private AbstractManager<Model> modelManager
		= managerCollection.getModelManager();

	/**
	 * @return - Project's form manager
	 */
	public AbstractManager<Form> getFormManager() {
		return formManager;
	}

	private AbstractManager<Form> formManager
		= managerCollection.getFormManager();

	/**
	 * @return - Controller manager
	 */
	public AbstractManager<Controller> getControllerManager() {
		return controllerManager;
	}

	private AbstractManager<Controller> controllerManager
		= managerCollection.getControllerManager();

	/**
	 * @return - View manager
	 */
	public AbstractManager<View> getViewManager() {
		return viewManager;
	}

	private AbstractManager<View> viewManager
		= managerCollection.getViewManager();

	/**
	 * @return - Module manager
	 */
	public AbstractManager<Module> getModuleManager() {
		return moduleManager;
	}

	private AbstractManager<Module> moduleManager
		= managerCollection.getModuleManager();

	/**
	 * @return - Widget manager
	 */
	public AbstractManager<Widget> getWidgetManager() {
		return widgetManager;
	}

	private AbstractManager<Widget> widgetManager
		= managerCollection.getWidgetManager();

	/**
	 * @return - Validator manager
	 */
	public AbstractManager<Validator> getValidatorManager() {
		return validatorManager;
	}

	private AbstractManager<Validator> validatorManager
		= managerCollection.getValidatorManager();

	/**
	 * @return - Project's manager
	 */
	public ProjectManager getProjectManager() {
		return projectManager;
	}

	private ProjectManager projectManager
		= new ProjectManager(this);

	/**
	 * @return - Component's factory
	 */
	public ComponentFactory getComponentFactory() {
		return componentFactory;
	}

	private ComponentFactory componentFactory
		= new ComponentFactory(this);

	/**
	 * @return - Router
	 */
	public Router getRouter() {
		return router;
	}

	private Router router
		= new Router(this);

	/**
	 * @return - User session manager
	 */
	public SessionManager getSessionManager() {
		return sessionManager;
	}

	private SessionManager sessionManager
		= new SessionManager(this);

	/**
	 * @return - Current user's session
	 */
	public Session getSession() {
		return getSessionManager().get();
	}

	/**
	 *
	 * @return - Mustache definer
	 */
	public MustacheDefiner getMustacheDefiner() {
		if (getSession() != null) {
			return getSession().getMustacheDefiner();
		} else {
			return mustacheDefiner;
		}
	}

	private MustacheDefiner mustacheDefiner = new MustacheDefiner();

	/**
	 * @return - Path to project
	 */
	public String getProjectPath() {
		return projectPath;
	}

	/**
	 * Get environment's server
	 * @return - Server instance
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * @return - Project's name
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return - Database connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @return - Session's id
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID - Session's id
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	private Server server;
	private String projectName;
	private String projectPath;
	private Connection connection;
	private String sessionID;
}
