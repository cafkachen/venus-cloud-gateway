package org.xujin.venus.cloud.gw.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xujin.venus.cloud.gw.admin.base.PageInfo;
import org.xujin.venus.cloud.gw.admin.constant.Constants;
import org.xujin.venus.cloud.gw.admin.entity.RouteInfo;
import org.xujin.venus.cloud.gw.admin.exception.ApplicationException;
import org.xujin.venus.cloud.gw.admin.mapper.ex.ExRouteInfoMapper;
import org.xujin.venus.cloud.gw.admin.model.RouteInfoModel;
import org.xujin.venus.cloud.gw.admin.service.RouteInfoService;
import org.xujin.venus.cloud.gw.admin.utils.BeanMapper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * 
 * @author xujin
 *
 */
@Service
@Transactional(readOnly = true)
public class RouteInfoServiceImpl implements RouteInfoService {

	@Autowired
	private ExRouteInfoMapper routeMapper;

	@Override
	@Transactional
	public void insert(RouteInfoModel routeInfoModel) {
		RouteInfo RouteInfo = BeanMapper.map(routeInfoModel, RouteInfo.class);
		RouteInfo.setIsDeleted(Constants.IS_DELETE_FALSE);
		routeMapper.insert(RouteInfo);
	}

	@Override
	public PageInfo<RouteInfo> findByPage(int pageNo, int pageSize) {
		PageHelper.startPage(pageNo, pageSize);
		Page<RouteInfo> pagelist = routeMapper.findByPage();
		PageInfo<RouteInfo> pageInfo = new PageInfo<>(pagelist);
		return pageInfo;
	}

	@Override
	@Transactional
	public void update(RouteInfoModel routeInfoModel) throws ApplicationException {
		RouteInfo RouteInfo = BeanMapper.map(routeInfoModel, RouteInfo.class);
		routeMapper.updateByPrimaryKey(RouteInfo);
	}

	@Override
	@Transactional
	public void deleteById(Long id) throws ApplicationException {
		RouteInfo routeInfo = routeMapper.selectByPrimaryKey(id);
		if (null == routeInfo) {
			throw new ApplicationException("ECOMMON00003", null);
		}
		routeInfo.setIsDeleted(Constants.IS_DELETE_TRUE);
		routeMapper.updateByPrimaryKey(routeInfo);
	}

}
