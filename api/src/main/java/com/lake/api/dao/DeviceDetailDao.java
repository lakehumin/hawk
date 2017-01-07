package com.lake.api.dao;

import java.util.List;
import com.lake.api.model.DeviceDetail;

/**
 * @author LakeHm
 *
 * 2017年1月5日下午8:32:59
 */
public interface DeviceDetailDao {

	List<DeviceDetail> searchAllDeviceDetail();
	
	DeviceDetail searchDeviceDetailById(String id);
}
