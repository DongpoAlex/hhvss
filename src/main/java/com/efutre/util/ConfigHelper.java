package com.efutre.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;

import com.efutre.config.DBConfig;
import com.efutre.config.HttpConfig;
import com.royalstone.util.sql.SqlUtil;


public class ConfigHelper {
    /**
     * ????????????????
     *
     * @param fpath
     * @return
     * @throws IOException
     */
    static public DBConfig getDBConfig(Properties prop) throws IOException {
        String password = prop.getProperty("password");
        return new DBConfig(prop.getProperty("url"), prop.getProperty("driver"), prop.getProperty("username"),password);
    }

    /**
     * ??????е???????????ü???Hashtable
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    static public Hashtable<String, HttpConfig> getHttpConfigMap(Connection conn) throws SQLException {
        Hashtable<String, HttpConfig> table = new Hashtable<String, HttpConfig>();
        String sql = "select code, name, connpara, ip, port, sender,interval from cwzzconservice";
        ResultSet rs = SqlUtil.querySQL(conn, sql);
        while (rs.next()) {
            String code = rs.getString("code");
            String name = rs.getString("name");
            String uri = rs.getString("connpara");
            String proxyHost = rs.getString("ip");
            int proxyPort = rs.getInt("port");
            String sender = rs.getString("sender");
            int interval = rs.getInt("interval");
            table.put(code, new HttpConfig(code, name, uri, proxyHost, proxyPort, sender, interval));
        }
        return table;
    }


}
