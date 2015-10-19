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
 * 显示全部结算地, 不受权限约束.
 *
 */
public class SelPayShop extends XComponent
{
    public SelPayShop(Token token) throws Exception
    {
    	super(token);
        try{
        	conn = openDataSource( token.site.getDbSrcName() );
            elm_ctrl = init();
        } catch( Exception e){
        	throw e;
        } finally {
        	closeDataSource( conn );
        }
    }
    
    private Element init() throws SQLException
    {   
        Element elm_sel         = new Element( "select" );
        Element elm_opt         = new Element( "option" );
        String sql              = " SELECT payshopid, payshopname,buid from payshop order by payshopid";
        PreparedStatement pstmt = this.conn.prepareStatement( sql );
        ResultSet rs            = pstmt.executeQuery();
        
        elm_opt.setAttribute( "value", "" );
        elm_opt.setAttribute( "alt", "");
        elm_sel.addContent( elm_opt.addContent( "全部" ) );
        
        while( rs.next() ){
            elm_opt         = new Element( "option" );
            String payshopid   = rs.getString( "payshopid" ).trim();			//该字段表中定义了不为空
	        String payshopname = rs.getString( "payshopname" ).trim();		//该字段在表中定义了不为空
	        String buid =  rs.getString( "buid" ).trim();
	        elm_opt.setAttribute( "value", payshopid);
	        elm_opt.setAttribute( "alt", payshopname);
	        elm_opt.setAttribute( "buid", buid);
	        payshopname = payshopid + " " + payshopname; 
	        elm_opt.addContent( SqlUtil.fromLocal( payshopname ) );
	        elm_sel.addContent(elm_opt);
        }
        rs.close();
        pstmt.close();    
        return elm_sel;
    }
    
	private Connection conn = null;
}
