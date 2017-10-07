package org.xujin.venus.cloud.gw.admin.entity;

import java.util.Date;

/**
 * 
 * @author xujin
 *
 */
public class Cluster {
	private Long id;

	private String name;

	private String aliasName;

	private String busId;

	private String bussName;

	private String description;

	private String sysAdmin;

	private String techAdmin;

	private Date createTime;

	private Date updateTime;

	private Byte isDeleted;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getBusId() {
		return busId;
	}

	public void setBusId(String busId) {
		this.busId = busId;
	}

	public String getBussName() {
		return bussName;
	}

	public void setBussName(String bussName) {
		this.bussName = bussName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSysAdmin() {
		return sysAdmin;
	}

	public void setSysAdmin(String sysAdmin) {
		this.sysAdmin = sysAdmin;
	}

	public String getTechAdmin() {
		return techAdmin;
	}

	public void setTechAdmin(String techAdmin) {
		this.techAdmin = techAdmin;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Byte getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Byte isDeleted) {
		this.isDeleted = isDeleted;
	}
}