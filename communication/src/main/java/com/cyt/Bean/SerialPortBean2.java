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
	private String appName = "����ͨѶ����";
	private int timeout = 2000;//open �˿�ʱ�ĵȴ�ʱ��
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
	 * byte�����ת��
	 * @param data
	 * @return
	 */
	//byte���鵽16�����ַ���
	  public String byte2string(byte[] data){
	    if(data==null||data.length<=1) return "0x";
	    if(data.length>200000) return "0x";
	    StringBuffer sb = new StringBuffer();
	    int buf[] = new int[data.length];
	    //byte����ת����ʮ����
	    for(int k=0;k<data.length;k++){
	      buf[k] = data[k]<0?(data[k]+256):(data[k]);
	    }
	    //ʮ����ת����ʮ������
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
		 * @�������� :selectPort
		 * @�������� :ѡ��һ���˿ڣ����磺COM1
		 * @����ֵ���� :void
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
		 * @�������� :openPort
		 * @�������� :��SerialPort
		 * @����ֵ���� :void
		 */
		private void openPort(){
			sign="";
			if(commPort == null)
			{
				log(String.format("�޷��ҵ�����Ϊ'%1$s'�Ĵ��ڣ�", commPort.getName()));
				 sign="�޷��ҵ�����Ϊ"+commPort.getName()+"�Ĵ��ڣ�";
			}
			else{
				log("�˿�ѡ��ɹ�����ǰ�˿ڣ�"+commPort.getName()+",����ʵ���� SerialPort:");
				sign="�˿�ѡ��ɹ�����ǰ�˿ڣ�"+commPort.getName()+",����ʵ���� SerialPort:";
				
				try{
					serialPort = (SerialPort)commPort.open(appName, timeout);
					log("ʵ�� SerialPort �ɹ���");
					sign+="ʵ�� SerialPort �ɹ���";
				}catch(PortInUseException e){
					sign+="�˿�"+commPort.getName()+"����ʹ���У�";
					throw new RuntimeException(String.format("�˿�'%1$s'����ʹ���У�", 
							commPort.getName()));
					
				}
			}
		}
		
		/**
		 * @�������� :checkPort
		 * @�������� :���˿��Ƿ���ȷ����
		 * @����ֵ���� :void
		 */
		private void checkPort(){
			sign="";
			if(commPort == null)
			{
				sign="û��ѡ��˿ڣ���ʹ�� " +"selectPort(String portName) ����ѡ��˿�";
				throw new RuntimeException("û��ѡ��˿ڣ���ʹ�� " +
						"selectPort(String portName) ����ѡ��˿�");
			}
			if(serialPort == null){
				sign="SerialPort ������Ч��";
				throw new RuntimeException("SerialPort ������Ч��");
			}
		}
		
		/**
		 * @�������� :write
		 * @�������� :��˿ڷ������ݣ����ڵ��ô˷���ǰ ��ѡ��˿ڣ���ȷ��SerialPort�����򿪣�
		 * @����ֵ���� :void
		 *	@param message
		 */
		public void write(String message) {
			
			checkPort();
			sign="";
			try{
				outputStream = new BufferedOutputStream(serialPort.getOutputStream());
			}catch(IOException e){
				sign="��ȡ�˿ڵ�OutputStream����"+e.getMessage();
				throw new RuntimeException("��ȡ�˿ڵ�OutputStream����"+e.getMessage());
			}
			
			try{
				byte[] send=hexStringToByte(message);
				outputStream.write(send);
				log("��Ϣ���ͳɹ���");
				sign="��Ϣ���ͳɹ���";
			}catch(IOException e){
				sign="��˿ڷ�����Ϣʱ����"+e.getMessage();
				throw new RuntimeException("��˿ڷ�����Ϣʱ����"+e.getMessage());
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
