package org.xujin.venus.cloud.gw.server.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class ByteUtils {
	public static byte[] toBytes(String str) {
		if (str == null) {
			return null;
		} else {
			return str.getBytes(CharsetUtil.UTF_8);
		}

	}

	public static ByteBuf toByteBuf(String str) {
		if (str == null) {
			return Unpooled.EMPTY_BUFFER;
		}
		return Unpooled.wrappedBuffer(ByteUtils.toBytes(str));
	}

}
