package com.royalstone.vss.edi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import com.royalstone.util.Log;
import com.royalstone.util.sql.SqlUtil;

public class Worker extends Thread {
	final Hashtable<String, String>	connTable;
	final int						interval;
	final String					name;

	public Worker(Hashtable<String, String> connTable, int interval, String name) {
		super();
		this.connTable = connTable;
		this.interval = interval;
		this.name = name;
	}

	public void run() {
		super.setName(name);
		Connection conn = null;
		while (true) {
			try {
				Log.event("EDI." + name, "Start……");
				conn = openDataBase(connTable.get("url"), connTable.get("username"), connTable.get("password"),
						connTable.get("driverClassName"));
				ArrayList<String> list = getVenderInfo(conn);
				for (String venderid : list) {
					EDIWork work = new EDIWork(venderid, getItem(conn, venderid), conn);
					work.work();
				}
				Log.event("EDI." + name, "now sleep " + interval + " mm……");
				Thread.sleep(1000 * interval);
			} catch (Exception e) {
				Log.event("EDI." + name + ".ERROR", e.getMessage());
				try {
					Thread.sleep(1000 * interval);
				} catch (InterruptedException e1) {}
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {}
			}
		}
	}

	private ArrayList<Item> getItem(Connection conn, String venderid) throws SQLException {
		String sqlitem = "select * from edi_venderitem where venderid=? order by seqno";
		ArrayList<Item> list = new ArrayList<Item>();
		ResultSet rsi = SqlUtil.queryPS(conn, sqlitem, venderid);
		while (rsi.next()) {
			Item item = new Item();
			item.exportFixName = rsi.getString("exportFixName");
			item.exportFolder = rsi.getString("exportFolder");
			item.importFixName = rsi.getString("importFixName");
			item.importFolder = rsi.getString("importFolder");
			item.seqno = rsi.getString("seqno");
			item.topic = rsi.getString("topic");
			item.venderid = rsi.getString("venderid");
			item.startTouchDate = rsi.getDate("startTouchDate");
			if (item.startTouchDate == null) {
				item.startTouchDate = java.sql.Date.valueOf("1900-01-01");
			}
			try {
				item.fixedTime = rsi.getString("fixedtime");
				item.execTime = rsi.getDate("exectime");
			} catch (Exception e) {}
			list.add(item);
		}
		rsi.close();
		return list;
	}

	static public void setExecTime(Connection conn, Item item) {
		String sql = "update edi_venderitem set exectime = sysdate where venderid=? and seqno=?";
		SqlUtil.executePS(conn, sql, item.venderid, item.seqno);
	}

	private ArrayList<String> getVenderInfo(Connection conn) throws SQLException, ParseException {
		ArrayList<String> list = new ArrayList<String>();
		String sql = "select venderid, startTime, endTime, flag from edi_vender where flag=0 and startTime is not null and startTime<=to_char(sysdate,'HH24:MM') and nvl(endTime,'24:59')>=to_char(sysdate,'HH24:MM')";
		ResultSet rs = SqlUtil.querySQL(conn, sql);
		while (rs.next()) {
			if (rs.getInt("flag") < 0)
				break;
			String venderid = rs.getString("venderid");
			list.add(venderid);
		}

		return list;
	}

	private Connection openDataBase(String dburl, String user, String passwd, String driver) throws SQLException,
			ClassNotFoundException {
		Class.forName(driver);
		Connection connection = DriverManager.getConnection(dburl, user, passwd);
		return connection;
	}

	/**
	 * 判断上次执行的时间是否小于当前日期，小于则返回true
	 * @param item
	 * @return
	 */
	public static boolean checkExecDateTieme(Item item) {
		boolean res = false;
		if (item.fixedTime != null) {
			int now = Integer.parseInt(SDF_HM.format(System.currentTimeMillis()));
			int fixed = Integer.parseInt(item.fixedTime);
			if (now > fixed) {
				// 检查上次执行时间
				if (item.execTime != null) {
					int curDay = Integer.parseInt(SDF_Y4MD.format(System.currentTimeMillis()));
					long execDay = Integer.parseInt(SDF_Y4MD.format(item.execTime.getTime()));
					// 如果上次执行日期< 当前日期则返回ture，认为可以执行
					if (execDay < curDay) {
						res = true;
					}
				}
			}
		}else{
			res = true;
		}
		return res;
	}
	
	public static SimpleDateFormat SDF_HM = new SimpleDateFormat("HHmm");
	public static SimpleDateFormat SDF_Y4MD = new SimpleDateFormat("yyyyMMdd");
}
