package org.xujin.venus.cloud.gw.server.filter.base;

import java.util.HashSet;
import java.util.Set;

import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.filter.model.RouteInfo;
import org.xujin.venus.cloud.gw.server.filter.model.ServiceRouteForwardMapping;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

/**
 * Created by fish24k on 16/11/24.
 */
public abstract class AbstractResponseHeaderFilter extends AbstractFilter {
	@Override
	public void run(AbstractFilterContext filterContext,
			JanusHandleContext sessionContext) throws Exception {

		RouteInfo apiInfo = sessionContext.getAPIInfo();
		HttpHeaders httpHeaders = sessionContext.getResponseHttpHeaders();
		// header的渲染,访问服务成功才进行渲染
		if (apiInfo != null) {
			// List<ServiceRouteForwardMapping> mappingList =
			// apiInfo.getResponseMapping();
			Set<Cookie> cookies = new HashSet<Cookie>();
			/**
			 * 1.forward header mapping
			 */
			/**
			 * if (mappingList != null) { for (ServiceRouteForwardMapping forwardMapping :
			 * mappingList) { if (filterMapping(forwardMapping)) {
			 * ForwardMappingTargetPosition targetPosition = forwardMapping .getTarget();
			 * String key = forwardMapping.getKey(); Object value =
			 * getOriginalValue(sessionContext, key, forwardMapping.getSource()); if
			 * (value == null) continue; if (targetPosition ==
			 * ForwardMappingTargetPosition.Cookie) { if (value instanceof Cookie) {
			 * cookies.add((Cookie) value); } else { cookies.add(new DefaultCookie(key,
			 * value.toString())); } } else if (targetPosition ==
			 * ForwardMappingTargetPosition.Header) { httpHeaders.set(key, value); } } } }
			 **/

			/**
			 * 2.janus custom header mapping
			 */
			/**
			 * mappingList = apiInfo.getCustomResponseHeader(); if (mappingList != null) {
			 * for (ServiceRouteForwardMapping customMapping : mappingList) { if
			 * (filterMapping(customMapping)) { ForwardMappingTargetPosition
			 * targetPosition = customMapping.getTarget(); String key =
			 * customMapping.getKey(); Object value = customMapping.getValue(); if (value
			 * == null) continue; if (targetPosition ==
			 * ForwardMappingTargetPosition.Cookie) { cookies.add(new DefaultCookie(key,
			 * value.toString())); } else if (targetPosition ==
			 * ForwardMappingTargetPosition.Header) { httpHeaders.set(key, value); } } } }
			 **/

			// encode cookie to header
			for (Cookie cookie : cookies) {
				// not validate cookie name & value
				httpHeaders.add(HttpHeaders.Names.SET_COOKIE,
						ServerCookieEncoder.LAX.encode(cookie));
			}
		}

		fireNextFilter(filterContext, sessionContext);

	}

	public abstract boolean filterMapping(ServiceRouteForwardMapping mapping);

	public abstract void fireNextFilter(AbstractFilterContext filterContext,
			JanusHandleContext sessionContext);

	/**
	 * 根据position和key获取原始返回response的value
	 *
	 * @param key
	 * @param position
	 * @return
	 */
	public abstract Object getOriginalValue(JanusHandleContext sessionContext, String key,
			ServiceRouteForwardMapping.ForwardMappingSourcePosition position);
}
