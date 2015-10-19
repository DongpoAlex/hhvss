package com.royalstone.vss.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.royalstone.util.InvalidDataException;
import com.royalstone.util.daemon.Values;
import com.royalstone.util.sql.SqlUtil;

public class PaymentsheetManager {

	public PaymentsheetManager(Connection conn) {
		this.conn = conn;
	}

	public int confirmSheet(Map map,String venderid) throws SQLException, InvalidDataException {
		int rows = 0;
		String[] arr_sheet = (String[]) map.get("sheetid");
		if (checkSheetVender(venderid, arr_sheet))
			for (int i = 0; i < arr_sheet.length; i++)
				rows += add(arr_sheet[i]);
		return rows;
	}

	/**
	 * 检查是否为供应商单
	 * @param venderid
	 * @param arr_sheet
	 * @return
	 * @throws SQLException
	 */
	private boolean checkSheetVender(String venderid, String[] arr_sheet)
			throws SQLException {
		int wrong_sheets = 0;
		Values arr_val = new Values(arr_sheet);
		String sql_chk = " SELECT sheetid, venderid FROM paymentsheet "
				+ " WHERE sheetid IN ( " + arr_val.toString4String() + " ) ";
		PreparedStatement pstmt = conn.prepareStatement(sql_chk);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String vid = rs.getString("venderid");
			if (!vid.equals(venderid))
				wrong_sheets++;
		}
		rs.close();
		pstmt.close();
		return (wrong_sheets == 0);
	}

	/**
	 * 提交申请用，状态直接为10
	 * @param sheetid
	 * @throws SQLException
	 */
	private int add(String sheetid) throws SQLException{
		String sqlUp="update paymentnotevenderask set reqdate=sysdate,reqflag=1 where sheetid=?";
		
		String sqlIns="insert into paymentnotevenderask(sheetid,reqflag,reqdate) values(?,1,sysdate)";
		
		//先更新，更新失败，直接插入
		
		int rows = SqlUtil.executePS(conn, sqlUp, new String[]{sheetid});
		
		if(rows==0){
			rows = SqlUtil.executePS(conn, sqlIns, new String[]{sheetid});
		}
		
		return rows;
	}

	final private Connection conn;
}
