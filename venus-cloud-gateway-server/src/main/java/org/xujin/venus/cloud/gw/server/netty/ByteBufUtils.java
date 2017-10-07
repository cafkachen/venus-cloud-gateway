package org.xujin.venus.cloud.gw.server.netty;

import io.netty.buffer.ByteBuf;

/**
 * ByteBuf工具类
 * @author xujin
 *
 */
public class ByteBufUtils {

	// sequence在协议数组中的索引位置
	private static final int SEQ_INDEX = 6;

	/**
	 * 更新Protocol中的sequence
	 */
	public static void updateSeq4ByteBuf(ByteBuf buf, int sequence) {
		buf.setInt(SEQ_INDEX, sequence);
	}

	/**
	 * 获取Protocol中的sequence
	 */
	public static int getSequence(ByteBuf buf) {
		return buf.getInt(SEQ_INDEX);
	}

	/**
	 * 重设farmeLength
	 */
	public static void resetFrameLength(ByteBuf buf) {
		int length = buf.writerIndex();
		int framelength = length - 4;
		buf.setInt(0, framelength);
	}

	public static String getEigthBitsStringFromByte(int c) {
		// if this is a positive number its bits number will be less than 8
		// so we have to fill it to be a 8 digit binary string
		// b=b+100000000(2^8=256) then only get the lower 8 digit
		int b = c;
		b |= 256; // mark the 9th digit as 1 to make sure the string has at
					// least 8 digits
		String str = Integer.toBinaryString(b);
		int len = str.length();
		return str.substring(len - 8, len);
	}
}
