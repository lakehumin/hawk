package com.cyt.Service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.cyt.Bean.SerialPortBean;
import com.lake.common_utils.stringutils.StringUtils;

public class Sim800AService 
{
	private SerialPortBean SP=null;
	private int delay=800;									//���崫��ʱ����500ms
	//private int long_delay=150*1000;	//�����ݽ���ʱ��Ϊ1.5s  ��ȡ���Ŵ��200k
	private int longlong_delay=45*1000;   
	//private String Message="";
	private boolean mms_isinit=false;
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
			log("test1 success!");
			b=true;
		}
		else {
			log("test1 fail!");
		}
		return b;
	}
	//2������ ����Ӣ�Ķ��ŵĻ���
	public boolean SetSIM_Text_Work_Environment()
	{
		boolean b=false;
		String ATCMGF="41542B434D47463D310D";//41 54 2B 43 4D 47 46 3D 31 0D�����ö��Ż�����AT+CMGF=1����16���Ʊ��ʽ
		SP.write(ATCMGF, "hex");
		delay(delay);
		if("41542B434D47463D310D0D0A4F4B0D0A".equals(SP.getRec_string()))  //����OK
		{
			log("set environment success!");
			b=true;
		}
		else {
			log("test2 fail!");
		}
		return b;
	}
	//3�����Ͷ��ţ���Ҫ�ֻ��źͶ��ţ���Ӣ�ġ����ն��豸ͨ�ţ�
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
			log("���ڷ��Ͷ��š�����");
			byte[] _msg=msg.getBytes();
			String msg_0x=StringUtils.byte2string(_msg)+"1A0D";
			SP.write(msg_0x, "hex");
			b=true; 
			//SP.write("48454C4C4F20434A53211A0D", "hex");
		}
		
		return b;
	}
	//3.1�� �������Ķ��ţ��������Ա����ͨ�ţ�
	public synchronized   boolean  Send_Message_toManger(String phoneNum,String Msg)
	{
		String ATCMGF="41542B434D47463D300D";  //AT+CMGF=0 PDUģʽ
		SP.write(ATCMGF, "hex");
		delay(delay);
		if (SP.getRec_string().equals("0D0A4F4B0D0A"))                 //���óɹ�
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
			log("�������豸������");
		}
		return true;
	}
	//4����ȡ���յ��Ķ���Ϣ,ͬʱ��ȡ��
	public  void Wait_For_Message()
	{
		boolean listen=true;
		log("����׼�����ն��š�����");
		while(listen)
		{
			delay(delay);  
			if(SP.getRec_string().startsWith("0D0A2B434D54493A2022534D22"))//��ȡ����Ϣ
			{
				if(SP.getRec_string().endsWith("224D4D532050555348220D0A"))   //�жϽ��յ����ǲ���
				{
					log("�յ�����");
					String texttitle=SP.getRec_string();
					int begin_index=28;
					int end_index=texttitle.indexOf("2C", 29);
					final String index=SP.getRec_string().substring(begin_index, end_index);
					String str=new String(StringUtils.hexStringToByte(index));
					log("��ȡ�ĺ����ǣ�"+str);
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
					log("�յ�����");
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
		log("����ѭ��");
	}
	//4*test��ȡ
	
	
	//5����ȡ��N������
	public  synchronized boolean  Read_Message(String _index) 
	{
		boolean b=false;
		String index=_index;
		String  ATCMGF="at+cmgf=1\r";//���ö�ȡ��ʽΪtext
		SP.write(ATCMGF, "ascll");
		delay(delay);
		if (new String(SP.getRec_byte()).equals(ATCMGF+"\r\nOK\r\n")) 
		{
			log("SUCCESS");
			String ATCMGR="41542B434D47523D"+index+"0D";//��ȡ����
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
					log(telephone+"����Ӧ�Ļ���������ʱ��ƫ������");
					String Msg=DataAnalyseService.Set_English_MSG("05", null);
					Send_Message(telephone, Msg);
					return false;
				}
				int text_bgn_index=rec.indexOf("0D",ATCMGR.length()+4);
				int text_end_index=rec.indexOf("0D",text_bgn_index+1);    //ȥ����β��\r\nOK\r\n
				//log("length="+rec.length());
				//log("bgnindex="+bgn_index);
				//log("endindex="+end_index);
				String Message=rec.substring(text_bgn_index+4,text_end_index);
				//log("MESSAGE="+Message);
				byte[] msg=StringUtils.hexStringToByte(Message);
				String _msg=new String(msg);     
				log("���������ǣ�"+_msg);
				DataAnalyseService.TextAnalyse(_msg, tel,date);
				b=true   ;
			}    
			else {
			log("��ȡ���ų���");
			}
		}
		else {
			log("Wrong");
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
		if(SP.getRec_string().equals(ATCMMSINIT+"0D0A4F4B0D0A")||mms_isinit)         				 //����ok
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
										if (SP.getRec_string().equals(ATSAPBR_11)||mms_isinit||SP.getRec_string().equals("0D0A4F4B0D0A"))     //���������
										{
											delay(delay);
											if (SP.getRec_string().equals("0D0A4F4B0D0A")||mms_isinit) //���������
											{
												//�ἤ�����4
												String ATSAPBR_21="61742B73617062723D322C310D";
												SP.write(ATSAPBR_21, "hex");
												delay(delay);
												if (SP.getRec_string().endsWith("0D0A4F4B0D0A"))
												{
													log("���û����ɹ���");
													return true;
												}
												else {
													logerr("���������ʧ�ܣ�");
												}
											}
											else {
												logerr("���������ʧ�ܣ�");
											}
											
										}
										else{
											logerr("���������ʧ�ܣ�");
										}
											
										
									}
									else {
										logerr("�����ʧ��");
									}
								}
						}
						else {
							logerr("�������ʧ�ܣ�");
						}
					}
					else {
						logerr("��������ipʧ�ܣ�������������");
					}
				}
				else {
					logerr("����������idʧ�ܣ�������������");
				}
			/*}
			else {
				logerr("���ö�������ʧ�ܣ�������������");
			}
			*/
		}
		else if(SP.getRec_string().equals(ATCMMSINIT+"0D0A4552524F520D0A")) 
		{
			log("����ϵͳ�Ѿ���ʼ��������");
			mms_isinit=true;
			return Set_MMS_Environment();
		}
		else {
			logerr("����ʧ�ܣ�������������");
		}
		return b;
	}
	
	//8����ȡ����MMS���洢�������ļ��У���·���������ݿ�
	public synchronized boolean Read_MMS(String _index)
	{
		String index=_index;
		String ATCMMSEDIT="61742B636D6D73656469743D300D";    //at+cmmsedit=0;
		SP.write(ATCMMSEDIT, "hex");
		delay(delay);
		if (!SP.getRec_string().equals(ATCMMSEDIT+"0D0A4F4B0D0A")) 
		{
			logerr("�����˳��༭ʧ�ܣ�");
			return false;
		}
		boolean b=false;
		String ATCMMSRECV="61742B636D6D73726563763D"+index+"0D";       //at+cmmsrecv=index;
		SP.write(ATCMMSRECV, "hex");
		delay(delay);
		if (SP.getRec_string().equals(ATCMMSRECV)) 
		{
			log("-------------------�ȴ�cmmsrecv�ظ�-------------------");
			delay(longlong_delay);
			if (SP.getRec_string().startsWith("0D0A2B434D4D5352454356"))    //+CMMSRECV
			{
				String temp_dataString=SP.getRec_string();
				int tel_bgn_index=temp_dataString.indexOf("22");
				int tel_end_index=temp_dataString.indexOf("22",tel_bgn_index+2);
				int date_bgn_index=tel_end_index+6;
				log("��ʼ��ȡ����");
				//System.out.println(temp_dataString.length());
				//System.out.println(tel_bgn_index);
				//System.out.println(tel_end_index);
				//System.out.println(date_bgn_index);
				String date=temp_dataString.substring(date_bgn_index, date_bgn_index+20);//��ȡ����
				String tel_num=temp_dataString.substring(tel_bgn_index+2,tel_end_index);//��ȡ�绰����
				String ATCMMSREAD="61742B636D6D73726561643D320D0A";                  //at+cmmsrecv=2
				SP.setRec_string("");
				SP.write(ATCMMSREAD, "hex");
				String origin_data="";
				//�Խ��յ�������ȡ���ݲ���
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
	//9��ɾ����Ӧ����
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
	//10����ʼ��sim GPRS����
	public boolean StartGPRS()
	{
		boolean b=false;
		//1����������ע�����  ����1,5���ɹ�
		String _atcreg="at+creg?\r";
		String atcreg=StringUtils.str2hexstr(_atcreg);
		SP.write(atcreg, "hex");
		delay(delay);
		if(SP.getRec_string().equals(atcreg+"0D0A2B435245473A20302C310D0A0D0A4F4B0D0A")||               //����������1,5����
				SP.getRec_string().equals(atcreg+"0D0A2B435245473A20302C350D0A0D0A4F4B0D0A"))
		{
			log("�����Ѿ�ע��ɹ���");
			//2�� ��ѯģ���Ƿ��� GPRS ����
			String _atcgatt="at+cgatt?\r";
			String atcgatt=StringUtils.str2hexstr(_atcgatt);
			SP.write(atcgatt, "hex");
			delay(delay);
			if (SP.getRec_string().equals(atcgatt+"0D0A2B43474154543A20310D0A0D0A4F4B0D0A"))     //����ok
			{
				log("�ɹ�����GPRS����");
				//3������APN
				String _atcstt="at+cstt\r";
				String atcstt=StringUtils.str2hexstr(_atcstt);
				SP.write(atcstt, "hex");
				delay(delay);
				if(SP.getRec_string().equals(atcstt+"0D0A4F4B0D0A"))              //����ok
				{
					log("����APN�ɹ�");
					//4�������ƶ�����
					String _atciicr="at+ciicr\r";
					String atciicr=StringUtils.str2hexstr(_atciicr);
					SP.write(atciicr, "hex");
					delay(delay);
					if (SP.getRec_string().equals(atciicr+"0D0A4F4B0D0A"))                     //����ok
					{
						log("�ƶ������Ѿ����");
						b=true;
					}
					else{
						logerr("�ƶ���������ʧ�ܣ�");
					}
				}
				else
				{
					logerr("����APNʧ�ܣ������ԣ�");
				}
			}
			else {
				logerr("ģ��δ����gprs���磬�����ԣ�");
			}
			
		}
		else
		{
			logerr("����δע�ᡣ������ע��");
		}
		return b;
	}
	//��ʱ����
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
	
	//delay����ʱ�Ӻ���
	public void delay(int delay)
	{
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	//log����
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


