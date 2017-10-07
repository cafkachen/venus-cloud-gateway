package org.xujin.venus.cloud.gw.server.exception;

/**
 * 
 * @author xujin
 *
 */
@SuppressWarnings("serial")
public class NotPrintStackJanusException extends JanusException {

	public NotPrintStackJanusException(String message, String errorCode,String className,String method) {
		super(message, errorCode);
		setStackTrace(new StackTraceElement[] { new StackTraceElement(className, method, null, -1)});
	}

}
