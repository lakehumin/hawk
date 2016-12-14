package com.lake.api.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lake.api.model.DeviceDetail;
import com.lake.api.model.Deviceinfo;
import com.lake.api.model.Terminaldev;
import com.lake.api.service.DeviceinfoService;
import com.lake.api.service.TerminaldevService;
import com.lake.common_utils.web_utils.BaseResponse;
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
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		br.setData(d);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/search/info")
    public void searchInfo(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		
		Terminaldev t = terminaldevService.getTerminalInfo(id);
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		br.setData(t);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/search/allinfo")
    public void searchAllInfo(HttpServletRequest request,HttpServletResponse response) {  
		List<Terminaldev> listT = terminaldevService.getAllTerminalInfo();
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		br.setData(listT);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/search/detail")
    public void searchDeviceDetail(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		Terminaldev t = terminaldevService.getTerminalInfo(id);
		Deviceinfo d = deviceinfoService.getDeviceInfo(id);
		
		BaseResponse br = new BaseResponse();
		if(null != d && null != d) {
			DeviceDetail dd = new DeviceDetail();
			dd.setBattery(d.getBattery());
			dd.setId(t.getTerminal_id());
			dd.setLocation(t.getLocation());
			dd.setState(t.getIslinked());
			dd.setTel(t.getTel_num());
			dd.setVoltage(d.getVoltage());
			dd.setWorkstate(d.getWorkstate());
			br.setSuccess(true);
			br.setData(dd);
		} else {
			br.setSuccess(false);
			log.warn("没找到设备id为" + id + "的详细信息");
		}
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
}
