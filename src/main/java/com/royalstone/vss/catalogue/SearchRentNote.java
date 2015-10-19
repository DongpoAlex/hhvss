/**
 * 
 */
package com.royalstone.vss.catalogue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;

/**
 * @author BaiJian
 * 联营租赁费用通知单
 */
public class SearchRentNote {
	final private Connection conn;
	final private Map map;
	/**
	 * @param conn
	 * @param map
	 */
	public SearchRentNote(Connection conn, Map map) {
		super();
		this.conn = conn;
		this.map = map;
	}
	
	final static String sql_head=" select a.sheetid, a.venderid,v.vendername, a.duedate, a.shopid,s.shopname, a.bookno, " +
			" a.receivableamt, a.receiveamt, a.saleamt, a.chargeamt, a.paynum, " +
			" a.flag, a.postflag, a.editor, a.editdate, a.checker, a.checkdate, a.note " +
			"  from rentnote0 a " +
			" join shop s on s.shopid=a.shopid " +
			" join vender v on v.venderid=a.venderid ";
	
	
	public Element getSheetList() throws InvalidDataException, SQLException{
		String sql_where = " where " + cookFilter(map).toString();
		String sql = sql_head+sql_where;
		
		Log.debug(this.getClass().getName(), sql);
		
		PreparedStatement pstmt = conn.prepareStatement( sql );
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter( rs );
		Element elm_cat = adapter.getRowSetElement( "catalogue", "row", 1, VSSConfig.getInstance().getRowsLimitSoft() );		
		int rows = adapter.rows();
		elm_cat.setAttribute( "rows", "" + rows );
		elm_cat.setAttribute( "sheetname", "rentnote" );
		rs.close();
		pstmt.close();
		return elm_cat;
	}
	
	private Filter cookFilter(Map map) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		String[] venderid = null;
		venderid = (String[]) map.get("venderid");
		if (venderid != null && venderid.length > 0) {
			Values val_vender = new Values(venderid);
			filter.add("a.venderid IN (" + val_vender.toString4String() + ") ");
		}
		/**
		 * 如果前台指定了sheetid, 则以sheetid为准, 忽略其他过滤条件.
		 */
		ss = (String[]) map.get("sheetid");
		if ((ss != null && ss.length > 0) && (venderid != null && venderid.length > 0)) {
			Values val_sheetid = new Values(ss);
			filter.add("a.sheetid IN (" + val_sheetid.toString4String() + ") ");
			return filter;
		}

		ss = (String[]) map.get("bookno");
		if (ss != null && ss.length > 0) {
			Values val_house = new Values(ss);
			filter.add("a.bookno IN (" + val_house.toString4String() + ") ");
		}

		ss = (String[]) map.get("shopid");
		if (ss != null && ss.length > 0) {
			Values val_editor = new Values(ss);
			filter.add("a.shopid IN (" + val_editor.toString4String() + ") ");
		}
		
		filter.add("a.flag=100");
		
		return filter;
	}
}
