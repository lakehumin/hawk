package com.cyt.test;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.cyt.Bean.DeviceInfoBean;
import com.cyt.Bean.Msg_Data_Bean;
import com.cyt.Bean.Msg_Title_Bean;
import com.cyt.Bean.Terminal_Dev_Bean;
import com.cyt.DAO.Device_Info_Dao;
import com.cyt.DAO.Msg_Title_Dao;
import com.cyt.DAO.Terminal_Dev_Dao;
import com.cyt.DAO.msg_data_Dao;
import com.cyt.Service.DataAnalyseService;
import com.lake.common_utils.db_utils.SqlHelper;

public class sqltest {
 public static void main(String []args)
 {
	 //msg_data_Dao msgDAO=new msg_data_Dao();
//	 for(int i=1;i<5;i++){
//	 msg_data_bean temp=new msg_data_bean();
//	 temp.setId(i);
//	 temp.setTerminal_id(i);
//	 temp.setImg_path("C:\\Users\\cyt\\Desktop\\ico");
//	 temp.setMsg_path("C:\\Users\\cyt\\Desktop\\ico");
//	 java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
//	 temp.setDate(currentDate);
//	 if(msgDAO.add(temp))
//	 {
//		System.out.println("��"+i+"�������Ѿ�����"); 
//	 }
//	 else {
//		 System.out.println("��"+i+"�����ݴ���ʧ��"); 
//	 }
//	 }
//	 System.out.println("�������");
//	 ArrayList<msg_data_bean> mdblst=new ArrayList<msg_data_bean>();
//	 String id="1";
//	 if (msgDAO.delete(1,id)) {
//		System.out.println("ɾ���ɹ�---------");
//	 }
//	 String date="'2016-11-22'";
//	 mdblst=msgDAO.Search(5, date);
//	 for(msg_data_bean mdb:mdblst)
//	 {
//		 //System.out.println("start!");
//		 System.out.println(mdb.getId()+"\t"+mdb.getTerminal_id()+"\t"+mdb.getImg_path()+"\t"+mdb.getMsg_path()+"\t"+mdb.getDate());
//	 }
	 DeviceInfoBean dib= new DeviceInfoBean();
	 String terminal_id="004";
	 String battery="31%";
	 String voltage="2.8v";
	 String workstate="normal";
	 String date="17/1/9";
     dib.setTerminal_id(terminal_id);
     dib.setBattery(battery);
     dib.setVoltage(voltage);
     dib.setWorkstate(workstate);
     dib.setDate(date);
     new Device_Info_Dao().add(dib);
	 System.out.println("OK");
	
	 
	 
 }
 }
