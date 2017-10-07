package org.xujin.venus.cloud.gw.server.monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.netty.http.JanusRequest;

import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.util.internal.InternalThreadLocalMap;

/**
 * MonitorAccessLog
 * @author xujin
 *
 */
public class MonitorAccessLog {
	private static Logger logger = LoggerFactory.getLogger(MonitorAccessLog.class);
	private String remote_addr;
	private long startTime = System.currentTimeMillis();
	private String request; // GET /_health_check HTTP/1.0
	private String http_referer;
	private String http_user_agent; // ?
	private String http_x_forwarded_for;
	private String http_Cdn_Src_Ip;
	private String http_Cdn_Src_Port;
	private String http_host;

	private static InetAddress inetAddress = null;
	private static String hostname = "-";
	private static String host = "-"; // 暂时存放ip
	static {
		try {
			inetAddress = InetAddress.getLocalHost();
			if (inetAddress != null) {
				hostname = inetAddress.getHostName();
				host = inetAddress.getHostAddress();
			}
		}
		catch (UnknownHostException e) {

		}
	}

	public MonitorAccessLog() {

	}

	public void start(JanusRequest janusRequest) {
		remote_addr = janusRequest.remoteAddress();
		StringBuilder builder = InternalThreadLocalMap.get().stringBuilder();
		builder.append(janusRequest.getMethod()).append(" ").append(janusRequest.getUri())
				.append(" ").append(janusRequest.getHttpVersion().toString());
		request = builder.toString();
		http_referer = janusRequest.getHeader(Names.REFERER);
		http_user_agent = janusRequest.getHeader(Names.USER_AGENT);
		http_x_forwarded_for = janusRequest.getHeader("X-Forwarded-For");
		http_Cdn_Src_Ip = janusRequest.getHeader("Cdn-Src-Ip");
		http_Cdn_Src_Port = janusRequest.getHeader("X-Cdn-Src-Port");
		http_host = janusRequest.getHeader("Host");

	}

	// 需要确定的 1：时间的单位 2：是否有空格 3：host如何取值 4：cdn的数据如何获取
	public void endAndLog(int status, int mercuryCode, long body_bytes_sent) {
		long endTime = System.currentTimeMillis();
		StringBuilder accessLog = InternalThreadLocalMap.get().stringBuilder();
		// 第一部分为：http协议相关的日志
		accessLog.append("[").append(wrapValue(remote_addr)).append("\t")
				.append(wrapValue(http_x_forwarded_for)).append("\t")
				.append(wrapValue(request)).append("\t").append(wrapValue(http_referer))
				.append("\t").append(wrapValue(http_user_agent)).append("\t")
				.append(wrapValue(http_Cdn_Src_Ip)).append("\t")
				.append(wrapValue(http_Cdn_Src_Port)).append("\t").append(wrapValue(host))
				.append("\t").append(wrapValue(hostname)).append("\t")
				.append(wrapValue(http_host)).append("\t").append(body_bytes_sent)
				.append("\t").append(status).append("\t").append(mercuryCode).append("\t")
				.append(endTime - startTime).append("]");
		// .append(" ");

		// 第二部分：扩展部分
		// accessLog.append("[").append(wrapValue(wafBusRuleId)).append("\t").append(wrapValue(wafRuleaction)).append("]");
		// 本地时间 格式： 18/May/2016:10:01:29 +0800

		logger.info(accessLog.toString());

	}

	private String wrapValue(String value) {
		if (value == null) {
			return "-";
		}
		else {
			return value;
		}
	}

}