package com.royalstone.vss.liquidation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.jdom.Element;

import com.royalstone.common.Sheetid;
import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.DbAdm;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.XErr;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于实现VSS中对帐申请单的后台处理. 包括:检查对帐数据有效性, 生成对帐申请单. 数据有效性检查包括以下方面: 1) 基本格式检查,
 * 金额是否有效数字. 2) 单据号检查, 系统中是否存在此单据. 3) 单据状态检查, 是否待结算单据, 是否在同一天内已经对过帐. 4)
 * 单据是否属于指定的供应商, 帐套. 5) 增加课类检查。预付款供应商对账前检查验收单是否为同一课类。
 * 
 * @author meng
 * @modification baijian
 */
public class LiquidationManager {
	public LiquidationManager(Connection conn, Token token, String venderid, String bookno) {
		this.conn = conn;
		this.token = token;
		this.venderid = venderid;
		this.bookno = bookno;
	}

	/**
	 * yzn 07/05/13 针对预付款供应商增加课类检测
	 * 
	 * @param input_majorid
	 * @param elm_data
	 * @return
	 * @throws SQLException
	 */
	public boolean JudgeMajor(String input_majorid, Element elm_data) throws SQLException {
		boolean result = true;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String previous_majorid = null;
		List list = elm_data.getChildren("row");

		String sql_select = " SELECT majorid FROM unpaidsheet0 "
				+ " WHERE venderid=? AND bookno=? AND sheetid=? AND sheettype=?";
		pstmt = conn.prepareStatement(sql_select);
		for (int k = 0; k < list.size(); k++) {
			String sheet_majorid = null;
			Element body = (Element) list.get(k);
			String sheetid = body.getAttributeValue("noteno");
			int sheettype = 2301;// VSS只对验收单
			pstmt.setString(1, venderid);
			pstmt.setString(2, bookno);
			pstmt.setString(3, sheetid);
			pstmt.setInt(4, sheettype);
			rs = pstmt.executeQuery();
			if (rs.next()) sheet_majorid = rs.getString(1);
			if (previous_majorid == null) previous_majorid = sheet_majorid;
			if (!input_majorid.equals(sheet_majorid) || !sheet_majorid.equals(previous_majorid)) {
				result = false;
				break;
			}
		}
		rs.close();
		pstmt.close();
		return result;
	}

	/**
	 * 此方法用于检查前台上传的数据是否有效. 1) 基本格式检查, 金额是否有效数字. 2) 单据号检查, 系统中是否存在此单据. 3) 单据状态检查,
	 * 是否待结算单据, 是否在同一天内已经对过帐. 3) 单据是否属于指定的供应商, 帐套.
	 * 
	 * @param elm_data
	 *            前台传入的数据
	 * @return
	 * @throws Exception
	 */
	public Element validate(Element elm_data) throws Exception {

		List lst = elm_data.getChildren("row");
		if (lst.size() == 0) { throw new InvalidDataException("没有获得有效的数据，请重新上传"); }
		/**
		 * 检查金额格式. 如果格式有误,就不再作进一步的处理.
		 */
		Element elm_ret = checkValue(elm_data);
		if (this.rows_err != 0) return elm_ret;

		/**
		 * 建立临时工作表. 向临时表 tmp00 内添加记录. 分析数据. 生成临时表tmp_lq 的记录; 生成临时表tmp_chk 的记录;
		 * 检查临时表tmp_chk 的数据,填写message. 根据临时表tmp_chk 生成给前台的XML元素.
		 */

		conn.setAutoCommit(false);
		createTempTable();
		insertTempTable(elm_data);
		analyseData();
		elm_ret = getCheckResult();
		// dropTempTable();
		conn.commit();
		conn.setAutoCommit(true);
		return elm_ret;
	}

	public XErr xerr() {
		XErr xerr;
		if (this.rows_err == 0) {
			xerr = new XErr(0, "OK");
		} else {
			xerr = new XErr(-1, "上传数据存在错误,请更正后重新上传.");
		}
		return xerr;
	}

	/**
	 * 准备临时表
	 * 
	 * @throws Exception
	 */
	private void createTempTable() throws SQLException {
		String sql00 = " create global temporary table " + tmp00 + " ( " + " seqno               int, "
				+ " noteno              varchar(20), " + " notevalue   number(12,2), "
				+ " PRIMARY KEY ( seqno ) " + " ) on commit delete rows ";

		String sql_id = " create global temporary table " + tmp_id
				+ "  ( noteno varchar(20) ) on commit delete rows ";
		String sql_dup = " create global temporary table " + tmp_dup
				+ " ( noteno varchar(20) ) on commit delete rows ";

		String sql_lq = " create global temporary table " + tmp_lq + " ( "
				+ " noteno              varchar(20), " + " lqsheetid   varchar(20) "
				+ " ) on commit delete rows ";

		String sql_pool = " create global temporary table " + tmp_pool + " ( " + " noteno      varchar(20), "
				+ " bookno      varchar(4), " + " bookname    varchar(100), " + " venderid    varchar(10), "
				+ " payflag     INT, " + " docdate     DATE, " + " payflagnote varchar( 255 ) "
				+ " ) on commit delete rows ";

		String sql_chk = " create global temporary table " + tmp_chk + " ( " + " seqno               INT, "
				+ " noteno              varchar(30), " + " notevalue   number(12,2), "
				+ " bookno              varchar(4), " + " bookname    varchar(100), "
				+ " venderid    varchar(10), " + " payflag             INT, " + " lqsheetid   varchar(30), "
				+ " dupno               varchar(30), " + " message             varchar(255), "
				+ " docdate             DATE," + " payflagnote varchar(255 ) " + " ) on commit delete rows ";

		// System.out.println(sql00);
		// System.out.println(sql_id);
		// System.out.println(sql_dup);
		// System.out.println(sql_lq);
		// System.out.println(sql_pool);
		// System.out.println(sql_chk);

		DbAdm.createTempTable(conn, sql00);
		DbAdm.createTempTable(conn, sql_id);
		DbAdm.createTempTable(conn, sql_dup);
		DbAdm.createTempTable(conn, sql_lq);
		DbAdm.createTempTable(conn, sql_pool);
		DbAdm.createTempTable(conn, sql_chk);
	}

	/**
	 * 把前台传入的数据插入临时表中. 金额可以使用财务格式(千分位以逗号分隔).
	 * 
	 * @param elm_data
	 * @throws SQLException
	 * @throws InvalidDataException
	 * 
	 */
	private void insertTempTable(Element elm_data) throws SQLException, InvalidDataException {
		String sql_ins = " INSERT INTO " + tmp00 + " ( seqno,noteno, notevalue ) VALUES ( ?, ?, ? )";
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(sql_ins);

		List lst = elm_data.getChildren("row");
		for (int i = 0; i < lst.size(); i++) {
			Element record = (Element) lst.get(i);
			String noteno = record.getAttributeValue("noteno");
			String notevalue = record.getAttributeValue("notevalue");
			notevalue = (notevalue == null) ? "" : notevalue.trim();
			pstmt.setInt(1, i);
			pstmt.setString(2, noteno);
			double value = ValueAdapter.parseDouble(notevalue);
			pstmt.setDouble(3, value);
			pstmt.execute();
		}
		pstmt.close();
	}

	/**
	 * 分析前台传入的数据.
	 * 
	 * @throws Exception
	 */
	private void analyseData() throws SQLException {
		String sql_id = " INSERT INTO " + tmp_id + " SELECT noteno FROM " + tmp00 + " GROUP BY noteno ";

		String sql_dup = " INSERT INTO " + tmp_dup + " SELECT noteno FROM " + tmp00
				+ " GROUP BY noteno HAVING COUNT(*)>1 ";

		String sql_pool00 = " INSERT INTO "
				+ tmp_pool
				+ " ( "
				+ " noteno, bookno, bookname, venderid, payflag, docdate, payflagnote "
				+ " ) SELECT u.sheetid, u.bookno, b.bookname, u.venderid, u.payflag, u.docdate, nvl(u.payflagnote,' ') "
				+ " FROM unpaidsheet0 u " + " JOIN " + tmp_id
				+ " t ON (t.noteno=u.sheetid AND u.sheettype=2301) "
				+ " left join unpaidsheet u2 on (u.sheetid=u2.sheetid and u.sheettype=u2.sheettype ) "
				+ " JOIN book b ON (b.bookno=u.bookno) where u2.sheetid is null ";

		String sql_pool01 = " INSERT INTO "
				+ tmp_pool
				+ " ( "
				+ " noteno, bookno, bookname, venderid, payflag, docdate, payflagnote "
				+ " ) SELECT u.sheetid, u.bookno, b.bookname, u.venderid, u.payflag, u.docdate, nvl(u.payflagnote,' ') "
				+ " FROM unpaidsheet u " + " JOIN " + tmp_id
				+ " t ON (t.noteno=u.sheetid AND u.sheettype=2301) " + " JOIN book b ON (b.bookno=u.bookno) ";

		String sql_lq = "" + " INSERT INTO " + tmp_lq + " ( noteno, lqsheetid ) "
				+ " SELECT i.noteno, lq.sheetid FROM " + tmp_id + " t  "
				+ " JOIN liquidationitem i ON ( i.noteno = t.noteno ) "
				+ " JOIN liquidation lq ON ( lq.sheetid = i.sheetid ) "
				+ " WHERE trunc(lq.editdate) = trunc(sysdate)  ";

		String sql_chk = " INSERT INTO "
				+ tmp_chk
				+ " ( "
				+ " seqno, noteno, notevalue, bookno, bookname, venderid, payflag, lqsheetid, dupno, docdate, payflagnote "
				+ " ) SELECT t.seqno, t.noteno, t.notevalue, u.bookno, u.bookname, u.venderid, u.payflag, q.lqsheetid, d.noteno, u.docdate, u.payflagnote "
				+ " FROM " + tmp00 + " t LEFT OUTER JOIN " + tmp_pool + " u ON (  u.noteno=t.noteno ) "
				+ " LEFT OUTER JOIN " + tmp_lq + " q ON ( q.noteno = t.noteno ) " + " LEFT OUTER JOIN "
				+ tmp_dup + " d ON (d.noteno=t.noteno) ";

		String upd_all = " UPDATE " + tmp_chk + " SET message = 'OK' ";
		String upd_01 = " UPDATE " + tmp_chk + " SET message = '单据在同一天内不可以多次对帐' WHERE lqsheetid IS NOT NULL ";
		String upd_02 = " UPDATE " + tmp_chk + " SET message = '对帐前请选择以下分公司:' || bookname WHERE bookno <> '"
				+ this.bookno + "' ";
		String upd_03 = " UPDATE " + tmp_chk + " SET message = '单据号错误' WHERE venderid <> '" + this.venderid
				+ "' ";
		String upd_04 = " UPDATE " + tmp_chk + " SET message = '此单据可能已经生成付款单' WHERE payflag > 1 ";
		String upd_05 = " UPDATE " + tmp_chk + " SET message = '此单据已结算' WHERE payflag = 4 ";
		String upd_06 = " UPDATE " + tmp_chk + " SET message = '合同已经过期，请尽快与采购员联系' WHERE payflag = -1 ";
		String upd_07 = " UPDATE " + tmp_chk + " SET message = '该单据暂不能结算，请与采购联系' WHERE payflag = 0 ";
		String upd_08 = " UPDATE " + tmp_chk + " SET message = '结算池内无此单据' WHERE bookno IS NULL ";
		String upd_09 = " UPDATE " + tmp_chk + " SET message = '单据号重复' WHERE dupno IS NOT NULL ";

		Statement st = conn.createStatement();
		try {
			// System.out.println(sql_id);
			// System.out.println(sql_dup);
			// System.out.println(sql_pool00);
			// System.out.println(sql_pool01);
			// System.out.println(sql_lq);
			// System.out.println(sql_chk);

			st.executeUpdate(sql_id);
			st.executeUpdate(sql_dup);
			st.executeUpdate(sql_pool00);
			st.executeUpdate(sql_pool01);
			st.executeUpdate(sql_lq);
			st.executeUpdate(sql_chk);

			st.executeUpdate(SqlUtil.toLocal(upd_all));

			this.rows_err = 0;
			this.rows_err += st.executeUpdate(SqlUtil.toLocal(upd_01));
			this.rows_err += st.executeUpdate(SqlUtil.toLocal(upd_02));
			this.rows_err += st.executeUpdate(SqlUtil.toLocal(upd_03));
			this.rows_err += st.executeUpdate(SqlUtil.toLocal(upd_04));
			this.rows_err += st.executeUpdate(SqlUtil.toLocal(upd_05));
			this.rows_err += st.executeUpdate(SqlUtil.toLocal(upd_06));
			this.rows_err += st.executeUpdate(SqlUtil.toLocal(upd_07));
			this.rows_err += st.executeUpdate(SqlUtil.toLocal(upd_08));
			this.rows_err += st.executeUpdate(SqlUtil.toLocal(upd_09));
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			st.close();
		}
	}

	/**
	 * 把临时表 tmp_chk 的数据转换成XML
	 * 
	 * @return
	 * @throws SQLException
	 */
	private Element getCheckResult() throws SQLException {
		String sql_sel = " SELECT seqno, noteno, notevalue, bookno, venderid, payflag, message, docdate "
				+ " FROM " + tmp_chk + " ORDER BY seqno ";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql_sel);
		Element elm_list = new Element("list");

		this.rows_err = 0;
		while (rs.next()) {
			Element elm_row = new Element("row");

			Element elm_seqno = new Element("seqno");
			Element elm_noteno = new Element("noteno");
			Element elm_notevalue = new Element("notevalue");
			Element elm_bookno = new Element("bookno");
			Element elm_venderid = new Element("venderid");
			Element elm_message = new Element("message");
			Element elm_docdate = new Element("docdate");

			String seqno = rs.getString("seqno");
			String venderid = rs.getString("venderid");
			String bookno = rs.getString("bookno");
			String noteno = rs.getString("noteno");
			String notevalue = rs.getString("notevalue");
			String docdate = rs.getString("docdate");

			seqno = (seqno == null) ? "" : seqno.trim();
			venderid = (venderid == null) ? "" : venderid.trim();
			bookno = (bookno == null) ? "" : bookno.trim();
			noteno = (noteno == null) ? "" : noteno.trim();
			notevalue = (notevalue == null) ? "0" : notevalue.trim();
			docdate = (docdate == null) ? "0" : docdate.trim();

			elm_seqno.setText(seqno);
			elm_noteno.setText(noteno);
			elm_notevalue.setText(notevalue);
			elm_bookno.setText(bookno);
			elm_venderid.setText(venderid);
			elm_docdate.setText(docdate);

			String str_msg = rs.getString("message");
			str_msg = (str_msg == null) ? "" : SqlUtil.fromLocal(str_msg).trim();

			elm_message.setText(str_msg);
			if (str_msg.equalsIgnoreCase("OK")) {
				elm_message.setAttribute("class", "ok");
			} else {
				rows_err++;
				elm_message.setAttribute("class", "warning");
			}

			elm_row.addContent(elm_seqno);
			elm_row.addContent(elm_noteno);
			elm_row.addContent(elm_notevalue);
			elm_row.addContent(elm_bookno);
			elm_row.addContent(elm_venderid);
			elm_row.addContent(elm_docdate);
			elm_row.addContent(elm_message);
			elm_list.addContent(elm_row);
		}

		rs.close();
		st.close();
		return elm_list;
	}

	private Element checkValue(Element elm_data) {
		Element elm_list = new Element("list");

		List lst = elm_data.getChildren("row");
		this.rows_err = 0;
		for (int i = 0; i < lst.size(); i++) {
			Element record = (Element) lst.get(i);
			String noteno = record.getAttributeValue("noteno");
			String notevalue = record.getAttributeValue("notevalue");
			notevalue = (notevalue == null) ? "" : notevalue.trim();
			Element elm_row = new Element("row");

			Element elm_seqno = new Element("seqno");
			Element elm_noteno = new Element("noteno");
			Element elm_notevalue = new Element("notevalue");
			Element elm_message = new Element("message");

			elm_seqno.setText("" + i);
			elm_noteno.setText(noteno);
			try {
				double value = ValueAdapter.parseDouble(notevalue);
				elm_message.setText("OK");
				elm_notevalue.setText("" + value);
			}
			catch (Exception e) {
				this.rows_err++;
				elm_notevalue.setText(notevalue);
				elm_notevalue.setAttribute("class", "warning");
				elm_message.setText("金额格式有误：" + notevalue);
				elm_message.setAttribute("class", "warning");
			}
			elm_row.addContent(elm_seqno);
			elm_row.addContent(elm_noteno);
			elm_row.addContent(elm_notevalue);
			elm_row.addContent(elm_message);
			elm_list.addContent(elm_row);
		}
		return elm_list;
	}

	/**
	 * 将临时表删除
	 * 
	 * @throws Exception
	 * 
	 */
	private void dropTempTable() {
		dropTable(tmp00);
		dropTable(tmp_id);
		dropTable(tmp_dup);
		dropTable(tmp_pool);
		dropTable(tmp_lq);
		dropTable(tmp_chk);
	}

	private void dropTable(String tabname) {
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(" DROP TABLE " + tabname);
			stmt.close();
		}
		catch (SQLException e) {}
	}

	/**
	 * 此方法用于生成新的对帐申请单. 先检查数据的有效性,再保存单据.
	 * 
	 * @param elm_data
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public String makeSheet(String majorid, Element elm_data) throws SQLException, InvalidDataException {
		if (majorid == null || majorid.length() == 0) majorid = null;
		String sheetid = "";
		try {
			sheetid = Sheetid.getSheetid(conn, 7001, this.bookno);

			conn.setAutoCommit(false);
			createTempTable();
			insertTempTable(elm_data);
			analyseData();

			if (this.rows_err != 0) throw new InvalidDataException("上传数据存在错误, 请检查.");

			insertHead(majorid, sheetid);
			insertBody(sheetid);

			// 发DXS传单请求
			int sid = token.site.getSid();
			if (sid == 3 || sid == 4) {
				String sql_sp = " call TL_FAS_Liquidation( ? ) ";
				PreparedStatement pstmt_sp = conn.prepareStatement(sql_sp);
				pstmt_sp.setString(1, sheetid);
				pstmt_sp.execute();
				pstmt_sp.close();
			}

			conn.commit();
		}
		catch (SQLException e) {
			conn.rollback();
			throw e;
		}
		finally {
			conn.setAutoCommit(true);
			// dropTempTable();
		}
		return sheetid;
	}

	/**
	 * 插入对帐申请单表头信息
	 * 
	 * @param sheetid
	 * @throws SQLException
	 *             modify:yzn 07/05/14 增加课类号majorid
	 */
	private void insertHead(String majorid, String sheetid) throws SQLException {
		String sql_ins = " INSERT INTO liquidation ( sheetid, venderid, bookno, note, editor, majorid ) "
				+ " VALUES ( ?, ?, ?, ?, ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql_ins);
		pstmt.setString(1, sheetid);
		pstmt.setString(2, this.venderid);
		pstmt.setString(3, this.bookno);
		pstmt.setString(4, "");
		pstmt.setString(5, this.token.getBusinessid());
		pstmt.setString(6, majorid);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * 插入数据到对帐申请单明细
	 * 
	 * @param sheetid
	 * @throws SQLException
	 */
	private void insertBody(String sheetid) throws SQLException {
		String sql_ins = " INSERT INTO liquidationitem ( sheetid, seqno, noteno, notetype, notevalue ) "
				+ " SELECT " + ValueAdapter.toString4String(sheetid)
				+ ", seqno, noteno, 2301, notevalue FROM " + tmp00 + " ";
		PreparedStatement pstmt = conn.prepareStatement(sql_ins);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * 查询供应商对帐申请是否在冻结帐套内
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean hasVenderFreeze() throws SQLException {
		boolean rel = false;
		String sql = "select payflag from venderpaystatus where venderid=? and bookno=? and payflag<>0";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, venderid);
		pstmt.setString(2, bookno);
		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) rel = true;

		rs.close();
		pstmt.close();

		return rel;
	}

	final private Connection	conn;
	private Token				token;
	private String				venderid;
	private String				bookno;
	private int					rows_err	= 0;

	final private String		tmp00		= "temp_LiquidationManager1";
	final private String		tmp_id		= "temp_LiquidationManager2";
	final private String		tmp_dup		= "temp_LiquidationManager3";
	final private String		tmp_lq		= "temp_LiquidationManager4";
	final private String		tmp_pool	= "temp_LiquidationManager5";
	final private String		tmp_chk		= "temp_LiquidationManager6";
}
