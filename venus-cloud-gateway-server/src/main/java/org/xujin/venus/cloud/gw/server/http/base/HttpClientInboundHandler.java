package org.xujin.venus.cloud.gw.server.http.base;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;

@Sharable
public class HttpClientInboundHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory
			.getLogger(HttpClientInboundHandler.class);

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Promise<FullHttpResponse> promise = ConnectionPool.getInstance()
				.getChannelPromise(ctx.channel());
		FullHttpResponse fullHttpResponse = (FullHttpResponse) msg;
		if (promise != null) {
			try {
				String connecionHeader = fullHttpResponse.headers().get(Names.CONNECTION);
				// 说明keepalive关闭了后端连接
				if (connecionHeader != null && connecionHeader.equals(Values.CLOSE)) {
					ConnectionPool.getInstance().forceClose(ctx.channel());
				}
				else {
					ConnectionPool.getInstance().releaseChannel(ctx.channel());
				}

				promise.setSuccess(fullHttpResponse);
			}
			catch (Exception e) {
				ReferenceCountUtil.safeRelease(fullHttpResponse.content());
				promise.setFailure(e);
			}
		}
		else {
			logger.error("channelRead can not find promise from channel: "
					+ ctx.channel().localAddress() + ">" + ctx.channel().remoteAddress()
					+ ",msg:" + msg != null ? msg.toString() : "");

		}

	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause)
			throws Exception {
		// 该处的错误，一般都是io方面的错误(比如:RST等错误)
		final Promise<FullHttpResponse> promise = ConnectionPool.getInstance()
				.getChannelPromise(ctx.channel());
		ConnectionPool.getInstance().forceClose(ctx.channel());
		if (cause instanceof IOException) {
			logger.warn("exceptionCaught " + ctx.channel().localAddress() + ">"
					+ ctx.channel().remoteAddress());
		}
		else {
			logger.error("exceptionCaught " + ctx.channel().localAddress() + ">"
					+ ctx.channel().remoteAddress());
		}

		if (promise != null) {
			promise.setFailure(cause);
		}

		// 最后一个异常，不需要上抛，否则在tail里面会报 没有地方处理异常。
		// super.exceptionCaught(ctx, cause);
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		final Promise<FullHttpResponse> promise = ConnectionPool.getInstance()
				.getChannelPromise(ctx.channel());
		ConnectionPool.getInstance().forceClose(ctx.channel()); // 连接已经是close的状态
		if (promise != null) {
			logger.error("channelInactive " + ctx.channel().localAddress() + ">"
					+ ctx.channel().remoteAddress());
			promise.setFailure(new ClosedChannelException());

		}
		super.channelInactive(ctx);
	}

	@Override
	public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt)
			throws Exception {
		// 如果是超时,则对连接进行关闭
		if (!(evt instanceof IdleStateEvent)) {
			super.userEventTriggered(ctx, evt);
			return;
		}
		Channel channel = ctx.channel();
		ConnectionPool.getInstance().forceClose(channel);

	}

}