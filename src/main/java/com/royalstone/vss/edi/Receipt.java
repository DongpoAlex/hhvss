package com.royalstone.vss.edi;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.royalstone.certificate.util.FileHandle;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;

public class Receipt implements IEdiSheet {
	final private String		folder;
	final private String		fixName;
	final private String		venderid;
	final private Date			startTouchDate;
	final private Connection	conn;
	private String name; 

	public Receipt(Item item, String venderid, Connection conn) {
		super();
		name = Thread.currentThread().getName();
		this.folder = item.exportFolder;
		this.fixName = name+"_"+item.exportFixName;
		this.startTouchDate = item.startTouchDate;
		this.venderid = venderid;
		this.conn = conn;
	}

	public int work() {
		
		int rows = 0;
		Log.event("EDI."+name+"."+fixName+" vender:" + this.venderid, "START");
		try {
			String[] arr_sheetid;
			do {
				arr_sheetid = scanSheetid();

				if (arr_sheetid == null || arr_sheetid.length == 0) break;

				rows += arr_sheetid.length;
				for (int i = 0; i < arr_sheetid.length; i++) {
					try {
						conn.setAutoCommit(false);
						export(arr_sheetid[i]);
						conn.commit();
					}
					catch (Exception e) {
						e.printStackTrace();
						conn.rollback();
						upStatusForErr(arr_sheetid[i]);
						conn.commit();
					}
					finally {
						conn.setAutoCommit(true);
					}
				}
				Log.event("EDI."+name+"."+fixName+" vender:" + this.venderid, "EDI.export: " + arr_sheetid.length
						+ "sheets");
			}
			while (arr_sheetid.length > 0);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Log.event("EDI."+name+"."+fixName+" vender:" + this.venderid, "END");
		return rows;
	}

	public void export(String sheetid) throws SQLException, IOException, InvalidDataException {
		String fileName = sheetid;

		// 直通订单，导出订货审批单
		// 创建文件
		String filePatch = this.folder + "/" + this.fixName + "_" + fileName + ".txt";
		FileHandle.createFolder(this.folder);
		File file = new File(filePatch);
		if (file.exists()) {
			file.delete();
		} else {
			file.createNewFile();
		}

		FileHandle.appendLineStringToFile(getHead(sheetid), filePatch, "GBK");
		ArrayList list = getItem(sheetid);
		for (Iterator it = list.iterator(); it.hasNext();) {
			String out = (String) it.next();
			FileHandle.appendLineStringToFile(out, filePatch, "GBK");
		}

		upStatus(sheetid);
		Log.event("EDI."+name+"."+fixName+" vender:" + this.venderid, "export sheet:" + sheetid);
	}

	private ArrayList getItem(String sheetid) throws SQLException{
		ArrayList rel = new ArrayList();
		String sql_detail = " select a.goodsid,a.barcode,b.goodsname,a.qty,(a.qty+a.giftqty)*a.cost from receiptitem a " +
				" join goods b on a.goodsid=b.goodsid " +
				" where a.sheetid=? ";
		PreparedStatement pstmt = conn.prepareStatement(sql_detail);
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();
		int colsCount = rs.getMetaData().getColumnCount();
		while (rs.next()) {
			String out = "";
			for (int i = 1; i <= colsCount; i++) {
				if (i == colsCount) {
					out += (rs.getString(i) != null ? rs.getString(i).trim() : "");
				}
				else {
					out += (rs.getString(i) != null ? rs.getString(i).trim() : "") + ";";
				}
			}
			rel.add(out);
		}
		rs.close();
		pstmt.close();
		return rel;
	}

	private String getHead(String sheetid) throws SQLException, InvalidDataException {
		String rel = "";

		String sql_head = " select sheetid,refsheetid,venderid,editdate from receipt where sheetid=? ";

		PreparedStatement pstmt = conn.prepareStatement(sql_head);
		pstmt.setString(1, sheetid);
		pstmt.setFetchSize(1);
		ResultSet rs = pstmt.executeQuery();
		int colsCount = rs.getMetaData().getColumnCount();
		if (rs.next()) {
			for (int i = 1; i <= colsCount; i++) {
				if (i == colsCount) {
					rel += (rs.getString(i) != null ? rs.getString(i).trim() : "");
				} else if (i == 1) {
					rel += (rs.getString(i) != null ? rs.getString(i).trim() + "XD;" : ";");
				} else {
					rel += (rs.getString(i) != null ? rs.getString(i).trim() : "") + ";";
				}
			}
		} else {
			Log.event("EDI."+name+"."+fixName+" vender:" + this.venderid, "not this sheet info:" + sheetid);
			throw new InvalidDataException("没有找到需要导出的数据，单号为" + sheetid);
		}
		rs.close();
		pstmt.close();

		return rel + ";" + this.fixName;
	}

	private String[] scanSheetid() throws SQLException {
		String[] arr_tmp = new String[1000];

		String sql_sheetid = " select c.sheetid from cat_receipt c where c.edi_status=0 and c.venderid=? and trunc(touchtime)>=?";

		PreparedStatement pstmt = conn.prepareStatement(sql_sheetid);

		pstmt.setString(1, this.venderid);
		pstmt.setDate(2, this.startTouchDate);
		ResultSet rs = pstmt.executeQuery();

		int rows = 0;
		for (int i = 0; rs.next() && i < arr_tmp.length; i++) {
			String sheetid = rs.getString(1);
			if (sheetid == null) break;
			arr_tmp[i] = sheetid.trim();
			rows++;
		}

		String[] arr_sheetid = new String[rows];
		System.arraycopy(arr_tmp, 0, arr_sheetid, 0, rows);

		rs.close();
		pstmt.close();

		return arr_sheetid;
	}

	public void upStatus(String sheetid) throws SQLException {
		String sql = "update cat_receipt set edi_status=100 where sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	public void upStatusForErr(String sheetid) throws SQLException {
		String sql = "update cat_receipt set edi_status=-100 where sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		pstmt.executeUpdate();
		pstmt.close();
	}
}
