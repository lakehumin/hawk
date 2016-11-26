package com.lake.api.dao;

import java.util.List;
import com.lake.api.model.User;

/**
 * @author LakeHm
 *
 * 2016年11月15日下午3:25:41
 */
public interface UserDao {

	void insert(User user);
	
	User searchByName(String name);
	
	List<User> getAllUser();
	
	void update(User user);
	
	void deleteByName(String name);
}
