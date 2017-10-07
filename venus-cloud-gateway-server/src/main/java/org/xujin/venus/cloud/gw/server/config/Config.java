package org.xujin.venus.cloud.gw.server.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author xujin
 *
 */
public class Config {

	private static Logger logger = LoggerFactory.getLogger(Config.class);

	private static final String DEFAULT_FILE = System.getProperty("user.dir")
			+ "/conf/config.properties";
	private static Properties properties = new Properties();

	private static Map<String, String> dynamicConfig = new HashMap<>();

	static {
		try {
			properties.load(new FileInputStream(DEFAULT_FILE));
		}
		catch (IOException e) {
			logger.error("load config.properties err", e);
			throw new RuntimeException("load config.properties err", e);
		}
	}

	public static String getString(String key) {
		if (filterDynamic(key)) {
			return getDynamicParam(key);
		}
		return properties.getProperty(key);
	}

	public static int getInt(String key) {
		return Integer.parseInt(getString(key));
	}

	public static boolean filterDynamic(String key) {
		return dynamicConfig.containsKey(key);
	}

	public static String getDynamicParam(String key) {
		return dynamicConfig.get(key);
	}

	public static void setDynamicConfig(String key, String value) {
		dynamicConfig.put(key, value);
	}
}
