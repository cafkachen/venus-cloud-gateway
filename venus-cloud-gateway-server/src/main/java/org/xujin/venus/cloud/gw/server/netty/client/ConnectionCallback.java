package org.xujin.venus.cloud.gw.server.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * 客户端发送异步回调接口
 */
public interface ConnectionCallback {

	// 发送完成
	void sentComplete(Channel channel, int sequence);

	void onReceivedPacket(int sequence, ByteBuf receivedBuf);

	/** 超时的时候被调用 */
	void onTimeout(int sequence);
}
