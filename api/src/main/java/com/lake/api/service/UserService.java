package com.lake.api.service;

import java.util.List;

import com.lake.api.model.User;

/**
 * @author LakeHm
 *
 * 2016年11月15日下午3:33:54
 */
public interface UserService {
	User getUserByName(String name);
	
	List<User> getAllUser();
	
	void insert(User u);
	
	void update(User u);
	
	void deleteByName(String name);
}
