package com.royalstone.util.sql;

import com.royalstone.util.sql.sqlformat.StringCheckUtil;
import org.jdom.Element;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author baijian 查询用SQLMap服务
 *         尚未完成
 */
public class querySqlMap {
    public static void main(String[] args) throws Exception {
        String sql = "select * from user where 1=1"
                + "<notempty name=\"sheetid\" table=\"order\">sheetid='{sheetid}'</notempty>"
                + "<notempty name=\"venderid\" table=\"vender\" >venderid='{venderid}'</notempty>";

        Map map = new HashMap();
        map.put("sheetid", "A00A2009100801001");
        map.put("venderid", "EX1172");
        System.out.println(getQuerySql(sql, map));

        String sql2 = "select * from vender where 1=1 "
                + "<notempty name=\"sheetid\" table=\"order\" />"
                + "<notempty name=\"venderid\" table=\"vender\" />";
    }

    /**
     * 将前台POST上来的xml解析。返回Params列表
     * <xparams>
     * <sheetid value="A00A001,A00A002" compare="in" />
     * <venderid value="EX1172" compare="="  />
     * <shopname value="广州" compare="like"  />
     * </xparams>
     *
     * @param xparams
     * @return
     */
    public static Map cookRequestMap(Element xparams) {
        String name = null;
        String value = null;
        String compare = null;
        HashMap map = new HashMap();
        List list = xparams.getChildren();
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            Element elm = (Element) iterator.next();
            name = elm.getName();
            value = elm.getAttributeValue("value");
            compare = elm.getAttributeValue("compare");
            map.put("name", new Params(name, value, compare));
        }
        return map;
    }

    public static String getQeurySQL(String sql, Map map) {
        if (sql == null) {
            return "null";
        }
        if (map == null || map.isEmpty()) {
            return sql;
        }
        String ps = "<notempty[\\s]{1,100}name=\\\"([0-9A-Za-z._-]{1,50})\\\"[\\s]{1,100}table=\\\"([0-9A-Za-z._-]{1,50})\\\"[\\s]{0,100}/notempty>";
        Pattern p = Pattern.compile(ps);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        String v = null;
        String rs = null;
        String name = null;
        String table = null;
        while (m.find()) {

            name = m.group(1);
            table = m.group(2);
            rs = m.group(3);
            v = (String) map.get(name);

            if (StringCheckUtil.isEmpty(v)) {
                m.appendReplacement(sb, "");
            } else {
                m.appendReplacement(sb, " " + table + "." + rs);
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    // 根据 <notempty name="xxx">content</notempty> 动态生成 sql
    // 参数xxx不为空时，才输出 content,实现动态sql
    // 目前只支持常见的notempty判断
    // 可使用模板来动态生成sql语句，这样将非常强大，常见的模板工具有 freemark,velocity等
    public static String getParamSql(String s, Map map) throws Exception {
        if (s == null) {
            return "null";
        }
        if (map == null) {
            return s;
        }

        String ps = "<notempty[\\s]{1,100}name=\\\"([0-9A-Za-z._-]{1,50})\\\"[\\s]{1,100}table=\\\"([0-9A-Za-z._-]{1,50})\\\"[\\s]{0,100}>([^/]{1,500})</notempty>";
        Pattern p = Pattern.compile(ps);
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        String v = null;
        String rs = null;
        String name = null;
        String table = null;
        while (m.find()) {

            name = m.group(1);
            table = m.group(2);
            rs = m.group(3);
            v = (String) map.get(name);

            if (StringCheckUtil.isEmpty(v)) {
                m.appendReplacement(sb, "");
            } else {
                m.appendReplacement(sb, " and " + table + "." + rs);
            }
        }
        m.appendTail(sb);
        return sb.toString();

    }

    // 获取所有参数 {xxx} xxx为参数名
    public static List getParamNames(String s) throws Exception {

        Map map = new HashMap();
        if (s == null) {
            return new ArrayList();
        }

        Pattern p = Pattern.compile("\\{([0-9A-Za-z._-]{0,50})\\}");
        Matcher m = p.matcher(s);
        String paramName = null;
        List list = new ArrayList();
        while (m.find()) {
            paramName = m.group(1);
            list.add(paramName);
            map.put(paramName, "1");
        }

        return list;
    }

    // 替换参数，全部替换
    public static String setParam(String s, Map map) throws Exception {
        if (map == null) {
            return s;
        }
        List list = getParamNames(s);
        if (list == null) {
            return s;
        }
        int i = 0;
        int num = 0;
        String paramName = null;
        String v = null;
        Object obj = null;

        num = list.size();
        for (i = 0; i < num; i++) {
            paramName = (String) list.get(i);
            obj = map.get(paramName);
            if (obj == null) {
                v = "";
            } else {
                v = obj + "";
            }

            s = setParamSingle(s, paramName, v);
        }

        return s;
    }

    // 参数替换，指定参数名

    public static String setParamSingle(String sql, String param, String v) throws Exception {

        if (v == null) v = "";
        if (param == null) param = "";
        param = param.trim();
        if (param.length() == 0) throw new Exception("\u53C2\u6570\u540D\u4E0D\u80FD\u4E3A\u7A7A");
        String s = "{" + param + "}";
        int ipos = 0;
        int len = 0;
        len = s.length();
        String head = null;
        String tail = null;
        StringBuffer sql2 = new StringBuffer();
        ipos = sql.indexOf(s);
        tail = sql;
        for (; ipos >= 0; ipos = sql.indexOf(s)) {
            head = sql.substring(0, ipos);
            tail = sql.substring(ipos + len);
            sql2.append(head).append(v);
            sql = tail;
        }

        sql2.append(tail);
        return sql2.toString();
    }

    public static String getQuerySql(String s, Map map) throws Exception {
        if (map == null) {
            return s;
        }
        if (map.isEmpty()) {
            return s;
        }
        s = getParamSql(s, map);
        return setParam(s, map);
    }
}
