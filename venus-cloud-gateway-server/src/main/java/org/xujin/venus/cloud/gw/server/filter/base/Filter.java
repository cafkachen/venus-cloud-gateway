package org.xujin.venus.cloud.gw.server.filter.base;

import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;

/**
 * Filter
 * @author xujin
 *
 */
public interface Filter {

	// Filter名称
	public String name();

	// Filter初始化
	public void init();

	// Filter Run
	public void run(AbstractFilterContext filterContext,
			JanusHandleContext janusHandleContext) throws Exception;

	// Filter是否开启
	public boolean isValid();

}
