package org.xujin.venus.cloud.gw.server.utils;

import java.io.IOException;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.constant.ConfigSyncType;
import org.xujin.venus.cloud.gw.server.constant.Constant;
import org.xujin.venus.cloud.gw.server.utils.env.EnvUtil;
import org.xujin.venus.cloud.gw.server.utils.env.ProperityConfig;
import org.xujin.venus.cloud.gw.server.utils.json.JsonUtils;

public class HttpUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	public static String requestGet(String url) throws Exception {
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(ProperityConfig.httpClientToConsoleReadTimeout)
				.setConnectTimeout(ProperityConfig.httpClientToConsoleConnectionTimeout)
				.setConnectionRequestTimeout(ProperityConfig.httpClientToConsoleRequstTimeout).build();
		CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();

		try {
			// 创建httpget.
			HttpGet httpget = new HttpGet(url);
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String result = EntityUtils.toString(entity, Consts.UTF_8);
					if (logger.isDebugEnabled()) {
						logger.debug(result);
					}
					return result;
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
		return null;
	}

	public static String requestConfigForBootStrap() throws Exception {
		String httpResult = requestGetForConfig(null, ConfigSyncType.BOOTSTRAP);
		return getConfigData(httpResult);
	}

	public static String requestConfigForZkNotify(String changeId) throws Exception {
		String httpResult = requestGetForConfig(changeId, ConfigSyncType.NOTIFICATION);
		return getConfigData(httpResult);
	}

	public static String notifyJanusadminByChangeId(String changeId, String status) throws Exception {
		String url = EnvUtil.getValue(Constant.JANUS_CONSOLE_URL);
		if (url == null) {
			throw new Exception(Constant.JANUS_CONSOLE_URL + " not config");
		}
		url = "http://" + url.trim() + "/query/notifyAdmin?changeId=" + changeId + "&status=" + status;
		String result = requestGet(url);
		return getConfigData(result);
	}

	public static String requestGetForConfig(String changeId, ConfigSyncType syncType) throws Exception {
		String url = EnvUtil.getValue(Constant.JANUS_CONSOLE_URL);
		if (url == null) {
			throw new Exception(Constant.JANUS_CONSOLE_URL + " not config");
		}

		url = "http://" + url.trim() + "/query/configs?version=" + Constant.JANUS_VERSION + "&type="
				+ syncType.getCode();

		if (changeId == null) {
			return requestGet(url);
		} else {
			return requestGet(url + "&oldChangeId=" + changeId);
		}

	}

	private static String requestForDomain() throws Exception {
		String url = EnvUtil.getValue(Constant.JANUS_CONSOLE_URL);
		if (url == null) {
			throw new Exception(Constant.JANUS_CONSOLE_URL + " not config");
		}
		url = "http://" + url.trim() + "/query/cluster?version=" + Constant.JANUS_VERSION;
		String result = requestGet(url);

		return result;
	}
	public static String requestDomain() throws Exception {
		String httpResult = requestForDomain();
		return getConfigData(httpResult);
	}

	public static String requestForRuleConfigData() throws Exception {
		String result = requestForTestConfig();
		return getConfigData(result);
	}

	private static String getConfigData(String httpResult) throws Exception {
		Map<String, String> map = JsonUtils.fromJsonToFirstMap(httpResult);
		if (map.get("status").equals("200") && map.get("code").equals("0") && map.get("data") != null) {
			return map.get("data");
		} else {
			logger.error("obtain config data error :" + httpResult);
			throw new Exception("obtain config data error");
		}
	}

	public static String requestForTestConfig() {
		return "{" + "	\"status\": 200," + "	\"code\": \"0\"," + "	\"message\": \"\"," + "	\"data\":{"
				+ "		\"maxChangeId\":111," + "		\"result\":[" + "			{"
				+ "				\"resourceId\": 1779," + "				\"type\": \"apis\","
				+ "				\"operation\": \"add\"," + "				\"data\": {"
				+ "					\"domainId\" : 1," + "					\"inboundMethod\" : \"GET\","
				+ "					\"inboundService\" : \"user/getusers/test\","
				+ "					\"outboundMethod\" : \"findByPrimaryKey\","
				+ "					\"outboundService\" : \"com.vip.venus.studentservice.service.StudService\","
				+ "					\"outboundVersion\" : \"1.0.0\"," + "					\"type\" : \"OSP\","
				+ "					\"isCallback\" : \"0\"," + "					\"signMethod\" : \"0\","
				+ "					\"wrapper\" : 1" + "				}" + "			}"

				+ "		]" + "	}" + "}";

	}

	public static void main(String[] args) throws Exception {
		System.out.println(requestGet("http://101.101.51.94:8080/query/configs?domainName=dev.janus.com"));
	}
}