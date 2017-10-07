package org.xujin.venus.cloud.gw.server.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class NettyHttpCallback extends ConnectionCallbackImpl {

	@Override
	public void sentComplete(Channel channel, int sequence) {
	}

	@Override
	public void onReceivedPacket(int sequence, ByteBuf receivedBuf) {
	}

	public abstract void onSuccess(FullHttpResponse fullHttpResponse);

	public abstract void onError(Exception e);

}