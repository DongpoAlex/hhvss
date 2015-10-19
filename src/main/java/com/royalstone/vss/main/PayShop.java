package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.html.DBSelect;
import com.royalstone.vss.html.Option;
import com.royalstone.vss.main.base.MainService;

/**
 * @author baij
 * 结算主体
 */
public class PayShop extends MainService {

	public PayShop(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		//payshop必须根据buid过滤
		HashSet<String[]> set = token.getBuidSet();
		String[] ss = new String[set.size()];
		int i = 0;
		for (Iterator<String[]> iterator = set.iterator(); iterator.hasNext();) {
			String[] values = iterator.next();
			ss[i] = values[0];
			i++;
		}
		//如果没有指定buid，则以用户buid为准
		String buid = request.getParameter("buid");
		if (buid == null || buid.length()==0) {
			map.put("buid", ss);
		}
		return new SqlFilter(map).addFilter2String("buid", "buid",true).
		add("buid IN (" + Values.toString4in(ss) + ") ");
	}

	public Element toElement() {
		String defaultValue = request.getParameter("defaultValue");
		String showAll = request.getParameter("showAll");
		Option opt = null;
		if ("true".equalsIgnoreCase(showAll)) {
			opt = new Option("全部");
			opt.addAttribute("value", "");
		}
		String sql = "select payshopid,payshopname,buid,payshopname alt from payshop " + cookFilter(getParams()).toWhereString()
				+ " order by payshopid";
		DBSelect select = new DBSelect(conn, sql, opt, defaultValue);

		String attr = request.getParameter("attribute");
		if (attr != null) {
			try {
				JSONObject jso = new JSONObject(attr);
				JSONArray jsr = jso.names();
				for (int i = 0; i < jsr.length(); i++) {
					String name = jsr.getString(i);
					String value = jso.getString(name);
					select.addAttribute(name, value);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return select.toElement();
	}

	/**
	 * 获取结算主体标题及logo
	 * 
	 * @param payshopid
	 * @return HashMap<String, String> ,keySet = {title,logo}
	 */
	static public HashMap<String, String> getTitleInfo(Connection conn, String payshopid) {
		String sql = "select taxername title,'../img/' || logopicture logo from payshop where payshopid=?";
		List<HashMap<String, String>> list = SqlUtil.queryPS4DataMap(conn, sql, payshopid);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	public Element getPayshopInfo(Connection conn, String payshopid){
		String sql = "select payshopid,payshopname,buid,taxername title,logopicture logo from payshop where payshopid=?";
		return SqlUtil.getRowSetElement(conn, sql, payshopid, "rowset");
	}
}
