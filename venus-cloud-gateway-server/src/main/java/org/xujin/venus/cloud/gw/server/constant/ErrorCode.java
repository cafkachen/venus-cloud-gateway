package org.xujin.venus.cloud.gw.server.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 以下错误码将直接作为HTTP状态码输出： 200：处理成功 401：需要认证 403：访问受限 405：HTTP 方法不支持（必须是GET/POST) GW的其他错误码将使用OSP预留的错误码段:
 * 560~599，作为returnCode输出 560：服务不存在（根据URL或者method, version, service找不到可用的服务） 561：无效的callback，如值为空
 * 562：应用参数错误（GW通用接口的参数错误，如认证缺少用户名密码等） 563: 缺少签名参数 564：签名失败 565：缺少时间戳 566：请求时间戳过期 567：调用次数受限
 * @author xujin
 *
 */
public class ErrorCode {
	private static Map<String, String> errorCodeMap = new HashMap<String, String>();

	public static final String UNSUPPORTED_HTTP_METHOD = "405";
	public final static String UNSUPPORTED_CONTENT_TYPE = "415";
	public final static String SERVER_ERROR = "500";

	public final static String SERVICE_NOT_EXISTS = "404";
	public final static String CALLBACK_NOT_EXISTS = "561";
	public final static String PARAMETER_NOT_NULL = "562";
	public final static String PARAMETER_INVALID = "563";

	public final static String OSP_INPUT_ILLEGAL_CODE = "RestErrorCode-3";
	public final static String OSP_SERIALIZE_FAILED_CODE = "OSP_SERIALIZE_FAILED";

	static {
		errorCodeMap.put(UNSUPPORTED_HTTP_METHOD, "unsupported http method");
		errorCodeMap.put(UNSUPPORTED_CONTENT_TYPE, "unsupported content type");
		errorCodeMap.put(SERVER_ERROR, "server error");
		errorCodeMap.put(SERVICE_NOT_EXISTS, "service not exists");
		errorCodeMap.put(CALLBACK_NOT_EXISTS, "callback not exists");
		errorCodeMap.put(PARAMETER_NOT_NULL, "parameter not null");
		errorCodeMap.put(PARAMETER_INVALID, "parameter invalid");
	}

	public static String getErrorMsg(String errorCode) {
		return errorCodeMap.get(errorCode);
	}
}
