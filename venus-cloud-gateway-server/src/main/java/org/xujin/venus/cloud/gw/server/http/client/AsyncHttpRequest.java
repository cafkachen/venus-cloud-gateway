package org.xujin.venus.cloud.gw.server.http.client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.core.JanusFilterRunner;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.exception.NoStrackException;
import org.xujin.venus.cloud.gw.server.http.HttpCode;
import org.xujin.venus.cloud.gw.server.http.base.ConnectionPool;
import org.xujin.venus.cloud.gw.server.http.base.ServerInfo;
import org.xujin.venus.cloud.gw.server.utils.env.ProperityConfig;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

/**
 * 基于Netty的异步http client request
 */
public class AsyncHttpRequest {
	private static final Logger logger = LoggerFactory.getLogger(AsyncHttpRequest.class);
	public static String className = AsyncHttpRequest.class.getSimpleName();
	private HttpVersion httpVersion; // 默认HttpVersion.HTTP_1_1
	private HttpMethod httpMethod;
	private String uri;
	private String remoteAddress;
	private JanusHandleContext sessionContext;
	private HttpHeaders headers;
	private ByteBuf content; // 默认Unpooled.EMPTY_BUFFER
	private Integer holdingTimeout;
	// private HttpCallback callback;
	private FullHttpRequest request;
	private int retry;
	private ServerInfo serverInfo;
	private HttpCallback callback;

	private AsyncHttpRequest(Builder builder) {
		this.remoteAddress = builder.remoteAddress;
		this.sessionContext = builder.sessionContext;
		this.httpMethod = builder.httpMethod;
		this.uri = builder.uri;
		this.httpVersion = builder.httpVersion;
		// this.callback = builder.callback;
		this.content = builder.content;
		this.headers = builder.headers;
		this.holdingTimeout = builder.holdingTimeout;
	}

	public Future execute(HttpCallback callback) {
		retry = ProperityConfig.janusHttpRetryNum;
		this.callback = callback;
		serverInfo = getServerInfo(remoteAddress);
		if (holdingTimeout != null) {
			serverInfo.setHoldingTimeout(holdingTimeout);
		}

		request = new DefaultFullHttpRequest(
				httpVersion == null ? HttpVersion.HTTP_1_1 : httpVersion, httpMethod, uri,
				content == null ? Unpooled.EMPTY_BUFFER : content);
		if (headers != null) {
			request.headers().add(headers);
		}
		request.headers().set("Host", remoteAddress);
		request.headers().set("Connection", "Keep-Alive");
		sendRequest();
		return null;// 暂无需支持Future
	}

	public Future execute(HttpCallback callback, FullHttpRequest fullRequest) {
		retry = ProperityConfig.janusHttpRetryNum;
		this.callback = callback;
		serverInfo = getServerInfo(remoteAddress);
		if (holdingTimeout != null) {
			serverInfo.setHoldingTimeout(holdingTimeout);
		}

		/**
		 * request = new DefaultFullHttpRequest( httpVersion == null ?
		 * HttpVersion.HTTP_1_1 : httpVersion, httpMethod, uri, content == null ?
		 * Unpooled.EMPTY_BUFFER : content);
		 **/
		request = fullRequest;
		if (headers != null) {
			request.headers().add(headers);
		}
		request.headers().set("Host", remoteAddress);
		request.headers().set("Connection", "Keep-Alive");
		sendRequest();
		// 暂无需支持Future
		return null;
	}

	private void sendRequest() {
		// todo 后续需要把pipeline逻辑分开,http转发以更好的支持chunked分段转发
		Future<Channel> future = ConnectionPool.getInstance().asynGetChannel(serverInfo);
		future.addListener(new ConnectionListener());
	}

	// 建立建立的结果通知
	private class ConnectionListener implements FutureListener<Channel> {

		@Override
		public void operationComplete(Future<Channel> future) throws Exception {
			// 响应的callback处理
			// ThreadLocalTrace.push(sessionContext.getTraceId());
			if (future.isSuccess()) { // 连接建立成功，将callback绑定到channel上面
				final Channel outboundChannel = (Channel) future.get();
				// 添加request的callback
				Promise<FullHttpResponse> callbackPromise = outboundChannel.eventLoop()
						.<FullHttpResponse> newPromise();
				callbackPromise.addListener(new ResponseListener());

				ConnectionPool.getInstance().addChannelPromise(outboundChannel,
						callbackPromise);
				ChannelPromise sendRequestPromise = outboundChannel.newPromise();
				sendRequestPromise
						.addListener(new GenericFutureListener<Future<? super Void>>() { // 发送的监听
							@Override
							public void operationComplete(Future<? super Void> future)
									throws Exception {
								if (future.isSuccess()) {
									// sessionContext.getMonitorAccessLog().pointOspSendOver();
									// 暂时不打点
								}
								else {
									// 如果写失败了，则需要关闭连接
									ConnectionPool.getInstance()
											.forceClose(outboundChannel);
									Promise<FullHttpResponse> promise = ConnectionPool
											.getInstance()
											.getChannelPromise(outboundChannel);
									if (promise != null) {
										promise.setFailure(future.cause());
									}
								}
							}
						});
				outboundChannel.writeAndFlush(request, sendRequestPromise);
			}
			else {
				// 连接失败
				errorProcess(future.cause());
			}
		}
	}

	// 结果处理，触发的来源：发送失败，接收数据成功，关闭连接，reset by peer连接
	private class ResponseListener implements FutureListener<FullHttpResponse> {

		@Override
		public void operationComplete(Future<FullHttpResponse> future) throws Exception {
			if (future.isSuccess()) {
				FullHttpResponse fullHttpResponse = future.getNow();
				try {
					callback.onSuccess(fullHttpResponse);
				}
				catch (Exception e) {
					JanusFilterRunner.errorProcess(sessionContext, e);
				}
				finally {
					deepRelease(fullHttpResponse.content());
					deepRelease(request.content());
				}
			}
			else {
				Throwable throwable = future.cause();
				// 异常处理
				if (throwable instanceof IOException) {
					// 重试机制
					if (retry-- > 0) {
						logger.warn("retry " + throwable.getMessage()
								+ throwable.getClass().toString());
						// 重试的机制，失败由于IO的异常，会进行重试发送
						sendRequest();
						return;
					}
				}
				errorProcess(throwable);

			}
		}

	}

	public void errorProcess(Throwable throwable) {
		try {
			if (throwable instanceof TimeoutException
					|| throwable instanceof ConnectTimeoutException) { // 连接超时如何处理
																		// ，访问超时
				logger.warn(throwable.getMessage());
				sessionContext.setResponseHttpCode(HttpCode.HTTP_GATEWAY_TIMEOUT);
				callback.onTimeout();
			}
			else {
				sessionContext.setResponseHttpCode(HttpCode.HTTP_BAD_GATEWAY);
				if (throwable instanceof ConnectException) { // 连接不上
					NoStrackException exception = new NoStrackException(
							throwable.getMessage(), "500", className, "errorProcess");
					callback.onError(exception);
				}
				else {
					callback.onError(throwable);
				}

			}
		}
		catch (Exception e) {
			sessionContext.setResponseHttpCode(HttpCode.HTTP_BAD_GATEWAY);
			callback.onError(e);
		}
		finally {
			deepRelease(request.content());
		}

	}

	public static ServerInfo getServerInfo(String remoteAddress) {
		String[] strs = remoteAddress.split(";");
		for (String s : strs) {
			ServerInfo ipPort = null;
			String[] ipPorts = s.split(":");
			if (ipPorts.length == 1) {// 默认80端口
				ipPort = new ServerInfo(ipPorts[0], 80);
			}
			else {
				ipPort = new ServerInfo(ipPorts[0], Integer.valueOf(ipPorts[1]));
			}
			return ipPort;
		}
		return null;
	}

	public static void deepRelease(ByteBuf buf) {
		while (true) {
			if (buf != null && buf.refCnt() > 0) {
				if (!buf.release()) {
					return;
				}
			}
			else {
				return;
			}
		}
	}

	public static final class Builder {
		private HttpVersion httpVersion;
		private String uri;
		private HttpMethod httpMethod;
		private String remoteAddress;
		private JanusHandleContext sessionContext;
		// private HttpCallback callback;
		private HttpHeaders headers;
		private ByteBuf content;
		private Integer holdingTimeout;

		public Builder remoteAddress(String remoteAddress) {
			this.remoteAddress = remoteAddress;
			return this;
		}

		public Builder sessionContext(JanusHandleContext sessionContext) {
			this.sessionContext = sessionContext;
			return this;
		}

		public Builder uri(String uri) {
			this.uri = uri;
			return this;
		}

		public Builder headers(HttpHeaders headers) {
			this.headers = headers;
			return this;
		}

		public Builder holdingTimeout(Integer holdingTimeout) {
			this.holdingTimeout = holdingTimeout;
			return this;
		}

		public Builder httpVersion(HttpVersion httpVersion) {
			this.httpVersion = httpVersion;
			return this;
		}

		public Builder httpMethod(HttpMethod httpMethod) {
			this.httpMethod = httpMethod;
			return this;
		}

		// public Builder callback(HttpCallback callback) {
		// this.callback = callback;
		// return this;
		// }

		public Builder content(ByteBuf content) {
			this.content = content;
			return this;
		}

		public AsyncHttpRequest build() {
			return new AsyncHttpRequest(this);
		}

	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public JanusHandleContext getSessionContext() {
		return sessionContext;
	}

	public HttpVersion getHttpVersion() {
		return httpVersion;
	}

	public String getUri() {
		return uri;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public static Builder builder() {
		return new Builder();
	}
}
