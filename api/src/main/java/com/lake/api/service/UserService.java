package com.lake.api.service;

import java.util.List;

import com.lake.api.model.User;

/**
 * @author LakeHm
 *
 * 2016��11��15������3:33:54
 */
public interface UserService {
	User getUserByName(String name);
	
	List<User> getAllUser();
	
	void insert(User u);
	
	void update(User u);
	
	void deleteByName(String name);
}
