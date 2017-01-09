package com.cyt.Bean;

public class MsgTitleBean {
private int id;
private String num;
private String type;
private boolean isRead;
public MsgTitleBean(){}
public MsgTitleBean(String num,String type,boolean isRead)
{
	this.num=num;
	this.type=type;
	this.isRead=isRead;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getnum() {
	return num;
}
public void setnum(String num) {
	this.num = num;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public boolean isRead() {
	return isRead;
}
public void setRead(boolean isRead) {
	this.isRead = isRead;
}
}
