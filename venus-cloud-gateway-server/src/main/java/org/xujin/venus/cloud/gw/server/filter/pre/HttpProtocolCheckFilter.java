package org.xujin.venus.cloud.gw.server.filter.pre;

import org.xujin.venus.cloud.gw.server.core.JanusFilterRunner;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.exception.HttpCodeErrorJanusException;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;
import org.xujin.venus.cloud.gw.server.filter.base.FilterType;
import org.xujin.venus.cloud.gw.server.http.HttpCode;
import org.xujin.venus.cloud.gw.server.netty.http.JanusRequest;
import org.xujin.venus.cloud.gw.server.utils.env.ProperityConfig;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;

/**
 * 协议Check Filter
 * @author xujin
 *
 */
public class HttpProtocolCheckFilter extends AbstractFilter {

	public static String DEFAULT_NAME = PRE_FILTER_NAME
			+ HttpProtocolCheckFilter.class.getSimpleName().toUpperCase();
	// 是否需要进行cookie校验
	public static Boolean validateCookie = ProperityConfig.validateCookie;
	public static String className = HttpProtocolCheckFilter.class.getSimpleName();
	public static String classMethod = "run";

	public static String filterType = FilterType.PRE.getFilterType();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void run(AbstractFilterContext filterContext,
			JanusHandleContext sessionContext) {
		JanusRequest request = sessionContext.getRequest();
		DecoderResult decoderResult = request.getDecoderResult();
		if (decoderResult != null) {
			if (decoderResult.isFailure()) {
				// uri或者header,或者body的长度过长,message如下:"An HTTP line is larger than "
				// +maxLength + " bytes."
				if (decoderResult.cause() instanceof TooLongFrameException) {
					JanusFilterRunner.errorProcess(sessionContext,
							new HttpCodeErrorJanusException(
									decoderResult.cause().getMessage(),
									HttpCode.HTTP_REQUEST_URI_TOO_LONG, className,
									classMethod));

				}
				else if (decoderResult.cause() instanceof IllegalArgumentException) {
					// header校验失败(invalid escape sequence `%' at index 1
					// ),uri校验失败( Header name cannot
					// contain non-ASCII
					// characters:)
					JanusFilterRunner.errorProcess(sessionContext,
							new HttpCodeErrorJanusException(
									decoderResult.cause().getMessage(),
									HttpCode.HTTP_PRECONDITION_FAILED, className,
									classMethod));
				}
				else if (decoderResult.cause() instanceof ErrorDataDecoderException) { // body存在非法字符校验失败Bad
																						// string:
					JanusFilterRunner.errorProcess(sessionContext,
							new HttpCodeErrorJanusException(
									"body invalid :" + decoderResult.cause().getMessage(),
									HttpCode.HTTP_PRECONDITION_FAILED, className,
									classMethod));
				}
				else if (decoderResult
						.cause() instanceof PrematureChannelClosureException) { // http解析到一半，客户端又发送fin或者reset，导致该错误。
																				// 由于先对socket进行了关闭，所以该异常只会落盘日志，不会发送出去
					JanusFilterRunner.errorProcess(sessionContext,
							new HttpCodeErrorJanusException(
									"http parser failed:"
											+ decoderResult.cause().getMessage(),
									HttpCode.HTTP_PARSER_FAILED, className, classMethod));
				}
				else { // 不确定的异常，返回原始的错误信息
					JanusFilterRunner.errorProcess(sessionContext, decoderResult.cause());
				}
				// 关闭掉channel
				if (sessionContext.getInBoundChannel() != null) {
					sessionContext.getInBoundChannel().close(); // 如果在解析的时候失败，则直接关闭掉该channel。
				}
				return;
			}

		}

		if (validateCookie) {
			// cookie包含无效字符时抛出412 name contains non-ascii character:
			try {
				request.getCookieMap();
			}
			catch (Throwable e) {
				JanusFilterRunner.errorProcess(sessionContext,
						new HttpCodeErrorJanusException(e.getMessage(),
								HttpCode.HTTP_PRECONDITION_FAILED, className,
								classMethod));
				// 关闭掉channel
				if (sessionContext.getInBoundChannel() != null) {
					sessionContext.getInBoundChannel().close(); // 如果在解析的时候失败，则直接关闭掉该channel。
				}
				return;
			}
		}

		// 自动跳转到下一个Filter
		filterContext.skipNextFilter(sessionContext);
	}

}
