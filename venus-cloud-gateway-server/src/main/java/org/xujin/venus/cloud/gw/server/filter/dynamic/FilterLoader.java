package org.xujin.venus.cloud.gw.server.filter.dynamic;

import java.util.List;

import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.FilterType;

/**
 * 
 * @author xujin
 *
 */
public enum FilterLoader {
	INSTANCE {
		@Override
		public List<AbstractFilter> getPreFilters() {
			return FilterRegistry.INSTANCE.getFilters(FilterType.PRE);
		}

		@Override
		public List<AbstractFilter> getPostFilters() {
			return FilterRegistry.INSTANCE.getFilters(FilterType.POST);
		}

		@Override
		public List<AbstractFilter> getBeforeFilters() {
			return FilterRegistry.INSTANCE.getFilters(FilterType.BEFORE);
		}

	};

	public abstract List<AbstractFilter> getPreFilters();

	public abstract List<AbstractFilter> getPostFilters();

	public abstract List<AbstractFilter> getBeforeFilters();
}
