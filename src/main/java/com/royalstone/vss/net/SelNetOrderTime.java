package com.royalstone.vss.net;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.component.XComponent;
import com.royalstone.util.sql.SqlUtil;

public class SelNetOrderTime extends XComponent {
	public SelNetOrderTime(Connection conn, Token token, String note, String dccode, String logistics) throws Exception {
		super(token);
		this.conn = conn;
		this.note = note;
		this.dccode = dccode;
		this.logistics = logistics;
		elm_ctrl = cook();
	}

	private Element cook() throws SQLException {
		String in = "";
		if (dccode != null) {
			in += " and dccode ='" + dccode + "'";
		}
		if (logistics != null) {
			in += " and logistics ='" + logistics + "'";
		}

		Element elm_sel = new Element("select");
		Element elm_opt = new Element("option");
		if (this.note != null && note.length() > 0) {
			elm_opt.addContent(this.note);
			elm_opt.setAttribute("value", "");
			elm_sel.addContent(elm_opt);
		}
		String sql = " select dccode,starttime, endtime from netordertime where 1=1 " + in
				+ " order by dccode,starttime ";
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {

			String ctid = rs.getString("starttime") + "," + rs.getString("endtime");
			String ctname = rs.getString("starttime") + "至" + rs.getString("endtime");
			elm_opt = new Element("option");
			elm_opt.setAttribute("value", ctid);
			elm_opt.setAttribute("ctname", ctname);
			if (this.defaultValue != null && this.defaultValue.length() > 0 && this.defaultValue.equals(ctid)) {
				elm_opt.setAttribute("selected", "selected");
			}
			elm_opt.addContent(SqlUtil.fromLocal(ctname));
			elm_sel.addContent(elm_opt);
		}
		rs.close();
		pstmt.close();

		return elm_sel;
	}

	public Element getElmCtrl() {
		return elm_ctrl;
	}

	private HashSet<Integer> getParamDateOfWeek() {
		String sql = " select sunday,monday, tuesday, wednesday, thursday, friday, saturday from netorderparadate where dccode=? and logistics=?";
		List<String[]> list = SqlUtil.queryPS4Column(conn, sql, dccode, logistics);
		HashSet<Integer> set = new HashSet<Integer>();
		if (list.size() > 0) {
			String[] ss = list.get(0);
			for (int i = 0; i < ss.length; i++) {
				String s = ss[i];
				if ("N".equalsIgnoreCase(s)) {
					set.add((i));
				}
			}
		}
		return set;
	}

	/**
	 * 获取可预约日期清单
	 * 当系统时间大于最晚预约时间点，则不可预约第二天
	 * baij 
	 * @return
	 */
	public Element getParamDateList() {
		Element elm_cat = new Element("dateList");
		// 取例外日和日期范围
		String sql = "select orderkfts, stoporderdate,orderlastdate from netorderpara where dccode=?";
		List<String[]> rslist = SqlUtil.queryPS4Column(conn, sql, dccode);
		if (rslist.size() == 0) {
			return elm_cat;
		}
		String tmp = rslist.get(0)[0];
		if (tmp == null || tmp.length() == 0) {
			return elm_cat;
		}
		// 预约天数
		int kfts = Integer.parseInt(tmp);
		if (kfts <= 0) {
			return elm_cat;
		}
		// 停止预约日
		tmp = rslist.get(0)[1];
		tmp = tmp==null?"":tmp;
		String[] ss = tmp.split(",");
		HashSet<String> expDateSet = new HashSet<String>();
		for (int i = 0; i < ss.length; i++) {
			String s = ss[i];
			if (s != null && s.length() > 0) {
				expDateSet.add(s);
				elm_cat.addContent(new Element("expDate").addContent(s));
			}
		}
		// //停止预约日 周日 sunday=0 开始，saturday=6
		HashSet<Integer> weekSet = getParamDateOfWeek();
		for (Integer days : weekSet) {
			elm_cat.addContent(new Element("disabledDays").addContent(days+""));
		}
		
		Calendar cal = Calendar.getInstance();
		//如果当前时间大于最晚预约时间，推后一天，也就是第二天不能预约
		tmp = rslist.get(0)[2];
		if (tmp != null || tmp.length() > 0) {
			tmp = tmp.replace(":", "");
			int lasttime = Integer.parseInt(tmp);
			int currenttime = cal.get(Calendar.HOUR_OF_DAY)*100 + cal.get(Calendar.MINUTE);
			if(currenttime > lasttime){
				cal.add(Calendar.DATE, 1);
			}
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 1; i <= kfts;) {
			cal.add(Calendar.DATE, 1);
			if (i == 1) {
				elm_cat.addContent(new Element("minDate").addContent(df.format(cal.getTime())));
			}
			// 判断日期是否在例外表中
			if (!isExpDate(expDateSet, df.format(cal.getTime()))) {
				// 判断是否在例外周内
				if (weekSet == null || weekSet.size() == 0 || !weekSet.contains(cal.get(Calendar.DAY_OF_WEEK) - 1)) {
					elm_cat.addContent(new Element("dateSet").addContent(df.format(cal.getTime())));
					i++;
				}
			}
			if (i == kfts+1) {
				elm_cat.addContent(new Element("maxDate").addContent(df.format(cal.getTime())));
			}
		}
		return elm_cat;
	}

	/**
	 * 校验日期是否在例外日期中
	 * 
	 * @return 在例外日期中返回 true
	 */
	private boolean isExpDate(HashSet<String> expDateSet, String d) {
		boolean res = false;
		if (expDateSet != null && expDateSet.size() > 0) {
			res = expDateSet.contains(d);
		}
		return res;
	}

	
	String						dccode;
	String						logistics;
	private String				note;
	private String				defaultValue;
	final private Connection	conn;			;
}
