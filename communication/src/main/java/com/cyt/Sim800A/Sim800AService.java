package com.cyt.Sim800A;

import java.io.File;

import javax.imageio.stream.FileImageOutputStream;

import com.cyt.Bean.SerialPortBean;
import com.lake.common_utils.stringutils.StringUtils;

public class Sim800AService 
{
	private SerialPortBean SP=null;
	private int delay=800;									//定义传输时延是500ms
	private int long_delay=2000;							//大数据接受时延为1.5s
	//private String Message="";
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
			System.out.println("test1 success!");
			b=true;
		}
		else {
			System.out.println("test1 fail!");
		}
		return b;
	}
	//2、设置 发送短信的环境
	public boolean SetSIM_Text_Work_Environment()
	{
		boolean b=false;
		String ATCMGF="41542B434D47463D310D";//41 54 2B 43 4D 47 46 3D 31 0D是设置短信环境（AT+CMGF=1）的16进制表达式
		SP.write(ATCMGF, "hex");
		delay(delay);
		if("41542B434D47463D310D0D0A4F4B0D0A".equals(SP.getRec_string()))  //返回OK
		{
			System.out.println("set environment success!");
			b=true;
		}
		else {
			System.out.println("test2 fail!");
		}
		return b;
	}
	//3、发送短信（需要手机号和短信）
	public boolean Send_Message(String phoneNum,String msg)
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
			System.out.println("设置成功。。。正在发送。。。");
			byte[] _msg=msg.getBytes();
			String msg_0x=StringUtils.byte2string(_msg)+"1A0D";
			SP.write(msg_0x, "hex");
			b=true; 
			//SP.write("48454C4C4F20434A53211A0D", "hex");
		}
		
		return b;
	}
	//4、等待读取短消息
	public  void Wait_For_Message()
	{
		boolean listen=true;
		System.out.println("正在准备接收短信。。。");
		while(listen)
		{
			delay(delay);  
			if(SP.getRec_string().startsWith("0D0A2B434D54493A2022534D22"))//0D0A2B434D54493A2022534D222C340D0A
			{
				String index=SP.getRec_string().substring(28, 30);
				System.out.println(index);
				if(Read_Message(index))
				{
					System.out.println("读取成功！");
				}
				else {
					System.out.println("读取错误！");
				}
				SP.setRec_string("");
			}
			else {
				//System.out.println("");
				continue;
			}
		}
		System.out.println("结束循环");
	}
	//5、读取第N条短信
	public  boolean  Read_Message(String index) 
	{
		boolean b=false;
		String ATCMGR="41542B434D47523D"+index+"0D0A";//读取命令
		SP.write(ATCMGR, "hex");
		
		if(SP.getRec_string().startsWith("0D0A2B434D47523A0D0A"))
		{
			String rec=SP.getRec_string();
			int bgn_index=rec.indexOf("0D",5);
			int end_index=rec.indexOf("0D", bgn_index);
			String Message=rec.substring(bgn_index+2,end_index);
			System.out.println("MESSAGE="+Message);
			byte[] msg=StringUtils.hexStringToByte(Message);
			String _msg=new String(msg);
			System.out.println("短信内容是："+_msg);
			b=true;
		}
		else {
			System.out.println("读到的信息有误");
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
		if(SP.getRec_string().equals(ATCMMSINIT+"0D0A4F4B0D0A"))         				 //返回ok
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
										if (SP.getRec_string().equals(ATSAPBR_11))     //激活承载三
										{
											delay(delay);
											if (SP.getRec_string().equals("0D0A4F4B0D0A")) //激活承载三
											{
												//⑨激活承载4
												String ATSAPBR_21="61742B73617062723D322C310D";
												SP.write(ATSAPBR_21, "hex");
												delay(delay);
												if (SP.getRec_string().endsWith("0D0A4F4B0D0A"))
												{
													System.out.println("设置环境成功！");
													b=true;
												}
												else {
													System.out.println("激活承载四失败！");
												}
											}
											else {
												System.out.println("激活承载三失败！");
											}
											
										}
										else{
											System.out.println("激活承载三失败！");
										}
											
										
									}
									else {
										System.out.println("激活二失败");
									}
								}
						}
						else {
							System.out.println("激活承载失败！");
						}
					}
					else {
						System.out.println("设置网络ip失败，请重启。。。");
					}
				}
				else {
					System.out.println("设置上下文id失败，请重启。。。");
				}
			/*}
			else {
				System.out.println("设置短信中心失败，请重启。。。");
			}
			*/
		}
		else if(SP.getRec_string().equals(ATCMMSINIT+"4552524F520D0A")) 
		{
			System.out.println("彩信系统已经初始化。。。");
		}
		else {
			System.out.println("设置失败，请重启。。。");
		}
		return b;
	}
	//7、等待接受彩信
	public void Wait_For_MMS()
	{
			boolean listen=true;
			String ATCMMSEDIT="61742B636D6D73656469743D300D";
			SP.write(ATCMMSEDIT, "hex");
			delay(delay);
			if (SP.getRec_string().equals(ATCMMSEDIT+"0D0A4F4B0D0A"))
			{
				System.out.println("关闭编辑模式，正在准备接收短信。。。");
			}			
			while(listen)
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(SP.getRec_string().startsWith("0D0A2B434D54493A2022534D22"))//0D0A2B434D54493A2022534D222C340D0A
				{
					String index=SP.getRec_string().substring(28, 30);
					System.out.println(index);
					if(Read_Message(index))
					{
						System.out.println("读取成功！");
					}
					else {
						System.out.println("读取错误！");
					}
					SP.setRec_string("");
				}
				else {
					//System.out.println("");
					continue;
				}
			}
			System.out.println("结束循环");
			
	}
	//8、读取彩信MMS并存储至本地文件夹，将路径存入数据库
	public String Read_MMS(String index)
	{
		String rec_MMS="";
		String ATCMMSRECV="61742B636D6D73726563763D"+index+"0D";       //at+atcmmsrecv=index;
		SP.write(ATCMMSRECV, "hex");
		delay(long_delay);
		if (SP.getRec_string().startsWith(ATCMMSRECV+"0D0A2B434D4D5352454356"))    //+CMMSRECV
		{
			String ATCMMSREAD="61742B636D6D73726561643D320D0A";                  //at+cmmsrecv=2
			SP.write(ATCMMSREAD, "hex");
			delay(long_delay);
			//对接收到的流截取数据部分
			String origin_data=SP.getRec_string();
			int bgn_index=origin_data.indexOf("0D0A", 38)+4;
			int end_index=origin_data.length()-12;
			String new_data=origin_data.substring(bgn_index,end_index);
			byte[]img_data=StringUtils.hexStringToByte(new_data);
			byte[] num=StringUtils.hexStringToByte(index);
			String Num=new String(num);
			String path="C:\\Users\\cyt\\Desktop\\ico\\test"+Num+".png";
			byte2image(img_data, path);
		}
		
		return rec_MMS;
	}
	
	
	//delay接收时延函数
	public void delay(int m_s)
	{
		try {
			Thread.sleep(m_s);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//字节转换图像
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

}
