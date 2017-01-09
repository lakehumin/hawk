package com.lake.api.controller;

import java.io.IOException;
import java.sql.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lake.api.model.Alarm;
import com.lake.api.model.DeviceMonitor;
import com.lake.api.model.Terminaldev;
import com.lake.api.service.AlarmService;
import com.lake.common_utils.web_utils.BaseResponse;
import com.lake.common_utils.web_utils.BaseWeb;

/**
 * @author LakeHm
 *
 * 2017年1月8日下午3:20:56
 */

@Controller
@RequestMapping("/alarm")
public class AlarmController extends BaseWeb {

	private static final Logger log = Logger.getLogger(AlarmController.class);
	
	@Resource
	private AlarmService alarmService;
	
	//查询报警信息
	@RequestMapping("/search")
    public void search(HttpServletRequest request,HttpServletResponse response) {  
		int code = Integer.parseInt(request.getParameter("searchcode"));
		
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		switch(code) {
		case 0 :
			br.setData(alarmService.getAllEvent());
			break;
		case 1 :
			br.setData(alarmService.getAllUnHandledEvent());
			break;
		case 2 :
			br.setData(alarmService.getAllHandlingEvent());
			break;
		case 3 :
			br.setData(alarmService.getAllHandledEvent());
			break;
		case 4 :
			int id = Integer.parseInt(request.getParameter("id"));
			br.setData(alarmService.getEventById(id));
			break;
		default :
			break;
		}
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	//更新报警处理状态，下同
	@RequestMapping("/update/begin")
    public void updateAlarmHandleBegin(HttpServletRequest request,HttpServletResponse response) {  
		int id = Integer.parseInt(request.getParameter("id"));
		int state = Integer.parseInt(request.getParameter("state"));
		String date = new Date(Long.parseLong(request.getParameter("begindate"))).toString();
		String user = request.getParameter("user");
		
		alarmService.setEventBeginHandle(id, date, user, state);
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/update/end")
    public void updateAlarmHandleEnd(HttpServletRequest request,HttpServletResponse response) {  
		int id = Integer.parseInt(request.getParameter("id"));
		int state = Integer.parseInt(request.getParameter("state"));
		String date = new Date(Long.parseLong(request.getParameter("enddate"))).toString();
		String record = request.getParameter("record");
		
		alarmService.setEventEndHandle(id, date, record, state);
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
	
	@RequestMapping("/add")
    public void addAlarm(HttpServletRequest request,HttpServletResponse response) {  
		String terminal_id = request.getParameter("terminal_id");
		Date date = new Date(Long.parseLong(request.getParameter("eventdate")));
		String event = request.getParameter("event");
		
		Alarm a = new Alarm();
		a.setEvent(event);
		a.setTerminal_id(terminal_id);
		a.setEventdate(date);
		
		alarmService.addEvent(a);
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response获取pw失败");
		}
        return ;
    }
}
