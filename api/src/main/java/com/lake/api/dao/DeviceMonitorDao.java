package com.lake.api.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.lake.api.model.DeviceMonitor;


/**
 * @author LakeHm
 *
 * 2017��1��7������4:20:40
 */
public interface DeviceMonitorDao {

	DeviceMonitor searchById(@Param("id")String id, @Param("date")String date);
	
	List<DeviceMonitor> searchHistoryById(@Param("id")String id, @Param("date")String date);
}
