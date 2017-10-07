package org.xujin.venus.cloud.gw.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xujin.venus.cloud.gw.admin.base.ResultData;
import org.xujin.venus.cloud.gw.admin.model.RouteInfoModel;
import org.xujin.venus.cloud.gw.admin.service.RouteInfoService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 路由配置 Controller
 * @author xujin
 *
 */
@RestController
@RequestMapping("/admin/route")
public class RouteInfoController {

	@Autowired
	private RouteInfoService routeInfoService;

	@ApiOperation(value = "分页获取路由配置列表", notes = "")
	@GetMapping("/listByPage")
	ResultData getRouteInfos(
			@RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
		ResultData result = new ResultData();
		result.setData(routeInfoService.findByPage(pageNo, pageSize));
		return result;
	}

	@ApiOperation(value = "创建路由配置", notes = "根据routeInfoModel对象创建路由配置")
	@ApiImplicitParam(name = "routeInfoModel", value = "路由配置详细实体routeInfoModel", required = true, dataType = "RouteInfoModel")
	@PostMapping("/add")
	public ResultData addRouteInfo(@RequestBody RouteInfoModel routeInfoModel) {
		ResultData result = new ResultData();
		routeInfoService.insert(routeInfoModel);
		return result;
	}

	@ApiOperation(value = "更新路由配置", notes = "根据routeInfoModel对象更新路由配置")
	@ApiImplicitParam(name = "routeInfoModel", value = "路由配置详细实体routeInfoModel", required = true, dataType = "RouteInfoModel")
	@PostMapping("/update")
	public ResultData updateRouteInfo(@RequestBody RouteInfoModel routeInfoModel) {
		ResultData result = new ResultData();
		routeInfoService.insert(routeInfoModel);
		return result;
	}

	@ApiOperation(value = "删除路由配置", notes = "根据路由Id逻辑删除")
	@GetMapping("/delete/{id}")
	public ResultData deleteById(@PathVariable Long id) {
		ResultData result = new ResultData();
		routeInfoService.deleteById(id);
		return result;
	}
}
