package org.xujin.venus.cloud.gw.admin.mapper;

import org.xujin.venus.cloud.gw.admin.entity.ClusterIp;

public interface ClusterIpMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ClusterIp record);

    int insertSelective(ClusterIp record);

    ClusterIp selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ClusterIp record);

    int updateByPrimaryKey(ClusterIp record);
}