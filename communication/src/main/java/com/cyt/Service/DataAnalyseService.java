package com.cyt.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections.bidimap.DualHashBidiMap;

import com.cyt.Bean.AlarmEventBean;
import com.cyt.Bean.DeviceInfoBean;
import com.cyt.Bean.MsgDataBean;
import com.cyt.Bean.MsgTitleBean;
import com.cyt.Bean.TerminalDevBean;
import com.cyt.DAO.AlarmEventDao;
import com.cyt.DAO.DeviceInfoDao;
import com.cyt.DAO.MsgTitleDao;
import com.cyt.DAO.TerminalDevDao;
import com.cyt.DAO.MsgDataDao;
import com.lake.common_utils.stringutils.StringUtils;

public class DataAnalyseService {
	//private static byte[] buffer=null;
	private static DualHashBidiMap tel_TidMap=null;
	private static HashMap<String, String> eventMap=null;
	private static boolean isinit=false;
	public static Sim800AService s800=null;
	public static void s800init(Sim800AService sim800)
	{
		s800=sim800;
	}
	private static void init(){
		tel_TidMap=new DualHashBidiMap();
		TerminalDevDao tdo=new TerminalDevDao();
		ArrayList<TerminalDevBean> tdblst=tdo.SearchAll();
		for (TerminalDevBean tdb : tdblst) {
			tel_TidMap.put(tdb.getTel_num(), tdb.getTerminal_id());
		}
		eventMap=new HashMap<String, String>();
		eventMap.put("LowPower", "�͵���");
		eventMap.put("Broken", "�豸��");
		eventMap.put("VolAbnormal", "��ѹ");
		eventMap.put("Offline", "ͨ���ж�");
		isinit=true;
		}
	private static void checkinit()
	{
		if (!isinit) {
			init();
		}
		else {
			return;
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
		checkinit();
		boolean b=false;
		MsgTitleBean mtb=new MsgTitleBean();
		mtb.setnum(index);
		mtb.setType(type);
		mtb.setRead(false);
		if(new MsgTitleDao().add(mtb))
		{
			b=true;
		}
		return b;
	}
	//ɨ�����ݿ���MsgTitle���е����ݣ������˶Զ�������ȡ��
	public static void ReadStoredMsg(Sim800AService s800 )
	{
		checkinit();
		MsgTitleDao mtd=new MsgTitleDao();
		//��ȡ���Ÿ�ʽ����Ϣ
		ArrayList<MsgTitleBean> text_lst=null;
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
		ArrayList<MsgTitleBean> mms_lst=null;
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
		checkinit();
		DeviceInfoDao did=new DeviceInfoDao();
		TerminalDevDao tdd=new TerminalDevDao();
		String telnum="'"+new String(StringUtils.hexStringToByte(tel))+"'";
		log("telnum="+telnum);
		ArrayList<TerminalDevBean> temp=tdd.Search(3,telnum);
		if (msg.startsWith("01"))
		{
			//01�����յ������豸��Ϣ
			String battery=new String(StringUtils.hexStringToByte(msg.substring(2,6)));
			log("battery="+battery);
			String voltage=Integer.parseInt(msg.substring(6,7),16)+""+"."+Integer.parseInt(msg.substring(7, 8),16);
			log("voltage="+voltage);
			String workstate=getWorkstate(msg.substring(8,10));
			log("workstate="+workstate);	
			for (int i = 0; i < temp.size(); i++) 
			{
			     String terminal_id=temp.get(i).getTerminal_id();
			     System.out.println("terminal_id="+terminal_id);
			     //�����ݿ�����¼�¼
			     DeviceInfoBean dib= new DeviceInfoBean();		    
			     dib.setTerminal_id(terminal_id);
			     dib.setBattery(battery);
			     dib.setVoltage(voltage);
			     dib.setWorkstate(workstate);
			     dib.setDate(date);
			     did.add(dib);
			     HandleEvent(workstate,terminal_id);
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
	//����gprs����ͷ()
	public static void GPRSDataAnalyse(String rec)
	{
		checkinit();
		String type=rec.substring(0,2);
		String terminal_id=rec.substring(2,5);
		String date=rec.substring(5,13);
		String msg=rec.substring(13);
		String telhex=StringUtils.str2hexstr((String)tel_TidMap.getKey(terminal_id));
		//System.out.println(type+"\t"+terminal_id+"\t"+date+"\t"+msg+"\t"+tel_TidMap.getKey(terminal_id)+"\t");
		if ("01".equals(type)) {
			msg=msg.substring(0,msg.length()-4);
			TextAnalyse(msg, telhex, date);
		}
		else if ("02".equals(type)) {
			log("����ͼ��"+terminal_id+"\t"+date);
			MSGDataAnalyse(msg, telhex, date);
		}
		else {
			logerr("���յ����Ϊ"+terminal_id+"�Ĵ������ݡ�����");
		}
	}
	//��ǰ�˵�ͨ��
	public static String TCPDataAnalyse(String rec)
	{
		checkinit();
		String Msg="";
		if (rec.startsWith("02")) {
			Msg="02";
		}
		else if (rec.startsWith("03")) {
			String terminal_id=rec.substring(2,6);
			String cmd=rec.substring(6,8);
			String content=rec.substring(8);
			if(s800.Send_Message((String)tel_TidMap.get(terminal_id),Set_English_MSG(cmd, content)))
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
	public static void MSGDataAnalyse(String img_data,String tel_num,String _date)
	{
		checkinit();
		log("ͼ�����ݴ�������");
		String telsString=new String(StringUtils.hexStringToByte(tel_num));
		String pathprefix="D:\\UI\\hawkui\\public\\monitorImg\\";
		String path=pathprefix+tel_TidMap.get(telsString)+"_"+_date+".png";
		String pathindata="monitorImg\\"+tel_TidMap.get(telsString)+"_"+_date+".png";
		System.out.println(_date);
		System.out.println(path);
		saveToImgFile(img_data,path);
		MsgDataDao mdd=new MsgDataDao();
		MsgDataBean temp=mdd.Searchid((String)tel_TidMap.get(telsString), _date);
		if (temp!=null) {
			temp.setImg_path(path);
			mdd.add(temp);
		}
		else {
			MsgDataBean mdb=new MsgDataBean((String)tel_TidMap.get(telsString),pathindata,_date);
			mdd.add(mdb);
		}
		log("ͼ�������");
	}
	//����Ӣ�Ķ��ŵķ��͸�ʽ
	public static String Set_English_MSG(String cmd,String content)
	{
		checkinit();
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
		if(phoneNum.length()>11){
			     //�ֻ�����ǰ׺+86 �����ֳ���Ϊ13 
				 //����sim800ͨ��ָ�������ֻ��ŵı���
			length="0D";
				for(int i=1;i<phoneNum.length()-1;i=i+2){
					telString+=String.valueOf(phoneNum.charAt(i+1))+String.valueOf(phoneNum.charAt(i));
					}
				telString+="F"+String.valueOf(phoneNum.charAt(phoneNum.length()-1));
		  }    
		else {
			length="0D";
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
		chmsg=chmsg.toUpperCase();
		return chmsg;
	}
	//unicode����
	public static String string2Unicode(String string) {  
        StringBuffer unicode = new StringBuffer();  
        for (int i = 0; i < string.length(); i++) {
        	String hex=Integer.toHexString(string.charAt(i));
            while(hex.length()<4)
            {
            	hex="0"+hex;
            }
            unicode.append(hex);
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
	@SuppressWarnings("deprecation")
	public  static Date getDate(String dateString)
	{
		Date date;
		int year=Integer.parseInt(dateString.substring(0,4))-1900;
		int month=Integer.parseInt(dateString.substring(5, 7))-1;
		int day=Integer.parseInt(dateString.substring(8,10));
		date=new Date(year,month,day);
		return date;
	}
	//����״̬�����ʹ���
	private static String getWorkstate(String index)
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
			return "Broken";
		}
		else {
			return "Exception";
		}
	}
	//�����豸�쳣��Ϣ���ϴ�ǰ�ˣ������ݿ�ķ�ʽ��
	public static void HandleEvent(String event,String terminal_id)
	{
		if ("Normal".equals(event)) {
			return;
		}
		String undeal="0";
		String underdealing="1";
		int count=0;
		TerminalDevDao tdd=new TerminalDevDao();
		AlarmEventDao aedao=new AlarmEventDao();
		ArrayList<AlarmEventBean> undeallst=aedao.Search(terminal_id, undeal);
		ArrayList<AlarmEventBean> underdeallst=aedao.Search(terminal_id, underdealing);
		//.......������¼�ڰ���δ������ϵ��쳣��¼�����뱾�ε��쳣��ͬ������м�¼���������¼��
		for (int i = 0; i < undeallst.size(); i++) {
			if (undeallst.get(i).getEvent().equals(event)) {
				count++;
			}
		}
		for (int i = 0; i < underdeallst.size(); i++) {
			if (underdeallst.get(i).getEvent().equals(event)) {
				count++;
			}
		}
		System.out.println("count= "+count);
		//��������  ÿ�μ�������˴ε��쳣��ͬ ��count������ֻ��count=0ʱ������˵������ͬ�쳣�¼�����ʱ������Ӽ�¼  
		if (count==0) {
			DateFormat format=new SimpleDateFormat("YY-MM-dd");
			String eventdate=format.format(new java.util.Date());
			AlarmEventBean alarm=new AlarmEventBean();
			alarm.setTerminal_id(terminal_id);
			alarm.setEvent(event);
			alarm.setEventdate(eventdate);
			aedao.add(alarm);
			//�����˷����쳣����
			TerminalDevBean alarmDev=tdd.Searchid(terminal_id);
			String managerTel="15905195757";
			String message="���棺����豸�����"+terminal_id+"����ַ��"+alarmDev.getLocation()+"������"+eventMap.get(event)+"�쳣���뼰ʱ����"
			               +"\r\n"+"�������Է�������";
			if (s800==null) {
				System.out.println("s800Ϊ��");
				return;
			}
			s800.Send_Message_toManger(managerTel, message);
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
