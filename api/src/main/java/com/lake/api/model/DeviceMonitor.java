package com.lake.api.model;

import java.sql.Date;

/**
 * @author LakeHm
 *
 * 2017年1月7日下午4:14:45
 */
public class DeviceMonitor {
	private String terminal_id;
	private String img_path;
	private Date date;
	
	public String getTerminal_id() {
		return terminal_id;
	}
	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}
	public String getImg_path() {
		return img_path;
	}
	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
