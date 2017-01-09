package com.cyt.Bean;
import java.sql.Date;

public class MsgDataBean {
	private int id;
	private String terminal_id;
	private String img_path;
	private Date date;
	public MsgDataBean(){}
	public MsgDataBean(String terminal_id,String img_path,Date date)
	{
		this.terminal_id=terminal_id;
		this.img_path=img_path;
		this.date=date;
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
