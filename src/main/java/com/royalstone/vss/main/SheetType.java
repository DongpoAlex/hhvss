package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.royalstone.security.Token;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.vss.html.DBSelect;
import com.royalstone.vss.html.Option;
import com.royalstone.vss.main.base.MainService;

/**
 * @author baij
 *         单据类型
 */
public class SheetType extends MainService {

	public SheetType(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return new SqlFilter(map).addFilter2String("sheettype", "sheettype",true);
	}

	public Element toElement() {
		String defaultValue = request.getParameter("defaultValue");
		String showAll = request.getParameter("showAll");
		Option opt = null;
		if ("true".equalsIgnoreCase(showAll)) {
			opt = new Option("全部");
			opt.addAttribute("value", "");
		}
		String sql = "select sheettype,sheettypename from sheettype " + cookFilter(getParams()).toWhereString()+" order by sheettype";
		DBSelect select = new DBSelect(conn, sql, opt, defaultValue);
		
		String attr = request.getParameter("attribute");
		if(attr!=null){
			try {
				JSONObject jso = new JSONObject(attr);
				JSONArray jsr = jso.names();
				for (int i = 0; i < jsr.length(); i++) {
					String name = jsr.getString(i);
					String value=jso.getString(name);
					select.addAttribute(name, value);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return select.toElement();
	}

}
