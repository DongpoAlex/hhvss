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

public class ShowRetNotice 
{
	
	public ShowRetNotice( Connection conn, String sheetid ) throws SQLException, InvalidDataException, IOException
	{
		if( conn == null ) throw new InvalidDataException( "conn is null." );
		if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid is invalid." );
		this.conn = conn;
		this.sheetid = sheetid;
		this.elm_sheet = new Element( "sheet" );
		this.elm_sheet.setAttribute( "name", "retnotice" );
		this.elm_sheet.setAttribute( "title", title );
		this.elm_sheet.setAttribute( "sheetid", sheetid );
		
		this.elm_sheet.addContent( this.getHead() );
		this.elm_sheet.addContent( this.getBody() );
	}
	
	private Element getHead() throws SQLException, InvalidDataException, IOException
	{
		String company=getPrintCompany();
		String sql =  "SELECT 	re.sheetid, re.refsheetid, "
			+" re.venderid, v.vendername, v.faxno, "
			+" re.shopid, sh.shopname, sh.address, sh.telno, sh.shopstatus," 
			+" p.paytypeid, p.paytypename, re.retdate, "
			+" re.flag, re.editor, re.editdate, '"
			+company+"' printcompany , "+isyancao+" as yancaoFlag,"
			+" re.operator, re.checker, re.checkdate, "
			+" re.placeid, pe.placename, re.majorid, c.categoryname majorname, " +
			" re.note, cat.status, cat.warnstatus,cat.releasedate,re.rettype, " +
			" re.askshopid,ss.shopname as askshopname,sh.controltype "
			+" FROM retnotice_cdept re "
			+" JOIN vender v ON ( v.venderid = re.venderid ) "
			+" JOIN shop sh ON ( re.shopid = sh.shopid ) "
			+" LEFT JOIN shop ss ON ( re.askshopid = ss.shopid ) "
			+" JOIN paytype p ON ( p.paytypeid = re.paytypeid ) "
			+" LEFT JOIN place pe ON ( pe.shopid = re.askshopid AND pe.placeid = re.placeid ) "
			+" left join category_cdept c ON ( categoryid = re.majorid ) " 
			+" JOIN cat_retnotice_cdept cat ON ( cat.sheetid = re.sheetid ) "
			+" WHERE re.sheetid= ?  " ;
		
		Log.debug(this.getClass().getName(), sql);
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );

		Element elm_head = adapter.getRowSetElement( "head", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "Sheet not found: " + sheetid );
		elm_head.setAttribute( "rows", ""+rows );
		elm_head.setAttribute( "name", "retnotice" );
		elm_head.setAttribute( "title", title );
		rs.close();
		pstmt.close();
		return elm_head;
	}
	
	private Element getBody( ) throws SQLException, InvalidDataException, IOException
	{
		String sql = " SELECT i.sheetid, i.goodsid, g.barcode, g.goodsname, g.spec, g.unitname, " +
				" i.vldqty, i.retqty, i.cost, (i.cost*i.vldqty) costvalue, i.reason, " +
				" i.categoryid, c.categoryname " +
				" FROM retnoticeitem_cdept i " +
				" JOIN goods_cdept g ON ( g.goodsid = i.goodsid ) " +
				" left join category_cdept c ON ( c.categoryid = i.categoryid ) " +
				" WHERE i.sheetid = ? " ;
		
		Log.debug(this.getClass().getName(), sql);
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );

		Element elm_body = adapter.getRowSetElement( "body", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "This sheet does not have detail info: " + sheetid );
		elm_body.setAttribute( "rows", ""+rows );
		elm_body.setAttribute( "name", "retnoticeitem" );
		rs.close();
		pstmt.close();
		return elm_body;
	}
	
	public String getVenderId() throws SQLException{
		String venderid="";
		String sql=" select venderid from retnotice_cdept where sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement( sql );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		if(rs.next())
			venderid=rs.getString(1);
		rs.close();
		pstmt.close();
		
		return venderid;
	}
	
	/**
	 * 取打印的烟草公司名称
	 * @return
	 * @throws SQLException 
	 */
	private String getPrintCompany() throws SQLException{
		String company=SqlUtil.toLocal(VSSConfig.getInstance().getPrintTobatoTitle());

		String shopid="";
		int majorid=0;
		{//取得门店和课类
			String sql_sel_shop = "select shopid,majorid from retnotice_cdept where sheetid=? ";
			PreparedStatement ps = conn.prepareStatement(sql_sel_shop);
			ps.setString(1, sheetid);
			ResultSet rs = ps.executeQuery();
			if( rs.next() ){
				shopid = rs.getString("shopid").trim()+"X";
				majorid = rs.getInt("majorid");
			}
			rs.close();
			ps.close();
		}
		if( majorid == 28 && !shopid.equals("") ){
			String sql_sel_headshopid = "select b.booktitle from shop s " +
			" inner join book b on (b.bookno=s.bookno and s.shopstatus=1) " +
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
	
	
	
	public Element toElement() { return elm_sheet; }
	
	public void setSheetRead ( String venderid ) throws SQLException, InvalidDataException
	{
		if( checkSheetVender( venderid, sheetid ) ) 
		updateReadTime( sheetid, 0, 1 );
	}
	
	private boolean checkSheetVender( String venderid, String sheetid ) throws SQLException
	{
		int wrong_sheets = 0;
		String sql_chk = " SELECT sheetid, venderid FROM retnotice_cdept "
			+ " WHERE sheetid =?";
		PreparedStatement pstmt = conn.prepareStatement( sql_chk );
		pstmt.setString(1, sheetid);
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
		String sql_upd = " UPDATE cat_retnotice_cdept SET status=?, readtime=sysdate WHERE status=? AND sheetid=? ";
		PreparedStatement pstmt = conn.prepareStatement( sql_upd );
		pstmt.setInt( 1, status_new );
		pstmt.setInt( 2, status_old );
		pstmt.setString( 3, sheetid );
		
		int rows = pstmt.executeUpdate();
		pstmt.close();
		return rows;
	}
	
	final private static String title = "退货通知单";
	private Element elm_sheet = null;
	final private String sheetid;
	final private Connection conn;
	int isyancao =0;//判断是否烟草,1代表启用烟草，前台根据1 将打印烟草logo和抬头
}
