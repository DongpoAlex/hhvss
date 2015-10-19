package com.royalstone.vss.main;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONObject;

import com.royalstone.security.Permission;
import com.royalstone.security.Token;
import com.royalstone.util.PermissionException;
import com.royalstone.util.sql.SqlFilter;
import com.royalstone.util.sql.SqlUtil;
import com.royalstone.vss.main.base.Sheet;

public class LQConfig extends Sheet {

	public LQConfig(HttpServletRequest request, Connection conn, Token token) {
		super(request, conn, token);
	}

	/**
	 * 默认初始化对账次数
	 */
	int	DEFCOUNT	= 1;

	public SqlFilter cookFilter(Map<String, String[]> map) {
		return new SqlFilter(map).addFilter2String("venderid", "a.venderid", true)
				.addFilter2StringLike("vendername", "d.vendername", true)
				.addFilter2String("buid", "a.buid", true)
				.addFilter2String("payshopid", "b.payshopid", true)
				.addFilter2String("flag", "a.flag", true);
	}

	public Element update() throws Exception {
		Permission p = token.getPermission(3020315);
		if(!p.include(Permission.EDIT)){
			throw new PermissionException("没有编辑权限");
		}
		
		try {
			String s = getPOSTString();
			JSONObject jso = new JSONObject(s);
			int rowCount = (Integer) jso.get("rowCount");
			JSONArray jsNames = jso.getJSONArray("nodeNames");
			HashMap<String, Integer> keyMap = this.parseNames(jsNames);
			int k = 0;
			if (rowCount > 0) {
				conn.setAutoCommit(false);
				JSONArray rowSet = jso.getJSONArray("rowSet");
				String sql = "update venderlqconfig set monthrest=?,monthcount=? where buid=? and venderid=? ";
				String sqlitem = "update venderlqconfigitem set lqcount=?,flag=?,editor=?,editdate=sysdate where buid=? and venderid=? and payshopid=? ";
				for (int i = 0; i < rowSet.length(); i++) {
					JSONArray row = rowSet.getJSONArray(i);
					String monthrest = row.getString(keyMap.get("monthrest"));
					String monthcount = row.getString(keyMap.get("monthcount"));
					String lqcount = row.getString(keyMap.get("lqcount"));
					String flag = row.getString(keyMap.get("flag"));
					String buid = row.getString(keyMap.get("buid"));
					String venderid = row.getString(keyMap.get("venderid"));
					String payshopid = row.getString(keyMap.get("payshopid"));
					String sflag = row.getString(keyMap.get("_sflag"));
					ArrayList<Object[]> list = new ArrayList<Object[]>();
					ArrayList<Object[]> listitem = new ArrayList<Object[]>();
					if ("M".equalsIgnoreCase(sflag)) {
						list.add(new Object[] { monthrest, monthcount, buid, venderid });
						listitem.add(new Object[] { lqcount, flag, token.loginid, buid, venderid, payshopid });
						log(buid, venderid, payshopid, "M", token.loginid);
						k++;
					}
					SqlUtil.executeBatchPS(conn, sql, list);
					SqlUtil.executeBatchPS(conn, sqlitem, listitem);
				}
				conn.commit();
			}
			return new Element("row").addContent(String.valueOf(k));
		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 获取供应商可对账次数
	 * 
	 * @return
	 */
	public Element getVenderLQCount() {
		String buid = this.getParamNotNull("buid");
		String payshopid = this.getParamNotNull("payshopid");
		String venderid = token.getBusinessid();
		if (buid == null || buid.length() == 0) {
			buid = token.getBuid();
		}
		Element res = new Element("result");
		int count = getVenderLQCount(buid, venderid, payshopid);
		if (count == -999) {
			// 如果没有记录，则初始化记录
			initVenderLQConfig(buid, venderid, payshopid);
			count = getVenderLQCount(buid, venderid, payshopid);
		}
		res.addContent(count + "");
		return res;
	}

	/**
	 * 获取供应商可对账次数 (a.monthcount - b.lqcount)
	 * 如果不控制则返回 999
	 * 没有记录则返回-999
	 * @param buid
	 * @param venderid
	 * @param payshopid
	 */
	protected int getVenderLQCount(String buid, String venderid, String payshopid) {
		String sql = " select b.flag,(a.monthcount - b.lqcount) cancount,a.currentmonth,a.monthcount from venderlqconfig a,venderlqconfigitem b where a.buid=b.buid and a.venderid=b.venderid and b.buid=? and b.venderid=? and b.payshopid=? ";
		List<String[]> list = SqlUtil.queryPS4Column(conn, sql, buid, venderid, payshopid);
		if (list.size() == 0) {
			return -999;
		} else {
			String[] ss = list.get(0);
			if ("Y".equals(ss[0])) {
				// 如果月份不同则更新月
				if (!getCurrentMonth().equals(ss[2])) {
					initLQConfigCurrentMonth(buid, venderid);
					return Integer.parseInt(ss[3]);
				} else {
					return Integer.parseInt(ss[1]);
				}
			} else {
				return 999;
			}
		}
	}

	/**
	 * 减少一次对账次数
	 * 
	 * @param buid
	 * @param venderid
	 */
	protected boolean minusLQCount(String buid, String venderid, String payshopid) {
		String sql = " update venderlqconfig set currentcount=currentcount-1 where currentmonth=? and buid=? and venderid=?";
		String sqlitem = " update venderlqconfigitem set lqcount=lqcount-1 where buid=? and venderid=? and payshopid=?";
		boolean res = false;
		res = SqlUtil.executePS(conn, sql, getCurrentMonth(), buid, venderid) == 1 ? true : false;
		res = SqlUtil.executePS(conn, sqlitem, buid, venderid, payshopid) == 1 ? true : false;
		return res;
	}

	/**
	 * 增加一次对账次数
	 * 
	 * @param buid
	 * @param venderid
	 * @param payshopid
	 */
	protected boolean addLQCount(String buid, String venderid, String payshopid) {
		String sql = " update venderlqconfig set currentcount=currentcount+1 where currentmonth=? and buid=? and venderid=?";
		String sqlitem = " update venderlqconfigitem set lqcount=lqcount+1 where buid=? and venderid=? and payshopid=? ";
		boolean res = false;
		res = SqlUtil.executePS(conn, sql, getCurrentMonth(), buid, venderid) == 1 ? true : false;
		res = SqlUtil.executePS(conn, sqlitem, buid, venderid, payshopid) == 1 ? true : false;
		return res;
	}

	/**
	 * 更新月份为当月，同时将对账次数归0
	 * 
	 * @param buid
	 * @param venderid
	 */
	protected void initLQConfigCurrentMonth(String buid, String venderid) {
		String sql = " update venderlqconfig set currentmonth=?,currentcount=0 where buid=? and venderid=? and monthrest='Y' ";
		String sqlitem = " update venderlqconfigitem set lqcount=0 where buid=? and venderid=? ";
		int i = SqlUtil.executePS(conn, sql, getCurrentMonth(), buid, venderid);
		if (i == 1) {
			SqlUtil.executePS(conn, sqlitem, buid, venderid);
		}
	}

	/**
	 * 初始化供应商可对账次数控制表
	 * 
	 * @param buid
	 * @param venderid
	 */
	protected void initVenderLQConfig(String buid, String venderid, String payshopid) {
		String sql = "select 1 from venderlqconfig where buid=? and venderid=?";
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, sql, buid, venderid);
		if (list.size() == 0) {
			sql = "insert into venderlqconfig(buid,venderid,monthrest,monthcount,currentmonth,currentcount,flag,editor,editdate)"
					+ " values(?,?,?,?,?,?,?,?,sysdate)";
			SqlUtil.executePS(conn, sql, buid, venderid, "Y", DEFCOUNT, getCurrentMonth(), 0, "Y", token.loginid);
		}
		sql = "insert into venderlqconfigitem(buid,venderid,payshopid,lqcount,flag,editor,editdate) values(?,?,?,?,?,?,sysdate)";
		SqlUtil.executePS(conn, sql, buid, venderid,payshopid, 0, "Y", token.loginid);
		log(buid, venderid, "A", token.loginid, payshopid);
	}

	/**
	 * 取得当前月
	 * 
	 * @return
	 */
	private String getCurrentMonth() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
		return sf.format(new Date());
	}

	/**
	 * 记录log
	 * 
	 * @param buid
	 * @param payshopid
	 * @param venderid
	 * @param attr
	 * @param loginid
	 */
	private void log(String buid, String venderid, String payshopid, String attr, String loginid) {
		String sql = "insert into venderlqconfiglog "
				+ "   (buid, venderid, payshopid,monthrest, monthcount, currentmonth, lqcount, flag, serialid, modiattr, moditime, modiopid) "
				+ " select a.buid, a.venderid, b.payshopid,a.monthrest, a.monthcount, a.currentmonth, b.lqcount, b.flag, seq_VenderLQConfigLog.nextval,?,sysdate,? "
				+ " from venderlqconfig a, venderlqconfigitem b where a.buid=b.buid and a.venderid=b.venderid and b.buid=? and b.venderid=? and b.payshopid=? ";
		SqlUtil.executePS(conn, sql, attr, loginid, buid, venderid, payshopid);
	}

}
