package com.royalstone.util.sql;

import com.royalstone.util.Log;
import com.royalstone.vss.Site;
import com.royalstone.vss.VSSConfig;
import org.jdom.Element;

import javax.naming.NamingException;
import java.sql.*;
import java.util.Hashtable;
import java.util.Set;

/**
 * @author baijian sql语句读取
 */
public class SqlMapLoader {
    private Hashtable<Integer, Hashtable<String, SqlUnit>> sqlsTable = null;
    static private SqlMapLoader instance = null;
    static private int dbType = 2;

    static public SqlMapLoader getInstance() {
        if (instance == null) {
            SqlMapLoader.instance = new SqlMapLoader();
            instance.init();
        }
        return SqlMapLoader.instance;
    }

    private void init() {
        String sql = " select smid,sqlstr from sqlmap where dbtype=" + dbType;
        sqlsTable = new Hashtable<Integer, Hashtable<String, SqlUnit>>();
        Connection conn = null;
        Set<Integer> set = VSSConfig.getInstance().getSiteTable().keySet();
        for (Integer sid : set) {
            try {
                Site site = (Site) VSSConfig.getInstance().getSiteTable().get(sid);
                if (site.getIsOpen()) {
                    Hashtable<String, SqlUnit> sqlTable = new Hashtable<String, SqlUnit>();
                    conn = SqlUtil.getConn(site.getDbSrcName());
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        String smid = rs.getString(1);
                        String sqlstr = SqlUtil.fromLocal(rs.getString(2));

                        if (smid != null && smid.length() > 0 && sqlstr != null && sqlstr.length() > 0) {
                            try {
                                // sqlstr = StringUtils.decodeBase64(sqlstr);
                                SqlUnit unit = SqlUnit.cook(sqlstr);
                                sqlTable.put(smid, unit);
                            } catch (Exception e) {
                                Log.db(conn, "Sqlmap", "站点" + site.getSiteName() + " SMID:" + smid + " SQL语法有错误");
                            }
                        }
                    }
                    Log.db(conn, "Sqlmap", "站点:" + site.getSiteName() + " Sqlmap加载完成");
                    sqlsTable.put(sid, sqlTable);
                    rs.close();
                    st.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NamingException e) {
                e.printStackTrace();
            } finally {
                SqlUtil.close(conn);
            }
        }

    }

    public SqlUnit getSql(int site, String smid) {
        Hashtable<String, SqlUnit> table = SqlMapLoader.getInstance().sqlsTable.get(site);
        SqlUnit unit = table.get(smid);
        if (unit == null)
            throw new DAOException(smid + ":找不到这条SQL");
        return unit;
    }

    public SqlUnit getSql(int site, long smid) {
        return getSql(site, String.valueOf(smid));
    }

    public Element getSqlInfo(Connection conn, String smid) throws NamingException, SQLException {
        String sql = " select smid,sqlstr,note from sqlmap where smid=? and dbtype=" + dbType;
        Element elm = SqlUtil.getRowSetElement(conn, sql, smid, "sqlmap");
        return elm;
    }

    public Element getSelSQLMap(Connection conn, int moduleid) throws SQLException {
        Element elm_sel = new Element("select");
        elm_sel.setAttribute("id", "selSQLMap");
        elm_sel.setAttribute("onchange", "changeSQL(this)");
        Element elm_opt = new Element("option");
        String sql = " SELECT smid, note,sqlstr  from sqlmap where moduleid=? and dbtype=? order by smid";
        Element elm = new Element("sqlmap");

        ResultSet rs = SqlUtil.queryPS(conn, sql, new Object[]{moduleid, dbType});

        while (rs.next()) {
            elm_opt = new Element("option");
            String smid = rs.getString("smid"); // 该字段表中定义了不为空
            String note = SqlUtil.fromLocal(rs.getString("note")); // 该字段在表中定义了不为空
            String sqlstr = SqlUtil.fromLocal(rs.getString("sqlstr")); // 该字段在表中定义了不为空
            // sqlstr = StringUtils.decodeBase64(sqlstr);

            elm_opt.setAttribute("value", smid);
            elm_opt.setAttribute("sqlstr", sqlstr);
            note = smid + " " + note;
            elm_opt.addContent(note);
            elm_sel.addContent(elm_opt);
        }
        SqlUtil.close(rs);
        elm.addContent(elm_sel);
        return elm;
    }

    public String getSelSqlCols(Connection conn, int sid, String smid) throws NamingException, SQLException {
        String sql = instance.getSql(sid, smid).toString(" rownum=1 ");
        String rel = "";
        ResultSet rs = SqlUtil.querySQL(conn, sql);
        ResultSetMetaData mt = rs.getMetaData();
        for (int i = 1; i <= mt.getColumnCount(); i++) {
            String colname = mt.getColumnName(i).toLowerCase();
            if (i == 1) {
                rel = colname;
            } else {
                rel += "," + colname;
            }
        }
        SqlUtil.close(rs);
        return rel;
    }

    public void updateSql(Connection conn, int site, String smid, String newSql, String note) throws NamingException,
            SQLException {
        // String sql = StringUtils.encodeBase64(newSql);
        String sql = "update sqlmap set sqlstr=?, note=? where smid=? and dbtype=?";
        try {
            conn.setAutoCommit(false);

            Hashtable<String, SqlUnit> table = SqlMapLoader.getInstance().sqlsTable.get(site);
            // 修改内存中的数据
            table.remove(smid);
            SqlUnit unit = SqlUnit.cook(newSql);
            table.put(smid, unit);

            // 修改数据库中的数据
            SqlUtil.executePS(conn, sql, new Object[]{newSql, note, smid, dbType});

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    private SqlMapLoader() {
    }
}
