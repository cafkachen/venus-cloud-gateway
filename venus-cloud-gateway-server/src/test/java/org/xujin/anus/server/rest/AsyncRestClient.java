package org.xujin.anus.server.rest;

import org.junit.Test;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.http.client.AsyncHttpRequest;
import org.xujin.venus.cloud.gw.server.http.client.SimpleHttpCallback;
import org.xujin.venus.cloud.gw.server.utils.env.ProperityConfig;

import com.alibaba.fastjson.JSON;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;

public class AsyncRestClient {

	@Test
	public void asyncTest() {
		JanusHandleContext sessionContext = new JanusHandleContext();
		AsyncHttpRequest.builder().remoteAddress("localhost:8000")
				.sessionContext(sessionContext).httpMethod(HttpMethod.GET)
				.uri("/sc/order/2")
				/**
				 * connection holding 500ms
				 */
				.holdingTimeout(ProperityConfig.janusHttpPoolOauthMaxHolding).build()
				.execute(new SimpleHttpCallback(sessionContext) {
					@Override
					public void onSuccess(FullHttpResponse result) {
						System.out.println(JSON.toJSONString(result));
					}
				});
	}

}
