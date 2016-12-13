package com.lake.api.model;

/**
 * @author LakeHm
 *
 * 2016年12月13日下午6:08:29
 */
public class Terminaldev {
	private int id;
	private String terminal_id;
	private String tel_num;
	private String location;
	private int islinked;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTerminal_id() {
		return terminal_id;
	}
	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}
	public String getTel_num() {
		return tel_num;
	}
	public void setTel_num(String tel_num) {
		this.tel_num = tel_num;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getIslinked() {
		return islinked;
	}
	public void setIslinked(int islinked) {
		this.islinked = islinked;
	}
}
