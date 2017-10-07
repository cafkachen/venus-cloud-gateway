package org.xujin.venus.cloud.gw.server.netty.handler;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.util.ReferenceCountUtil;

public class JanusHttpContentCompressor extends HttpContentCompressor {

	private int gzipMinLength = -1; // 默认为全部压缩

	public JanusHttpContentCompressor() {
		super();
	}

	public JanusHttpContentCompressor(int compressionLevel, int windowBits, int memLevel) {
		super(compressionLevel, windowBits, memLevel);
	}

	public JanusHttpContentCompressor(int compressionLevel) {
		super(compressionLevel);
	}

	public JanusHttpContentCompressor(int compressionLevel, int gzipMinLength) {
		super(compressionLevel);
		this.gzipMinLength = gzipMinLength;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
		try {
			super.encode(ctx, msg, out);
		} catch (IllegalStateException e) {
			out.add(ReferenceCountUtil.retain(msg));
		}

	}

	@Override
	protected Result beginEncode(HttpResponse response, String acceptEncoding) throws Exception {
		if (gzipMinLength == -1) {
			return super.beginEncode(response, acceptEncoding);
		} else {
			String contentLengthStr = response.headers().get(Names.CONTENT_LENGTH);
			if (contentLengthStr != null) {
				int contentLength = Integer.valueOf(contentLengthStr);
				if (contentLength < gzipMinLength) {
					return null;
				}
			}
			return super.beginEncode(response, acceptEncoding);
		}

	}

}
