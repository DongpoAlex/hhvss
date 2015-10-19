package com.royalstone.myshop.basic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.daemon.XResultAdapter;

public class Look4Goods {

	public Look4Goods( Connection conn, String barcode ) throws SQLException
	{
		String sql = " select a.goodsid,g.barcode,a.goodsname,a.shortname,a.spec,a.unitname,a.deptid " +
				//" from gpackage g join goods a on a.goodsid=g.goodsid " +
				" from goods a join gpackage g on a.goodsid=g.goodsid" +
				" where g.barcode=? ";
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, barcode);
		ResultSet rs 	= ps.executeQuery( );
        if( rs.next() ) {
        	XResultAdapter adapter = new XResultAdapter( rs );
        	elmGoods = adapter.getElement4CurrentRow( "goods" );
        }
        rs.close();
        ps.close();
        if( elmGoods == null ) throw new SQLException( "无此商品:" + barcode, "NOT FOUND", 100 );
    }
    
    public Element toElement()
    {
        return elmGoods;
    }
    
    private Element elmGoods = null;
}
