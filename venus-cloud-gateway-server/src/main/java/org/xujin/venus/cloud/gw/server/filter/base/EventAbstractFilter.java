package org.xujin.venus.cloud.gw.server.filter.base;

import org.xujin.venus.cloud.gw.server.config.event.AbstactFilterObserver;

public abstract class EventAbstractFilter<T> extends AbstractFilter implements AbstactFilterObserver<T> {

	@Override
	public void add(Long resourceId, T value) {

	}

	@Override
	public void delete(Long resourceId, T value) {

	}

	@Override
	public void update(Long resourceId, T value) {

	}

	@Override
	public void start() {

	}

	@Override
	public void complete() {

	}

}
