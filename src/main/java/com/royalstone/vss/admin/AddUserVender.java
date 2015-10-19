package com.royalstone.vss.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class AddUserVender
{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if( args.length != 2 ) {
			System.err.println( "Usage: AddUserVender dbconnect_file passwd_file" );
			System.err.println( "Data in passwd_file should be seperated by TAB, including such columns:" );
			System.err.println( "venderid loginid username passwd menuroot");
			return;
		}
		
		String file_config = args[0];
		String file_passwd = args[1];
		
		File fconfig = new File ( file_config );
		if( ! fconfig.exists() ) {
			System.err.println( "File not exists, please check your file path:" + file_config );
			return;
		}
		
		File fpasswd = new File ( file_passwd );
		if( ! fpasswd.exists() ) {
			System.err.println( "File not exists, please check your file path: " + file_passwd );
			return;
		}
		
		try {
			Connection conn = openDataBase( fconfig );
			VenderUserAdm adm = new VenderUserAdm( conn );

			FileReader freader = new FileReader( fpasswd );
			LineNumberReader lreader = new LineNumberReader( freader );
			while ( true ) {
				String line = lreader.readLine();
				if( line == null ) break;
				
				String[] arr_str = line.split( "\t", 5 );
				/**
				 * venderid:loginid:username:passwd:menuroot
				 */
				if( arr_str != null && arr_str.length == 5){
					String venderid 	= arr_str[0];
					String loginid 		= arr_str[1];
					String username 	= arr_str[2];
					String passwd 		= arr_str[3];
					String menuroot 	= arr_str[4];
					
					loginid = loginid.toUpperCase();
					
					int menuid = Integer.parseInt( menuroot );
					adm.addVenderUser( -1, username, loginid, venderid, passwd, menuid );
					System.out.println( loginid + " added for vender: " + venderid );
				} else {
					System.err.println( line + " : INVALID DATA");
				}
			}
			conn.close();
		} catch (FileNotFoundException e) {
			System.err.println( e.toString() );
		} catch (IOException e) {
			System.err.println( e.toString() );
		} catch (SQLException e) {
			System.err.println( e.toString() );
		} catch (ClassNotFoundException e) {
			System.err.println( e.toString() );
		} finally {
			
		}

	}
	
	static private Connection openDataBase( File fconfig ) throws IOException, SQLException, ClassNotFoundException
	{
		FileInputStream fin = new FileInputStream( fconfig );
		Properties prop = new Properties();
		prop.load( fin );
		
		String username = prop.getProperty( "username" );
		String password = prop.getProperty( "password" );
		String url 		= prop.getProperty( "url" );
//		System.out.println( url );
//		System.out.println( username );
//		System.out.println( password );
		
		if( url == null || url.length() == 0 ) throw new IllegalArgumentException( "invalid url for db!" );
		if( username == null || username.length() == 0 ) throw new IllegalArgumentException( "invalid username for db!" );
		if( password == null || password.length() == 0 ) throw new IllegalArgumentException( "invalid password for db!" );
		
		Connection conn = openDataBase( url, username, password );
		return conn;
	}
	
	static private Connection openDataBase( String dburl, String user, String passwd ) 
	throws SQLException, ClassNotFoundException
	{
		Class.forName ( "com.informix.jdbc.IfxDriver" );
		Connection connection = DriverManager.getConnection( dburl, user, passwd );
		return connection;
	}

}
