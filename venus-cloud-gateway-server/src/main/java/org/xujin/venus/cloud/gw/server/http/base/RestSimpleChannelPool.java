package org.xujin.venus.cloud.gw.server.http.base;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

import java.util.Deque;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;

/**
 * Simple {@link ChannelPool} implementation which will create new {@link Channel}s if
 * someone tries to acquire a {@link Channel} but none is in the pool atm. No limit on the
 * maximal concurrent {@link Channel}s is enforced.
 *
 * This implementation uses LIFO order for {@link Channel}s in the {@link ChannelPool}.
 *
 */
public class RestSimpleChannelPool implements ChannelPool {
	private static final AttributeKey<RestSimpleChannelPool> POOL_KEY = AttributeKey
			.newInstance("JanusChannelPool");
	private static final IllegalStateException FULL_EXCEPTION = new IllegalStateException(
			"ChannelPool full");
	private static final IllegalStateException UNHEALTHY_NON_OFFERED_TO_POOL = new IllegalStateException(
			"Channel is unhealthy not offering it back to pool");
	static {
		FULL_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
		UNHEALTHY_NON_OFFERED_TO_POOL.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
	}
	private final Deque<Channel> deque = PlatformDependent.newConcurrentDeque();
	private final ChannelPoolHandler handler;
	private final ChannelHealthChecker healthCheck;
	private final Bootstrap bootstrap;
	private final boolean releaseHealthCheck;

	/**
	 * Creates a new instance using the {@link ChannelHealthChecker#ACTIVE}.
	 *
	 * @param bootstrap the {@link Bootstrap} that is used for connections
	 * @param handler the {@link ChannelPoolHandler} that will be notified for the
	 * different pool actions
	 */
	public RestSimpleChannelPool(Bootstrap bootstrap, final ChannelPoolHandler handler) {
		this(bootstrap, handler, ChannelHealthChecker.ACTIVE);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param bootstrap the {@link Bootstrap} that is used for connections
	 * @param handler the {@link ChannelPoolHandler} that will be notified for the
	 * different pool actions
	 * @param healthCheck the {@link ChannelHealthChecker} that will be used to check if a
	 * {@link Channel} is still healthy when obtain from the {@link ChannelPool}
	 */
	public RestSimpleChannelPool(Bootstrap bootstrap, final ChannelPoolHandler handler,
			ChannelHealthChecker healthCheck) {
		this(bootstrap, handler, healthCheck, true);
	}

	/**
	 * Creates a new instance.
	 *
	 * @param bootstrap the {@link Bootstrap} that is used for connections
	 * @param handler the {@link ChannelPoolHandler} that will be notified for the
	 * different pool actions
	 * @param healthCheck the {@link ChannelHealthChecker} that will be used to check if a
	 * {@link Channel} is still healthy when obtain from the {@link ChannelPool}
	 * @param releaseHealthCheck will offercheck channel health before offering back if
	 * this parameter set to {@code true}.
	 */
	public RestSimpleChannelPool(Bootstrap bootstrap, final ChannelPoolHandler handler,
			ChannelHealthChecker healthCheck, boolean releaseHealthCheck) {
		this.handler = checkNotNull(handler, "handler");
		this.healthCheck = checkNotNull(healthCheck, "healthCheck");
		this.releaseHealthCheck = releaseHealthCheck;
		// Clone the original Bootstrap as we want to set our own handler
		this.bootstrap = checkNotNull(bootstrap, "bootstrap").clone();
		this.bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				assert ch.eventLoop().inEventLoop();
				handler.channelCreated(ch);
			}
		});
	}

	@Override
	public final Future<Channel> acquire() {
		return acquire(bootstrap.group().next().<Channel> newPromise());
	}

	@Override
	public Future<Channel> acquire(final Promise<Channel> promise) {
		checkNotNull(promise, "promise");
		return acquireHealthyFromPoolOrNew(promise);
	}

	/**
	 * Tries to retrieve healthy channel from the pool if any or creates a new channel
	 * otherwise.
	 * @param promise the promise to provide acquire result.
	 * @return future for acquiring a channel.
	 */
	private Future<Channel> acquireHealthyFromPoolOrNew(final Promise<Channel> promise) {
		try {
			final Channel ch = pollChannel();
			if (ch == null) {
				// No Channel left in the pool bootstrap a new Channel
				Bootstrap bs = bootstrap.clone();
				bs.attr(POOL_KEY, this);
				ChannelFuture f = connectChannel(bs);
				if (f.isDone()) {
					notifyConnect(f, promise);
				}
				else {
					f.addListener(new ChannelFutureListener() {
						@Override
						public void operationComplete(ChannelFuture future)
								throws Exception {
							notifyConnect(future, promise);
						}
					});
				}
				return promise;
			}
			EventLoop loop = ch.eventLoop();
			if (loop.inEventLoop()) {
				doHealthCheck(ch, promise);
			}
			else {
				loop.execute(new Runnable() {
					@Override
					public void run() {
						doHealthCheck(ch, promise);
					}
				});
			}
		}
		catch (Throwable cause) {
			promise.setFailure(cause);
		}
		return promise;
	}

	private static void notifyConnect(ChannelFuture future, Promise<Channel> promise) {
		if (future.isSuccess()) {
			promise.setSuccess(future.channel());
		}
		else {
			promise.setFailure(future.cause());
		}
	}

	private void doHealthCheck(final Channel ch, final Promise<Channel> promise) {
		assert ch.eventLoop().inEventLoop();

		Future<Boolean> f = healthCheck.isHealthy(ch);
		if (f.isDone()) {
			notifyHealthCheck(f, ch, promise);
		}
		else {
			f.addListener(new FutureListener<Boolean>() {
				@Override
				public void operationComplete(Future<Boolean> future) throws Exception {
					notifyHealthCheck(future, ch, promise);
				}
			});
		}
	}

	private void notifyHealthCheck(Future<Boolean> future, Channel ch,
			Promise<Channel> promise) {
		assert ch.eventLoop().inEventLoop();

		if (future.isSuccess()) {
			if (future.getNow() == Boolean.TRUE) {
				try {
					ch.attr(POOL_KEY).set(this);
					handler.channelAcquired(ch);
					promise.setSuccess(ch);
				}
				catch (Throwable cause) {
					closeAndFail(ch, cause, promise);
				}
			}
			else {
				closeChannel(ch);
				acquireHealthyFromPoolOrNew(promise);
			}
		}
		else {
			closeChannel(ch);
			acquireHealthyFromPoolOrNew(promise);
		}
	}

	/**
	 * Bootstrap a new {@link Channel}. The default implementation uses
	 * {@link Bootstrap#connect()}, sub-classes may override this.
	 * <p>
	 * The {@link Bootstrap} that is passed in here is cloned via
	 * {@link Bootstrap#clone()}, so it is safe to modify.
	 */
	protected ChannelFuture connectChannel(Bootstrap bs) {
		return bs.connect();
	}

	public Future<Void> forceClose(final Channel channel, final Promise<Void> promise) {
		checkNotNull(channel, "channel");
		checkNotNull(promise, "promise");
		try {
			EventLoop loop = channel.eventLoop();
			if (loop.inEventLoop()) {
				doForceClose(channel, promise);
			}
			else {
				loop.execute(new Runnable() {
					@Override
					public void run() {
						doForceClose(channel, promise);
					}
				});
			}
		}
		catch (Throwable cause) {
			closeAndFail(channel, cause, promise);
		}
		return promise;
	}

	private void doForceClose(final Channel channel, final Promise<Void> promise) {

		try {

			// 不是空，说明连接池里面没有连接同时需要对连接获取的个数-1
			if (channel.attr(POOL_KEY).getAndSet(null) != null) {
				handler.channelReleased(channel); // 说明进行了移除触发
				closeChannel(channel);
				promise.setSuccess(null);
			}
			else { // 空的话，则抛出一个异常
				removeChannel(channel);
				closeAndFail(channel,
						new IllegalArgumentException(
								"Channel " + channel + "  ChannelPool key is null "),
						promise);
			}

		}
		catch (Exception e) {
			closeAndFail(channel, e, promise);
		}
	}

	@Override
	public final Future<Void> release(Channel channel) {
		return release(channel, channel.eventLoop().<Void> newPromise());
	}

	@Override
	public Future<Void> release(final Channel channel, final Promise<Void> promise) {
		checkNotNull(channel, "channel");
		checkNotNull(promise, "promise");
		try {
			EventLoop loop = channel.eventLoop();
			if (loop.inEventLoop()) {
				doReleaseChannel(channel, promise);
			}
			else {
				loop.execute(new Runnable() {
					@Override
					public void run() {
						doReleaseChannel(channel, promise);
					}
				});
			}
		}
		catch (Throwable cause) {
			closeAndFail(channel, cause, promise);
		}
		return promise;
	}

	private void doReleaseChannel(Channel channel, Promise<Void> promise) {
		assert channel.eventLoop().inEventLoop();
		// 防止重复归还连接，及归还到错误的pool 。 如果是这种情况，则不需要对总连接数操作-1
		// Remove the POOL_KEY attribute from the Channel and check if it was acquired
		// from this pool, if not fail.
		if (channel.attr(POOL_KEY).getAndSet(null) != this) {
			closeAndFail(channel,
					// Better include a stracktrace here as this is an user error.
					new IllegalArgumentException("Channel " + channel
							+ " was not acquired from this ChannelPool"),
					promise);
		}
		else {
			try {
				if (releaseHealthCheck) {
					doHealthCheckOnRelease(channel, promise);
				}
				else {
					releaseAndOffer(channel, promise);
				}
			}
			catch (Throwable cause) {
				closeAndFail(channel, cause, promise);
			}
		}
	}

	private void doHealthCheckOnRelease(final Channel channel,
			final Promise<Void> promise) throws Exception {
		final Future<Boolean> f = healthCheck.isHealthy(channel);
		if (f.isDone()) {
			releaseAndOfferIfHealthy(channel, promise, f);
		}
		else {
			f.addListener(new FutureListener<Boolean>() {
				@Override
				public void operationComplete(Future<Boolean> future) throws Exception {
					releaseAndOfferIfHealthy(channel, promise, f);
				}
			});
		}
	}

	/**
	 * Adds the channel back to the pool only if the channel is healty.
	 * @param channel the channel to put back to the pool
	 * @param promise offer operation promise.
	 * @param future the future that contains information fif channel is healthy or not.
	 * @throws Exception in case when failed to notify handler about release operation.
	 */
	private void releaseAndOfferIfHealthy(Channel channel, Promise<Void> promise,
			Future<Boolean> future) throws Exception {
		if (future.getNow()) { // channel turns out to be healthy, offering and releasing
								// it.
			releaseAndOffer(channel, promise);
		}
		else { // channel ont healthy, just releasing it.
			handler.channelReleased(channel);
			closeAndFail(channel, UNHEALTHY_NON_OFFERED_TO_POOL, promise);
		}
	}

	private void releaseAndOffer(Channel channel, Promise<Void> promise)
			throws Exception {
		if (offerChannel(channel)) {
			handler.channelReleased(channel);
			promise.setSuccess(null);
		}
		else {
			closeAndFail(channel, FULL_EXCEPTION, promise);
		}
	}

	private static void closeChannel(Channel channel) {
		channel.attr(POOL_KEY).getAndSet(null);
		channel.close();
	}

	private static void closeAndFail(Channel channel, Throwable cause,
			Promise<?> promise) {
		closeChannel(channel);
		promise.setFailure(cause);
	}

	/**
	 * Poll a {@link Channel} out of the internal storage to reuse it. This will return
	 * {@code null} if no {@link Channel} is ready to be reused.
	 *
	 * Sub-classes may override {@link #pollChannel()} and {@link #offerChannel(Channel)}.
	 * Be aware that implementations of these methods needs to be thread-safe!
	 */
	protected Channel pollChannel() {
		return deque.pollLast();
	}

	protected boolean removeChannel(Channel channel) {
		return deque.remove(channel);
	}

	/**
	 * Offer a {@link Channel} back to the internal storage. This will return {@code true}
	 * if the {@link Channel} could be added, {@code false} otherwise.
	 *
	 * Sub-classes may override {@link #pollChannel()} and {@link #offerChannel(Channel)}.
	 * Be aware that implementations of these methods needs to be thread-safe!
	 */
	protected boolean offerChannel(Channel channel) {
		return deque.offer(channel);
	}

	@Override
	public void close() {
		for (;;) {
			Channel channel = pollChannel();
			if (channel == null) {
				break;
			}
			channel.close();
		}
	}
}