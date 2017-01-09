package com.lake.common_utils.db_utils;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.lake.common_utils.db_utils.SqlConnectionPool.SqlConnection;

public class SqlHelper {
	    private static PreparedStatement ps=null;
	    private static ResultSet rs=null;
	    private static Connection ct=null;
	    private static String url="";
		private static String username="";
		private static String driver="";
		private static String password="";
		private static Properties pp=null;
		private static InputStream fis=null;
		//加载驱动，只需一次 ，所以用Static方法
		static
		{
			try
			{
				//从dbinfo.properties文件中读取配置信息
				pp=new Properties();
				fis=SqlHelper.class.getClassLoader().getResourceAsStream("dbinfo.properties");
				pp.load(fis);
				url=pp.getProperty("url");
				username=pp.getProperty("username");
				driver=pp.getProperty("driver");
				password=pp.getProperty("password");
				Class.forName(driver);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
					try
					{
						fis.close();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					fis=null;
			
			}
		}
	    public static Connection getConnection()
		{
			try
			{
				ct=DriverManager.getConnection(url, username, password);
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
