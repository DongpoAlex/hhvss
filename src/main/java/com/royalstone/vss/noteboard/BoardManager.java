package com.royalstone.vss.noteboard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.royalstone.security.Token;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于实现功能: 公文附件下载.
 * 
 * @author meng
 */
public class BoardManager {
	public BoardManager(Token token) {
		this.token = token;
	}

	/**
	 * 保存公文标题
	 * 
	 * @throws InvalidDataException
	 */
	public int saveTitle(Connection conn, String title, String expiredate, String istop, String dept, String ismobile)
			throws SQLException, InvalidDataException {
		int noteid = getNoteid(conn);
		String sql = " INSERT INTO noteboard_title( noteid, title, editor, expiredate,istop,dept,ismobile ) VALUES (?,?,?,"
				+ ValueAdapter.std2mdy(expiredate) + ",?,?,?) ";
		String editor = token.username;
		SqlUtil.executePS(conn, sql, noteid,title,editor,istop,dept,ismobile);
		if (ismobile.equals("1")) {
			sendToMobile(conn, noteid);
		}

		return noteid;
	}

	private void sendToMobile(Connection conn, int noteid) throws SQLException {
		String sql = " call sendtomobile('41', 'noteid', " + noteid + ", '公文')";
		Statement st = conn.createStatement();
		st.execute(sql);
		st.close();
	}

	/**
	 * 保存公文附件
	 * 
	 * @param conn
	 * @param noteid
	 * @param map_file
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	public void saveNoteFile(Connection conn, int noteid, Map map_file) throws SQLException, InvalidDataException {
		Set entry_set = map_file.entrySet();
		if (entry_set == null)
			throw new InvalidDataException("set is null");

		Entry[] arr_entry = new Entry[entry_set.size()];
		arr_entry = (Entry[]) entry_set.toArray(arr_entry);
		for (int i = 0; i < arr_entry.length; i++) {
			Entry entry = arr_entry[i];
			String fileid = (String) entry.getKey();
			int id = Integer.parseInt(fileid);
			saveFile4Note(conn, noteid, id);
		}
	}

	/**
	 * 保存公文附件
	 * 
	 * @param conn
	 * @param noteid
	 * @param fileid
	 * @throws SQLException
	 */
	private void saveFile4Note(Connection conn, int noteid, int fileid) throws SQLException {
		String sql = " UPDATE noteboard_finfo SET noteid=" + noteid + " WHERE fileid=" + fileid;
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * 保存公文正文内容
	 * 
	 * @param conn
	 * @param noteid
	 * @param content
	 * @throws SQLException
	 */
	public void saveContent(Connection conn, int noteid, String content) throws SQLException {
		content = SqlUtil.toLocal(content);// 代码集转换

		int iPart = content.length() / 250;
		if ((content.length() % 250) > 0)
			iPart = iPart + 1;
		String[] bodyDetail = new String[iPart];
		String tmpbody = content;

		for (int i = 0; i < iPart; i++) {
			if (tmpbody.length() < 250)
				bodyDetail[i] = tmpbody;
			else {
				bodyDetail[i] = tmpbody.substring(0, 250);
				tmpbody = tmpbody.substring(250);
			}
		}

		String sql = " INSERT INTO noteboard_text( noteid, seqno, data ) VALUES ( ?, ?, ? ) ";
		for (int j = 0; j < bodyDetail.length; j++) {
			int delrow = j + 1;
			bodyDetail[j] = bodyDetail[j];
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, noteid);
			pstmt.setInt(2, delrow);
			pstmt.setString(3, bodyDetail[j]);
			pstmt.executeUpdate();
			pstmt.close();
		}
	}

	/**
	 * 取得公文id
	 * 
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private int getNoteid(Connection conn) throws SQLException {
		int noteid = 0;

		String sql4Selfileinfo = "SELECT MAX(noteid) FROM noteboard_title";
		PreparedStatement pstmt = conn.prepareStatement(sql4Selfileinfo);
		ResultSet rs = pstmt.executeQuery();

		if (rs.next())
			noteid = rs.getInt(1);
		rs.close();
		pstmt.close();

		return noteid + 1;
	}

	/**
	 * 此方法用于查询附件的文件名
	 * 
	 * @param con
	 * @param fileid
	 * @return
	 * @throws SQLException
	 */
	public static String getFileName(Connection con, int fileid) throws SQLException {
		String filename = "UNKNOWN";
		String sql4filename = " SELECT filename FROM noteboard_finfo where fileid=? ";
		PreparedStatement pstmt = con.prepareStatement(sql4filename);
		pstmt.setInt(1, fileid);
		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			filename = rs.getString(1);
			filename = (filename == null) ? "UNKNOWN" : SqlUtil.fromLocal(filename.trim());
		} else {
			throw new SQLException("File NOF_FOUND", "File NOF_FOUND", 100);
		}

		rs.close();
		pstmt.close();
		return filename;
	}

	/**
	 * 此方法把Map对象内的值转成一个HTML内的ul控件. 用于输出文件名称.
	 * 
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 */
	public static String toListString(Map map) throws InvalidDataException {
		Element elm_ctrl = new Element("ul");
		if (map == null)
			throw new InvalidDataException("map is null");
		Set entry_set = map.entrySet();
		if (entry_set == null)
			throw new InvalidDataException("set is null");

		Entry[] arr_entry = new Entry[entry_set.size()];
		arr_entry = (Entry[]) entry_set.toArray(arr_entry);
		for (int i = 0; i < arr_entry.length; i++) {
			Entry entry = arr_entry[i];
			String filename = (String) entry.getValue();
			Element elm = new Element("li");
			elm.addContent(filename);
			elm_ctrl.addContent(elm);
		}

		XMLOutputter outputter = new XMLOutputter("  ", true, "UTF-8");
		outputter.setTextTrim(true);
		return outputter.outputString(elm_ctrl);
	}

	/**
	 * 过滤fileid
	 * 
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	static private int fetchFileid(Connection conn) throws SQLException {
		int id_info = 0, id_body = 0;
		int fileid = 0;

		{
			String sql_info = "SELECT MAX(fileid) fileid from noteboard_finfo";
			PreparedStatement pstmt_info = conn.prepareStatement(sql_info);
			ResultSet rs_info = pstmt_info.executeQuery();
			if (rs_info.next())
				id_info = rs_info.getInt(1);
			rs_info.close();
			pstmt_info.close();
		}

		{
			String sql_body = "SELECT MAX(fileid) fileid from noteboard_fbody";
			PreparedStatement pstmt_body = conn.prepareStatement(sql_body);
			ResultSet rs_body = pstmt_body.executeQuery();
			if (rs_body.next())
				id_body = rs_body.getInt(1);
			rs_body.close();
			pstmt_body.close();
		}

		fileid = (id_info > id_body) ? id_info : id_body;
		return fileid + 1;
	}

	/**
	 * 将附件内容输出
	 * 
	 * @param conn
	 * @param fileid
	 * @param ostream
	 * @throws SQLException
	 * @throws IOException
	 * @throws NamingException
	 * @throws InvalidDataException
	 */
	public void outputFileBody(Connection conn, int fileid, OutputStream ostream) throws SQLException, IOException,
			NamingException, InvalidDataException {
		String sql4body = "SELECT seqno, data FROM noteboard_fbody WHERE fileid=? order by seqno ";
		PreparedStatement pstmt = conn.prepareStatement(sql4body);
		pstmt.setInt(1, fileid);
		ResultSet rs = pstmt.executeQuery();

		/**
		 * 从数据库中逐行取出此文件的数据,解码后写入stream.
		 */
		BASE64Decoder decoder = new BASE64Decoder();
		while (rs.next()) {
			String data = rs.getString("data").trim();
			byte[] b = decoder.decodeBuffer(data);
			ostream.write(b);
		}
		rs.close();
		pstmt.close();
		ostream.flush();
	}

	/**
	 * 保存附件
	 * 
	 * @param conn
	 * @param request
	 * @return
	 * @throws FileUploadException
	 * @throws IOException
	 * @throws SQLException
	 */
	static public Map saveFileItems(Connection conn, HttpServletRequest request) throws FileUploadException,
			IOException, SQLException {
		/**
		 * FileUploadBase类里面有setHeaderEncoding方法
		 * 读取上传表单的各部分时会用到该encoding，如果没有指定encoding则使用系统缺省的encoding。
		 */
		DiskFileUpload fuploader = new DiskFileUpload();
		fuploader.setHeaderEncoding("UTF-8");
		fuploader.setSizeMax(10485760); // 允许上传文件最大值为10M
		List fileItems = fuploader.parseRequest(request);

		FileItem[] items = new FileItem[fileItems.size()];
		items = (FileItem[]) fileItems.toArray(items);
		return saveFileItems(conn, items);
	}

	/**
	 * 保存附件
	 * 
	 * @param conn
	 * @param items
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	static public Map saveFileItems(Connection conn, FileItem[] items) throws IOException, SQLException {
		HashMap map = new HashMap();
		for (int i = 0; i < items.length; i++) {
			FileItem fitem = items[i];
			if (fitem == null)
				continue;
			String fname = fitem.getName();
			if (fname != null && fname.length() > 0) {
				int fid = saveFile(conn, fitem);
				int p = fname.lastIndexOf('\\');
				if (p >= 0)
					fname = fname.substring(p + 1);
				map.put("" + fid, fname);
			}
		}

		return map;
	}

	/**
	 * 此方法用于把文件保存到数据库中.
	 * 
	 * @param conn
	 * @param fitem
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	static public int saveFile(Connection conn, FileItem fitem) throws IOException, SQLException {
		int fid = -1;
		String filename = fitem.getName();
		if (filename != null && filename.length() > 0) {
			int p = filename.lastIndexOf('\\');
			if (p >= 0)
				filename = filename.substring(p + 1);

			try {
				conn.setAutoCommit(false);
				fid = fetchFileid(conn);
				saveFileInfo(conn, fid, filename);
				saveFileBody(conn, fid, fitem);
				conn.commit();
			} catch (IOException e) {
				conn.rollback();
				throw e;
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(true);
			}
		}

		return fid;
	}

	/**
	 * 此方法用于保存文件信息: 文件名称等
	 * 
	 * @param conn
	 * @param filename
	 * @throws SQLException
	 */
	private static void saveFileInfo(Connection conn, int fileid, String filename) throws SQLException {
		String sql_ins = "INSERT INTO noteboard_finfo ( fileid, filename ) values( ?, ? ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql_ins);
		filename = SqlUtil.toLocal(filename);
		pstmt.setInt(1, fileid);
		pstmt.setString(2, filename);
		pstmt.execute();
		pstmt.close();
	}

	/**
	 * 此方法用于保存文件内容
	 * 
	 * @param conn
	 * @param fileid
	 * @param instream
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void saveFileBody(Connection conn, int fileid, FileItem fitem) throws SQLException, IOException {
		InputStream instream = fitem.getInputStream();

		String sql_ins = "insert into noteboard_fbody( fileid, seqno, data ) values(?,?,?) ";
		PreparedStatement pstmt = conn.prepareStatement(sql_ins);

		// 183转成base64后刚好是250字节。
		// 受base64的"由3个字节转成4个字节"的规则影响，定义的字节数组的下标一定要为3的倍数。
		int seqno = 1;
		byte[] b = new byte[183];
		byte[] bb;

		try {
			int len = instream.read(b, 0, b.length);
			while (len > 0) {
				if (len == b.length)
					bb = b;
				else {
					bb = new byte[len];
					System.arraycopy(b, 0, bb, 0, len);
				}
				String data = new BASE64Encoder().encode(bb);
				pstmt.setInt(1, fileid);
				pstmt.setInt(2, seqno);
				pstmt.setString(3, data); // 把转换后的内容写入数据库中
				pstmt.execute();
				seqno++;
				len = instream.read(b, 0, b.length);
			}
		} catch (SQLException e_sql) {
			e_sql.printStackTrace();
			throw e_sql;
		} finally {
			pstmt.close();
			instream.close();
		}
	}

	/**
	 * 取得公文列表
	 * 
	 * @param conn
	 * @param senddate
	 * @param sender
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static Element getNoteCatalogue(Connection conn, Map map) throws SQLException, IOException {
		String sqlParms = new SqlFilter(map).addFilter2String("sender", "t.editor", true).addFilter2String("dept",
				"t.dept", true).addFilter2MinDate("min_senderdate", "trunc(t.editdate)", true).addFilter2MaxDate(
				"max_senderdate", "trunc(t.editdate)", true).toWhereString();

		Statement stmt = conn.createStatement();
		String sql_output = " SELECT t.istop,t.ismobile,t.dept,t.noteid, t.title, t.editor, t.editdate, t.expiredate, "
				+ " (select count(DISTINCT venderid) from noteboard_log l where l.noteid=t.noteid ) call_number "
				+ " FROM noteboard_title t " + sqlParms + " ORDER BY t.istop desc,t.noteid desc ";
		ResultSet rs = stmt.executeQuery(sql_output);
		Element elm_cat;
		XResultAdapter adapter = new XResultAdapter(rs);
		elm_cat = adapter.getRowSetElement("catalogue", "row");

		int rows = adapter.rows();
		elm_cat.setAttribute("rows", "" + rows);
		elm_cat.setAttribute("focus", "noteboard");
		rs.close();
		stmt.close();

		return elm_cat;
	}

	/**
	 * 取得供应商公文列表
	 * 
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static Element getVenderNoteCatalogue(Connection conn, Token token, Map map) throws SQLException, IOException {
		Statement stmt = conn.createStatement();

		String sqlParms = new SqlFilter(map).addFilter2String("sender", "t.editor", true).addFilter2String("dept",
				"t.dept", true).addFilter2MinDate("min_senderdate", "trunc(t.editdate)", true).addFilter2MaxDate(
				"max_senderdate", "trunc(t.editdate)", true).add("trunc(t.expiredate) >= trunc(sysdate)").toString();
		
		String sql_output = " SELECT t.istop,t.dept,t.noteid, t.title, t.editor, to_char(editdate,'YYYY-MM-DD') editdate,to_char(expiredate,'YYYY-MM-DD') expiredate, "
				+ " (select count(*) from noteboard_log l where l.noteid=t.noteid and l.venderid='"
				+ token.getBusinessid()
				+ "' ) call_number "
				+ " FROM noteboard_title t  "
				+ " WHERE "
				+ sqlParms
				+ " ORDER BY t.istop desc,t.noteid desc ";

		ResultSet rs = stmt.executeQuery(sql_output);

		Element elm_cat;
		XResultAdapter adapter = new XResultAdapter(rs);
		elm_cat = adapter.getRowSetElement("catalogue", "row");

		int rows = adapter.rows();
		elm_cat.setAttribute("rows", "" + rows);
		elm_cat.setAttribute("focus", "noteboard");
		rs.close();
		stmt.close();

		return elm_cat;
	}

	/**
	 * 取得公文内容
	 * 
	 * @param conn
	 * @param token
	 * @param noteid
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 * @throws InvalidDataException
	 * @throws IOException 
	 */
	public static Element fetchNoteDetail(Connection conn, Token token, int noteid) throws SQLException,
			NamingException, InvalidDataException, IOException {
		String sql_sel = " SELECT noteid, title, editor, to_char(editdate,'YYYY-MM-DD') editdate,to_char(expiredate,'YYYY-MM-DD') expiredate,istop,dept "
				+ " FROM noteboard_title WHERE noteid = ? ";

		PreparedStatement pstmt = conn.prepareStatement(sql_sel);
		pstmt.setInt(1, noteid);
		ResultSet rs = pstmt.executeQuery();

		Element elm_file;
		if (rs.next()) {
			XResultAdapter convertor = new XResultAdapter(rs);
			elm_file = convertor.getElement4CurrentRow("notedetail");
		} else {
			throw new SQLException("系统中没有你要的记录!", "NOT_FOUND", 100);
		}
		rs.close();
		pstmt.close();

		String text = fetchContent(conn, noteid);
		Element elm_content = new Element("content");
		elm_content.addContent(text);
		elm_file.addContent(elm_content);

		Element elm_list = fetchFileCatalogue(conn, noteid);
		elm_file.addContent(elm_list);

		/**
		 * 记录点击
		 */
		String operator = token.getBusinessid();
		String rolename = token.isVender ? "VENDER" : "RETAIL";
		String venderid = token.getBusinessid();

		writeLog(conn, noteid, rolename, venderid, operator);

		return elm_file;
	}

	/**
	 * 取得公文附件列表
	 * 
	 * @param conn
	 * @param noteid
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static Element fetchFileCatalogue(Connection conn, int noteid) throws SQLException, IOException {
		String sql_sel = " SELECT noteid, fileid, filename  FROM noteboard_finfo WHERE noteid = ? ";

		PreparedStatement pstmt = conn.prepareStatement(sql_sel);
		pstmt.setInt(1, noteid);
		ResultSet rs = pstmt.executeQuery();

		Element elm_detail = null;

		XResultAdapter adapter = new XResultAdapter(rs);
		elm_detail = adapter.getRowSetElement("filelist", "row");

		int rows = adapter.rows();
		elm_detail.setAttribute("rows", "" + rows);
		rs.close();
		pstmt.close();

		return elm_detail;
	}

	/**
	 * 转换公文内容为本地编码
	 * 
	 * @param conn
	 * @param noteid
	 * @return
	 * @throws SQLException
	 */
	private static String fetchContent(Connection conn, int noteid) throws SQLException {
		String text = "";
		String sql = " SELECT seqno, data FROM noteboard_text WHERE noteid=? ORDER BY 1 ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, noteid);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
			text += rs.getString("data");
		pstmt.close();
		return SqlUtil.fromLocal(text);
	}

	/**
	 * 删除公文
	 * 
	 * @param conn
	 * @param noteid
	 * @throws SQLException
	 */
	public static void deletenote(Connection conn, int noteid) throws SQLException {
		String sql_fbody = " DELETE FROM noteboard_fbody WHERE fileid = ? ";
		String sql_finfo = " DELETE FROM noteboard_finfo WHERE noteid = ? ";
		String sql_text = " DELETE FROM noteboard_text  WHERE noteid = ? ";
		String sql_title = " DELETE FROM noteboard_title WHERE noteid = ? ";
		String sql_log = " DELETE FROM noteboard_log   WHERE noteid = ? ";

		try {
			conn.setAutoCommit(false);
			int fileid = getfileid(conn, noteid);
			if (fileid > 0) {
				PreparedStatement pstmt_fbody = conn.prepareStatement(sql_fbody);
				PreparedStatement pstmt_finfo = conn.prepareStatement(sql_finfo);
				pstmt_fbody.setInt(1, fileid);
				pstmt_finfo.setInt(1, noteid);
				pstmt_fbody.executeUpdate();
				pstmt_finfo.executeUpdate();
				pstmt_fbody.close();
				pstmt_finfo.close();
			}

			PreparedStatement pstmt_text = conn.prepareStatement(sql_text);
			PreparedStatement pstmt_title = conn.prepareStatement(sql_title);
			PreparedStatement pstmt_log = conn.prepareStatement(sql_log);

			pstmt_title.setInt(1, noteid);
			pstmt_text.setInt(1, noteid);
			pstmt_log.setInt(1, noteid);

			pstmt_text.executeUpdate();
			pstmt_title.executeUpdate();
			pstmt_log.executeUpdate();

			pstmt_text.close();
			pstmt_title.close();
			pstmt_log.close();

			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 得到公文附件id
	 * 
	 * @param conn
	 * @param noteid
	 * @return
	 * @throws SQLException
	 */
	private static int getfileid(Connection conn, int noteid) throws SQLException {
		int fileid = 0;
		String sql = " SELECT fileid FROM noteboard_finfo WHERE noteid=?  ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, noteid);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
			fileid = rs.getInt("fileid");
		pstmt.close();
		return fileid;
	}

	/**
	 * 阅读公文时调用此方法, 写日志 noteboatd_log.
	 * 每次阅读时都会留下记录, 但查询日志的程序只取出第一次阅读记录.
	 * 
	 * @param conn
	 * @param fileid
	 * @param rolename
	 * @param venderid
	 * @param operator
	 * @throws SQLException
	 */
	static private void writeLog(Connection conn, int noteid, String rolename, String venderid, String operator)
			throws SQLException {
		String sql_ins = "INSERT INTO noteboard_log (logid, noteid, fileid, rolename, venderid, operator, operation ) "
				+ " VALUES ( noteboard_log_id.nextval,?, ?, ?, ?, ?, ? ) ";

		PreparedStatement pstmt = conn.prepareStatement(sql_ins);
		pstmt.setInt(1, noteid);
		pstmt.setInt(2, 0);
		pstmt.setString(3, rolename);
		pstmt.setString(4, venderid);
		pstmt.setString(5, operator);
		pstmt.setString(6, "DOWNLOAD");
		pstmt.execute();
		pstmt.close();
	}

	/**
	 * 查询下载日志, 返回对指定公文的下载访问历史.
	 * 
	 * @param conn
	 * @param noteid
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static Element getVisitLog(Connection conn, int noteid) throws SQLException, IOException {

		String sql_sel = " SELECT t.logid, g.noteid, n.title, g.logdate, g.logtime, "
				+ " g.rolename, t.venderid, v.vendername, g.operator, g.operation "
				+ " FROM (SELECT noteid, venderid, MIN(logid) logid FROM noteboard_log WHERE noteid=? GROUP BY noteid, venderid) t "
				+ " JOIN noteboard_log g ON ( g.logid = t.logid ) "
				+ " JOIN noteboard_title n ON ( n.noteid = t.noteid ) "
				+ " JOIN vender v ON ( v.venderid = t.venderid ) " + " ORDER BY t.logid ";

		PreparedStatement pstmt_sel = conn.prepareStatement(sql_sel);
		pstmt_sel.setInt(1, noteid);
		ResultSet rs = pstmt_sel.executeQuery();

		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_list = adapter.getRowSetElement("vender_list", "row");

		int rows = adapter.rows();
		elm_list.setAttribute("rows", "" + rows);
		rs.close();
		pstmt_sel.close();

		return elm_list;
	}

	public static int cookExcelFile(Connection conn, int noteid, File file) throws SQLException, InvalidDataException,
			IOException {
		String[] title = { "公文号", "标题", "下载日期", "供应商号", "供应商名称", "下载人" };
		String sql_sel = " SELECT g.noteid, n.title, g.logdate, "
				+ " t.venderid, v.vendername, g.operator "
				+ " FROM (SELECT noteid, venderid, MIN(logid) logid FROM noteboard_log WHERE noteid=? GROUP BY noteid, venderid) t "
				+ " JOIN noteboard_log g ON ( g.logid = t.logid ) "
				+ " JOIN noteboard_title n ON ( n.noteid = t.noteid ) "
				+ " JOIN vender v ON ( v.venderid = t.venderid ) " + " ORDER BY t.logid ";

		PreparedStatement pstmt_sel = conn.prepareStatement(sql_sel);
		pstmt_sel.setInt(1, noteid);
		ResultSet rs = pstmt_sel.executeQuery();

		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		int rows = excel.cookExcelFile(file, rs, title, "公文下载记录");

		rs.close();
		pstmt_sel.close();
		return rows;
	}

	private Token	token;
}
