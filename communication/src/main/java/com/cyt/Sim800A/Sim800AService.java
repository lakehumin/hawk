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
	//����ܷ����atָ��
	public boolean CheckAT()
	{
		boolean b=false;
		String at="61740D";                                 //61740D��at(����)��16����
		SP.write(at,"hex");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if("61740D0D0A4F4B0D0A".equals(SP.getRec_string()))  //61740D0D0A4F4B0D0A�� at(����)(����)OK(����)��16���Ʊ��ʽ
		{
			System.out.println("test1 success!");
			b=true;
		}
		else {
			System.out.println("test1 fail!");
		}
		return b;
	}
	//2������ ���Ͷ��ŵĻ���
	public boolean SetSIM_Text_Work_Environment()
	{
		boolean b=false;
		String ATCMGF="41542B434D47463D310D";//41 54 2B 43 4D 47 46 3D 31 0D�����ö��Ż�����AT+CMGF=1����16���Ʊ��ʽ
		SP.write(ATCMGF, "hex");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if("41542B434D47463D310D0D0A4F4B0D0A".equals(SP.getRec_string()))  //����OK
		{
			System.out.println("set environment success!");
			b=true;
		}
		else {
			System.out.println("test2 fail!");
		}
		return b;
	}
	//3�����Ͷ��ţ���Ҫ�ֻ��źͶ��ţ�
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
			System.out.println("���óɹ����������ڷ��͡�����");
			byte[] _msg=msg.getBytes();
			String msg_0x=byte2string(_msg)+"1A0D";
			SP.write(msg_0x, "hex");
			b=true; 
			//SP.write("48454C4C4F20434A53211A0D", "hex");
		}
		
		return b;
	}
	//4��׼����ȡ����Ϣ
	public  void Waiting_Read_Message()
	{
		boolean listen=true;
		System.out.println("����׼�����ն��š�����");
		while(listen)
		{
			//System.out.println("1");
			if(SP.getRec_string().startsWith("0D0A2B434D54493A2022534D22"))//0D0A2B434D54493A2022534D222C340D0A
			{
				String index=SP.getRec_string().substring(28, 30);
				System.out.println("index");
				if(Read_Message(index))
				{
					System.out.println("��ȡ�ɹ���");
				}
				else {
					System.out.println("��ȡ����");
				}
				SP.setRec_string("");
			}
			else {
				System.out.println("");
			}
		}
	}
	//5����ȡ��N������
	public synchronized boolean  Read_Message(String index) 
	{
		boolean b=false;
		String ATCMGR="41542B434D47523D"+index+"0D0A";//��ȡ����
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
			System.out.println("���������ǣ�"+_msg);
			b=true;
		}
		else {
			System.out.println("��������Ϣ����");
		}
		return b;
	}
	
	
	
	
	
	//byteתstring
	 public  String byte2string(byte[] data){
		    if(data==null||data.length<=1) return "0x1";
		    if(data.length>8000000) return "0x2";
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
	 //hexstringתbyte
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
