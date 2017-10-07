package org.xujin.venus.cloud.gw.admin.service;

import org.xujin.venus.cloud.gw.admin.base.PageInfo;
import org.xujin.venus.cloud.gw.admin.entity.Cluster;
import org.xujin.venus.cloud.gw.admin.exception.ApplicationException;
import org.xujin.venus.cloud.gw.admin.model.ClusterModel;

/**
 * 集群接口
 * 
 * @author xujin
 *
 */
public interface ClusterService {

	public void insert(ClusterModel clusterModel) throws ApplicationException;

	PageInfo<Cluster> findByPage(int pageNo, int pageSize) throws ApplicationException;

	void update(ClusterModel clusterModel) throws ApplicationException;

	void deleteById(Long id) throws ApplicationException;

}
