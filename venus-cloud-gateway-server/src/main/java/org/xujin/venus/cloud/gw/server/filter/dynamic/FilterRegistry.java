package org.xujin.venus.cloud.gw.server.filter.dynamic;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.compiler.JDKLoader;
import org.xujin.venus.cloud.gw.server.config.FilterModel;
import org.xujin.venus.cloud.gw.server.constant.Constants;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.FilterType;

/**
 * 
 * @author xujin
 *
 */
public enum FilterRegistry {

	INSTANCE {

		private Logger logger = LoggerFactory.getLogger(FilterRegistry.class);

		private volatile List<AbstractFilter> beforeFiltersQueue = new CopyOnWriteArrayList<>();
		private volatile List<AbstractFilter> preFiltersQueue = new CopyOnWriteArrayList<>();
		private volatile List<AbstractFilter> postFiltersQueue = new CopyOnWriteArrayList<>();
		private volatile List<AbstractFilter> errorFiltersQueue = new CopyOnWriteArrayList<>();
		private volatile List<AbstractFilter> routeFiltersQueue = new CopyOnWriteArrayList<>();
		private volatile List<AbstractFilter> restFiltersQueue = new CopyOnWriteArrayList<>();

		@Override
		public List<AbstractFilter> getFilters(FilterType type) {
			switch (type) {
			case BEFORE:
				return beforeFiltersQueue;
			case PRE:
				return preFiltersQueue;
			case POST:
				return postFiltersQueue;
			case ERROR:
				return errorFiltersQueue;
			case ROUTE:
				return routeFiltersQueue;
			case REST:
				return restFiltersQueue;
			default:
				return new CopyOnWriteArrayList<>();
			}
		}

		/**
		 * 移除Filter
		 */
		@Override
		public boolean remove(String key, FilterType type) throws Exception {
			logger.info("remove filter, key is: " + key + ", type is: " + type.name());
			List<AbstractFilter> filterQueue = getFilters(type);
			for (int i = 0; i < filterQueue.size(); i++) {
				AbstractFilter filter = filterQueue.get(i);
				if (key.equals(filter.getFilterName())) {
					filterQueue.remove(i);
					return true;
				}
			}
			logger.error("remove filter error" + key + " not exist!");
			throw new Exception(
					String.format("remove filter error. " + key + "%s not exist"));
		}

		/**
		 * 添加Filter
		 * @param filter
		 * @param filterQueue
		 * @throws Exception
		 */
		private void put(AbstractFilter filter, List<AbstractFilter> filterQueue)
				throws Exception {
			try {
				// Filter队列为空直接放进去，不为空就根据优先级插入进去
				if (filterQueue.isEmpty()) {
					filterQueue.add(filter);
				}
				else {
					// 按数组下标索引添加
					filterQueue.add(getInsertPosition(filterQueue, filter.getPriority()),
							filter);
				}
			}
			catch (Exception e) {
				throw new Exception("insert error:" + e);
			}
		}

		public AbstractFilter get(String key, List<AbstractFilter> filterQueue) {
			for (AbstractFilter filter : filterQueue) {
				if (key.equals(filter.getFilterName())) {
					return filter;
				}

			}
			return null;
		}

		// 更新Filter
		@Override
		public boolean update(String filterName, FilterModel filterModel, FilterType type)
				throws Exception {
			try {
				logger.info("update filter, filterName is: " + filterName
						+ ", configValue is: " + filterModel.toString() + ", type is: "
						+ type.name());
				List<AbstractFilter> filterQueue = getFilters(type);
				int priority = Integer.parseInt(filterModel.getPriority());
				for (int i = 0; i < filterQueue.size(); i++) {
					AbstractFilter f = filterQueue.get(i);
					if (f.getFilterName().equals(filterName)) {
						// 更新Filter对应的数据
						// f.updateItem(configValue);
						f.setFilterSwitch(filterModel.isFilterSwitch());
						if (f.getPriority() != priority) {
							f.setPriority(priority);
							filterQueue.remove(f);
							put(f, filterQueue);
						}
						logger.info("filter {} update succeed! {}", filterName,
								filterModel.toString());
						return true;
					}
				}
				// 新增
				AbstractFilter newFilter = getFilterInstance(filterModel);
				put(newFilter, filterQueue);
				logger.info("filter {} add succeed! {}", filterName,
						filterModel.toString());
				return true;
			}
			catch (Exception e) {
				String errorMessage = e.getMessage();
				Throwable cause = e.getCause();
				if (e instanceof InvocationTargetException) {
					errorMessage = ((InvocationTargetException) e).getTargetException()
							.getMessage();
					cause = ((InvocationTargetException) e).getTargetException()
							.getCause();
				}
				logger.error("update [{}] error:[{}]", filterModel.toString(), cause);
				throw new Exception(
						String.format("update %s error: %s", filterName, errorMessage));
			}
		}

		private List<AbstractFilter> parseFilter(List<FilterModel> filterList)
				throws Exception {
			List<AbstractFilter> filters = new CopyOnWriteArrayList<>();
			for (FilterModel filter : filterList) {
				try {
					AbstractFilter instance = getFilterInstance(filter);
					if (filter.getTimeout() != null) {
						instance.setTimeout(Long.valueOf(filter.getTimeout()));
					}
					filters.add(getInsertPosition(filters,
							Integer.parseInt(filter.getPriority())), instance);
				}
				catch (Exception e) {
					logger.error("parseFilter {} error ", filter.getKey(), e);
				}
			}

			return filters;
		}

		// 得到插入Filter的位置
		private int getInsertPosition(List<AbstractFilter> filters, int priority) {
			int position = 0;
			for (AbstractFilter f : filters) {
				if (f == null || f.getPriority() > priority) {
					break;
				}
				position++;

			}
			return position;
		}

		private AbstractFilter getFilterInstance(Class<?> clazz, FilterModel filterModel)
				throws Exception {

			Constructor<?> con = clazz.getConstructor(FilterModel.class);
			return (AbstractFilter) con.newInstance(filterModel);

		}

		private AbstractFilter getFilterInstance(FilterModel filter) throws Exception {
			Class<?> clazz = null;
			String className = filter.getKey();
			if ((clazz = JDKLoader.getInstance().getFilterClass(className)) == null) {
				className = "org.xujin.janus.server.filter." + filter.getFilterType()
						+ "." + className;
				clazz = Class.forName(className);
			}
			return getFilterInstance(clazz, filter);
		}

		public void initFilter(List<FilterModel> filters) throws Exception {
			List<FilterModel> preFilters = new ArrayList<>();
			List<FilterModel> postFilters = new ArrayList<>();
			List<FilterModel> beforeFilters = new ArrayList<>();

			for (FilterModel filterModel : filters) {
				if (Constants.PRE_FILTER.equalsIgnoreCase(filterModel.getFilterType())) {
					preFilters.add(filterModel);
				}
				else if (Constants.POST_FILTER
						.equalsIgnoreCase(filterModel.getFilterType())) {
					postFilters.add(filterModel);
				}
				else if (Constants.THRO_FILTER
						.equalsIgnoreCase(filterModel.getFilterType())
						|| Constants.BEFORE_FILTER
								.equalsIgnoreCase(filterModel.getFilterType())) {
					beforeFilters.add(filterModel);
				}
			}

			this.preFiltersQueue = parseFilter(preFilters);
			this.postFiltersQueue = parseFilter(postFilters);
			this.beforeFiltersQueue = parseFilter(beforeFilters);
		}

		@Override
		public boolean update(File file, FilterType type, FilterModel filterModel) {
			String filterName = "";
			try {
				List<AbstractFilter> filterQueue = getFilters(type);
				filterName = file.getName().split("\\.")[0];
				AbstractFilter instance = getFilterInstance(filterModel);
				for (int i = 0; i < filterQueue.size(); i++) {
					AbstractFilter f = filterQueue.get(i);
					if (f.getFilterName().toUpperCase()
							.equals(filterName.toUpperCase())) {
						filterQueue.set(i, instance);
						return true;
					}
				}
				put(instance, filterQueue);
				logger.info("update groovy filter %s to filterQueue succeed!",
						file.getName());
				return true;
			}
			catch (Exception e) {
				logger.error("update filter " + filterName + " to filterQueue failed", e);
				return false;
			}

		}

		@Override
		public boolean filterSwitch(String key, boolean flag, FilterType type)
				throws Exception {
			List<AbstractFilter> filterQueue = getFilters(type);
			if (filterQueue == null || filterQueue.isEmpty()) {
				logger.error("filter {} not found caused by {} filterQueue is empty!",
						key, type);
				throw new Exception(String.format(
						"filter %s not found. %s filterQueue is empty", key, type));
			}
			for (AbstractFilter filter : filterQueue) {
				if (filter.getFilterName().equals(key)) {
					filter.setFilterSwitch(flag);
					logger.info("[{}] update filter switch to {} succeed!",
							filter.getFilterName(), flag);
					return true;
				}
			}
			logger.error("filter {} not found in {} filterQueue!", key, type);
			throw new Exception(
					String.format("filter %s not found in %s filterQueue", key, type));
		}

		@Override
		public AbstractFilter getFilterByName(String filterName, FilterType type) {
			List<AbstractFilter> filterQueue = getFilters(type);
			if (filterQueue == null || filterQueue.isEmpty()) {
				return null;
			}
			for (AbstractFilter AbstractFilter : filterQueue) {
				if (filterName.equalsIgnoreCase(AbstractFilter.getFilterName())) {
					return AbstractFilter;
				}
			}
			return null;
		}

		@Override
		public AbstractFilter hasFilterEndsWith(String suffix, FilterType type) {
			List<AbstractFilter> filterQueue = getFilters(type);
			if (filterQueue == null || filterQueue.isEmpty()) {
				return null;
			}
			for (AbstractFilter AbstractFilter : filterQueue) {
				if (AbstractFilter.getFilterName().endsWith(suffix)) {
					return AbstractFilter;
				}
			}
			return null;
		}
	};

	public abstract AbstractFilter getFilterByName(String filterName, FilterType type);

	public abstract List<AbstractFilter> getFilters(FilterType type);

	public abstract boolean remove(String key, FilterType type) throws Exception;

	public abstract boolean update(String filterName, FilterModel filterModel,
			FilterType type) throws Exception;

	public abstract void initFilter(List<FilterModel> filters) throws Exception;

	public abstract boolean update(File file, FilterType type, FilterModel filterModel);

	public abstract boolean filterSwitch(String key, boolean flag, FilterType type)
			throws Exception;

	public abstract AbstractFilter hasFilterEndsWith(String suffix, FilterType type);

}
