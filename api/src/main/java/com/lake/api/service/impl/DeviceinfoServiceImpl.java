package com.lake.api.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lake.api.dao.DeviceDetailDao;
import com.lake.api.dao.DeviceMonitorDao;
import com.lake.api.dao.DeviceinfoDao;
import com.lake.api.model.DeviceDetail;
import com.lake.api.model.DeviceMonitor;
import com.lake.api.model.Deviceinfo;
import com.lake.api.service.DeviceinfoService;

/**
 * @author LakeHm
 *
 * 2016年12月13日下午4:56:46
 */

@Service("deviceinfoService") 
public class DeviceinfoServiceImpl implements DeviceinfoService{
	
	@Resource
	private DeviceinfoDao deviceinfoDao;
	
	@Resource
	private DeviceMonitorDao deviceMonitorDao;
	
	@Resource
	private DeviceDetailDao deviceDetailDao;
	
	@Override
	public Deviceinfo getDeviceInfo(String id) {
		return deviceinfoDao.searchById(id);
	}

	@Override
	public List<Deviceinfo> getDeviceHistoryInfo(String id, String date) {
		return deviceinfoDao.searchHistoryById(id,date);
	}
	
	@Override
	public DeviceMonitor getDeviceMonitorImg(String id, String date) {
		return deviceMonitorDao.searchById(id,date);
	}

	@Override
	public List<DeviceMonitor> getDeviceHistoryMonitorImg(String id, String date) {
		return deviceMonitorDao.searchHistoryById(id,date);
	}
	
	@Override
	public List<DeviceDetail> getAllDeviceDetail() {
		return deviceDetailDao.searchAllDeviceDetail();
	}

	@Override
	public DeviceDetail getDeviceDetailById(String id) {
		return deviceDetailDao.searchDeviceDetailById(id);
	}
}
