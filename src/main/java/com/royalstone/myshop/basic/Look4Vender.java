package com.royalstone.myshop.basic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.Element;

import com.royalstone.util.daemon.XResultAdapter;

public class Look4Vender {

    public Look4Vender( Connection conn, String venderid ) throws SQLException
    {
        String sql = " SELECT "
        	+ " venderid, vendername, shortname, paytypeid, "
        	+ " corporator, taxno, bank, bankaccno, bankaccname, "
        	+ " address, zipcode, telno, faxno, email, www, "
        	+ " buyer, buyertelno, spermit, ppermit, busscope, status, localflag, payflag, payflagnote "
        	+ " FROM vender "
        	+ " WHERE venderid = ? ";

        PreparedStatement pstmt = conn.prepareStatement( sql );
        pstmt.setString( 1, venderid );
        ResultSet rs 	= pstmt.executeQuery(); 
        if( rs.next() ) {
        	XResultAdapter adapter = new XResultAdapter( rs );
        	elm_vender = adapter.getElement4CurrentRow( "vender" );
        }
        rs.close();
        pstmt.close();
        if( elm_vender == null ) throw new SQLException( "无此供应商:" + venderid, "NOT FOUND", 100 );
    }
    
    public Element toElement()
    {
        return elm_vender;
    }
    
    private Element elm_vender = null;
}
