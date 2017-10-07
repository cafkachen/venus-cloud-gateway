package org.xujin.venus.cloud.gw.admin.mapper.ex;

import java.util.List;

import org.xujin.venus.cloud.gw.admin.entity.RouteInfo;
import org.xujin.venus.cloud.gw.admin.mapper.RouteInfoMapper;

import com.github.pagehelper.Page;

public interface ExRouteInfoMapper extends RouteInfoMapper {

	public List<RouteInfo> selectAll();

	/**
	 * 分页查询数据
	 * @return
	 */
	Page<RouteInfo> findByPage();

}