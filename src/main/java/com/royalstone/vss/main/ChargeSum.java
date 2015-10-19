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
public class ChargeSum extends Sheet {

	public ChargeSum(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return  new SqlFilter(map).
		addFilter2String( "venderid", "a.venderid",true).
		addFilter2String( "shopid", "a.shopid",true).
		addFilter2String( "payshopid", "a.payshopid",true).
		addFilter2String( "buid", "a.buid",true).
		addFilter2String( "flag", "a.flag",true).
		addFilter2String( "sheetid", "a.noteno",true).
		addFilter2MinDate( "docdate_min", "a.docdate",true).
		addFilter2MaxDate( "docdate_max", "a.docdate",true);
	}

}
