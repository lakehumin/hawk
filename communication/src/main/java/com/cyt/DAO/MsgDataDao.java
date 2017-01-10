package com.cyt.DAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import com.cyt.Bean.MsgDataBean;
import com.lake.common_utils.db_utils.SqlHelper;

public class MsgDataDao {
	private HashMap<Integer,String> hmp;
	//��ʼ�� hashӳ��
	public MsgDataDao() 
	{
		hmp=new HashMap<Integer, String>();
		hmp.put(1, "id");
		hmp.put(2, "ternimal_id");
		hmp.put(3,"img_path");
		hmp.put(4, "date");
	}
	//��
	public boolean add(MsgDataBean mdb) 
	{
		boolean b=false;
		String sql="insert into msg_data(terminal_id,img_path,date) values(?,?,?)";
		String []parameters={mdb.getTerminal_id()+"",mdb.getImg_path(),mdb.getDate()+""};
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
	public boolean update(MsgDataBean mdb)
	{
		boolean b=false;
		String sql="update msg_data set img_path=?,date=? where id=?";
		String []parameters={mdb.getImg_path(),mdb.getDate()+"",mdb.getId()+""};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//��ѯĳһ������ ���磺ʱ�䡢�ն˱��
	public ArrayList<MsgDataBean> Search(int key,String content)
	{
		ArrayList<MsgDataBean> mdb_lst=new ArrayList<MsgDataBean>();
		String sql="select *from msg_data where "+hmp.get(key)+"="+content;
		//String parameters[]={content};
		ResultSet rs=SqlHelper.executeQuery(sql, null);
		try {
			while(rs.next())
			{
				MsgDataBean mdb=new MsgDataBean();
				mdb.setId(rs.getInt(1));
				mdb.setTerminal_id(rs.getString(2));
				mdb.setImg_path(rs.getString(3)); 
				mdb.setDate(rs.getString(4));
				mdb_lst.add(mdb);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getConnection());
		}
		return mdb_lst;
	}
	//��ѯĳ���ն�ĳһ��ļ�¼
	public MsgDataBean Searchid(String terminal_id,String date)
	{
		MsgDataBean mdb=null;
		String sql="select *from msg_data where terminal_id=? and date=?";
		String parameters[]={terminal_id,date};
		ResultSet rs=SqlHelper.executeQuery(sql, parameters);
		try {
			if(rs.next())
			{
				mdb=new MsgDataBean();
				mdb.setId(rs.getInt(1));
				mdb.setTerminal_id(rs.getString(2));
				mdb.setImg_path(rs.getString(3)); 
				mdb.setDate(rs.getString(4));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getConnection());
		}
		return mdb;
	}
	
}
