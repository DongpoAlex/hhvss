package com.royalstone.vss.detail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.XResultAdapter;

public class ShowRet 
{

	public ShowRet( Connection conn, String sheetid ) throws SQLException, InvalidDataException, IOException
	{
		if( conn == null ) throw new InvalidDataException( "conn is null." );
		if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid is invalid." );
		this.conn = conn;
		this.sheetid = sheetid;
		this.elm_sheet = new Element( "sheet" );
		this.elm_sheet.setAttribute( "name", "ret" );
		this.elm_sheet.setAttribute( "title", title );
		this.elm_sheet.setAttribute( "sheetid", sheetid );
		
		this.elm_sheet.addContent( this.getHead() );
		this.elm_sheet.addContent( this.getBody() );
	}
	
	private Element getHead() throws SQLException, InvalidDataException, IOException
	{
		String sql_head = " SELECT      re.sheetid, re.refsheetid, "
						+ " re.venderid, v.vendername, "
						+ " re.shopid, sh.shopname,  "
						+ " re.paytypeid, p.paytypename,  "
						+ " re.majorid, c.categoryname majorname, re.logistics, "
						+ " re.retdate, re.flag,  "
						+ " re.editor, re.editdate, re.checker, re.checkdate, re.operator "
						+ " FROM ret re "
						+ " JOIN vender v ON ( v.venderid = re.venderid ) "
						+ " JOIN shop sh ON ( re.shopid = sh.shopid ) "
						+ " JOIN paytype p ON ( p.paytypeid = re.paytypeid ) "
						+ " left join category c ON (c.categoryid=re.majorid) "
						+ " WHERE re.sheetid= ? " ;
//System.out.println( sql_head );
		PreparedStatement pstmt = conn.prepareStatement( sql_head );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );

		Element elm_head = adapter.getRowSetElement( "head", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "Sheet not found: " + sheetid );
		elm_head.setAttribute( "rows", ""+rows );
		elm_head.setAttribute( "name", "ret" );
		elm_head.setAttribute( "title", title );
		rs.close();
		pstmt.close();
		return elm_head;
	}
	
	private Element getBody( ) throws SQLException, InvalidDataException, IOException
	{
		String sql_body = " SELECT i.sheetid, i.goodsid, g.barcode, "
			+ " g.goodsname, g.spec, g.unitname,  "
			+ " i.categoryid, c.categoryname, "
			+ " i.cost, i.askqty, i.vldqty, i.retqty,  "
			+ " i.reasontypeid, i.reason "
			+ " FROM retitem i "
			+ " JOIN goods g ON ( g.goodsid = i.goodsid ) "
			+ " left join category c ON (c.categoryid=i.categoryid) "
			+ " WHERE i.sheetid = ? " ;
		
		PreparedStatement pstmt = conn.prepareStatement( sql_body );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );

		Element elm_body = adapter.getRowSetElement( "body", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "This sheet does not have detail info: " + sheetid );
		elm_body.setAttribute( "rows", ""+rows );
		elm_body.setAttribute( "name", "retitem" );
		rs.close();
		pstmt.close();
		return elm_body;
	}
	
	public String getVenderId() throws SQLException{
		String venderid="";
		String sql=" select venderid from ret where sheetid=?";
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
	
	final private static String title = "退货单";
	private Element elm_sheet = null;
	final private String sheetid;
	final private Connection conn;
}
