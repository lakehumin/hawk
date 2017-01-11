package com.cyt.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.text.SimpleAttributeSet;

import com.cyt.Service.DataAnalyseService;
import com.lake.common_utils.stringutils.StringUtils;

public class CommonTest {
	public synchronized void test1()
	{
		int num=5;
		while(num-->0)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("test1:"+num);
		}
	}
	public synchronized void test2()
	{
		int num=5;
		while(num-->0)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("test2:"+num);
		}
		System.out.println("----test2---");
		test1();
	}
	public static void main(String []args)
	{ 
		String rec="";
		int month_bgn_index=rec.indexOf("2F")+2;
		int day_bgn_index=rec.indexOf("2C",month_bgn_index)-4;
		String date=new String(rec.substring((month_bgn_index)-6,day_bgn_index+4));
		String month=new String(StringUtils.hexStringToByte(rec.substring(month_bgn_index,month_bgn_index+4)));
		String day=new String(StringUtils.hexStringToByte(rec.substring(day_bgn_index,day_bgn_index+4)));
		System.out.println(month+"\t"+day);
	}
	
	
	
	 
}
