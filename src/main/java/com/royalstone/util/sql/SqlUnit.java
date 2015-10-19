package com.royalstone.util.sql;

import java.util.HashMap;
import java.util.HashSet;

/**
 * SQL语句分析
 *
 * @author baij
 */
public class SqlUnit {
    /**
     * 构建SqlUnit
     *
     * @param sql
     * @return
     */
    static public SqlUnit cook(String sql) {
        sql = " " + sql;
        sql = sql.replaceAll("\r\n", " ");
        sql = sql.replaceAll("\n", " ");
        sql = sql.replaceAll("\t", " ");

        // 关键字转为统一大写
        sql = sql.replaceAll(" select ", " SELECT ");
        sql = sql.replaceAll(" delete ", " DELETE ");
        sql = sql.replaceAll(" insert ", " INSERT ");
        sql = sql.replaceAll(" update ", " UPDATE ");
        sql = sql.replaceAll(" from ", " FROM ");
        sql = sql.replaceAll(" where ", " WHERE ");
        sql = sql.replaceAll(" group ", " GROUP ");
        sql = sql.replaceAll(" by ", " BY ");
        sql = sql.replaceAll(" order ", " ORDER ");
        sql = sql.replaceAll(" join ", " JOIN ");
        sql = sql.replaceAll(" left ", " LEFT ");
        sql = sql.replaceAll(" right ", " RIGHT ");
        sql = sql.replaceAll(" on ", " ON ");
        sql = sql.replaceAll(" as ", " AS ");

        String sqlHead = "", sqlFrom = "", sqlWhere = "", sqlGroupBy = "", sqlOrderBy = "";

        // 非select语句，DDL语句
        if (sql.indexOf(" DELETE ") != -1 || sql.indexOf(" INSERT ") != -1 || sql.indexOf(" UPDATE ") != -1) {
            sqlHead = sql;
        } else {

            // select语句

            int idxFR = sql.indexOf(" FROM ");
            int idxWH = sql.indexOf(" WHERE ");
            int idxGB = sql.indexOf(" GROUP ");
            int idxOB = sql.indexOf(" ORDER ");

            int idxEnd;
            // 截取select部分
            sqlHead = sql.substring(0, idxFR) + " ";

            // 截取FROM部分
            if (idxWH > 0) {
                idxEnd = idxWH;
            } else {
                if (idxGB > 0) {
                    idxEnd = idxGB;
                } else {
                    if (idxOB > 0) {
                        idxEnd = idxOB;
                    } else {
                        idxEnd = sql.length();
                    }
                }
            }
            sqlFrom = sql.substring(idxFR, idxEnd) + " ";

            // 截取Where部分
            if (idxWH > 0) {
                if (idxGB > 0) {
                    idxEnd = idxGB;
                } else {
                    if (idxOB > 0) {
                        idxEnd = idxOB;
                    } else {
                        idxEnd = sql.length();
                    }
                }
                sqlWhere = sql.substring(idxWH, idxEnd) + " ";
            }

            // 截取GROUP BY 部分
            if (idxGB > 0) {
                if (idxOB > 0) {
                    idxEnd = idxOB;
                } else {
                    idxEnd = sql.length();
                }
                sqlGroupBy = sql.substring(idxGB, idxEnd) + " ";
            }

            // 截取order by 部分
            if (idxOB > 0) {
                sqlOrderBy = sql.substring(idxOB, sql.length()) + " ";
            }

        }
        return new SqlUnit(sqlHead, sqlFrom, sqlWhere, sqlGroupBy, sqlOrderBy);
    }

    static public HashMap<String, SqlUnitColumn> cookCol(String sqlHead) {
        HashMap<String, SqlUnitColumn> colSet = new HashMap<String, SqlUnitColumn>();
        // 将FROM过滤
        sqlHead = sqlHead.replace(" SELECT ", "");
        sqlHead = sqlHead.replace(" AS ", "");
        sqlHead = sqlHead.toLowerCase();
        // 根据逗号分割列
        String[] ss = sqlHead.split(",");
        for (int i = 0; i < ss.length; i++) {
            String colsql = ss[i].trim();
            // 忽略*这种列名
            if (colsql.indexOf("*") > 0) {
                continue;
            }
            //忽略函数列
            if (colsql.indexOf("(") > 0) {
                continue;
            }
            SqlUnitColumn col = new SqlUnitColumn();

            int spaceIdx = colsql.indexOf(" ");
            if (colsql.indexOf("case ") > 0) {
                colsql.replace(colsql, "case ");
                colsql = colsql.trim();
                colsql.substring(0, colsql.indexOf(" "));
                col.setColSqlName(colsql);
                spaceIdx = colsql.indexOf(".");
                if (spaceIdx < 0) {
                    col.setColumnName(colsql);
                } else {
                    col.setColumnName(colsql.substring(spaceIdx + 1, colsql.length()));
                }
                col.setColumnAlias(col.getColumnName());
            } else {
                // a.column 这种列名
                if (spaceIdx < 0) {
                    col.setColSqlName(colsql);
                    spaceIdx = colsql.indexOf(".");
                    if (spaceIdx < 0) {
                        col.setColumnName(colsql);
                    } else {
                        col.setColumnName(colsql.substring(spaceIdx + 1, colsql.length()));
                    }
                    col.setColumnAlias(col.getColumnName());
                } else {
                    // 有别名
                    col.setColSqlName(colsql.substring(0, spaceIdx));
                    col.setColumnAlias(colsql.substring(spaceIdx + 1, colsql.length()));
                    spaceIdx = colsql.indexOf(".");
                    if (spaceIdx < 0) {
                        col.setColumnName(col.getColSqlName());
                    } else {
                        col.setColumnName(colsql.substring(spaceIdx + 1, col.getColSqlName().length()));
                    }
                }
            }
            colSet.put(col.getColumnName(), col);

        }
        return colSet;
    }

    private String sqlHead;    // select * 部分
    private String sqlFrom;    // from * 部分
    private String sqlWhere;    // where * 部分
    private String sqlGroupBy; // group by * 部分
    private String sqlOrderBy; // order by * 部分
    private HashSet<SqlUnitColumn> colSet;    // 列定义集合
    private HashSet<SqlUnitTable> tabSet;    // 表定义集合

    public SqlUnit(String sqlHead, String sqlFrom, String sqlWhere, String sqlGroupBy, String sqlOrderBy) {
        super();
        this.sqlHead = sqlHead;
        this.sqlFrom = sqlFrom;
        this.sqlWhere = sqlWhere;
        this.sqlGroupBy = sqlGroupBy;
        this.sqlOrderBy = sqlOrderBy;
    }

    public HashSet<SqlUnitColumn> getColSet() {
        return this.colSet;
    }

    public String getSqlFrom() {
        return this.sqlFrom;
    }

    public String getSqlGroupBy() {
        return this.sqlGroupBy;
    }

    public String getSqlHead() {
        return this.sqlHead;
    }

    public String getSqlOrderBy() {
        return this.sqlOrderBy;
    }

    public String getSqlWhere() {
        return this.sqlWhere;
    }

    public HashSet<SqlUnitTable> getTabSet() {
        return this.tabSet;
    }

    public String toString() {
        return sqlHead + sqlFrom + sqlWhere + sqlGroupBy + sqlOrderBy;
    }

    public String toString(String where) {
        String sql = "";
        where = where.trim();
        // 没有sqlmap 定义where条件，有用户定义where条件
        if (sqlWhere.length() == 0 && where.length() > 0) {
            sql = sqlHead + sqlFrom + " where " + where + sqlGroupBy + sqlOrderBy;
            // 没有sqlmap 定义where条件，没有用户定义where条件
        } else if (sqlWhere.length() == 0 && where.length() == 0) {
            sql = sqlHead + sqlFrom + sqlGroupBy + sqlOrderBy;
            // 有sqlmap 定义where条件，没有用户定义where条件
        } else if (sqlWhere.length() > 0 && where.length() == 0) {
            sql = sqlHead + sqlFrom + sqlWhere + sqlGroupBy + sqlOrderBy;
            // 有sqlmap 定义where条件，有用户定义where条件
        } else {
            sql = sqlHead + sqlFrom + sqlWhere + " and " + where + sqlGroupBy + sqlOrderBy;
        }
        return sql;
    }

}
