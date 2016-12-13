package com.lake.api.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lake.api.model.Deviceinfo;
import com.lake.api.model.Terminaldev;
import com.lake.api.service.DeviceinfoService;
import com.lake.api.service.TerminaldevService;
import com.lake.common_utils.web_utils.BaseWeb;

/**
 * @author LakeHm
 *
 * 2016年12月13日下午5:03:09
 */

@Controller
@RequestMapping("/device")
public class DeviceController extends BaseWeb {
	private static final Logger log = Logger.getLogger(DeviceController.class);
	
	@Resource
	private DeviceinfoService deviceinfoService;
	@Resource
	private TerminaldevService terminaldevService;
	
	@RequestMapping("/search/state")
    public void searchState(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		
		Deviceinfo d = deviceinfoService.getDeviceInfo(id);
		try {
			print(response, d);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/search/info")
    public void searchInfo(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		
		Terminaldev t = terminaldevService.getTerminalInfo(id);
		try {
			print(response, t);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
}
