package com.royalstone.vss.gate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.jdom.Element;

import com.royalstone.security.Token;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlMapLoader;
import com.royalstone.util.sql.SqlUtil;

/**
 * 此模块用于统计供应商用户的待处理任务数量
 * 
 * @author meng
 * 
 * @modification 白剑 应林剑要求将：待确认订货审批单 －》 待阅读订货审批单 待确认退货通知单 －》 待阅读退货通知单
 *               增加待阅读群发消息和待阅读邮件（最新邮件）
 */
public class TaskPending {

	public TaskPending(Connection conn, String[] arr_vender, Token token) throws SQLException {
		this.conn = conn;
		this.venders = arr_vender;
		this.token = token;
		this.sid=token.site.getSid();
	}

	/**
	 * 此方法用于统计待阅读订货通知单数量
	 * 
	 * @return
	 * @throws SQLException
	 */
	private int getTaskOrder() throws SQLException {
		int count = 0;
		Values val_vender = new Values(venders);
		String sql = " SELECT COUNT(*) FROM cat_order   WHERE status = 0   AND venderid IN ( "
				+ val_vender.toString4String() + " ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}

	/**
	 * 此方法用于统计待阅读退货通知单数量
	 * 
	 * @return
	 * @throws SQLException
	 */
	private int getTaskRet() throws SQLException {
		int count = 0;
		Values val_vender = new Values(venders);
		String sql = " SELECT COUNT(*) FROM cat_retnotice   WHERE status = 0   AND venderid IN ( "
				+ val_vender.toString4String() + " ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}

	/**
	 * 此方法用于统计待开发票的付款申请单数量
	 * 
	 * @return
	 * @throws SQLException
	 */
	private int getTaskInvoice() throws SQLException {
		int count = 0;
		Values val_vender = new Values(venders);
		String sql = "SELECT count(*)   FROM paymentnote0 pn "
				+ " LEFT OUTER JOIN venderinvoice vi ON ( vi.refsheetid = pn.sheetid) "
				+ " WHERE pn.flag=2 AND pn.venderid IN ( " + val_vender.toString4String()
				+ " ) AND ( vi.flag IS NULL OR vi.flag=0 )";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}
	
	private int getMALLTaskInvoice() throws SQLException {
		int count = 0;
		Values val_vender = new Values(venders);
		String sql = "SELECT count(*) FROM paymentsheet0 pn "
				+ " LEFT OUTER JOIN venderinvoice vi ON ( vi.refsheetid = pn.sheetid) "
				+ " WHERE pn.flag=2 AND pn.venderid IN ( " + val_vender.toString4String()
				+ " ) AND ( vi.flag IS NULL OR vi.flag=0 )";
		System.out.println(sql);
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}

	/**
	 * 统计登录供应商尚未下载的有效公文数
	 * 
	 * @return
	 * @throws SQLException
	 */
	private int getNoteCount() throws SQLException {
		int count = 0;
		Values val_vender = new Values(venders);
		String sql = " SELECT t.noteid, g.venderid FROM noteboard_title t "
				+ " LEFT OUTER JOIN noteboard_log g ON ( t.noteid = g.noteid AND g.rolename = 'VENDER' "
				+ " AND g.venderid IN ( " + val_vender.toString4String() + " ) ) "
				+ " WHERE trunc(expiredate) >= trunc(sysdate) ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String venderid = rs.getString("venderid");
			if (venderid == null || venderid.length() == 0) count++;
		}
		rs.close();
		pstmt.close();

		return count;
	}

	/**
	 * 未阅读的邮件数目
	 * 
	 * @return
	 * @throws SQLException
	 */
	private int getMailCount() throws SQLException {
		int count = 0;
		Values val_vender = new Values(venders);
		String sql = " select  count(*)   from  mail_receiptor   where status=0 AND receiptor IN ( "
				+ val_vender.toString4String() + " ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}

	/**
	 * 带阅读群发消息
	 * 
	 * @return
	 * @throws SQLException
	 */
	private int getVenderMsgCount() throws SQLException {
		int count = 0;
		Values val_vender = new Values(venders);
		String sql = " select count(*) from vendermsgitem vi   join vendermsg v on (vi.msgid = v.msgid) "
				+ " where vi.venderid in (" + val_vender.toString4String() + ") "
				+ " and trunc(v.expiredate) >=trunc(sysdate)   and vi.msgid not in ( "
				+ " select distinct msgid from vendermsg_log where venderid in ("
				+ val_vender.toString4String() + "  ) )";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) count = rs.getInt(1);
		rs.close();
		pstmt.close();
		return count;
	}

	/**
	 * 退货预警
	 * 
	 * @param status
	 * @return
	 * @throws SQLException
	 */
	public int getWarningRetCount(int status) throws SQLException {
		int num = 0;
		Values val_vender = new Values(venders);
		String sql = "SELECT count(*) FROM cat_retnotice   WHERE   warnstatus = ? " + " AND venderid IN ("
				+ val_vender.toString4String() + ") ";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, status);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) num = rs.getInt(1);
		rs.close();
		pstmt.close();
		return num;
	}

	/**
	 * 证照预警
	 * 
	 * @return
	 * @throws SQLException
	 * @throws SQLException
	 */
	private int getCertificateCount(int flag) throws SQLException {
		int num = 0;
		Values val_vender = new Values(venders);
		String sql = " select count(*) from certificate c "
				+ " join certificateitem i on i.sheetid=c.sheetid   where c.venderid IN ("
				+ val_vender.toString4String() + ") and i.flag=?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, flag);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) num = rs.getInt(1);
		rs.close();
		pstmt.close();
		return num;
	}
	
	/**
	 * 休假日填写预警
	 * 
	 * @return
	 * @throws SQLException
	 * @throws SQLException
	 */
	private int getHolidayWarn() throws SQLException {
		int num = 0;
		Values val_vender = new Values(venders);
		String sql = " select count(*) from vender_holidayexp a "
				+ " left join ( " +
						" select tb.htype,tb.updatetime from vender_holiday ta " +
						" join vender_holidayitem tb on ta.venderid=tb.venderid " +
						" where ta.venderid IN ("+val_vender.toString4String()+")) b on a.htype=b.htype " +
						" where trunc(sysdate) between a.startdate-45 and a.startdate-21 and (b.htype is null or b.updatetime<a.startdate-45) ";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) num = rs.getInt(1);
		rs.close();
		pstmt.close();
		return num;
	}
	
	private int getPrePayWarn() throws SQLException {
		int num = 0;
		String venderid =token.getBusinessid();
		List<String> list = SqlUtil.queryPS4SingleColumn(conn, SqlMapLoader.getInstance().getSql(sid, "9000103002").toString(), venderid ,venderid,venderid);
		if(list.size()>0){
			num = Integer.parseInt(list.get(0));
		}
		return num;
	}
	
	public Element toElement() throws SQLException {
		int row = 0;
		elm_cat = new Element("catalogue");
		Element elm_row;
		
		row = getNoteCount();
		if(row>0){
			elm_row = new Element("row");
			elm_row.setAttribute("moduleid", "5020102");
			elm_row.setAttribute("taskname", "待下载的有效公文");
			elm_row.setAttribute("g_status", "0");
			elm_row.setAttribute("tasks", "" + row);
			elm_cat.addContent(elm_row);
		}
		
		row = getVenderMsgCount();
		if(row>0){
			elm_row = new Element("row");
			elm_row.setAttribute("moduleid", "5030102");
			elm_row.setAttribute("taskname", "待下载的群消息");
			elm_row.setAttribute("g_status", "0");
			elm_row.setAttribute("tasks", "" + row);
			elm_cat.addContent(elm_row);
		}
		
		row = getMailCount();
		if(row>0){
			elm_row = new Element("row");
			elm_row.setAttribute("moduleid", "5010102");
			elm_row.setAttribute("taskname", "未阅读邮件");
			elm_row.setAttribute("g_status", "0");
			elm_row.setAttribute("tasks", "" + row);
			elm_cat.addContent(elm_row);
		}

		row = getCertificateCount(0);
		if(row>0){
			elm_row = new Element("row");
			elm_row.setAttribute("moduleid", "8000004");
			elm_row.setAttribute("taskname", "待提交证照");
			elm_row.setAttribute("g_status", "0");
			elm_row.setAttribute("tasks", "" + row);
			elm_cat.addContent(elm_row);
		}

		row = getCertificateCount(-1);
		if(row>0){
			elm_row = new Element("row");
			elm_row.setAttribute("moduleid", "8000004");
			elm_row.setAttribute("taskname", "审核返回证照");
			elm_row.setAttribute("g_status", "-1");
			elm_row.setAttribute("tasks", "" + row);
			elm_cat.addContent(elm_row);
		}

		row = getCertificateCount(-10);
		if(row>0){
			elm_row = new Element("row");
			elm_row.setAttribute("moduleid", "8000004");
			elm_row.setAttribute("taskname", "证照年审预警");
			elm_row.setAttribute("g_status", "-10");
			elm_row.setAttribute("tasks", "" + row);
			elm_cat.addContent(elm_row);
		}

		row = getCertificateCount(-10);
		if(row>0){
			elm_row = new Element("row");
			elm_row.setAttribute("moduleid", "8000004");
			elm_row.setAttribute("taskname", "证照有效期预警");
			elm_row.setAttribute("g_status", "-11");
			elm_row.setAttribute("tasks", "" + row);
			elm_cat.addContent(elm_row);
		}

		row = getCertificateCount(-100);
		if(row>0){
			elm_row = new Element("row");
			elm_row.setAttribute("moduleid", "8000004");
			elm_row.setAttribute("taskname", "已过期证照");
			elm_row.setAttribute("g_status", "-100");
			elm_row.setAttribute("tasks", "" + row);
			elm_cat.addContent(elm_row);
		}
		
		if(sid!=11){
			row = getTaskInvoice();
			if(row>0){
				elm_row = new Element("row");
				elm_row.setAttribute("moduleid", "3020105");
				elm_row.setAttribute("taskname", "待开发票付款申请单");
				elm_row.setAttribute("g_status", "0");
				elm_row.setAttribute("tasks", "" + row);
				elm_cat.addContent(elm_row);
			}
			row = getTaskOrder();
			if(row>0){
				elm_row = new Element("row");
				elm_row.setAttribute("moduleid", "3010110");
				elm_row.setAttribute("taskname", "待阅读订货审批单");
				elm_row.setAttribute("g_status", "0");
				elm_row.setAttribute("tasks", "" + row);
				elm_cat.addContent(elm_row);
			}

			row = getTaskRet();
			if(row>0){
				elm_row = new Element("row");
				elm_row.setAttribute("moduleid", "3010131");
				elm_row.setAttribute("taskname", "待阅读退货通知单");
				elm_row.setAttribute("g_status", "0");
				elm_row.setAttribute("tasks", "" + row);
				elm_cat.addContent(elm_row);
			}
			
			row = getHolidayWarn();
			if(row>0){
				elm_row = new Element("row");
				elm_row.setAttribute("moduleid", "3050002");
				elm_row.setAttribute("taskname", "休假计划维护");
				elm_row.setAttribute("g_status", "0");
				elm_row.setAttribute("tasks", "" + row);
				elm_cat.addContent(elm_row);
			}
			
			int retCount7 = getWarningRetCount(90);
			int retCount14 = getWarningRetCount(91);
			if (retCount7 > 0) {
				elm_row = new Element("warning");
				elm_row.setAttribute("moduleid", "3010131");
				elm_row.setAttribute("status", "90");
				elm_row.setAttribute("warningname", retCount7 + "预警状态退货通知单");
				elm_cat.addContent(elm_row);
			}
			if (retCount14 > 0) {
				elm_row = new Element("warning");
				elm_row.setAttribute("moduleid", "3010131");
				elm_row.setAttribute("status", "91");
				elm_row.setAttribute("warningname", retCount14 + "份过期状态退货通知单");
				elm_cat.addContent(elm_row);
			}
		}else{
			row = getPrePayWarn();
			if(row>0){
				elm_row = new Element("row");
				elm_row.setAttribute("moduleid", "9000103");
				elm_row.setAttribute("taskname", "预收款预警");
				elm_row.setAttribute("g_status", "0");
				elm_row.setAttribute("tasks", "" + getPrePayWarn());
				elm_cat.addContent(elm_row);
			}
			
			row = getMALLTaskInvoice();
			if(row>0){
				elm_row = new Element("row");
				elm_row.setAttribute("moduleid", "3020105");
				elm_row.setAttribute("taskname", "待开发票付款申请单");
				elm_row.setAttribute("g_status", "0");
				elm_row.setAttribute("tasks", "" + row);
				elm_cat.addContent(elm_row);
			}
		}
		return elm_cat;
	}

	final private Connection	conn;
	final private Token			token;
	final private int 			sid;
	private Element				elm_cat	= new Element("catalogue");
	private String[]			venders;
}
