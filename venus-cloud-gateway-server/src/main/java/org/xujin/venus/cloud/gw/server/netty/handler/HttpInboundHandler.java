package org.xujin.venus.cloud.gw.server.netty.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.constant.JanusProduceConstants;
import org.xujin.venus.cloud.gw.server.core.JanusFilterRunner;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.filter.base.DefaultFilterPipeLine;
import org.xujin.venus.cloud.gw.server.filter.error.ErrorFilter;
import org.xujin.venus.cloud.gw.server.filter.post.JanusResponserFilter;
import org.xujin.venus.cloud.gw.server.http.HttpCode;
import org.xujin.venus.cloud.gw.server.monitor.MonitorAccessLog;
import org.xujin.venus.cloud.gw.server.netty.http.JanusRequest;
import org.xujin.venus.cloud.gw.server.netty.http.NettyJanusRequest;
import org.xujin.venus.cloud.gw.server.startup.JanusNettyServer;
import org.xujin.venus.cloud.gw.server.utils.ByteUtils;
import org.xujin.venus.cloud.gw.server.utils.env.JanusIpUtil;
import org.xujin.venus.cloud.gw.server.utils.env.ProperityConfig;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 
 * Janus Gateway Server的请求入口
 * @author xujin
 * 
 * Sharable注解主要是用来标示一个ChannelHandler可以被安全地共享，即可以在多个Channel的ChannelPipeline中</br>
 * 使用同一个ChannelHandler， 而不必每一个ChannelPipeline都重新new一个新的ChannelHandler。
 * 
 *
 */
@Sharable
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
	private static Map<String, String> returnOkUrlMap = new HashMap<String, String>();

	static {
		if (!(ProperityConfig.janusReturnOkUrl == null)) {
			String[] returnOkUrl = ProperityConfig.janusReturnOkUrl.split(";");
			for (int i = 0; i < returnOkUrl.length; i++) {
				returnOkUrlMap.put(returnOkUrl[i], "ok");
			}
		}
	}

	public HttpInboundHandler() {

	}

	/**
	 * 从ChannelRead做读取请求数据,放入Janus Server处理的上下文
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		/**
		 * DefaultFullHttpRequest(decodeResult:
		 * failure(io.netty.handler.codec.TooLongFrameException: An HTTP line is larger
		 * than 10 bytes.), version: HTTP/1.0, content: EmptyByteBufBE) GET /bad-request
		 * HTTP/1.0 如果http协议报错的处理
		 */
		JanusRequest request = null;
		JanusHandleContext context = null;
		try {
			FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
			/**
			 * 检查path,netty中为uri，判断http头，如果是http开头，说明path为：
			 * http://mapi.abc.com:80/hello?fields=1。 该处需要做两件事情： 1：将path变成/hello?fields=1
			 * 2:将http header的host设置为mapi.abc.com:80
			 **/
			String uri = fullHttpRequest.getUri();
			if (StringUtils.startsWithIgnoreCase(uri, "http")) {
				int pos = StringUtils.indexOf(uri, "://");
				if (pos != -1 && pos < 5) {
					int slashPos = StringUtils.indexOf(uri, "/", pos + 3);
					fullHttpRequest.setUri(StringUtils.substring(uri, slashPos));
					HttpHeaders.setHost(fullHttpRequest,
							StringUtils.substring(uri, pos + 3, slashPos));
				}
			}
			// 设置channel
			context = new JanusHandleContext();
			context.setInBoundChannel(ctx.channel());

			// 设置request
			request = new NettyJanusRequest(fullHttpRequest, ctx.channel(), context);
			context.setRequest(request);
			context.setRequestUri(uri);

			// netty未对参数的非法性进行校验，这里增加对参数的非法性进行校验，避免错误的参数透传到后端。
			// http协议处理统一在HttpProtocolCheckFilter处理，这里进行获取，实际上是做参数校验的工作。主要校验url和form表单提交的body参数校验
			try {
				// 如果path存在非法字符：IllegalArgumentException
				request.getPathInfo();
				// 如果uri字符非法：IllegalArgumentException,如果body非法字符：ErrorDataDecoderException
				request.getParameterMap();
			}
			catch (Exception e) {
				fullHttpRequest.setDecoderResult(DecoderResult.failure(e));
			}
			// 判断只有http协议解析成功，才会进行心跳检查，否则不进行心跳检查
			if (fullHttpRequest.getDecoderResult().isSuccess()) {
				// 进行健康检查
				if (isHeatBeatUrl(request, context)) {
					ctx.channel().close(); // 健康检查后关闭掉连接
					return;
				}
			}

			// 放置healthcheck记录access日志，所在在这里记录
			MonitorAccessLog accessLog = new MonitorAccessLog();
			context.setMonitorAccessLog(accessLog);
			accessLog.start(request); // 记录accessLog

			// 设置Ip
			// context.setClientIp();
			request.setHeader(JanusProduceConstants.X_FORWARDED_FOR,
					JanusIpUtil.getIpByWafIpRule(context));
			// Filter Runner执行整个Filter的生命周期
			JanusFilterRunner.run(context);
		}
		catch (Throwable t) {
			logger.error(t.getMessage(), t);
			if (context != null) {
				// 设置返回的httpcode
				context.setThrowable(t);
				context.setResponseHttpCode(HttpCode.HTTP_INTERNAL_SERVER_ERROR);
				DefaultFilterPipeLine.getInstance().get(ErrorFilter.DEFAULT_NAME)
						.fireSelf(context);
			}
		}
	}

	private boolean isHeatBeatUrl(JanusRequest request, JanusHandleContext context) {
		if (returnOkUrlMap.size() != 0) {
			if (returnOkUrlMap.get(request.getPathInfo()) != null) {
				writeBody(context, HttpCode.HTTP_OK_CODE,
						returnOkUrlMap.get(request.getPathInfo()));
				// request.writeAndFlush(HttpCode.HTTP_OK_CODE,
				// returnOkUrlMap.get(request.getPathInfo()));
				return true;
			}
		}

		// 心跳检测
		if (request.getPathInfo().equals("/health")) {
			if (JanusNettyServer.online == false) {
				writeBody(context, HttpCode.HTTP_GATEWAY_SHUTDOWN, "offline");
			}
			else {
				writeBody(context, HttpCode.HTTP_OK_CODE, "online");
			}

			return true;
		}
		return false;
	}

	private void writeBody(JanusHandleContext context, int httpCode, String body) {
		context.setResponseHttpCode(httpCode);
		context.setResponseBody(ByteUtils.toByteBuf(body));
		DefaultFilterPipeLine.getInstance().get(JanusResponserFilter.DEFAULT_NAME)
				.fireSelf(context);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if (cause instanceof IOException) { // client is shutdown, log.warn
			logger.warn("Unexpected IOException from downstream.Throwable Message:"
					+ cause.getMessage());
		}
		else if (cause instanceof TooLongFrameException) {
			/**
			 * bugfix: body过长不是设置DecoderResult,而是抛异常进入exceptionCaught,导致没有进入janus流程
			 */
			FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
					HttpMethod.GET, "/bad-request", Unpooled.EMPTY_BUFFER, false);
			httpRequest.setDecoderResult(DecoderResult.failure(cause));
			channelRead(ctx, httpRequest);
			return;
		}
		else { // other errors, log.error
			logger.error("[janusExceptionCaught][" + ctx.channel().remoteAddress()
					+ " -> " + ctx.channel().localAddress() + "]", cause);
		}
		// 错误的上报
		ctx.close();
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
		channel.close();
	}

}