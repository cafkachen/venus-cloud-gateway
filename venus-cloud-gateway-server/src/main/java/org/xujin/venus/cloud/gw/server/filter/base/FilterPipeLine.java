package org.xujin.venus.cloud.gw.server.filter.base;

import java.util.List;

/***
 * Filter处理链
 * @author xujin
 *
 */
public interface FilterPipeLine {

	void addLastSegment(Filter... filters);

	AbstractFilterContext get(String name);

	List<Filter> getAllFilter();

}
