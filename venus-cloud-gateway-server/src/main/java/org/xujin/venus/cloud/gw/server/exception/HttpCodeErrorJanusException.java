package org.xujin.venus.cloud.gw.server.exception;

/**
 * 需要直接返回对应的httpcode
 * @author xujin
 *
 */
@SuppressWarnings("serial")
public class HttpCodeErrorJanusException extends NotPrintStackJanusException {
	private int httpCode;

	//通过设置className和method,可以减少堆栈信息的输出，提升性能
	public HttpCodeErrorJanusException(String message, int httpCode,String className,String method) {
		super(message, Integer.valueOf(httpCode).toString(), className, method);
		this.httpCode = httpCode;
	}
	
	public int getHttpCode() {
		return httpCode;
	}

	
}
