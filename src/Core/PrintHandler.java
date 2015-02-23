package Core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Stack;

public class PrintHandler {

	public static interface Lambda {

		/**
		 * Override that method to provide
		 * some actions with output stream
		 */
		public void call();
	}

	/**
	 * Push current output stream to stack and
	 * set string print stream as current
	 * default. It will catch all output strings
	 * and store in byteArray buffer. You can
	 * buffer via pop method
	 * @see PrintHandler#pop
	 */
	public static synchronized void push() {
		Node node = new Node();
		node.byteArrayOutputStream = byteArrayOutputStream;
		node.printStream = System.out;
		stack.push(node);
		System.setOut(new PrintStream(byteArrayOutputStream));
	}

	/**
	 * Restore previous output stream writer and
	 * return all caught output strings
	 * @return - String with caught output data
	 */
	public static synchronized String pop() {
		if (stack.size() == 0) {
			return null;
		}
		Node node = stack.pop();
		System.setOut(node.printStream);
		String buffer = byteArrayOutputStream.toString();
		byteArrayOutputStream.reset();
		byteArrayOutputStream = node.byteArrayOutputStream;
		return buffer;
	}

	/**
	 * Lock current output stream buffer to
	 * provide lambda action. It will restore
	 * previous printer
	 * @param lambda - Lambda expression
	 * @return - Caught buffer
	 */
	public static synchronized String handle(Lambda lambda) {
		push();
		lambda.call();
		return pop();
	}

	/**
	 * Reset current byte array buffer with
	 * output stream stuff
	 */
	public static synchronized void reset() {
		byteArrayOutputStream.reset();
	}

	private static class Node {
		public ByteArrayOutputStream byteArrayOutputStream;
		public PrintStream printStream;
	}

	static private ByteArrayOutputStream byteArrayOutputStream
		= new ByteArrayOutputStream();

	static private Stack<Node> stack
		= new Stack<Node>();
}
