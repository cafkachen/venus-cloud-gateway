package org.xujin.venus.cloud.gw.server.filter.model;

/**
 * 
 * @author xujin
 *
 */
public class ServiceRouteForwardMapping {

	private RouteInfo.RouteType type;// 映射类型值，只有osp,rest

	private ForwardMappingSourcePosition source;// 来源位置,包含header,parameter,cookie

	private String key;// 映射的key

	private String value;// 映射的值

	private ForwardMappingTargetPosition target;// 映射的目标 包含Header,Cookie

	public RouteInfo.RouteType getType() {
		return type;
	}

	public void setType(RouteInfo.RouteType type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ForwardMappingSourcePosition getSource() {
		return source;
	}

	public void setSource(ForwardMappingSourcePosition source) {
		this.source = source;
	}

	public ForwardMappingTargetPosition getTarget() {
		return target;
	}

	public void setTarget(ForwardMappingTargetPosition target) {
		this.target = target;
	}

	public static enum ForwardMappingSourcePosition {
		Header, Parameter, Cookie
	}

	public static enum ForwardMappingTargetPosition {
		Header, Cookie
	}
}
