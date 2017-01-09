package com.cyt.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cyt.Bean.MsgTitleBean;
import com.lake.common_utils.db_utils.SqlHelper;

public class MsgTitleDao {
	//��
	public boolean add(MsgTitleBean mtb) 
	{
		boolean b=false;
		String sql="insert into MsgTitle(num,type,isread) values(?,?,?)";
		Object []parameters={mtb.getnum(),mtb.getType(),mtb.isRead()};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//ɾ
	public boolean delete()
	{
		boolean b=false;
		String sql="delete from MsgTitle where isread=1";
		SqlHelper.executeUpdate(sql, null);
		b=true;
		return b;
	}
	//������״̬�ĳ��Ѷ�
	public boolean update(MsgTitleBean mtb)
	{
		boolean b=false;
		String sql="update MsgTitle set isread=? where id=?";
		Object []parameters={true,mtb.getId()+""};
		SqlHelper.executeUpdate(sql, parameters);
		b=true;
		return b;
	}
	//��ѯδ���Ķ��Ż����
	public ArrayList<MsgTitleBean> Search(String type)
	{
		ArrayList<MsgTitleBean> mtb_lst=new ArrayList<MsgTitleBean>();
		String sql="select *from msgtitle where isread= false and type='"+type+"'";
		//String parameters[]={content};
		ResultSet rs=SqlHelper.executeQuery(sql, null);
		try {
			while(rs.next())
			{
				MsgTitleBean mtb=new MsgTitleBean();
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
