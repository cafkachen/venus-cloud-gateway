package org.xujin.venus.cloud.gw.admin.mapper;

import org.xujin.venus.cloud.gw.admin.entity.Domain;

public interface DomainMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Domain record);

    int insertSelective(Domain record);

    Domain selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Domain record);

    int updateByPrimaryKey(Domain record);
}