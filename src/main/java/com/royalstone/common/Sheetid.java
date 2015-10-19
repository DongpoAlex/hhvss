/*
 * Created on 2005-9-7
 *
 */
package com.royalstone.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 此模块用于生成新的单据编号.
 * @author meng
 *
 */
public class Sheetid {

	
    /**
     * 这个方法生成单据编号, 并初始化sheetid;
     */
    public static String getSheetid( Connection conn, int sheettype, String note )throws SQLException
	{
    	String sheetid = null;
        String sql             	= "call tl_getsheetid(?, ?, ?)";
        CallableStatement psc = conn.prepareCall(sql);
        psc.setInt(1, sheettype );
        psc.setString(2, note );
        psc.registerOutParameter(3, Types.VARCHAR);
        psc.execute();
        sheetid = psc.getString(3);
        psc.close();
        if( sheetid == null ) throw new SQLException( "取单据号失败", "", -1 );
        return sheetid;
    }
}
