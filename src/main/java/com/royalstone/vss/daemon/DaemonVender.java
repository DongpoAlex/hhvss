/*
 * Created on 20057-03-08
 *
 */
package com.royalstone.vss.daemon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.daemon.XErr;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.PermissionException;

/**
 * @author yzn
 * 
 */
public class DaemonVender extends XDaemon {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 20070329L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		this.check4Gzip(request);
		request.setCharacterEncoding( "UTF-8" );
		Element elm_doc = new Element("xdoc");

		Connection conn = null;
		try {
			if (!isSessionActive(request)) throw new PermissionException(PermissionException.LOGIN_PROMPT);
			Token token = this.getToken(request);
			if (token == null) throw new PermissionException(PermissionException.LOGIN_PROMPT);

			conn = openDataSource(token.site.getDbSrcName());
			this.setDirtyRead(conn);

			String venderid = request.getParameter("venderid");
			if (venderid == null || venderid.length() == 0) throw new InvalidDataException(
					"venderid not set! ");

			Vender vender_info = new Vender(conn, venderid);
			Element elm = vender_info.toElement();

			elm_doc.addContent(new Element("xout").addContent(elm));
			elm_doc.addContent(new XErr(0, "OK").toElement());

		}
		catch (SQLException e) {
			//e.printStackTrace();
			elm_doc.addContent(new XErr(e.getErrorCode(), e.getMessage()).toElement());
		}
		catch (Exception e) {
			//e.printStackTrace();
			elm_doc.addContent(new XErr(-1, e.toString()).toElement());
		}
		finally {
			output(response, elm_doc);
			closeDataSource(conn);
		}
	}
}

/**
 * @author yzn 查询供应商信息，主要用于判断是否预付款供应商
 * 
 */
class Vender {
	public Vender(Connection conn, String venderid) throws SQLException {
		String sql = " SELECT " + " venderid, vendername, prepayflag FROM vender "
				+ " WHERE venderid = ? ";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, venderid);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);
		if (rs.next()) elm_vender = adapter.getElement4CurrentRow("vender");
		rs.close();
		pstmt.close();
		if (elm_vender == null) throw new SQLException("无此供应商:" + venderid, "NOT FOUND", 100);
	}

	public Element toElement() {
		return elm_vender;
	}

	private Element	elm_vender	= null;
}
