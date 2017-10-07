package org.xujin.venus.cloud.gw.server.utils.env;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

public class EnvUtil {
	private final static String APP_PROPERTIES = "application.properties";
	private final static Logger logger = LoggerFactory.getLogger(EnvUtil.class);
	private static Properties applicationProperties;

	static {
		applicationProperties = getProperties(APP_PROPERTIES);
	}

	// 线上要求参数必须要大写。 所以这个会进行转换。
	public static String getValue(String key, String defaultVal) {
		String val = getSystem(key);
		if (val != null)
			return val;
		val = getEnv(key);
		if (val != null)
			return val;
		val = getProp(key);
		if (val != null) {
			return val;
		}
		return defaultVal;
	}

	private static String getSystem(String key) {

		String val = System.getProperty(key);
		if (val != null)
			return val;
		String capitalKey = key.toUpperCase();
		val = System.getProperty(capitalKey);
		return val;
	}

	private static String getEnv(String key) {

		String val = System.getenv(key);
		if (val != null)
			return val;
		String capitalKey = key.toUpperCase();
		val = System.getenv(capitalKey);
		return val;
	}

	private static String getProp(String key) {

		String val = applicationProperties.getProperty(key);
		if (val != null)
			return val;
		String capitalKey = key.toUpperCase();
		val = applicationProperties.getProperty(capitalKey);
		return val;
	}

	public static String getValue(String key) {
		return getValue(key, null);
	}

	public static String getFileDir(String key) {
		String dir = getValue(key);
		if (dir == null) {
			return dir;
		}
		else {
			if (dir.endsWith("/")) {
				return dir;
			}
			else {
				return dir + "/";
			}
		}
	}

	public static String getValue(Enum<?> key, String defaultVal) {
		return getValue(key.name(), defaultVal);
	}

	public static String getValue(Enum<?> key) {
		return getValue(key.name(), null);
	}

	public static boolean getBoolean(String key, String defaultVal) {
		try {
			return Boolean.valueOf(getValue(key, defaultVal));
		}
		catch (Exception e) {
			return false;
		}
	}

	public static int getInt(String key, int defaultVal) {
		try {
			String value = getValue(key);
			if (value == null) {
				return defaultVal;
			}
			else {
				return Integer.valueOf(value).intValue();
			}

		}
		catch (Exception e) {
			return defaultVal;
		}
	}

	public static Properties getProperties(String resource) {
		InputStream in = null;
		Properties properties = new Properties();
		try {
			ClassLoader cl = EnvUtil.class.getClassLoader();
			Enumeration<URL> urls = cl.getResources(resource);
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				logger.info("getProperties: url=" + url);
				if (url.getPath().indexOf("osp-engine.jar!") >= 0) // A temp work around
																	// as osp-engine
																	// contains
																	// application.properties
																	// as well.
					continue;
				byte[] bytes = Resources.toByteArray(url);
				properties.load(new ByteArrayInputStream(bytes));
			}

			//
			// if (cl.getResource(resource) != null) {
			// in = cl.getResourceAsStream(resource);
			// properties.load(in);
			// }
		}
		catch (Exception e) {
			logger.error("Error load: " + resource, e);
		}
		finally {
			if (in != null)
				try {
					in.close();
				}
				catch (IOException e) {
					logger.error("error close stream", e);
				}
		}
		return properties;
	}

}