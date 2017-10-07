package org.xujin.venus.cloud.gw.server.startup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.VenusCloudGatewayServerAppliaction;
import org.xujin.venus.cloud.gw.server.compiler.JDKLoader;
import org.xujin.venus.cloud.gw.server.config.ConfigManager;
import org.xujin.venus.cloud.gw.server.config.FilterModel;
import org.xujin.venus.cloud.gw.server.constant.Constant;
import org.xujin.venus.cloud.gw.server.filter.base.DefaultFilterPipeLine;
import org.xujin.venus.cloud.gw.server.filter.base.EventAbstractFilter;
import org.xujin.venus.cloud.gw.server.filter.base.Filter;
import org.xujin.venus.cloud.gw.server.filter.dynamic.FilterRegistry;
import org.xujin.venus.cloud.gw.server.filter.error.ErrorFilter;
import org.xujin.venus.cloud.gw.server.filter.post.JanusResponserFilter;
import org.xujin.venus.cloud.gw.server.filter.post.ResponseSendFilter;
import org.xujin.venus.cloud.gw.server.filter.pre.HttpProtocolCheckFilter;
import org.xujin.venus.cloud.gw.server.filter.pre.ProtocolAdaptorFilter;
import org.xujin.venus.cloud.gw.server.filter.pre.RouteMappingFilter;
import org.xujin.venus.cloud.gw.server.filter.rest.RestInvokerFilter;
import org.xujin.venus.cloud.gw.server.filter.rest.RestRequestBodyFilter;
import org.xujin.venus.cloud.gw.server.filter.rest.RestRequestHeaderFilter;
import org.xujin.venus.cloud.gw.server.filter.rest.RestRequestUriFilter;
import org.xujin.venus.cloud.gw.server.filter.rest.RestReturnResponseFilter;
import org.xujin.venus.cloud.gw.server.utils.FileUtils;
import org.xujin.venus.cloud.gw.server.utils.HttpUtils;
import org.xujin.venus.cloud.gw.server.utils.env.EnvUtil;
import org.xujin.venus.cloud.gw.server.utils.json.JsonUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class JanusBootStrap {
	private static Logger logger = LoggerFactory.getLogger(JanusBootStrap.class);
	public static String appName = "";

	public static void initGateway() throws Exception {
		// envConfig
		envConfig();
		// 初始化静态Filter
		initStaticFilters();

		/**
		 * 初始化动态Filters initDynamicFilter();
		 */
		// 从admin或者本地加载配置
		initConfigManager();

	}

	/**
	 * 先从console读取，如果读取不到，再从本地读取。 会将domain进行本地固化
	 */
	public static void envConfig() {
		String responseData = null;
		String domainPath = EnvUtil.getFileDir(Constant.CONFIG_MANAGER_PATH)
				+ "clusterdomain.conf";
		try {
			responseData = HttpUtils.requestDomain();
			Map<String, String> dataMap = JsonUtils.fromJsonToFirstMap(responseData);
			if (!dataMap.containsKey("clusterName")) {
				throw new Exception(
						"Get cluster name from admin fail . Response dosen't contains clusterName .");
			}
			appName = dataMap.get("clusterName");

			FileUtils.writeStringToFile(domainPath, responseData);
		}
		catch (Exception e) {
			logger.error("console appname is not connection then load from file .");
			logger.error(e.getMessage(), e);
			try {
				responseData = FileUtils.readFileToString(domainPath);
				Map<String, String> dataMap = JsonUtils.fromJsonToFirstMap(responseData);
				if (!dataMap.containsKey("clusterName")) {
					throw new Exception(
							"Get cluster name from admin fail . Response dosen't contains clusterName .");
				}
				appName = dataMap.get("clusterName");
			}
			catch (Exception e1) {
				logger.error("can not load appname from file : ", e1.getMessage());
				logger.error(e1.getStackTrace().toString());

			}
		}
		logger.info("domainName:" + appName);
		if (appName == null || appName.length() == 0) {
			try {

				FileUtils.writeStringToFile(domainPath, "domain not exists");
				VenusCloudGatewayServerAppliaction.fail("domain not exists");
			}
			catch (IOException e) {
				logger.error(e.getStackTrace().toString());
			}

		}

	}

	/**
	 * 启动时初始化动态Filter </br>
	 * 1.先连接远端获取Filter信息，连接成功动态加载Filter，更新持久化文件 </br>
	 * 2.启动连接远端Filter失败，读取本地Filter配置，加载Filter
	 */
	@SuppressWarnings("rawtypes")
	public static void initDynamicFilter() {
		try {
			File localConfigFile = new File(
					EnvUtil.getFileDir(Constant.CONFIG_MANAGER_PATH) + "filter.conf");
			if (!localConfigFile.exists()) {
				throw new Exception("local config not exists! gateway start failed!");
			}
			String localContent = Files.toString(localConfigFile, Charsets.UTF_8);
			Map<String, Object> configMap = JSON.parseObject(localContent,
					new TypeReference<Map<String, Object>>() {
					});

			for (Map.Entry<String, Object> entry : configMap.entrySet()) {
				if (entry.getKey().endsWith(".java")) {
					File javaFile = new File((String) entry.getValue());
					if (javaFile.exists()) {
						JDKLoader.getInstance().putFilterClass(javaFile);
					}
				}
			}

			Map<String, FilterModel> filterMetaMap = JSON.parseObject(
					configMap.get("filters").toString(),
					new TypeReference<Map<String, FilterModel>>() {
					});

			List<FilterModel> filterMetas = new ArrayList<>(filterMetaMap.values());
			FilterRegistry.INSTANCE.initFilter(filterMetas);
		}
		catch (Exception e) {
			VenusCloudGatewayServerAppliaction.fail("config load from console and local all failed");
		}
	}

	@SuppressWarnings("rawtypes")
	private static void initStaticFilters() {
		// 前置流程
		DefaultFilterPipeLine.getInstance().addLastSegment(new HttpProtocolCheckFilter(),
				new RouteMappingFilter(), new ProtocolAdaptorFilter());

		// rest流程
		DefaultFilterPipeLine.getInstance().addLastSegment(new RestRequestUriFilter(),
				new RestRequestBodyFilter(), new RestRequestHeaderFilter(),
				new RestInvokerFilter(), new RestReturnResponseFilter());

		// 后置流程
		DefaultFilterPipeLine.getInstance().addLastSegment(
				new JanusResponserFilter(), new ResponseSendFilter());

		// 错误处理
		DefaultFilterPipeLine.getInstance().addLastSegment(new ErrorFilter());

		// 注册监听事件
		for (Filter filter : DefaultFilterPipeLine.getInstance().getAllFilter()) {
			filter.init();
			if (filter instanceof EventAbstractFilter) {
				EventAbstractFilter eventFilter = (EventAbstractFilter) filter;
				if (eventFilter.getPublishType() != null) {
					ConfigManager.getInstance().addObserver(eventFilter);

				}
			}

		}

	}

	public static void initConfigManager() {

		ConfigManager manager = ConfigManager.getInstance();
		try {
			// 从admin获取数据
			// 固化本地
			String ruleConfigData = HttpUtils.requestConfigForBootStrap();
			manager.reflushBootStrapConfig(ruleConfigData);
		}
		catch (Exception e) {
			// 如果网路等异常
			logger.error("console config is not connection then load from file .");
			logger.error(e.getMessage(), e);
			try {
				List<String> persistList = FileUtils.readLines(ConfigManager.configPath);
				manager.reflushPersistConfig(persistList);
			}
			catch (IOException e1) {
				logger.error("can not load config from file ." + ConfigManager.configPath,
						e1);
				VenusCloudGatewayServerAppliaction
						.fail("config load from console and local all failed");
			}
		}

	}

	private static void initZk() {
		/**
		 * try { ZkClient.getInstance().addNode();
		 * ZkClient.getInstance().addConfigListener(); } catch (Exception e) {
		 * logger.error("add zk node failed .", e); }
		 */
	}

	public static void loadFilter() throws IOException {

		try {
			String filterJavaPath = EnvUtil.getFileDir(Constant.CONFIG_MANAGER_PATH)
					+ "plugin/filters/";
			String path = filterJavaPath + "HttpProtocolCheckFilter.java";
			File javaFile = new File(path);
			if (!javaFile.exists()) {
				Files.createParentDirs(javaFile);
				javaFile.createNewFile();
			}
			JDKLoader.getInstance().putFilterClass(javaFile);

			List<FilterModel> filters = new ArrayList();
			FilterModel filterModel = new FilterModel();
			filterModel.setFilterSwitch(true);
			filterModel.setFilterType("pre");
			filterModel.setKey("HttpProtocolCheckFilter");
			filterModel.setTimeout("30");
			filters.add(filterModel);
			FilterRegistry.INSTANCE.initFilter(filters);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
