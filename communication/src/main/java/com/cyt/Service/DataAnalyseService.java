package com.cyt.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JApplet;
import javax.swing.Spring;

import com.cyt.Bean.DeviceInfoBean;
import com.cyt.Bean.Msg_Data_Bean;
import com.cyt.Bean.Msg_Title_Bean;
import com.cyt.Bean.SerialPortBean;
import com.cyt.Bean.Terminal_Dev_Bean;
import com.cyt.DAO.Device_Info_Dao;
import com.cyt.DAO.Msg_Title_Dao;
import com.cyt.DAO.Terminal_Dev_Dao;
import com.cyt.DAO.msg_data_Dao;
import com.lake.common_utils.stringutils.StringUtils;

public class DataAnalyseService {
	//private static byte[] buffer=null;
	private static HashMap<String, String> tel_TidMap=null;
	static{
		tel_TidMap=new HashMap<String, String>();
		Terminal_Dev_Dao tdo=new Terminal_Dev_Dao();
		ArrayList<Terminal_Dev_Bean> tdblst=tdo.SearchAll();
		for (Terminal_Dev_Bean tdb : tdblst) {
			tel_TidMap.put(tdb.getTel_num(), tdb.getTerminal_id());
		}
		
		}
	
	public static byte[] List2Array(ArrayList<Byte> array)
	{
		byte[] Buffer=new byte[array.size()];
		for(int i=0;i<array.size();i++)
		{
			Buffer[i]=array.get(i);
		}
		return Buffer;
	}
	//��ȡWait_For_Message()�����еĶ������Բ�����������ݿ⣬���ڳ��͵�ͳһ��ȡ�ͽ�����
	public static boolean SaveMsgTitle(String index,String type)
	{
		boolean b=false;
		Msg_Title_Bean mtb=new Msg_Title_Bean();
		mtb.setnum(index);
		mtb.setType(type);
		mtb.setRead(false);
		if(new Msg_Title_Dao().add(mtb))
		{
			b=true;
		}
		return b;
	}
	//ɨ�����ݿ���MsgTitle���е����ݣ������˶Զ�������ȡ��
	public static void ReadStoredMsg(Sim800AService s800 )
	{
		Msg_Title_Dao mtd=new Msg_Title_Dao();
		//��ȡ���Ÿ�ʽ����Ϣ
		ArrayList<Msg_Title_Bean> text_lst=null;
		text_lst=mtd.Search("Text");
		if(text_lst.size()!=0)
		{
			for(int i=0;i<text_lst.size();i++)
			{
				//�������飬��ÿ����Ϣ���ж�ȡ
				String index=text_lst.get(i).getnum();
				if(s800.Read_Message(index))
				{
					//�����ŵ�isread״̬��Ϊ�Ѷ�
					log("��ȡ"+index+"������ϣ�����Ϊ�Ѷ�");
					mtd.update(text_lst.get(i));
				}
			}
		}
		else {
			log("�洢�����ı�����");
		}
		//��ȡ���Ÿ�ʽ�Ķ���Ϣ
		ArrayList<Msg_Title_Bean> mms_lst=null;
		mms_lst=mtd.Search("MMS");
		if (mms_lst.size()!=0) 
		{
			if(s800.Set_MMS_Environment())
			{	
				for(int i=0;i<mms_lst.size();i++)
				{
					log("��ʼ��ȡ����");
					//�������飬��ÿ����Ϣ���ж�ȡ
					String index=mms_lst.get(i).getnum();
					if(s800.Read_MMS(index))
					{
						//�����ŵ�isread״̬��Ϊ�Ѷ�
						log("��ȡ"+index+"������ϣ�����Ϊ�Ѷ�");
						mtd.update(mms_lst.get(i));
					}
					else {
						logerr("��ȡ"+index+"����ʧ��");
					}
				}
			}
			else {
				logerr("���ų�ʼ��ʧ�ܣ�������");
			}
		}
		else{
			log("�洢����δ������");
		}
	} 
	//����text���ŵ�����
	public static void TextAnalyse(String msg,String tel,String date)
	{
		Device_Info_Dao did=new Device_Info_Dao();
		Terminal_Dev_Dao tdd=new Terminal_Dev_Dao();
		String telnum="'"+new String(StringUtils.hexStringToByte(tel))+"'";
		log("telnum="+telnum);
		ArrayList<Terminal_Dev_Bean> temp=tdd.Search(3,telnum);
		if (msg.startsWith("01"))
		{
			//01�����յ������豸��Ϣ
			String battery=new String(StringUtils.hexStringToByte(msg.substring(2,6)));
			log("battery="+battery);
			String voltage=Integer.parseInt(msg.substring(6,7),16)+""+"."+Integer.parseInt(msg.substring(7, 8),16);
			log("voltage="+voltage);
			String workstate=getworkstate(msg.substring(8,10));
			log("workstate="+workstate);	
			for (int i = 0; i < temp.size(); i++) 
			{
			     String terminal_id=temp.get(i).getTerminal_id();
			     //�����ݿ�����¼�¼
			     DeviceInfoBean dib= new DeviceInfoBean();		    
			     dib.setTerminal_id(terminal_id);
			     dib.setBattery(battery);
			     dib.setVoltage(voltage);
			     dib.setWorkstate(workstate);
			     dib.setDate(date);
			     did.add(dib);
		   }
	   }
		else if (msg.startsWith("02")) 
		{
			//02�����յ���������ȷ�ϰ�
			for (int i=0;i<temp.size();i++)
			{
				//�����豸��Ϣ��Ϊͨ������
				temp.get(i).setIslinked(true);
			}
			
		}
		else if(msg.startsWith("03"))
		{
			//03�����յ�����������Ϣ�Ļظ�
			if(msg.endsWith("01"))
			{
				log(temp.get(0).getTerminal_id()+"����ʱ��ɹ�");
			}
			else if (msg.endsWith("02")) {
				logerr(temp.get(0).getTerminal_id()+"����ʱ��ʧ�ܣ������ԣ�");
			}
			else {
				logerr(temp.get(0).getTerminal_id()+"���ܶ����������������ԣ�");
			}
		}
		else if (msg.startsWith("04")) 
		{
			//04�������õ绰����Ļظ�
		    String tel_change=msg.substring(2, msg.length()-2);
		    if (msg.endsWith("01")) 
		    {
		    	log(temp.get(0).getTerminal_id()+"���õ绰�ɹ�");
		    	log(temp.get(0).getTerminal_id()+"���ĵĵ绰Ϊ"+tel_change);
			}
		    else if(msg.endsWith("02")){
		    	logerr(temp.get(0).getTerminal_id()+"���ĺ���ʧ��");
		    }
		    else
		    {
		    	logerr(temp.get(0).getTerminal_id()+"���յ��Ķ�������      ");
		    }
			
		}
		else if(msg.startsWith("05"))
		{
			//05�����ʱ���
			log(temp.get(0).getTerminal_id()+"��ʱ���");
		}
		else 
		{
			log(temp.get(0).getTerminal_id()+"�յ����ն˶��� "+msg);
		}
	}
	//����gprs������()
	public static void GPRSDataAnalyse(String rec)
	{
		if(rec.startsWith("3F"))
		{
			int devnum=Integer.parseInt(rec.substring(2,4),16);	
			String devString=String.valueOf(devnum);
			String msg_Date=new String(StringUtils.hexStringToByte(rec.substring(4,20)));
			String origin=new String(StringUtils.hexStringToByte(rec.substring(20,rec.length()-4)));
			log("�豸����ǣ�"+devString);
			log("���������ǣ�"+msg_Date);
			log("ԭʼ�����ǣ�"+origin);	
		}
		else 
		{
			logerr("��Ϣ�������ط�������");
		}
	}
	//��ǰ�˵�ͨ��
	public static String TCPDataAnalyse(String rec,Sim800AService s800)
	{
		String Msg="";
		if (rec.startsWith("02")) {
			Msg="02";
		}
		else if (rec.startsWith("03")) {
			String terminal_id=rec.substring(2,6);
			String cmd=rec.substring(6,8);
			String content=rec.substring(8);
			if(s800.Send_Message(tel_TidMap.get(terminal_id),Set_English_MSG(cmd, content)))
			{
				Msg=rec+"01";
			}
			else {
				Msg=rec+"02";
			}
		}		
		return Msg;
	}
	//����MMS�����ݲ����浽����
	public static void MSGDataAnalyse(String origin_data,String index,String tel_num,String _date)
	{
		log("MMS���ݴ�������");
		int bgn_index=origin_data.indexOf("0D0A", 38)+4;
		int end_index=origin_data.length()-12;
		String new_data=origin_data.substring(bgn_index,end_index);
		String dateString=new String(StringUtils.hexStringToByte(_date));
		String telsString=new String(StringUtils.hexStringToByte(tel_num));
		byte[] num=StringUtils.hexStringToByte(index);
		String Num=new String(num);
		String path="D:\\SerialPort_MSG\\"+tel_TidMap.get(telsString)+"_"+dateString+".png";
		saveToImgFile(new_data, path);
		Date date=getDate(dateString);
		Msg_Data_Bean mdb=new Msg_Data_Bean(tel_TidMap.get(telsString),null,path,date);
		new msg_data_Dao().add(mdb);
		log("MMS�������");
	}
	//����Ӣ�Ķ��ŵķ��͸�ʽ
	public static String Set_English_MSG(String cmd,String content)
	{
		String Msg="";
		if ("01".equals(cmd)) {
			Msg="01";
		}
		if ("02".equals(cmd)) {
			Msg="02";
		}
		if ("03".equals(cmd)) {
			Msg="03"+content;
		}
		if ("04".equals(cmd)) {
			Msg="04"+content;
		}
		if ("05".equals(cmd)) {
			Msg="05";
		}
		return Msg;
	}
	//�������Ķ��ŵķ�����Ϣ��ʽ
	public static String Set_CHINESE_MSG(String msg,String phoneNum)
	{
		String length;
		String telString="";
		length="0D"; 
		if(phoneNum.length()>11){
			     //�ֻ�����ǰ׺+86 �����ֳ���Ϊ13 
				 //����sim800ͨ��ָ�������ֻ��ŵı���
				for(int i=1;i<phoneNum.length()-1;i=i+2){
					telString+=String.valueOf(phoneNum.charAt(i+1))+String.valueOf(phoneNum.charAt(i));
					}
				telString+="F"+String.valueOf(phoneNum.charAt(phoneNum.length()-1));
		  }    
		else {
			for(int i=0;i<phoneNum.length()-1;i=i+2){
				telString+=String.valueOf(phoneNum.charAt(i+1))+String.valueOf(phoneNum.charAt(i));
				}
			telString="68"+telString+"F"+String.valueOf(phoneNum.charAt(phoneNum.length()-1));
		}
		//��string����תΪUnicode�����Է������Ķ���
		String unicodemsg=string2Unicode(msg);
		int len=unicodemsg.length()/2;
		String hexlen=Integer.toHexString(len);
		if (hexlen.length()<2) {
			hexlen="0"+hexlen;
		}
		//�������Ķ��ŷ��͵ı���
		String chmsg="001100"+length+"91"+telString+"000800"+hexlen+unicodemsg;
		return chmsg;
	}
	//unicode����
	public static String string2Unicode(String string) {  
        StringBuffer unicode = new StringBuffer();  
        for (int i = 0; i < string.length(); i++) {
        	String hex=Integer.toHexString(string.charAt(i));
            if(hex.length()<4)
            {
            	hex="00"+hex;
            }
            unicode.append(hex+" ");
        }  
        return unicode.toString();
    }
	//hexstringת��ͼ��
	private static  void saveToImgFile(String src, String output)
	{
		if (src == null || src.length() == 0)
		{
			return;
		}
		try
		{
			FileOutputStream out = new FileOutputStream(new File(output));
			byte[] bytes = src.getBytes();
			for (int i = 0; i < bytes.length; i += 2)
			{
				out.write(charToInt(bytes[i]) * 16 + charToInt(bytes[i + 1]));
			}
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	private static  int charToInt(byte ch)
	{
		int val = 0;
		if (ch >= 0x30 && ch <= 0x39)
		{
			val = ch - 0x30;
		}
		else if (ch >= 0x41 && ch <= 0x46)
		{
			val = ch - 0x41 + 10;
		}
		return val;
	}
	//����java.sql.date���͵�����
	public  static Date getDate(String dateString)
	{
		Date date;
		int year=Integer.parseInt(dateString.substring(0,4))-1900;
		int month=Integer.parseInt(dateString.substring(5, 7))-1;
		int day=Integer.parseInt(dateString.substring(8,10));
		date=new Date(year,month,day);
		return date;
	}
	//����״̬ӳ��
	private static String getworkstate(String index)
	{
		if ("01".equals(index)) 
		{
			return "Normal";
		}
		else if ("02".equals(index)) {
			return "LowPower";
		}
		else if ("03".equals(index)) {
			return "VolAbnormal";
		}
		else if ("04".equals(index)) {
			return "Destroyed";
		}
		else {
			return "Exception";
		}
	}
	//log����
	private static void log(String msg)
	{
		DateFormat format=new SimpleDateFormat("YY-MM-dd HH:mm:ss");
		String time=format.format(new java.util.Date());
		System.out.println(time+"--> "+msg);
	}
	private static void logerr(String msg)
	{
		DateFormat format=new SimpleDateFormat("YY-MM-dd HH:mm:ss");
		String time=format.format(new java.util.Date());
		System.err.println(time+"--> "+msg);
	}
}
