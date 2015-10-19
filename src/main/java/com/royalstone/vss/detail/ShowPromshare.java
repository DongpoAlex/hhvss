package com.royalstone.vss.detail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.XResultAdapter;
/**
 * 促销折让单
 * @author meng
 *
 */
public class ShowPromshare {

	private String sql_sel =  " SELECT "
		+" p.sheetid,p.accmonth,s.shopname,b.bookname,v.vendername,"
		+"t.paytypename,p.totalqty,p.totalamt,p.totalamt17,p.totaltaxamt17,"
		+"p.totaltaxamt13,p.totalsaleamt,p.flag,p.editor,p.editdate,p.checker,"
		+"p.checkdate,p.note";
	final static private String sql_join = " FROM "
		+"Promshare p"
		+ " JOIN vender v ON ( v.venderid = p.venderid ) "
		+ " JOIN book b ON ( b.bookno = p.bookno ) "
		+ " JOIN shop s ON ( s.shopid = p.shopid ) "
		+ " JOIN paytype t ON ( t.paytypeid = p.paytypeid ) "
		+ " WHERE p.sheetid = ? " ;
	public ShowPromshare( Connection conn, String sheetid ) throws SQLException, InvalidDataException, IOException{
		PreparedStatement pstmt = conn.prepareStatement( sql_sel+sql_join );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm_cat = adapter.getRowSetElement( "catalogue", "row" );
		
		int rows = adapter.rows();
		if( rows == 0 ) throw new SQLException( "系统中没有你要的记录!", "NOT_FOUND", 100 );
		elm_cat.setAttribute( "rows", "" + rows );
		elm_cat.setAttribute( "sheetname", "Promshare" );
		
		rs.close();
		pstmt.close();
	}
	public Element toElement() { return elm_cat; }
	private Element elm_cat = null;
}
