package org.xujin.venus.cloud.gw.admin.mapper;

import org.xujin.venus.cloud.gw.admin.entity.RouteInfo;

public interface RouteInfoMapper {
	int deleteByPrimaryKey(Long id);

	int insert(RouteInfo record);

	int insertSelective(RouteInfo record);

	RouteInfo selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(RouteInfo record);

	int updateByPrimaryKey(RouteInfo record);
}