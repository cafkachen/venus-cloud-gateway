package org.xujin.venus.cloud.gw.admin.mapper;

import java.util.Date;

import org.xujin.venus.cloud.gw.admin.model.ClusterModel;

import com.alibaba.fastjson.JSON;

public class ClusterTest {

	public static void main(String[] args) {

		ClusterModel model = new ClusterModel();
		model.setId(2L);
		model.setAliasName("test");
		model.setBusId("1");
		model.setBussName("sss");
		model.setCreateTime(new Date());
		model.setDescription("sss");
		model.setName("测试集群1123");
		model.setSysAdmin("admin");
		model.setTechAdmin("admin");
		System.out.println(JSON.toJSONString(model));
	}

}
