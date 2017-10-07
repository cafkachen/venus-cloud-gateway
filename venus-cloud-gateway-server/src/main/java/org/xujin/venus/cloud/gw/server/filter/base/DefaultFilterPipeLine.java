package org.xujin.venus.cloud.gw.server.filter.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * janus Server默认Filter执行链
 * @author xujin
 *
 */
public class DefaultFilterPipeLine implements FilterPipeLine {

	private final Map<String, AbstractFilterContext> name2ctx = new HashMap<String, AbstractFilterContext>(4);
	private static DefaultFilterPipeLine instance;

	private DefaultFilterPipeLine() {
	}

	public static FilterPipeLine getInstance() {
		if (instance == null) {
			synchronized (DefaultFilterPipeLine.class) {
				if (instance == null) {
					instance = new DefaultFilterPipeLine();
				}
			}
		}
		return instance;
	}

	@Override
	public void addLastSegment(Filter... filters) {
		if (filters.length == 0 || filters[0] == null) {
			return;
		}
		for (int size = 0; size < filters.length; size++) {
			Filter filter = filters[size];
			checkDuplicateName(filter.name());
			name2ctx.put(filter.name(), new FilterContext(filter));
			if (size == 0) {
				continue;
			} else {
				name2ctx.get(filters[size - 1].name()).next = name2ctx.get(filters[size].name());
			}

		}
	}

	@Override
	public List<Filter> getAllFilter() {
		Collection<AbstractFilterContext> con = name2ctx.values();
		List<Filter> filterList = new ArrayList<Filter>();
		for (AbstractFilterContext context : con) {
			filterList.add(context.getFilter());
		}
		return filterList;
	}

	@Override
	public AbstractFilterContext get(String name) {

		return name2ctx.get(name);
	}

	private void checkDuplicateName(String name) {
		if (name2ctx.containsKey(name)) {
			throw new IllegalArgumentException("Duplicate filter name: " + name);
		}
	}

}
