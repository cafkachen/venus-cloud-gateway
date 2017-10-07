package org.xujin.venus.cloud.gw.server.exception;

/**
 * Janus Server自定义异常
 * @author xujin
 *
 */
@SuppressWarnings("serial")
public class JanusException extends RuntimeException {
	public String errorCode;
	public String errorCause;
	private String message;

	public JanusException(String errorCode) {
		this.errorCode = errorCode;
	}

	public JanusException(String errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

	public JanusException(String message, String errorCode) {
		this.message = message;
		this.errorCode = errorCode;
	}

	public JanusException(String message, String errorCode, Throwable cause) {
		super(message, cause);
		this.message = message;
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
