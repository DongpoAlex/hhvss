package com.royalstone.vss.detail;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.SimpleExcelAdapter;

public class DownloadOrder {

	/**
	 * Constructor
	 * 
	 * @param conn
	 *            数据库连接
	 * @param sheetid
	 *            单据号
	 * @throws SQLException
	 * @throws InvalidDataException
	 */

	public DownloadOrder(Connection conn, String sheetid,String month) throws SQLException, InvalidDataException {
		this.conn = conn;
		this.sheetid = sheetid;
		this.month = month;
	}

	public void makeExcel(File file) throws SQLException, InvalidDataException, IOException{
		String[] title = { "审批单号", "门店编码","门店名称", "物流模式", "送货日期", "上传时间","要货地","商品编码","条形码","商品名称","规格","运输规格","订货数","订货箱数","进价","折扣率","备注" };
		String sql="SELECT p.refsheetid,p.shopid,s.shopname, " +
				" name4code(p0.logistics, 'logistics'),p.vdeliverdate,TO_CHAR(c.releasedate, 'YYYY-MM-DD HH24:MI:SS') releasedate, ss.shopname," +
						" i.goodsid, g.barcode, g.goodsname, g.spec, 'Unit='||i.pkgvolume||g.unitname, " +
						" i.qty||g.unitname, round(i.qty / i.pkgvolume,2),i.concost,i.firstdisc||'%',i.memo " +
						" FROM purchaseitem"+this.month+" i " +
						" JOIN purchase"+this.month+" p ON (p.sheetid=i.sheetid and p.refsheetid=?) " +
						" left join purchase0"+this.month+" p0 ON (p0.sheetid=p.refsheetid) " +
						" left join cat_order"+this.month+" c ON ( c.sheetid=p.sheetid )  " +
						" JOIN goods g ON (i.goodsid=g.goodsid) " +
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

	
	/**
	 * 此方法用于查询指定付款单的供应商编码, 主要用于访问权限检查. 如果在数据库中没有找到指定的单据号,则返回长度为零的字串.
	 * 
	 * @param sheetid
	 *            付款单单据号
	 * @return 供应商编码
	 * @throws SQLException
	 */
	public String getSheetVenderid( String sheetid ) throws SQLException {
		String rel = "";
		String sql = " SELECT venderid FROM purchase0"+month+" WHERE sheetid=? ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			rel = rs.getString(1);

		return rel;
	}

	final private Connection	conn;

	private String				sheetid;
	
	private String 				month;

}
