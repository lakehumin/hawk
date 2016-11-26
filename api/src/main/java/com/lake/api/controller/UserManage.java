package com.lake.api.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.lake.api.model.User;
import com.lake.api.service.UserService;
import com.lake.common_utils.web_utils.BaseResponse;
import com.lake.common_utils.web_utils.BaseWeb;

/**
 * @author LakeHm
 *
 * 2016年11月23日上午10:21:14
 */

@Controller
@RequestMapping("/user")
public class UserManage extends BaseWeb {
	private static final Logger log = Logger.getLogger(UserManage.class);

	@Resource
	private UserService userService;
	
	@RequestMapping("/add")
    public void add(HttpServletRequest request,HttpServletResponse response) {  
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		User u = new User();
		u.setUsername(username);
		u.setPassword(password);
		userService.insert(u);
		
		try {
			print(response, "success");
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
    }
	
	@RequestMapping("/search")
    public void search(HttpServletRequest request,HttpServletResponse response) {  
		String username = request.getParameter("username");
		
		User u = userService.getUserByName(username);
		try {
			print(response, u);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/getAll")
    public void getAll(HttpServletRequest request,HttpServletResponse response) {  
		
		List<User> userList = userService.getAllUser();
		try {
			print(response, userList);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/update")
    public void update(HttpServletRequest request,HttpServletResponse response) {  
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		User u = new User();
		u.setUsername(username);
		u.setPassword(password);
		userService.update(u);
		try {
			print(response, u);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/delete")
    public void delete(HttpServletRequest request,HttpServletResponse response) {  
		String username = request.getParameter("username");
		
		userService.deleteByName(username);
		log.warn("delete");
		try {
			print(response, "success");
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/login")
    public void login(HttpServletRequest request,HttpServletResponse response) {  
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		User u = userService.getUserByName(username);
		BaseResponse br = new BaseResponse();
		if(null != u && u.getPassword().equals(password)) {
			br.setSuccess(true);
			br.setData(u);
		} else {
			br.setSuccess(false);
		}
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
}
