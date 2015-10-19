package com.royalstone.certificate;
  
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;

public class SelCertificateCategory extends XComponent {
	public SelCertificateCategory(Token token) throws Exception{
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
	public SelCertificateCategory(Token token,String note) throws Exception{
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

	private Element cook() throws SQLException, NamingException {
		
		Element elm_sel = new Element("select");
		Element elm_opt = new Element("option");
		if(this.note!=null && note.length()>0){
			elm_opt.addContent(this.note);
			elm_opt.setAttribute("value", "");
			elm_sel.addContent(elm_opt);
		}
		String sqlcount = "select count(*) from venderCategoryRelation where venderid=?";
		String sql="SELECT cc.ccid, cc.ccname from certificateCategory cc";
		
		String[] env = token.getEnv("venderid");
		if(token.isVender && env!=null && env.length>0 ){
			String venderid = env[0];
			PreparedStatement pstmt = this.conn.prepareStatement(sqlcount);
			pstmt.setString(1, venderid);
			ResultSet rs = pstmt.executeQuery();
			//如果没有配置品类关系，则默认显示全部
			if(rs.next()){
				if(rs.getInt(1)>0){
					sql = " SELECT distinct cc.ccid, cc.ccname from certificateCategory cc " +
					" join venderCategoryRelation vr on (vr.ccid=cc.ccid and vr.venderid='"+venderid+"')";
				}
			}
			rs.close();
			pstmt.close();
		}
		
		
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			elm_opt = new Element("option");
			String ctid = rs.getString("ccid").trim(); // 该字段表中定义了不为空
			String ctname = rs.getString("ccname").trim(); // 该字段在表中定义了不为空
			elm_opt.setAttribute("value", ctid);
			elm_opt.addContent(SqlUtil.fromLocal(ctname));
			elm_sel.addContent(elm_opt);
		}
		rs.close();
		pstmt.close();
		return elm_sel;
	}
	public Element getElmCtrl(){
		return elm_ctrl;
	}
	private String note;
	private Connection					conn	= null;
}
