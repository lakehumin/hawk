package com.lake.api.model;

/**
 * @author LakeHm
 *
 * 2016年11月15日下午3:24:03
 */
public class User {
	private int id;
	private String username;
	private String password;
	private String type = "general";
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
