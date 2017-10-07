package org.xujin.venus.cloud.gw.admin.base;

/**
 * 
 * @author xujin
 *
 */
public class ResultData {

	private int code = 200;

	/** 消息Key */
	private String msgCode;

	/** 消息内容 */
	private String msgContent = "success";

	/** 返回的数据 **/
	private Object data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsgCode() {
		return msgCode;
	}

	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
