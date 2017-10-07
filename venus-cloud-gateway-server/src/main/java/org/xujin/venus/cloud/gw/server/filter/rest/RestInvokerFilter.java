package org.xujin.venus.cloud.gw.server.filter.rest;

import org.springframework.cloud.client.ServiceInstance;
import org.xujin.venus.cloud.gw.server.core.JanusFilterRunner;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.exception.JanusException;
import org.xujin.venus.cloud.gw.server.exception.JanusTimeoutException;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.AbstractFilterContext;
import org.xujin.venus.cloud.gw.server.filter.base.FilterType;
import org.xujin.venus.cloud.gw.server.http.HttpCode;
import org.xujin.venus.cloud.gw.server.http.client.AsyncHttpRequest;
import org.xujin.venus.cloud.gw.server.http.client.SimpleHttpCallback;
import org.xujin.venus.cloud.gw.server.netty.http.JanusRequest;
import org.xujin.venus.cloud.gw.server.utils.SpringCloudHelper;
import org.xujin.venus.cloud.gw.server.utils.env.ProperityConfig;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ThrowableUtil;

/**
 * 协议转发为内部REST服务
 * @author xujin
 *
 */
public class RestInvokerFilter extends AbstractFilter {

	private static final JanusTimeoutException TIME_OUT_EXCEPTION = ThrowableUtil
			.unknownStackTrace(new JanusTimeoutException(), RestInvokerFilter.class,
					"onTimeout()");
	private static final JanusException REST_INVOKER_ERROR_EXCEPTION = ThrowableUtil
			.unknownStackTrace(new JanusException(HttpCode.HTTP_BAD_GATEWAY_STR),
					RestInvokerFilter.class, "onError()");

	public static String DEFAULT_NAME = PRE_FILTER_NAME
			+ RestInvokerFilter.class.getSimpleName().toUpperCase();

	public static String filterType = FilterType.ROUTE.getFilterType();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void run(final AbstractFilterContext filterContext,
			final JanusHandleContext janusHandleContext) throws Exception {
		ServiceInstance serviceInstance = SpringCloudHelper.getServiceInstanceByLB(
				janusHandleContext, janusHandleContext.getAPIInfo().getRouteServiceId());
		// 生成发送的Request对象
		FullHttpRequest outBoundRequest = getOutBoundHttpRequest(janusHandleContext);

		HttpHeaders headers = outBoundRequest.headers();
		String address = headers.get(HttpHeaders.Names.HOST);

		// 转发的时候设置LB获取到的主机IP和端口即可
		AsyncHttpRequest.builder()
				.remoteAddress(
						serviceInstance.getHost() + ":" + serviceInstance.getPort())
				.sessionContext(janusHandleContext)
				/**
				 * connection holding 500ms
				 */
				.holdingTimeout(ProperityConfig.janusHttpPoolOauthMaxHolding).build()
				.execute(new SimpleHttpCallback(janusHandleContext) {
					@Override
					public void onSuccess(FullHttpResponse result) {
						// testResult(result);
						janusHandleContext.setRestFullHttpResponse(result);
						// 跳转到下一个Filter
						filterContext.skipNextFilter(janusHandleContext);
					}

					@Override
					public void onError(Throwable e) {
						janusHandleContext.setResponseHttpCode(HttpCode.HTTP_BAD_GATEWAY);
						REST_INVOKER_ERROR_EXCEPTION.setMessage(e.getMessage());
						JanusFilterRunner.errorProcess(janusHandleContext,
								REST_INVOKER_ERROR_EXCEPTION);
					}

					@Override
					public void onTimeout() {
						janusHandleContext
								.setResponseHttpCode(HttpCode.HTTP_GATEWAY_TIMEOUT);
						TIME_OUT_EXCEPTION.setMessage("timeout:" + "3000ms");
						JanusFilterRunner.errorProcess(janusHandleContext,
								TIME_OUT_EXCEPTION);
					}
				}, outBoundRequest);

	}

	/**
	 * 
	 * 拼接参数
	 * @param JanusHandleContext
	 * @return
	 */
	private String appandParams(JanusHandleContext janusHandleContext) {
		StringBuilder stringBuilder = InternalThreadLocalMap.get().stringBuilder();
		stringBuilder.append("client_id=");
		stringBuilder.append("ss");
		stringBuilder.append("&access_token=");
		stringBuilder.append("sss");
		return stringBuilder.toString();
	}

	/**
	 * 需要添加 header,httpVersion,httpMethod,uri,body等数据
	 * @param janusHandleContext
	 * @return
	 */
	private FullHttpRequest getOutBoundHttpRequest(
			JanusHandleContext janusHandleContext) {
		JanusRequest httpRequest = janusHandleContext.getRequest();
		FullHttpRequest request = new DefaultFullHttpRequest(httpRequest.getHttpVersion(),
				httpRequest.getHttpMethod(), janusHandleContext.getRestRequestUri(),
				janusHandleContext.getRestRequestBody());
		// source header is http request header
		request.headers().add(httpRequest.getHttpHeader());
		return request;
	}

	/**
	 * 测试返回数据输出流
	 * @param result
	 */
	private void testResult(FullHttpResponse result) {
		byte[] bytes = new byte[result.content().readableBytes()];
		result.content().readBytes(bytes);
		String response = new String(bytes);
		System.out.println(response);
	}
}
