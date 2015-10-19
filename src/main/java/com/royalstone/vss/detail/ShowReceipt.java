package com.royalstone.vss.detail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.XResultAdapter;

public class ShowReceipt
{
	public ShowReceipt ( Connection conn, String sheetid ) throws SQLException, InvalidDataException, IOException
	{
		if( conn == null ) throw new InvalidDataException( "conn is null." );
		if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid is invalid." );
		this.conn = conn;
		this.sheetid = sheetid;
		this.elm_sheet = new Element( "sheet" );
		this.elm_sheet.setAttribute( "name", "receipt" );
		this.elm_sheet.setAttribute( "title", title );
		this.elm_sheet.setAttribute( "sheetid", sheetid );
		
		this.elm_sheet.addContent( this.getHead() );
		this.elm_sheet.addContent( this.getBody() );
	}
	
	private Element getHead() throws SQLException, InvalidDataException, IOException
	{
		String sql_head = " SELECT h.sheetid, h.refsheetid, "
			+ " h.venderid, v.vendername, h.paytypeid, h.shopid, "
			+ " h.houseid, h.logistics, h.flag, h.note, "
			+ " h.totalqty, h.totalcost, h.editor, "
			+ " h.editdate, h.operator, h.checker, "
			+ " h.checkdate, h.touchtime, h.toucher "
			+ " FROM receipt h "
			+ " JOIN shop s ON ( s.shopid = h.shopid ) "
			+ " JOIN vender v ON( v.venderid = h.venderid )"
			+ " WHERE h.sheetid = ? " ;

		PreparedStatement pstmt = conn.prepareStatement( sql_head );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );

		Element elm_head = adapter.getRowSetElement( "head", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "Sheet not found: " + sheetid );
		elm_head.setAttribute( "rows", ""+rows );
		elm_head.setAttribute( "name", "receipt" );
		elm_head.setAttribute( "title", title );
		return elm_head;
	}
	
	private Element getBody( ) throws SQLException, InvalidDataException, IOException
	{
		String sql_body = " SELECT i.goodsid, g.goodsname, g.barcode, g.unitname, g.spec, "
			+ " i.rcvqty, i.rcvpqty, i.cost, "
			+ " i.costtaxrate, i.saletaxrate "
			+ " FROM receiptitem i "
			+ " JOIN goods g ON ( g.goodsid = i.goodsid ) "
			+ " WHERE i.sheetid = ?  " ;
		
		PreparedStatement pstmt = conn.prepareStatement( sql_body );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );

		Element elm_body = adapter.getRowSetElement( "body", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "This sheet does not have detail info: " + sheetid );
		elm_body.setAttribute( "rows", ""+rows );
		elm_body.setAttribute( "name", "receiptitem" );
		return elm_body;
	}
	public String getVenderId() throws SQLException{
		String venderid="";
		String sql=" select venderid from receipt where sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		if(rs.next())
			venderid=rs.getString(1);
		rs.close();
		pstmt.close();
		
		return venderid;
	}
	public Element toElement() { return elm_sheet; }
	
	final private static String title = "收货单";
	private Element elm_sheet = null;
	final private String sheetid;
	final private Connection conn;
}
