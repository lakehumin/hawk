package com.cyt.test;

import java.io.File;

import javax.imageio.stream.FileImageOutputStream;

import com.cyt.Bean.SerialPortBean;
import com.cyt.Bean.SerialPortBean2;

import java.io.*;
import java.util.*;

public class SerialPortCommunicationTest
{
	 public static void main(String args[]) { 
	        
	 /*   
		SerialPortBean2 SPB=new SerialPortBean2();
		SPB.selectPort("COM2");
	    ArrayList<Byte> data=new ArrayList<Byte>();
	    		data=SPB.getPack();
	    try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	    byte[] rec=new byte[data.size()];
	    */
		 SerialPortBean SPB=new SerialPortBean();
		 SPB.selectPort("COM2");
		 SPB.startRead(0);
		 boolean isnew=true;
		 int i=0;
		 byte[] data=null;  
		 while(true){
			 try {
					Thread.sleep(5000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			 System.out.println("123");
		 if(SPB.getRec_byte()!=null)
		 {  
			//System.out.println("\n");
			data=SPB.getRec_byte();
		 	if(data!=null){
		 	System.out.println("¿ªÊ¼×ªÍ¼Æ¬¡£¡£¡£");	
		 	i++;
		 	String path= "C:\\Users\\cyt\\Desktop\\ico\\test"+i+".jpg";
			byte2image(data,path);
			String string=SPB.getRec_string();
			//SPB.setRec_byte(null);
			System.out.println("rec_string= "+string);
			System.out.println("ok");
			SPB.write(string,"hex");
			System.out.println("send success!");
		 	data=null;
		 	SPB.setRec_byte(null);
		 	//SPB.hasnew=false;
		 	}
			//break;
		}
		 }
			 
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
