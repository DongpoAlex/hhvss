package com.royalstone.vss.main;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONObject;

import com.royalstone.security.Token;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.main.base.MainService;

public class Config extends MainService {

	public Config(HttpServletRequest request, Connection conn, Token token) throws Exception {
		super(request, conn, token);
	}

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return null;
	}

	/**
	 * 配置值清单
	 * 
	 * @return
	 */
	public Element getList() {
		String sql = "select valuekey,keyvalue,note,editflag from vssconfig order by editflag desc,valuekey";
		return SqlUtil.getRowSetElement(conn, sql, "rowset");
	}

	/**
	 * 编辑
	 * @throws Exception 
	 */
	public Element update() throws Exception {
		try {

			String s = getPOSTString();
			JSONObject jso = new JSONObject(s);
			int rowCount = (Integer) jso.get("rowCount");
			int k = 0;
			if (rowCount > 0) {
				conn.setAutoCommit(false);
				JSONArray rowSet = jso.getJSONArray("rowSet");
				String sql = "update vssconfig set  keyvalue=?,note=? where valuekey=? and editflag='Y'";
				for (int i = 0; i < rowSet.length(); i++) {
					JSONArray row = rowSet.getJSONArray(i);
					String valuekey = row.getString(0);
					String keyvalue = row.getString(1);
					String note = row.getString(2);
					String editflag = row.getString(3);
					int rowid = row.getInt(4);
					String sflag = row.getString(5);
					if ("M".equalsIgnoreCase(sflag)) {
						SqlUtil.executePS(conn, sql, keyvalue, note, valuekey);
						k++;
					}
				}
				conn.commit();
			}
			return new Element("row").addContent(String.valueOf(k));
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit(true);
		}
	}
}
