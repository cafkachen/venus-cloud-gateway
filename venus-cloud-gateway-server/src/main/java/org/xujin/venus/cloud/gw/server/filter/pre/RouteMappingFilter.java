package org.xujin.venus.cloud.gw.server.filter.pre;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.config.vo.PublishType;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.exception.HttpCodeErrorJanusException;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;
import org.xujin.venus.cloud.gw.server.filter.base.EventAbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.model.RouteInfo;
import org.xujin.venus.cloud.gw.server.filter.model.UrlParamsInfo;
import org.xujin.venus.cloud.gw.server.http.HttpCode;
import org.xujin.venus.cloud.gw.server.netty.http.JanusRequest;

/**
 * URL路由信息和下发URL匹配 Filter
 * @author xujin
 */
public class RouteMappingFilter extends EventAbstractFilter<RouteInfo> {

	private static final Logger logger = LoggerFactory
			.getLogger(RouteMappingFilter.class);
	public static String DEFAULT_NAME = PRE_FILTER_NAME
			+ RouteMappingFilter.class.getSimpleName().toUpperCase();

	public static String className = RouteMappingFilter.class.getSimpleName();

	public static String classMethod = "run";

	// key为url+method,用于快速匹配请求进来的URL
	private ConcurrentMap<String, Object> urlMethodMap = new ConcurrentHashMap<String, Object>();

	// key为唯一主键，用于查看添加的routeInfo对象
	private ConcurrentMap<Long, RouteInfo> innerIdMap = new ConcurrentHashMap<Long, RouteInfo>();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void run(AbstractFilterContext filterContext,
			JanusHandleContext sessionContext) {
		JanusRequest request = sessionContext.getRequest();
		RouteInfo routeInfo = matchApiRoute(request);
		if (routeInfo == null) {
			throw new HttpCodeErrorJanusException(
					"Service not found:" + combinationUrlMethod(request.getPathInfo(),
							request.getMethod()),
					HttpCode.HTTP_NOT_FOUND, className, classMethod);
		}
		sessionContext.setAPIInfo(routeInfo);
		filterContext.skipNextFilter(sessionContext);
	}

	/**
	 * 路由匹配
	 *
	 * @param request
	 * @return
	 */
	public RouteInfo matchApiRoute(JanusRequest request) {
		String url = request.getPathInfo();
		if (url.length() > 1) {
			if (url.endsWith("/")) {
				url = url.substring(0, url.length() - 1);
			}
		}
		Object object = urlMethodMap.get(combinationUrlMethod(url, request.getMethod()));
		if (object instanceof RouteInfo) {
			return (RouteInfo) object;
		}
		return null;
	}

	@Override
	public PublishType getPublishType() {
		return PublishType.APIS;
	}

	@SuppressWarnings("static-access")
	@Override
	public void add(Long resourceId, RouteInfo value) {
		// 参数校验
		if (!validateExtConfig(value)) {
			return;
		}
		String uniqeKey = combinationUrlMethod(value.getRequestUrl(),
				value.getRequestMethod().toString());
		urlMethodMap.put(uniqeKey, value);
		innerIdMap.put(resourceId, value);
	}

	@Override
	public void delete(Long resourceId, RouteInfo value) {

		RouteInfo innerIdOldInfo = innerIdMap.get(resourceId);
		if (innerIdOldInfo == null) {
			throw new IllegalArgumentException(resourceId + " key not exists ");
		}
		String urlKey = getUrlKey(innerIdOldInfo);
		Object object = urlMethodMap.get(urlKey);
		if (object == null) {
			throw new IllegalArgumentException(resourceId + " key not exists ");
		}
		else if (object instanceof RouteInfo) {
			urlMethodMap.remove(urlKey);
		}
		innerIdMap.remove(resourceId);
	}

	private String getUrlKey(RouteInfo routeInfo) {
		return combinationUrlMethod(routeInfo.getRequestUrl(),
				routeInfo.getRequestMethod());
	}

	@SuppressWarnings("static-access")
	@Override
	public void update(Long resourceId, RouteInfo value) {
		// 参数校验
		if (!validateExtConfig(value)) {
			return;
		}
		RouteInfo oldInfo = innerIdMap.get(resourceId);
		if (oldInfo == null) {
			throw new IllegalArgumentException(resourceId + " key not exists ");
		}

		String urlKey = getUrlKey(oldInfo);
		Object object = urlMethodMap.get(urlKey);
		if (object == null) {
			throw new IllegalArgumentException(resourceId + " key not exists ");
		}
		else if (object instanceof RouteInfo) {
			urlMethodMap.put(urlKey, value);
		}
		innerIdMap.put(resourceId, value);

	}

	// 对外暴露所有的routeInfo配置，主要用于心跳等机制
	public ConcurrentMap<Long, RouteInfo> getInnerIdMap() {
		return innerIdMap;
	}

	// 解析url_params的对象结构主要分为：path,key,value
	private UrlParamsInfo getParamInfo(String configUrl) {
		String inboundService = configUrl.split(":")[1];
		if (inboundService.indexOf("?") < 0) {
			throw new IllegalArgumentException(
					"invalid url params config" + inboundService);
		}
		else {
			String[] tokens = inboundService.split("\\?");
			String inboundPath = tokens[0];
			// params like: service=aaa
			String[] paramSpit = tokens[1].split("=");
			String paramName = paramSpit[0].trim();
			String paramValue = paramSpit[1].trim();
			UrlParamsInfo info = new UrlParamsInfo();
			info.setParamName(paramName);
			info.setParamValue(paramValue);
			info.setPath(inboundPath);
			return info;
		}
	}

	/**
	 * 校验RouteInfo的数据
	 * @param info
	 * @return
	 */
	private boolean validateExtConfig(RouteInfo info) {
		if (info == null) {
			logger.error(info + " not valid,info is null");
			return false;
		}
		if (info.getRequestUrl() == null || info.getRequestMethod() == null) {
			logger.error(info + " inbound not valid");
			return false;
		}

		if (info.getType() == null) {
			logger.error(info + " type not valid");
			return false;
		}

		if (info.getType() == RouteInfo.RouteType.REST) {
			if (info.getRouteServiceId() == null) {
				logger.error(info + " service Id not valid");
				return false;
			}

			if (info.getRouteServicePath() == null) {
				logger.error(info + " service Path not valid");
				return false;
			}
		}
		return true;
	}

	public static String combinationUrlMethod(String url, String method) {
		return (url + "-" + method).toLowerCase();
	}

}
