package com.cyt.DAO;

import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.cyt.Bean.msg_data_bean;
import com.lake.common_utils.db_utils.SqlHelper;

public class msg_data_Dao {
	private HashMap<Integer,String> hmp;
	//��ʼ�� hashӳ��
	public msg_data_Dao() 
	{
		hmp=new HashMap<Integer, String>();
		hmp.put(1, "id");
		hmp.put(2, "ternimal_id");
		hmp.put(3,"msg_path");
		hmp.put(4,"img_path");
		hmp.put(5, "date");
		SqlHelper.init();
	}
	//��
	public boolean add(msg_data_bean mdb) 
	{
		boolean b=false;
		String sql="insert into msg_data values(?,?,?,?,?)";
		String []parameters={mdb.getId()+"",mdb.getTerminal_id()+"",mdb.getMsg_path(),mdb.getImg_path(),mdb.getDate()+""};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//ɾ
	public boolean delete(int key,String content)
	{
		boolean b=false;
		String sql="delete from msg_data where"+" "+hmp.get(key)+"="+content;
		SqlHelper.executeUpdate(sql, null);
		b=true;
		return b;
	}
	//��
	public boolean update(msg_data_bean mdb)
	{
		boolean b=false;
		String sql="update msg_data set msg_path=?,img_path=?,date=? where id=?";
		String []parameters={mdb.getMsg_path(),mdb.getImg_path(),mdb.getDate()+"",mdb.getId()+""};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//��ѯĳһ������ ���磺ʱ�䡢�ն˱��
	public ArrayList<msg_data_bean> Search(int key,String content)
	{
		ArrayList<msg_data_bean> mdb_lst=new ArrayList<msg_data_bean>();
		String sql="select *from msg_data where "+hmp.get(key)+"="+content;
		//String parameters[]={content};
		ResultSet rs=SqlHelper.executeQuery(sql, null);
		try {
			while(rs.next())
			{
				msg_data_bean mdb=new msg_data_bean();
				mdb.setId(rs.getInt(1));
				mdb.setTerminal_id(rs.getInt(2));
				mdb.setMsg_path(rs.getString(3));
				mdb.setImg_path(rs.getString(4)); 
				mdb.setDate(rs.getDate(5));
				mdb_lst.add(mdb);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getSqlConnection().getConnection());
		}
		return mdb_lst;
	}
	//��ѯĳһ��id�ļ�¼
	public msg_data_bean Searchid(String id)
	{
		msg_data_bean mdb=new msg_data_bean();
		String sql="select *from msg_data where id=?";
		String parameters[]={id};
		ResultSet rs=SqlHelper.executeQuery(sql, parameters);
		try {
			while(rs.next())
			{
				mdb.setId(rs.getInt(1));
				mdb.setTerminal_id(rs.getInt(2));
				mdb.setMsg_path(rs.getString(3));
				mdb.setImg_path(rs.getString(4)); 
				mdb.setDate(rs.getDate(5));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getSqlConnection().getConnection());
		}
		return mdb;
	}
	
}
