package com.royalstone.myshop.component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jdom.Element;

import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;

/**
 * @author baij
 * 权限类型下拉菜单
 */
public class SelRoleType extends XComponent{
    public SelRoleType(com.royalstone.security.Token token ) throws Exception
	{  
    	super(token);
    	Connection conn = null;
        try{
            conn = openDataSource( token.site.getDbSrcName() );
            elm_ctrl = getList( conn );
        }catch(Exception e){
            throw e;
        }finally{
            if( conn != null ) closeDataSource( conn );
        }
    }
    
    
    private Element getList(Connection conn) throws SQLException
	{

    	Element elm_sel  = new Element("select");
        
        String sql       = "SELECT roletype,headroletype,roletypename FROM roletypeconfig ORDER BY roletype";
        Statement st     = conn.createStatement();
        ResultSet rs     = st.executeQuery(sql);
        
        Element elm_opt  = new Element("option");
        while(rs.next()){
            elm_opt                        = new Element( "option" );
            String id                      = rs.getString( "roletype" ).trim();
            String roelTypeName                = rs.getString( "roletypename" ).trim();
            elm_opt.setAttribute("value",id);
            elm_opt.setAttribute("headroletype",rs.getString("headroletype"));
            elm_opt.addContent( SqlUtil.fromLocal( roelTypeName ) );
            elm_sel.addContent(elm_opt);
        }
        rs.close();
        st.close();
        return elm_sel;
    }
}
