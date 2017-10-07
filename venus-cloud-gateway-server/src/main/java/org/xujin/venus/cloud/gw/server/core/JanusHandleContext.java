package org.xujin.venus.cloud.gw.server.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.xujin.venus.cloud.gw.server.filter.model.RouteInfo;
import org.xujin.venus.cloud.gw.server.http.HttpCode;
import org.xujin.venus.cloud.gw.server.monitor.MonitorAccessLog;
import org.xujin.venus.cloud.gw.server.netty.http.JanusRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpVersion;

/**
 * Janus处理上下文
 * @author xujin
 *
 */
public class JanusHandleContext extends ConcurrentHashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public JanusHandleContext() {
		super();
	}

	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defaultResponse) {
		Boolean b = (Boolean) get(key);
		if (b != null) {
			return b.booleanValue();
		}
		return defaultResponse;
	}

	public void set(String key) {
		put(key, Boolean.TRUE);
	}

	public void set(String key, Object value) {
		if (value != null)
			put(key, value);
		else
			remove(key);
	}

	/**
	 * returns a set throwable
	 *
	 * @return a set throwable
	 */
	public Throwable getThrowable() {
		return (Throwable) get("throwable");

	}

	public void setThrowable(Throwable th) {
		put("throwable", th);

	}

	public Boolean getIsPrintExceStackInfo() {

		return (Boolean) get("isPrint") == null ? true : (Boolean) get("isPrint");

	}

	public void setIsPrintExceStackInfo(Boolean isPrint) {
		put("isPrint", isPrint);

	}

	public String getRequestCallBack() {
		return (String) get("callback");
	}

	public void setRequestCallBack(String callbackValue) {
		set("callback", callbackValue);
	}

	public RouteInfo getAPIInfo() {
		return (RouteInfo) get("context_apiinfo");
	}

	public void setAPIInfo(RouteInfo apiInfo) {
		set("context_apiinfo", apiInfo);
	}

	// 如果之前设置过200状态，则不再对mercury的状态进行设置
	public void setMercuryHttpCode(int mercuryHttpCode) {
		if (getMercuryHttpCode() != HttpCode.HTTP_OK_CODE) {
			set("mercuryHttpCode", mercuryHttpCode);
		}

	}

	public int getMercuryHttpCode() {
		Object object = get("mercuryHttpCode");
		if (object == null) {
			return 0;
		}
		else {
			return (int) object;
		}
	}

	public void setBodySend() {
		set("bodySend", true);
	}

	public boolean isBodySend() {
		return getBoolean("bodySend");
	}

	// 设置monitor日志
	public void setMonitorAccessLog(MonitorAccessLog accessLog) {
		set("accessLog", accessLog);
	}

	// 获取monitor日志
	public MonitorAccessLog getMonitorAccessLog() {
		Object object = get("accessLog");
		if (object != null) {
			return (MonitorAccessLog) object;
		}
		else {
			return null;
		}
	}

	// 设置server的channel
	public void setInBoundChannel(Channel channel) {
		set("inBoundChannel", channel);
	}

	// 获取server的channel
	public Channel getInBoundChannel() {
		Object object = get("inBoundChannel");
		if (object != null) {
			return (Channel) object;
		}
		else {
			return null;
		}
	}

	/**
	 * @return the HttpServletRequest from the "request" key
	 */
	public JanusRequest getRequest() {
		return (JanusRequest) get("request");
	}

	public void setRequest(JanusRequest request) {
		put("request", request);
	}

	// ----------osp request
	/**
	 * osp request body
	 * @param ospRequestBodyJson 内容必须是json形式
	 */
	public void setOspRequestBodyJson(String ospRequestBodyJson) {
		set("ospRequestBodyJson", ospRequestBodyJson);
	}

	public String getOspRequestBodyJson() {
		return (String) get("ospRequestBodyJson");
	}

	/**
	 * @return 发送给osp的header
	 */
	public Map<String, String> getOspRequestHeader() {
		Map<String, String> map = (Map<String, String>) get("ospRequestHeader");
		if (map == null) {
			map = new HashMap<String, String>();
			set("ospRequestHeader", map);
		}
		return map;
	}

	// ----------rest request

	/**
	 * @param uri 需要发送到rest服务的uri
	 */
	public void setRestRequestUri(String uri) {
		set("restRequestUri", uri);
	}

	/**
	 * @return rest服务转发，从该处获取uri数据
	 */
	public String getRestRequestUri() {
		return (String) get("restRequestUri");
	}

	public void setRequestUri(String uri) {
		set("requestUri", uri);
	}

	/**
	 * @return rest服务转发，从该处获取uri数据
	 */
	public String getRequestUri() {
		return (String) get("requestUri");
	}

	/**
	 * @param modifyBodyContent 放到body的数据
	 */
	public void setRestRequestBody(ByteBuf modifyBodyContent) {
		set("restRequestBody", modifyBodyContent);

	}

	/**
	 * @return rest服务转发，从该处获取uri数据
	 */
	public ByteBuf getRestRequestBody() {
		return (ByteBuf) get("restRequestBody");
	}

	// ---------- 返回给访问端
	/**
	 * 获取返回的response对象
	 */
	public HttpHeaders getResponseHttpHeaders() {
		HttpHeaders headers = (HttpHeaders) get("responseHttpHeaders");
		if (headers == null) {
			headers = new DefaultHttpHeaders(false);
			set("responseHttpHeaders", headers);
		}
		return headers;

	}

	/**
	 * @param body 解析后的数据
	 */
	public void setResponseBody(ByteBuf body) {
		set("responseBody", body);
	}

	public ByteBuf getResponseBody() {
		return (ByteBuf) get("responseBody");
	}

	/**
	 * @param httpCode 返回的状态码
	 */
	public void setResponseHttpCode(int httpCode) {
		set("responseHttpCode", httpCode);
	}

	public int getResponseHttpCode() {
		Object object = get("responseHttpCode");
		if (object == null) {
			return HttpCode.HTTP_OK_CODE;
		}
		else {
			return (int) object;
		}
	}

	// 返回的httpVersion
	public void setResponseHttpVersion(HttpVersion httpVersion) {
		set("responseHttpVersion", httpVersion);
	}

	public HttpVersion getResponseHttpVersion() {
		HttpVersion httpVersion = (HttpVersion) get("responseHttpVersion");
		if (httpVersion == null) {
			return HttpVersion.HTTP_1_1;
		}
		else {
			return httpVersion;
		}
	}

	// -------------- rest返回的数据
	public void setRestFullHttpResponse(FullHttpResponse fullHttpResponse) {
		set("restFullHttpResponse", fullHttpResponse);
	}

	/**
	 * @return rest服务返回的response对象
	 */
	public FullHttpResponse getRestFullHttpResponse() {
		return (FullHttpResponse) get("restFullHttpResponse");
	}

	// --------------- osp返回的数据
	public void setOspResponseHeader(Map<String, String> cookie) {
		set("ospResponseHeader", cookie);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getOspResponseHeader() {
		return (Map<String, String>) get("ospResponseHeader");
	}

	public String getTokenSecret() {
		return (String) get("tokenSecret");
	}

	// 认证的秘钥
	public void setTokenSecret(String tokenSecret) {
		set("tokenSecret", tokenSecret);
	}

}
