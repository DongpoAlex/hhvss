package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.royalstone.security.Token;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.vss.html.Option;
import com.royalstone.vss.html.Select;
import com.royalstone.vss.main.base.MainService;

/**
 * @author Administrator
 * BU
 */
public class BU extends MainService {

	public BU(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return null;
	}

	public Element toElement() {
		String defaultValue = request.getParameter("defaultValue");
		String showAll = request.getParameter("showAll");
		
		Select select = new Select();
		Option opt = null;
		if ("true".equalsIgnoreCase(showAll)) {
			opt = new Option("全部");
			opt.addAttribute("value", "");
			select.addChild(opt);
		}
		
		HashSet<String[]> set = token.getBuidSet();
		for (Iterator<String[]> iterator = set.iterator(); iterator.hasNext();) {
			String[] values = iterator.next();
			opt = new Option(values[1]);
			opt.addAttribute("value", values[0]);
			if (values[0].equals(token.getBuid())) {
				opt.addAttribute("style", "font-weight:bold;color:blue;");
				opt.addAttribute("selected", "selected");
			}
			select.addChild(opt);
		}
		
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
}
