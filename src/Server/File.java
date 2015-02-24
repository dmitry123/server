package Server;

import java.nio.file.Files;
import java.nio.file.Paths;

public class File {

	/**
	 * Construct file class without any errors
	 * @param fileName - Native file name
	 * @param temporaryName - Temporary system file name
	 * @param contentType - Name of received content type
	 */
	public File(Server server, String fileName, String temporaryName, String contentType) {
		this(server, fileName, temporaryName, contentType, null);
	}

	/**
	 * Construct file class with some errors
	 * @param fileName - Native file name
	 * @param temporaryName - Temporary system file name
	 * @param contentType - Name of received content type
	 * @param exception - Exception provided while receiving file
	 */
	public File(Server server, String fileName, String temporaryName, String contentType, Exception exception) {
		this.server = server;
		this.fileName = fileName;
		this.temporaryName = temporaryName;
		this.contentType = contentType;
		this.exception = exception;
	}

	/**
	 * Move just received file to another directory
	 * @param folder - Path to directory
	 */
	public synchronized void moveTo(String folder) throws Exception {
		if (!folder.endsWith(java.io.File.separator)) {
			folder = folder + java.io.File.separator;
		}
		Files.move(Paths.get(getTemporaryName()),
			Paths.get(getServer().getDocumentRoot() + folder + getFileName())
		);
	}

	/**
	 * Get native file's name
	 * @return - Native file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Get temporary system file's name
	 * @return - Temporary file name or null
	 */
	public String getTemporaryName() {
		return temporaryName;
	}

	/**
	 * Get type of received file's content
	 * @return - Native file content
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Get some exception, that has been provided
	 * while creating temporary file
	 * @return - Exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Get file's server instance
	 * @return - Server instance
	 */
	public Server getServer() {
		return server;
	}

	private String fileName;
	private String temporaryName;
	private String contentType;
	private Exception exception;
	private Server server;
}
