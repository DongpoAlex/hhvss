package com.royalstone.util.sql;

import com.royalstone.util.InvalidDataException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author baijian
 */
public class SqlMapUtil {
    /**
     * sql_where 参数需要含有where关键字
     *
     * @param conn
     * @param sql
     * @param sql_where
     * @return
     * @throws SQLException
     */
    static public int getCount(Connection conn, SqlUnit sqlUnit, String sql_where) {
        int count = 0;
        String sql = "";
        sql_where = sql_where == null ? "" : sql_where.trim();
        // sql_where = sql_where.length()>0?" and "+sql_where:sql_where;

        if (sqlUnit.getSqlGroupBy().length() > 8) {// 有group by，使用子结果count(*) 统计
            // 如果没有sql写死where条件，有用户定义where条件，则 加上 where 关键字
            if (sqlUnit.getSqlWhere().length() == 0 && (sql_where != null && sql_where.length() > 0)) {
                sql = "select count(*) from ( " + sqlUnit.getSqlHead() + sqlUnit.getSqlFrom() + " where " + sql_where
                        + sqlUnit.getSqlGroupBy() + ")";
                // 如果没有sql写死where条件，也没有用户定义where条件，则不 加上 where 关键字
            } else if (sqlUnit.getSqlWhere().length() == 0 && (sql_where == null || sql_where.length() == 0)) {
                sql = "select count(*) from ( " + sqlUnit.getSqlHead() + sqlUnit.getSqlFrom() + sqlUnit.getSqlGroupBy()
                        + ")";
                // 如果有sql写死where条件，有用户定义where条件
            } else if (sqlUnit.getSqlWhere().length() > 0 && (sql_where != null && sql_where.length() > 0)) {
                sql = "select count(*) from ( " + sqlUnit.getSqlHead() + sqlUnit.getSqlFrom() + sqlUnit.getSqlWhere()
                        + " and " + sql_where + sqlUnit.getSqlGroupBy() + ")";
                // 如果有sql写死where条件，没有用户定义where条件
            } else {
                sql = "select count(*) from ( " + sqlUnit.getSqlHead() + sqlUnit.getSqlFrom() + sqlUnit.getSqlWhere()
                        + sqlUnit.getSqlGroupBy() + ")";
            }
        } else {// 没有group by，使用max(rownum) 统计
            // 如果没有sql写死where条件，有用户定义where条件，则 加上 where 关键字
            if (sqlUnit.getSqlWhere().length() == 0 && (sql_where != null && sql_where.length() > 0)) {
                sql = "select count(*) " + sqlUnit.getSqlFrom() + " where " + sql_where + sqlUnit.getSqlGroupBy();
                // 如果没有sql写死where条件，也没有用户定义where条件，则不 加上 where 关键字
            } else if (sqlUnit.getSqlWhere().length() == 0 && (sql_where == null || sql_where.length() == 0)) {
                sql = "select count(*) " + sqlUnit.getSqlFrom() + sqlUnit.getSqlGroupBy();
                // 如果有sql写死where条件，有用户定义where条件
            } else if (sqlUnit.getSqlWhere().length() > 0 && (sql_where != null && sql_where.length() > 0)) {
                sql = "select count(*) " + sqlUnit.getSqlFrom() + sqlUnit.getSqlWhere() + " and " + sql_where
                        + sqlUnit.getSqlGroupBy();
                // 如果有sql写死where条件，没有用户定义where条件
            } else {
                sql = "select count(*) " + sqlUnit.getSqlFrom() + sqlUnit.getSqlWhere() + sqlUnit.getSqlGroupBy();
            }
        }

        count = Integer.parseInt(SqlUtil.querySQL4SingleColumn(conn, sql).get(0));
        return count;
    }

    static public int getCount(Connection conn, String sql, String sql_where) throws InvalidDataException, SQLException {
        int count = 0;
        int idx = sql.toLowerCase().indexOf(" from ");
        if (idx == -1) {
            throw new InvalidDataException("SQL 找不到from关键字！sql=" + sql);
        }

        sql = "select count(*) " + sql.substring(idx, sql.length()) + " " + sql_where;

        count = Integer.parseInt(SqlUtil.querySQL4SingleColumn(conn, sql).get(0));
        return count;
    }
}
