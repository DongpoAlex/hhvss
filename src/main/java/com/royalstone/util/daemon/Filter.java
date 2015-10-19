/*
 * Created on 2004-9-22
 */
package com.royalstone.util.daemon;

import java.util.Map;
import java.util.Vector;

/**
 * Filter 用于组织SQL查询中的过滤条件.
 * 在Filter 对象中添加过滤元素,调用方法toString, 可以得到SQL语句中使用的过滤条件字串.
 * 转换过程中, 假定所有过滤元素是"与"关系. 转换后的字串中, 各过滤元素以 "AND" 分隔.
 * 
 * @author Mengluoyi
 * 
 */
public class Filter {
	/**
	 * 此函数用于从Map 对象中取出指定名字的参数字串.
	 * 参数map_filter 为request.getParameterMap()返回的对象, 其中包含了客户端送往服务器端的一系列参数.
	 * 在map_filter中,名字parm_name 对应的是一个字串组, 本函数只取该字串组的第一个成员.如果需要取所有成员,则不应使用本函数.
	 * 
	 * @param map_filter
	 *            request.getParameterMap()返回的对象
	 * @param parm_name
	 *            参数名字
	 * @return 参数值
	 */
	public static String getParameter(Map map_filter, String parm_name) {
		if (map_filter == null)
			return null;
		Object obj = map_filter.get(parm_name);
		if (obj != null && obj instanceof String[]) {
			String[] values = (String[]) obj;
			if (values.length > 0)
				return values[0];
		}
		return null;
	}

	final Map		params;

	/**
	 * <code>filters</code>Filter 内部用此变量保存过滤条件.
	 */
	private Vector	filters	= new Vector(0);

	public Filter() {
		super();
		params = null;
	}

	public Filter(Map params) {
		super();
		this.params = params;
	}

	/**
	 * 向Filter 对象中添加过滤元素.
	 * 
	 * @param filter
	 *            针对具体字段的过滤条件字串
	 * @return
	 */
	public Filter add(String filter) {
		if (filter != null && filter.length() > 0)
			filters.add(filter);
		return this;
	}

	final public Filter addFilter2Date(String key, String colName, boolean ignoreNullValue) {
		String[] ss = null;
		ss = (String[]) params.get(key);
		if (ss != null && ss.length > 0 && (ss[0].length() == 0) != ignoreNullValue) {
			this.add(" " + colName + " = " + ValueAdapter.std2mdy(ss[0]));
		}
		ss = null;
		return this;
	}

	final public Filter addFilter2MaxDate(String key, String colName, boolean ignoreNullValue) {
		String[] ss = null;
		ss = (String[]) params.get(key);
		if (ss != null && ss.length > 0 && (ss[0].length() == 0) != ignoreNullValue) {
			this.add(" " + colName + " <= " + ValueAdapter.std2mdy(ss[0]));
		}
		ss = null;
		return this;
	}

	final public Filter addFilter2MinDate(String key, String colName, boolean ignoreNullValue) {
		String[] ss = null;
		ss = (String[]) params.get(key);
		if (ss != null && ss.length > 0 && (ss[0].length() == 0) != ignoreNullValue) {
			this.add(" " + colName + " >= " + ValueAdapter.std2mdy(ss[0]));
		}
		ss = null;
		return this;
	}

	final public Filter addFilter2String(String key, String colName, boolean ignoreNullValue) {
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

	public String toWhereString() {
		return (count() == 0) ? "" : " WHERE " + toString();
	}
}
