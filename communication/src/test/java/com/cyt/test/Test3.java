package com.cyt.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.text.SimpleAttributeSet;

public class Test3 {
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
		Date now=new Date();
		int hours=now.getHours();
		System.out.println(hours);
	}
	 
}
