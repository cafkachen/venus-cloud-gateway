package org.xujin.venus.cloud.gw.server.filter.rest;

import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;

import io.netty.handler.codec.http.HttpHeaders;

public class RestRequestHeaderFilter extends AbstractFilter {
	public static String DEFAULT_NAME = PRE_FILTER_NAME
			+ RestRequestHeaderFilter.class.getSimpleName().toUpperCase();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void run(AbstractFilterContext filterContext,
			JanusHandleContext sessionContext) throws Exception {
		// 原始的header获取，系统生成的已经在header里面
		// sessionContext.getRequest().getFullHttpRequest().headers();
		// rest暂时不考虑设置映射关系
		// 自定义header添加
		// CustomHeader(sessionContext);
		// 通用header的设置
		sessionContext.getRequest().setHeader(HttpHeaders.Names.CONNECTION,
				HttpHeaders.Values.KEEP_ALIVE);

		// 用于http转发
		// sessionContext.getRequest().setHeader(HttpHeaders.Names.HOST,
		// sessionContext.getAPIInfo().getOutboundService());
		super.run(filterContext, sessionContext);
	}

}
