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
 * 零售商品销售调整单
 * @author meng
 *
 */
public class ShowSaleadj {

	private String sql_sel =  " SELECT "
		+ " p.sheetid, p.accmonth, s.shopname, p.saledate, p.totalsaleamt, "
		+ " p.orssaleamt, p.orssaletaxamt17, p.orssaletaxamt13, p.ocssaleamt, "
		+ " p.ocssaletaxamt17, p.ocssaletaxamt13, p.orentsaleamt, p.orsamt, p.ocsamt ";
	final static private String sql_join = " FROM "
		+ " Saleadj p"
		+ " JOIN shop s ON ( s.shopid = p.shopid ) "
		+ " WHERE p.sheetid = ? " ;
	public ShowSaleadj( Connection conn, String sheetid ) throws SQLException, InvalidDataException, IOException{
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
