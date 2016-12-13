package com.cyt.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.cyt.Bean.DeviceInfoBean;
import com.cyt.Bean.Terminal_Dev_Bean;
import com.lake.common_utils.db_utils.SqlHelper;

public class Device_Info_Dao {
	private HashMap<Integer,String> hmp;
	//初始化 hash映射
	public Device_Info_Dao() 
	{
		hmp=new HashMap<Integer, String>();
		hmp.put(1, "id");
		hmp.put(2, "ternimal_id");
		hmp.put(3,"battery");
		hmp.put(4,"voltage");
		hmp.put(5, "workstate");
		SqlHelper.init();
	}
	//增
	public boolean add(DeviceInfoBean dib) 
	{
		boolean b=false;
		String sql="insert into deviceinfo(terminal_id,battery,voltage,workstate) values(?,?,?,?)";
		String []parameters={dib.getTerminal_id()+"",dib.getBattery(),dib.getVoltage()+"",dib.getWorkstate()+""};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//删
	public boolean delete(int key,String content)
	{
		boolean b=false;
		String sql="delete from deviceinfo where"+" "+hmp.get(key)+"="+content;
		SqlHelper.executeUpdate(sql, null);
		b=true;
		return b;
	}
	//改
	public boolean update(DeviceInfoBean dib)
	{
		boolean b=false;
		String sql="update deviceinfo set battery=?,voltage=?,workstate=? where terminal_id=?";
		String []parameters={dib.getBattery(),dib.getVoltage(),dib.getWorkstate(),dib.getTerminal_id()};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//查询某一项属性 例如：连接状态、工作状态
	public ArrayList<DeviceInfoBean> Search(int key,String content)
	{
		ArrayList<DeviceInfoBean> dib_lst=new ArrayList<DeviceInfoBean>();
		String sql="select *from deviceinfo where "+hmp.get(key)+"="+content;
		//String parameters[]={content};
		ResultSet rs=SqlHelper.executeQuery(sql, null);
		try {
			while(rs.next())
			{
				DeviceInfoBean dib=new DeviceInfoBean();
				dib.setId(rs.getInt(1));
				dib.setTerminal_id((rs.getString(2)));
				dib.setBattery(rs.getString(3));
				dib.setVoltage(rs.getString(4));
				dib.setWorkstate(rs.getString(5)); 		
				dib_lst.add(dib);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getSqlConnection().getConnection());
		}
		return dib_lst;
	}
	public DeviceInfoBean Searchid(String terminal_id)
	{
		DeviceInfoBean dib=new DeviceInfoBean();
		String sql="select *from deviceinfo where terminal_id=?";
		String parameters[]={terminal_id};
		ResultSet rs=SqlHelper.executeQuery(sql, parameters);
		try {
			if(rs.next())
			{
				dib.setId(rs.getInt(1));
				dib.setTerminal_id((rs.getString(2)));
				dib.setBattery(rs.getString(3));
				dib.setVoltage(rs.getString(4));
				dib.setWorkstate(rs.getString(5)); 		
			}
			else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getSqlConnection().getConnection());
		}
		return dib;
	}
	
}
