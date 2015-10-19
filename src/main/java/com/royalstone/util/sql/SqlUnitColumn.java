package com.royalstone.util.sql;

public class SqlUnitColumn {
    private String columnName;//列名
    private String columnAlias;//别名
    private String colSqlName;//查询列值

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnAlias() {
        return this.columnAlias;
    }

    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    public String getColSqlName() {
        return this.colSqlName;
    }

    public void setColSqlName(String colSqlName) {
        this.colSqlName = colSqlName;
    }
}
