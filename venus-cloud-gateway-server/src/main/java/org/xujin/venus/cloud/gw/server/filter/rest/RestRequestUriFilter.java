package org.xujin.venus.cloud.gw.server.filter.rest;

import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;

/**
 * 除去主机名+端口之外的拼接真是请求的URI
 * @author xujin
 *
 */
public class RestRequestUriFilter extends AbstractFilter {
	public static String DEFAULT_NAME = PRE_FILTER_NAME
			+ RestRequestUriFilter.class.getSimpleName().toUpperCase();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void run(AbstractFilterContext filterContext, JanusHandleContext janusContext)
			throws Exception {
		/**
		 * 未来支持两种转发 </br>
		 * 1. Spring Cloud默认路由规则 </br>
		 * 2.直接转发
		 */
		if ("POST".equals(janusContext.getAPIInfo().getRequestMethod())) {
			janusContext
					.setRestRequestUri(janusContext.getAPIInfo().getRouteServicePath());
		}
		else {
			janusContext.setRestRequestUri(janusContext.getAPIInfo().getRouteServicePath()+"123");
		}

		super.run(filterContext, janusContext);
	}

}
