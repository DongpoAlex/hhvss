/*
 * 创建日期 2005-10-09
 *
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
 * 此模块在后台生成一个WEB控件, 用于显示扣项代码, 包括临时扣项和固定扣项.
 * @author Windfly
 *
 */
public class SelChargeCodeList extends XComponent
{
    public SelChargeCodeList(Token token) throws Exception
	{        
    	super(token);
        try{
            conn = openDataSource( token.site.getDbSrcName() );
            elm_ctrl = fetchChargeCodeList();
            this.setAttribute("name","txt_chargecodeid");
        }catch(Exception e){
            throw e;
        }finally{
            closeDataSource( conn );
        }
    }
    
    
    /**
     * @return
     * @throws SQLException
     */
    private Element fetchChargeCodeList() throws SQLException
	{
        Element elm_sel  = new Element("select");
        Element elm_opt  = new Element("option");
        String sql       = " SELECT chargecodeid,chargename FROM chargecode ORDER BY chargecodeid ";
        Statement st     = this.conn.createStatement();
        ResultSet rs     = st.executeQuery(sql);
        
        elm_opt.setAttribute( "value","" );
        elm_sel.addContent(elm_opt.addContent("请选择"));
        
        while(rs.next()){
            elm_opt                        = new Element("option");
            String chargecodeid                      = rs.getString("chargecodeid").trim();
            String chargename                = rs.getString("chargename").trim();
            chargename = chargecodeid + " " + chargename;
            if(chargecodeid==null)         chargecodeid   = "";
            if(chargename == null) chargename  = "";
            elm_opt.setAttribute("value",chargecodeid);
            elm_opt.addContent(SqlUtil.fromLocal(chargename));
            elm_sel.addContent(elm_opt);
        }
        rs.close();
        st.close();
        return elm_sel;
    }
    
	private Connection conn = null;
}
