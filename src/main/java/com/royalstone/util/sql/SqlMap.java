package com.royalstone.util.sql;

import com.royalstone.util.sql.sqlformat.StringCheckUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlMap {
    static Properties config = null;
    // static String sql_config_file = "/ajf_sql.properties";
    static String sql_config_file = "/ajf_sql_map.config";
    static Class c = SqlMap.class;
    static String SEP = "ajf_sql_map\\(";
    static String sql_config_string = null;

    // ------------
    private SqlMap() {
    }

    // clear the note,start with // ,single line
    // 去掉注释，只支持以//开头的单行注释
    public static String getConfigString(String s) throws Exception {

        if (s == null) {
            return "";
        }
        s = s.replaceAll("//[^\\r\\n]{0,2000}[\\r\\n]{1,100}", "");
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

        // return StringUtil.setParam(sql,param,v);
    }

    // --------------
    public static Properties getSqlConfigProp() throws Exception {
        if (config != null) {
            return config;
        }
        return getSqlConfigPropInternal();

    }

    // 读取配置文件
    // 配置文件格式 ajf_sql_map(xxx)=select * from x ,xxx表示sql name
    public synchronized static Properties getSqlConfigPropInternal() throws Exception {

        if (config != null) {
            return config;
        }
        InputStream is = null;
        InputStreamReader ir = null;
        StringBuffer sb = new StringBuffer();
        Properties prop = null;
        String msg = null;

        msg = "error when read ajf sql map config file [ WEB-INF/classes" + sql_config_file;
        msg = msg + " ],please check is it exist";
        // System.out.println("1");

        is = c.getResourceAsStream(sql_config_file);
        // System.out.println("2");
        if (is == null) {

            throw new Exception(msg);
        }
        // if(is==null){System.out.println("is is null");}

        ir = new InputStreamReader(is);
        int ch = 0;

        if (ir == null) {

            throw new Exception(msg);
        }

        ch = ir.read();

        // if(ir==null){System.out.println("ir is null");}

        while (ch > 0) {
            // System.out.println(ch);
            sb.append((char) ch);
            ch = ir.read();

        }

        String s = sb.toString();

        s = getConfigString(s);

        sql_config_string = s + "";
        // System.out.println(sql_config_string);
        int i = 0;
        int num = 0;
        String name = null;
        int pos = 0;
        int start = 0;
        int end = 0;
        String[] arr = null;
        String ss = null;
        String sql = null;
        arr = s.split(SEP);
        num = arr.length;
        prop = new Properties();
        for (i = 0; i < num; i++) {

            ss = arr[i];
            pos = ss.indexOf(")=");
            if (pos <= 0) {
                continue;
            }
            name = ss.substring(0, pos);
            if (name == null) {
                continue;
            }
            name = name.trim();
            if (name.length() < 1) {
                continue;
            }
            sql = ss.substring(pos + 2);
            if (StringCheckUtil.isEmpty(sql)) {
                continue;
            }
            sql = sql.trim();
            if (!StringCheckUtil.isEmpty(prop.getProperty(name))) {
                throw new Exception("duplicate sql name["
                        + name + "]");
            }
            prop.setProperty(name, sql);

        }

        return prop;

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
        while (m.find()) {
            paramName = m.group(1);
            // list.add(paramName);
            map.put(paramName, "1");
        }

        // return list;
        return getMapKey(map);
    }

    private static List getMapKey(Map map) {
        // TODO Auto-generated method stub
        ArrayList<String> list = new ArrayList<String>();
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
            // System.out.println(paramName);
            obj = map.get(paramName);
            if (obj == null) {
                v = "";
            } else {
                v = obj + "";
            }

            // s = setParamSingle(s, paramName, (String)map.get(paramName));
            // System.out.println(v);
            // s = StringUtil.setParam(s, paramName, v);
            s = setParamSingle(s, paramName, v);
            // System.out.println(s);
        }

        return s;
    }

    // 根据 <notempty name="xxx">content</notempty> 动态生成 sql
    // 参数xxx不为空时，才输出 content,实现动态sql
    // 目前只支持常见的notempty判断
    // 可使用模板来动态生成sql语句，这样将非常强大，常见的模板工具有 freemark,velocity等
    // 还是比较喜欢简单，就不使用第三方的模板工具了

    public static String getParamSql(String s, Map map) throws Exception {
        if (s == null) {
            return "null";
        }
        if (map == null) {
            return s;
        }

        String ps =

                "<notempty[\\s]{1,100}name=\\\"([0-9A-Za-z._-]{1,50})\\\">([^/]{1,500})</notempty>";
        Pattern p = Pattern.compile(ps);
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        String v = null;
        String rs = null;
        String name = null;

        while (m.find()) {

            name = m.group(1);
            rs = m.group(2);
            v = (String) map.get(name);

            if (StringCheckUtil.isEmpty(v)) {
                m.appendReplacement(sb, "");
            } else {
                m.appendReplacement(sb, rs);

            }

        }
        m.appendTail(sb);
        return sb.toString();

    }

    //

    /**
     * 根据名称获取原始的sql语句
     *
     * @param name
     * @return
     * @throws Exception
     */
    public static String getSqlByName(String name) throws Exception {
        String s = null;
        if (StringCheckUtil.isEmpty(name)) {
            throw new Exception("sql name is empty");
        }
        s = getSqlConfigProp().getProperty(name);
        if (StringCheckUtil.isEmpty(s)) {
            throw new Exception("no sql names [" + name + "]");
        }
        return s;

    }

    /**
     * 根据名称获取sql语句，动态生成，参数替换后的sql,可直接执行
     *
     * @param name
     * @param map
     * @return
     * @throws Exception
     */
    public static String getSqlByName(String name, Map map) throws Exception {
        String sql = null;

        sql = getSqlByName(name);

        sql = getParamSql(sql, map);
        if (map == null) {
            return sql;
        }
        return getSql(sql, map);

    }

    public static String getSql(String s, Map map) throws Exception {
        if (map == null) {
            return s;
        }
        if (map.isEmpty()) {
            return s;
        }
        List list = getParamNames(s);
        return setParam(s, map);
    }

    // 读取配置内容，文件内容 和 sql map 语句
    public static String getSqlConfigContent() throws Exception {
        String s = null;
        Properties prop = getSqlConfigProp();

        //s = PropUtil.dump(prop) + "\n\n\n" + sql_config_string;

        return s;
    }

    public static void query(Connection cn, String name, Map map) throws Exception {
        if (cn == null) {
            throw new Exception("cn is null");
        }

        String sql = null;

        Statement stmt = null;
        ResultSet rs = null;

        sql = getSqlByName(name, map);
        // sql = getParamSql(sql,map);
        // getSql(sql,map);

    }

}
