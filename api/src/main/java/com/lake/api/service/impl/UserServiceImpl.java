package com.lake.api.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lake.api.dao.UserDao;
import com.lake.api.model.User;
import com.lake.api.service.UserService;

/**
 * @author LakeHm
 *
 * 2016��11��15������3:35:08
 */

@Service("userService") 
public class UserServiceImpl implements UserService{

	@Resource
	private UserDao userDao;

	@Override
	public User getUserByName(String name) {
		return userDao.searchByName(name);
	}

	@Override
	public List<User> getAllUser() {
		return userDao.getAllUser();
	}
	
	@Override
	public void insert(User u) {
		userDao.insert(u);
	}

	@Override
	public void update(User u) {
		userDao.update(u);
	}

	@Override
	public void deleteByName(String name) {
		userDao.deleteByName(name);
	}
}
