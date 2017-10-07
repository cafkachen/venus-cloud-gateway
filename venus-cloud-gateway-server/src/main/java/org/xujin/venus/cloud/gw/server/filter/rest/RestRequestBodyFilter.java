package org.xujin.venus.cloud.gw.server.filter.rest;

import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;
import org.xujin.venus.cloud.gw.server.filter.base.FilterType;

public class RestRequestBodyFilter extends AbstractFilter {
	public static String DEFAULT_NAME = PRE_FILTER_NAME
			+ RestRequestBodyFilter.class.getSimpleName().toUpperCase();

	public static String filterType = FilterType.REST.getFilterType();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void run(AbstractFilterContext filterContext,
			JanusHandleContext sessionContext) throws Exception {
		// body的处理
		sessionContext
				.setRestRequestBody(sessionContext.getRequest().getModifyBodyContent());
		super.run(filterContext, sessionContext);
	}

}
