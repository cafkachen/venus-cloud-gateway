package org.xujin.anus.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xujin.venus.cloud.gw.server.config.ConfigManager;
import org.xujin.venus.cloud.gw.server.config.vo.PublishDataVO;
import org.xujin.venus.cloud.gw.server.config.vo.PublishOperation;
import org.xujin.venus.cloud.gw.server.config.vo.PublishType;
import org.xujin.venus.cloud.gw.server.filter.model.RouteInfo;
import org.xujin.venus.cloud.gw.server.filter.model.RouteInfo.RouteType;
import org.xujin.venus.cloud.gw.server.utils.FileUtils;
import org.xujin.venus.cloud.gw.server.utils.json.JsonUtils;

public class PublishDataVOTest {

	public static void main(String[] args) {

		List<PublishDataVO> list = new ArrayList<PublishDataVO>();

		PublishDataVO pdo = new PublishDataVO();
		pdo.setResourceId(1L);
		pdo.setOperation(PublishOperation.add);
		pdo.setType(PublishType.APIS.getName());

		RouteInfo routeInfo = new RouteInfo();
		routeInfo.setDomainId(1L);
		routeInfo.setIsCallback("1");
		routeInfo.setName("测试test");
		routeInfo.setRequestMethod("GET");

		// localhost:8081/getUserById?id=123
		routeInfo.setRequestUrl("/getUserById");
		routeInfo.setRouteServiceId("janus-provider");

		routeInfo.setRouteServicePath("/sc/order/");
		routeInfo.setRouteVersion("v1");

		routeInfo.setType(RouteType.REST);
		routeInfo.setWrapper(1);

		pdo.setData(routeInfo);

		list.add(pdo);

		PublishDataVO pdo1 = new PublishDataVO();
		pdo1.setResourceId(2L);
		pdo1.setOperation(PublishOperation.add);
		pdo1.setType(PublishType.APIS.getName());

		RouteInfo routeInfo1 = new RouteInfo();
		routeInfo1.setDomainId(1L);
		routeInfo1.setIsCallback("1");
		routeInfo1.setName("测试POST");
		routeInfo1.setRequestMethod("POST");

		// localhost:8081/getUserById?id=123
		routeInfo1.setRequestUrl("/testPost");
		routeInfo1.setRouteServiceId("janus-provider");

		routeInfo1.setRouteServicePath("/sc/post");
		routeInfo1.setRouteVersion("v1");

		routeInfo1.setType(RouteType.REST);
		routeInfo1.setWrapper(0);

		pdo1.setData(routeInfo1);

		list.add(pdo1);

		List<String> persistList = new ArrayList<String>();
		for (PublishDataVO vo : list) {
			try {
				persistList.add(JsonUtils.toJson(vo));
			}
			catch (Exception e) {
			}

		}
		// 可重复写，每次写是append操作
		try {
			FileUtils.writeAppendLines(ConfigManager.configPath, persistList);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

}
