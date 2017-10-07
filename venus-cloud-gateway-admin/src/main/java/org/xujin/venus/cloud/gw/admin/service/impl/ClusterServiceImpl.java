package org.xujin.venus.cloud.gw.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xujin.venus.cloud.gw.admin.base.PageInfo;
import org.xujin.venus.cloud.gw.admin.constant.Constants;
import org.xujin.venus.cloud.gw.admin.entity.Cluster;
import org.xujin.venus.cloud.gw.admin.exception.ApplicationException;
import org.xujin.venus.cloud.gw.admin.mapper.ex.ExClusterMapper;
import org.xujin.venus.cloud.gw.admin.model.ClusterModel;
import org.xujin.venus.cloud.gw.admin.service.ClusterService;
import org.xujin.venus.cloud.gw.admin.utils.BeanMapper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * @author xujin
 *
 */
@Service
@Transactional(readOnly = true)
public class ClusterServiceImpl implements ClusterService {

	@Autowired
	private ExClusterMapper clusterMapper;

	@Override
	@Transactional
	public void insert(ClusterModel clusterModel) throws ApplicationException {
		Cluster cluster = BeanMapper.map(clusterModel, Cluster.class);
		cluster.setIsDeleted(Constants.IS_DELETE_FALSE);
		clusterMapper.insert(cluster);

	}

	@Override
	public PageInfo<Cluster> findByPage(int pageNo, int pageSize)
			throws ApplicationException {
		PageHelper.startPage(pageNo, pageSize);
		Page<Cluster> pagelist = clusterMapper.findByPage();
		PageInfo<Cluster> pageInfo = new PageInfo<>(pagelist);
		return pageInfo;
	}

	@Override
	@Transactional
	public void update(ClusterModel clusterModel) throws ApplicationException {
		Cluster cluster = BeanMapper.map(clusterModel, Cluster.class);
		cluster.setIsDeleted(Constants.IS_DELETE_FALSE);
		clusterMapper.updateByPrimaryKey(cluster);
	}

	@Override
	@Transactional
	public void deleteById(Long id) throws ApplicationException {
		Cluster cluster = clusterMapper.selectByPrimaryKey(id);
		if (null == cluster) {
			throw new ApplicationException("ECOMMON00003", null);
		}
		cluster.setIsDeleted(Constants.IS_DELETE_TRUE);
		clusterMapper.updateByPrimaryKey(cluster);
	}

}
