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
	//�������ݿ��е��豸��Ϣ���õ��ն���绰��ӳ���
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
	//���豸���߼�����״̬��Ϊ���ߣ�trueΪ���ߣ�falseΪ����
	public void ResetConnection()
	{
		ConnectioinMap=new HashMap<String, Boolean>();
		ArrayList<TerminalDevBean> tdblst=tdo.SearchAll();
		for (int i = 0; i < tdblst.size(); i++) {
			ConnectioinMap.put(tdblst.get(i).getTerminal_id(), false);
		}
	}
	//�������ݿ��е������ݼ�¼,�Ų������δ�ϱ���Ϣ���豸,
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
			//System.out.println(todaylst.get(i).getTerminal_id()+"�Ѿ��ϱ�");
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
	//�������ݿ����豸�б���ȡ��������״̬Ϊ���ߵ��豸��
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
	//���������쳣�¼�
	public void SetOfflineEvent(ArrayList<String> disconnectdevice)
	{
		for (int i = 0; i < disconnectdevice.size(); i++) {
			DataAnalyseService.HandleEvent("Offline", disconnectdevice.get(i));
		}		  
	}
	//�������豸���豸���е�����״̬��Ϊ���ߣ�Ȼ������豸���ͷ�������ָ��"02"������ظ�����ָ����ݿ��е�����״̬��ظ�Ϊ����
	public void ConfirmConnection(ArrayList<String> disconnectdevice)
	{
		Update();
		for (int i = 0; i < disconnectdevice.size(); i++) {
			String cmd=DataAnalyseService.Set_English_MSG("02", null);
			s800.Send_Message(terid_telMap.get(disconnectdevice.get(i)),cmd);
		}
	}
	//�ӳٺ���(����)
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
			//ÿ��35���Ӽ��һ��ʱ���Ƿ�Ϊ�趨ʱ�䣬�����ˣ������ͨѶ����״̬�ļ��
			Delay(1*60);    
		}
		System.out.println("����ͨ����ϣ�");
	}
}
