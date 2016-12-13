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
	//获取Wait_For_Message()函数中的短信属性并将其存入数据库，用于迟滞的统一读取和解析。
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
	//扫描数据库中MsgTitle表中的数据，并依此对短信做读取。
	public static void ReadStoredMsg(Sim800AService s800 )
	{
		Msg_Title_Dao mtd=new Msg_Title_Dao();
		//读取短信格式的消息
		ArrayList<Msg_Title_Bean> text_lst=null;
		text_lst=mtd.Search("Text");
		if(text_lst.size()!=0)
		{
			for(int i=0;i<text_lst.size();i++)
			{
				//遍历数组，对每条消息进行读取
				String index=text_lst.get(i).getnum();
				if(s800.Read_Message(index))
				{
					//将短信的isread状态设为已读
					System.out.println("读取此短信完毕，设置为已读");
					mtd.update(text_lst.get(i));
				}
			}
		}
		else {
			System.out.println("存储中无文本短信");
		}
		//读取彩信格式的短消息
		ArrayList<Msg_Title_Bean> mms_lst=null;
		mms_lst=mtd.Search("MMS");
		if (mms_lst.size()!=0) 
		{
			if(s800.Set_MMS_Environment())
			{	
				System.out.println("hello");
				for(int i=0;i<mms_lst.size();i++)
				{
					System.out.println("开始读取彩信");
					//遍历数组，对每条消息进行读取
					String index=mms_lst.get(i).getnum();
					if(s800.Read_MMS(index))
					{
						//将短信的isread状态设为已读
						System.out.println("读取此短信完毕，设置为已读");
						mtd.update(mms_lst.get(i));
					}
					else {
						System.out.println("读取失败");
					}
				}
			}
			else {
				System.out.println("短信初始化失败，请重启");
			}
		}
		else{
			System.out.println("存储中无未读彩信");
		}
	} 
	//处理text短信的内容
	public static void TextAnalyse(String msg,String tel)
	{
		Device_Info_Dao did=new Device_Info_Dao();
		Terminal_Dev_Dao tdd=new Terminal_Dev_Dao();
		String telnum="'"+new String(StringUtils.hexStringToByte(tel))+"'";
		System.out.println("telnum="+telnum);
		ArrayList<Terminal_Dev_Bean> temp=tdd.Search(3, telnum);
		if (msg.startsWith("01"))
		{
			//01代表收到的是设备信息
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
			    //判断数据库无此号码设备 则增添新设备
			    dib=new DeviceInfoBean();
			    dib.setTerminal_id(terminal_id);
			    dib.setBattery(battery);
			    dib.setVoltage(voltage);
			    dib.setWorkstate(workstate);
			    did.add(dib);
			}
			    else {
			    //检索出数据库中的此号设备
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
			//02代表收到的是心跳确认包
			for (int i=0;i<temp.size();i++)
			{
				//将该设备信息设为通信在线
				temp.get(i).setIslinked(true);
			}
			
		}
		else if(msg.startsWith("03"))
		{
			//03代表收到的是配置信息的回复
			if(msg.endsWith("01"))
			{
				System.out.println("配置时间成功");
			}
			else if (msg.endsWith("02")) {
				System.err.println("配置时间失败，请重试！");
			}
			else {
				System.err.println("接受短信内容有误，请重试！");
			}
		}
		else if (msg.startsWith("04")) 
		{
			//04代表配置电话号码的回复
		    String tel_change=msg.substring(2, msg.length()-2);
		    if (msg.endsWith("01")) 
		    {
		    	System.out.println("配置电话成功");
		    	System.out.println("更改的电话为"+tel_change);
			}
		    else if(msg.endsWith("02")){
		    	System.out.println("更改号码失败");
		    }
		    else
		    {
		    	System.out.println("接收到的短信有误      ");
		    }
			
		}
	}
	//处理gprs裸数据
	public static void GPRSDataAnalyse(String rec)
	{
		if(rec.startsWith("3F"))
		{
			int devnum=Integer.parseInt(rec.substring(2,4),16);	
			String devString=String.valueOf(devnum);
			String msg_Date=new String(StringUtils.hexStringToByte(rec.substring(4,20)));
			String origin=new String(StringUtils.hexStringToByte(rec.substring(20,rec.length()-4)));
			System.out.println("设备编号是："+devString);
			System.out.println("发送日期是："+msg_Date);
			System.out.println("原始数据是："+origin);	
		}
		else 
		{
			System.err.println("信息有误，请重发。。。");
		}
	}
	//处理MMS裸数据并保存到本地
	public static void MSGDataAnalyse(String origin_data,String index,String tel_num,String _date)
	{
		System.out.println("数据处理。。。");
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
		System.out.println("处理完毕");
	}
	//设置中文短信的发送信息格式
	public static String Set_CHINESE_MSG(String msg,String phoneNum)
	{
		String length;
		String telString="";
		length="0D"; 
		if(phoneNum.length()>11){
			     //手机号有前缀+86 ，数字长度为13 
				 //依据sim800通信指令设置手机号的编码
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
		//将string类型转为Unicode码用以发送中文短信
		String unicodemsg=string2Unicode(msg);
		int len=unicodemsg.length()/2;
		String hexlen=Integer.toHexString(len);
		if (hexlen.length()<2) {
			hexlen="0"+hexlen;
		}
		//生成中文短信发送的编码
		String chmsg="001100"+length+"91"+telString+"000800"+hexlen+unicodemsg;
		return chmsg;
	}
	//unicode编码
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
	//hexstring转换图像
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
	//生成java.sql.date类型的数据
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
