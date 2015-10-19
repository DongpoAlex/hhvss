package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.main.base.MainService;

/**
 * @author baij
 * 品类
 */
public class Category extends MainService{

	public Category(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return null;
	}
	
	public Element getCategoryByVenderID(){
		String venderid = token.getBusinessid();
		String categoryid = getParamNotNull("categoryid");
		String sql = " select a.categorytreeid,a.categoryid,a.hqcategoryid,a.categoryname,a.deptlevelid,a.headcatid " +
				"from category a,buinfo b, vender c where a.categorytreeid=b.categorytreeid and c.buid=b.buid and a.categoryid=? and c.venderid=? ";
		return SqlUtil.getRowSetElement(conn, sql, new String[]{categoryid,venderid}, "result");
	}

}
