package com.royalstone.vss.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Values;

public class RetnoticeManager
{

	public RetnoticeManager( Connection conn ) 
	{
		this.conn = conn;
	}
	
	public int setSheetRead ( Map map ) throws SQLException, InvalidDataException
	{
		int rows = 0;
		String[] arr_vender = (String[]) map.get( "venderid" );
		if( arr_vender == null || arr_vender.length == 0 ) throw new InvalidDataException( "venderid invalid!" );
		String venderid = arr_vender[0];
		if( venderid == null || venderid.length() == 0 ) throw new InvalidDataException( "venderid invalid!" );
		String[] arr_sheet = (String[]) map.get( "sheetid" );
		if( checkSheetVender( venderid, arr_sheet ) ) 
		for( int i=0; i < arr_sheet.length; i++ ) rows += updateReadTime( arr_sheet[i], 0, 1 );
		return rows;
	}
	
	public int setSheetConfirm ( Map map ) throws SQLException, InvalidDataException
	{
		int rows = 0;
		String[] arr_vender = (String[]) map.get( "venderid" );
		if( arr_vender == null || arr_vender.length == 0 ) throw new InvalidDataException( "venderid invalid!" );
		String venderid = arr_vender[0];
		if( venderid == null || venderid.length() == 0 ) throw new InvalidDataException( "venderid invalid!" );
		String[] arr_sheet = (String[]) map.get( "sheetid" );
		if( checkSheetVender( venderid, arr_sheet ) ) 
		for( int i=0; i < arr_sheet.length; i++ ) rows += updateReadTime( arr_sheet[i], 1, 10 );
		return rows;
	}
	
	private boolean checkSheetVender( String venderid, String[] arr_sheet ) throws SQLException
	{
		int wrong_sheets = 0;
		Values arr_val = new Values ( arr_sheet );
		String sql_chk = " SELECT sheetid, venderid FROM retnotice "
			+ " WHERE sheetid IN ( " + arr_val.toString4String() + " ) ";
		PreparedStatement pstmt = conn.prepareStatement( sql_chk );
		ResultSet rs = pstmt.executeQuery();
		while( rs.next() ) {
			String vid = rs.getString( "venderid" );
			vid = vid.trim();
			if ( !vid.equals( venderid ) ) wrong_sheets++ ;
		}
		rs.close();
		pstmt.close();
		return ( wrong_sheets == 0 );
	}
	
	
	private int updateReadTime( String sheetid, int status_old, int status_new ) throws SQLException
	{
		String sql_upd = " UPDATE cat_retnotice SET status=?, readtime=sysdate WHERE status=? AND sheetid=? ";
		PreparedStatement pstmt = conn.prepareStatement( sql_upd );
		pstmt.setInt( 1, status_new );
		pstmt.setInt( 2, status_old );
		pstmt.setString( 3, sheetid );
		
		int rows = pstmt.executeUpdate();
		pstmt.close();
		return rows;
	}
	
	private int updateStatus( String sheetid, int status_old, int status_new ) throws SQLException
	{
		String sql_upd = " UPDATE cat_retnotice SET status=? WHERE status=? AND sheetid=? ";
		PreparedStatement pstmt = conn.prepareStatement( sql_upd );
		pstmt.setInt( 1, status_new );
		pstmt.setInt( 2, status_old );
		pstmt.setString( 3, sheetid );
		
		int rows = pstmt.executeUpdate();
		pstmt.close();
		return rows;
	}
	final private Connection conn;
}
