package com.royalstone.email;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Filter;
import com.royalstone.util.daemon.SimpleExcelAdapter;
import com.royalstone.util.daemon.ValueAdapter;

/**
 * 邮件统计员，负责邮件状态的更新，以及统计
 * 
 * @author baibai
 */
public class MailStatistician {

	public MailStatistician(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 设置邮件回复信息
	 * 
	 * @param receiptor
	 * @param mailid
	 * @param remailid
	 * @throws SQLException
	 */
	public void setWriteBack( String receiptor, int mailid, int remailid ) throws SQLException {
		String SQL_UPDATE_REMAIL_MAIL = " update mail_receiptor set backtime=sysdate, ifback=200, backid=? "
				+ " where mailid=? and receiptor=? and ifback=-1 and backid=-1 and backtime is null ";

		PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_REMAIL_MAIL);
		pstmt.setInt(1, remailid);
		pstmt.setInt(2, mailid);
		pstmt.setString(3, receiptor);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * 设置邮件读取时间
	 * 
	 * @param receiptor
	 * @param mailid
	 * @throws SQLException
	 */
	public void setReadTime( String receiptor, int mailid ) throws SQLException {
		String SQL_UPDATE_READTIME_MAIL = " update mail_receiptor set readtime=sysdate,status=1 "
				+ "where mailid=? and receiptor=? and status=0 and readtime is null";

		PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_READTIME_MAIL);
		pstmt.setInt(1, mailid);
		pstmt.setString(2, receiptor);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * 返回所有收到邮件数目 map中是过滤条件
	 * 
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public int getReceCount( Map map ) throws InvalidDataException, SQLException {
		String sql_sel = " select count(*) from mail_receiptor r";
		Filter filter = cookFilter(map);
		String sql_where = (filter.count() > 0) ? " where " + filter.toString() : "";

		PreparedStatement pstmt = conn.prepareStatement(sql_sel + sql_where);
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}

	/**
	 * 所有回复邮件数目
	 * 
	 * @return
	 * @throws InvalidDataException
	 * @throws SQLException
	 */
	public int getBackCount( Map map ) throws InvalidDataException, SQLException {
		String sql_sel = " select count(*) from mail_receiptor r";
		Filter filter = cookFilter(map);
		String sql_where = (filter.count() > 0) ? " where " + filter.toString() + " and ifback<> -1 "
				: " where ifback<> -1 ";

		sql_sel = sql_sel + sql_where;
		PreparedStatement pstmt = conn.prepareStatement(sql_sel);
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}


	public int statisticMailBack( Map map, File file ) throws InvalidDataException, SQLException, IOException {

		String[] title = { "用户ID", "收到邮件数量", "回复邮件数量", "未回复数量", "邮件回复率", "邮件未回复率" };
		Filter filter = cookFilter(map);
		String sql_where = (filter.count() > 0) ? " where " + filter.toString() : "";
		Filter filter1 = cookFilter4Count(map);
		String sql_count_where = " where " + filter1.toString();

		String sql_backcount = " select count(*) from mail_receiptor rr ";
		sql_backcount += sql_count_where + " and rr.receiptor=r.receiptor ";

		Statement stmt = conn.createStatement();

		String sql_sel = " select receiptor,rececount,backcount,TRUNC( rececount-backcount,2 ), "
				+ " TRUNC(backcount/rececount,2), TRUNC(( rececount-backcount )/rececount,2) " + " from " +
				" (select receiptor,count(*) rececount,(" + sql_backcount + " ) backcount from mail_receiptor r "
				+ sql_where + " and r.receiptor=lower(r.receiptor) " + " group by receiptor) "
				+ " order by 6 DESC ";

		PreparedStatement pstmt = conn.prepareStatement(sql_sel);
		ResultSet rs = pstmt.executeQuery();

		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		int rows = excel.cookExcelFile(file, rs, title, "邮件统计");

		rs.close();
		pstmt.close();

		stmt.close();

		return rows;
	}


	public int getMailBackDetailBook( Map map, File file ) throws InvalidDataException, SQLException, IOException {
		String[] title = { "收件人", "发件人", "收件ID", "收件标题", "收件时间", "邮件状态", "回复时间", "回复邮件ID", "回复标题", "回复时间差" };

		final String sql_stat = " SELECT r.receiptor,t.sender, r.mailid, t.title, "
				+ " TO_CHAR(r.recetime, 'YYYY-MM-DD HH24:MI:SS') recetime ,status4mail(r.status) status, "
				+ " TO_CHAR(r.backtime, 'YYYY-MM-DD HH24:MI:SS') backtime, "
				+ " r.backid, tt.title, r.backtime-recetime difftime " + " FROM mail_receiptor r "
				+ " INNER JOIN mail_title t ON ( t.mailid=r.mailid ) "
				+ " LEFT OUTER JOIN mail_title tt ON( tt.mailid=r.backid) ";

		Filter filter = cookFilter(map);
		String sql_where = (filter.count() == 0) ? "" : " WHERE " + filter.toString();

		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql_stat + sql_where);
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		int rows = excel.cookExcelFile(file, rs, title, "邮件回复情况统计");
		rs.close();
		stmt.close();

		return rows;
	}


	/**
	 * 邮件发送统计
	 * 
	 * @param map
	 * @param file
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 * @throws IOException
	 */
	public int getSenderCount( Map map, File file ) throws SQLException, InvalidDataException, IOException {
		String[] ss = null;
		String sql_where = " ";
		ss = (String[]) map.get("min_recetime");
		if (ss != null && ss.length > 0) {
			sql_where = " and trunc(t.sendtime) >= " + ValueAdapter.std2mdy(ss[0]);
		}
		ss = (String[]) map.get("max_recetime");
		if (ss != null && ss.length > 0) {
			sql_where += " and trunc(t.sendtime) <= " + ValueAdapter.std2mdy(ss[0]);
		}

		String[] title = { "发件人", "统计月份", "数量" };
		String sql = " select sender,to_char(t.sendtime,'YYYYMM') as sendtime,count(*) from  mail_title t "
				+ " where LOWER(sender)=sender " + sql_where + " group by sender,to_char(t.sendtime, 'YYYYMM') order by 1,2";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		SimpleExcelAdapter excel = new SimpleExcelAdapter();
		int rows = excel.cookExcelFile(file, rs, title, "邮件发送统计");
		rs.close();
		stmt.close();

		return rows;
	}

	private Filter cookFilter( Map map ) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		// 指定收件人范围
		ss = (String[]) map.get("receiptor_min");
		if (ss != null && ss.length > 0) {
			filter.add(" r.receiptor >= " + ValueAdapter.toString4String(ss[0]));
		}
		ss = (String[]) map.get("receiptor_max");
		if (ss != null && ss.length > 0) {
			filter.add(" r.receiptor >= " + ValueAdapter.toString4String(ss[0]));
		}
		ss = (String[]) map.get("receiptor");
		if (ss != null && ss.length > 0) {
			filter.add(" r.receiptor = " + ValueAdapter.toString4String(ss[0]));
		}

		// 指定日期范围
		ss = (String[]) map.get("min_recetime");
		if (ss != null && ss.length > 0) {
			filter.add(" r.recetime >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("max_recetime");
		if (ss != null && ss.length > 0) {
			filter.add(" r.recetime <= " + ValueAdapter.std2mdy(ss[0]));
		}

		// 指定邮箱类型
		ss = (String[]) map.get("mailtype");
		if (ss != null && ss.length > 0) {
			filter.add(" r.status = " + ValueAdapter.toString4String(ss[0]));
		}

		// 过滤掉已回复的
		ss = (String[]) map.get("noback");
		if (ss != null && ss.length > 0) {
			filter.add(" r.backid = -1 ");
		}

		return filter;
	}

	private Filter cookFilter4Count( Map map ) throws InvalidDataException {
		Filter filter = new Filter();
		String[] ss = null;

		// 指定日期范围
		ss = (String[]) map.get("min_recetime");
		if (ss != null && ss.length > 0) {
			filter.add(" rr.recetime >= " + ValueAdapter.std2mdy(ss[0]));
		}

		ss = (String[]) map.get("max_recetime");
		if (ss != null && ss.length > 0) {
			filter.add(" rr.recetime <= " + ValueAdapter.std2mdy(ss[0]));
		}

		// 过滤掉已回复的
		filter.add(" rr.ifback <> -1 ");
		return filter;
	}

	final private Connection	conn;
}
