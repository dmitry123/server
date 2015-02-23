package Core;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class VelocityRender extends Extension {

	/**
	 * @param environment - Every core's extension must have
	 * environment with predeclared extensions
	 */
	public VelocityRender(Environment environment) {
		super(environment);
	}

	/**
	 * Render velocity file
	 * @param fileName - Name of velocity file
	 * @return - Rendered content
	 * @throws Exception
	 */
	public String render(String fileName) throws Exception {
		return render(fileName, new HashMap<>());
	}

	/**
	 * Render velocity file
	 * @param fileName - Name of velocity file
	 * @param hashData - Data to render
	 * @return - Rendered content
	 * @throws Exception
	 */
	public String render(String fileName, Map<String, Object> hashData) throws Exception {

		VelocityEngine engine = new VelocityEngine() {{
			setProperty("file.resource.loader.path",
				getEnvironment().getServer().getConfigLoader().get("root")
			);
			init();
		}};

		VelocityContext context = new VelocityContext();
		StringWriter writer = new StringWriter();

		for (Map.Entry<String, Object> entry : hashData.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}

		engine.getTemplate(fileName, "UTF-8").merge(context, writer);

		return writer.toString();
	}
}
