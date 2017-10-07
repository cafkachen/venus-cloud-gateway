package org.xujin.venus.cloud.gw.server.http.base;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.http.base.RestChannelPool.AcquireTimeoutAction;
import org.xujin.venus.cloud.gw.server.netty.ByteBufManager;
import org.xujin.venus.cloud.gw.server.startup.JanusNettyServer;
import org.xujin.venus.cloud.gw.server.utils.env.ProperityConfig;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;

public class ConnectionPool extends AbstractChannelPoolMap<ServerInfo, RestChannelPool> {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
	private static final ConnectionPool INSTANCE = new ConnectionPool(); // 单例实例
	// channel对应的serverInfo对象
	public static final AttributeKey<ServerInfo> SERVICE_INFO_KEY = AttributeKey
			.newInstance("janusRestServerInfo");
	// channel的创建时间 单位为ms
	public static final AttributeKey<Long> CHANNEL_CREATE_TIME = AttributeKey
			.newInstance("janusChannelCreateTime");
	// public static final AttributeKey<SessionContext> SESSION_CONTEXT =
	// AttributeKey.newInstance("sessionContext");

	public static final AttributeKey<Promise<FullHttpResponse>> CAHNNEL_PROMISE = AttributeKey
			.newInstance("janusChannelPromise");

	// 连接被持有的标识
	public static final AttributeKey<ScheduledFuture> holding_schedule = AttributeKey
			.newInstance("janusHolding");

	public RestChannelHealthChecker restChecker = new RestChannelHealthChecker();

	public static ConnectionPool getInstance() {
		return INSTANCE;
	}

	private ConnectionPool() {

	}

	@Override
	protected RestChannelPool newPool(ServerInfo key) {
		Bootstrap bootStrap = new Bootstrap();
		bootStrap.group(JanusNettyServer.workerGroup).channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.ALLOCATOR, ByteBufManager.byteBufAllocator)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
						ProperityConfig.httpConnectTimeout);

		ChannelPoolHandler handler = new RestChannelPoolHandler(key);
		bootStrap.remoteAddress(key.getInetAddress());
		// TODO: 设置最大连接数，获取连接超时时间(超时时间如果获取，超时后如何处理)
		// 只使用一个worker去新建连接(可以考虑单独使用一个worker去新建连接，避免影响正常的运行)

		Map<String, String> tag = new HashMap<String, String>();
		tag.put("ip", String.valueOf(key.inetAddress.getAddress()));
		tag.put("port", String.valueOf(key.inetAddress.getPort()));
		RestChannelPool pool = new RestChannelPool(bootStrap, handler, restChecker,
				AcquireTimeoutAction.FAIL, ProperityConfig.aquirePoolTimeout,
				ProperityConfig.maxConnections, ProperityConfig.maxPendings, true);
		return pool;
	}

	public Future<Channel> asynGetChannel(final ServerInfo serverInfo) {
		ChannelPool channelPool = get(serverInfo);
		Future<Channel> future = channelPool.acquire();
		future.addListener(new GenericFutureListener<Future<? super Channel>>() {
			@Override
			public void operationComplete(Future<? super Channel> future)
					throws Exception {
				if (future.isSuccess()) {
					// read server custom timeout
					Integer holdingTimeout = serverInfo.getHoldingTimeout();
					if (holdingTimeout == null || holdingTimeout <= 0) {
						holdingTimeout = ProperityConfig.maxHolding;

					}
					if (logger.isDebugEnabled()) {
						logger.debug(
								serverInfo.getHost() + " maxHolding : " + holdingTimeout);
					}

					Channel channel = (Channel) future.getNow();
					ScheduledFuture scheduledFuture = channel.eventLoop().schedule(
							new HoldTimeoutHandleTask(channel), holdingTimeout,
							TimeUnit.MILLISECONDS);
					channel.attr(holding_schedule).set(scheduledFuture);
				}
			}
		});

		return future;
	}

	// 归还连接
	public Future<Void> releaseChannel(final Channel channel) {
		resetChannelPromise(channel);
		ChannelPool channelPool = getChannelPool(channel);
		Future<Void> future = channelPool.release(channel);
		future.addListener(new CancelHoldTask(channel));

		return future;
	}

	// 强制关闭
	public Future<Void> forceClose(final Channel channel) {
		resetChannelPromise(channel);
		RestSimpleChannelPool channelPool = getChannelPool(channel);
		ChannelPromise promise = channel.newPromise();
		Future<Void> future = channelPool.forceClose(channel, promise);
		future.addListener(new CancelHoldTask(channel));
		return promise;
	}

	private RestSimpleChannelPool getChannelPool(Channel channel) {
		ServerInfo serverInfo = channel.attr(SERVICE_INFO_KEY).get();
		return get(serverInfo);
	}

	private void resetChannelPromise(Channel channel) {
		channel.attr(CAHNNEL_PROMISE).set(null);

	}

	public void addChannelPromise(Channel channel, Promise<FullHttpResponse> promise) {
		channel.attr(CAHNNEL_PROMISE).set(promise);
	}

	public Promise<FullHttpResponse> getChannelPromise(Channel channel) {
		Promise<FullHttpResponse> promise = channel.attr(CAHNNEL_PROMISE).getAndSet(null);
		return promise;
	}

	public class CancelHoldTask implements GenericFutureListener<Future<Void>> {
		private Channel channel;

		public CancelHoldTask(Channel channel) {
			this.channel = channel;
		}

		@Override
		public void operationComplete(Future<Void> future) throws Exception {
			if (future.isSuccess()) {
				ScheduledFuture scheduledFuture = channel.attr(holding_schedule)
						.getAndSet(null);
				if (scheduledFuture != null) {
					scheduledFuture.cancel(false);
				}
			}

		}

	}

	// 如果超时，需要将连接给close掉，避免该连接被其他访问使用，导致数据的错乱。
	private class HoldTimeoutHandleTask implements Runnable {
		private final Channel channel;

		private HoldTimeoutHandleTask(Channel channel) {
			this.channel = channel;
		}

		@Override
		public void run() {
			Promise<FullHttpResponse> promise = null;
			if (channel != null && (promise = getChannelPromise(channel)) != null) {
				forceClose(channel);
				promise.tryFailure(new TimeoutException(channel.toString()));
			}
		}
	}
}
