package com.royalstone.vss.edi;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import com.royalstone.certificate.util.FileHandle;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.sql.SqlUtil;

public class Shop implements IEdiSheet {
	final private String		venderid;
	final private Connection	conn;
	final private String		name;
	final private Item			item;
	final private String		exportFixName;

	public Shop(Item item, String venderid, Connection conn) {
		super();
		name = Thread.currentThread().getName();
		this.item = item;
		this.exportFixName = name + "_" + item.exportFixName;
		this.venderid = venderid;
		this.conn = conn;
	}

	public int work() {
		int rows = 0;
		if(!Worker.checkExecDateTieme(item)){
			return 0;
		}
		Log.event("EDI." + name + "." + exportFixName + " vender:" + this.venderid, "START");
		try {
			try {
				conn.setAutoCommit(false);
				export(this.venderid);
				//toBAK(this.venderid);
				Worker.setExecTime(conn, item);
				conn.commit();
			} catch (Exception e) {
				e.printStackTrace();
				conn.rollback();
				conn.commit();
			} finally {
				conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.event("EDI." + name + "." + exportFixName + " vender:" + this.venderid, "ERR:" + e.getMessage());
		}
		Log.event("EDI." + name + "." + exportFixName + " vender:" + this.venderid, "END");
		return rows;
	}

	public void export(String venderid) throws SQLException, IOException, InvalidDataException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		
		String fileName = "CRV-"+venderid+"-"+Worker.SDF_Y4MD.format(cal.getTime())+"-StoreTable.txt";
		// 创建文件
		String filePatch = item.exportFolder + "/" + fileName;
		FileHandle.createFolder(item.exportFolder);
		File file = new File(filePatch);
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}

		ArrayList<String> list = getItem(venderid);
		for (Iterator<String> it = list.iterator(); it.hasNext();) {
			String out = (String) it.next();
			FileHandle.appendLineStringToFile(out, filePatch, "GBK");
		}
	}

	private ArrayList<String> getItem(String sheetid) throws SQLException {
		ArrayList<String> rel = new ArrayList<String>(1000);
		String sql_detail = " select shopid, shopname, yingyid, yingyarea, startdate, shop_type, dc_shopid, dc_shopname, flag from edi_shop_bg order by shopid";
		PreparedStatement pstmt = conn.prepareStatement(sql_detail);
		ResultSet rs = pstmt.executeQuery();
		int colsCount = rs.getMetaData().getColumnCount();
		while (rs.next()) {
			String out = "";
			for (int i = 1; i <= colsCount; i++) {
				if (i == colsCount) {
					out += (rs.getString(i) != null ? rs.getString(i).trim() : "");
				} else {
					out += (rs.getString(i) != null ? rs.getString(i).trim() : "") + ";";
				}
			}
			rel.add(out);
		}
		rs.close();
		pstmt.close();
		return rel;
	}

	private void toBAK(String venderid) {
		String sql = "delete from edi_shop_bg where 1=1 ";
		SqlUtil.executeSQL(conn, sql);
	}
}
