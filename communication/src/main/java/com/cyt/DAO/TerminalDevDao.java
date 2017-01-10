package com.cyt.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import com.cyt.Bean.TerminalDevBean;
import com.lake.common_utils.db_utils.SqlHelper;

public class TerminalDevDao {
	private HashMap<Integer,String> hmp;
	//初始化 hash映射
	public TerminalDevDao() 
	{
		hmp=new HashMap<Integer, String>();
		hmp.put(1, "id");
		hmp.put(2, "ternimal_id");
		hmp.put(3,"tel_num");
		hmp.put(4,"location");
		hmp.put(5, "islinked");
	}
	//增
	public boolean add(TerminalDevBean tdb) 
	{
		boolean b=false;
		String sql="insert into terminal_dev(terminal_id,tel_num,location,islinked) values(?,?,?,?)";
		Object []parameters={tdb.getTerminal_id()+"",tdb.getTel_num(),tdb.getLocation()+"",tdb.isIslinked()};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//删
	public boolean delete(int key,String content)
	{
		boolean b=false;
		String sql="delete from terminal_dev where"+" "+hmp.get(key)+"="+content;
		SqlHelper.executeUpdate(sql, null);
		b=true;
		return b;
	}
	//改
	public boolean update(TerminalDevBean tdb)
	{
		boolean b=false;
		String sql="update terminal_dev set tel_num=?,location=?,islinked=? where terminal_id=?";
		Object []parameters={tdb.getTel_num(),tdb.getLocation(),tdb.isIslinked(),tdb.getTerminal_id()};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//查询某一项属性 例如：连接状态、工作状态
	public ArrayList<TerminalDevBean> Search(int key,String content)
	{
		ArrayList<TerminalDevBean> tdb_lst=new ArrayList<TerminalDevBean>();
		String sql="select *from terminal_dev where "+hmp.get(key)+"="+content;
		//String parameters[]={content};
		ResultSet rs=SqlHelper.executeQuery(sql, null);
		try {
			while(rs.next())
			{
				TerminalDevBean tdb=new TerminalDevBean();
				tdb.setId(rs.getInt(1));
				tdb.setTerminal_id((rs.getString(2)));
				tdb.setTel_num(rs.getString(3));
				tdb.setLocation(rs.getString(4));
				tdb.setIslinked(rs.getBoolean(5)); 		
				tdb_lst.add(tdb);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getConnection());
		}
		return tdb_lst;
	}
	//查询表中所有成员
	public ArrayList<TerminalDevBean> SearchAll()
	{
		ArrayList<TerminalDevBean> tdb_lst=new ArrayList<TerminalDevBean>();
		String sql="select *from terminal_dev";
		//String parameters[]={content};
		ResultSet rs=SqlHelper.executeQuery(sql, null);
		System.out.println("start search");
		try {
			while(rs.next())
			{
				TerminalDevBean tdb=new TerminalDevBean();
				tdb.setId(rs.getInt(1));
				tdb.setTerminal_id((rs.getString(2)));
				tdb.setTel_num(rs.getString(3));
				tdb.setLocation(rs.getString(4));
				tdb.setIslinked(rs.getBoolean(5)); 	
				tdb_lst.add(tdb);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getConnection());
		}
		return tdb_lst;
	}
	//查询某一个id的记录
	public TerminalDevBean Searchid(String terminal_id)
	{
		TerminalDevBean tdb=new TerminalDevBean();
		String sql="select *from terminal_dev where terminal_id=?";
		String parameters[]={terminal_id};
		ResultSet rs=SqlHelper.executeQuery(sql, parameters);
		try {
			if(rs.next())
			{
				tdb.setId(rs.getInt(1));
				tdb.setTerminal_id((rs.getString(2)));
				tdb.setTel_num(rs.getString(3));
				tdb.setLocation(rs.getString(4));
				tdb.setIslinked(rs.getBoolean(5)); 	
			}
			else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getConnection());
		}
		return tdb;
	}
}
