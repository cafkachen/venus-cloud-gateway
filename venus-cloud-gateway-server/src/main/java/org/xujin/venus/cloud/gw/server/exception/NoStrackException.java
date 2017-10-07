package org.xujin.venus.cloud.gw.server.exception;

/**
 * @author xujin
 * 不需要打印堆栈信息，提升性能
 *
 */
public class NoStrackException extends JanusException{

	public NoStrackException(String message, String errorCode,String className,String method) {
		super(message, errorCode);
		setStackTrace(new StackTraceElement[] { new StackTraceElement(className, method, null, -1)});
	}

}
