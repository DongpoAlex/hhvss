/*
 * 创建日期 2005-12-13
 */
package com.royalstone.myshop.component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;

/**
 * 构建一个支付方式的下拉列表。
 * @author yantao
 */
public class SelPaytype extends XComponent 
{
	public SelPaytype(Token token) throws Exception
    {
		super(token);
    	Connection conn = null;
        try{
        	conn = openDataSource( token.site.getDbSrcName() );
            elm_ctrl = Paytype(conn);
            this.setAttribute( "name","txt_paytypeid");
        } catch( Exception e){
        	throw e;
        } finally {
            if( conn != null ) closeDataSource( conn );
        }
    }

	private Element Paytype(Connection conn) throws SQLException
	{
		Element elm_sel  = new Element("select");
        
        
        String sql       = "SELECT id, name FROM paytype ORDER BY id";
        Statement st     = conn.createStatement();
        ResultSet rs     = st.executeQuery(sql);
        
        Element elm_opt  = new Element("option");
        elm_opt.setAttribute( "value","" );
        elm_sel.addContent(elm_opt.addContent( "请选择") );
        
        while(rs.next()){
            elm_opt                        = new Element("option");
            String id                      = rs.getString("id").trim();
            String name                = rs.getString("name").trim();
            name = id + " " + name;
            if(id==null)         id        = "";
            if(name == null) name  = "";
            elm_opt.setAttribute("value",id);
            elm_opt.addContent(SqlUtil.fromLocal(name));
            elm_sel.addContent(elm_opt);
        }
        rs.close();
        st.close();
        return elm_sel;
	}

	
}
