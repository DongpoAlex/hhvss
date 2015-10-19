package com.royalstone.vss.edi;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.royalstone.certificate.util.FileHandle;
import com.royalstone.common.Sheetid;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.DbAdm;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.sql.SqlUtil;

public class Checking implements IEdiSheet {
	final private String		importFolder;
	final private String		importFixName;
	final private String		exportFolder;
	final private String		exportFixName;
	final private String		venderid;
	final private String name; 
	final private Connection	conn;

	public Checking(Item item, String venderid, Connection conn) {
		super();
		name = Thread.currentThread().getName(); //线程名称，和区域名称一致。
		this.importFolder = item.importFolder;
		this.importFixName = name+"_"+item.importFixName; //对账申请文件前缀按区域而不同
		this.exportFolder = item.exportFolder;
		this.exportFixName = name+"_"+item.exportFixName;
		this.venderid = venderid;
		this.conn = conn;
	}

	public int work() {
		Log.event("EDI."+name+"."+importFixName+" vender:" + this.venderid, "START");

		String[] files = scanFile();
		int count = 0;
		for (int i = 0; i < files.length; i++) {
			Hashtable table = cookFile(files[i]);
			if(table == null){
				continue;
			}
			try {
				Log.event("EDI","start taskid:"+table.get("askid"));
				String sheetid = liquidation(table);
				Log.event("EDI."+name+"."+importFixName+ " vender:" + this.venderid, "EDI.LQ: " + sheetid);
				count++;
			}
			catch (Exception e) {
				e.printStackTrace();
				Log.event("EDI " ,"ERR:"+e.getMessage());
			}
		}

		Log.event("EDI."+name+"."+importFixName+ " vender:" + this.venderid, "EDI.import END: " + count + " sheets");
		return 0;
	}

	private boolean bak(String fileName){
		boolean res;
		String strOldFilePath=this.importFolder + "/" + fileName;
		String strNewFilePath=this.importFolder + "/bak/"+fileName;
		res = FileHandle.createFolder(this.importFolder + "/bak/");
		if(!res) return res;
		res = FileHandle.copyFile(strOldFilePath, strNewFilePath);
		if(!res) return res;
		res = FileHandle.delFile(strOldFilePath);
		return res;
	}
	private String[] scanFile() {
		ArrayList<String> list = new ArrayList();
		String[] arr_tmp = FileHandle.getAllFile(this.importFolder);

		for (int i = 0; (arr_tmp != null && i < arr_tmp.length); i++) {
			if (arr_tmp[i].indexOf(importFixName) != -1) {
				list.add(arr_tmp[i]);
			}
		}

		Object[] ss = list.toArray();
		String[] rels = new String[ss.length];
		for (int i = 0; i < ss.length; i++) {
			rels[i] = (String) ss[i];
		}
		return rels;
	}

	private Hashtable cookFile(String fileName) {
		Hashtable table = new Hashtable();
		String filePatch = this.importFolder + "/" + fileName;
		String[] ss = FileHandle.fileToArrString(filePatch, "GBK");
		if (ss == null || ss.length < 2) {
			throw new InvalidDataException("文件" + fileName + "无内容");
		} else {
			String[] st = ss[0].split(";");
			if (st.length < 3) { throw new InvalidDataException("文件" + fileName + "首行格式错误"); }
			String askid = st[0];
			String date = st[1];
			String bookno = st[2];
			String vender = st[3];
			if(!this.venderid.equals(vender)){
				return null;
			}
			String[][] items = new String[ss.length - 1][2];
			for (int i = 1; i < ss.length; i++) {
				st = ss[i].split(";");
				if (st.length < 2) { throw new InvalidDataException("文件" + fileName + "明细格式错误"); }
				;
				items[i - 1][0] = st[0];
				items[i - 1][1] = st[1];
			}
			table.put("askid", askid);
			table.put("date", date);
			table.put("bookno", bookno);
			table.put("items", items);
			table.put("filename", fileName);
		}
		return table;
	}

	/**
	 * 返回对账单号
	 * 
	 * @param table
	 * @throws Exception
	 */
	private String liquidation(Hashtable table) throws Exception {
		String askid = (String) table.get("askid");
		String date = (String) table.get("date");
		String bookno = (String) table.get("bookno");
		String filename = (String) table.get("filename");
		String[][] items = (String[][]) table.get("items");
		String sheetid;
		try {
			conn.setAutoCommit(false);
			Element elm_data = validate(bookno, items);
			
			sheetid = makeSheet(bookno, date, elm_data);

			DZJG(sheetid, askid,date,bookno);
			
			//移动文件到bak目录
			if(!bak(filename)){
				throw new IOException("File move Err");
			}
			
			conn.commit();
		}
		catch (Exception e) {
			conn.rollback();
			throw e;
		}
		return sheetid;
	}

	/**
	 * 此方法用于检查前台上传的数据是否有效. 1) 基本格式检查, 金额是否有效数字. 2) 单据号检查, 系统中是否存在此单据. 3) 单据状态检查,
	 * 是否待结算单据, 是否在同一天内已经对过帐. 3) 单据是否属于指定的供应商, 帐套.
	 * 
	 * @param elm_data
	 *            前台传入的数据
	 * @return
	 * @return
	 * @throws Exception
	 */
	public Element validate(String bookno, String[][] items) throws Exception {

		/**
		 * 建立临时工作表. 向临时表 tmp00 内添加记录. 分析数据. 生成临时表tmp_lq 的记录; 生成临时表tmp_chk 的记录;
		 * 检查临时表tmp_chk 的数据,填写message. 根据临时表tmp_chk 生成给前台的XML元素.
		 */

		createTempTable();
		insertTempTable(items);
		analyseData(bookno);
		Element elm_ret = getCheckResult();
		return elm_ret;
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

		DbAdm.createTempTable(conn, sql00);
		DbAdm.createTempTable(conn, sql_id);
		DbAdm.createTempTable(conn, sql_dup);
		DbAdm.createTempTable(conn, sql_lq);
		DbAdm.createTempTable(conn, sql_pool);
		DbAdm.createTempTable(conn, sql_chk);
	}

	private void insertTempTable(String[][] items) throws SQLException, InvalidDataException {
		String sql_ins = " INSERT INTO " + tmp00 + " ( seqno,noteno, notevalue ) VALUES ( ?, ?, ? )";
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(sql_ins);

		for (int i = 0; i < items.length; i++) {
			String noteno = items[i][0];
			String notevalue = items[i][1];
			notevalue = (notevalue == null) ? "" : notevalue.trim();
			pstmt.setInt(1, i);
			pstmt.setString(2, noteno);
			double value = ValueAdapter.parseDouble(notevalue);
			pstmt.setDouble(3, value);
			pstmt.execute();
		}
		pstmt.close();
	}

	private void analyseData(String bookno) throws SQLException {
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
				+ bookno + "' ";
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
			st.executeUpdate(sql_id);
			st.executeUpdate(sql_dup);
			st.executeUpdate(sql_pool00);
			st.executeUpdate(sql_pool01);
			st.executeUpdate(sql_lq);
			st.executeUpdate(sql_chk);

			st.executeUpdate(SqlUtil.toLocal(upd_all));

			st.executeUpdate(SqlUtil.toLocal(upd_01));
			st.executeUpdate(SqlUtil.toLocal(upd_02));
			st.executeUpdate(SqlUtil.toLocal(upd_03));
			st.executeUpdate(SqlUtil.toLocal(upd_04));
			st.executeUpdate(SqlUtil.toLocal(upd_05));
			st.executeUpdate(SqlUtil.toLocal(upd_06));
			st.executeUpdate(SqlUtil.toLocal(upd_07));
			st.executeUpdate(SqlUtil.toLocal(upd_08));
			st.executeUpdate(SqlUtil.toLocal(upd_09));
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			st.close();
		}
	}

	private Element getCheckResult() throws SQLException {
		String sql_sel = " SELECT seqno, noteno, notevalue, bookno, venderid, payflag, message, docdate "
				+ " FROM " + tmp_chk + " ORDER BY seqno ";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql_sel);
		Element elm_list = new Element("list");

		while (rs.next()) {
			String str_msg = rs.getString("message");
			str_msg = (str_msg == null) ? "" : SqlUtil.fromLocal(str_msg).trim();
			
			if(!str_msg.equalsIgnoreCase("OK")){
				continue;
			}
			
			
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

			

			elm_message.setText(str_msg);

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

	public String makeSheet(String bookno, String date, Element elm_data) throws SQLException,
			InvalidDataException {
		if(elm_data.getChildren("row").size()==0){
			return "";
		}
		
		String sheetid = "";
		sheetid = Sheetid.getSheetid(conn, 7001, bookno);
		insertHead(sheetid, bookno, date);
		insertBody(sheetid, elm_data);

		return sheetid;
	}

	/**
	 * 插入对帐申请单表头信息
	 * 
	 * @param sheetid
	 * @throws SQLException
	 *             modify:yzn 07/05/14 增加课类号majorid
	 */
	private void insertHead(String sheetid, String bookno, String date) throws SQLException {
		String sql_ins = " INSERT INTO liquidation ( sheetid, venderid, bookno, note, editor, majorid ,editdate) "
				+ " VALUES ( ?, ?, ?, ?, ?, ?,to_date('" + date + "','YYYY-MM-DD') ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql_ins);
		pstmt.setString(1, sheetid);
		pstmt.setString(2, this.venderid);
		pstmt.setString(3, bookno);
		pstmt.setString(4, "EDI 对账");
		pstmt.setString(5, this.venderid);
		pstmt.setNull(6, java.sql.Types.INTEGER);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * 插入数据到对帐申请单明细
	 * 
	 * @param sheetid
	 * @throws SQLException
	 */
	private void insertBody(String sheetid, Element elm) throws SQLException {
		String sql_ins = " INSERT INTO liquidationitem ( sheetid, seqno, noteno, notetype, notevalue ) "
				+ " values(?,?,?,2301,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql_ins);
		List list = elm.getChildren("row");
		int rows = 0;
		for (Iterator it = list.iterator(); it.hasNext();) {
			Element item = (Element) it.next();
			String noteno = item.getChildTextTrim("noteno");
			String notevalue = item.getChildTextTrim("notevalue");
			String message = item.getChildTextTrim("message");
			if ("OK".equals(message)) {
				++rows;
				pstmt.setString(1, sheetid);
				pstmt.setInt(2, rows);
				pstmt.setString(3, noteno);
				pstmt.setString(4, notevalue);
				pstmt.executeUpdate();
			}
		}
		pstmt.close();
	}

	private void DZJG(String sheetid, String askid, String date, String bookno) throws SQLException, IOException {
		// 创建文件
		String filePatch = this.exportFolder + "/" + this.exportFixName + "_" + askid + ".txt";
		FileHandle.createFolder(this.exportFolder);
		File file = new File(filePatch);
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}

		String out = askid+";"+date+";"+bookno+";"+this.venderid+";"+exportFixName;
		FileHandle.appendLineStringToFile(out , filePatch, "GBK");
		
		String sql = " select * from liquidationitem where sheetid='" + sheetid + "'";
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		while (rs.next()) {
			String notno = rs.getString("noteno");
			out = notno + ";" + sheetid;
			FileHandle.appendLineStringToFile(out, filePatch, "GBK");
		}
		SqlUtil.close(rs);
	}

	final private String	tmp00		= "temp_LiquidationManager1";
	final private String	tmp_id		= "temp_LiquidationManager2";
	final private String	tmp_dup		= "temp_LiquidationManager3";
	final private String	tmp_lq		= "temp_LiquidationManager4";
	final private String	tmp_pool	= "temp_LiquidationManager5";
	final private String	tmp_chk		= "temp_LiquidationManager6";
}
