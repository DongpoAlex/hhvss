package com.royalstone.util.sql;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.jdom.Element;

import com.royalstone.util.Log;
import com.royalstone.util.daemon.XResultAdapter;
import com.royalstone.vss.VSSConfig;

/**
 * 用来执行sql语句的工具类
 * 
 * @author baij
 * 
 */
public class SqlUtil {
	/**
	 * 数据库字符集-转码用
	 */
	final static public String	DB_CODE		= "ISO8859-1";

	/**
	 * 本地字符集-转码用
	 */
	final static public String	LOCAL_CODE	= "GBK";

	/**
	 * 是否对sql结果及语句转码
	 */
	final static public boolean	DECODE		= false;

	static public void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static public void close(ResultSet rs) {
		try {
			if (rs != null) {
				Statement st = rs.getStatement();
				rs.close();
				if (st != null) {
					st.close();
				}
				rs = null;
				st = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static public void executeCall(Connection conn, String sql, Object... objs) {
		log(sql);
		long s = System.currentTimeMillis();
		try {
			CallableStatement cs = conn.prepareCall(sql);
			setPS(cs, objs);
			cs.execute();
			cs.close();
		} catch (SQLException e) {
			throw new DAOException(e);
		}
		log("times:" + (System.currentTimeMillis() - s));
	}

	/**
	 * 执行PS，返回行数
	 * 
	 * @param conn
	 * @param sql
	 * @param list
	 * @return
	 * @throws SQLException
	 */
	static public int executePS(Connection conn, String sql, Object... objs) throws DAOException {
		log(sql);
		long s = System.currentTimeMillis();
		int rows = 0;
		try {
			sql = toLocal(sql);
			PreparedStatement ps = conn.prepareStatement(sql);
			setPS(ps, objs);
			rows = ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			throw new DAOException(e);
		}
		log("times:" + (System.currentTimeMillis() - s));
		return rows;
	}

	/**
	 * 批量执行PS
	 * 
	 * @param conn
	 * @param sql
	 * @param list
	 *            PS值数组
	 * @return
	 */
	static public int[] executeBatchPS(Connection conn, String sql, ArrayList<Object[]> paramsList) {
		log(sql);
		long s = System.currentTimeMillis();
		int[] rows;
		try {
			sql = toLocal(sql);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (Object[] objs : paramsList) {
				setPS(ps, objs);
				ps.addBatch();
			}
			rows = ps.executeBatch();
			ps.close();
		} catch (Exception e) {
			throw new DAOException(e);
		}
		log("times:" + (System.currentTimeMillis() - s));

		return rows;
	}

	static public int executePS(Connection conn, String sql, Object obj) throws DAOException {
		log(sql);
		long s = System.currentTimeMillis();
		int rows = 0;
		try {
			sql = toLocal(sql);
			PreparedStatement ps = conn.prepareStatement(sql);
			setPS(ps, obj);
			rows = ps.executeUpdate();
			ps.close();
		} catch (Exception e) {
			throw new DAOException(e);
		}
		log("times:" + (System.currentTimeMillis() - s));
		return rows;
	}

	/**
	 * 执行更新SQL，返回更新行数
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	static public int executeSQL(Connection conn, String sql) {
		sql = toLocal(sql);
		log(sql);
		long s = System.currentTimeMillis();
		int rows = 0;
		try {
			Statement st = conn.createStatement();
			rows = st.executeUpdate(sql);
			st.close();
		} catch (Exception e) {
			throw new DAOException(e);
		}
		log("times:" + (System.currentTimeMillis() - s));
		return rows;
	}

	/**
	 * 批量执行sql
	 * 
	 * @param conn
	 * @param sqls
	 *            sql数组
	 * @return
	 */
	static public int[] executeBatchSQL(Connection conn, boolean trans, String... sqls) {
		long s = System.currentTimeMillis();
		int[] rows;
		try {
			if (trans) {
				conn.setAutoCommit(false);
			}
			Statement st = conn.createStatement();
			for (String sql : sqls) {
				log(sql);
				sql = toLocal(sql);
				st.addBatch(sql);
			}
			rows = st.executeBatch();
			if (trans) {
				conn.commit();
				conn.setAutoCommit(true);
			}
			st.close();
		} catch (Exception e) {
			if (trans) {
				try {
					conn.rollback();
				} catch (SQLException e1) {}
			}
			throw new DAOException(e);
		}
		log("times:" + (System.currentTimeMillis() - s));
		return rows;
	}

	final static public Connection getConn(String dbname) throws NamingException, SQLException {
		Context initCtx = new javax.naming.InitialContext();
		DataSource ds = (DataSource) initCtx.lookup("java:comp/env/" + dbname);
		Connection conn = ds.getConnection();
		return conn;
	}

	/**
	 * 执行 单一条件 PS SQL查询语句，返回指定行数XML对象
	 * 
	 * @param conn
	 * @param sql
	 * @param keyvalue
	 * @param setname
	 * @param first
	 * @param loadCount
	 * @return
	 * @throws SQLException
	 */
	static public Element getRowSetElement(Connection conn, String sql, Object keyvalue, String setname)
			throws DAOException {
		ResultSet rs = queryPS(conn, sql, keyvalue);
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_set;
		try {
			elm_set = adapter.getRowSetElement(setname, "row");

			// 添加行数统计
			elm_set.setAttribute("row_count", String.valueOf(adapter.rows()));
			log(sql, elm_set);
			close(rs);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return elm_set;
	}

	/**
	 * 执行 PS SQL查询语句，返回XML对象
	 * 
	 * @param conn
	 * @param sql
	 * @param objs
	 * @param setname
	 * @return
	 * @throws SQLException
	 */
	static public Element getRowSetElement(Connection conn, String sql, Object[] objs, String setname)
			throws DAOException {
		Element elm_set;
		try {
			ResultSet rs = queryPS(conn, sql, objs);
			XResultAdapter adapter = new XResultAdapter(rs);
			elm_set = adapter.getRowSetElement(setname, "row");
			// 添加行数统计
			elm_set.setAttribute("row_count", String.valueOf(adapter.rows()));
			log(sql, elm_set);
			close(rs);
		} catch (SQLException e) {
			throw new DAOException(e);
		}
		return elm_set;
	}

	/**
	 * 执行 PS SQL查询语句，返回指定行数XML对象
	 * 
	 * @param conn
	 * @param sql
	 * @param list
	 * @param setname
	 * @param first
	 * @param loadCount
	 * @return
	 * @throws SQLException
	 */
	static public Element getRowSetElement(Connection conn, String sql, Object[] objs, String setname, int first,
			int loadCount) throws DAOException {
		ResultSet rs = queryPS(conn, sql, objs);
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_set;
		try {
			elm_set = adapter.getRowSetElement(setname, "row", first, loadCount);

			// 添加行数统计
			elm_set.setAttribute("row_count", String.valueOf(adapter.rows()));
			log(sql, elm_set);
			close(rs);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return elm_set;
	}

	/**
	 * 执行SQL查询语句，返回XML对象
	 * 
	 * @param conn
	 * @param sql
	 * @param setname
	 * @return
	 * @throws SQLException
	 */
	static public Element getRowSetElement(Connection conn, String sql, String setname) throws DAOException {
		ResultSet rs = querySQL(conn, sql);
		XResultAdapter adapter = new XResultAdapter(rs);
		Element elm_set;
		try {
			elm_set = adapter.getRowSetElement(setname, "row");
			// 添加行数统计
			elm_set.setAttribute("row_count", String.valueOf(adapter.rows()));
			log(sql, elm_set);
			close(rs);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return elm_set;
	}

	/**
	 * 执行SQL查询语句，返回指定行数XML对象
	 * 
	 * @param conn
	 * @param sql
	 * @param setname
	 * @param first
	 * @param loadCount
	 * @return
	 * @throws SQLException
	 */
	static public Element getRowSetElement(Connection conn, String sql, String setname, int first, int loadCount)
			throws DAOException {
		Element elm_set;
		try {
			ResultSet rs = querySQL(conn, sql);

			XResultAdapter adapter = new XResultAdapter(rs);
			elm_set = adapter.getRowSetElement(setname, "row", first, loadCount);

			// 添加行数统计
			elm_set.setAttribute("rows", String.valueOf(adapter.rows()));
			log(sql, elm_set);
			close(rs);
		} catch (SQLException e) {
			throw new DAOException(e);
		}
		return elm_set;
	}

	/**
	 * 全局参数
	 */

	static public void log(String sql) {
		if (VSSConfig.printSQL.equalsIgnoreCase("true"))
			System.out.println(sql);
		if (VSSConfig.logSQL.equalsIgnoreCase("true"))
			Log.event("", sql);
	}

	static public void log(String sql, Element elm) {
		if (VSSConfig.outSQL.equalsIgnoreCase("true") && elm != null) {
			// elm.addContent(new Element("sql").addContent( new CDATA(sql)));
			elm.setAttribute("strsql", sql);
		}
	}

	/**
	 * 查询list为参数列表的PS
	 * 
	 * @param conn
	 * @param sql
	 * @param list
	 * @return
	 * @throws SQLException
	 */
	static public ResultSet queryPS(Connection conn, String sql, Object... objs) throws DAOException {
		log(sql);
		long s = System.currentTimeMillis();
		ResultSet rs;
		try {
			sql = toLocal(sql);
			PreparedStatement ps = conn.prepareStatement(sql);
			setPS(ps, objs);
			rs = ps.executeQuery();
		} catch (Exception e) {
			throw new DAOException(e);
		}
		log("times:" + (System.currentTimeMillis() - s));
		return rs;
	}

	static public ResultSet queryPS(Connection conn, String sql, Object obj) throws DAOException {
		log(sql);
		long s = System.currentTimeMillis();
		ResultSet rs;
		try {
			sql = toLocal(sql);
			PreparedStatement ps = conn.prepareStatement(sql);
			setPS(ps, obj);
			rs = ps.executeQuery();
		} catch (Exception e) {
			throw new DAOException(e);
		}
		log("times:" + (System.currentTimeMillis() - s));
		return rs;
	}

	/**
	 * 返回结果集List，列为String[]
	 * 
	 * @param conn
	 * @param sql
	 * @param keyvalue
	 * @return
	 * @throws DAOException
	 */
	static public List<String[]> queryPS4Column(Connection conn, String sql, Object... objs) throws DAOException {
		List<String[]> list = new ArrayList<String[]>();
		try {
			ResultSet rs = queryPS(conn, sql, objs);
			ResultSetMetaData mt = rs.getMetaData();
			while (rs.next()) {
				String[] os = new String[mt.getColumnCount()];
				for (int i = 0; i < os.length; i++) {
					os[i] = rs.getString(i + 1);
				}
				list.add(os);
			}
			close(rs);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return list;
	}

	static public List<String[]> queryPS4Column(Connection conn, String sql, Object obj) throws DAOException {
		return queryPS4Column(conn, sql, new Object[] { obj });
	}

	/**
	 * 返回查询结果集
	 * 
	 * @param conn
	 * @param sql
	 * @param objs
	 * @return List<HashMap<String, String>>
	 */
	static public List<HashMap<String, String>> queryPS4DataMap(Connection conn, String sql, Object... objs) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			ResultSet rs = queryPS(conn, sql, objs);
			ResultSetMetaData mt = rs.getMetaData();
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < mt.getColumnCount(); i++) {
					map.put(mt.getColumnName(i + 1).toLowerCase(), rs.getString(i + 1));
				}
				list.add(map);
			}
			close(rs);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return list;
	}

	/**
	 * 返回第一行第一列的String类型值
	 * 
	 * @param conn
	 * @param sql
	 * @param keyvalue
	 *            []
	 * @return
	 * @throws DAOException
	 */
	static public List<String> queryPS4SingleColumn(Connection conn, String sql, Object... objs) throws DAOException {
		List<String> list = new ArrayList<String>();
		try {
			ResultSet rs;
			rs = queryPS(conn, sql, objs);
			while (rs.next()) {
				String o = rs.getString(1);
				if (o != null)
					list.add(o);
			}
			close(rs);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return list;
	}

	static public List<String> queryPS4SingleColumn(Connection conn, String sql, Object objs) throws DAOException {
		List<String> list = new ArrayList<String>();
		try {
			ResultSet rs;
			rs = queryPS(conn, sql, objs);
			while (rs.next()) {
				String o = rs.getString(1);
				if (o != null)
					list.add(o);
			}
			close(rs);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return list;
	}

	/**
	 * 执行SQL查询语句，返回结果集
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	static public ResultSet querySQL(Connection conn, String sql) {
		sql = toLocal(sql);
		log(sql);
		long s = System.currentTimeMillis();
		ResultSet rs;
		try {
			Statement st = conn.createStatement();
			rs = st.executeQuery(sql);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		log("times:" + (System.currentTimeMillis() - s));
		return rs;
	}

	static public List<String> querySQL4SingleColumn(Connection conn, String sql) {
		List<String> list = new ArrayList<String>();
		try {
			ResultSet rs;
			rs = querySQL(conn, sql);
			while (rs.next()) {
				String o = rs.getString(1);
				if (o != null)
					list.add(o);
			}
			close(rs);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return list;
	}

	/**
	 * 设定PS参数
	 * 
	 * @param i
	 * @param ps
	 * @param value
	 * @throws SQLException
	 */
	static public void setPS(PreparedStatement ps, int i, Object value) throws DAOException {
		try {

			if (value instanceof String) {
				String str = (String) value;
				log("String:" + i + " _ " + str);
				str = toLocal(str);
				ps.setString(i, str);
			} else if (value instanceof Integer) {
				log("Integer:" + i + " _ " + value);
				ps.setInt(i, ((Integer) value).intValue());
			} else if (value instanceof Long) {
				log("Long:" + i + " _ " + value);
				ps.setLong(i, ((Long) value).longValue());
			} else if (value instanceof Double) {
				log("Double:" + i + " _ " + value);
				ps.setDouble(i, ((Double) value).doubleValue());
			} else if (value instanceof Float) {
				log("Float:" + i + " _ " + value);
				ps.setFloat(i, ((Float) value).floatValue());
			} else if (value instanceof Date) {
				log("Date:" + i + " _ " + value);
				ps.setDate(i, (Date) value);
			} else if (value instanceof java.util.Date) {
				log("Date:" + i + " _ " + value);
				ps.setDate(i, new Date(((java.util.Date) value).getTime()));
			} else if (value instanceof Timestamp) {
				log("Timestamp:" + i + " _ " + value);
				ps.setTimestamp(i, (Timestamp) value);
			} else if (value == null) {
				log("NULL:" + i + " _ " + value);
				ps.setString(i, "");
			} else {
				throw new SQLException("SqlUtil尚未定义的数据类型，请定义。value:" + value);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * 通过List循环设置PS参数
	 * 
	 * @param list
	 * @param ps
	 * @throws SQLException
	 */
	static public void setPS(PreparedStatement ps, Object... objs) throws DAOException {
		for (int j = 0; j < objs.length; j++) {
			Object value = objs[j];
			setPS(ps, j + 1, value);
		}
	}

	/**
	 * 将字符串中转为数据库字符集
	 * 
	 * @param str
	 * @return
	 */
	static public String toLocal(String str) {
		//if (DECODE && str != null && !LOCAL_CODE.equals(DB_CODE)) {
		if (str != null && !LOCAL_CODE.equals(DB_CODE)) {
			try {
				return new String(str.getBytes(LOCAL_CODE), DB_CODE);
			} catch (UnsupportedEncodingException e) {}
		}
		return str;
	}

	/**
	 * 将数据库转为本地字符集
	 * 
	 * @param str
	 * @return
	 */
	static public String fromLocal(String str) {
		//if (DECODE && str != null && !LOCAL_CODE.equals(DB_CODE)) {
		if (str != null && !LOCAL_CODE.equals(DB_CODE)) {	
			try {
				return new String(str.getBytes(DB_CODE), LOCAL_CODE);
			} catch (UnsupportedEncodingException e) {}
		}
		return str;
	}
}
