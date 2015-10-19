package com.royalstone.util.sql;

import com.royalstone.util.daemon.ValueAdapter;
import com.royalstone.util.daemon.Values;

import java.util.Map;
import java.util.Vector;

public class SqlFilter {

    final Map<String, String[]> params;
    private Vector<String> filters = new Vector<String>(0);

    public SqlFilter() {
        super();
        params = null;
    }

    public SqlFilter(Map<String, String[]> params) {
        super();
        this.params = params;
    }

    /**
     * 向Filter 对象中添加过滤元素.
     *
     * @param filter 针对具体字段的过滤条件字串
     * @return
     */
    public SqlFilter add(String filter) {
        if (filter != null && filter.length() > 0)
            filters.add(filter);
        return this;
    }

    /**
     * 添加日期类型 =
     *
     * @param key
     * @param colName
     * @param ignoreNullValue 是否忽略空值，true = 当值为''时，不添加该条件
     * @return
     */
    final public SqlFilter addFilter2Date(String key, String colName, boolean ignoreNullValue) {
        String[] ss = null;
        ss = (String[]) params.get(key);
        if (ss != null && ss.length > 0 && (ss[0].length() == 0) != ignoreNullValue) {
            this.add(" " + colName + " = " + ValueAdapter.std2mdy(ss[0]));
        }
        ss = null;
        return this;
    }

    /**
     * 添加日期类型 <=
     *
     * @param key
     * @param colName
     * @param ignoreNullValue 是否忽略空值，true = 当值为''时，不添加该条件
     * @return
     */
    final public SqlFilter addFilter2MaxDate(String key, String colName, boolean ignoreNullValue) {
        String[] ss = null;
        ss = (String[]) params.get(key);
        if (ss != null && ss.length > 0 && (ss[0].length() == 0) != ignoreNullValue) {
            this.add(" " + colName + " <= " + ValueAdapter.std2mdy(ss[0]));
        }
        ss = null;
        return this;
    }

    /**
     * 添加日期类型>=
     *
     * @param key
     * @param colName
     * @param ignoreNullValue 是否忽略空值，true = 当值为''时，不添加该条件
     * @return
     */
    final public SqlFilter addFilter2MinDate(String key, String colName, boolean ignoreNullValue) {
        String[] ss = null;
        ss = (String[]) params.get(key);
        if (ss != null && ss.length > 0 && (ss[0].length() == 0) != ignoreNullValue) {
            this.add(" " + colName + " >= " + ValueAdapter.std2mdy(ss[0]));
        }
        ss = null;
        return this;
    }

    /**
     * 添加字符类型，如果值个数为1则使用=，如果值个数>1则使用IN
     *
     * @param key
     * @param colName
     * @param ignoreNullValue 是否忽略空值，true 表示 当值为''时，不添加该条件
     * @return
     */
    final public SqlFilter addFilter2String(String key, String colName, boolean ignoreNullValue) {
        String[] ss = null;
        ss = (String[]) params.get(key);
        if (ss != null) {
            if (ss.length == 1 && (ss[0].length() == 0) != ignoreNullValue) {
                this.add(" " + colName + " = " + Values.toString4String(ss[0]));
            } else if (ss.length > 1) {
                this.add(" " + colName + " IN (" + Values.toString4in(ss) + ") ");
            }
        }
        ss = null;
        return this;
    }

    /**
     * 添加字符类型模糊查询，仅支持值个数为1
     *
     * @param key
     * @param colName
     * @param ignoreNullValue 是否忽略空值，true 表示 当值为''时，不添加该条件
     * @return
     */
    final public SqlFilter addFilter2StringLike(String key, String colName, boolean ignoreNullValue) {
        String[] ss = null;
        ss = (String[]) params.get(key);
        if (ss != null) {
            if (ss.length == 1 && (ss[0].length() == 0) != ignoreNullValue) {
                this.add(" " + colName + " LIKE " + Values.toString4Like(ss[0]));
            }
        }
        ss = null;
        return this;
    }

    /**
     * @param key
     * @param colName
     * @param sign            符号，目前只实现 = IN LIKE >= <=
     * @param ignoreNullValue 是否忽略空值，true 表示 当值为''时，不添加该条件
     * @return
     */
    final public SqlFilter addFilter(String key, String colName, String sign, boolean ignoreNullValue) {
        String[] ss = null;
        ss = (String[]) params.get(key);
        if (ss != null) {
            if (ss.length == 1 && (ss[0].length() == 0) != ignoreNullValue) {
                if ("LIKE".equalsIgnoreCase(sign)) {
                    this.add(" " + colName + " LIKE " + Values.toString4Like(ss[0]));
                } else {
                    this.add(" " + colName + " " + sign + " " + Values.toString4String(ss[0]));
                }
            } else if (ss.length > 1) {
                //当多值时强制为IN
                this.add(" " + colName + " IN (" + Values.toString4in(ss) + ") ");
            }
        }
        ss = null;
        return this;
    }

    /**
     * @return Filter 对象中所保存的过滤条件数目.
     */
    public int count() {
        return filters.size();
    }

    /**
     * 此函数把Filter 对象内所保存的过滤条件转化为用于SQL语句中的字串.
     *
     * @return 用于SQL语句的字串
     */
    public String toString() {
        String str_filter = " ";

        if (filters.size() > 0)
            str_filter += " (" + ((String) filters.get(0)) + ") ";
        for (int i = 1; i < filters.size(); i++) {
            String s = (String) filters.get(i);
            str_filter += "\n AND ( " + s + " ) ";
        }
        return str_filter;
    }

    /**
     * 如果有过滤条件 则自动补 WHERE关键字
     *
     * @return
     */
    public String toWhereString() {
        return (count() == 0) ? "" : " WHERE " + toString();
    }
}
