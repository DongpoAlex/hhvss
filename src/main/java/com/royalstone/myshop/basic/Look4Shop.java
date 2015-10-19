package com.royalstone.myshop.basic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jdom.Element;

import com.royalstone.util.daemon.XResultAdapter;

public class Look4Shop {

	public Look4Shop( Connection conn, String shopid ) throws SQLException
	{
		String sql = " SELECT shopid, shopname, bookno, shoptypeid, zoneid, headshopid "
			+ " FROM shop WHERE shopid ='"+shopid+"'";
		
		Statement st = conn.createStatement();
		ResultSet rs 	= st.executeQuery( sql );
        if( rs.next() ) {
        	XResultAdapter adapter = new XResultAdapter( rs );
        	elm_shop = adapter.getElement4CurrentRow( "shop" );
        }
        rs.close();
        st.close();
        if( elm_shop == null ) throw new SQLException( "无此门店:" + shopid, "NOT FOUND", 100 );
    }
    
    public Element toElement()
    {
        return elm_shop;
    }
    
    private Element elm_shop = null;
}
