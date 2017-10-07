package org.xujin.venus.cloud.gw.server.constant;

/**
 * 常量类
 * @author xujin
 *
 */
public interface Constant {

	// janus server version
	public final static String JANUS_VERSION = "";

	// 上下文的apiinfo对象
	public final static String CONTEXT_APIINFO = "context_apiinfo";

	public final static String CONTENT_TYPE_JSON = "application/json";

	public final static String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

	public final static String CALLBACK = "callback";

	// hermes name

	public final static String JANUS_WHITE_FILTE = "janus.whiteFilter";

	public final static String JANUS_CONSOLE_URL = "janus_console_url";
	public final static String CONFIG_MANAGER_PATH = "configpath";

	// hermes event
	public final static String DOMAIN_NAME_CONNECTION_EXCEPTION = "janus_admin_exception";
	public final static String CONFIG_CONNECTION_EXCEPTION = "CONFIG.CONNECTION.EXCEPTION";
	public final static String ZK_EXCEPTION = "janus_zk_connection_failed";
	public final static String ZK_LISTENER_EXCEPTION = "janus_zk_notify_exception";
	public final static String CONFIG_WHITE_EMPTY_EXCEPTION = "CONFIG.WHITE.EMPTY.EXCEPTION";

	public final static String CONFIG_ANTIBRUSH_EMPTY_EXCEPTION = "CONFIG.ANTIBRUSH.EMPTY.EXCEPTION";

	public final static String HEAD_TRACEID = "X-Traceid";
	public final static String CONFIG_WAF_EMPTY_EXCEPTION = "CONFIG.WAF.EMPTY.EXCEPTION";

	public final static String CALLBACK_STATE_true = "1";
	public final static String JANUS_WAF_FILTE_WHITE = "janus.wafFilter.white";

	public final static String JANUS_WAF_FILTE_BLACK = "janus.wafFilter.black";

	public final static String JANUS_WAF_FILTE_BLACKSTATUS = "janus.wafFilter.blackStatus";

	public final static String JANUS_AUTH_MAPI = "1";

	public final static String JANUS_SERVER_ENV = "janus.server.env";

	// netty bossGroupSize Hermes埋点
	public final static String JANUS_HERMES_BOSS_GROUP_SIZE = "janusBossGroupSize";

	// netty WorkerGroupSize Hermes埋点
	public final static String JANUS_HERMES_WORKER_GROUP_SIZE = "janusWorkerGroupSize";

	// janusGzip 状态 Hermes埋点
	public final static String JANUS_HERMES_GZIP_STATUS = "janusGzipStatus";

	// gzip_min_length Hermes埋点
	public final static String JANUS_HERMES_GZIP_MIN_LENGTH = "janusGzipMinlength";

	// keepalive超时时间 Hermes埋点
	public final static String JANUS_HERME_KEEP_ALIVE_TIMEOUT = "janusKeepAliveTimeout";

	// 连接池 MaxConnections Hermes埋点
	public final static String JANUS_HERMES_MAX_CONNECTIONS = "janusMaxConnections";

	// 连接空闲时间Hermes埋点
	public final static String JANUS_HERMES_HTTP_IDLET_IMEOUT = "janusHttpIdleTimeout";

	// aquire_timeout Hermes埋点
	public final static String JANUS_HERMES_AQUIRE_POOL_TIMEOUT = "janusAquirePoolTimeout";

	// Http Connect Timeout Hermes埋点
	public final static String JANUS_HERMES_HTTP_CONNECT_TIMEOUT = "janusHttpConnectTimeout";

	// 连接池 MaxPendings Hermes埋点
	public final static String JANUS_HERMES_MAXPENDINGS = "janusMaxPendings";

	// ServerInfo中的Ip,端口,holdingTimeout埋点
	public final static String JANUS_HERMES_SERVERINFO = "janusServerInfo";

	// RestChannelPool中的acquiredChannelCount埋点
	public final static String JANUS_HERMES_ACQUIRED_CHANNELCOUNT = "JanusAcquiredChannelCount";

	// janus对外 当前连接数Hermes埋点
	public final static String JANUS_HERMES_IN_CONNECT_COUNT = "janus_in_connect_count";

	// janus对外 超过最大水平位Hermes埋点
	public final static String JANUS_HERMES_HIGH_WATER_MARK = "janus_high_water_mark";

	// janus对外 新建连接数 Hermes埋点
	public final static String JANUS_HERMES_NEW_CONNMETER = "janusNewConnMeter";

	// janus对外 当前连接数 Hermes埋点
	public final static String JANUS_HERMES_ACTIVE_CONN_COUNTER = "janusActiveConnCounter";

	// janus对外连接时间 Hermes埋点
	public final static String JANUS_HERMES_CONN_LIVE_TIMER = "janusConnLiveTimer";

	/**
	 * ip在白名单中的key
	 */
	public final static String IP_IN_WHITE_LIST_KEY = "ipInWhiteListKey";

	// janus对外 janus主动关闭连接数 Hermes埋点
	public final static String JANUS_HERMES_CONN_IDLEMETER = "janusConnIdleCloseMeter";

	// janus对外 janus被动关闭连接数 Hermes埋点
	public final static String JANUS_HERMES_CONN_CLOSE_METER = "janusconnTotalCloseMeter";

	public final static String PULL_STATUS_SUCCESS = "2";

	public final static String PULL_STATUS_FAIL = "0";

}
