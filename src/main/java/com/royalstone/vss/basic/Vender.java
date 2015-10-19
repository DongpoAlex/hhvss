package com.royalstone.vss.basic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.sql.SqlUtil;

public class Vender 
{
	public String venderid;
	public String vendername;
	public String shortname;
	public String address;
	public String zipcode;
	public String website;
	public String email;
	public String telno;
	public String faxno;
	
	static public int getVenderContracttype(Connection conn, String venderid) throws SQLException{
		int contracttype=0;
		String sql = " SELECT contracttype " +
		" FROM vender " +
		" WHERE venderid = ? " ;
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString(1, venderid);
		ResultSet rs = pstmt.executeQuery();
		if(  rs.next() ){
			contracttype = rs.getInt("contracttype");
		}
			
		rs.close();
		pstmt.close();
		
		return contracttype;
	}
	static public Vender getVender( Connection conn, String venderid ) throws SQLException
	{
		String sql = " SELECT venderid, vendername, shortname, address, zipcode, website, email, telno, faxno " +
		" FROM vender " +
		" WHERE venderid = ? " ;

		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString(1, venderid);
		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) throw new SQLException( "没有找到供应商:" + venderid, "NOT_FOUND", 100 );
		Vender v = new Vender();
		v.venderid = rs.getString( "venderid" );
		v.vendername = rs.getString( "vendername" );
		v.shortname = rs.getString( "shortname" );
		v.address = rs.getString( "address" );
		v.zipcode = rs.getString( "zipcode" );
		v.website = rs.getString( "website" );
		v.email = rs.getString( "email" );
		v.telno = rs.getString( "telno" );
		v.faxno = rs.getString( "faxno" );
		
		v.venderid = ( v.venderid == null ) ? "" : SqlUtil.fromLocal( v.venderid );
		v.vendername = ( v.vendername == null ) ? "" : SqlUtil.fromLocal( v.vendername );
		v.shortname = ( v.shortname == null ) ? "" : SqlUtil.fromLocal( v.shortname );
		v.address = ( v.address == null ) ? "" : SqlUtil.fromLocal( v.address );
		v.zipcode = ( v.zipcode == null ) ? "" : SqlUtil.fromLocal( v.zipcode );
		v.website = ( v.website == null ) ? "" : SqlUtil.fromLocal( v.website );
		v.email = ( v.email == null ) ? "" : SqlUtil.fromLocal( v.email );
		v.telno = ( v.telno == null ) ? "" : SqlUtil.fromLocal( v.telno );
		v.faxno = ( v.faxno == null ) ? "" : SqlUtil.fromLocal( v.faxno );
		pstmt.close();
		return v;
	}
	
	static public Map search( Connection conn, String id_min, String id_max,String id ) throws SQLException, InvalidDataException
	{
		HashMap map = new HashMap();
		String sql_count = " SELECT count(*) FROM vender ";
		String sql_list = " SELECT venderid, vendername, shortname, address, zipcode, website, email, telno, faxno " +
				" FROM vender " ;
		
		Filter filter = new Filter();
		if( id_min != null && id_min.length()>0 ) filter.add( " venderid >= " + ValueAdapter.toString4String(id_min) );
		if( id_max != null && id_max.length()>0 ) filter.add( " venderid <= " + ValueAdapter.toString4String(id_max) );
		if( id != null && id.length()>0 ) filter.add( " venderid like '%" + ValueAdapter.toSafeString(id)+"%'" );

		if( filter.count()>0 ) sql_count += " WHERE " + filter.toString();
		PreparedStatement stmt_count = conn.prepareStatement( sql_count );
		ResultSet rs_count = stmt_count.executeQuery();
		rs_count.next();
		int rows = rs_count.getInt( 1 );
		rs_count.close();
		stmt_count.close();
		
		if( rows > ROWS_LIMIT ) throw new InvalidDataException( "目前满足条件的记录为:" + rows + ", 本系统能支持的最大记录数为:" + ROWS_LIMIT + "; 请修改过滤条件."  );
		
		if( filter.count()>0 ) sql_list += " WHERE " + filter.toString();
		PreparedStatement pstmt = conn.prepareStatement( sql_list );
		
		ResultSet rs = pstmt.executeQuery();
		while( rs.next() ) {
			Vender v = new Vender();
			v.venderid = rs.getString( "venderid" );
			v.vendername = rs.getString( "vendername" );
			v.shortname = rs.getString( "shortname" );
			v.address = rs.getString( "address" );
			v.zipcode = rs.getString( "zipcode" );
			v.website = rs.getString( "website" );
			v.email = rs.getString( "email" );
			v.telno = rs.getString( "telno" );
			v.faxno = rs.getString( "faxno" );
			
			v.venderid = ( v.venderid == null ) ? "" : SqlUtil.fromLocal( v.venderid );
			v.vendername = ( v.vendername == null ) ? "" : SqlUtil.fromLocal( v.vendername );
			v.shortname = ( v.shortname == null ) ? "" : SqlUtil.fromLocal( v.shortname );
			v.address = ( v.address == null ) ? "" : SqlUtil.fromLocal( v.address );
			v.zipcode = ( v.zipcode == null ) ? "" : SqlUtil.fromLocal( v.zipcode );
			v.website = ( v.website == null ) ? "" : SqlUtil.fromLocal( v.website );
			v.email = ( v.email == null ) ? "" : SqlUtil.fromLocal( v.email );
			v.telno = ( v.telno == null ) ? "" : SqlUtil.fromLocal( v.telno );
			v.faxno = ( v.faxno == null ) ? "" : SqlUtil.fromLocal( v.faxno );
			map.put( v.venderid, v );
		}
		
		rs.close();
		pstmt.close();
		return map;
	}
	
	
	static final private int ROWS_LIMIT = 2000;
}
