package com.lake.api.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.lake.api.model.User;
import com.lake.api.service.UserService;
import com.lake.common_utils.web_utils.BaseWeb;

/**
 * @author LakeHm
 *
 * 2016年11月15日下午2:39:47
 */

@Controller
@RequestMapping("/db")
public class TestDB extends BaseWeb{
	private static final Logger log = Logger.getLogger(TestDB.class);

	@Resource
	private UserService userService;
	
	@RequestMapping("/add")
    public void add(HttpServletRequest request,HttpServletResponse response) {  
		String name = request.getParameter("name");
		int age = Integer.parseInt(request.getParameter("age"));
		
		User u = new User();
		u.setAge(age);
		u.setName(name);
		u.setDate(new Timestamp(System.currentTimeMillis()));
		userService.insert(u);
		
		try {
			print(response, "success");
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
    }
	
	@RequestMapping("/search")
    public void search(HttpServletRequest request,HttpServletResponse response) throws Exception{  
		String name = request.getParameter("name");
		
		User u = userService.getUserByName(name);
		try {
			print(response, u);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/update")
    public void update(HttpServletRequest request,HttpServletResponse response) throws Exception{  
		String name = request.getParameter("name");
		int age = Integer.parseInt(request.getParameter("age"));
		
		User u = new User();
		u.setAge(age);
		u.setName(name);
		u.setDate(new Timestamp(System.currentTimeMillis()));
		userService.update(u);
		try {
			print(response, u);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/delete")
    public void delete(HttpServletRequest request,HttpServletResponse response) throws Exception{  
		String name = request.getParameter("name");
		
		userService.deleteByName(name);
		log.warn("delete");
		try {
			print(response, "success");
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
}
