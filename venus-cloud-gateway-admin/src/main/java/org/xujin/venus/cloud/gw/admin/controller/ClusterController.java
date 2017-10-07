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
import org.xujin.venus.cloud.gw.admin.model.ClusterModel;
import org.xujin.venus.cloud.gw.admin.service.ClusterService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 集群管理
 * @author xujin
 *
 */
@RestController
@RequestMapping("/admin/cluster")
public class ClusterController {

	@Autowired
	private ClusterService clusterService;

	@ApiOperation(value = "分页获取集群列表", notes = "")
	@GetMapping("/listByPage")
	ResultData getRouteInfos(
			@RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
		ResultData result = new ResultData();
		result.setData(clusterService.findByPage(pageNo, pageSize));
		return result;
	}

	@ApiOperation(value = "创建集群", notes = "根据routeInfoModel对象创建路由配置")
	@ApiImplicitParam(name = "clusterModel", value = "根据集群Model", required = true, dataType = "ClusterModel")
	@PostMapping("/add")
	public ResultData addRouteInfo(@RequestBody ClusterModel clusterModel) {
		ResultData result = new ResultData();
		clusterService.insert(clusterModel);
		return result;
	}

	@ApiOperation(value = "更新集群", notes = "根据clusterModel对象更新路由配置")
	@ApiImplicitParam(name = "clusterModel", value = "集群信息Model", required = true, dataType = "ClusterModel")
	@PostMapping("/update")
	public ResultData updateRouteInfo(@RequestBody ClusterModel clusterModel) {
		ResultData result = new ResultData();
		clusterService.update(clusterModel);
		return result;
	}

	@ApiOperation(value = "删除集群", notes = "根据集群Id逻辑删除")
	@GetMapping("/delete/{id}")
	public ResultData deleteById(@PathVariable Long id) {
		ResultData result = new ResultData();
		clusterService.deleteById(id);
		return result;
	}
}
