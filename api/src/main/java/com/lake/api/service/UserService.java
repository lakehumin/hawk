package com.lake.api.service;

import com.lake.api.model.User;

/**
 * @author LakeHm
 *
 * 2016年11月15日下午3:33:54
 */
public interface UserService {
	User getUserByName(String name);
	
	void insert(User u);
	
	void update(User u);
	
	void deleteByName(String name);
}
