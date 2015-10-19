package com.royalstone.vss;

import com.royalstone.util.Log;
import com.royalstone.util.aw.ColModelLoader;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.main.base.ClassReg;

import javax.naming.NamingException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class VSSConfig extends Config {
	private static VSSConfig	instance	= null;
	public static String		VSS_DB_MAIN	= "dbmain";
	public String				optHTML		= "";

	static public VSSConfig getInstance() {
		if (instance == null) {
			VSSConfig.instance = new VSSConfig();
			Connection conn = null;
			try {
				conn = SqlUtil.getConn(VSS_DB_MAIN);
				Log.db(conn, "Init", "开始系统初始化!");
				initValue(conn);
				Log.db(conn, "ClassReg", "成功初始化参数");
				initSite(conn);
				toHTMLOption();
				Log.db(conn, "ClassReg", "成功加载站点");
				initModule(conn);
				Log.db(conn, "ClassReg", "成功加载模块");
				ClassReg.getInstance();
				Log.db(conn, "ClassReg", "成功注册服务");
				SqlMapLoader.getInstance();
				Log.db(conn, "Sqlmap", "查询语句加载完成!");
				ColModelLoader.getInstance();
				Log.db(conn, "ColModel", "显示模型加载完成!");
//				MemcachedClient mc = MemCachedManager.getMClientInstance();
//				if(MemCachedManager.enableMem && mc!=null){
//					Log.db(conn, "ColModel", "初始化内存缓存完成!");
//				}
				Log.db(conn, "Init", "系统初始化完成!");
			} catch (NamingException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				SqlUtil.close(conn);
			}
		}
		return instance;
	}

	private VSSConfig() {}

	static private void initValue(Connection conn) throws SQLException {
		String sql = " select valuekey,keyvalue from vssconfig ";
		ResultSet rs = SqlUtil.querySQL(conn, sql);

		while (rs.next()) {
			String key = rs.getString(1);
			String value = rs.getString(2);
			Class<Config> classType = Config.class;
			try {
				Field field = classType.getDeclaredField(key);
				String typeName = field.getType().getSimpleName();

				if ("String".equals(typeName)) {
					value = SqlUtil.fromLocal(value);
					field.set(VSSConfig.instance, value);
				} else if ("int".equals(typeName)) {
					field.set(VSSConfig.instance, Integer.parseInt(value));
				}

				Log.db(conn, "vssconfig", "全局参数定义:" + key + "=" + value);
			} catch (SecurityException e) {
				Log.db(conn, "vssconfig", "无法更新变量!key:" + key + ";value:" + value);
			} catch (NoSuchFieldException e) {
				Log.db(conn, "vssconfig", "无法找到变量!key:" + key + ";value:" + value);
			} catch (IllegalArgumentException e) {
				Log.db(conn, "vssconfig", "无法对应变量!key:" + key + ";value:" + value);
			} catch (IllegalAccessException e) {
				Log.db(conn, "vssconfig", "无法对应变量!key:" + key + ";value:" + value);
			}
		}
		SqlUtil.close(rs);
	}

	static private void initSite(Connection conn) throws SQLException {
		// 读取站点信息
		String sql = "select * from siteconfig order by sid";
		ResultSet rs = SqlUtil.queryPS(conn, sql);
		VSSConfig.instance.siteTable = new Hashtable<Integer, Site>();
		while (rs.next()) {
			Site s = new Site();
			s.setSid(rs.getInt("sid"));
			s.setSiteName(SqlUtil.fromLocal(rs.getString("sitename")));
			s.setDbSrcName(rs.getString("dbsrcname"));
			s.setIsOpen(rs.getInt("isopen"));
			s.setLogo(rs.getString("logo"));
			s.setTitle(SqlUtil.fromLocal(rs.getString("title")));
			s.setBuid(rs.getString("buid"));
			VSSConfig.instance.siteTable.put(s.getSid(), s);
			Log.db(conn, "site", "加载站点：" + s.getSiteName());
		}
		SqlUtil.close(rs);
	}

	static private void initModule(Connection conn) throws SQLException {
		// 读取模块信息
		String sql = "select * from module_list order by moduleid";
		ResultSet rs = SqlUtil.queryPS(conn, sql);

		VSSConfig.instance.moduleTable = new Hashtable<Integer, Module>();
		VSSConfig.instance.modulePathTable = new Hashtable<String, Module>();

		while (rs.next()) {
			Module m = new Module();
			m.setModuleID(rs.getInt("moduleid"));
			m.setModuleName(rs.getString("modulename"));
			m.setRightID(rs.getInt("rightid"));
			m.setRoleTypeID(rs.getInt("roletype"));
			m.setModulePath(rs.getString("action"));
			m.setCmID(rs.getString("cmid"));

			VSSConfig.instance.moduleTable.put(m.getModuleID(), m);
			VSSConfig.instance.modulePathTable.put(m.getModulePath(), m);

		}
		SqlUtil.close(rs);
	}

	public static void toHTMLOption() {
		Set<Integer> set = VSSConfig.instance.siteTable.keySet();
		Iterator<Integer> it = set.iterator();
		while (it.hasNext()) {
			Integer sid = it.next();
			Site site = VSSConfig.instance.siteTable.get(sid);
			//if (site.getIsOpen()) {
				VSSConfig.instance.optHTML = "<option value='" + site.getSid() + "' isopen='" + site.getIsOpen() + "'>"
						+ site.getSiteName() + "</option>" + VSSConfig.instance.optHTML;
			//}
		}
	}
	
	public static String getValue(Connection conn, String key){
		String sql = "select keyvalue from VSSCONFIG where valuekey=?";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, key);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}

	public static String cookInDC(Connection conn) {
		String thridDC = getValue(conn,"thridDC");
		StringBuffer in = new StringBuffer();
		if (thridDC  != null) {
			String[] ss = thridDC.split(",");
			for (int i = 0; i < ss.length; i++) {
				if (ss[i].length() > 0) {
					if (in .length() == 0) {
						in.append("'" + ss[i] + "'");
					} else {
						in.append(",'" + ss[i] + "'");
					}
				}
			}
		}
		return in.toString();
	}
}
