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
				System.out.println("----------------������Ҫ���͵�����-----------------");
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
		
		String msg="�����Ѿ����";
		final Sim800AService s800service=new Sim800AService();
		s800service.setSP(SPB);
		String managerTel="15861815868";
		String message="����֪ͨ������豸�����"+"001"+"����ַ��"+"�Ͼ�"+"��������"+"�͵�ѹ"+"�쳣�������������ĵ�¼�鿴����ʱ����"
		               +"\r\n"+"�����������Է�������";
//		s800service.Send_Message(managerTel, msg);
//		String chmsg=DataAnalyseService.Set_CHINESE_MSG(message, managerTel);
//		System.out.println(chmsg);
		s800service.Send_Message_toManger(managerTel,message);
		System.out.println("OK");
		 //���Խ��ܶ���		
//		
//		Thread read_thread=new Thread()
//		{
//			public void run() {
//				s800service.Wait_For_Message();
//			}
//		};
//		read_thread.start();
		//s800service.Read_MMS("3037");
		//������ȡ�߳�
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
		
		//���Զ��ŷ��͹���
		/*if(s800service.CheckAT())
		{
			if(s800service.SetSIM_Text_Work_Environment())
			{
				s800service.Send_Message(phonenum, msg);
			}
		}
		*/
		//c���Բ������ù���
		/*
		if (s800service.CheckAT()) 
		{
			if (s800service.Set_MMS_Environment()) 
			{
				System.out.println("���Գɹ���");
			}
		}*/
		//s800service.Read_MMS("32");
		//���Խ��ܲ���
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
		//����gprs��ʼ������
		//if(s800service.StartGPRS())
		//{
		//	System.out.println("��ʼ�� SUCCESS!");
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
