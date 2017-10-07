package org.xujin.venus.cloud.gw.server.filter.base;

/**
 * 
 * Filter Type
 * @author xujin
 *
 */
public enum FilterType {

	BEFORE("before"), PRE("pre"), POST("post"), ERROR("error"), CUSTOM("custom"), ROUTE(
			"route"), REST("REST");

	private String filterType;

	private FilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

}
