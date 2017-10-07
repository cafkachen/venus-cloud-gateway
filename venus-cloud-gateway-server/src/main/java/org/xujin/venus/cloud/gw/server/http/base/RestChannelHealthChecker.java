package org.xujin.venus.cloud.gw.server.http.base;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.util.concurrent.Future;

/**
 * 1:检测channel的active状态 2:检测idle的时间？
 * @author freeman.he
 *
 */
public class RestChannelHealthChecker implements ChannelHealthChecker {

	public RestChannelHealthChecker() {
		super();
	}

	@Override
	public Future<Boolean> isHealthy(Channel channel) {
		EventLoop loop = channel.eventLoop();
		return channel.isActive() ? loop.newSucceededFuture(Boolean.TRUE)
				: loop.newSucceededFuture(Boolean.FALSE);
	}

}
