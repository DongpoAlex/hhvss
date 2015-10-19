package com.royalstone.vss.edi;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.royalstone.certificate.util.FileHandle;
import com.royalstone.util.InvalidDataException;
import com.royalstone.util.Log;
import com.royalstone.util.sql.SqlUtil;

public class SaleDaily implements IEdiSheet {
	final private String		venderid;
	final private Connection	conn;
	final private String		name;
	final private Item			item;
	final private String		exportFixName;

	public SaleDaily(Item item, String venderid, Connection conn) {
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
					toBAK(this.venderid);
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
		List<String> list = getDateList();
		for (String date : list) {
			String fileName = "CRV-"+venderid+"-"+date+"-PGSales.txt";
			// 创建文件
			String filePatch = item.exportFolder + "/" + fileName;
			FileHandle.createFolder(item.exportFolder);
			File file = new File(filePatch);
			if (file.exists()) {
				file.delete();
			} else {
				file.createNewFile();
			}
			exportItem(venderid,filePatch,date);
		}
	}
	
	private List<String> getDateList(){
		String sql = "SELECT DISTINCT to_char(sdate,'yyyyMMdd') FROM EDI_DAILYSALERB_BG";
		return SqlUtil.querySQL4SingleColumn(conn, sql);
	}

	private void exportItem(String sheetid,String filePatch, String date) throws SQLException {
		String sql_detail = " select a.venderid,a.barcodeid,a.goodsid,a.shopid,a.cost,a.price,to_char(a.sdate,'yyyyMMdd') sdate,a.disctype,a.salevalue,a.maoli,a.saleqty,a.stockqty,a.stockcostv,a.dmid,a.tuan,a.status "
				+ " from EDI_DAILYSALERB_BG a  where a.venderid=?  and sdate=to_date(?,'yyyyMMdd') order by a.sdate,a.shopid,a.barcodeid";
		PreparedStatement pstmt = conn.prepareStatement(sql_detail);
		pstmt.setString(1, this.venderid);
		pstmt.setString(2, date);
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
			FileHandle.appendLineStringToFile(out, filePatch, "GBK");
		}
		rs.close();
		pstmt.close();
	}

	private void toBAK(String venderid) {
		String sql0 = "delete from EDI_DAILYSALERB_BG_BAK  a where  a.sdate < sysdate-7 and a.venderid='" + venderid  + "' ";
		String sql1 = "delete from EDI_DAILYSALERB_BG_BAK  a where (a.sdate,a.venderid) in (select sdate,venderid from EDI_DAILYSALERB_BG where venderid='"+venderid+"')";
		String sql2 = "insert into EDI_DAILYSALERB_BG_BAK select * from EDI_DAILYSALERB_BG where venderid='" + venderid  + "' ";
		String sql3 = "delete from EDI_DAILYSALERB_BG where venderid='" + venderid + "'  ";
		SqlUtil.executeBatchSQL(conn, false, sql0, sql1, sql2,sql3);
	}
}
