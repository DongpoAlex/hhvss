package com.royalstone.certificate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;

public class SelCertificateType extends XComponent {
	public SelCertificateType(Token token) throws Exception {
		super(token);
		try {
			conn = openDataSource(token.site.getDbSrcName());
			elm_ctrl = cook();
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			closeDataSource(conn);
		}
	}

	public SelCertificateType(Token token, String note) throws Exception {
		super(token);
		this.note = note;
		try {
			conn = openDataSource(token.site.getDbSrcName());
			elm_ctrl = cook();
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			closeDataSource(conn);
		}
	}

	public SelCertificateType(Token token, String[] flags) throws Exception {
		super(token);

		if (flags != null && flags.length > 0) {
			this.flags = flags;
		}
		try {
			conn = openDataSource(token.site.getDbSrcName());
			elm_ctrl = cook();
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			closeDataSource(conn);
		}
	}

	public SelCertificateType(Token token,String[] flags, String note,String defaultValue) throws Exception {
		super(token);
		this.note = note;
		this.defaultValue = defaultValue;
		if (flags != null && flags.length > 0) {
			this.flags = flags;
		}
		try {
			conn = openDataSource(token.site.getDbSrcName());
			elm_ctrl = cook();
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			closeDataSource(conn);
		}
	}

	private Element cook() throws SQLException {
		String in = "";
		for (int i = 0; i < flags.length; i++) {
			if (flags.length == i + 1) {
				in += flags[i];
				break;
			}
			in += flags[i] + ",";
		}

		Element elm_sel = new Element("select");
		Element elm_opt = new Element("option");
		if (this.note != null && note.length() > 0) {
			elm_opt.addContent(this.note);
			elm_opt.setAttribute("value", "");
			elm_sel.addContent(elm_opt);
		}
		String sql = " SELECT ctid, ctname,yearflag,flag,appflag,whflag from certificateType where flag IN (" + in + ") order by ctid ";
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String ctid = rs.getString("ctid").trim(); // 该字段表中定义了不为空
			String ctname = rs.getString("ctname").trim(); // 该字段在表中定义了不为空
			String yearflag = rs.getString("yearflag");
			String whflag = rs.getString("whflag");
			String appflag = rs.getString("appflag");
			elm_opt = new Element("option");
			elm_opt.setAttribute("value", ctid);
			elm_opt.setAttribute("yearflag", yearflag);
			elm_opt.setAttribute("appflag", appflag);
			elm_opt.setAttribute("whflag", whflag);
			if(this.defaultValue!= null && this.defaultValue.length()>0 && this.defaultValue.equals(ctid)){
				elm_opt.setAttribute("selected","selected");
			}
			elm_opt.addContent(SqlUtil.fromLocal(ctname));
			elm_sel.addContent(elm_opt);
		}
		rs.close();
		pstmt.close();
		
		return elm_sel;
	}

	public Element getElmCtrl() {
		return elm_ctrl;
	}
	public HashSet<String> getHashSet(Connection conn) throws SQLException{
		HashSet<String> set = new HashSet<String>();
		String sql = " SELECT ctid from certificateType where flag=0";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		
		while (rs.next()) {
			set.add(rs.getString(1));
		}
		rs.close();
		pstmt.close();
		return set;
	}
	String[]							flags	= { "0", "1", "2" };

	private String						note;
	
	private String 						defaultValue;

	private Connection					conn	= null;
}
