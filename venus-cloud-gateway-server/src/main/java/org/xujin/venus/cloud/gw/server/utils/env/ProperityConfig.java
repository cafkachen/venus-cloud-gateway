package org.xujin.venus.cloud.gw.server.utils.env;

public class ProperityConfig {

	// server端 boss的线程个数默认为core/2
	public static int bossGroupSize = EnvUtil.getInt("janus_boss_size",
			Runtime.getRuntime().availableProcessors() / 2);

	// worker的个数默认为core的个数
	public static int workerGroupSize = EnvUtil.getInt("janus_worker_size",
			Runtime.getRuntime().availableProcessors());

	// TCP包组成HTTP包的最大长度，默认为64M
	public static int httpAggregatorMaxLength = EnvUtil
			.getInt("janus_http_aggregator_maxlength", 1024 * 1024 * 64);

	// http 单个ip连接池的最大连接数
	public static final Integer maxConnections = EnvUtil
			.getInt("janus_http_pool_maxConnections", 1000);

	// http 单个ip连接池,如果连接池已经满了，设置等待获取的个数
	public static final Integer maxPendings = EnvUtil.getInt("janus_http_pool_maxPending",
			Integer.MAX_VALUE);

	// http 单个ip连接池。拿连接的使用时间(默认为5000ms) 功能待实现。。。。。。。。
	public static final Integer maxHolding = EnvUtil.getInt("janus_http_pool_maxHolding",
			5000);

	// 连接http 最大超时时间 默认单元是 ms
	public static final Integer httpConnectTimeout = EnvUtil
			.getInt("janus_http_connection_timeout", 5000);

	// http connector获取连接池的超时时间 ms
	public static final Integer aquirePoolTimeout = EnvUtil
			.getInt("janus_http_pool_aquire_timeout", 5000);

	public static final Integer httpIdleTimeout = EnvUtil
			.getInt("janus_http_pool_idle_timeout", 30 * 60 * 1000);

	// http server的参数:maxInitialLineLength 长度，超过的话抛出 TooLongFrameException
	public static final Integer maxInitialLineLength = EnvUtil
			.getInt("janus_http_server_maxInitialLineLength", 4096);

	// http server的参数:header 长度，超过的话抛出 TooLongFrameException (处理方式和maxInitialLineLength一致)
	public static final Integer maxHeaderSize = EnvUtil
			.getInt("janus_http_server_maxHeaderSize", 8192);

	// http server参数：对header的name和value进行校验。 一般校验特殊字符或者非空 。 是针对set？？还需要调研
	public static final Boolean validateHeaders = EnvUtil
			.getBoolean("janus_http_server_validateHeaders", "true");

	// http server：
	public static final Integer keepAliveTimeout = EnvUtil
			.getInt("janus_http_server_keepalive_timeout", 75 * 1000);

	// http server的参数: gzip参数,默认为true,主要是针对内部RPC的访问
	public static final Boolean gzip = EnvUtil.getBoolean("janus_http_server_gzip",
			"true");

	public static final Integer gzipMinLength = EnvUtil
			.getInt("janus_http_server_gzip_min_length", 1 * 1024);
	// 压缩级别
	public static final Integer gzipLevel = EnvUtil
			.getInt("janus_http_server_gzip_comp_level", 1);

	public final static Boolean validateCookie = EnvUtil.getBoolean("janus_valid_cookie",
			"true");

	public final static Boolean validateCallBack = EnvUtil
			.getBoolean("janus_valid_callback", "true");

	// httpclient访问console 的read超时
	public static final Integer httpClientToConsoleReadTimeout = EnvUtil
			.getInt("janus_console_socket_timeout", 5000);
	public static final Integer httpClientToConsoleConnectionTimeout = EnvUtil
			.getInt("janus_console_connection_timeout", 5000);
	public static final Integer httpClientToConsoleRequstTimeout = EnvUtil
			.getInt("janus_console_request_timeout", 5000);

	public static final Integer healthCheckInvalidTime = EnvUtil
			.getInt("janus_health_invalid_time", 10 * 1000);
	public static final Integer janusHttpRetryNum = EnvUtil.getInt("janus_http_retry_num",
			1);

	public static final String janusOauthUrl = EnvUtil.getValue("janus_oauth_url"); // 访问oauth的url
	public static final String janusMiUrl = EnvUtil.getValue("janus_mi_url"); // 访问mi的url
	public static final String janusMobileCCUrl = EnvUtil.getValue("janus_mobile_cc_url"); // 访问mobile_cc的url
	public final static String janusMobileCCEnv = EnvUtil.getValue("janus_mobile_cc_env",
			"pro");

	public final static String janusMobileCCTimerPeriod = EnvUtil
			.getValue("janus_mobile_cc_timer_period", "1800"); // 默认为30分钟，单位为s

	public final static String janusReturnOkUrl = EnvUtil.getValue("janus_return_ok_url");

	public final static Integer janusHttpPoolOauthMaxHolding = EnvUtil
			.getInt("janus_http_pool_oauth_maxHolding", 500);

	public static final String janusMapiDeviceUrl = EnvUtil
			.getValue("janus_mapi_device_url");

}
