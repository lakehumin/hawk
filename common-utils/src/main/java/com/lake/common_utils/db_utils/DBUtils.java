package com.lake.common_utils.db_utils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import com.lake.common_utils.db_utils.DBConnectionPool.DBConnection;

/**
 * 通用数据库工具类JDBC的封装的模板，带数据库连接池,线程安全
 * 带默认参数
 * 一般先init初始化，如果没有，则默认参数初始化
 * 通过init方法配置参数
 */

/**
 * @author LakeHm
 *
 * 2016年10月23日下午7:18:35
 */
public class DBUtils {
	//最大连接数
	private static int maxSize = 20;
	//初始连接数
	private static int initialSize = 3;
	
	//数据库驱动  
	private static String jdbcDriver = "com.mysql.jdbc.Driver";
	//数据库url  
    private static String dbUrl = "jdbc:mysql://114.212.118.115:3306/springmvc";
    //数据库ip
//    private static String dbIp = "114.212.118.115";
    //数据库名称
//    private static String dbName = "springmvc";
    //数据库表名称
    private static String dbTableName = "user";
    //数据库用户名  
    private static String dbUsername = "root";
    //数据库密码 
    private static String dbPassword = "root";
    //数据库连接池
    private static volatile DBConnectionPool dbConnectionPool;
    //prepareStatement sql语句预存储
    private static Map<String,String> sqlMap;
    
    public static synchronized void init() {
    	if(checkInit()) {
    		System.err.println("DBUtils初始化已完成");
    		return ;
    	}
    	dbConnectionPool = new DBConnectionPool(maxSize,initialSize,
    			jdbcDriver,dbUrl,dbUsername,dbPassword);
    	initSqlMap();
    }

	public static synchronized void init(String tableName) {
    	if(checkInit()) {
    		System.err.println("DBUtils初始化已完成");
    		return ;
    	}
    	dbTableName = tableName;
    	init();
    }

	public static synchronized void init(String dbName, 
			String tableName) {
    	if(checkInit()) {
    		System.err.println("DBUtils初始化已完成");
    		return ;
    	}
    	dbUrl = "jdbc:mysql://114.212.118.115:3306/" + dbName;
    	dbTableName = tableName;
    	init();
    }

	public static synchronized void init(String ip, 
			String dbName, String tableName) {
		if(checkInit()) {
    		System.err.println("DBUtils初始化已完成");
    		return ;
    	}
		dbUrl = "jdbc:mysql://" + ip + ":3306/" + dbName;
		dbTableName = tableName;
		init();
	}
	
	public static synchronized void init(String ip, String dbName, 
			String tableName, int maxSize, int initialSize) {
		if(checkInit()) {
    		System.err.println("DBUtils初始化已完成");
    		return ;
    	}
		dbUrl = "jdbc:mysql://" + ip + ":3306/" + dbName;
		dbTableName = tableName;
		DBUtils.maxSize = maxSize;
		DBUtils.initialSize = initialSize;
		init();
	}
	
    
    private static void initSqlMap() {
		sqlMap = new HashMap<String,String>();
		sqlMap.put("add", "insert into " + dbTableName + 
				" (id, name, age, date) values (?,?,?,?)");
		sqlMap.put("delete", "delete from " + dbTableName + 
				" where name = ?");
		sqlMap.put("update", "update " + dbTableName + 
				" set age = ?, date = ? where name = ?");
		sqlMap.put("search", "select * from " + dbTableName + 
				" where name = ?");
	}
	
	private static boolean checkInit() {
		if(null != dbConnectionPool) {
    		return true;
    	}
		return false;
	}
	
	private static PreparedStatement getPreparedStatement(String key,
			DBConnection dbconnection) {
		PreparedStatement ps = dbconnection.getPreparedStatement(key);
		if(null == ps) {
			try {
				ps = dbconnection.getConnection().prepareStatement(sqlMap.get(key));
				dbconnection.setPreparedStatement(key, ps);
			} catch (SQLException e) {
				System.err.println("prepareStatement创建失败！");
				e.printStackTrace();;
			}
		}
		return ps;
	}
	
	//运行前检查初始化
	public static void add(int id, String name, int age) {
		if(!checkInit()) {
//			throw new IllegalStateException("DBUtils未初始化");
			init();
		}
		DBConnection dbconnection = dbConnectionPool.getConnection();
		try {
			PreparedStatement ps = getPreparedStatement("add",dbconnection);
			if(null == ps) return ;
			try {
				 ps.setInt(1, id);
				 ps.setString(2, name);
				 ps.setInt(3, age);
				 ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			} catch (SQLException e) {
				System.err.println("prepareStatement设置参数失败！");
				e.printStackTrace();
				return ;
			}
			try {
				ps.execute();
			} catch (SQLException e) {
				System.err.println("sql执行失败！");
				e.printStackTrace();
				return ;
			}
		} finally {
			dbConnectionPool.freeConnection(dbconnection);
		}
	}
	
	public static void delete(String name) {
		if(!checkInit()) {
//			throw new IllegalStateException("DBUtils未初始化");
			init();
		}
		DBConnection dbconnection = dbConnectionPool.getConnection();
		try {
			PreparedStatement ps = getPreparedStatement("delete",dbconnection);
			if(null == ps) return ;
			try {
				 ps.setString(1, name);
			} catch (SQLException e) {
				System.err.println("prepareStatement创建失败！");
				e.printStackTrace();
				return ;
			}
			try {
				ps.execute();
			} catch (SQLException e) {
				System.err.println("sql执行失败！");
				e.printStackTrace();
				return ;
			}
		} finally {
			dbConnectionPool.freeConnection(dbconnection);
		}
	}
	
	public static void update(int age, String name) {
		if(!checkInit()) {
//			throw new IllegalStateException("DBUtils未初始化");
			init();
		}
		DBConnection dbconnection = dbConnectionPool.getConnection();
		try {
			PreparedStatement ps = getPreparedStatement("update",dbconnection);
			if(null == ps) return ;
			try {
				 ps.setInt(1, age);
				 ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				 ps.setString(3, name);
			} catch (SQLException e) {
				System.err.println("prepareStatement创建失败！");
				e.printStackTrace();
				return ;
			}
			try {
				ps.execute();
			} catch (SQLException e) {
				System.err.println("sql执行失败！");
				e.printStackTrace();
				return ;
			}
		} finally {
			dbConnectionPool.freeConnection(dbconnection);
		}
	}
	
	public static String search(String name) {
		if(!checkInit()) {
//			throw new IllegalStateException("DBUtils未初始化");
			init();
		}
		DBConnection dbconnection = dbConnectionPool.getConnection();
		try {
			PreparedStatement ps = getPreparedStatement("search",dbconnection);
			if(null == ps) return null;
			try {
				 ps.setString(1, name);
			} catch (SQLException e) {
				System.err.println("prepareStatement创建失败！");
				e.printStackTrace();
				return null;
			}
			ResultSet rs = null;
			try {
				rs = ps.executeQuery();
				String ans = "";
				while(rs.next()) {
					ans += rs.getInt("id") + " ";
					ans += rs.getString("name") + " ";
					ans += rs.getInt("age") + " ";
					ans += rs.getTimestamp("date") + "\n";
				}
				return ans;
			} catch (SQLException e) {
				System.err.println("sql执行失败！");
				e.printStackTrace();
				return null;
			} finally {
				if(null != rs) {
					try {
						rs.close();
					} catch (SQLException e) {
						System.err.println("ResultSet关闭失败！");
						e.printStackTrace();
					}
				}
			}
		} finally {
			dbConnectionPool.freeConnection(dbconnection);
		}
	}
}
