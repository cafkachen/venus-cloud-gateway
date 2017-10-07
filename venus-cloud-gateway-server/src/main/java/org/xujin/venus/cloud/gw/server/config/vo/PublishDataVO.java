package org.xujin.venus.cloud.gw.server.config.vo;

import java.io.Serializable;

/**
 * 下发数据类型Vo
 * @author xujin
 *
 */
public class PublishDataVO implements Serializable {

	private static final long serialVersionUID = 1L;

	// 下发数据的唯一标识
	public Long resourceId;

	// type数据配型
	public String type;

	// add update del
	public PublishOperation operation;

	// 具体的数据内容,JSON数据
	public Object data;

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public PublishOperation getOperation() {
		return operation;
	}

	public void setOperation(PublishOperation operation) {
		this.operation = operation;
	}

}
