package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.royalstone.security.Token;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.vss.main.base.Sheet;

/**
 * @author baij
 * 供应商扣项
 */
public class Dealings extends Sheet {

	public Dealings(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return  new SqlFilter(map).
		addFilter2String( "venderid", "supid",true).
		addFilter2String( "shopid", "substr(billno,1,4)",true).
		addFilter2String( "sheettype", "btype",true).
		addFilter2String( "status", "flag",true).
		addFilter2MinDate( "editdate_min", "bcdate",true).
		addFilter2MaxDate( "editdate_max", "bcdate",true);
	}

}
