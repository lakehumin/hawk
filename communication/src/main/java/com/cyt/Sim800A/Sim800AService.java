package com.cyt.Sim800A;

import java.io.File;

import javax.imageio.stream.FileImageOutputStream;

import com.cyt.Bean.SerialPortBean;
import com.lake.common_utils.stringutils.StringUtils;

public class Sim800AService 
{
	private SerialPortBean SP=null;
	private int delay=800;									//���崫��ʱ����500ms
	private int long_delay=2000;							//�����ݽ���ʱ��Ϊ1.5s
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
		delay(delay);
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
		delay(delay);
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
		String numstrString=StringUtils.byte2string(num);
		ATCMGS+="22"+numstrString+"22"+"0D";
		SP.write(ATCMGS, "hex");
		delay(delay);
		if ((ATCMGS+"0D0A3E20").equals(SP.getRec_string())) 
		{
			System.out.println("���óɹ����������ڷ��͡�����");
			byte[] _msg=msg.getBytes();
			String msg_0x=StringUtils.byte2string(_msg)+"1A0D";
			SP.write(msg_0x, "hex");
			b=true; 
			//SP.write("48454C4C4F20434A53211A0D", "hex");
		}
		
		return b;
	}
	//4���ȴ���ȡ����Ϣ
	public  void Wait_For_Message()
	{
		boolean listen=true;
		System.out.println("����׼�����ն��š�����");
		while(listen)
		{
			delay(delay);  
			if(SP.getRec_string().startsWith("0D0A2B434D54493A2022534D22"))//0D0A2B434D54493A2022534D222C340D0A
			{
				String index=SP.getRec_string().substring(28, 30);
				System.out.println(index);
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
				//System.out.println("");
				continue;
			}
		}
		System.out.println("����ѭ��");
	}
	//5����ȡ��N������
	public  boolean  Read_Message(String index) 
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
			byte[] msg=StringUtils.hexStringToByte(Message);
			String _msg=new String(msg);
			System.out.println("���������ǣ�"+_msg);
			b=true;
		}
		else {
			System.out.println("��������Ϣ����");
		}
		return b;
	}
	//6�����ò��Ź�������
	public boolean Set_MMS_Environment() 
	{
		boolean b =false;
		//�ٳ�ʼ������
		String ATCMMSINIT="61742B636D6D73696E69740D";  
		SP.write(ATCMMSINIT, "hex");
		delay(delay);
		if(SP.getRec_string().equals(ATCMMSINIT+"0D0A4F4B0D0A"))         				 //����ok
		{
			//�������й��ƶ��ֻ��������ĵ�ַ
			/*
			String URL="mmsc.monternet.com";
			byte[] url_byte=URL.getBytes();
			String urlString=StringUtils.byte2string(url_byte);
			String ATCMMSURL="41542B434D4D5355524C3D22"+urlString+"220D";
			SP.write(ATCMMSURL, "hex");
			delay(delay);
			if(SP.getRec_string().equals(ATCMMSURL+"0D0A4F4B0D0A"))						//����ok
			{*/
				//�����ó���������id
				String ATCMMSCID="61742B636D6D736369643D310D";
				SP.write(ATCMMSCID, "hex");
				delay(delay);
				if (SP.getRec_string().equals(ATCMMSCID+"0D0A4F4B0D0A"))   				 //����ok
				{
					//����������IP�ʹ������˿ڡ�10.0.0.172����80
					String ATCMMSPROTO="61742B636D6D7370726F746F3D2231302E302E302E313732222C38300D";//at+cmmsproto="10.0.0.172",80
					SP.write(ATCMMSPROTO, "hex");
					delay(delay);
					if(SP.getRec_string().equals(ATCMMSPROTO+"0D0A4F4B0D0A"))				//����ok
					{
						//�����ò��Ų���
						String ATCMMSSENDCFG="61742B636D6D7373656E646366673D362C332C302C302C322C340D";
						SP.write(ATCMMSSENDCFG, "hex");
						delay(delay);
						if (SP.getRec_string().equals(ATCMMSSENDCFG+"0D0A4F4B0D0A"))       //���Ų���ok
						{		//�޼������1
								String ATSAPBR_Contype_GPRS="61742B73617062723D332C312C22436F6E74797065222C2247505253220D";
								SP.write(ATSAPBR_Contype_GPRS, "hex");
								delay(delay);
								if (SP.getRec_string().equals(ATSAPBR_Contype_GPRS+"0D0A4F4B0D0A"))     //����һok
								{
									//�߼������2
									String ATSAPBR_APN_CMWAP="61742B73617062723D332C312C2241504E222C22434D574150220D";
									SP.write(ATSAPBR_APN_CMWAP, "hex");
									delay(delay);
									if (SP.getRec_string().equals(ATSAPBR_APN_CMWAP+"0D0A4F4B0D0A"))    //�����ok
									{
										//�༤�����3
										String ATSAPBR_11="61742B73617062723D312C310D";
										SP.write(ATSAPBR_11, "hex");
										delay(delay);
										if (SP.getRec_string().equals(ATSAPBR_11))     //���������
										{
											delay(delay);
											if (SP.getRec_string().equals("0D0A4F4B0D0A")) //���������
											{
												//�ἤ�����4
												String ATSAPBR_21="61742B73617062723D322C310D";
												SP.write(ATSAPBR_21, "hex");
												delay(delay);
												if (SP.getRec_string().endsWith("0D0A4F4B0D0A"))
												{
													System.out.println("���û����ɹ���");
													b=true;
												}
												else {
													System.out.println("���������ʧ�ܣ�");
												}
											}
											else {
												System.out.println("���������ʧ�ܣ�");
											}
											
										}
										else{
											System.out.println("���������ʧ�ܣ�");
										}
											
										
									}
									else {
										System.out.println("�����ʧ��");
									}
								}
						}
						else {
							System.out.println("�������ʧ�ܣ�");
						}
					}
					else {
						System.out.println("��������ipʧ�ܣ�������������");
					}
				}
				else {
					System.out.println("����������idʧ�ܣ�������������");
				}
			/*}
			else {
				System.out.println("���ö�������ʧ�ܣ�������������");
			}
			*/
		}
		else if(SP.getRec_string().equals(ATCMMSINIT+"4552524F520D0A")) 
		{
			System.out.println("����ϵͳ�Ѿ���ʼ��������");
		}
		else {
			System.out.println("����ʧ�ܣ�������������");
		}
		return b;
	}
	//7���ȴ����ܲ���
	public void Wait_For_MMS()
	{
			boolean listen=true;
			String ATCMMSEDIT="61742B636D6D73656469743D300D";
			SP.write(ATCMMSEDIT, "hex");
			delay(delay);
			if (SP.getRec_string().equals(ATCMMSEDIT+"0D0A4F4B0D0A"))
			{
				System.out.println("�رձ༭ģʽ������׼�����ն��š�����");
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
						System.out.println("��ȡ�ɹ���");
					}
					else {
						System.out.println("��ȡ����");
					}
					SP.setRec_string("");
				}
				else {
					//System.out.println("");
					continue;
				}
			}
			System.out.println("����ѭ��");
			
	}
	//8����ȡ����MMS���洢�������ļ��У���·���������ݿ�
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
			//�Խ��յ�������ȡ���ݲ���
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
	
	
	//delay����ʱ�Ӻ���
	public void delay(int m_s)
	{
		try {
			Thread.sleep(m_s);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//�ֽ�ת��ͼ��
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
