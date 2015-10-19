/*
 * 创建日期 2006-3-10
 */
package com.royalstone.myshop.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;

/**
 * 构建业务单据的下拉列表
 * @author yantao
 */
public class SelSheetPayable extends XComponent
{
	public SelSheetPayable(Token token) throws Exception
    {
		super(token);
        try{
            conn = openDataSource( token.site.getDbSrcName() );
            this.elm_ctrl = fetchSheetType();
        } catch( Exception e){
        	throw e;
        } finally {
            closeDataSource( conn );
        }
    }
	
	private Element fetchSheetType() throws SQLException
	{
        Element elm_sel = new Element( "select" );
        
        String sql = " SELECT serialid AS sheettype, name  FROM serialnumber WHERE payableflag=1 ORDER BY serialid";
        
        PreparedStatement pstmt = conn.prepareStatement( sql );
        ResultSet rs = pstmt.executeQuery();

        Element elm_opt = new Element( "option" );
        elm_opt.setAttribute( "value","" );
        elm_sel.addContent(elm_opt.addContent("请选择"));
        while( rs.next() ){
        	elm_opt = new Element( "option" );
            String sheettype = rs.getString("sheettype").trim();
	        String name = rs.getString( "name" ).trim();
	        
	        //name = sheettype + " " + name;
	        if(sheettype==null)         sheettype        = "";
            if(name == null) name  = "";
            elm_opt.setAttribute( "value", sheettype );
	        elm_opt.addContent( SqlUtil.fromLocal( name ) );
	        elm_sel.addContent(elm_opt);
        }
        rs.close();
        pstmt.close();    
        return elm_sel;
    }

	
	 private Connection conn = null;
}
