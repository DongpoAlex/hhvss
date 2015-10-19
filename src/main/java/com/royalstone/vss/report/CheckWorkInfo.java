/**
 * 
 */
package com.royalstone.vss.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;
import com.royalstone.workbook.Workbook;

/**
 * @author BaiJian 考勤信息报表
 */
public class CheckWorkInfo {

	final private Connection conn;

	final String sql_sel = " select c.kh,c.kqh,c.bh,c.rq,to_char(c.sj,'HH24:MI:SS') sj,c.jh,c.xm,c.bc,c.ip,substr(c.xm,1,2) as shop from cx_yskq c ";

	final private String sql_count = " SELECT COUNT(*) FROM cx_yskq c ";

	final private String sql_order = " order by shop,c.kh,c.rq,c.sj ";

	/**
	 * @param conn
	 */
	public CheckWorkInfo(Connection conn) {
		super();
		this.conn = conn;
	}

	public Element getCheckWorkInfo( Map map ) throws InvalidDataException, SQLException, UnsupportedEncodingException {
		Element elm_data = null;

		Filter filter = cookFilter(map);
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();

		int count = getCount(sql_where);
		if (count > VSSConfig.getInstance().getRowsLimitHard())
			throw new InvalidDataException("满足条件的记录数已超过系统处理上限,请重新设置查询条件.");

		String sql = sql_sel + sql_where + sql_order;
		
		Log.debug(this.getClass().getName(), sql);
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		elm_data = adapter.getRowSetElement("report", "row", 1, VSSConfig.getInstance().getRowsLimitSoft());
		rs.close();
		pstmt.close();

		int rows = adapter.rows();
		elm_data.setAttribute("count", "" + count);
		elm_data.setAttribute("rows", "" + rows);
		elm_data.setAttribute("reportname", "checkwork");

		return elm_data;
	}

	public int getCount( String sql_where ) throws SQLException {
		int count = 0;

		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql_count + sql_where);
		if (rs.next())
			count = rs.getInt(1);
		rs.close();
		stmt.close();

		return count;
	}

	/**
	 * 这里的导出用了jxl，兼容excel2000格式。
	 * @param map
	 * @param os
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws RowsExceededException
	 * @throws WriteException
	 * @throws InvalidDataException
	 */
	public Workbook getWorkBook( Map map, OutputStream os ) throws IOException, SQLException, RowsExceededException, WriteException, InvalidDataException {
		Filter filter = cookFilter2(map);
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();
		int count = getCount(sql_where);
		if (count > VSSConfig.getInstance().getRowsLimitHard())
			throw new InvalidDataException("满足条件的记录数已超过系统处理上限"+VSSConfig.getInstance().getRowsLimitHard()+",请重新设置查询条件.");

		Workbook book = new Workbook(os);
		String sql_sel = " select c.kh,c.bh,to_char(c.rq,'YYYY-MM-DD') as rq,to_char(c.sj,'HH24:MI:SS') sj,c.xm,substr(c.xm,1,2) as shop from cx_yskq c ";
		String sql_sel_book = sql_sel + sql_where + sql_order;
		
		String[] title = { "考勤卡号", "人员编号","刷卡记录日期","刷卡记录时间","员工姓名","门店" };

		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql_sel_book);
		book.addSheet(rs, "第一页", title);
		rs.close();
		stmt.close();

		return book;
	}

	private Filter cookFilter( Map map ) throws InvalidDataException, SQLException, UnsupportedEncodingException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("kid");
		if (ss != null && ss.length > 0) {
			filter.add(" c.kh = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("startdate");
		if (ss != null && ss.length > 0) {
			filter.add(" c.rq >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("enddate");
		if (ss != null && ss.length > 0) {
			filter.add(" c.rq <= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("xm");
		if (ss != null && ss.length > 0) {
			String xm = ss[0].trim();
			System.out.println(xm);
			//xm = InfoVender.SqlUtil.toLocal( xm ); 
//			xm = "%" + xm + "%" ;
			filter.add(" c.xm = " + ValueAdapter.toString4String(xm));
		}
		
//		ss = (String[]) map.get("shopid");
//		if (ss != null && ss.length > 0) {
//			String shopname = "";
//			PreparedStatement pstmt = conn.prepareStatement(sql_shop);
//			pstmt.setString(1, ss[0]);
//			ResultSet rs = pstmt.executeQuery();
//			if (rs.next()) {
//				shopname = rs.getString(1);
//				if (shopname != null) {
//					shopname = shopname.substring(0,2);
//				}
//			}
//			rs.close();
//			pstmt.close();
//
//			filter.add(" c.xm like '" + shopname + "%' ");
//		}
		return filter;
	}
	
	private Filter cookFilter2( Map map ) throws InvalidDataException, SQLException, UnsupportedEncodingException {
		Filter filter = new Filter();
		String[] ss = null;

		ss = (String[]) map.get("kid");
		if (ss != null && ss.length > 0) {
			filter.add(" c.kh = " + Values.toString4String(ss[0]));
		}

		ss = (String[]) map.get("startdate");
		if (ss != null && ss.length > 0) {
			filter.add(" c.rq >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("enddate");
		if (ss != null && ss.length > 0) {
			filter.add(" c.rq <= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("xm");
		if (ss != null && ss.length > 0) {
			String xm = ss[0].trim();
			xm = SqlUtil.toLocal( xm ); 
//			xm = "%" + xm + "%" ;
			filter.add(" c.xm = " + ValueAdapter.toString4String(xm));
		}
		
		return filter;
	}
}
