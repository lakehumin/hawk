package com.lake.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lake.api.model.Alarm;

/**
 * @author LakeHm
 *
 * 2017年1月8日下午3:05:07
 */
public interface AlarmDao {

	void insert(Alarm a);
	
	List<Alarm> searchAllEvent();
	
	List<Alarm> searchAllUnHandledEvent();
	
	List<Alarm> searchAllHandlingEvent();
	
	List<Alarm> searchAllHandledEvent();
	
	Alarm searchEventById(int id);
	
	void updateBeginHandle(@Param("id")int id, @Param("begindate")String begindate,
			@Param("user")String user, @Param("state")int state);
	
	void updateEndHandle(@Param("id")int id, @Param("enddate")String enddate,
			@Param("record")String record, @Param("state")int state);
}
