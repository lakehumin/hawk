package com.lake.api.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lake.api.model.DeviceDetail;
import com.lake.api.model.DeviceMonitor;
import com.lake.api.model.Deviceinfo;
import com.lake.api.model.Terminaldev;
import com.lake.api.service.DeviceinfoService;
import com.lake.api.service.TerminaldevService;
import com.lake.common_utils.web_utils.BaseResponse;
import com.lake.common_utils.web_utils.BaseWeb;

/**
 * @author LakeHm
 *
 * 2016��12��13������5:03:09
 */

@Controller
@RequestMapping("/device")
public class DeviceController extends BaseWeb {
	private static final Logger log = Logger.getLogger(DeviceController.class);
	
	@Resource
	private DeviceinfoService deviceinfoService;
	@Resource
	private TerminaldevService terminaldevService;
	
	//��ѯ�豸��ʷ״̬��Ϣ�����ѹ��������
	@RequestMapping("/search/devicehistory")
    public void searchDeviceHistory(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		String date = request.getParameter("date");
		String d = new Date(Long.parseLong(date)).toString();
		
		List<Deviceinfo> listd = deviceinfoService.getDeviceHistoryInfo(id, d);
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		br.setData(listd);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response��ȡpwʧ��");
		}
        return ;
    }
	
	//��ѯ�豸�����Ƭ
	@RequestMapping("/search/img")
    public void searchDeviceImg(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		String date = request.getParameter("date");
		String d = new Date(Long.parseLong(date)).toString();
		
		DeviceMonitor dm = deviceinfoService.getDeviceMonitorImg(id, d);
		BaseResponse br = new BaseResponse();
		if(null != dm) {
			br.setSuccess(true);
			br.setData(dm);
		} else {
			br.setSuccess(false);
		}
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response��ȡpwʧ��");
		}
        return ;
    }
	
	//��ѯ�豸��ʷ�����Ƭ
	@RequestMapping("/search/historyimg")
    public void searchDeviceHistoryImg(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		String date = request.getParameter("date");
		String d = new Date(Long.parseLong(date)).toString();
		
		List<DeviceMonitor> listdm = deviceinfoService.getDeviceHistoryMonitorImg(id, d);
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		br.setData(listdm);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response��ȡpwʧ��");
		}
        return ;
    }
	
	//��ѯ�����豸��ϸ��Ϣ
	@RequestMapping("/search/alldetail")
    public void searchAllDetail(HttpServletRequest request,HttpServletResponse response) { 
		List<DeviceDetail> listdd = deviceinfoService.getAllDeviceDetail();
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		br.setData(listdd);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response��ȡpwʧ��");
		}
        return ;
    }
	
	//��ѯ�豸�̶���Ϣ�б�
	@RequestMapping("/search/allinfo")
    public void searchAllInfo(HttpServletRequest request,HttpServletResponse response) {  
		List<Terminaldev> listT = terminaldevService.getAllTerminalInfo();
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		br.setData(listT);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response��ȡpwʧ��");
		}
        return ;
    }
	
	//��ѯ�����豸��ϸ��Ϣ
	@RequestMapping("/search/detail")
    public void searchDeviceDetail(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		DeviceDetail dd = deviceinfoService.getDeviceDetailById(id);
		
		BaseResponse br = new BaseResponse();
		if(null != dd) {
			br.setSuccess(true);
			br.setData(dd);
		} else {
			br.setSuccess(false);
			log.warn("û�ҵ��豸idΪ" + id + "���յ���ϸ��Ϣ");
		}
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response��ȡpwʧ��");
		}
        return ;
    }
	
	//��ѯ�����豸������ϸ��Ϣ
	@RequestMapping("/search/devdetail")
    public void searchDeviceDevDetail(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		Terminaldev dd = terminaldevService.getTerminalInfo(id);
		
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		br.setData(dd);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response��ȡpwʧ��");
		}
        return ;
    }
	
	//�����豸�̶���Ϣ����delete add�ֱ�Ϊɾ��������
	@RequestMapping("/update")
    public void updateDevice(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		String tel = request.getParameter("tel");
		String location = request.getParameter("location");
		
		Terminaldev t = new Terminaldev();
		t.setTerminal_id(id);
		t.setTel_num(tel);
		t.setLocation(location);
		
		terminaldevService.updateTerminalInfo(t);
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		br.setData(t);
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response��ȡpwʧ��");
		}
        return ;
    }
	
	@RequestMapping("/delete")
    public void deleteDevice(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		
		terminaldevService.deleteTerminalById(id);
		BaseResponse br = new BaseResponse();
		br.setSuccess(true);
		log.warn("delete");
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response��ȡpwʧ��");
		}
        return ;
    }
	
	@RequestMapping("/add")
    public void add(HttpServletRequest request,HttpServletResponse response) {  
		String id = request.getParameter("id");
		String tel = request.getParameter("tel");
		String location = request.getParameter("location");
		
		Terminaldev tt = terminaldevService.getTerminalInfo(id);
		BaseResponse br = new BaseResponse();
		if(null == tt) {
			Terminaldev t = new Terminaldev();
			t.setTerminal_id(id);
			t.setTel_num(tel);
			t.setLocation(location);
			t.setIslinked(0);
			terminaldevService.insertTerminal(t);
			br.setSuccess(true);
		} else {
			br.setSuccess(false);
		}
		
		try {
			print(response, br);
		} catch (IOException e) {
			log.error("response��ȡpwʧ��");
		}
    }
}
