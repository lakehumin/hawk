package com.lake.api.model;

import java.sql.Date;

/**
 * @author LakeHm
 *
 * 2017年1月8日下午2:36:39
 */
public class Alarm {
	private int id;
	private String terminal_id;
	private String event;
	private String user;
	private int state;
	private String record;
	private Date eventdate;
	private Date begindate;
	private Date enddate;
	
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
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}
	public Date getEventdate() {
		return eventdate;
	}
	public void setEventdate(Date eventdate) {
		this.eventdate = eventdate;
	}
	public Date getBegindate() {
		return begindate;
	}
	public void setBegindate(Date begindate) {
		this.begindate = begindate;
	}
	public Date getEnddate() {
		return enddate;
	}
	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}
}
