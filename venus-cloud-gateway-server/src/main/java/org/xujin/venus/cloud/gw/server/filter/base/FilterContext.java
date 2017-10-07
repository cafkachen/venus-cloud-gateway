package org.xujin.venus.cloud.gw.server.filter.base;

/**
 * 
 * @author xujin
 *
 */
public class FilterContext extends AbstractFilterContext {
	
	private Filter filter;

	public FilterContext(Filter filter) {
		this.filter = filter;
	}

	@Override
	public Filter getFilter() {
		return filter;
	}
}
