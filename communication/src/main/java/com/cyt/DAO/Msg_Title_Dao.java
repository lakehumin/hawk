package com.cyt.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cyt.Bean.Msg_Title_Bean;
import com.lake.common_utils.db_utils.SqlConnectionPool;
import com.lake.common_utils.db_utils.SqlHelper;

public class Msg_Title_Dao {
	//增
	public boolean add(Msg_Title_Bean mtb) 
	{
		boolean b=false;
		String sql="insert into MsgTitle(num,type,isread) values(?,?,?)";
		Object []parameters={mtb.getnum(),mtb.getType(),mtb.isRead()};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//删
	public boolean delete()
	{
		boolean b=false;
		String sql="delete from MsgTitle where isread=1";
		SqlHelper.executeUpdate(sql, null);
		b=true;
		return b;
	}
	//将短信状态改成已读
	public boolean update(Msg_Title_Bean mtb)
	{
		boolean b=false;
		String sql="update MsgTitle set isread=? where id=?";
		Object []parameters={true,mtb.getId()+""};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//查询未读的短信或彩信
	public ArrayList<Msg_Title_Bean> Search(String type)
	{
		ArrayList<Msg_Title_Bean> mtb_lst=new ArrayList<Msg_Title_Bean>();
		String sql="select *from msgtitle where isread= false and type='"+type+"'";
		//String parameters[]={content};
		ResultSet rs=SqlHelper.executeQuery(sql, null);
		try {
			while(rs.next())
			{
				Msg_Title_Bean mtb=new Msg_Title_Bean();
				mtb.setId(rs.getInt(1));
				mtb.setnum(rs.getString(2));
				mtb.setType(rs.getString(3));
				mtb.setRead(rs.getBoolean(4)); 
				//System.out.println(rs.getInt(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getBoolean(4));
				mtb_lst.add(mtb);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			SqlHelper.close(rs, SqlHelper.getpPreparedStatement(), SqlHelper.getConnection());
		}
		return mtb_lst;
	}
}
