package com.cyt.test;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.cyt.Bean.msg_data_bean;
import com.cyt.DAO.msg_data_Dao;
import com.lake.common_utils.db_utils.SqlHelper;

public class sqltest {
 public static void main(String []args)
 {
	 msg_data_Dao msgDAO=new msg_data_Dao();
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
//		System.out.println("第"+i+"号数据已经存入"); 
//	 }
//	 else {
//		 System.out.println("第"+i+"号数据存入失败"); 
//	 }
//	 }
//	 System.out.println("插入完毕");
	 ArrayList<msg_data_bean> mdblst=new ArrayList<msg_data_bean>();
	 String id="1";
	 if (msgDAO.delete(1,id)) {
		System.out.println("删除成功---------");
	 }
	 String date="'2016-11-22'";
	 mdblst=msgDAO.Search(5, date);
	 for(msg_data_bean mdb:mdblst)
	 {
		 //System.out.println("start!");
		 System.out.println(mdb.getId()+"\t"+mdb.getTerminal_id()+"\t"+mdb.getImg_path()+"\t"+mdb.getMsg_path()+"\t"+mdb.getDate());
	 }
 }
 }
