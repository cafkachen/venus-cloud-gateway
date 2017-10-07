package org.xujin.venus.cloud.gw.server.utils.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.config.vo.PublishDataVO;
import org.xujin.venus.cloud.gw.server.config.vo.PublishOperation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;


public class JsonUtils implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	private static final long serialVersionUID = -4067763405310397425L;

	public static <T> String toJson(T o) {
		GsonBuilder gb =new GsonBuilder();
		gb.disableHtmlEscaping();
		return gb.create().toJson(o);
	}

	public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
		JsonParser parser = new JsonParser();
		List<T> result = new ArrayList<T>();
		JsonArray jsAr = parser.parse(json).getAsJsonArray();

		Gson gs = new Gson();
		for (JsonElement jsIt : jsAr) {
			T obj = gs.fromJson(jsIt, clazz);
			result.add(obj);
		}
		return result;
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		Gson gs = new Gson();
		return gs.fromJson(json, clazz);
	}

	public static Map<String, String> fromJsonToFirstMap(String json) {
		Map<String, String> result = new HashMap<String, String>();
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(json).getAsJsonObject();
		Set<Entry<String, JsonElement>> set = jsonObject.entrySet();
		for (Map.Entry<String, JsonElement> entry : set) {
			JsonElement element = entry.getValue();
			if (element instanceof JsonPrimitive) {
				result.put(entry.getKey().toString(), entry.getValue().getAsString());
			} else {
				result.put(entry.getKey().toString(), entry.getValue().toString());
			}

		}
		return result;
	}

	/**
	 * 将JSONObjec对象转换成Map集合
	 * 
	 * @see JSONHelper#reflect(JSONArray)
	 * @param json
	 * @return
	 */
	public static HashMap<String, Object> reflect(JSONObject json) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Set keys = json.keySet();
		for (Object key : keys) {
			Object o = json.get(key);
			map.put((String) key, o);
		}
		return map;
	}

	// 获取的数据进行封装
	public static List<PublishDataVO> fromJsonArrayPublisVO(String json, Map<String, Class> typeMap) {
		JsonParser parser = new JsonParser();
		List<PublishDataVO> result = new ArrayList<PublishDataVO>();
		JsonArray jsAr = parser.parse(json).getAsJsonArray();

		Gson gs = new Gson();
		for (JsonElement jsIt : jsAr) {
			if (jsIt instanceof JsonObject) {
				PublishDataVO vo = fromPublisVO((JsonObject) jsIt, typeMap, gs);
				if (vo != null) {
					result.add(vo);
				}
			}
		}
		return result;
	}

	public static PublishDataVO fromJsonPublisVO(String json, Map<String, Class> typeMap) {
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(json);
		if (jsonElement instanceof JsonObject) {
			Gson gs = new Gson();
			return fromPublisVO((JsonObject) jsonElement, typeMap, gs);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	private static PublishDataVO fromPublisVO(JsonObject object, Map<String, Class> typeMap, Gson gs) {
		PublishDataVO vo = new PublishDataVO();
		String resourceId = getAndValidation(object, "resourceId");
		if (resourceId == null) {
			return null;
		}

		String type = getAndValidation(object, "type");
		if (type == null) {
			return null;
		}

		String operation = getAndValidation(object, "operation");
		if (operation == null) {
			return null;
		}
		vo.setResourceId(Long.valueOf(resourceId));
		;
		vo.setOperation(PublishOperation.valueOf(operation));
		vo.setType(type);

		Class clazz = typeMap.get(vo.getType());
		if (clazz == null) {
			logger.error(" object:" + object.toString() + "  type not exist");
			return null;
		}
		JsonElement dataElement = object.get("data");

		Object aaa = gs.fromJson(dataElement, clazz);
		vo.setData(aaa);
		return vo;
	}

	private static String getAndValidation(JsonObject object, String key) {
		if (object.get(key) == null) {
			logger.error("key" + key + " object:" + object.toString() + " not exist");
			return null;
		} else {
			return object.get(key).getAsString();
		}
	}

	public static void main(String[] args) {

		System.out.println();
	}

	static String a = "[{\"id\":\"1779\",\"type\":\"apis\",\"operation\":\"add\",\"data\":{\"domainId\":1,\"flag\":1,\"id\":1425,\"inboundMethod\":\"GET\",\"inboundService\":\"user/getusers/test\",\"name\":\"testxsdsdsdsddsd-1111\",\"outboundMethod\":\"findByPrimaryKey\",\"outboundService\":\"com.vip.venus.studentservice.service.StudService\",\"outboundVersion\":\"1.0.0\",\"serviceRouteId\":1779,\"type\":\"OSP\",\"version\":20,\"workflowId\":48,\"wrapper\":1}}]";
}
