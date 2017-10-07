package org.xujin.venus.cloud.gw.server.http.client;

import org.xujin.venus.cloud.gw.server.core.JanusFilterRunner;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.exception.JanusTimeoutException;
import org.xujin.venus.cloud.gw.server.http.HttpCode;

import io.netty.util.internal.ThrowableUtil;

public abstract class SimpleHttpCallback implements HttpCallback {
	JanusHandleContext sessionContext;
	private static final JanusTimeoutException TIME_OUT_EXCEPTION = ThrowableUtil
			.unknownStackTrace(new JanusTimeoutException(), SimpleHttpCallback.class,
					"onTimeout()");

	public SimpleHttpCallback(JanusHandleContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	@Override
	public void onTimeout() {
		sessionContext.setResponseHttpCode(HttpCode.HTTP_GATEWAY_TIMEOUT);
		JanusFilterRunner.errorProcess(sessionContext, TIME_OUT_EXCEPTION);
	}

	@Override
	public void onError(Throwable e) {
		JanusFilterRunner.errorProcess(sessionContext, e);
	}
}
