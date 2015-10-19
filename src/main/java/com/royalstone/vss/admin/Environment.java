/*
 * Created on 2005-12-8
 *
 */
package com.royalstone.vss.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.sql.SqlUtil;

/**
 * @author meng
 *
 */
public class Environment {
	
	public Environment( String name, String value )
	{
		this.name = name;
		this.value = value;
	}

	public Environment( Element elm ) throws InvalidDataException
	{
		String name = elm.getAttributeValue( "name" );
		String value = elm.getAttributeValue( "value" );
		if ( name == null || name.length() == 0 ) throw new InvalidDataException( "environment name is invalid." );
		this.name = name;
		this.value = value;
	}

	public Element toElement()
	{
		Element elm_env = new Element( "environment" );
		elm_env.setAttribute( "name", name );
		elm_env.setAttribute( "value", value );
		return elm_env;
	}
	
	public static Element toElement ( Environment[] env_list )
	{
		Element elm_list = new Element( "environment_set" );
		for ( int i=0; i<env_list.length; i++ ){
			elm_list.addContent( env_list[i].toElement() );
		}
		return elm_list;
	}

	/**
	 * @param conn
	 * @param roleid
	 * @return
	 * @throws SQLException
	 */
	public static Environment[] getValue( Connection conn, int roleid ) throws SQLException
	{
		String sql = " SELECT dataflag name, datavalue value FROM role_environment WHERE roleid = ? ORDER BY 1,2";

		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setInt( 1, roleid );
		ResultSet rs = pstmt.executeQuery();
		
		Vector vec = new Vector(0);
		while( rs.next() ){
			String name = rs.getString( "name" );
			String value = rs.getString( "value" );
			name = (name==null) ? "" : SqlUtil.fromLocal( name ).trim();
			value = (value==null) ? "" : SqlUtil.fromLocal( value ).trim();
			Environment env = new Environment( name, value );
			vec.add( env );
		}
		
		rs.close();
		pstmt.close();
		Environment[] lst = new Environment[vec.size()];
		for( int i=0; i<lst.length; i++ ) lst[i] = (Environment) vec.get(i);
		return lst;
	}
	
	/**
	 * @param elm_set
	 * @return
	 * @throws InvalidDataException
	 */
	public static Environment[] parseEnvironmentSet( Element elm_set ) throws InvalidDataException
	{
		List lst = elm_set.getChildren( "environment" );
		
		Environment[] envs = new Environment[ lst.size() ];
		for( int i=0; i<lst.size(); i++ ){
			Environment e = new Environment( (Element) lst.get(i) );
			envs[i] = e;
		}
		return envs;
	}
	
	/**
	 * @param conn
	 * @param roleid
	 * @param env_list
	 * @throws SQLException
	 */
	public static void save( Connection conn, int roleid, Environment[] env_list) throws SQLException
	{
		try {
			conn.setAutoCommit( false );
			delete( conn, roleid );
			insert( conn, roleid, env_list );
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit( true );
		}
	}
	
	/**
	 * @param conn
	 * @param roleid
	 * @param name
	 * @param value
	 * @throws SQLException
	 */
	public static void add( Connection conn, int roleid, String name, String value  ) throws SQLException
	{	
		String sql_save = " INSERT INTO role_environment ( roleid, dataflag, datavalue )  VALUES ( ?, ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement( sql_save );
			pstmt.setInt( 1, roleid );
			pstmt.setString( 2, SqlUtil.toLocal(name) );
			pstmt.setString( 3, SqlUtil.toLocal(value) );
			pstmt.executeUpdate();
		pstmt.close();
	}
	
	
	/**
	 * @param conn
	 * @param roleid
	 * @throws SQLException
	 */
	public static void delete( Connection conn, int roleid ) throws SQLException
	{
		String sql_del = " DELETE FROM role_environment WHERE roleid = ? ";
		PreparedStatement pstmt = conn.prepareStatement( sql_del );
		pstmt.setInt( 1, roleid );
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	/**
	 * @param conn
	 * @param roleid
	 * @throws SQLException
	 */
	public static void delete( Connection conn, int roleid,String name, String value ) throws SQLException
	{
		String sql_del = " DELETE FROM role_environment WHERE roleid = ? AND dataflag=? AND datavalue=? ";
		PreparedStatement pstmt = conn.prepareStatement( sql_del );
		pstmt.setInt( 1, roleid );
		pstmt.setString( 2, SqlUtil.toLocal(name) );
		pstmt.setString( 3, SqlUtil.toLocal(value) );
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	/**
	 * @param conn
	 * @param roleid
	 * @param env_list
	 * @throws SQLException
	 */
	public static void insert( Connection conn, int roleid, Environment[] env_list) throws SQLException
	{
		String sql_save = " INSERT INTO role_environment ( roleid, dataflag, datavalue )  VALUES ( ?, ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement( sql_save );
		for( int i=0; i<env_list.length; i++ ){
			String name = SqlUtil.toLocal(env_list[i].name);
			String value = SqlUtil.toLocal(env_list[i].value);
			pstmt.setInt( 1, roleid );
			pstmt.setString( 2, name );
			pstmt.setString( 3, value );
			pstmt.executeUpdate();
		}
		pstmt.close();
	}
	
	final public String name;
	final public String value;
	
}
