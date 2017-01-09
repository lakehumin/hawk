package com.cyt.Bean;

/**
 * @author cyt
 *
 */
public class AlarmEventBean {
private String terminal_id;
private String event;
private String eventdate;
private String eventstate;
public AlarmEventBean(){}
public AlarmEventBean(String terminal_id,String event,String eventdate,String eventstate)
{
	this.terminal_id=terminal_id;
	this.event=event;
	this.eventdate=eventdate;
	this.eventstate=eventstate;
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
public String getEventdate() {
	return eventdate;
}
public void setEventdate(String eventdate) {
	this.eventdate = eventdate;
}
public String getEventstate() {
	return eventstate;
}
public void setEventstate(String eventstate) {
	this.eventstate = eventstate;
}
}
