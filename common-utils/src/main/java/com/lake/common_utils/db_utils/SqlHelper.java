package com.lake.common_utils.db_utils;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import com.lake.common_utils.db_utils.SqlConnectionPool.SqlConnection;

public class SqlHelper {
		//数据库驱动  
		private static String jdbcDriver = "com.mysql.jdbc.Driver";
		//数据库url  
	    private static String dbUrl = "jdbc:mysql://114.212.118.115:3306/hawk";
	    //数据库ip
//	    private static String dbIp = "114.212.118.115";
	    //数据库名称
//	    private static String dbName = "hawk";
	    //数据库表名称
	    private static String dbTableName = "user";
	    //数据库用户名  
	    private static String dbUsername = "root";
	    //数据库密码 
	    private static String dbPassword = "root";
	    //数据库连接池
	    //private static volatile SqlConnectionPool SqlConnectionPool;
	    private static PreparedStatement ps=null;
	    private static ResultSet rs=null;
	    private static Connection ct=null;
	    static{
	    	try {
				Class.forName(jdbcDriver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	    }
	    public static Connection getConnection()
		{
			try
			{
				ct=DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			} catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ct;
		}
		//重载executeQuery函数
	 public static synchronized ResultSet executeQuery(String sql,Object[]parameters)
	 {	
		try {
			    ct=getConnection();
		        ps=ct.prepareStatement(sql);
			if (parameters!=null&&!parameters.equals(""))
			{
				for(int i=0;i<parameters.length;i++)
				{
					ps.setObject(i+1, parameters[i]);
				}
			}
			rs=ps.executeQuery();
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	 }
	 //重载executeUpdate函数
	 public static synchronized void executeUpdate(String sql,Object []parameters)
	{
		 
		 try {
			 ct=getConnection();
			 ps=ct.prepareStatement(sql);
			if (parameters!=null&&!parameters.equals(""))
			{
				for(int i=0;i<parameters.length;i++)
				{
					ps.setObject(i+1, parameters[i]);
				}
			}
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		 finally
			{
				close(rs,ps,ct);
				
			}
		 
	}
	 //关闭流
	 public static void close(ResultSet rs,PreparedStatement ps,Connection ct)
		{
			if (rs!=null) 
			{
				try 
				{
					rs.close();
				} catch ( SQLException e2) 
				{
					e2.printStackTrace();
				}
				rs=null;
			}
			if(ps!=null)
			{
				try 
				{
					ps.close();
				} 
				catch (SQLException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ps=null;
			}
			
			if (ct!=null) 
			{
				try 
				{
					ct.close();
				} 
				catch (SQLException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ct=null;
			}
			
			
		}	
	 public static ResultSet getreResultSet() 
	 {
		 return rs;		
	 }
	 public static PreparedStatement getpPreparedStatement() 
	 {
		 return ps;		
	 }
}
