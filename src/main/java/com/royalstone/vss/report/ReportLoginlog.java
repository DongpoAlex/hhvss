package com.royalstone.vss.report;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.royalstone.util.sql.SqlUtil;

import org.jdom.Element;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportLoginlog {

	final private Connection conn;

	private Element elm_report;
	private Map<?, ?> map;
	
	private int mmonths;

	/**
	 * Constructor
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	public ReportLoginlog(Connection conn, Map<?, ?> map) {
		this.conn = conn;
		this.map = map;
	}

    /**
     * 获取JASON数据
     * @return数据字符串
     * @throws SQLException
     * @throws InvalidDataException
     * @throws JSONException
     */
    public List<List<String>> toJSON() throws SQLException, InvalidDataException, JSONException {
        List<List<String>> array = new ArrayList<List<String>>();
        Filter filter;
        try {
            filter = getFilter(map);
            String sql = "";
            String[] strs = (String[]) map.get("moduleid");
            String[] strs3 = (String[]) map.get("jygs");
            String jygs = "CONTROLTYPE>1";
            if (strs3 != null && strs3.length > 0) {

                jygs = "CONTROLTYPE=" + strs3[0];

            }
            if (strs != null && strs.length > 0) {
                int moduleid = Integer.parseInt(strs[0]);
                if (moduleid == 101) {

                    sql = "SELECT e.JYGSNAME CONTROLTYPE, COUNT(C.LOGINID) loginid, '80' DJE, COUNT(C.LOGINID) * 80 * " + mmonths + " SUNMJE FROM "
                            + "(SELECT l.LOGINID FROM SYSLOG l WHERE userlevel=100 and l.MODULEID = 102  AND "
                            + filter.toString()
                            + " GROUP BY LOGINID) C, "
                            + "(SELECT A.VENDERID, B.CONTROLTYPE FROM GOODSSHOP A, SHOP B WHERE A.SHOPID = B.SHOPID AND " + jygs + " GROUP BY A.VENDERID, B.CONTROLTYPE) D,vendernamefrst e "
                            + "WHERE D.CONTROLTYPE=e.JYGSID and C.LOGINID = D.VENDERID GROUP BY E.JYGSNAME ORDER BY E.JYGSNAME";

                } else {
                    sql = " SELECT e.JYGSNAME CONTROLTYPE, C.LOGINID, c.vendername, '80' DJE, COUNT(C.LOGINID) * 80 * " + mmonths + " SUNMJE FROM"
                            + " (SELECT l.LOGINID,a.vendername FROM SYSLOG l, vender a WHERE userlevel = 100 AND l.MODULEID = 102 AND l.loginid=a.venderid and "
                            + filter.toString()
                            + " GROUP BY LOGINID,a.vendername ) C, "
                            + "(SELECT A.VENDERID, B.CONTROLTYPE FROM GOODSSHOP A, SHOP B WHERE A.SHOPID = B.SHOPID AND " + jygs + " GROUP BY A.VENDERID, B.CONTROLTYPE) D,vendernamefrst e"
                            + " WHERE D.CONTROLTYPE=e.JYGSID and C.LOGINID = D.VENDERID GROUP BY e.JYGSNAME,C.LOGINID,c.vendername ORDER BY JYGSNAME";
                }
            }
            System.out.print(sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            // 获取列数
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 遍历ResultSet中的每条数据
            while (rs.next()) {
            	List<String> list = new ArrayList<String>(); 
                // 遍历每一列
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value =  SqlUtil.fromLocal(rs.getString(columnName).trim());
                    list.add(value);
                }
                array.add(list);
            }
            rs.close();
            pstmt.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  array;
    }



    public Element toElement() throws SQLException, InvalidDataException {
		fetchLog(map);
		return elm_report;
	}

	/**
	 * 根据前台提供的过滤条件, 在数据库中查询满足条件的日志, 并以XML形式返回.
	 * 
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	private void fetchLog(Map<?, ?> map) throws SQLException,
			InvalidDataException {
		Filter filter;
		try {
			filter = getFilter(map);
		
		int count = getCount(filter);
		String sql = "";
		String[] strs = (String[]) map.get("moduleid");
        String[] strs3 = (String[]) map.get("jygs");
        String jygs ="CONTROLTYPE>1";
        if (strs3 != null && strs3.length > 0) {

                 jygs = "CONTROLTYPE="+strs3[0];

         }
		if (strs != null && strs.length > 0) {
			int moduleid = Integer.parseInt(strs[0]);
			if (moduleid == 101) {			

				sql = "SELECT  e.JYGSNAME CONTROLTYPE, COUNT(C.LOGINID) loginid, '80' DJE, COUNT(C.LOGINID) * 80 * "+mmonths+" SUNMJE FROM "
						+ "(SELECT l.LOGINID FROM SYSLOG l WHERE userlevel=100 and l.MODULEID = 102  AND "
						+ filter.toString()
						+ " GROUP BY LOGINID) C, "
						+ "(SELECT A.VENDERID, B.CONTROLTYPE FROM GOODSSHOP A, SHOP B WHERE A.SHOPID = B.SHOPID AND "+jygs+" GROUP BY A.VENDERID, B.CONTROLTYPE) D,vendernamefrst e "
						+ "WHERE D.CONTROLTYPE=e.JYGSID and C.LOGINID = D.VENDERID GROUP BY e.JYGSNAME ORDER BY e.JYGSNAME";

			}else{
			sql = " SELECT  e.JYGSNAME CONTROLTYPE, C.LOGINID, c.vendername, '80' DJE, COUNT(C.LOGINID) * 80 * "+mmonths+" SUNMJE FROM"
					+ " (SELECT l.LOGINID,a.vendername FROM SYSLOG l, vender a WHERE userlevel = 100 AND l.MODULEID = 102 AND l.loginid=a.venderid and "
					+ filter.toString()
					+" GROUP BY LOGINID,a.vendername ) C, "
					+ "(SELECT A.VENDERID, B.CONTROLTYPE FROM GOODSSHOP A, SHOP B WHERE A.SHOPID = B.SHOPID AND "+jygs+" GROUP BY A.VENDERID, B.CONTROLTYPE) D,vendernamefrst e"
					+ " WHERE D.CONTROLTYPE=e.JYGSID and C.LOGINID = D.VENDERID GROUP BY e.JYGSNAME,C.LOGINID,c.vendername ORDER BY e.JYGSNAME";
			}
		}
		System.out.print(sql);
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();		
		XResultAdapter adapter = new XResultAdapter(rs);

		/**
		 * 查询返回的记录数可能很多. 仅取最前面的10000行.
		 */
		elm_report = adapter.getRowSetElement("report", "row", 1, VSSConfig
				.getInstance().getRowsLimitHard());

		int rows = adapter.rows();
		elm_report.setAttribute("count", "" + count);
		elm_report.setAttribute("rows", "" + rows);
		elm_report.setAttribute("name", "Loginlog");
		rs.close();
		pstmt.close();
		return;
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取得符合条件的记录数
	 * 
	 * @param filter
	 * @return
	 * @throws SQLException
	 */
	private int getCount(Filter filter) throws SQLException {
		String sql = " SELECT count(*) FROM syslog l";
		if (filter.count() > 0)
			sql += " WHERE userlevel=100 and l.MODULEID = 102 and " + filter.toString();

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (!rs.next())
			throw new SQLException("getCount failed!", "", -1);
		int count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}

	/**
	 * @param map
	 * @return
	 * @throws InvalidDataException
	 * @throws ParseException 
	 */
	private Filter getFilter(Map<?, ?> map) throws InvalidDataException, ParseException {
		Filter filter = new Filter();

		
			String[] strs1 = (String[]) map.get("logdate_min");
			if (strs1 != null && strs1.length > 0) {
				String date = strs1[0];
				filter.add(" trunc(l.logdate) >= " + ValueAdapter.std2mdy(date));
			}
		

		
			String[] strs2 = (String[]) map.get("logdate_max");
			if (strs2 != null && strs2.length > 0) {
				String date = strs2[0];
				filter.add(" trunc(l.logdate) <= " + ValueAdapter.std2mdy(date));
			}

		mmonths=getMonthSpace(strs1[0],strs2[0]);

		return filter;
	}
	public int getMonthSpace(String date1, String date2)
			throws ParseException {

		int result = 0;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(sdf.parse(date1));
		c2.setTime(sdf.parse(date2));

		result = c2.get(Calendar.MONDAY) - c1.get(Calendar.MONTH);

		return result == 0 ? 1 : Math.abs(result);

	}
		 
	public Element getStat(String filtrate) throws SQLException,
			InvalidDataException, ParseException {
		Filter filter = getFilter(map);

		String sql_stat = " select l.moduleid,count(" + filtrate
				+ " loginid) count,m.modulename " + " from syslog l "
				+ " left join module_list m on (m.moduleid = l.moduleid) ";
		if (filter.count() > 0)
			sql_stat += " WHERE " + filter.toString();
		sql_stat += " group by l.moduleid,m.modulename ";

		PreparedStatement pstmt = conn.prepareStatement(sql_stat);
		ResultSet rs = pstmt.executeQuery();
		XResultAdapter adapter = new XResultAdapter(rs);

		/**
		 * 查询返回的记录数可能很多. 仅取最前面的10000行.
		 */
		elm_report = adapter.getRowSetElement("report", "row", 1, VSSConfig
				.getInstance().getRowsLimitHard());

		int rows = adapter.rows();
		elm_report.setAttribute("rows", "" + rows);
		elm_report.setAttribute("name", "syslog_stat");
		rs.close();
		pstmt.close();
		return elm_report;
	}

}
