package org.xujin.venus.cloud.gw.server.filter.error;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.exception.HttpCodeErrorJanusException;
import org.xujin.venus.cloud.gw.server.exception.NotPrintStackJanusException;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;
import org.xujin.venus.cloud.gw.server.filter.post.JanusResponserFilter;
import org.xujin.venus.cloud.gw.server.http.HttpCode;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 错误统一处理
 * @author xujin
 *
 */
public class ErrorFilter extends AbstractFilter {
	private static Logger logger = LoggerFactory.getLogger(ErrorFilter.class);
	public static String DEFAULT_NAME = PRE_FILTER_NAME
			+ ErrorFilter.class.getSimpleName().toUpperCase();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void run(AbstractFilterContext filterContext, JanusHandleContext janusContext)
			throws Exception {
		Throwable throwable = janusContext.getThrowable();
		if (throwable != null) {
			// 是否打印堆栈信息
			janusContext.setIsPrintExceStackInfo(
					printExceptionStackInfo(throwable, janusContext));
			// 打印错误信息
			logThrowable(throwable, janusContext);
			// 错误信息渲染，设置到body里面。返回状态码和mercury状态码处理
			processThrowable(janusContext, throwable);

		}
		filterContext.skipFilterByFilterName(janusContext,
				JanusResponserFilter.DEFAULT_NAME);

	}

	public static void processThrowable(JanusHandleContext janusContext, Throwable t) {

		// 返回的httpcode，并且apiinfo为空(还不确定wrapper类型)
		if (t instanceof HttpCodeErrorJanusException) {
			if (janusContext.getAPIInfo() == null) {
				janusContext.setResponseHttpCode(
						((HttpCodeErrorJanusException) t).getHttpCode());
				janusContext.setMercuryHttpCode(janusContext.getResponseHttpCode());
				return;
			}
		}

		// 在确定apiInfo之前，不确定的异常都返回500
		if (janusContext.getAPIInfo() == null) {
			janusContext.setResponseHttpCode(HttpCode.HTTP_INTERNAL_SERVER_ERROR);
			janusContext.setMercuryHttpCode(janusContext.getResponseHttpCode());
			return;
		}

		// 一般是http协议校验，白名单校验，参数校验等错误。
		if (t instanceof HttpCodeErrorJanusException) {
			janusContext
					.setResponseHttpCode(((HttpCodeErrorJanusException) t).getHttpCode());
			janusContext.setMercuryHttpCode(janusContext.getResponseHttpCode());
		}

		/**
		 * if (janusContext.getAPIInfo().getWrapper() == APIInfo.WRAPPER_STANDARD) { //
		 * 返回的http code均为200 janusContext.setResponseHttpCode(HttpCode.HTTP_OK_CODE); if
		 * ((t instanceof JanusException)) { JanusException exc = (JanusException) t; //
		 * 如果是访问osp的错误,则返回的错误码为osp返回的error code errorResultProcess(exc.getErrorCode(),
		 * exc.getMessage(), janusContext); } else {
		 * errorResultProcess(Integer.valueOf(HttpCode.HTTP_INTERNAL_SERVER_ERROR).toString(),
		 * t.getMessage(), janusContext); }
		 * 
		 * } else { // 透传 // 如果没有设置过httpcode，则返回500 if (janusContext.getResponseHttpCode()
		 * == HttpCode.HTTP_OK_CODE) {
		 * janusContext.setResponseHttpCode(HttpCode.HTTP_INTERNAL_SERVER_ERROR); }
		 * 
		 * }
		 **/
	}

	private static void errorResultProcess(String errorCode, String msg,
			JanusHandleContext requestContext) {
		JSONObject responseBody = new JSONObject();
		responseBody.put("code", errorCode);
		responseBody.put("msg", msg);
		ByteBuf byteBuf = null;
		try {
			byteBuf = Unpooled.wrappedBuffer(responseBody.toString().getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException err) {
			throw new RuntimeException(err);
		}
		requestContext.setResponseBody(byteBuf);
	}

	private static void logThrowable(Throwable t, JanusHandleContext janusContext) {

		if (t instanceof NotPrintStackJanusException) {
			// 不需要打印日志
			return;
		}
		/**
		 * if (t instanceof MapiAuthException) { logger.warn(t.getMessage()); return; }
		 **/

		logger.error(t.getMessage(), t);

	}

	/**
	 * 判断是否需要打印堆栈信息
	 * 
	 * @param t
	 * @param janusContext
	 * @return
	 */
	private static Boolean printExceptionStackInfo(Throwable t,
			JanusHandleContext janusContext) {
		if (t instanceof NotPrintStackJanusException) {
			return false;
		}
		return true;
	}

}
