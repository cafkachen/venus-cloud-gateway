package org.xujin.venus.cloud.gw.server.constant;

/**
 * xujin
 * 跟janus console的同步类型定义
 */
public enum ConfigSyncType {
	
	BOOTSTRAP("1", "启动"), NOTIFICATION("2", "zk触发");
	private String code;
	private String desc;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	private ConfigSyncType(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}
}
