/*
 * Created on 2005-12-29
 *
 */
package com.royalstone.vss.admin;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * NewPass 用于在后台修改用户密码. 系统管理员专用.
 * @author meng
 *
 */
public class NewPass 
{

	public static void main(String[] args) 
	{
		if( args.length < 2 ){
			usage();
			return;
		}
		
		String cfg_file = ( args.length == 3 ) ? args[2] : "admin.ini";

		String loginid = args[0];
		String new_pass = args[1];
		
		try {
			Connection conn = openDataSource( cfg_file );
			UserAdm adm = new UserAdm( conn );
			adm.setPassword( loginid, new_pass );
//			System.out.println( "password set.");
		} catch (SQLException e) {
//			System.out.println( "Failed. " + e.getErrorCode() + ":" + e.getMessage() );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static Connection openDataSource( String cfg_file ) throws ClassNotFoundException, IOException, SQLException
	{
		Connection conn = null;
		Properties prop = new Properties();
		FileInputStream input = new FileInputStream( cfg_file );
		prop.load( input );
		
//		String hostip = prop.getProperty( "hostip" );
//		String port   = prop.getProperty( "port" );
//		String informixserver = prop.getProperty( "informixserver" );
//		String dbname = prop.getProperty( "dbname" );
		String username = prop.getProperty( "username" );
		String password = prop.getProperty( "password" );
//		String dbURL = "jdbc:informix-sqli://" + hostip + ":" + port + "/"+ dbname
//			+ ":informixserver="+ informixserver;	
		String dbURL = prop.getProperty( "db_url" );

		Class.forName( "com.informix.jdbc.IfxDriver" );
		conn = DriverManager.getConnection( dbURL, username, password );
		return conn;
	}
	
	
	public static void usage()
	{
		System.out.println( "Usage:" );
		System.out.println( "NewPass  loginid new_password" );
		
	}
}
