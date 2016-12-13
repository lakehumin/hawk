package com.cyt.Bean;

import java.io.DataInput;
import java.sql.Date;

public class Msg_Data_Bean {
	private int id;
	private String terminal_id;
	private String msg_path;
	private String img_path;
	private Date date;
	public Msg_Data_Bean(){}
	public Msg_Data_Bean(String terminal_id,String msg_path,String img_path,Date date)
	{
		this.terminal_id=terminal_id;
		this.msg_path=msg_path;
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

	public String getMsg_path() {
		return msg_path;
	}

	public void setMsg_path(String msg_path) {
		this.msg_path = msg_path;
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
