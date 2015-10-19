/**
 * 
 */
package com.royalstone.vss.detail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.daemon.XResultAdapter;

/**
 * @author BaiJian
 *
 */
public class ShowRentNote {
	final private Connection conn;
	final private String sheetid;
	/**
	 * @param conn
	 * @param sheetid
	 * @param venderid
	 */
	public ShowRentNote(Connection conn, String sheetid) {
		super();
		this.conn = conn;
		this.sheetid = sheetid;
	}
	
	public Element getSheetHead() throws SQLException, IOException{
		String sql=" select a.sheetid, a.venderid,v.vendername, a.duedate, a.shopid,s.shopname, a.bookno, " +
		" a.receivableamt, a.receiveamt, a.saleamt, a.chargeamt, a.paynum, " +
		" a.flag, a.postflag, a.editor, a.editdate, a.checker, a.checkdate, a.note " +
		"  from rentnote0 a " +
		" join shop s on s.shopid=a.shopid " +
		" join vender v on v.venderid=a.venderid " +
		" where sheetid=?";
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();

		XResultAdapter adapter = new XResultAdapter( rs );
		Element elm_sheethead = adapter.getRowSetElement( "head", "rows" );
		rs.close();
		pstmt.close();
		
		return elm_sheethead;
	}
	
	public Element getSheetBody() throws SQLException, IOException{
		String sql=" select a.sheetid,a.docno,a.doctype,a.docdate,a.duedate,a.bookno,a.shopid,s.shopname, " +
				" a.paytypeid,p.paytypename,a.docamt,a.docamt17,a.taxamt17,a.taxamt13,a.paydocamt,a.paydocamt17, " +
				" a.paytaxamt17,a.paytaxamt13,a.saleamt,a.chargecodeid,c.chargename,a.invoicemode,a.noteremark, a.majorid " +
				" from rentnoteitem0 a " +
				" join shop s on s.shopid=a.shopid " +
				" join paytype p on p.paytypeid=a.paytypeid " +
				" left join chargecode c on c.chargecodeid=a.chargecodeid " +
				" where sheetid=?";
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();

		XResultAdapter adapter = new XResultAdapter( rs );
		Element elm_body = adapter.getRowSetElement( "body", "rows" );
		rs.close();
		pstmt.close();
		
		return elm_body;
	}
	
	public String getVenderId() throws SQLException{
		String venderid="";
		String sql=" select venderid from rentnote0 where sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		if(rs.next())
			venderid=rs.getString(1);
		rs.close();
		pstmt.close();
		
		return venderid;
	}
	
	public Element getSheetDetail() throws SQLException, IOException{
		Element elm = new Element("sheet");
		elm.setAttribute("sheetname","rentnote");
		elm.addContent(getSheetHead());
		elm.addContent(getSheetBody());
		return elm;
	}
}
