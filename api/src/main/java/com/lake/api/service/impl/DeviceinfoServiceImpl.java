package com.lake.api.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lake.api.dao.DeviceinfoDao;
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

	@Override
	public Deviceinfo getDeviceInfo(String id) {
		return deviceinfoDao.searchById(id);
	}
}
