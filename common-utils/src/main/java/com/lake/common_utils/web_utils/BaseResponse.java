package com.lake.common_utils.web_utils;

/**
 * @author LakeHm
 *
 * 2016年11月26日下午1:43:17
 */
public class BaseResponse {

	private boolean success;
	private Object data;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
