package com.cyt.Bean;

public class TerminalDevBean {
	private int id;
	private String terminal_id;
	private String tel_num;
	private String location;
	private boolean islinked;
	public TerminalDevBean(){}
	public TerminalDevBean(String terminal_id,String tel_num,String location,boolean islinked)
	{
		this.terminal_id=terminal_id;
		this.tel_num=tel_num;
		this.islinked=islinked;
		this.location=location;
	}
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
	public boolean isIslinked() {
		return islinked;
	}
	public void setIslinked(boolean islinked) {
		this.islinked = islinked;
	}
	
	
}
