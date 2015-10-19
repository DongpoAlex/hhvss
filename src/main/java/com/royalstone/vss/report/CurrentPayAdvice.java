package com.royalstone.vss.report;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.XResultAdapter;

public class CurrentPayAdvice {

	/**
	 * 取得实时的结算限额
	 * @param conn
	 * @param parms
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException 
	 * @throws IOException 
	 */
	public Element getCurrentPayAdvice(Connection conn, HashMap parms) throws SQLException, InvalidDataException, IOException
	{
		String[] ss = (String[]) parms.get("venderid");
		if (ss == null || ss.length == 0) throw new InvalidDataException("venderid is invalid!");
		String venderid = ss[0];
		if (venderid == null || venderid.length() == 0) throw new InvalidDataException("venderid is invalid!");
		
		Element elm_rpt = null;
		String sql_current = " SELECT "
			+ " a.bookno, b.bookname,b.booktitle,b.booktypeid,b.booklogofname, "
			+ " a.openadviceamt, a.opentaxamt, "
			+ " a.incadviceamt, a.inctaxamt, "
			+ " a.decadviceamt, a.dectaxamt, "
			+ " a.adviceamt, a.taxamt " 
			+ " FROM  VenderPayAdvice a " 
			+ " JOIN book b ON ( a.bookno = b.bookno ) " 
			+ " WHERE a.venderid = ? " 
			+ " ORDER BY 1 DESC, 2 ASC";
		PreparedStatement pstmt = conn.prepareStatement( sql_current );
		pstmt.setString( 1, venderid );		
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		
		elm_rpt = adapter.getRowSetElement( "report", "row" );
		int rows = adapter.rows();
		elm_rpt.setAttribute( "rows", "" + rows );
		elm_rpt.setAttribute( "sheetname", "current_payadvice" );
		
		rs.close();
		pstmt.close();
		return elm_rpt;
	}
}
