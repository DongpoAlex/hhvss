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
public class SupsettleList extends Sheet {

	public SupsettleList(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return  new SqlFilter(map).
		addFilter2String( "venderid", "SUPID",true).
		addFilter2String( "sheetid", "BILLNO",true).
		addFilter2String( "skmode", "SKMODE",true).
		addFilter2String( "chargeid", "CHARGEID",true).
		/*addFilter2String( "status", "STATUS",true).*/
		addFilter2String( "status", "decode(STATUS,'Z','Y','M','T','B','T','G','T','I','T','E','T',STATUS)",true).
		addFilter2MinDate( "editdate_min", "SCLDATE",true).
		addFilter2MaxDate( "editdate_max", "SCLDATE",true);
	}

}
