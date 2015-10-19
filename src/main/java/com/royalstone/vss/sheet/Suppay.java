package com.royalstone.vss.sheet;

import java.sql.Connection;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUtil;

public class Suppay extends SheetService {

	static String	tableName		= "fin_suppay_ih";
	String sql4Body2;
	public Suppay(Connection conn,Token token,String cmid) {
		super(conn, token, cmid, 3020211, "3020211001", "3020211002", tableName);
		super.catTableName="";
		int sid = token.site.getSid();
		sql4Body2 = SqlMapLoader.getInstance().getSql(sid, 3020211003L).toString();
	}

	public Filter cookFilter(Map parms) {
		Filter filter = new Filter();
		String[] ss = null;

		filter.add("a.flag NOT IN ('C','T') ");
		
		ss = (String[]) parms.get("venderid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			//如果是父供应商查询，加载子供应商
//			if(token.isVender && token.getBusinessid().equals(token.getDefbusinessid())){
//				ss = (String[])token.getBusinessidSet().toArray(ss);
//			}
			
			Values val_vender = new Values(ss);
			
			filter.add("a.supid IN (" + val_vender.toString4String() + ") ");
		}

		/**
		 * 如果前台指定了sheetid, 则以{sheetid+venderid}为准, 忽略其他过滤条件.
		 */
		ss = (String[]) parms.get("sheetid");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("a.billno IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}
		
		ss = (String[]) parms.get("status");
		if (ss != null && ss.length > 0 && ss[0] != null && ss[0].length() > 0) {
			Values val_sheetid = new Values(ss);
			filter.add("a.flag IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}

		ss = (String[]) parms.get("editdate_min");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.fkdate) >= " + ValueAdapter.std2mdy(date));
		}

		ss = (String[]) parms.get("editdate_max");
		if (ss != null && ss.length > 0) {
			String date = ss[0];
			filter.add("trunc(a.fkdate) <= " + ValueAdapter.std2mdy(date));
		}

		return filter;
	}
	
	
	@Override
	public Element veiw(String sheetid) {
		Element elm_sheet = setSheetInfo(sheetid);
		elm_sheet.addContent(getHead(sheetid));
		elm_sheet.addContent(getBody(sheetid));
		elm_sheet.addContent(SqlUtil.getRowSetElement(conn, sql4Body2, sheetid, "body2"));
		return elm_sheet;
	}
	
	public String getVenderid(String sheetid) {
		String sql = " select supid from " + tableName + " a where a.billno=?";
		return SqlUtil.queryPS4SingleColumn(conn, sql, sheetid).get(0);
	}
}
