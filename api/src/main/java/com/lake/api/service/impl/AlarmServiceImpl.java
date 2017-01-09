package com.lake.api.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lake.api.dao.AlarmDao;
import com.lake.api.model.Alarm;
import com.lake.api.service.AlarmService;

/**
 * @author LakeHm
 *
 * 2017年1月8日下午3:16:28
 */

@Service("alarmService") 
public class AlarmServiceImpl implements AlarmService{
	
	@Resource
	private AlarmDao alarmDao;

	@Override
	public void addEvent(Alarm a) {
		alarmDao.insert(a);
	}

	@Override
	public List<Alarm> getAllEvent() {
		return alarmDao.searchAllEvent();
	}

	@Override
	public List<Alarm> getAllUnHandledEvent() {
		return alarmDao.searchAllUnHandledEvent();
	}

	@Override
	public List<Alarm> getAllHandlingEvent() {
		return alarmDao.searchAllHandlingEvent();
	}

	@Override
	public List<Alarm> getAllHandledEvent() {
		return alarmDao.searchAllHandledEvent();
	}

	@Override
	public Alarm getEventById(int id) {
		return alarmDao.searchEventById(id);
	}

	@Override
	public void setEventBeginHandle(int id, String begindate, String user,
			int state) {
		alarmDao.updateBeginHandle(id, begindate, user, state);
	}

	@Override
	public void setEventEndHandle(int id, String enddate, String record,
			int state) {
		alarmDao.updateEndHandle(id, enddate, record, state);
	}

}
