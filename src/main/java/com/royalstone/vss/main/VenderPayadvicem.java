package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.royalstone.security.Token;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.vss.main.base.Sheet;

/**
 * @author baij
 * 供应商结算限额
 */
public class VenderPayadvicem extends Sheet{

	public VenderPayadvicem(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return  new SqlFilter(map).
		addFilter2String( "venderid", "a.venderid",true).
		addFilter2String( "payshopid", "a.payshopid",true).
		addFilter2String( "buid", "a.buid",true).
		addFilter2String( "month", "a.accmonth",true);
	}

}
