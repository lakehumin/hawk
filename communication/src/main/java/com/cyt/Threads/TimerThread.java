package com.cyt.Threads;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import com.cyt.Bean.DeviceInfoBean;
import com.cyt.Bean.TerminalDevBean;
import com.cyt.DAO.DeviceInfoDao;
import com.cyt.DAO.TerminalDevDao;
import com.cyt.Service.DataAnalyseService;
import com.cyt.Service.Sim800AService;

public class TimerThread extends Thread {
	private int timeToTestConnection;
	private Sim800AService s800;
	private String todaydate;
	private TerminalDevDao tdo;
	private HashMap<String, Boolean> ConnectioinMap=null;
	private HashMap<Integer, String> terminal_idMap=null;
	private HashMap<String, String> terid_telMap=null;
	public TimerThread(Sim800AService s800,int timeToTestConnection)
	{
		this.s800=s800;
		this.timeToTestConnection=timeToTestConnection;
		tdo=new TerminalDevDao();
	}
	//更新数据库中的设备信息，得到终端与电话的映射表
	public void Update()
	{
		terminal_idMap=new HashMap<Integer, String>();
		terid_telMap=new HashMap<String, String>();
		ArrayList<TerminalDevBean> tdblst=tdo.SearchAll();
		for (int i = 0; i < tdblst.size(); i++) {
			terminal_idMap.put(i, tdblst.get(i).getTerminal_id());
			terid_telMap.put(tdblst.get(i).getTerminal_id(), tdblst.get(i).getTel_num());
			//System.out.println(tdblst.get(i).getTerminal_id()+"\t"+tdblst.get(i).getTel_num());
		}
	}
	//将设备的逻辑连接状态设为离线，true为在线，false为离线
	public void ResetConnection()
	{
		ConnectioinMap=new HashMap<String, Boolean>();
		ArrayList<TerminalDevBean> tdblst=tdo.SearchAll();
		for (int i = 0; i < tdblst.size(); i++) {
			ConnectioinMap.put(tdblst.get(i).getTerminal_id(), false);
		}
	}
	//检索数据库中当天数据记录,排查出其中未上报信息的设备,
	private ArrayList<String> SearchDisConnectionFromInfo()
	{
		DateFormat format=new SimpleDateFormat("YY-MM-dd");
		todaydate=format.format(new Date());
		DeviceInfoDao did=new DeviceInfoDao();
		ArrayList<String> disconnectdevice=new ArrayList<String>();
		ArrayList<DeviceInfoBean> todaylst=did.Search(6,todaydate);
		Update();
		ResetConnection();
		for (int i = 0; i < todaylst.size(); i++) {
			//System.out.println(todaylst.get(i).getTerminal_id()+"已经上报");
			ConnectioinMap.put(todaylst.get(i).getTerminal_id(),true);			
		}
		for (int i = 0; i < ConnectioinMap.size(); i++) {
			if(!ConnectioinMap.get(terminal_idMap.get(i)))
			{
				TerminalDevBean disconnect=tdo.Searchid(terminal_idMap.get(i));
				disconnect.setIslinked(false);
				tdo.update(disconnect);
				disconnectdevice.add(terminal_idMap.get(i));
			}
		}
		ResetConnection();
		return disconnectdevice;
	}
	//检索数据库中设备列表，获取其中连接状态为离线的设备名
	public ArrayList<String> SearchDisConnectionFromDevice()
	{
		ArrayList<String> disconnectdevice=new ArrayList<String>();
		ArrayList<TerminalDevBean> tdblst=tdo.SearchAll();
		for (int i = 0; i < tdblst.size(); i++) {
			if(!tdblst.get(i).isIslinked())
			{
				disconnectdevice.add(tdblst.get(i).getTerminal_id());
			}
		}
		return disconnectdevice;
	}
	//设置离线异常事件
	public void SetOfflineEvent(ArrayList<String> disconnectdevice)
	{
		for (int i = 0; i < disconnectdevice.size(); i++) {
			DataAnalyseService.HandleEvent("Offline", disconnectdevice.get(i));
		}		  
	}
	//将离线设备在设备表单中的连接状态设为离线，然后向该设备发送发送心跳指令"02"，若其回复心跳指令，数据库中的离线状态会回复为在线
	public void ConfirmConnection(ArrayList<String> disconnectdevice)
	{
		Update();
		for (int i = 0; i < disconnectdevice.size(); i++) {
			String cmd=DataAnalyseService.Set_English_MSG("02", null);
			s800.Send_Message(terid_telMap.get(disconnectdevice.get(i)),cmd);
		}
	}
	//延迟函数(秒数)
	public void Delay(int delay)
	{
		try {
			Thread.sleep(delay*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while(true)
		{
			
			Date now=new Date();
			@SuppressWarnings("deprecation")
			int hour=now.getHours();
			if (hour==timeToTestConnection) {
				ConfirmConnection(SearchDisConnectionFromInfo());
				Delay(1*60);
				SetOfflineEvent(SearchDisConnectionFromDevice());
				break;
			}
			//每隔35分钟检查一下时间是否为设定时间，若到了，则进行通讯连接状态的检查
			Delay(1*60);    
		}
		System.out.println("测试通信完毕！");
	}
}
