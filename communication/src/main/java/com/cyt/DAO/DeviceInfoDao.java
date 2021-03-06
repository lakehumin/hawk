package com.cyt.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import com.cyt.Bean.DeviceInfoBean;
import com.lake.common_utils.db_utils.SqlHelper;

public class DeviceInfoDao {
	private HashMap<Integer,String> hmp;
	//初始化 hash映射
	public DeviceInfoDao() 
	{
		hmp=new HashMap<Integer, String>();
		hmp.put(1, "id");
		hmp.put(2, "ternimal_id");
		hmp.put(3,"battery");
		hmp.put(4,"voltage");
		hmp.put(5, "workstate");
		hmp.put(6, "date");
	}
	//增
	public boolean add(DeviceInfoBean dib) 
	{
		boolean b=false;
		String sql="insert into deviceinfo(terminal_id,battery,voltage,workstate,date) values(?,?,?,?,?)";
		String []parameters={dib.getTerminal_id()+"",dib.getBattery(),dib.getVoltage()+"",dib.getWorkstate()+"",dib.getDate()};
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
		String sql="update deviceinfo set battery=?,voltage=?,workstate=?,date=? where terminal_id=?";
		String []parameters={dib.getBattery(),dib.getVoltage(),dib.getWorkstate(),dib.getTerminal_id(),dib.getDate()};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//查询某一项属性 例如：连接状态、工作状态
	public ArrayList<DeviceInfoBean> Search(int key,String content)
	{
		ArrayList<DeviceInfoBean> dib_lst=new ArrayList<DeviceInfoBean>();
		String sql="select *from deviceinfo where "+hmp.get(key)+" =?";
		String parameters[]={content};
		ResultSet rs=SqlHelper.executeQuery(sql, parameters);
		try {
			while(rs.next())
			{
				DeviceInfoBean dib=new DeviceInfoBean();
				dib.setId(rs.getInt(1));
				dib.setTerminal_id((rs.getString(2)));
				dib.setBattery(rs.getString(3));
				dib.setVoltage(rs.getString(4));
				dib.setWorkstate(rs.getString(5));
				dib.setDate(rs.getString(6));
				dib_lst.add(dib);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getConnection());
		}
		return dib_lst;
	}
	//根据终端编号查询对应的
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
				dib.setDate(rs.getString(6));
			}
			else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getConnection());
		}
		return dib;
	}
	
}
