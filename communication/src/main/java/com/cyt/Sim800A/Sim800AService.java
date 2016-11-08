package com.cyt.Sim800A;

import com.cyt.Bean.SerialPortBean;

public class Sim800AService 
{
	private SerialPortBean SP=null;
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
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		String numstrString=byte2string(num);
		ATCMGS+="22"+numstrString+"22"+"0D";
		SP.write(ATCMGS, "hex");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ((ATCMGS+"0D0A3E20").equals(SP.getRec_string())) 
		{
			System.out.println("设置成功。。。正在发送。。。");
			byte[] _msg=msg.getBytes();
			String msg_0x=byte2string(_msg)+"1A0D";
			SP.write(msg_0x, "hex");
			b=true; 
			//SP.write("48454C4C4F20434A53211A0D", "hex");
		}
		
		return b;
	}
	//4、准备读取短消息
	public  void Waiting_Read_Message()
	{
		boolean listen=true;
		System.out.println("正在准备接收短信。。。");
		while(listen)
		{
			//System.out.println("1");
			if(SP.getRec_string().startsWith("0D0A2B434D54493A2022534D22"))//0D0A2B434D54493A2022534D222C340D0A
			{
				String index=SP.getRec_string().substring(28, 30);
				System.out.println("index");
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
				System.out.println("");
			}
		}
	}
	//5、读取第N条短信
	public synchronized boolean  Read_Message(String index) 
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
			byte[] msg=hexStringToByte(Message);
			String _msg=new String(msg);
			System.out.println("短信内容是："+_msg);
			b=true;
		}
		else {
			System.out.println("读到的信息有误");
		}
		return b;
	}
	
	
	
	
	
	//byte转string
	 public  String byte2string(byte[] data){
		    if(data==null||data.length<=1) return "0x1";
		    if(data.length>8000000) return "0x2";
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
	 //hexstring转byte
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
