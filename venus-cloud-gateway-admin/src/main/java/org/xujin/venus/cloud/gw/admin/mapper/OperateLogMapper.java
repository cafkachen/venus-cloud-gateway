package org.xujin.venus.cloud.gw.admin.mapper;

import org.xujin.venus.cloud.gw.admin.entity.OperateLog;

public interface OperateLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OperateLog record);

    int insertSelective(OperateLog record);

    OperateLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OperateLog record);

    int updateByPrimaryKey(OperateLog record);
}