package com.cyt.Bean;
import java.awt.List;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.imageio.stream.FileImageOutputStream;

import gnu.io.*;
public class SerialPortBean2 implements Runnable 
{
	private String appName = "串口通讯测试";
	private int timeout = 2000;//open 端口时的等待时间
	private int threadTime = 0;
	private int PacketLength=1024*4;
	//private SendMessage SM=new SendMessage();
	private CommPortIdentifier commPort;
	private SerialPort serialPort;
	private InputStream inputStream;
	private OutputStream outputStream;
	public String sign="";
	public String rec_string="";
	public byte[] rec_byte;
	public byte[] getRec_byte() {
		return rec_byte;
	}
	public void setRec_byte(byte[] rec_byte) {
		this.rec_byte = rec_byte;
	}
	public String getSign()
	{
		return sign;
	}
	public void setSign(String sign)
	{
		this.sign = sign;
	}
	public void setRec_string(String rec_string)
	{
		this.rec_string = rec_string;
	}
	public String getRec_string()
	{
		return rec_string;
	}
	public void log(String msg){
		System.out.println(appName+" --> "+msg);
	}
	/**
	 * byte数组的转化
	 * @param data
	 * @return
	 */
	//byte数组到16进制字符串
	  public String byte2string(byte[] data){
	    if(data==null||data.length<=1) return "0x";
	    if(data.length>200000) return "0x";
	    StringBuffer sb = new StringBuffer();
	    int buf[] = new int[data.length];
	    //byte数组转化成十进制
	    for(int k=0;k<data.length;k++){
	      buf[k] = data[k]<0?(data[k]+256):(data[k]);
	    }
	    //十进制转化成十六进制
	    for(int k=0;k<buf.length;k++){
	      if(buf[k]<16) sb.append("0"+Integer.toHexString(buf[k]));
	      else sb.append(Integer.toHexString(buf[k]));
	    }
	    return ""+sb.toString().toUpperCase();
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
		/**
		 * @方法名称 :selectPort
		 * @功能描述 :选择一个端口，比如：COM1
		 * @返回值类型 :void
		 *	@param portName
		 */
		//@SuppressWarnings("rawtypes")
		public void selectPort(String portName){
			
			this.commPort = null;
			CommPortIdentifier cpid;
			Enumeration en = CommPortIdentifier.getPortIdentifiers();
			
			while(en.hasMoreElements()){
				cpid = (CommPortIdentifier)en.nextElement();
				if(cpid.getPortType() == CommPortIdentifier.PORT_SERIAL
						&& cpid.getName().equals(portName)){
					this.commPort = cpid;
					break;
				}
			}
			
			openPort();
		}
		
		/**
		 * @方法名称 :openPort
		 * @功能描述 :打开SerialPort
		 * @返回值类型 :void
		 */
		private void openPort(){
			sign="";
			if(commPort == null)
			{
				log(String.format("无法找到名字为'%1$s'的串口！", commPort.getName()));
				 sign="无法找到名字为"+commPort.getName()+"的串口！";
			}
			else{
				log("端口选择成功，当前端口："+commPort.getName()+",现在实例化 SerialPort:");
				sign="端口选择成功，当前端口："+commPort.getName()+",现在实例化 SerialPort:";
				
				try{
					serialPort = (SerialPort)commPort.open(appName, timeout);
					log("实例 SerialPort 成功！");
					sign+="实例 SerialPort 成功！";
				}catch(PortInUseException e){
					sign+="端口"+commPort.getName()+"正在使用中！";
					throw new RuntimeException(String.format("端口'%1$s'正在使用中！", 
							commPort.getName()));
					
				}
			}
		}
		
		/**
		 * @方法名称 :checkPort
		 * @功能描述 :检查端口是否正确连接
		 * @返回值类型 :void
		 */
		private void checkPort(){
			sign="";
			if(commPort == null)
			{
				sign="没有选择端口，请使用 " +"selectPort(String portName) 方法选择端口";
				throw new RuntimeException("没有选择端口，请使用 " +
						"selectPort(String portName) 方法选择端口");
			}
			if(serialPort == null){
				sign="SerialPort 对象无效！";
				throw new RuntimeException("SerialPort 对象无效！");
			}
		}
		
		/**
		 * @方法名称 :write
		 * @功能描述 :向端口发送数据，请在调用此方法前 先选择端口，并确定SerialPort正常打开！
		 * @返回值类型 :void
		 *	@param message
		 */
		public void write(String message) {
			
			checkPort();
			sign="";
			try{
				outputStream = new BufferedOutputStream(serialPort.getOutputStream());
			}catch(IOException e){
				sign="获取端口的OutputStream出错："+e.getMessage();
				throw new RuntimeException("获取端口的OutputStream出错："+e.getMessage());
			}
			
			try{
				byte[] send=hexStringToByte(message);
				outputStream.write(send);
				log("信息发送成功！");
				sign="信息发送成功！";
			}catch(IOException e){
				sign="向端口发送信息时出错："+e.getMessage();
				throw new RuntimeException("向端口发送信息时出错："+e.getMessage());
			}finally{
				try{
					outputStream.close();
				}catch(Exception e){
				}
			}
		}
		public ArrayList<Byte> getPack(){
			try {
				inputStream = new BufferedInputStream(serialPort.getInputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int newData=0;Boolean isok=true;
           ArrayList<Byte> msgPack=new ArrayList<Byte>();
           while(isok)
           {
		       try {
				while ((newData = inputStream.read()) != -1) {
						msgPack.add((byte)newData);
						isok=false;
					    
				}}
					catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
				                
				                //String path= "C:\\Users\\cyt\\Desktop\\ico\\test2.jpg";
				                //byte2image(msgPack, path);
				                //System.out.println("ok!");
           }
           		    //isok=true;
				    return msgPack;
				    
				                
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
