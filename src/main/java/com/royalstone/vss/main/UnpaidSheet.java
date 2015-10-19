package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.vss.main.base.Sheet;

/**
 * @author baij
 * 供应商发票录入
 */
public class UnpaidSheet extends Sheet {

	public UnpaidSheet(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String,String[]> map) {
		return  new SqlFilter(map).
				addFilter2String( "venderid", "a.venderid",true).
				addFilter2String( "shopid", "a.shopid",true).
				addFilter2String( "payshopid", "a.payshopid",true).
				addFilter2String( "buid", "a.buid",true).
				addFilter2String( "payflag", "a.payflag",true).
				addFilter2String( "sheettype", "a.sheettype",true).
				addFilter2String( "sheetid", "a.sheetid",true).
				addFilter2MinDate( "docdate_min", "a.docdate",true).
				addFilter2MaxDate( "docdate_max", "a.docdate",true);
	}

}
