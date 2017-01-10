package com.cyt.test;

import java.io.File;

import javax.imageio.stream.FileImageOutputStream;

import com.cyt.Bean.SerialPortBean;
import com.cyt.Service.DataAnalyseService;
import com.cyt.Service.Sim800AService;

import java.io.*;
import java.util.Date;

public class Test2
{
	 public static void main(String args[]) { 
	        
	    
		SerialPortBean SPB=new SerialPortBean();
		//SPB.listPort();
		//System.out.println(new Date()+" --> "+"helo");
		SPB.selectPort("COM8");
		SPB.startRead(0);
		/*
		while(true)
		{
			try {
				BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));  
				System.out.println("----------------请输入要发送的内容-----------------");
				String data=strin.readLine();
				data=data.replace(" ", "");
				SPB.write(data,"hex");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		*/
		//String phonenum="15861815868"; 
		
		String msg="测试已经完成";
		final Sim800AService s800service=new Sim800AService();
		s800service.setSP(SPB);
		String managerTel="15861815868";
		String message="警报通知：检测设备（编号"+"001"+"，地址："+"南京"+"）出现了"+"低电压"+"异常，请至服务中心登录查看并及时处理"
		               +"\r\n"+"————来自服务中心";
//		s800service.Send_Message(managerTel, msg);
//		String chmsg=DataAnalyseService.Set_CHINESE_MSG(message, managerTel);
//		System.out.println(chmsg);
		s800service.Send_Message_toManger(managerTel,message);
		System.out.println("OK");
		 //测试接受短信		
//		
//		Thread read_thread=new Thread()
//		{
//			public void run() {
//				s800service.Wait_For_Message();
//			}
//		};
//		read_thread.start();
		//s800service.Read_MMS("3037");
		//开启读取线程
		/*
		new Thread(){
				public void run()
				{
//					try {
//						Thread.sleep(60*1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					DataAnalyseService.ReadStoredMsg(s800service);
				}
			}.start();
		
		//测试短信发送功能
		/*if(s800service.CheckAT())
		{
			if(s800service.SetSIM_Text_Work_Environment())
			{
				s800service.Send_Message(phonenum, msg);
			}
		}
		*/
		//c测试彩信设置功能
		/*
		if (s800service.CheckAT()) 
		{
			if (s800service.Set_MMS_Environment()) 
			{
				System.out.println("测试成功！");
			}
		}*/
		//s800service.Read_MMS("32");
		//测试接受彩信
		/*
		 
		Thread read_thread=new Thread()
		{
			public void run() {
				s800service.Wait_For_MMS();;
			}
		};
//		if (s800service.CheckAT()) {
//			read_thread.start();
//		}
		read_thread.start();
		*/
		//测试gprs初始化设置
		//if(s800service.StartGPRS())
		//{
		//	System.out.println("初始化 SUCCESS!");
		//}
		//SPB.startReadingDataThread();
		//String path= "C:\\Users\\cyt\\Desktop\\ico\\test2.png";
//		while(true){
//		 String rec=SPB.getRec_string();
//		 System.out.println("rec= "+rec);
//		 if(null!=rec&&!rec.equals("")){
//		 byte[] rec_data=hexStringToByte(rec);
//		 System.out.println("okokoko");
//		 byte2image(rec_data,path);
//		 break;
//		 }
//		}
		 
	}
	 public static void byte2image(byte[] data,String path){
		    if(data.length<3||path.equals("")) return;
		    try{
		    FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
		    imageOutput.write(data, 0, data.length);
		    imageOutput.close();
		    System.out.println("Make Picture success,Please find image in " + path);
		    } catch(Exception ex) {
		      System.out.println("Exception: " + ex);
		      ex.printStackTrace();
		    }
	 }
		public static byte[] hexStringToByte(String hex) {   
		    int len = (hex.length() / 2);   
		    byte[] result = new byte[len];   
		    char[] achar = hex.toCharArray();   
		    for (int i = 0; i < len; i++) {   
		     int pos = i * 2;   
		     result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));   
		    }   
		    return result;   
		}  
		  
		private static byte toByte(char c) {   
		    byte b = (byte) "0123456789ABCDEF".indexOf(c);   
		    return b;   
		}  
}
