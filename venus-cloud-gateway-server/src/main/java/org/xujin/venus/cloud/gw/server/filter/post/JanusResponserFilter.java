package org.xujin.venus.cloud.gw.server.filter.post;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;

import org.xujin.venus.cloud.gw.server.constant.Constant;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;
import org.xujin.venus.cloud.gw.server.filter.base.FilterType;
import org.xujin.venus.cloud.gw.server.filter.model.RouteInfo;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;

/**
 * 来源主要有三部分：error,rest
 * @author xujin
 *
 */
public class JanusResponserFilter extends AbstractFilter {
	public static String DEFAULT_NAME = PRE_FILTER_NAME
			+ JanusResponserFilter.class.getSimpleName().toUpperCase();

	public static String filterType = FilterType.POST.getFilterType();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void run(AbstractFilterContext filterContext, JanusHandleContext janusContext)
			throws Exception {
		HttpHeaders headers = janusContext.getResponseHttpHeaders();
		RouteInfo apiInfo = janusContext.getAPIInfo();

		if (janusContext.getRequest().getHttpVersion().isKeepAliveDefault()) {
			headers.remove(Names.CONNECTION);
		}
		else {
			headers.set(Names.CONNECTION, Values.CLOSE);
		}
		// 对content-type的添加,如果是jsonp则返回script，如果不是，则返回json。
		if (null != apiInfo
				&& Constant.CALLBACK_STATE_true.equals(apiInfo.getIsCallback())) {
			headers.set(Names.CONTENT_TYPE, "text/javascript;charset=utf-8");
		}
		else {
			if (headers.get(Names.CONTENT_TYPE) == null) {// 如果用户设置了，以用户为准
				headers.set(Names.CONTENT_TYPE, "application/json;charset=utf-8");
			}
		}
		// 获取body
		ByteBuf byteBuf = janusContext.getResponseBody();

		// 在Header中设置内容的长度
		headers.set(CONTENT_LENGTH, byteBuf == null ? 0 : byteBuf.readableBytes());
		super.run(filterContext, janusContext);

	}

}
