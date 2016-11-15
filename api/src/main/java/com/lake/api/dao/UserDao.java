package com.lake.api.dao;

import com.lake.api.model.User;

/**
 * @author LakeHm
 *
 * 2016��11��15������3:25:41
 */
public interface UserDao {

	void insert(User user);
	
	User searchByName(String name);
	
	void update(User user);
	
	void deleteByName(String name);
}
