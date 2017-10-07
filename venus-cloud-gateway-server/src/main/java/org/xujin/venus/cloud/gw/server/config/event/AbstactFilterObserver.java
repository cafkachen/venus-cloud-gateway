package org.xujin.venus.cloud.gw.server.config.event;

import org.xujin.venus.cloud.gw.server.config.vo.PublishType;

/**
 * 
 * @author xujin
 *
 * @param <T>
 */
public interface AbstactFilterObserver<T> {

	// 注册的类型
	public PublishType getPublishType();

	// 数据开始下发
	public void start();

	public void add(Long resourceId, T value);

	public void delete(Long resourceId, T value);

	public void update(Long resourceId, T value);

	// 数据下发结束
	public void complete();
}
