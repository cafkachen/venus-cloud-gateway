package org.xujin.venus.cloud.gw.admin.base;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.xujin.venus.cloud.gw.admin.utils.FastjsonFilterUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Controller基类
 *
 * @author xujin
 */
public class BaseController {

	/** Log类 */
	private Logger logger = Logger.getLogger(getClass());

	/**
	 * 将对象转换成JSON字符串，并响应回前台
	 *
	 * @param response HttpServletResponse
	 * @param obj 需要转化为JSON的对象
	 * @throws IOException 发生异常
	 */
	protected void writeJson(HttpServletResponse response, Object obj) {
		this.writeJsonByFilter(response, obj, null, null);
	}

	/**
	 * 将对象转换成JSON字符串，并响应回前台
	 *
	 * @param response HttpServletResponse
	 * @param object 需要转化为JSON的对象
	 * @param includesProperties 需要转换的属性
	 * @param excludesProperties 不需要转换的属性
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
			// 判断如果为JSONP格式，则需要将转化为JSONP格式的字符串
			/*
			 * if (!StringUtils.isEmpty(model.getCallback())) { json = model.getCallback()
			 * + "(" + json + ");"; }
			 */

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
