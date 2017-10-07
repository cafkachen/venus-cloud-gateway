package org.xujin.venus.cloud.gw.server.startup;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.netty.ByteBufManager;
import org.xujin.venus.cloud.gw.server.netty.handler.HttpInboundHandler;
import org.xujin.venus.cloud.gw.server.netty.handler.JanusHttpContentCompressor;
import org.xujin.venus.cloud.gw.server.utils.env.ProperityConfig;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * 基于Netty的HTTP Proxy服务器
 * @author xujin
 *
 */
public class JanusNettyServer {

	private static Logger logger = LoggerFactory.getLogger(JanusNettyServer.class);

	private NioEventLoopGroup bossGroup = new NioEventLoopGroup(
			ProperityConfig.bossGroupSize,
			new DefaultThreadFactory("Janus-Http-Boss", true));
	public static NioEventLoopGroup workerGroup = new NioEventLoopGroup(
			ProperityConfig.workerGroupSize,
			new DefaultThreadFactory("Janus-Http-Worker", true));

	public volatile static boolean online = true;

	public void startServer(int noSSLPort) throws InterruptedException {

		// http请求ChannelInbound
		final HttpInboundHandler httpInboundHandler = new HttpInboundHandler();

		ServerBootstrap insecure = new ServerBootstrap();
		insecure.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				// SO_REUSEADDR,表示允许重复使用本地地址和端口
				.option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
				.option(ChannelOption.ALLOCATOR, ByteBufManager.byteBufAllocator)
				/**
				 * SO_KEEPALIVE
				 * 该参数用于设置TCP连接，当设置该选项以后，连接会测试链接的状态，这个选项用于可能长时间没有数据交流的连接。当设置该选项以后，
				 * 如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文。
				 */
				.childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
				.childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
				.childOption(ChannelOption.ALLOCATOR, ByteBufManager.byteBufAllocator)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						// 对channel监控的支持 暂不支持
						// keepalive_timeout 的支持
						pipeline.addLast(
								new IdleStateHandler(ProperityConfig.keepAliveTimeout, 0,
										0, TimeUnit.MILLISECONDS));
						// pipeline.addLast(new JanusHermesHandler());
						pipeline.addLast(new HttpResponseEncoder());
						// 经过HttpRequestDecoder会得到N个对象HttpRequest,first HttpChunk,second
						// HttpChunk,....HttpChunkTrailer
						pipeline.addLast(new HttpRequestDecoder(
								ProperityConfig.maxInitialLineLength,
								ProperityConfig.maxHeaderSize, 8192,
								ProperityConfig.validateHeaders));
						// 把HttpRequestDecoder得到的N个对象合并为一个完整的http请求对象
						pipeline.addLast(new HttpObjectAggregator(
								ProperityConfig.httpAggregatorMaxLength));

						// gzip的支持
						if (ProperityConfig.gzip) {
							pipeline.addLast(new JanusHttpContentCompressor(
									ProperityConfig.gzipLevel,
									ProperityConfig.gzipMinLength));
						}

						pipeline.addLast(httpInboundHandler);
					}
				});

		ChannelFuture insecureFuture = insecure.bind(noSSLPort).sync();

		logger.info("[listen HTTP NoSSL][" + noSSLPort + "]");

		/**
		 * Wait until the server socket is closed.</br>
		 * 找到之前的无日志打印spring 容器也没启动的原因了，集成spring boot
		 * 和eureka放上放下并不是问题，是因为JanusNettyServer服务启动后，阻塞监听端口导致的
		 **/
		insecureFuture.channel().closeFuture().sync();
		logger.info("[stop HTTP NoSSL success]");

	}

	public void shutdownBossGroup() {
		bossGroup.shutdownGracefully();
	}

	public boolean isBossGroupShuttingDown() {
		return bossGroup.isShuttingDown();
	}

	public void shutdownWorkerGroup() {
		workerGroup.shutdownGracefully();
	}

	public boolean isWorkerGroupShuttingDown() {
		return workerGroup.isShuttingDown();
	}

}
