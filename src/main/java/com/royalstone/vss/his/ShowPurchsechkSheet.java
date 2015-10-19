package com.royalstone.vss.his;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * 此模块用于查询两层主从结构的订货审批单明细: purchase0/purchase/purchaseitem.
 * 
 * @author meng
 * 
 */
public class ShowPurchsechkSheet {

	public ShowPurchsechkSheet(Connection conn, String sheetid) throws SQLException, InvalidDataException {
		this.conn = conn;
		this.sheetid = sheetid;
	}

	/**
	 * 此模块从数据库取订货审批单的表头信息.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	private Element getHead() throws SQLException, InvalidDataException, IOException {
		String company = getPrintCompany();
		String sql = " SELECT p.sheetid, " + " p.venderid, v.vendername, "
				+ " p.paytypeid, pt.paytypename, substr(pt.paytypename,1,1) paytypeflag, "
				+ " p.sgroupid, sg.categoryname sgroupname, "
				+ " p.orderdate, p.validDays, p.deadline, p.vdeliverdate,p.discountrate,"
				+ " p.logistics, name4code(p.logistics, 'logistics') logisticsname, p.logistics, "
				+ "  p.note, c.releasedate,c.status, p.editor, p.checker, p.checkdate,' " + company
				+ "' printcompany, " + isyancao + " as yancaoFlag " + " FROM purchase0_cdept p"
				+ " JOIN cat_order_cdept c ON ( c.sheetid=p.sheetid ) "
				+ " JOIN vender v ON ( v.venderid=p.venderid ) "
				+ " JOIN paytype pt ON ( pt.paytypeid=p.paytypeid ) "
				+ " left join category_cdept  sg ON ( sg.categoryid = p.sgroupid ) " + " WHERE p.sheetid = ? ";

		Log.debug(this.getClass().getName(), sql);

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);

		Element elm_head = adapter.getRowSetElement("head", "row");
		int rows = adapter.rows();
		if (rows == 0) throw new InvalidDataException("Sheet not found: " + this.sheetid);
		elm_head.setAttribute("rows", "" + rows);
		elm_head.setAttribute("name", "purchase0");
		rs.close();
		pstmt.close();
		return elm_head;
	}

	/**
	 * 此模块从数据库中取订货商品明细信息.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	private Element getOrderDetail() throws SQLException, InvalidDataException, IOException {
		String sql = "SELECT " + " p.sheetid, " + " p.destshopid, s.shopname destshopname,"
				+ " p.shopid, sh.shopname shopname,sh.shopstatus,"
				+ " i.goodsid, i.barcode, g.goodsname, g.spec, g.unitname,"
				+ " i.qty, i.pkgqty, i.pkgvolume, (i.qty / i.pkgvolume) as pkqty, i.memo, "
				+ " i.concost,i.cost,i.firstdisc " + " FROM purchase_cdept p"
				+ " JOIN purchaseitem_cdept i ON (p.sheetid=i.sheetid) "
				+ " JOIN goods_cdept g ON (i.goodsid=g.goodsid) " + " JOIN shop s ON (s.shopid=p.destshopid) "
				+ " JOIN shop sh ON (sh.shopid=p.shopid) "
				+ " WHERE p.refsheetid= ? order by p.shopid,g.deptid,i.goodsid";

		Log.debug(this.getClass().getName(), sql);

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);

		Element elm_body = adapter.getRowSetElement("body", "row");
		int rows = adapter.rows();
		if (rows == 0) throw new InvalidDataException("This sheet does not have detail info: " + this.sheetid);
		elm_body.setAttribute("rows", "" + rows);
		elm_body.setAttribute("name", "purchaseitem");
		rs.close();
		pstmt.close();
		return elm_body;
	}

	/**
	 * 此模块从数据库中取出订货审批单对应的订货单信息. 门店+单品汇总信息
	 * 
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	private Element getOderCatalogue() throws SQLException, InvalidDataException, IOException {
		String sql = " SELECT " + " p.sheetid," + " p.destshopid, s.shopname destshopname,"
				+ " p.shopid, sh.shopname shopname," + " p.deliverdate, p.checkdate, "
				+ " p.deliverTimeid, dt.startTime || '-' || dt.endTime deliverTime,s.controltype "
				+ " FROM purchase_cdept p" + " JOIN shop s ON (s.shopid=p.destshopid)"
				+ " JOIN shop sh ON (sh.shopid=p.shopid)"
				+ " LEFT OUTER JOIN DeliverTime dt ON ( dt.DeliverID = p.deliverTimeid )"
				+ " WHERE refsheetid = ? order by p.shopid ";

		Log.debug(this.getClass().getName(), sql);

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);

		Element elm_body = adapter.getRowSetElement("body", "row");
		int rows = adapter.rows();
		if (rows == 0) throw new InvalidDataException("This sheet does not have detail info: " + this.sheetid);
		elm_body.setAttribute("rows", "" + rows);
		elm_body.setAttribute("name", "purchase");
		rs.close();
		pstmt.close();
		return elm_body;
	}

	/**
	 * 按单品汇总
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	private Element getGoodsGroup() throws SQLException, IOException {
		String sql = " select i.goodsid, Max(i.barcode) barcode, MAX(g.goodsname) goodsname,MAX(g.spec) spec, MAX(g.unitname) unitname,MAX(i.pkgqty) pkgqty, MAX(i.pkgvolume) pkgvolume, sum(i.qty / i.pkgvolume) as pkqty,MAX(i.cost) cost,MIN(i.firstdisc) firstdisc,sum(i.qty) qty, MAX(i.concost) concost"
				+ " from purchaseitem_cdept i "
				+ " JOIN goods_cdept g ON (i.goodsid=g.goodsid) "
				+ " WHERE i.sheetid in (select sheetid from purchase_cdept where refsheetid=?) "
				+ " group by i.goodsid order by 1 ";

		Log.debug(this.getClass().getName(), sql);

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);

		Element elm_body = adapter.getRowSetElement("goodsgroup", "row");
		int rows = adapter.rows();
		elm_body.setAttribute("rows", "" + rows);
		elm_body.setAttribute("name", "goodsgroup");
		rs.close();
		pstmt.close();
		return elm_body;
	}

	public String getVenderId() throws SQLException {
		String venderid = "";
		String sql = " select venderid from purchase0_cdept where sheetid=?";

		Log.debug(this.getClass().getName(), sql);

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, this.sheetid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) venderid = rs.getString(1);
		rs.close();
		pstmt.close();

		return venderid;
	}

	/**
	 * 取打印的烟草公司名称
	 * 
	 * @return
	 */
	private String getPrintCompany() throws SQLException {
		String company = SqlUtil.toLocal(VSSConfig.getInstance().getPrintTobatoTitle());

		String shopid = "";
		int majorid = 0;
		{// 取得门店和课类
			String sql_sel_shop = "select shopid,sgroupid from purchase_cdept where refsheetid=? order by 1 desc";
			PreparedStatement ps = conn.prepareStatement(sql_sel_shop);
			ps.setString(1, sheetid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				shopid = rs.getString("shopid").trim() + "X";
				majorid = rs.getInt("sgroupid");
			}
			rs.close();
			ps.close();
		}

		if (majorid == 28 && !shopid.equals("")) {
			String sql_sel_headshopid = "select s.shopname from shop s "
					 + " where s.shopid=? ";
			PreparedStatement ps = conn.prepareStatement(sql_sel_headshopid);
			ps.setString(1, shopid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String title = rs.getString(1);
				if (title != null && title.length() != 0) company = title;
				isyancao = 1;
			}
			rs.close();
			ps.close();
		}

		return company;
	}

	public Element toElement() throws SQLException, InvalidDataException, IOException {
		Element elm_sheet = new Element("sheet");
		elm_sheet.setAttribute("name", "ordersheet");
		elm_sheet.setAttribute("title", title);
		elm_sheet.setAttribute("sheetid", sheetid);

		elm_sheet.addContent(this.getHead());
		elm_sheet.addContent(this.getOderCatalogue());
		elm_sheet.addContent(this.getOrderDetail());
		elm_sheet.addContent(this.getGoodsGroup());

		return elm_sheet;
	}
	
	
	public void setSheetRead( String venderid ) throws SQLException, InvalidDataException
	{
		if( checkSheetVender( venderid, this.sheetid ) ) 
		updateReadTime( sheetid, 0, 1 );
	}
	
	private int updateReadTime( String sheetid, int status_old, int status_new ) throws SQLException
	{
		String sql_upd = " UPDATE cat_order_cdept SET status = ? ,readtime=sysdate WHERE  sheetid = ? AND status = ? ";
		PreparedStatement pstmt = conn.prepareStatement( sql_upd );
		pstmt.setInt( 1, status_new );
		pstmt.setString(2, sheetid.trim() );
		pstmt.setInt( 3, status_old );
		
		int rows = pstmt.executeUpdate();
		pstmt.close();
		return rows;
	}
	private boolean checkSheetVender( String venderid, String sheetid ) throws SQLException
	{
		int wrong_sheets = 0;
		String sql_chk = " SELECT sheetid, venderid FROM purchase0_cdept "
			+ " WHERE sheetid=?";
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
	
	public void makeExcel(File file) throws SQLException, InvalidDataException, IOException{
		String[] title = { "审批单号", "门店编码","门店名称", "物流模式", "送货日期", "上传时间","要货地","商品编码","条形码","商品名称","规格","运输规格","订货数","订货箱数","进价","折扣率","备注" };
		String sql="SELECT p.refsheetid,p.shopid,s.shopname, " +
				" name4code(p0.logistics, 'logistics'),p.vdeliverdate,TO_CHAR(c.releasedate, 'YYYY-MM-DD HH24:MI:SS') releasedate, ss.shopname," +
						" i.goodsid, g.barcode, g.goodsname, g.spec, 'Unit='||i.pkgvolume||g.unitname, " +
						" i.qty||g.unitname, round(i.qty / i.pkgvolume,2),i.concost,i.firstdisc||'%',i.memo " +
						" FROM purchaseitem_cdept i " +
						" JOIN purchase_cdept p ON (p.sheetid=i.sheetid and p.refsheetid=?) " +
						" left join purchase0_cdept p0 ON (p0.sheetid=p.refsheetid) " +
						" left join cat_order_cdept c ON ( c.sheetid=p.sheetid )  " +
						" JOIN goods_cdept g ON (i.goodsid=g.goodsid) " +
						" JOIN shop s ON (s.shopid=p.shopid) " +
						" JOIN shop ss ON (ss.shopid=p.destshopid) " +
						" order by p.shopid,g.deptid,i.goodsid ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();
		SimpleExcelAdapter excel = new SimpleExcelAdapter( );
		excel.cookExcelFile( file, rs, title, "订货审批单明细" );
		rs.close();
		pstmt.close();
	}
	
	final private static String	title		= "订货审批单";
	final private String		sheetid;
	final private Connection	conn;
	int							isyancao	= 0;		// 判断是否烟草,1代表启用烟草，前台根据1
														// 将打印烟草logo和抬头
}
