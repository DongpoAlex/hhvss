package com.royalstone.vss.net;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class Orderserial {
	/**
     * 这个方法生成流水号, 并初始化流水号;
     */
    public static String getOrderserial( Connection conn, int pex, int floor,String request_date,String starttime )throws SQLException
	{
    	String orderserial = null;
        String sql             	= "call tl_getorderserial(?,?,?,?,?)";
        CallableStatement psc = conn.prepareCall(sql);
        psc.setInt(1, pex );
        psc.setInt(2, floor );
        psc.setString(3, request_date );
        psc.setString(4, starttime );
        psc.registerOutParameter(5, Types.VARCHAR);
        psc.execute();
        orderserial = psc.getString(5);
        psc.close();
        if( orderserial == null ) throw new SQLException( "取流水号失败", "", -1 );
        return orderserial;
    }
}
