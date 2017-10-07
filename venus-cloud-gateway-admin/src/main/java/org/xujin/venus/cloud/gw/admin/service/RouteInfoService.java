package org.xujin.venus.cloud.gw.admin.service;

import org.xujin.venus.cloud.gw.admin.base.PageInfo;
import org.xujin.venus.cloud.gw.admin.entity.RouteInfo;
import org.xujin.venus.cloud.gw.admin.exception.ApplicationException;
import org.xujin.venus.cloud.gw.admin.model.RouteInfoModel;

/**
 * 路由配置interface
 * @author xujin
 *
 */
public interface RouteInfoService {

	/**
	 * Add RouteInfo
	 * @param routeInfoModel
	 */
	public void insert(RouteInfoModel routeInfoModel) throws ApplicationException;

	/**
	 * 分页查询
	 * @param pageNo 页号
	 * @param pageSize 每页显示记录数
	 * @return
	 */
	PageInfo<RouteInfo> findByPage(int pageNo, int pageSize) throws ApplicationException;

	void update(RouteInfoModel routeInfoModel) throws ApplicationException;

	void deleteById(Long id) throws ApplicationException;
}
