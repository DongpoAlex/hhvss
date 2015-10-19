package com.royalstone.myshop.info;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.daemon.XResultAdapter;

public class InfoMajor {

	public InfoMajor ( Connection conn, Map map ) throws SQLException, IOException {
		this.conn = conn;
		getInfo();
	}
	
	private void getInfo () throws SQLException, IOException {
		String sql = " SELECT categoryid, categoryname, taxrate, note FROM category WHERE deptlevelid = 1 " +
				" ORDER BY 1 ";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		elm = adapter.getRowSetElement( "catalogue", "row" );
		elm.setAttribute( "rows", "" + adapter.rows() );
		rs.close();
		pstmt.close();
	}
	
	public Element toElement()
	{
		return elm;
	}

	final private Connection conn;
	private Element elm;
}
