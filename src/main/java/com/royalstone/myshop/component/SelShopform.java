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
 * 业态的下拉列表。
 * @author baij
 */
public class SelShopform extends XComponent 
{
	public SelShopform(Token token) throws Exception
    {
		super(token);
    	Connection conn = null;
        try{
        	conn = openDataSource( token.site.getDbSrcName() );
            elm_ctrl = cook(conn);
            this.setAttribute( "name","txt_shopform");
            this.setAttribute( "id","txt_shopform");
        } catch( Exception e){
        	throw e;
        } finally {
            if( conn != null ) closeDataSource( conn );
        }
    }

	private Element cook(Connection conn) throws SQLException
	{
		Element elm_sel  = new Element("select");
        
        
        String sql       = "select fieldvalue,lookup from lookupitem where lookupid='SHOPFORM' and status=0 and substr(note,0,1)='S' order by fieldvalue";
        Statement st     = conn.createStatement();
        ResultSet rs     = st.executeQuery(sql);
        
        Element elm_opt  = new Element("option");
//        elm_opt.setAttribute( "value","" );
//        elm_sel.addContent(elm_opt.addContent( "全部") );
        
        while(rs.next()){
            elm_opt                        = new Element("option");
            String id                      = rs.getString("fieldvalue").trim();
            String name                = rs.getString("lookup").trim();
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
