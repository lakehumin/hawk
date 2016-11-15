package com.lake.api.service;

import com.lake.api.model.User;

/**
 * @author LakeHm
 *
 * 2016��11��15������3:33:54
 */
public interface UserService {
	User getUserByName(String name);
	
	void insert(User u);
	
	void update(User u);
	
	void deleteByName(String name);
}
