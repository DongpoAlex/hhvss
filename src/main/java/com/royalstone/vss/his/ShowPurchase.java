package com.royalstone.vss.his;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * 用于显示订货单订货通知单
 * @autohor baij
 */
public class ShowPurchase 
{
	public ShowPurchase( Connection conn, String sheetid ) throws SQLException, InvalidDataException
	{
		if( conn == null ) throw new InvalidDataException( "conn is null." );
		if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid is invalid." );
		this.conn = conn;
		this.sheetid = sheetid;

	}
	
	private Element getHead() throws SQLException, InvalidDataException, IOException
	{
		String company=getPrintCompany();
		String sql = " SELECT " +
		" p.sheetid, p.refsheetid, sh.shopname destshopname , s.shopname , p.sgroupid,s.shopstatus, p.discountrate, " +
		" c.categoryname, p.paytypeid, pt.paytypename, p.logistics , name4code(p.logistics,'logistics') as logisticsname, " +
		" p.orderdate, p.validdays, (p.orderdate + p.validdays - 1) deadline, p.vdeliverdate,p.venderid, v.vendername, v.faxno, " +
		" p.deliverdate, p.delivertimeid, p.note, p.editor, p.checker, substr(pt.paytypename,1,1) paytypeflag, " +
		" dt.startTime || '-' || dt.endTime deliverTime, '" +
		  company+"' printcompany, "+isyancao+" as yancaoFlag,s.controltype "+
		" FROM purchase_cdept p " +
		" INNER JOIN shop s ON ( s.shopid=p.shopid  ) " +
		" INNER JOIN shop sh ON ( sh.shopid=p.destshopid ) " +
		" left  JOIN category_cdept c ON ( c.categoryid=p.sgroupid ) " +
		" INNER JOIN paytype pt ON ( pt.paytypeid=p.paytypeid ) " +
		" INNER JOIN vender v ON ( v.venderid=p.venderid ) " +
		" LEFT OUTER JOIN DeliverTime dt ON ( dt.DeliverID = p.deliverTimeid ) " +
		" WHERE p.sheetid=?  ";
				
		Log.debug(this.getClass().getName(), sql);

		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );

		Element elm_head = adapter.getRowSetElement( "head", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "Sheet not found: " + sheetid );
		elm_head.setAttribute( "rows", ""+rows );
		elm_head.setAttribute( "name", "purchase" );
		elm_head.setAttribute( "title", title );
		rs.close();
		pstmt.close();
		return elm_head;
	}
	
	private Element getBody( ) throws SQLException, InvalidDataException, IOException
	{
		String sql = " SELECT " +
				" s.shopname destshopname,sh.shopname shopname, " +
				" i.goodsid, i.barcode, g.goodsname, g.spec, g.unitname, " +
				" i.qty, i.pkgqty, i.pkgvolume,(i.qty / i.pkgvolume) as pkqty, i.memo, i.concost,i.cost,i.firstdisc " +
				" FROM purchase_cdept p " +
				" JOIN purchaseitem_cdept i ON (p.sheetid=i.sheetid)  " +
				" JOIN goods_cdept g ON (i.goodsid=g.goodsid) " +
				" JOIN shop s ON (s.shopid=p.destshopid) " +
				" JOIN shop sh ON (sh.shopid=p.shopid) " +
				" WHERE p.sheetid=? order by p.shopid,g.deptid,i.goodsid";
		
		Log.debug(this.getClass().getName(), sql);
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );

		Element elm_body = adapter.getRowSetElement( "body", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "This sheet does not have detail info: " + sheetid );
		elm_body.setAttribute( "rows", ""+rows );
		elm_body.setAttribute( "name", "purchaseitem" );
		rs.close();
		pstmt.close();
		return elm_body;
	}
	/**
	 * 取打印的烟草公司名称
	 * @return
	 */
	private String getPrintCompany() throws SQLException{
		String company=SqlUtil.toLocal(VSSConfig.getInstance().getPrintTobatoTitle());

		String shopid="";
		int majorid=0;
		{//取得门店和课类
			String sql_sel_shop = "select shopid,sgroupid from purchase_cdept where sheetid=? order by 1 desc";
			PreparedStatement ps = conn.prepareStatement(sql_sel_shop);
			ps.setString(1, sheetid);
			ResultSet rs = ps.executeQuery();
			if( rs.next() ){
				shopid = rs.getString("shopid").trim()+"X";
				majorid = rs.getInt("sgroupid");
			}
			rs.close();
			ps.close();
		}
		if( majorid == 28 && !shopid.equals("") ){
			String sql_sel_headshopid = "select s.shopname from shop s " +					
					" where s.shopid=? ";
			PreparedStatement ps = conn.prepareStatement(sql_sel_headshopid);
			ps.setString(1, shopid);
			ResultSet rs = ps.executeQuery();
			if( rs.next() ){
				String title = rs.getString(1);
				if( title != null && title.length() !=0 ) company=title;
				isyancao = 1;
			}
			rs.close();
			ps.close();
		}
		
		return company;
	}
	
	public String getVenderId() throws SQLException{
		String venderid="";
		String sql=" select venderid from purchase_cdept where sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		if(rs.next())
			venderid=rs.getString(1);
		rs.close();
		pstmt.close();
		
		return venderid;
	}
	
	public Element toElement() throws SQLException, InvalidDataException, IOException { 		
		Element elm_sheet = new Element( "sheet" );
		elm_sheet.setAttribute( "name", "purchase" );
		elm_sheet.setAttribute( "title", title );
		elm_sheet.setAttribute( "sheetid", sheetid );
		
		elm_sheet.addContent( this.getHead() );
		elm_sheet.addContent( this.getBody() );
		return elm_sheet; 
	}
	
	final private static String title = "订货通知单";
	final private String sheetid;
	final private Connection conn;
	int isyancao =0;//判断是否烟草,1代表启用烟草，前台根据1 将打印烟草logo和抬头
}
