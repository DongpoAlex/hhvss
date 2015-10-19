/**
 * 
 */
package com.royalstone.certificate.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.naming.NamingException;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.Site;
import com.royalstone.vss.VSSConfig;

/**
 * @author BaiJian
 */
public class Config {

	private String			imgPatch;

	private int				fileMaxSize;

	private String			type1Name;

	private String			type2Name;

	private String			type3Name;

	private String			type4Name;

	private String			ifUrl;
	private String			ifUserName;
	private String			ifPassword;
	private String			ifKey;
	
	private String			imageURL;
	
	private String 			sid;
	

	private static HashMap	instanceMap	= null;

	static public Config getInstance(Token token) {
		if (instanceMap == null) {
			instanceMap = new HashMap();
			Hashtable siteTable = VSSConfig.getInstance().getSiteTable();
			Set set = siteTable.keySet();
			for (Iterator it = set.iterator(); it.hasNext();) {
				Integer sid = (Integer) it.next();
				Site site = (Site) siteTable.get(sid);
				instanceMap.put(site.getSid(), new Config(site.getDbSrcName()));
			}
		}
		return (Config) Config.instanceMap.get(token.site.getSid());
	}

	static public Config getInstance(Connection conn,String key) {
		if (instanceMap == null) {
			instanceMap = new HashMap();
		}
		Config res = (Config)instanceMap.get(key);
		if(res == null){
			res = new Config(conn);
			instanceMap.put(key, res);
		}
		return res;
	}
	
	private Config(String dbname) {
		Connection conn = null;
		try {
			conn = SqlUtil.getConn(dbname);
			init(conn);
		}
		catch (NamingException e) {}
		catch (SQLException e) {}
		finally {
			SqlUtil.close(conn);
		}
	}

	private Config(Connection conn) {
		try {
			init(conn);
		}
		catch (SQLException e) {
			
		}
	}

	private void init(Connection conn) throws SQLException {
		this.type1Name = getValue(conn, "Type1Name");
		this.type2Name = getValue(conn, "Type2Name");
		this.type3Name = getValue(conn, "Type3Name");
		this.type4Name = getValue(conn, "Type4Name");
		this.imgPatch = getValue(conn, "ImgPatch");
		String tmp = getValue(conn, "FileMaxSize");
		if (tmp != null && tmp.length() > 0) {
			this.fileMaxSize = Integer.parseInt(tmp);
		} else {
			this.fileMaxSize = 200;
		}
		
		this.ifUrl = getValue(conn, "IFUrl");
		this.ifUserName = getValue(conn, "IFUserName");
		this.ifPassword = getValue(conn, "IFPassword");
		this.ifKey = getValue(conn, "IFKey");
		this.imageURL = getValue(conn, "imageURL");
		this.sid = getValue(conn, "sid");
	}

	static private String getValue(Connection conn, String valueKey) throws SQLException {
		String res = "";
		String sql = "select keyvalue from certificateConfig where valuekey=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, valueKey);
		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			res = SqlUtil.fromLocal(rs.getString("keyvalue"));
		}
		rs.close();
		ps.close();
		return res;
	}

	static public String getTypeName(String type, Token token) throws Exception {
		String title = "";
		if (type == null) { throw new Exception("未知的录入类型！"); }

		Config config = Config.getInstance(token);
		if (type.equals("1")) {
			title = config.type1Name;
		} else if (type.equals("2")) {
			title = config.type2Name;
		} else if (type.equals("3")) {
			title = config.type3Name;
		} else if (type.equals("4")) {
			title = config.type4Name;
		} else {
			throw new Exception("未知的录入类型！");
		}

		return title;
	}

	public String getImgPatch() {
		return this.imgPatch;
	}

	public String getType1Name() {
		return this.type1Name;
	}

	public String getType2Name() {
		return this.type2Name;
	}

	public String getType3Name() {
		return this.type3Name;
	}

	public String getType4Name() {
		return this.type4Name;
	}

	public int getFileMaxSize() {
		return this.fileMaxSize;
	}

	public String getIfUrl() {
		return this.ifUrl;
	}

	public String getIfUserName() {
		return this.ifUserName;
	}

	public String getIfPassword() {
		return this.ifPassword;
	}

	public String getIfKey() {
		return this.ifKey;
	}

	public String getImageURL() {
		return this.imageURL;
	}

	public String getSid() {
		return this.sid;
	}

}
