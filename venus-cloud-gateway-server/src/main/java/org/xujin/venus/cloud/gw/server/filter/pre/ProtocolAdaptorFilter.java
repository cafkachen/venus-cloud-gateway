package org.xujin.venus.cloud.gw.server.filter.pre;

import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;
import org.xujin.venus.cloud.gw.server.filter.base.FilterType;
import org.xujin.venus.cloud.gw.server.filter.rest.RestRequestUriFilter;

/**
 * 协议适配，根据配置，决定走RPC流程还是REST流程
 * @author xujin
 *
 */
public class ProtocolAdaptorFilter extends AbstractFilter {
	public static String DEFAULT_NAME = PRE_FILTER_NAME
			+ ProtocolAdaptorFilter.class.getSimpleName().toUpperCase();

	public static String filterType = FilterType.PRE.getFilterType();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void run(AbstractFilterContext filterContext,
			JanusHandleContext janusHandleContext) throws Exception {
		filterContext.skipFilterByFilterName(janusHandleContext,
				RestRequestUriFilter.DEFAULT_NAME);
	}

}
