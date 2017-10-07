package org.xujin.venus.cloud.gw.server.filter.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author xujin
 *
 */
public class RouteInfo {

	public static final int WRAPPER_NO = 0;
	public static final int WRAPPER_STANDARD = 1;

	private Long resourceId;

	/**
	 * 对应的domainId
	 */
	private Long domainId;

	/**
	 * api协议适配的类型
	 */
	private RouteType type;

	private String name;

	private String requestUrl;

	private String requestMethod;

	private String routeServiceId;

	private String routeServicePath;

	private String routeVersion;

	private String isCallback;

	/**
	 * 是否需要包装返回
	 */
	private int wrapper = WRAPPER_STANDARD;

	public RouteType getType() {
		return type;
	}

	public void setType(RouteType type) {
		this.type = type;
	}

	public int getWrapper() {
		return wrapper;
	}

	public void setWrapper(int wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static enum RouteType {
		RPC, REST
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
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

	public String getIsCallback() {
		return isCallback;
	}

	public void setIsCallback(String isCallback) {
		this.isCallback = isCallback;
	}

}
