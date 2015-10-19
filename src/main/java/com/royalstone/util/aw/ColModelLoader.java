package com.royalstone.util.aw;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.daemon.XDaemon;
import com.royalstone.vss.Site;
import com.royalstone.vss.VSSConfig;

public class ColModelLoader {
	static private ColModelLoader							instance;
	/** 存放各个站点 CM信息的Table */
	private Hashtable<Integer, Hashtable<String, ColModel>>	cmsTable	= null;

	static public ColModelLoader getInstance() {
		if (instance == null) {
			ColModelLoader.instance = new ColModelLoader();
			instance.init();
		}
		return ColModelLoader.instance;
	}

	private void init() {
		cmsTable = new Hashtable<Integer, Hashtable<String, ColModel>>();
		Connection conn = null;
		Set<Integer> set = VSSConfig.getInstance().getSiteTable().keySet();
		Iterator<Integer> its = set.iterator();
		while (its.hasNext()) {
			try {
				Integer sid = (Integer) its.next();
				Site site = (Site) VSSConfig.getInstance().getSiteTable().get(sid);
				if (site.getIsOpen()) {
					conn = XDaemon.openDataSource(site.getDbSrcName());
					ColModelDAO dao = new ColModelDAO(conn);
					Hashtable<String, ColModel> cmTable = new Hashtable<String, ColModel>();
					List<ColModel> list = dao.loadColModel();
					for (Iterator<ColModel> it = list.iterator(); it.hasNext();) {
						ColModel cm = (ColModel) it.next();
						cmTable.put(cm.getCmid() + "", cm);
					}
					Log.db(conn, "ColModel", "站点:" + site.getSiteName() + " ColModel加载完成");
					cmsTable.put(sid, cmTable);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NamingException e) {
				e.printStackTrace();
			} finally {
				XDaemon.closeDataSource(conn);
			}

		}
	}

	/**
	 * @param sid
	 *            站点
	 * @param cmid
	 * @return
	 * @throws InvalidDataException
	 */
	public ColModel getCM(int sid, String cmid)  {
		Hashtable<String, ColModel> table = ColModelLoader.getInstance().cmsTable.get(sid);
		ColModel cm = table.get(cmid);
		return cm;
	}

	public void addCM(int sid, ColModel cm) {
		Hashtable<String, ColModel> table = ColModelLoader.getInstance().cmsTable.get(sid);
		table.put(cm.getCmid(), cm);
	}

	private ColModelLoader() {}
}
