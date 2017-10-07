package org.xujin.venus.cloud.gw.server.constant;

import org.xujin.venus.cloud.gw.server.config.Config;

public class Constants {

	public static final int DUMMY_SEMAPHORE_MAX_COUNT = 60000;
	// public static final int MAX_REQUEST_SIZE = 10 * 1024 * 1024;
	public static final int MAX_REQUEST_SIZE = 500 * 1024 * 1024;
	public static final int MAX_RESPONSE_SIZE = 10 * 1024 * 1024;
	public static final int MAX_INITIALLINE_LENGTH = 4096;
	public static final int MAX_HEADER_SIZE = 16 * 1024;
	public static final int MAX_CHUNK_SIZE = 16 * 1024;
	// project config items specialization
	public static final String PRE_FILTER = "pre";
	public static final String POST_FILTER = "post";
	public static final String THRO_FILTER = "thro";
	public static final String BEFORE_FILTER = "before";
	public static final String FILTER_SPECIAL = "FILTER";
	public static final String MAX_HTTP_SESSION = "maxHttpSession";
	public static final String METRICS_URL = "metricsUrl";
	public static final String MAX_QUEUE_SIZE = "maxQueueSize";
	// public static final String CLUSTER = Config.getString("cluster");
	public static final String CLUSTER = "cluster";
	public static final String Fallback_SPECIAL = "FALLBACK";
	public static final String APPID = "appid";
	public static final String ETRACE_URL = "ELE_TRACE_URL";
	public static final String TOTAL_SHUTDOWN_PARK = "totalShutdownPark";
	public static String CONFIG_PATH = Config.getString("configPath");
	public static String URL_ROUTE_CONFIG_PATH = CONFIG_PATH + "/location.yml";
	public static String LOCAL_CONFIG = CONFIG_PATH + "/localConfig.json";

	public static volatile long SERVER_TIMEOUT_MILLIS = 1500l;// Millis
	public static volatile long GATEWAY_TIMEOUT_MILLIS = 3000l;// Millis
	public static volatile long DELAY = 500l;
	public static volatile long SLEEP_INTERVAL = 500l;

	public static String SHUTDOWN_PARK = "shutdownPark";
	public static String TIME_UNIT = "unit";
	// listener server auto close
	public static String TIMEOUT = "timeout";
	public static String WHITE_URL = "whiteUrl";
	public static String AUTO_CLOSE = "autoClose";
	public static String SEPARATOR = ",";
	public final static String IDX_PATH = "idxPath";

	// Whether we need to print host to metrics
	public static final String NEEDS_HOST = "needhost";

	// huskar configs
	public static final String HUSKAR_URL = Config.getString("ELE_HUSKAR_URL");
	public static final String HUSKAR_TOKEN = Config.getString("huskarToken");

	public static final int DAEMON_PORT = Integer
			.parseInt(Config.getString("daemonPort"));

	// ==================== loggin configuration ====================
	public static final String ROLLING_HISTORY = "rollinghistory";
}
