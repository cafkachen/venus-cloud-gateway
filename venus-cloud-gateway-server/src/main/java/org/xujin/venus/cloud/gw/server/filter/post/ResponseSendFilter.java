package org.xujin.venus.cloud.gw.server.filter.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;
import org.xujin.venus.cloud.gw.server.filter.base.FilterType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class ResponseSendFilter extends AbstractFilter {
	private static Logger logger = LoggerFactory.getLogger(ResponseSendFilter.class);
	public static String DEFAULT_NAME = PRE_FILTER_NAME
			+ ResponseSendFilter.class.getSimpleName().toUpperCase();

	public static String filterType = FilterType.POST.getFilterType();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void run(AbstractFilterContext filterContext,
			JanusHandleContext sessionContext) {
		// 保证只会发送一次
		if (sessionContext.isBodySend()) {
			logger.error("error happens twice,then close channel",
					sessionContext.getThrowable());
			sessionContext.getRequest().closeChannle();
			return;
		}
		sessionContext.setBodySend();
		FullHttpResponse fullResponse = null;
		try {
			HttpHeaders headers = sessionContext.getResponseHttpHeaders();
			ByteBuf body = sessionContext.getResponseBody();
			fullResponse = getFullHttpResponse(sessionContext.getResponseHttpCode(), body,
					sessionContext.getResponseHttpVersion());
			fullResponse.headers().add(headers);
			// 发送信息
			sessionContext.getRequest().writeAndFlush(fullResponse);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			sessionContext.getRequest().closeChannle();
		}
		finally {
			// 记录accesslog
			/**
			 * sessionContext.getMonitorAccessLog().endAndLog(
			 * sessionContext.getResponseHttpCode(), HttpCode.HTTP_OK_CODE, fullResponse
			 * == null ? 0 : fullResponse.content().readableBytes());
			 **/
			System.out.println("记录access.log");
		}

	}

	public FullHttpResponse getFullHttpResponse(int httpCode, ByteBuf body,
			HttpVersion httpVersion) {
		HttpResponseStatus status = HttpResponseStatus.valueOf(httpCode);
		return new DefaultFullHttpResponse(httpVersion, status,
				body == null ? Unpooled.EMPTY_BUFFER : body, false);
	}
}
