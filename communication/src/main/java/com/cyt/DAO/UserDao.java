package com.cyt.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.cyt.Bean.UserBean;
import com.lake.common_utils.db_utils.SqlHelper;

public class UserDao {
	//Ôö
		public boolean add(UserBean user) 
		{
			boolean b=false;
			String sql="insert into deviceinfo(username,password,type,realname,tel) values(?,?,?,?,?)";
			String []parameters={user.getUsername()+"",user.getPassword(),user.getType(),user.getRealname(),user.getTel()};
			SqlHelper.executeUpdate(sql, parameters);
			b=true;
			return b;
		}
		//É¾
		public boolean delete(UserBean user)
		{
			boolean b=false;
			String sql="delete from deviceinfo where"+" id="+user.getId();
			SqlHelper.executeUpdate(sql, null);
			b=true;
			return b;
		}
		//¸Ä
		public boolean update(UserBean user)
		{
			boolean b=false;
			String sql="update user set username=?,password=?,type=?,realname=?,tel=? where id=?";
			String []parameters={user.getUsername(),user.getPassword(),user.getType(),user.getRealname(),user.getTel(),user.getId()+""};
			SqlHelper.executeUpdate(sql, parameters);
			b=true;
			return b;
		}
		//²éÑ¯
		public UserBean SearchById(String id)
		{
			UserBean user=null;
			String sql="select * from user where id="+id;
			ResultSet rs=SqlHelper.executeQuery(sql, null);
			try {
				if (rs.next()) 
				{
					user =new UserBean();
					user.setUsername(rs.getString("username"));
					user.setPassword(rs.getString("password"));
					user.setType(rs.getString("type"));
					user.setTel(rs.getString("tel"));
					user.setRealname(rs.getString("realname"));
				}
			} catch (SQLException e) {
				e.printStackTrace();         
			}
			return user;
		}
}
