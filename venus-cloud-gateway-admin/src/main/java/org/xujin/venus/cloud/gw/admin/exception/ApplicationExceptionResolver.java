package org.xujin.venus.cloud.gw.admin.exception;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.xujin.venus.cloud.gw.admin.base.ResultData;
import org.xujin.venus.cloud.gw.admin.constant.Config;
import org.xujin.venus.cloud.gw.admin.utils.FastjsonFilterUtil;
import org.xujin.venus.cloud.gw.admin.utils.ResourcesUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author xujin
 * @Description: 全局异常处理器
 */
public class ApplicationExceptionResolver implements HandlerExceptionResolver {
	/**
	 * Log类
	 */
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		logger.info("异常拦截器执行开始。");
		// 输出 异常信息
		String msgContent = ResourcesUtil.getValue(Config.MESSAGE, ex.getMessage());
		logger.error("发生异常:" + msgContent, ex);
		// 将异常信息转json输出
		this.writeJsonByFilter(response, this.resolveExceptionCustom(ex), null, null);
		logger.info("异常拦截器执行结束。");
		return new ModelAndView();
	}

	/**
	 * 异常信息解析方法
	 *
	 * @param ex
	 */
	private ResultData resolveExceptionCustom(Exception ex) {
		ResultData model = new ResultData();
		if (ex instanceof ApplicationException) {
			// 抛出的是系统自定义异常
			model.setMsgCode(this.getMsgCode(ex));
		}
		else if (ex instanceof UnauthenticatedException) {
			// 没有权限的异常
			model.setMsgCode("ECOMMON00002");
		}
		else {
			// 未知错误
			model.setMsgCode("ECOMMON00001");
		}
		return model;
	}

	private String getMsgCode(Exception ex) {
		// 输出 异常信息
		String msgCode = ex.getMessage();
		// 若返回的异常不直接是自定义异常，而是经过封装的异常
		if (msgCode.charAt(0) != 'E' && msgCode.charAt(0) != 'W'
				&& msgCode.charAt(0) != 'I') {
			msgCode = ex.getCause().getMessage();
		}
		return msgCode;
	}

	/**
	 * 将对象转换成JSON字符串，并响应回前台
	 */
	protected void writeJsonByFilter(HttpServletResponse response, Object object,
			String[] includesProperties, String[] excludesProperties) {
		try {
			// excludes优先于includes
			FastjsonFilterUtil filter = new FastjsonFilterUtil();
			if (excludesProperties != null && excludesProperties.length > 0) {
				filter.getExcludes().addAll(Arrays.<String> asList(excludesProperties));
			}
			if (includesProperties != null && includesProperties.length > 0) {
				filter.getIncludes().addAll(Arrays.<String> asList(includesProperties));
			}
			// 使用SerializerFeature.WriteDateUseDateFormat特性来序列化日期格式的类型为yyyy-MM-dd
			// hh24:mi:ss
			// 使用SerializerFeature.DisableCircularReferenceDetect特性关闭引用检测和生成
			String json = JSON.toJSONString(object, filter,
					SerializerFeature.WriteDateUseDateFormat,
					SerializerFeature.DisableCircularReferenceDetect);

			logger.info("JSON String is:" + json);
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(json);
			response.getWriter().flush();
			response.getWriter().close();
		}
		catch (IOException e) {
			logger.error("An error occurred when object was converted to JSON", e);
		}
	}

}
