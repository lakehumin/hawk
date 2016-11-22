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
		//String message="hello";
		ssb.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("----------start-------------------");
		//System.out.println("¿ªÊ¼´«Êä£¡");
		//ssb.write(message);
		
	}
}
