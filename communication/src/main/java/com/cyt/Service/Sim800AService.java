package com.cyt.Service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.cyt.Bean.SerialPortBean;
import com.lake.common_utils.stringutils.StringUtils;

public class Sim800AService 
{
	private SerialPortBean SP=null;
	private int delay=800;									//定义传输时延是500ms
	//private int long_delay=150*1000;	//大数据接受时延为1.5s  读取彩信大概200k
	private int longlong_delay=45*1000;   
	//private String Message="";
	private boolean mms_isinit=false;
	public SerialPortBean getSP() {
		return SP;  
	}

	public void setSP(SerialPortBean sP) {
		SP = sP;
	}
	//检查能否进行at指令
	public boolean CheckAT()
	{
		boolean b=false;
		String at="61740D";                                 //61740D是at(换行)的16进制
		SP.write(at,"hex");
		delay(delay);
		if("61740D0D0A4F4B0D0A".equals(SP.getRec_string()))  //61740D0D0A4F4B0D0A是 at(换行)(换行)OK(换行)的16进制表达式
		{
			log("test1 success!");
			b=true;
		}
		else {
			log("test1 fail!");
		}
		return b;
	}
	//2、设置 发送英文短信的环境
	public boolean SetSIM_Text_Work_Environment()
	{
		boolean b=false;
		String ATCMGF="41542B434D47463D310D";//41 54 2B 43 4D 47 46 3D 31 0D是设置短信环境（AT+CMGF=1）的16进制表达式
		SP.write(ATCMGF, "hex");
		delay(delay);
		if("41542B434D47463D310D0D0A4F4B0D0A".equals(SP.getRec_string()))  //返回OK
		{
			log("set environment success!");
			b=true;
		}
		else {
			log("test2 fail!");
		}
		return b;
	}
	//3、发送短信（需要手机号和短信）（英文、与终端设备通信）
	public synchronized boolean  Send_Message(String phoneNum,String msg)
	{
		boolean b=false;
		String ATCMGS="41542B434D47533D";
		byte[] num=phoneNum.getBytes();
		String numstrString=StringUtils.byte2string(num);
		ATCMGS+="22"+numstrString+"22"+"0D";
		SP.write(ATCMGS, "hex");
		delay(delay);
		if ((ATCMGS+"0D0A3E20").equals(SP.getRec_string())) 
		{
			log("正在发送短信。。。");
			byte[] _msg=msg.getBytes();
			String msg_0x=StringUtils.byte2string(_msg)+"1A0D";
			SP.write(msg_0x, "hex");
			b=true; 
			//SP.write("48454C4C4F20434A53211A0D", "hex");
		}
		
		return b;
	}
	//3.1、 发送中文短信（与管理人员进行通信）
	public synchronized   boolean  Send_Message_toManger(String phoneNum,String Msg)
	{
		String ATCMGF="41542B434D47463D300D";  //AT+CMGF=0 PDU模式
		SP.write(ATCMGF, "hex");
		delay(delay);
		if (SP.getRec_string().equals("0D0A4F4B0D0A"))                 //设置成功
		{
			String unicodemsg=DataAnalyseService.string2Unicode(Msg);
			int len=unicodemsg.length()/2-1;
			String hexlen=Integer.toHexString(len);
			String chmsg=DataAnalyseService.Set_CHINESE_MSG(Msg, phoneNum);
			String ATCMGS="61742B636D67733D"+hexlen+"0D";
			SP.write(ATCMGS, "hex");
			delay(delay);
			if (SP.getRec_string().equals(ATCMGS+"0D0A3E20")) {
				log("start send");
				SP.write(chmsg, "ascll");
				SP.write("1a", "hex");
			}
		}
		else {
			log("请重启设备。。。");
		}
		return true;
	}
	//4、获取接收到的短消息,同时读取。
	public  void Wait_For_Message()
	{
		boolean listen=true;
		log("正在准备接收短信。。。");
		while(listen)
		{
			delay(delay);  
			if(SP.getRec_string().startsWith("0D0A2B434D54493A2022534D22"))//获取短消息
			{
				if(SP.getRec_string().endsWith("224D4D532050555348220D0A"))   //判断接收到的是彩信
				{
					log("收到彩信");
					String texttitle=SP.getRec_string();
					int begin_index=28;
					int end_index=texttitle.indexOf("2C", 29);
					final String index=SP.getRec_string().substring(begin_index, end_index);
					String str=new String(StringUtils.hexStringToByte(index));
					log("读取的号码是："+str);
					new Thread(){
						public void run()
						{
							Read_MMS(index);
						}
					}.start();
					//DataAnalyseService.SaveMsgTitle(index, "MMS");
					SP.setRec_string("");
				}					
				else 
				{
					log("收到短信");
					int begin_index=28;
					final String index=SP.getRec_string().substring(begin_index,SP.getRec_string().length()-4);
					log(index);
					new Thread()
					{
						public void run()
						{
							Read_Message(index);
						}
					}.start();
					//DataAnalyseService.SaveMsgTitle(index, "Text");
					   SP.setRec_string("");
				}
			}
			else {
				continue;
			}
		}
		log("结束循环");
	}
	//4*test读取
	
	
	//5、读取第N条短信
	public  synchronized boolean  Read_Message(String _index) 
	{
		boolean b=false;
		String index=_index;
		String  ATCMGF="at+cmgf=1\r";//设置读取形式为text
		SP.write(ATCMGF, "ascll");
		delay(delay);
		if (new String(SP.getRec_byte()).equals(ATCMGF+"\r\nOK\r\n")) 
		{
			log("SUCCESS");
			String ATCMGR="41542B434D47523D"+index+"0D";//读取命令
			SP.write(ATCMGR, "hex");
			delay(delay);
			if(SP.getRec_string().startsWith(ATCMGR+"0D0A2B434D47523A"))
			{ 
				String rec=SP.getRec_string();
				int tel_bgnindex=rec.indexOf("2C",30)+4;
				int tel_endindex=rec.indexOf("2C", tel_bgnindex)-2;
				String tel=rec.substring(tel_bgnindex,tel_endindex);
				int month_bgn_index=rec.indexOf("2F")+2;
				int day_bgn_index=rec.indexOf("2C",month_bgn_index)-6;
				String date=rec.substring((month_bgn_index)-6,day_bgn_index+4);
				String month=new String(StringUtils.hexStringToByte(rec.substring(month_bgn_index,month_bgn_index+4)));
				String day=rec.substring(day_bgn_index,day_bgn_index+4);
				if(!CheckDate(month, day))
				{
					String telephone=new String(StringUtils.hexStringToByte(tel));
					log(telephone+"所对应的机器出现了时间偏离问题");
					String Msg=DataAnalyseService.Set_English_MSG("05", null);
					Send_Message(telephone, Msg);
					return false;
				}
				int text_bgn_index=rec.indexOf("0D",ATCMGR.length()+4);
				int text_end_index=rec.indexOf("0D",text_bgn_index+1);    //去除结尾的\r\nOK\r\n
				//log("length="+rec.length());
				//log("bgnindex="+bgn_index);
				//log("endindex="+end_index);
				String Message=rec.substring(text_bgn_index+4,text_end_index);
				//log("MESSAGE="+Message);
				byte[] msg=StringUtils.hexStringToByte(Message);
				String _msg=new String(msg);     
				log("短信内容是："+_msg);
				DataAnalyseService.TextAnalyse(_msg, tel,date);
				b=true   ;
			}    
			else {
			log("读取短信出错");
			}
		}
		else {
			log("Wrong");
		}
		return b;
	}
	//6、设置彩信工作环境
	public boolean Set_MMS_Environment() 
	{
		boolean b =false;
		//①初始化彩信
		String ATCMMSINIT="61742B636D6D73696E69740D";  
		SP.write(ATCMMSINIT, "hex");
		delay(delay);
		if(SP.getRec_string().equals(ATCMMSINIT+"0D0A4F4B0D0A")||mms_isinit)         				 //返回ok
		{
			//②配置中国移动手机彩信中心地址
			/*
			String URL="mmsc.monternet.com";
			byte[] url_byte=URL.getBytes();
			String urlString=StringUtils.byte2string(url_byte);
			String ATCMMSURL="41542B434D4D5355524C3D22"+urlString+"220D";
			SP.write(ATCMMSURL, "hex");
			delay(delay);
			if(SP.getRec_string().equals(ATCMMSURL+"0D0A4F4B0D0A"))						//返回ok
			{*/
				//③设置承载上下文id
				String ATCMMSCID="61742B636D6D736369643D310D";
				SP.write(ATCMMSCID, "hex");
				delay(delay);
				if (SP.getRec_string().equals(ATCMMSCID+"0D0A4F4B0D0A"))   				 //返回ok
				{
					//④配置网络IP和代理服务端口“10.0.0.172”，80
					String ATCMMSPROTO="61742B636D6D7370726F746F3D2231302E302E302E313732222C38300D";//at+cmmsproto="10.0.0.172",80
					SP.write(ATCMMSPROTO, "hex");
					delay(delay);
					if(SP.getRec_string().equals(ATCMMSPROTO+"0D0A4F4B0D0A"))				//返回ok
					{
						//⑤配置彩信参数
						String ATCMMSSENDCFG="61742B636D6D7373656E646366673D362C332C302C302C322C340D";
						SP.write(ATCMMSSENDCFG, "hex");
						delay(delay);
						if (SP.getRec_string().equals(ATCMMSSENDCFG+"0D0A4F4B0D0A"))       //彩信参数ok
						{		//⑥激活承载1
								String ATSAPBR_Contype_GPRS="61742B73617062723D332C312C22436F6E74797065222C2247505253220D";
								SP.write(ATSAPBR_Contype_GPRS, "hex");
								delay(delay);
								if (SP.getRec_string().equals(ATSAPBR_Contype_GPRS+"0D0A4F4B0D0A"))     //激活一ok
								{
									//⑦激活承载2
									String ATSAPBR_APN_CMWAP="61742B73617062723D332C312C2241504E222C22434D574150220D";
									SP.write(ATSAPBR_APN_CMWAP, "hex");
									delay(delay);
									if (SP.getRec_string().equals(ATSAPBR_APN_CMWAP+"0D0A4F4B0D0A"))    //激活二ok
									{
										//⑧激活承载3
										String ATSAPBR_11="61742B73617062723D312C310D";
										SP.write(ATSAPBR_11, "hex");
										delay(delay);
										if (SP.getRec_string().equals(ATSAPBR_11)||mms_isinit||SP.getRec_string().equals("0D0A4F4B0D0A"))     //激活承载三
										{
											delay(delay);
											if (SP.getRec_string().equals("0D0A4F4B0D0A")||mms_isinit) //激活承载三
											{
												//⑨激活承载4
												String ATSAPBR_21="61742B73617062723D322C310D";
												SP.write(ATSAPBR_21, "hex");
												delay(delay);
												if (SP.getRec_string().endsWith("0D0A4F4B0D0A"))
												{
													log("设置环境成功！");
													return true;
												}
												else {
													logerr("激活承载四失败！");
												}
											}
											else {
												logerr("激活承载三失败！");
											}
											
										}
										else{
											logerr("激活承载三失败！");
										}
											
										
									}
									else {
										logerr("激活二失败");
									}
								}
						}
						else {
							logerr("激活承载失败！");
						}
					}
					else {
						logerr("设置网络ip失败，请重启。。。");
					}
				}
				else {
					logerr("设置上下文id失败，请重启。。。");
				}
			/*}
			else {
				logerr("设置短信中心失败，请重启。。。");
			}
			*/
		}
		else if(SP.getRec_string().equals(ATCMMSINIT+"0D0A4552524F520D0A")) 
		{
			log("彩信系统已经初始化。。。");
			mms_isinit=true;
			return Set_MMS_Environment();
		}
		else {
			logerr("设置失败，请重启。。。");
		}
		return b;
	}
	
	//8、读取彩信MMS并存储至本地文件夹，将路径存入数据库
	public synchronized boolean Read_MMS(String _index)
	{
		String index=_index;
		String ATCMMSEDIT="61742B636D6D73656469743D300D";    //at+cmmsedit=0;
		SP.write(ATCMMSEDIT, "hex");
		delay(delay);
		if (!SP.getRec_string().equals(ATCMMSEDIT+"0D0A4F4B0D0A")) 
		{
			logerr("设置退出编辑失败！");
			return false;
		}
		boolean b=false;
		String ATCMMSRECV="61742B636D6D73726563763D"+index+"0D";       //at+cmmsrecv=index;
		SP.write(ATCMMSRECV, "hex");
		delay(delay);
		if (SP.getRec_string().equals(ATCMMSRECV)) 
		{
			log("-------------------等待cmmsrecv回复-------------------");
			delay(longlong_delay);
			if (SP.getRec_string().startsWith("0D0A2B434D4D5352454356"))    //+CMMSRECV
			{
				String temp_dataString=SP.getRec_string();
				int tel_bgn_index=temp_dataString.indexOf("22");
				int tel_end_index=temp_dataString.indexOf("22",tel_bgn_index+2);
				int date_bgn_index=tel_end_index+6;
				log("开始读取彩信");
				//System.out.println(temp_dataString.length());
				//System.out.println(tel_bgn_index);
				//System.out.println(tel_end_index);
				//System.out.println(date_bgn_index);
				String date=temp_dataString.substring(date_bgn_index, date_bgn_index+20);//获取日期
				String tel_num=temp_dataString.substring(tel_bgn_index+2,tel_end_index);//获取电话号码
				String ATCMMSREAD="61742B636D6D73726561643D320D0A";                  //at+cmmsrecv=2
				SP.setRec_string("");
				SP.write(ATCMMSREAD, "hex");
				String origin_data="";
				//对接收到的流截取数据部分
				//delay(long_delay);
				while(true)
				{
					delay(delay);
					if (!"".equals(SP.getRec_string())&&SP.getRec_string().endsWith("0D0A4F4B0D0A"))
					{
						origin_data=SP.getRec_string();
						break;
					}
				}
				DataAnalyseService.MSGDataAnalyse(origin_data,index,tel_num,date);
				//if(DeleteMMS(index))
				//{b=true;}
				b=true;
			}
		}
		
		return b;
	}
	//9、删除对应彩信
	public boolean DeleteMMS(String index)
	{
		String ATCMGD="41542B434D47443D"+index+"0D"; 
		SP.write(ATCMGD, "hex");
		delay(delay);
		if (SP.getRec_string().equals("0D0A4F4B0D0A")) {
			return true;
		}
		else {
			return false;
		}
	}
	//10、初始化sim GPRS服务
	public boolean StartGPRS()
	{
		boolean b=false;
		//1、测试网络注册情况  返回1,5都成功
		String _atcreg="at+creg?\r";
		String atcreg=StringUtils.str2hexstr(_atcreg);
		SP.write(atcreg, "hex");
		delay(delay);
		if(SP.getRec_string().equals(atcreg+"0D0A2B435245473A20302C310D0A0D0A4F4B0D0A")||               //返回数据是1,5即可
				SP.getRec_string().equals(atcreg+"0D0A2B435245473A20302C350D0A0D0A4F4B0D0A"))
		{
			log("网络已经注册成功！");
			//2、 查询模块是否附着 GPRS 网络
			String _atcgatt="at+cgatt?\r";
			String atcgatt=StringUtils.str2hexstr(_atcgatt);
			SP.write(atcgatt, "hex");
			delay(delay);
			if (SP.getRec_string().equals(atcgatt+"0D0A2B43474154543A20310D0A0D0A4F4B0D0A"))     //返回ok
			{
				log("成功附着GPRS网络");
				//3、设置APN
				String _atcstt="at+cstt\r";
				String atcstt=StringUtils.str2hexstr(_atcstt);
				SP.write(atcstt, "hex");
				delay(delay);
				if(SP.getRec_string().equals(atcstt+"0D0A4F4B0D0A"))              //返回ok
				{
					log("设置APN成功");
					//4、激活移动场景
					String _atciicr="at+ciicr\r";
					String atciicr=StringUtils.str2hexstr(_atciicr);
					SP.write(atciicr, "hex");
					delay(delay);
					if (SP.getRec_string().equals(atciicr+"0D0A4F4B0D0A"))                     //返回ok
					{
						log("移动场景已经激活！");
						b=true;
					}
					else{
						logerr("移动场景激活失败！");
					}
				}
				else
				{
					logerr("设置APN失败，请重试！");
				}
			}
			else {
				logerr("模块未附着gprs网络，请重试！");
			}
			
		}
		else
		{
			logerr("网络未注册。。。请注册");
		}
		return b;
	}
	//对时操作
	private boolean CheckDate(String terminal_Month,String terminal_Day)
	{
		boolean b=false;
		Date date=new Date();
		DateFormat format_month=new SimpleDateFormat("MM");
		DateFormat format_day=new SimpleDateFormat("dd");
		String local_month=format_month.format(date);
		String local_day=format_day.format(date);
		if (local_month.equals(terminal_Month)&&local_day.equals(terminal_Day)) {
			b=true;
		}
		return b;
	}
	
	//delay接收时延函数
	public void delay(int delay)
	{
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	//log函数
	private void log(String msg)
	{
		DateFormat format=new SimpleDateFormat("YY-MM-dd HH:mm:ss");
		String time=format.format(new Date());
		System.out.println(time+"--> "+msg);
	}
	private void logerr(String msg)
	{
		DateFormat format=new SimpleDateFormat("YY-MM-dd HH:mm:ss");
		String time=format.format(new Date());
		System.err.println(time+"--> "+msg);
	}
 }


