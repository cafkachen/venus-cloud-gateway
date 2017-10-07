package org.xujin.venus.cloud.gw.server.filter.base;

import org.xujin.venus.cloud.gw.server.core.JanusFilterRunner;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;

public abstract class AbstractFilterContext {

	public AbstractFilterContext next;

	public void skipNextFilter(JanusHandleContext janusHandleContext) {
		AbstractFilterContext next = this.next;
		if (next == null) {
			throw new RuntimeException();
		}
		skipToNextFilter(next, janusHandleContext);
	}

	public void fireSelf(JanusHandleContext janusHandleContext) {
		skipToNextFilter(this, janusHandleContext);
	}

	public void skipFilterByFilterName(JanusHandleContext janusHandleContext,
			String filterName) {
		AbstractFilterContext filterContext = DefaultFilterPipeLine.getInstance()
				.get(filterName);
		if (filterContext == null) {
			throw new RuntimeException();
		}
		skipToNextFilter(filterContext, janusHandleContext);

	}

	private void skipToNextFilter(AbstractFilterContext filterContext,
			JanusHandleContext janusHandleContext) {
		try {
			if (filterContext.getFilter().isValid()) {
				filterContext.getFilter().run(filterContext, janusHandleContext);
			}
			else {
				skipToNextFilter(filterContext.next, janusHandleContext);
			}

		}
		catch (Exception e) {
			JanusFilterRunner.errorProcess(janusHandleContext, e);
		}
	}

	protected abstract Filter getFilter();
}
