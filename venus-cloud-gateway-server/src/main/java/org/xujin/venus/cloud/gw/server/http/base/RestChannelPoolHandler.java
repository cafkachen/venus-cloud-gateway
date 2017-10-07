package org.xujin.venus.cloud.gw.server.http.base;

import java.util.concurrent.TimeUnit;

import org.xujin.venus.cloud.gw.server.utils.env.ProperityConfig;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class RestChannelPoolHandler extends AbstractChannelPoolHandler {

	private ServerInfo serverInfo;
	private HttpClientInboundHandler clientHandler;

	public RestChannelPoolHandler(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
		clientHandler = new HttpClientInboundHandler();
	}

	@Override
	public void channelCreated(Channel ch) throws Exception {
		// 绑定channel对应的serverInfo对象。这样可以通过channel直接拿到对应的channelPool
		ch.attr(ConnectionPool.SERVICE_INFO_KEY).set(serverInfo);
		ch.attr(ConnectionPool.CHANNEL_CREATE_TIME).set(System.currentTimeMillis());

		ch.pipeline().addLast(new IdleStateHandler(ProperityConfig.httpIdleTimeout, 0, 0,
				TimeUnit.MILLISECONDS));
		ch.pipeline().addLast(new HttpResponseDecoder());
		// 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
		ch.pipeline().addLast(new HttpRequestEncoder());

		ch.pipeline().addLast(new HttpObjectAggregator(1024 * 1024 * 64));
		ch.pipeline().addLast(clientHandler);
	}

}
