package org.xujin.venus.cloud.gw.server.utils.env;

import org.xujin.venus.cloud.gw.server.constant.JanusProduceConstants;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.netty.http.JanusRequest;

/**
 * 
 * @description:根据waf规则获取Ip
 * @author gavin.xu
 *
 */
public class JanusIpUtil {

	public static String getIpByWafIpRule(JanusHandleContext sessionContext) {
		String ip = null;
		String cdnSrcIp = sessionContext.getRequest().getHeader("Cdn-Src-Ip");// 默认大小写忽略
		if (cdnSrcIp == null || cdnSrcIp.length() <= 0) {
			ip = JudgeIpByXFor(sessionContext, ip);
		} else {
			ip = cdnSrcIp;
		}
		return ip;
	}

	private static String JudgeIpByXFor(JanusHandleContext sessionContext, String ip) {
		JanusRequest request = sessionContext.getRequest();
		String forwardedForIp = sessionContext.getRequest().getHeader(JanusProduceConstants.X_FORWARDED_FOR);
		if (forwardedForIp == null || forwardedForIp.length() <= 0) {
			ip = request.remoteAddress();
		} else {
			String ipStr = forwardedForIp.replaceAll(" ", "");
			if (ipStr.contains(",")) {
				String[] ipList = ipStr.split(",");
				if (ipList != null && ipList.length > 0) {
					ip = ipList[0];
				} else {
					ip = request.remoteAddress();
				}
			} else {
				ip = ipStr;
			}

		}
		return ip;
	}
}
