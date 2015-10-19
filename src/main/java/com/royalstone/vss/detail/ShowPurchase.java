package com.royalstone.vss.detail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * 用于显示订货单订货通知单
 * @autohor baij
 */
public class ShowPurchase 
{
	private String title;
	private String logo;
	final private String sheetid;
	final private String month;
	final private Connection conn;
	
	public ShowPurchase( Connection conn, String sheetid,String month ) throws SQLException, InvalidDataException
	{
		if( conn == null ) throw new InvalidDataException( "conn is null." );
		if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid is invalid." );
		this.conn = conn;
		this.sheetid = sheetid;
		this.month = month;
	}
	
	private Element getHead() throws SQLException, InvalidDataException
	{
		String sql = " SELECT " +
		" p.sheetid, p.refsheetid, p.destshopid,p.shopid, sh.shopname destshopname , s.shopname , s.address shopaddr ,s.telno shoptel ,p.sgroupid,s.shopstatus, p.discountrate, " +
		" c.categoryname, p.paytypeid, pt.paytypename, p.logistics , name4code(p.logistics,'logistics') as logisticsname, " +
		" p.orderdate, p.validdays, (p.orderdate + p.validdays - 1) deadline, p.venderid, v.vendername, v.faxno venderfax, v.address venderaddr," +
		" p.deliverdate, p.delivertimeid, p.vdeliverdate,p.note, p.editor, p.checker, substr(pt.paytypename,1,1) paytypeflag, " +
		" dt.startTime || '-' || dt.endTime delivertime, s.controltype "+
		" FROM purchase"+month+" p " +
		" INNER JOIN shop s ON ( s.shopid=p.shopid  ) " +
		" INNER JOIN shop sh ON ( sh.shopid=p.destshopid ) " +
		" left  JOIN category c ON ( c.categoryid=p.sgroupid ) " +
		" INNER JOIN paytype pt ON ( pt.paytypeid=p.paytypeid ) " +
		" INNER JOIN vender v ON ( v.venderid=p.venderid ) " +
		" LEFT OUTER JOIN DeliverTime dt ON ( dt.DeliverID = p.deliverTimeid ) " +
		" WHERE p.sheetid=?  ";
		
		return SqlUtil.getRowSetElement(conn, sql, sheetid, "head");
	}
	
	private Element getBody( ) throws SQLException, InvalidDataException
	{
		String sql = " SELECT " +
				" s.shopid destshopid,s.shopname destshopname,sh.shopname shopname,sh.shopid shopid, " +
				" i.goodsid, i.barcode, g.goodsname, g.spec, g.unitname, g.ycomp,g.qadays," +
				" i.qty, i.pkgqty, i.pkgvolume,trunc((i.qty / i.pkgvolume)) as pkqty, i.memo,i.presentqty, " +
				" i.concost,i.cost,i.firstdisc,(i.qty*i.cost) sumcost " +
				" FROM purchase"+month+" p " +
				" JOIN purchaseitem"+month+" i ON (p.sheetid=i.sheetid)  " +
				" JOIN goods g ON (i.goodsid=g.goodsid) " +
				" JOIN shop s ON (s.shopid=p.destshopid) " +
				" JOIN shop sh ON (sh.shopid=p.shopid) " +
				" WHERE p.sheetid=? order by p.shopid,g.deptid,i.goodsid";
		
		
		return SqlUtil.getRowSetElement(conn, sql, sheetid, "body");
	}
	/**
	 * 取打印的烟草公司名称
	 * @return
	 */
	private void setPrintInfo() throws SQLException{
		String company=null;
		String shopid=null;
		int controltype=0; 
		int majorid=0;
		//controltype 门店控制符合，MR公司用
		//取得门店和课类,抬头公司
		String sql_sel_shop = "select a.shopid,a.sgroupid,s.controltype,s.shopname from purchase"+month+" a " +
				" inner join shop s on s.shopid=a.shopid " +
				" where sheetid=?";
		ResultSet rs = SqlUtil.queryPS(conn, sql_sel_shop, sheetid);
		if( rs.next() ){
			shopid = rs.getString("shopid")+"X";
			majorid = rs.getInt("sgroupid");
			controltype = rs.getInt("controltype");
			company = SqlUtil.toLocal(rs.getString("shopname"));
			//this.logo = rs.getString("booklogofname");
		}
		SqlUtil.close(rs);
		
		//如果是烟草课类，则修改打印抬头和logo
		/*if( majorid == VSSConfig.getInstance().getTobatoMajorid() && !shopid.equals("") ){
			String sql_sel_headshopid = "select b.booktitle,b.booklogofname from shop s " +
					" inner join book b on (b.bookno=s.bookno and s.shopstatus=1) " +
					" where s.shopid=? ";*/
		    String sql_sel_headshopid = "select s.shopname from shop s " +
		            " where s.shopid=? ";
			rs = SqlUtil.queryPS(conn, sql_sel_headshopid, sheetid);
			if( rs.next() ){
				company = SqlUtil.toLocal(rs.getString("shopname"));
				//this.logo = rs.getString("booklogofname");
			}
			SqlUtil.close(rs);
		//民润店
		/*}else if(controltype==2){
			company="MR";
			this.logo = VSSConfig.getInstance().getPrintTobatoLogo();
		}*/
		
		this.title=company+"订货通知单";
		this.logo="../img/"+this.logo;
	}
	
	public String getVenderId() throws SQLException{
		String sql=" select venderid from purchase"+month+" where sheetid=?";
		return SqlUtil.queryPS4SingleColumn(conn, sql, sheetid).get(0);
	}
	
	public Element toElement() throws SQLException, InvalidDataException {
		setPrintInfo();
		Element elm_sheet = new Element( "sheet" );
		elm_sheet.setAttribute( "title", title );
		elm_sheet.setAttribute( "logo", logo );
		
		elm_sheet.addContent( this.getHead() );
		elm_sheet.addContent( this.getBody() );
		return elm_sheet; 
	}
}
