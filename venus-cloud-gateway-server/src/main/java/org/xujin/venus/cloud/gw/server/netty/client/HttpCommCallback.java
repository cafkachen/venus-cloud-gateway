package org.xujin.venus.cloud.gw.server.netty.client;

import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpCommCallback extends NettyHttpCallback {

	public void onSuccess(FullHttpResponse fullHttpResponse) {
	};

	public void onError(Exception e) {
	}

	public void onTimeout(int sequence) {
	};
}
