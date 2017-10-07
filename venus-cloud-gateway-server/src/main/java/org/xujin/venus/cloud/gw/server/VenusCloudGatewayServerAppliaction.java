package org.xujin.venus.cloud.gw.server;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.xujin.venus.cloud.gw.server.startup.JanusBootStrap;
import org.xujin.venus.cloud.gw.server.startup.JanusNettyServer;

/**
 * Janus Server Gateway主程序入口
 * @author xujin
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class VenusCloudGatewayServerAppliaction {

	private static Logger logger = LoggerFactory.getLogger(VenusCloudGatewayServerAppliaction.class);

	// 非SSL的监听HTTP端口
	public static int httpPort = 8081;

	public static void main(String[] args) throws Exception {

		// ConfigurableApplicationContext context =
		SpringApplication.run(VenusCloudGatewayServerAppliaction.class, args);

		// logger.info("services: {}", context.getBean("discoveryClient",
		// DiscoveryClient.class).getServices());

		logger.info("Gateway Server Application Start...");
		// 解析启动参数
		parseArgs(args);

		// 初始化网关Filter和配置
		logger.info("init Gateway Server ...");
		JanusBootStrap.initGateway();

		logger.info("start netty  Server...");
		final JanusNettyServer gatewayServer = new JanusNettyServer();
		// 启动HTTP容器
		gatewayServer.startServer(httpPort);

	}

	private static void parseArgs(String[] args) throws MalformedURLException {

		if (args != null && args.length > 1) {
			for (int i = 0; i < args.length; i += 2) {
				String paraName = args[i];
				String paraVal = null;
				if ((i + 1) >= args.length) {
					fail("param value must be set.");
					break;
				}
				else {
					paraVal = args[i + 1];
				}

				if ("-httpPort".equals(paraName)) {
					if (paraVal.matches("[0-9]*")) {
						httpPort = Integer.parseInt(paraVal);
					}
					else {
						fail("httpPort must be a int.");
						break;
					}
				}
			}
		}
	}

	public static void fail(String message) {
		System.out.println("Error: " + message);
		System.exit(-1);
	}
}
