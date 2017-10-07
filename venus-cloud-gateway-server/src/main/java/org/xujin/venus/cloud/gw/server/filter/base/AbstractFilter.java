package org.xujin.venus.cloud.gw.server.filter.base;

import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;

/**
 * 
 * @author xujin
 *
 */
public abstract class AbstractFilter implements Filter {

	public final static String PRE_FILTER_NAME = "JANUS_FILTER_";

	// filter 默认启用
	protected volatile boolean filterSwitch = true;

	protected volatile String filterName;

	protected volatile long timeout = 10;

	protected volatile int priority = -1;

	protected String filterType;

	@Override
	public void init() {
		filterSwitch = true;
	}

	@Override
	public void run(AbstractFilterContext filterContext,
			JanusHandleContext janusHandleContext) throws Exception {
		filterContext.skipNextFilter(janusHandleContext);
	}

	@Override
	public boolean isValid() {
		return filterSwitch;
	}

	public void setValid(boolean isValid) {
		this.filterSwitch = filterSwitch;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isFilterSwitch() {
		return filterSwitch;
	}

	public void setFilterSwitch(boolean filterSwitch) {
		this.filterSwitch = filterSwitch;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

}
