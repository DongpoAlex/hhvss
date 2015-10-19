package com.royalstone.myshop.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;
//import com.royalstone.fas.component.XComponent;

public class SelSheettype extends XComponent
{
    public SelSheettype(com.royalstone.security.Token token) throws Exception
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
        
        String sql = " SELECT serialid, name  from serialnumber order by serialid";
        
        PreparedStatement pstmt = conn.prepareStatement( sql );
        ResultSet rs = pstmt.executeQuery();

        while( rs.next() ){
	        Element elm_opt = new Element( "option" );

	        String typeid = rs.getString( "serialid" ).trim();
	        String typename = rs.getString( "name" ).trim();
	        if ( typeid == null ) typeid = "";
	        elm_opt.setAttribute( "value", typeid);

	        if ( typename == null ) typename = "";
	        typename = typeid + " " + typename; 
	        elm_opt.addContent( SqlUtil.fromLocal( typename ) );
	        elm_sel.addContent(elm_opt);
        }
        rs.close();
        pstmt.close();    
        return elm_sel;
    }

	
	 private Connection conn = null;
}
