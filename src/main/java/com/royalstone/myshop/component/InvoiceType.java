package com.royalstone.myshop.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.VSSConfig;

/**
 * 显示发票种类
 * 
 */
public class InvoiceType extends XComponent {
	public InvoiceType(Token token) throws Exception {
		super(token);
		try {
			conn = openDataSource(VSSConfig.VSS_DB_MAIN);
			elm_ctrl = init();
		} catch (Exception e) {
			throw e;
		} finally {
			closeDataSource(conn);
		}
	}

	private Element init() throws SQLException {
		 Element elm_sel         = new Element( "select" );
	        Element elm_opt         = new Element( "option" );
	        String sql              = "select invoicetypeid,invoicetypename from invoicetype";
	        PreparedStatement pstmt = this.conn.prepareStatement( sql );
	        ResultSet rs            = pstmt.executeQuery();
	        
//	        elm_opt.setAttribute( "value", "" );
//	        elm_opt.setAttribute( "alt", "");
//	        elm_sel.addContent( elm_opt.addContent( "全部" ) );
	        
	        while( rs.next() ){
	            elm_opt         = new Element( "option" );
	            String payshopid   = rs.getString( "invoicetypeid" ).trim();			//该字段表中定义了不为空
		        String payshopname = rs.getString( "invoicetypename" ).trim();		//该字段在表中定义了不为空
		        elm_opt.setAttribute( "value", payshopid);
		        elm_opt.setAttribute( "alt", payshopname);
		        payshopname = payshopid + " " + payshopname; 
		        elm_opt.addContent( SqlUtil.fromLocal( payshopname ) );
		        elm_sel.addContent(elm_opt);
	        }
	        rs.close();
	        pstmt.close();    
	        return elm_sel;
	}

	private Connection	conn	= null;
}
