package org.xujin.venus.cloud.gw.server.http.client;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * 
 * @author xujin
 *
 */
public interface HttpCallback {

	void onSuccess(FullHttpResponse result);

	void onTimeout();

	void onError(Throwable e);
}
