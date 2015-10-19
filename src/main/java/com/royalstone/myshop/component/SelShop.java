/*
 * 创建日期 2005-10-27
 *
 */
package com.royalstone.myshop.component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jdom.Element;

import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;

/**
 * @author Mengluoyi
 *
 */
public class SelShop extends XComponent{
    public SelShop(com.royalstone.security.Token token ) throws Exception
	{  
    	super(token);
    	Connection conn = null;
        try{
            conn = openDataSource( token.site.getDbSrcName() );
            elm_ctrl = fetchShopList( conn );
        }catch(Exception e){
            throw e;
        }finally{
            if( conn != null ) closeDataSource( conn );
        }
    }
    
    
    private Element fetchShopList(Connection conn) throws SQLException
	{

    	Element elm_sel  = new Element("select");
        
        String sql       = "SELECT shopid,shopname FROM shop ORDER BY shopid";
        Statement st     = conn.createStatement();
        ResultSet rs     = st.executeQuery(sql);
        
        Element elm_opt  = new Element("option");
        elm_opt.setAttribute( "value","" );
        elm_sel.addContent(elm_opt.addContent( "" ) );
        
        while(rs.next()){
            elm_opt                        = new Element( "option" );
            String id                      = rs.getString( "shopid" ).trim();
            String shopname                = rs.getString( "shopname" ).trim();
            shopname = id + " " + shopname;
            if(id==null)         id        = "";
            if(shopname == null) shopname  = "";
            elm_opt.setAttribute("value",id);
            elm_opt.addContent( SqlUtil.fromLocal( shopname ) );
            elm_sel.addContent(elm_opt);
        }
        rs.close();
        st.close();
        return elm_sel;
    }
}
