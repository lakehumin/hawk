package com.cyt.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import com.cyt.Bean.Msg_Title_Bean;
import com.cyt.Bean.Terminal_Dev_Bean;
import com.cyt.DAO.Terminal_Dev_Dao;
import com.cyt.DAO.msg_data_Dao;
import com.cyt.Service.DataAnalyseService;
import com.lake.common_utils.stringutils.StringUtils;

public class CommunicationTest {
	static private HashMap<String, String> tel_TidMap=new HashMap<String, String>();
	static{	
	Terminal_Dev_Dao tdo=new Terminal_Dev_Dao();
	ArrayList<Terminal_Dev_Bean> tdblst=tdo.SearchAll();
	for(int i=0;i<tdblst.size();i++)
	{
		tel_TidMap.put(tdblst.get(i).getTerminal_id(),tdblst.get(i).getTel_num());
	}
	}
	public static void main(String[] args)
	{
		
		Thread thread1=new Thread()
		{
			public void run()
			{
				SendMsg("001");
			}
		};
		Thread thread2=new Thread()
		{
			public void run()
			{
				SendMsg("002");
			}
		};
		Thread thread3=new Thread()
		{
			public void run()
			{
				SendMsg("003");
			}
		};
		Thread thread4=new Thread()
		{
			public void run()
			{
				SendMsg("004");
			}
		};
		Thread thread5=new Thread()
		{
			public void run()
			{
				SendMsg("005");
			}
		};
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();
	}
	public static void SendMsg(String terminal_id)
	{
		String  cmdbase="12345";
		Calendar calendar=Calendar.getInstance();
		int month=calendar.get(Calendar.MONTH)+1;
		int day=calendar.get(Calendar.DAY_OF_MONTH);
		while(true)
		{
			Random random = new Random();
			int cmdnum=random.nextInt(cmdbase.length());
			String _tel=tel_TidMap.get(terminal_id);
			String telhex=StringUtils.str2hexstr(_tel);
			String cmd="0"+cmdbase.charAt(cmdnum);
			//生成随机消息msg
			String msg=SetMsg("01");
			String date="17/"+month+"/"+day;
			day++;
			System.out.println(date+" --> "+terminal_id+": msg= "+msg);
			DataAnalyseService.TextAnalyse(msg,telhex,date);
			try {
				Thread.sleep(5*1000);	  
			} catch (Exception e) {
			}
		}
	}
	public static String SetMsg(String cmd)
	{
		String MSG="";         
		if ("01".equals(cmd)) {
			String battery="3"+(int)(Math.random()*10)+"3"+(int)(Math.random()*10);
			String voltage=(int)(Math.random()*10)+""+(int)(Math.random()*10);
			String state="0"+(int)(1+Math.random()*4);
			MSG+=cmd+battery+voltage+state;
		}
		else if ("02".equals(cmd)) {
			MSG+=cmd;
		}
		else if ("03".equals(cmd)) {
			DateFormat format=new SimpleDateFormat("YY/MM/dd");
			String date=format.format(new Date());
			MSG+=cmd+date+"01";
		}
		else if ("04".equals(cmd)) {
			MSG+=cmd+"15905195757"+"01";
		}
		else if ("05".equals(cmd)) {
			MSG+=cmd;
		}
		else {
			
		}
		return MSG;
	}
}
