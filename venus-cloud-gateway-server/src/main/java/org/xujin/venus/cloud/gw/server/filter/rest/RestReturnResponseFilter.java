package org.xujin.venus.cloud.gw.server.filter.rest;

import java.util.List;

import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractResponseHeaderFilter;
import org.xujin.venus.cloud.gw.server.filter.model.RouteInfo;
import org.xujin.venus.cloud.gw.server.filter.model.ServiceRouteForwardMapping;
import org.xujin.venus.cloud.gw.server.filter.model.ServiceRouteForwardMapping.ForwardMappingSourcePosition;
import org.xujin.venus.cloud.gw.server.filter.post.JanusResponserFilter;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.cookie.ClientCookieDecoder;
import io.netty.handler.codec.http.cookie.Cookie;

/**
 * 返回Response Header
 * @author xujin
 *
 */
public class RestReturnResponseFilter extends AbstractResponseHeaderFilter {
	public static String DEFAULT_NAME = PRE_FILTER_NAME
			+ RestReturnResponseFilter.class.getSimpleName().toUpperCase();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public boolean filterMapping(ServiceRouteForwardMapping mapping) {
		return mapping.getType() == RouteInfo.RouteType.REST;
	}

	@Override
	public void fireNextFilter(AbstractFilterContext filterContext,
			JanusHandleContext sessionContext) {
		// 设置返回的http version等
		FullHttpResponse httpResponse = sessionContext.getRestFullHttpResponse();
		sessionContext.setResponseHttpVersion(httpResponse.getProtocolVersion());
		sessionContext.setResponseBody(httpResponse.content());
		sessionContext.setResponseHttpCode(httpResponse.getStatus().code());
		// 添加默认要加上的header信息

		// 默认增加Content-Encoding 和vary 两个字段
		addHeader(Names.CONTENT_ENCODING, sessionContext); // 该字段比较重要，用户确定网关层是否要进行gzip压缩
		addHeader(Names.VARY, sessionContext); // nginx默认会加vary字段
		filterContext.skipFilterByFilterName(sessionContext,
				JanusResponserFilter.DEFAULT_NAME);
	}

	private void addHeader(String name, JanusHandleContext sessionContext) {
		HttpHeaders responseHeaders = sessionContext.getResponseHttpHeaders();
		HttpHeaders restResponseHeaders = sessionContext.getRestFullHttpResponse()
				.headers();
		String value = restResponseHeaders.get(name);
		if (value != null) {
			responseHeaders.set(name, value);
		}
	}

	@Override
	public Object getOriginalValue(JanusHandleContext sessionContext, String key,
			ForwardMappingSourcePosition position) {
		FullHttpResponse httpResponse = sessionContext.getRestFullHttpResponse();
		if (position == ForwardMappingSourcePosition.Cookie) {
			List<String> cookies = httpResponse.headers()
					.getAll(HttpHeaders.Names.SET_COOKIE);
			// cookies not null here
			// todo cache cookie decode result
			for (String cookie : cookies) {
				Cookie cookie1 = ClientCookieDecoder.LAX.decode(cookie);
				if (cookie1 != null && key.equals(cookie1.name())) {
					return cookie1;
				}
			}
		}
		else if (position == ForwardMappingSourcePosition.Header) {
			return httpResponse.headers().get(key);
		}
		return null;
	}
}
