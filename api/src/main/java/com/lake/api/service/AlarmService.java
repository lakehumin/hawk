package com.lake.api.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lake.api.model.Alarm;

/**
 * @author LakeHm
 *
 * 2017年1月8日下午3:13:05
 */
public interface AlarmService {

	void addEvent(Alarm a);
	
	List<Alarm> getAllEvent();
	
	List<Alarm> getAllUnHandledEvent();
	
	List<Alarm> getAllHandlingEvent();
	
	List<Alarm> getAllHandledEvent();
	
	Alarm getEventById(int id);
	
	void setEventBeginHandle(int id, String begindate, String user, int state);
	
	void setEventEndHandle(int id, String enddate, String record, int state);
}
