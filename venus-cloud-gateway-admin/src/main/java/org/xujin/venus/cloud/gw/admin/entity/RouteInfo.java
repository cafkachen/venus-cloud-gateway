package org.xujin.venus.cloud.gw.admin.entity;

import java.util.Date;

/**
 * 路由信息
 * @author xujin
 *
 */
public class RouteInfo {
	private Long id;

	private Integer domainId;

	private String type;

	private String name;

	private String requestUrl;

	private String requestMethod;

	private String routeServiceId;

	private String routeServiceUrl;

	private String routeServicePath;

	private String routeVersion;

	private Integer wrapper;

	private String extconfig;

	private Date createTime;

	private Date updateTime;

	private Byte isDeleted;

	private String createBy;

	private String updateBy;

	private Byte flag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRouteServiceId() {
		return routeServiceId;
	}

	public void setRouteServiceId(String routeServiceId) {
		this.routeServiceId = routeServiceId;
	}

	public String getRouteServiceUrl() {
		return routeServiceUrl;
	}

	public void setRouteServiceUrl(String routeServiceUrl) {
		this.routeServiceUrl = routeServiceUrl;
	}

	public String getRouteServicePath() {
		return routeServicePath;
	}

	public void setRouteServicePath(String routeServicePath) {
		this.routeServicePath = routeServicePath;
	}

	public String getRouteVersion() {
		return routeVersion;
	}

	public void setRouteVersion(String routeVersion) {
		this.routeVersion = routeVersion;
	}

	public Integer getWrapper() {
		return wrapper;
	}

	public void setWrapper(Integer wrapper) {
		this.wrapper = wrapper;
	}

	public String getExtconfig() {
		return extconfig;
	}

	public void setExtconfig(String extconfig) {
		this.extconfig = extconfig;
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

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Byte getFlag() {
		return flag;
	}

	public void setFlag(Byte flag) {
		this.flag = flag;
	}
}