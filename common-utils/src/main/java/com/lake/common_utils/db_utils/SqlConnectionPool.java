package com.lake.common_utils.db_utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lake.common_utils.db_utils.DBConnectionPool.DBConnection;

public class SqlConnectionPool {
	//最大连接数
	private int maxSize;
	//初始连接数
	private int initialSize;
	
	//数据库驱动  
	private String jdbcDriver;
	//数据库url  
    private String sqlUrl;
    //数据库用户名  
    private String sqlUsername;
    //数据库密码 
    private String sqlPassword;
    //数据库连接存储
    private List<SqlConnection> connections;
    
	SqlConnectionPool(int maxSize, int initialSize, String jdbcDriver,
			String sqlUrl, String sqlUsername, String sqlPassword) {
		this.maxSize = maxSize;
		this.initialSize = initialSize;
		this.jdbcDriver = jdbcDriver;
		this.sqlUrl = sqlUrl;
		this.sqlUsername = sqlUsername;
		this.sqlPassword = sqlPassword;
		connections = new ArrayList<SqlConnection>(initialSize);
		init();
	}

	private void init() {
		try {
			Class.forName(jdbcDriver);
		} catch (ClassNotFoundException e) {
			System.err.println("数据库驱动程序注册失败！");
			throw new RuntimeException(e);
		}
		for (int i = 0; i < initialSize; i++) {
			connections.add(new SqlConnection(createOneConnection()));
		}
	}
	
	private Connection createOneConnection() {
		Connection connection = null;
        try {
			connection = DriverManager.getConnection(sqlUrl,   
			        sqlUsername, sqlPassword);
		} catch (SQLException e) {
			System.err.println("创建数据库连接失败");
			throw new RuntimeException(e);
		}
        return connection;
	}
	
	public SqlConnection getConnection() {
		SqlConnection sqlConnection = getFreeConnection();
		synchronized (sqlConnection) {
			if(!sqlConnection.isBusy()) {
				sqlConnection.setBusy(true);
				return sqlConnection;
			} else {
				return getConnection();
			}
		}
	}
	
	private SqlConnection getFreeConnection() {
		synchronized (connections) {
			SqlConnection sqlConnection = findFreeConnection();
			
			//retry
			if(null == sqlConnection) {
				sqlConnection = findFreeConnection();
				if(null != sqlConnection) {
					return sqlConnection;
				}
			} else {
				return sqlConnection;
			}
			
			/** 可能没有空闲连接，那就新建 */
			//判断是否超出最大连接数
			if(connections.size() < maxSize) {
				sqlConnection = new SqlConnection(createOneConnection());
				connections.add(sqlConnection);
				return sqlConnection;
			} else {
				return getFreeConnection();
			}
		}
	}

	private SqlConnection findFreeConnection() {
		for (SqlConnection sqlcConnection : connections) {
			if(!sqlcConnection.isBusy()) {
				return sqlcConnection;
			}
		}
		return null;
	}
	
	public void freeConnection(SqlConnection sqlconnection) {
		synchronized (connections) {
			for (SqlConnection sqlConnection : connections) {
				if(sqlConnection == sqlconnection) {
					sqlConnection.setBusy(false);
					return ;
				}
			}
		}
	}
	
	public static class SqlConnection {
		private Connection connection;
		private volatile boolean busy;
		//private Map<String,PreparedStatement> pStatMap;
		private static PreparedStatement ps=null;
		private static ResultSet rs=null;
		public SqlConnection(Connection connection) {
			this.connection = connection;
			busy = false;
			//pStatMap = new HashMap<String,PreparedStatement>();
		}

		public Connection getConnection() {
			return connection;
		}

		public void setConnection(Connection connection) {
			this.connection = connection;
		}

		public boolean isBusy() {
			return busy;
		}

		public void setBusy(boolean busy) {
			this.busy = busy;
		}
		public static ResultSet getResultSet()
		{
			return rs;
		}
		public static PreparedStatement getPreparedStatement()
		{
			return ps;
		}
		
	}
}
