package com.royalstone.myshop.basic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.sql.SqlUtil;

public class Vender 
{
	static public Vender getVender( Connection conn, String venderid ) throws SQLException, InvalidDataException
	{
		String sql = " SELECT vendername, address, telno, faxno, email " +
				" FROM vender WHERE venderid = ? ";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, venderid );
		ResultSet rs = pstmt.executeQuery();
		if( ! rs.next() ) throw new SQLException( "vender NOT FOUND:" + venderid, "", 100 );
		Vender vender = new Vender();
		vender.venderid = venderid;
		vender.vendername = rs.getString( "vendername" );
		vender.shortname = rs.getString( "vendername" );
		vender.address 	= rs.getString( "address" );
		vender.telno 	= rs.getString( "telno" );
		vender.faxno 	= rs.getString( "faxno" );
		vender.email 	= rs.getString( "email" );
		
		if( vender.vendername == null ) throw new InvalidDataException( "vendername is null!" );
		vender.vendername = SqlUtil.fromLocal( vender.vendername );
		vender.shortname = ( vender.shortname == null )? "" : SqlUtil.fromLocal( vender.shortname );
		vender.address = ( vender.address == null )? "" : SqlUtil.fromLocal( vender.address );
		pstmt.close();
		return vender;
	}

	
	public String venderid;
	public String vendername;
	public String shortname;
	public String address;
	public String zipcode;
	public String telno;
	public String faxno;
	public String email;
	public String website;
}
