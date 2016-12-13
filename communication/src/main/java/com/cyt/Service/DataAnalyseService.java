package com.cyt.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.stream.FileImageOutputStream;
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
	private static HashMap<String, String> tel_TidMap=new HashMap<String, String>();
	static{
		tel_TidMap.put("15905195757", "01");
		tel_TidMap.put("+8613814074227", "02");
		tel_TidMap.put("+8613814074233", "03");
		//tel_TidMap.put("15905195757", "02");
		//tel_TidMap.put("15905195757", "03");
		//tel_TidMap.put("15905195757", "04");
		//tel_TidMap.put("15905195757", "05");
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
					System.out.println("��ȡ�˶�����ϣ�����Ϊ�Ѷ�");
					mtd.update(text_lst.get(i));
				}
			}
		}
		else {
			System.out.println("�洢�����ı�����");
		}
		//��ȡ���Ÿ�ʽ�Ķ���Ϣ
		ArrayList<Msg_Title_Bean> mms_lst=null;
		mms_lst=mtd.Search("MMS");
		if (mms_lst.size()!=0) 
		{
			if(s800.Set_MMS_Environment())
			{	
				System.out.println("hello");
				for(int i=0;i<mms_lst.size();i++)
				{
					System.out.println("��ʼ��ȡ����");
					//�������飬��ÿ����Ϣ���ж�ȡ
					String index=mms_lst.get(i).getnum();
					if(s800.Read_MMS(index))
					{
						//�����ŵ�isread״̬��Ϊ�Ѷ�
						System.out.println("��ȡ�˶�����ϣ�����Ϊ�Ѷ�");
						mtd.update(mms_lst.get(i));
					}
					else {
						System.out.println("��ȡʧ��");
					}
				}
			}
			else {
				System.out.println("���ų�ʼ��ʧ�ܣ�������");
			}
		}
		else{
			System.out.println("�洢����δ������");
		}
	} 
	//����text���ŵ�����
	public static void TextAnalyse(String msg,String tel)
	{
		Device_Info_Dao did=new Device_Info_Dao();
		Terminal_Dev_Dao tdd=new Terminal_Dev_Dao();
		String telnum="'"+new String(StringUtils.hexStringToByte(tel))+"'";
		System.out.println("telnum="+telnum);
		ArrayList<Terminal_Dev_Bean> temp=tdd.Search(3, telnum);
		if (msg.startsWith("01"))
		{
			//01�����յ������豸��Ϣ
			String battery=new String(StringUtils.hexStringToByte(msg.substring(2,6)));
			System.out.println("battery="+battery);
			String voltage=Integer.parseInt(msg.substring(6,7),16)+""+"."+Integer.parseInt(msg.substring(7, 8),16)+"v";
			System.out.println("voltage="+voltage);
			String workstate=getworkstate(msg.substring(8,10));
			System.out.println("workstate="+workstate);	
			for (int i = 0; i < temp.size(); i++) {
			     String terminal_id=temp.get(i).getTerminal_id();
			    DeviceInfoBean dib= did.Searchid(terminal_id);
			    if(dib==null){	
			    //�ж����ݿ��޴˺����豸 ���������豸
			    dib=new DeviceInfoBean();
			    dib.setTerminal_id(terminal_id);
			    dib.setBattery(battery);
			    dib.setVoltage(voltage);
			    dib.setWorkstate(workstate);
			    did.add(dib);
			}
			    else {
			    //���������ݿ��еĴ˺��豸
			    	 dib=new DeviceInfoBean();
					 dib.setBattery(battery);
					 dib.setVoltage(voltage);
					 dib.setWorkstate(workstate);
					 did.update(dib);
				}
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
				System.out.println("����ʱ��ɹ�");
			}
			else if (msg.endsWith("02")) {
				System.err.println("����ʱ��ʧ�ܣ������ԣ�");
			}
			else {
				System.err.println("���ܶ����������������ԣ�");
			}
		}
		else if (msg.startsWith("04")) 
		{
			//04�������õ绰����Ļظ�
		    String tel_change=msg.substring(2, msg.length()-2);
		    if (msg.endsWith("01")) 
		    {
		    	System.out.println("���õ绰�ɹ�");
		    	System.out.println("���ĵĵ绰Ϊ"+tel_change);
			}
		    else if(msg.endsWith("02")){
		    	System.out.println("���ĺ���ʧ��");
		    }
		    else
		    {
		    	System.out.println("���յ��Ķ�������      ");
		    }
			
		}
	}
	//����gprs������
	public static void GPRSDataAnalyse(String rec)
	{
		if(rec.startsWith("3F"))
		{
			int devnum=Integer.parseInt(rec.substring(2,4),16);	
			String devString=String.valueOf(devnum);
			String msg_Date=new String(StringUtils.hexStringToByte(rec.substring(4,20)));
			String origin=new String(StringUtils.hexStringToByte(rec.substring(20,rec.length()-4)));
			System.out.println("�豸����ǣ�"+devString);
			System.out.println("���������ǣ�"+msg_Date);
			System.out.println("ԭʼ�����ǣ�"+origin);	
		}
		else 
		{
			System.err.println("��Ϣ�������ط�������");
		}
	}
	//����MMS�����ݲ����浽����
	public static void MSGDataAnalyse(String origin_data,String index,String tel_num,String _date)
	{
		System.out.println("���ݴ�������");
		int bgn_index=origin_data.indexOf("0D0A", 38)+4;
		int end_index=origin_data.length()-12;
		String new_data=origin_data.substring(bgn_index,end_index);
		String dateString=new String(StringUtils.hexStringToByte(_date));
		String telsString=new String(StringUtils.hexStringToByte(tel_num));
		byte[] num=StringUtils.hexStringToByte(index);
		String Num=new String(num);
		String path="C:\\Users\\cyt\\Desktop\\ico\\test"+Num+".png";
		saveToImgFile(new_data, path);
		Date date=getDate(dateString);
		Msg_Data_Bean mdb=new Msg_Data_Bean(tel_TidMap.get(telsString),null,path,date);
		new msg_data_Dao().add(mdb);
		System.out.println("�������");
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
	public      static Date getDate(String dateString)
	{
		Date date;
		int year=Integer.parseInt(dateString.substring(0,4))-1900;
		int month=Integer.parseInt(dateString.substring(5, 7))-1;
		int day=Integer.parseInt(dateString.substring(8,10));
		date=new Date(year,month,day);
		return date;
	}
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
		
}
