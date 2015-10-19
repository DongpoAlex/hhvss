package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.main.base.MainService;

/**
 * @author baij
 * 
 */
public class VenderTask extends MainService {

	public VenderTask(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return null;
	}

	private int getVenderInvoice(){
		int num = 0;
		String venderid =token.getBusinessid();
		String sql = "select count(*) from venderinvoice a,paymentnote b where a.pnsheetid=b.sheetid and a.flag=0 and b.flag=2 and a.venderid=?";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, venderid);
		if(list.size()>0){
			num = Integer.parseInt(list.get(0));
		}
		return num;
	}
	
	public Element getTask(){
		Element	elm_cat	= new Element("taskList");
		int row = getVenderInvoice();
		if(row>0){
			Element elm_row = new Element("row");
			elm_row.setAttribute("moduleid", "3020306");
			elm_row.setAttribute("taskname", "新结算发票录入");
			elm_row.setAttribute("g_status", "0");
			elm_row.setAttribute("tasks", "" + row);
			elm_cat.addContent(elm_row);
		}
		return elm_cat;
	}
}
