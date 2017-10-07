package org.xujin.venus.cloud.gw.server.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.config.event.Observable;
import org.xujin.venus.cloud.gw.server.config.vo.PublishDataVO;
import org.xujin.venus.cloud.gw.server.constant.Constant;
import org.xujin.venus.cloud.gw.server.utils.FileUtils;
import org.xujin.venus.cloud.gw.server.utils.env.EnvUtil;
import org.xujin.venus.cloud.gw.server.utils.json.JsonUtils;


/**
 * 配置管理
 * @author xujin
 *
 */
public class ConfigManager extends Observable {
	private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
	private static ConfigManager instance;
	public static String configPath = EnvUtil.getFileDir(Constant.CONFIG_MANAGER_PATH) + "config.conf";
	public static String changeIdPath = EnvUtil.getFileDir(Constant.CONFIG_MANAGER_PATH) + "change.conf";

	private ConfigManager() {
	}

	public static ConfigManager getInstance() {
		if (instance == null) {
			synchronized (ConfigManager.class) {
				if (instance == null) {
					instance = new ConfigManager();
				}
			}
		}
		return instance;
	}

	// 正常启动：获取的数据格式为：{"maxChangeId":"ssss","result":""}
	// 1:先读取result的数据
	// 2:maxChangeId
	// 3:分发result数据
	// 4:固化result数据；启动，则删除再追加。 如果不是启动，直接追加
	// 5:固化maxChangeId
	public void reflushConfig(String data, boolean isDeleteConfigFirst) throws IOException {
		if (data == null) {
			return;
		}
		Map<String, String> map = JsonUtils.fromJsonToFirstMap(data);
		// 需要本地进行固化
		String maxChangeId = map.get("maxChangeId");
		String result = map.get("result");
		List<PublishDataVO> resuleList = JsonUtils.fromJsonArrayPublisVO(result, typeMap);
		// 数据通知到业务
		notifyClassify(resuleList);
		// 进行固化
		persistConfig(resuleList, isDeleteConfigFirst);

		// 写maxChangeId每次写是覆盖的操作
		FileUtils.writeStringToFile(changeIdPath, maxChangeId);

	}

	// 固化配置
	public void persistConfig(List<PublishDataVO> resuleList, boolean isDeleteConfigFirst) throws IOException {
		if (resuleList == null || resuleList.size() == 0) {
			return;
		}
		List<String> persistList = new ArrayList<String>();
		for (PublishDataVO vo : resuleList) {
			try {
				persistList.add(JsonUtils.toJson(vo));
			} catch (Exception e) {
				logger.error("errorData:" + JsonUtils.toJson(vo), e);
			}

		}
		// 判断开始的时候是否要删除文件
		if (isDeleteConfigFirst) {
			FileUtils.deleteFile(configPath);
		}
		// 可重复写，每次写是append操作
		FileUtils.writeAppendLines(configPath, persistList);
	}

	// 用于zk触发
	public void reflushZkNotifyConfig(String data) throws IOException {
		reflushConfig(data, false);
	}

	// 用于启动触发
	public void reflushBootStrapConfig(String data) throws IOException {
		reflushConfig(data, true);
	}

	// 启动失败，从固化文件中获取的是一条一条的json数据
	public void reflushPersistConfig(List<String> persistList) {
		if (persistList == null) {
			return;
		}
		List<PublishDataVO> resuleList = new ArrayList<PublishDataVO>();
		for (String str : persistList) {
			try {
				PublishDataVO vo = JsonUtils.fromJsonPublisVO(str, typeMap);
				if (vo == null || vo.getResourceId() == null) {
					logger.error("errorData:" + JsonUtils.toJson(vo) + " orgin data:" + str);
					continue;
				}
				resuleList.add(vo);
			} catch (Exception e) {
				logger.error("fromJson Exception" + str, e);
			}
		}
		notifyClassify(resuleList);
	}

	// 进行分类通知。通知顺序为：先按照type进行分类，通知start,operation,complete 等操作
	private void notifyClassify(List<PublishDataVO> resuleList) {
		if (resuleList == null || resuleList.size() == 0) {
			return;
		}
		// 按照type进行分类
		Map<String, List<PublishDataVO>> map = new HashMap<String, List<PublishDataVO>>();
		for (PublishDataVO vo : resuleList) {
			List<PublishDataVO> list = map.get(vo.getType());
			if (list == null) {
				list = new ArrayList<PublishDataVO>();
				map.put(vo.getType(), list);
			}
			list.add(vo);
		}
		Set<String> keySet = map.keySet();
		for (String name : keySet) {
			try {
				notifyStart(name);
				List<PublishDataVO> dataList = map.get(name);
				for (PublishDataVO vo : dataList) {
					try {
						notifyObserver(vo);
					} catch (Exception e) {
						logger.error("errorData:" + JsonUtils.toJson(vo), e);
					}
				}
				notifyComplete(name);
			} catch (Exception e) {
				logger.error("exception: ", e);
			}
		}
	}

	// 从配置文件中获取固化的changeid
	public String getPersistChangeId() throws IOException {
		return FileUtils.readFileToString(changeIdPath);
	}

	// 如果获取changeid失败，返回0
	public String getPersistChangeIdQuietly() {
		try {
			return FileUtils.readFileToString(changeIdPath);
		} catch (Exception e) {
			logger.error("changeid obtain error", e);
			return "0";
		}
	}
}
