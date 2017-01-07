package com.lake.api.service;

import java.util.List;

import com.lake.api.model.DeviceDetail;
import com.lake.api.model.DeviceMonitor;
import com.lake.api.model.Deviceinfo;

/**
 * @author LakeHm
 *
 * 2016年12月13日下午4:54:11
 */
public interface DeviceinfoService {

	Deviceinfo getDeviceInfo(String id);
	
	List<Deviceinfo> getDeviceHistoryInfo(String id, String date);
	
	DeviceMonitor getDeviceMonitorImg(String id, String date);
	
	List<DeviceMonitor> getDeviceHistoryMonitorImg(String id, String date);
	
	List<DeviceDetail> getAllDeviceDetail();
	
	DeviceDetail getDeviceDetailById(String id);
}
