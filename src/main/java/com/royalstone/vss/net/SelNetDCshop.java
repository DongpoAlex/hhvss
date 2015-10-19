package com.royalstone.vss.net;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;

public class SelNetDCshop extends XComponent {
	public SelNetDCshop(Token token) throws Exception {
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

	public SelNetDCshop(Token token, String note) throws Exception {
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

	private Element cook() throws SQLException {

		Element elm_sel = new Element("select");
		Element elm_opt = new Element("option");
		if(this.note!=null && note.length()>0){
			elm_opt.addContent(this.note);
			elm_opt.setAttribute("value", "");
			elm_sel.addContent(elm_opt);
		}
		String sql = " select shopid,shopname  from shop where shoptype='22' order by shopid ";
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			
			String shopid = rs.getString("shopid");
			String shopname = shopid+" | "+rs.getString("shopname");
			elm_opt = new Element("option");
			elm_opt.setAttribute("value", shopid);
			if(this.defaultValue!= null && this.defaultValue.length()>0 && this.defaultValue.equals(shopid)){
				elm_opt.setAttribute("selected","selected");
			}
			elm_opt.addContent(SqlUtil.fromLocal(shopname));
			elm_sel.addContent(elm_opt);
		}
		rs.close();
		pstmt.close();
		
		return elm_sel;
	}

	public Element getElmCtrl() {
		return elm_ctrl;
	}
	private String						note;
	private String 						defaultValue;
	private Connection					conn	= null;

}
