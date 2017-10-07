package org.xujin.venus.cloud.gw.server.config.vo;

import org.xujin.venus.cloud.gw.server.filter.model.RouteInfo;

/**
 * 下发配置类型
 * @author xujin
 *
 */
public enum PublishType {
	

	APIS("apis", RouteInfo.class);
	
	private String name;
	
	private Class clazz;

	private PublishType(String name, Class clazz) {
		this.name = name;
		this.clazz = clazz;
	}

	// 普通方法
	public static Class getClass(String name) {
		for (PublishType c : PublishType.values()) {
			if (c.getName() == name) {
				return c.clazz;
			}
		}
		return null;
	}

	// get set 方法
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

}
