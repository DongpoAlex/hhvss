/**
 * 
 */
package com.royalstone.vss.his;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.daemon.XResultAdapter;

/**
 * @author BaiJian
 * 带货安装单明细显示
 */
public class ShowSalePick {
	final private Connection conn;
	final private String sheetid;
	/**
	 * @param conn
	 * @param sheetid
	 * @param venderid
	 */
	public ShowSalePick(Connection conn, String sheetid) {
		super();
		this.conn = conn;
		this.sheetid = sheetid;
	}
	
	public Element getSheetHead() throws SQLException, IOException{
		String sql=" SELECT sp.sheetid,sp.refsheetid,sp.flag,sp.shopid,s.shopname,sp.saledate,sp.customer,sp.telephone, " +
		" sp.cman,sp.address,sp.deliverdate,sp.pickshopid,sp.pickdate,sp.consignee,sp.dman,sp.notes,sp.recordtime, " +
		" sp.deliverflag,sp.venderid,v.vendername,sp.paytypeid,sp.majorid,sp.editor,sp.operator,sp.editdate,sp.checker, " +
		" sp.checkdate,sp.trandate,sp.name,sp.btime,sp.servicephone,sp.sendphone,sp.nopayvalue " +
		" FROM salepick_cdept sp " +
		" INNER JOIN shop s ON ( s.shopid=sp.shopid ) " +
		" INNER JOIN vender v ON ( v.venderid=sp.venderid ) " +
		" where sp.sheetid=?";
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
		String sql=" SELECT spi.sheetid,spi.placeid,p.placename,spi.goodsid,g.barcode, " +
				" g.deptid,g.goodsname,spi.price,spi.qty,spi.discpricev,spi.disccostv " +
				" FROM salepickitem_cdept spi " +
				" INNER JOIN salepick_cdept sp ON ( sp.sheetid=spi.sheetid ) " +
				" INNER JOIN goods_cdept g ON ( g.goodsid=spi.goodsid ) " +
				" LEFT OUTER JOIN place p ON ( p.placeid=spi.placeid AND sp.shopid=p.shopid ) " +
				" where spi.sheetid=?";
		
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
		String sql=" select venderid from salepick_cdept where sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		if(rs.next())
			venderid=rs.getString(1);
		rs.close();
		pstmt.close();
		
		return venderid;
	}
	
	public int updateStatus(int oldStatus,int newStatus,String sheetid) throws SQLException{
		String sql="update cat_salepick_cdept set status=? where sheetid=? and status=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, newStatus);
		pstmt.setString(2, sheetid);
		pstmt.setInt(3, oldStatus);
		int i = pstmt.executeUpdate();
		pstmt.close();
		return i;
	}
	
	public Element getSheetDetail() throws SQLException, IOException{
		Element elm = new Element("sheet");
		elm.setAttribute("sheetname","rentnote");
		elm.addContent(getSheetHead());
		elm.addContent(getSheetBody());
		return elm;
	}
}
