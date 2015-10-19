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
 * 显示全部帐套清单, 不受权限约束.
 * @author mengluoyi
 *
 */
public class SelBook extends XComponent
{
    public SelBook(Token token) throws Exception
    {
    	super(token);
        try{
        	conn = openDataSource( token.site.getDbSrcName() );
            elm_ctrl = fetchUserType();
        } catch( Exception e){
        	throw e;
        } finally {
        	closeDataSource( conn );
        }
    }
    
    private Element fetchUserType() throws SQLException
    {   
        Element elm_sel         = new Element( "select" );
        Element elm_opt         = new Element( "option" );
        String sql              = " SELECT bookno, bookname  from book order by bookno";
        PreparedStatement pstmt = this.conn.prepareStatement( sql );
        ResultSet rs            = pstmt.executeQuery();
        
        elm_opt.setAttribute( "value", "" );
        elm_sel.addContent( elm_opt.addContent( "" ) );
        
        while( rs.next() ){
            elm_opt         = new Element( "option" );
            String BookNo   = rs.getString( "bookno" ).trim();			//该字段表中定义了不为空
	        String BookName = rs.getString( "bookname" ).trim();		//该字段在表中定义了不为空	        
	        elm_opt.setAttribute( "value", BookNo);
	        BookName = BookNo + " " + BookName; 
	        elm_opt.addContent( SqlUtil.fromLocal( BookName ) );
	        elm_sel.addContent(elm_opt);
        }
        rs.close();
        pstmt.close();    
        return elm_sel;
    }
    
	
	private Connection conn = null;
}
