package com.cyt.test;

import java.io.IOException;
import java.net.ServerSocket;

import com.cyt.Bean.ServerSocketBean;
import com.lake.common_utils.stringutils.StringUtils;

public class test1
{
	public static void main(String []args)
	{
		ServerSocketBean ssb=new ServerSocketBean(8080);
		String Message="hello";
		ssb.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("start check 1");
		//if(ssb.CheckCNCT())
		//{
			//for(int i=1;i<10;i++){
			ssb.write(Message, 1);//}
			
		//}
		System.out.println("---------------start---------------");
		//System.out.println("¿ªÊ¼´«Êä£¡");
		//ssb.write(message);
		
	}
}
