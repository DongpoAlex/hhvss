/*
 * Created on 2007-02-02
 *
 */
package com.royalstone.vss.detail;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.excel.Workbook;

public class ShowStkCostAdj {

	public ShowStkCostAdj( Connection conn, String sheetid ) throws SQLException, InvalidDataException
	{
		if( conn == null ) throw new InvalidDataException( "conn is null." );
		if( sheetid == null || sheetid.length() == 0 ) throw new InvalidDataException( "sheetid is invalid." );
		this.conn = conn;
		this.sheetid = sheetid;

	}

	/**
	 * 此模块从数据库取库存进价调整单的表头信息.
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	private Element getHead() throws SQLException, InvalidDataException
	{
		String sql_head =  " SELECT "
				+ " s.sheetid, sh.shopname, s.finalshopid,ss.shopname finalshopname,"
				+ " s.venderid, v.vendername, pay.paytypeid,pay.paytypename , s.TotalAmt, "
				+ " (s.TotalAmt+s.TotalTaxAmt17+s.TotalTaxAmt13) as totaltaxamt,  "
				+ " s.TotalAmt17, s.TotalTaxAmt17, s.TotalTaxAmt13, "
				+ " s.editor, s.editdate, s.checker, s.checkdate, s.note "
				+ " FROM stkcostadj s"
				+ " JOIN vender v ON ( v.venderid = s.venderid ) "
				+ " JOIN paytype pay ON ( pay.paytypeid = s.paytypeid ) "
				+ " JOIN shop sh ON ( sh.shopid = s.shopid ) "
				+ " LEFT JOIN shop ss ON ( ss.shopid = s.finalshopid ) "
				+ " WHERE s.sheetid = ? ";

		PreparedStatement pstmt = conn.prepareStatement( sql_head );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();

		XResultAdapter adapter = new XResultAdapter( rs );

		Element elm_head = adapter.getRowSetElement( "head", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "Sheet not found: " + this.sheetid );
		elm_head.setAttribute( "rows", ""+rows );
		elm_head.setAttribute( "name", "stkcostadj" );
		elm_head.setAttribute( "title", title );
		rs.close();
		pstmt.close();
		return elm_head;
	}

	/**
	 * 此模块从数据库中取库存进价调整单明细信息.
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	private Element getDetail() throws SQLException, InvalidDataException
	{
		String sql_detail = "SELECT "
				+ " s.shopid, sh.shopname ,"
				+ " i.goodsid, i.DeptID,cat.categoryname,g.barcode, g.goodsname, i.qty, i.OldCost,"
				+ " i.NewCost, i.AdjAmt, i.CostTaxRate,i.SaleTaxRate "
				+ " FROM "
				+ " stkcostadjitem i"
				+ " JOIN stkcostadj s ON (s.sheetid=i.sheetid)"
				+ " left join category cat ON (cat.categoryid=i.deptid)"
				+ " JOIN goods g ON (i.goodsid=g.goodsid)"
				+ " JOIN shop sh ON (sh.shopid=s.shopid)"
				+ " WHERE i.sheetid= ? ";

		PreparedStatement pstmt = conn.prepareStatement( sql_detail );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );

		Element elm_body = adapter.getRowSetElement( "body", "row" );
		int rows = adapter.rows();
		if( rows == 0 ) throw new InvalidDataException( "This sheet does not have detail info: " + this.sheetid );
		elm_body.setAttribute( "rows", ""+rows );
		elm_body.setAttribute( "name", "stkcostadjitem" );
		rs.close();
		pstmt.close();
		return elm_body;
	}

	public Element toElement() throws SQLException, InvalidDataException {
		this.elm_sheet = new Element( "sheet" );
		this.elm_sheet.setAttribute( "name", "stkcostadj" );
		this.elm_sheet.setAttribute( "title", title );
		this.elm_sheet.setAttribute( "sheetid", sheetid );

		this.elm_sheet.addContent( this.getHead() );
		this.elm_sheet.addContent( this.getDetail() );
		return elm_sheet;
	}

	public int cookExcelFile(File file) throws SQLException, InvalidDataException, IOException{
		String sql_detail = "SELECT "
				+ " s.shopid, sh.shopname ,s.finalshopid,ss.shopname finalshopname,"
				+ " i.goodsid, i.DeptID,cat.categoryname,g.barcode, g.goodsname, i.qty, i.OldCost,"
				+ " i.NewCost, i.AdjAmt, i.CostTaxRate,i.SaleTaxRate "
				+ " FROM "
				+ " stkcostadjitem i"
				+ " JOIN stkcostadj s ON (s.sheetid=i.sheetid) "
				+ " JOIN goods g ON (i.goodsid=g.goodsid) "
				+ " JOIN shop sh ON (sh.shopid=s.shopid) "
				+ " left JOIN category cat ON (cat.categoryid=i.deptid) "
				+ " LEFT JOIN shop ss ON ( ss.shopid = s.finalshopid ) "
				+ " WHERE i.sheetid= ? ";

		String[] title = {"门店编码","门店","结算地编码","结算地","商品编码","品别编码","品类","条码","商品名","数量",
				"原成本单价","新成本单价","调整金额","进项税率","销项税率"};

		PreparedStatement pstmt = conn.prepareStatement( sql_detail );
		pstmt.setString( 1, this.sheetid );
		ResultSet rs = pstmt.executeQuery();

		SimpleExcelAdapter excel = new SimpleExcelAdapter( );
		int rows = excel.cookExcelFile( file, rs, title, "进价调整单" );

		rs.close();
		pstmt.close();

		return rows;
	}
	final private static String title = "库存进价调整单";
	private Element elm_sheet = null;
	final private String sheetid;
	final private Connection conn;
}
