package com.cyt.Bean;

public class DeviceInfoBean {
private int id;
private String terminal_id;
private String battery;
private String voltage;
private String workstate;
public DeviceInfoBean(){}
public DeviceInfoBean(String terminal_id,String battery,String voltage,String workstate)
{
	this.terminal_id=terminal_id;
	this.battery=battery;
	this.voltage=voltage;
	this.workstate=workstate;
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
public String getBattery() {
	return battery;
}
public void setBattery(String battery) {
	this.battery = battery;
}
public String getVoltage() {
	return voltage;
}
public void setVoltage(String voltage) {
	this.voltage = voltage;
}
public String getWorkstate() {
	return workstate;
}
public void setWorkstate(String workstate) {
	this.workstate = workstate;
}
}
