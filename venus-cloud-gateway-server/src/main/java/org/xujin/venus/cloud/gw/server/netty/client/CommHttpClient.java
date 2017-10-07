package org.xujin.venus.cloud.gw.server.netty.client;

import java.net.URI;

import org.xujin.venus.cloud.gw.server.netty.ByteBufManager;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

public class CommHttpClient {

	public static void sendMessage(final FullHttpRequest request,
			final HttpCommCallback callback) {

		sendMessage(request, callback, 0);
	}

	public static void sendMessage(final FullHttpRequest request,
			final HttpCommCallback callback, final int timeout) {

		// SSL 方法未实现
		boolean isSsl = false;

		HttpCommCallback releaseCallback = new HttpCommCallback() {

			@Override
			public void onTimeout(int sequence) {
				ByteBufManager.deepRelease(request.content());
				callback.onTimeout(sequence);
			}

			@Override
			public void onSuccess(FullHttpResponse fullHttpResponse) {
				ByteBufManager.deepRelease(request.content());
				callback.onSuccess(fullHttpResponse);
			}

			@Override
			public void onError(Exception e) {
				ByteBufManager.deepRelease(request.content());
				callback.onError(e);
			}
		};

		try {
			HttpHeaders headers = request.headers();

			String address = headers.get(HttpHeaders.Names.HOST);

			// #1025 通过伪协议转换为URI，避免 IPv6处理不当
			final URI uri = new URI("foo://" + address);

		}
		catch (Exception e) {
			releaseCallback.onError(e);
		}
		catch (Throwable e) {

		}

	}

}
