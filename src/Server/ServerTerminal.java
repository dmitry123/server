package Server;

import Console.Instruction;
import Console.Machine;
import Console.Station;
import Core.Environment;
import Core.EnvironmentManager;
import Core.Logger;

import java.util.Map;
import java.util.Vector;

/**
 * Created by Savonin on 2014-11-15
 */
public class ServerTerminal extends Station {

	/**
	 * Construct terminal without parent
	 * @param machine - Station's machine
	 * @throws Exception
	 */
	public ServerTerminal(EnvironmentManager environmentManager, Machine machine) throws Exception {
		this(machine, null); this.environmentManager = environmentManager;
	}

	/**
	 * Construct controller with instructions
	 */
	public ServerTerminal(final Machine machine, Station parent) throws Exception {

		super(machine, parent);

		register(new Instruction(this, "kill", "-k") {
			@Override
			public void run(String[] arguments) throws Error, InterruptedException {
				getMachine().getStack().clear(); throw new InterruptedException();
			}
			@Override
			public String getDescription() {
				return "That instruction will cleanup station machine" +
					"stack and terminate it's thread";
			}
		});

		register(new Instruction(this, "log", "-l") {
			@Override
			public void run(String[] arguments) throws Error, InterruptedException {
				Vector<String> stringVector;
				if (arguments.length != 0) {
					stringVector = Logger.loadLog(arguments[0]);
				} else {
					stringVector = Logger.loadLog();
				}
				for (String s : stringVector) {
					System.out.format(" + %s", s);
				}
			}
			@Override
			public String getDescription() {
				return "Display log messages for some day, if day hasn't been set, " +
					"then it will display log for current day";
			}
		});

		register(new Instruction(this, "compile", "-c") {
			@Override
			public void run(String[] arguments) throws Exception {
				if (arguments.length != 1) {
					throw new Console.Error(this, "That instruction can assume only one argument");
				}
				Environment e = getEnvironmentManager().get(arguments[0]);
				if (e == null) {
					throw new Console.Error(this, "Unresolved project name (" + arguments[0] + ")");
				}
				e.getManagerCollection().cleanup();
				e.getProjectManager().getCompiler().compile();
			}
			@Override
			public String getDescription() {
				return "Compile project by it's name, also it will cleanup cache for" +
					"project's environment";
			}
		});

		register(new Instruction(this, "cleanup", "-r") {
			@Override
			public void run(String[] arguments) throws Exception {
				if (arguments.length != 1) {
					throw new Console.Error(this, "That instruction can assume only one argument");
				}
				Environment e = getEnvironmentManager().get(arguments[0]);
				if (e == null) {
					throw new Console.Error(this, "Unresolved project name (" + arguments[0] + ")");
				}
				e.getManagerCollection().cleanup();
			}
			@Override
			public String getDescription() {
				return "Remove all cached components for current project's environment";
			}
		});

		register(new Station(machine, this) {
			{
				register(new Instruction(this, "show", "-s") {
					@Override
					public void run(String[] arguments) throws Exception {
						if (arguments.length != 1) {
							throw new Console.Error(this, "That instruction can assume only one argument");
						}
						Environment e = getEnvironmentManager().get(arguments[0]);
						for (Map.Entry<String, Core.Session> i : e.getSessionManager().getEmployeeHashMap().entrySet()) {
							System.out.format(" + [%s] -> (%s)\n", i.getKey(), i.getValue().getLogin());
						}
						if (e.getSessionManager().getEmployeeHashMap().size() == 0) {
							System.out.println(" + No sessions open");
						}
					}
					@Override
					public String getDescription() {
						return "Display opened sessions for some environment";
					}
				});

				register(new Instruction(this, "clear", "-c") {
					@Override
					public void run(String[] arguments) throws Exception {
						if (arguments.length != 1) {
							throw new Console.Error(this, "That instruction can assume only one argument");
						}
						getEnvironmentManager().get(arguments[0])
							.getSessionManager().getEmployeeHashMap().clear();
					}
					@Override
					public String getDescription() {
						return "Close sessions for some environment";
					}
				});

				register(new Instruction(this, "drop", "-d") {
					@Override
					public void run(String[] arguments) throws Exception {
						if (arguments.length != 2) {
							throw new Console.Error(this, "That instruction can assume only two arguments");
						}
						Map<String, Core.Session> stringUserMap = getEnvironmentManager().get(arguments[0])
							.getSessionManager().getEmployeeHashMap();
						for (Map.Entry<String, Core.Session> i : stringUserMap.entrySet()) {
							if (i.getValue().getLogin().equals(arguments[1])) {
								stringUserMap.remove(i.getKey());
								System.out.println(" + Session dropped [" + i.getKey() + "]");
							}
						}
					}
					@Override
					public String getDescription() {
						return "Drop session from some environment by user's login";
					}
				});

				register(new Instruction(this, "save", "-sf") {
					@Override
					public void run(String[] arguments) throws Exception {
						if (arguments.length != 1) {
							throw new Console.Error(this, "That instruction can assume only two arguments");
						}
						getEnvironmentManager().get(arguments[0]).getSessionManager().save();
					}
					@Override
					public String getDescription() {
						return "Save session for some environment";
					}
				});

				register(new Instruction(this, "load", "-lf") {
					@Override
					public void run(String[] arguments) throws Exception {
						if (arguments.length != 1) {
							throw new Console.Error(this, "That instruction can assume only two arguments");
						}
						getEnvironmentManager().get(arguments[0]).getSessionManager().load();
					}
					@Override
					public String getDescription() {
						return "Load session from filesystem for some environment";
					}
				});
			}

			/**
			 * @return - Instruction description
			 */
			@Override
			public String getDescription() {
				return "That station will provide some operations with sessions, likes " +
					"saving, erasing, loading etc";
			}

			@Override
			public String getName() {
				return "session";
			}

			@Override
			public String getTag() {
				return "-s";
			}
		});
	}

	/**
	 * Every instruction must have it's owner (station)
	 * @return - Instruction's owner
	 */
	@Override
	public Station getStation() {
		if (getParent() != null) {
			return getParent();
		}
		return this;
	}

	/**
	 * Every instruction must have name
	 * @return - Instruction or station name
	 */
	@Override
	public String getName() {
		return "jaw";
	}

	/**
	 * Short name for every station's element
	 * @return - Instruction short tag
	 */
	@Override
	public String getTag() {
		return "-j";
	}

	/**
	 * Get environment manager for current server
	 * @return - Environment manager instance
	 */
	public EnvironmentManager getEnvironmentManager() {
		return environmentManager;
	}

	private EnvironmentManager environmentManager;
}
