package org.xujin.venus.cloud.gw.admin.mapper;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.xujin.venus.cloud.gw.admin.base.PageInfo;
import org.xujin.venus.cloud.gw.admin.entity.RouteInfo;
import org.xujin.venus.cloud.gw.admin.mapper.ex.ExRouteInfoMapper;
import org.xujin.venus.cloud.gw.admin.model.RouteInfoModel;
import org.xujin.venus.cloud.gw.admin.service.RouteInfoService;

import com.alibaba.fastjson.JSON;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RouteInfoMapperTest {

	@Resource
	private RouteInfoMapper routeInfoMapper;

	@Resource
	private ExRouteInfoMapper exrouteInfoMapper;

	@Autowired
	private RouteInfoService routeInfoService;

	@Test
	public void context() {
		assertNotNull(routeInfoMapper);
	}

	@Test
	public void testCountByExample() {

		RouteInfoModel model = new RouteInfoModel();
		model.setDomainId(1);
		model.setCreateBy("admin");
		model.setCreateTime(new Date());
		model.setExtconfig("");
		model.setFlag((byte) 0);
		model.setIsDeleted((byte) 0);
		model.setName("test");
		model.setRequestMethod("GET");
		model.setDomainId(1);
		model.setName("测试test");
		model.setRequestMethod("GET");

		// localhost:8081/getUserById?id=123
		model.setRequestUrl("/getUserById");
		model.setRouteServiceId("janus-provider");

		model.setRouteServicePath("/sc/order/");
		model.setRouteVersion("v1");

		model.setType("rest");
		model.setWrapper(1);
		model.setUpdateBy("admin");
		model.setRouteServiceUrl("/sc/order/");
		System.out.println(JSON.toJSONString(model));
		// RouteInfo routeInfo = BeanMapper.map(model, RouteInfo.class);
		// routeInfoMapper.insert(routeInfo);
	}

	@Test
	public void testRouteInfo() {
		List<RouteInfo> list = exrouteInfoMapper.selectAll();
		System.out.println(list.size());
	}

	@Test
	public void testSelectPage() {
		PageInfo<RouteInfo> list = routeInfoService.findByPage(1, 10);
		System.out.println(JSON.toJSONString(list));
	}

}
