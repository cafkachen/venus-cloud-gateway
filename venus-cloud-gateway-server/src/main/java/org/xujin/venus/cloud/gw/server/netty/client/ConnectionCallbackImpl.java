package org.xujin.venus.cloud.gw.server.netty.client;

import java.util.concurrent.ScheduledFuture;

/**
 * 客户端发送异步回调接口实现
 */
public abstract class ConnectionCallbackImpl implements ConnectionCallback {

	private ScheduledFuture<?> timeoutFuture;

	public ScheduledFuture<?> getTimeoutFuture() {
		return timeoutFuture;
	}

	public void setTimeoutFuture(ScheduledFuture<?> timeoutFuture) {
		this.timeoutFuture = timeoutFuture;
	}
}
