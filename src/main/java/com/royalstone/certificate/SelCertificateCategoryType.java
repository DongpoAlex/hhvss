package com.royalstone.certificate;
  
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;

public class SelCertificateCategoryType extends XComponent {
	public SelCertificateCategoryType(Token token,String ccid, String note,String defaultValue) throws Exception {
		super(token);
		this.note = note;
		this.defaultValue = defaultValue;
		if(ccid!=null){
			this.ccid = ccid;
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
		ArrayList<String[]> belist=new ArrayList<String[]>(),slist=new ArrayList<String[]>();
		Element elm_sel = new Element("select");
		Element elm_opt = new Element("option");
		if(this.note!=null && note.length()>0){
			elm_opt.addContent(this.note);
			elm_opt.setAttribute("value", "");
			elm_sel.addContent(elm_opt);
		}
		String sql = " SELECT t.ctid, t.ctname, t.yearflag, r.flag from certificateType t " +
				" join certificateRelation r on r.ctid=t.ctid " +
				" join certificateCategory c on c.ccid=r.ccid " +
				" where r.ccid=? ";
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		pstmt.setInt(1, Integer.parseInt(this.ccid));
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			elm_opt = new Element("option");
			String ctid = rs.getString("ctid").trim(); // 该字段表中定义了不为空
			String ctname = rs.getString("ctname").trim(); // 该字段在表中定义了不为空
			String yearflag = rs.getString("yearflag");
			String flag = rs.getString("flag");
			
			String[] ss = new String[4];
			ss[0] = ctid;
			ss[1] = ctname;
			ss[2] = yearflag;
			ss[3] = flag;
			if("0".equals(flag)){
				belist.add(ss);
			}else{
				slist.add(ss);
			}
			
			
//			elm_opt.setAttribute("value", ctid);
//			elm_opt.setAttribute("yearflag",yearflag);
//			elm_opt.setAttribute("flag",flag);
//			if(this.defaultValue!= null && this.defaultValue.length()>0 && this.defaultValue.equals(ctid)){
//				elm_opt.setAttribute("selected","selected");
//			}
//			elm_opt.addContent(SqlUtil.fromLocal(ctname));
//			elm_sel.addContent(elm_opt);
		}
		rs.close();
		pstmt.close();
		
		
		//添加必录证照
		Element elm_grp = new Element( "optgroup" );
		elm_grp.setAttribute("label",  "必备证照" );
		for (Iterator<String[]> i = belist.iterator(); i.hasNext();) {
			String[] ss = (String[]) i.next();
			elm_opt = new Element("option");
			elm_opt.setAttribute("value", ss[0]);
			elm_opt.setAttribute("yearflag", ss[2]);
			elm_opt.setAttribute("flag", ss[3]);
			if(this.defaultValue!= null && this.defaultValue.length()>0 && this.defaultValue.equals(ss[0])){
				elm_opt.setAttribute("selected","selected");
			}
			elm_opt.addContent(SqlUtil.fromLocal(ss[1]));
			elm_grp.addContent(elm_opt);
		}
		elm_sel.addContent(elm_grp);
		
		
		//添加可选
		Element elm_grp2 = new Element( "optgroup" );
		elm_grp2.setAttribute("label",  "可选证照" );
		
		for (Iterator<String[]> i = slist.iterator(); i.hasNext();) {
			String[] ss = (String[]) i.next();
			elm_opt = new Element("option");
			elm_opt.setAttribute("value", ss[0]);
			elm_opt.setAttribute("yearflag", ss[2]);
			elm_opt.setAttribute("flag", ss[3]);
			if(this.defaultValue!= null && this.defaultValue.length()>0 && this.defaultValue.equals(ss[0])){
				elm_opt.setAttribute("selected","selected");
			}
			elm_opt.addContent(SqlUtil.fromLocal(ss[1]));
			elm_grp2.addContent(elm_opt);
		}
		elm_sel.addContent(elm_grp2);
		
		
		return elm_sel;
	}
	public Element getElmCtrl(){
		return elm_ctrl;
	}
	
	public HashSet<String> getHashSet(Connection conn) throws SQLException{
		HashSet<String> set = new HashSet<String>();
		String sql = " SELECT t.ctid from certificateType t " +
		" join certificateRelation r on r.ctid=t.ctid " +
		" where r.ccid=? and r.flag=0";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, Integer.parseInt(this.ccid));
		ResultSet rs = pstmt.executeQuery();
		
		while (rs.next()) {
			set.add(rs.getString(1));
		}
		rs.close();
		pstmt.close();
		return set;
	}
	private String 						defaultValue;
	private String ccid;
	private String note;
	private Connection					conn	= null;
}
