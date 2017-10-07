package org.xujin.venus.cloud.gw.admin.mapper;

import org.xujin.venus.cloud.gw.admin.entity.Cluster;

public interface ClusterMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Cluster record);

    int insertSelective(Cluster record);

    Cluster selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Cluster record);

    int updateByPrimaryKey(Cluster record);
}