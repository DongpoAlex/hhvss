package com.royalstone.vss.detail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * 此模块用于查询两层主从结构的订货审批单明细: purchase0/purchase/purchaseitem.
 * @author meng
 *
 */
public class ShowPurchsechk 
{
	private String title;
	private String logo;
	final private String sheetid;
	final private String month;
	final private Connection conn;
	
	public ShowPurchsechk( Connection conn, String sheetid, String month ) throws SQLException, InvalidDataException
	{
		this.conn = conn;
		this.sheetid = sheetid;
		this.month = month;
	}
	
	/**
	 * 此模块从数据库取订货审批单的表头信息.
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	private Element getHead() throws SQLException, InvalidDataException
	{
		String sql = " SELECT p.sheetid, "
			+ " p.venderid, v.vendername, v.address venderaddr,v.telno vendertel,v.faxno venderfax, "
			+ " p.paytypeid, pt.paytypename, substr(pt.paytypename,1,1) paytypeflag, "
			+ " p.sgroupid, sg.categoryname sgroupname, "
			+ " p.orderdate, p.validDays, p.deadline, p.vdeliverdate,p.discountrate,"
			+ " p.logistics, name4code(p.logistics, 'logistics') logisticsname, p.logistics, "
			+ " p.note, c.releasedate,c.status,c.readtime, p.editor,p.editdate, p.checker, p.checkdate "
			+ " FROM purchase0"+month+" p"
			+ " JOIN cat_order"+month+" c ON ( c.sheetid=p.sheetid ) "
			+ " JOIN vender v ON ( v.venderid=p.venderid ) "
			+ " JOIN paytype pt ON ( pt.paytypeid=p.paytypeid ) "
			+ " left join category  sg ON ( sg.categoryid = p.sgroupid ) "
			+ " WHERE p.sheetid = ? ";
		
		return SqlUtil.getRowSetElement(conn, sql, sheetid, "head");

	}
	/**
	 * 此模块从数据库中取出订货审批单对应的订货单信息.
	 * 门店+单品汇总信息
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	private Element getOderCatalogue( ) throws SQLException, InvalidDataException
	{
		String sql = " SELECT "
			+" p.sheetid,"
			+" p.destshopid, s.shopname destshopname,s.address,s.telno,"
			+" p.shopid, sh.shopname shopname,"
			+" p.deliverdate, p.checkdate, "
			+" p.deliverTimeid, dt.starttime || '-' || dt.endtime delivertime,s.controltype "
			+" FROM purchase"+month+" p"
			+" JOIN shop s ON (s.shopid=p.destshopid)"
			+" JOIN shop sh ON (sh.shopid=p.shopid)"
			+" LEFT OUTER JOIN DeliverTime dt ON ( dt.DeliverID = p.deliverTimeid )"
			+" WHERE refsheetid = ? order by p.shopid ";
		
		return SqlUtil.getRowSetElement(conn, sql, sheetid, "body");
	}
	/**
	 * 此模块从数据库中取订货商品明细信息.
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	private Element getOrderDetail() throws SQLException, InvalidDataException
	{
		String sql = "SELECT "
			+ " p.sheetid, "
			+ " p.destshopid, s.shopname destshopname,"
			+ " p.shopid, sh.shopname shopname,sh.shopstatus,"
			+ " i.goodsid, i.barcode, g.goodsname, g.spec, g.unitname,g.ycomp,g.qadays,"
			+ " i.qty, i.pkgqty, i.pkgvolume, trunc((i.qty / i.pkgvolume)) as pkqty, i.memo,i.presentqty, " 
			+ " i.concost,i.cost,i.firstdisc,(i.qty*i.cost) sumcost  "
			+ " FROM "
			+ " purchase"+month+" p"
			+ " JOIN purchaseitem"+month+" i ON (p.sheetid=i.sheetid) "
			+ " JOIN goods g ON (i.goodsid=g.goodsid) "
			+ " JOIN shop s ON (s.shopid=p.destshopid) "
			+ " JOIN shop sh ON (sh.shopid=p.shopid) "
			+ " WHERE p.refsheetid= ? order by p.shopid,g.deptid,i.goodsid";
		
		return SqlUtil.getRowSetElement(conn, sql, sheetid, "bodydetail");
	}
	

	
	
	/**
	 * 按单品汇总
	 * @return
	 * @throws SQLException
	 */
	private Element getGoodsGroup() throws SQLException{
		String sql=" select i.goodsid, Max(i.barcode) barcode, MAX(g.goodsname) goodsname,MAX(g.spec) spec, max(g.ycomp) ycomp,max(g.qadays) qadays," +
				" MAX(g.unitname) unitname,MAX(i.pkgqty) pkgqty, MAX(i.pkgvolume) pkgvolume, " +
				" trunc(sum(i.qty / i.pkgvolume)) as pkqty,MAX(i.cost) cost,MIN(i.firstdisc) firstdisc," +
				" sum(i.qty) qty, MAX(i.concost) concost,sum(i.qty*i.cost) sumcost, sum(presentqty) presentqty " +
				" from purchaseitem"+month+" i " +
				" JOIN goods g ON (i.goodsid=g.goodsid) " +
				" WHERE i.sheetid in (select sheetid from purchase"+month+" where refsheetid=?) " +
				" group by i.goodsid order by 1 ";
		return SqlUtil.getRowSetElement(conn, sql, sheetid, "goodsgroup");
	}
	
	public String getVenderId() throws SQLException{
		String sql=" select venderid from purchase0"+month+" where sheetid=?";
		return SqlUtil.queryPS4SingleColumn(conn, sql, sheetid).get(0);
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
				" where refsheetid=? and rownum=1 ";
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
		}	*/
			
		this.title=company+"订货审批单";
		this.logo="../img/"+this.logo;
	}
	
	public Element toElement() throws SQLException, InvalidDataException { 
		setPrintInfo();
		Element elm_sheet = new Element( "sheet" );
		elm_sheet.setAttribute( "title", title );
		elm_sheet.setAttribute( "logo", logo );
		
		elm_sheet.addContent( this.getHead() );
		elm_sheet.addContent( this.getOderCatalogue() );
		elm_sheet.addContent( this.getOrderDetail() );
		elm_sheet.addContent( this.getGoodsGroup());
		
		return elm_sheet; }
	
}
