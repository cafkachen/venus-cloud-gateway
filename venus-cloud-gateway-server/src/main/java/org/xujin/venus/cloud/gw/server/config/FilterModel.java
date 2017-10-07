package org.xujin.venus.cloud.gw.server.config;

import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;
import org.xujin.venus.cloud.gw.server.filter.base.Filter;

/**
 * FilterModel
 * @author xujin
 *
 */
public class FilterModel implements Filter {

	// filter 默认启用
	protected volatile boolean filterSwitch = true;

	private String priority = "1";
	private String key = "defaultFilter";
	private String timeout = "1000";
	private String params;
	private String filterType = "pre";

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

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public boolean isFilterSwitch() {
		return filterSwitch;
	}

	public void setFilterSwitch(boolean filterSwitch) {
		this.filterSwitch = filterSwitch;
	}

	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("filterSwitch: " + filterSwitch + "\n");
			sb.append("priority: " + priority + "\n");
			sb.append("filterType: " + filterType + "\n");
			sb.append("key: " + key + "\n");
			sb.append("params: " + params + "\n");
			sb.append("\n");
			return sb.toString();
		}
		catch (Exception ignore) {
			return "NULL";
		}
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

}
