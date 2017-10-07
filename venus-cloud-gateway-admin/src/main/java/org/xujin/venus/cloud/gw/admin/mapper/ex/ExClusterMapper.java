package org.xujin.venus.cloud.gw.admin.mapper.ex;

import org.xujin.venus.cloud.gw.admin.entity.Cluster;
import org.xujin.venus.cloud.gw.admin.mapper.ClusterMapper;

import com.github.pagehelper.Page;

/**
 * 
 * @author xujin
 *
 */
public interface ExClusterMapper extends ClusterMapper {

	/**
	 * 分页查询数据
	 * @return
	 */
	Page<Cluster> findByPage();

}