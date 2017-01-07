package com.lake.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lake.api.model.Deviceinfo;

/**
 * @author LakeHm
 *
 * 2016��12��13������4:52:35
 */
public interface DeviceinfoDao {

	Deviceinfo searchById(String id);
	
	List<Deviceinfo> searchHistoryById(@Param("id")String id, @Param("date")String date);
}
