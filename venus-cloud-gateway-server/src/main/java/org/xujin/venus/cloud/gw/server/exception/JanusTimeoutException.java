package org.xujin.venus.cloud.gw.server.exception;

/**
 * 
 * @author xujin
 *
 */
public class JanusTimeoutException extends Exception {
	private String message;
	private static final long serialVersionUID = 1900926677490660714L;

	/**
	 * Constructs a <tt>TimeoutException</tt> with no specified detail message.
	 */
	public JanusTimeoutException() {
	}

	/**
	 * Constructs a <tt>TimeoutException</tt> with the specified detail message.
	 *
	 * @param message the detail message
	 */
	public JanusTimeoutException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}