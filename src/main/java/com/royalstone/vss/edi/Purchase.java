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

public class Purchase implements IEdiSheet{
	final private String		folder;
	final private String 		fixName;
	final private String		venderid;
	final private Date			startTouchDate;

	final private Connection	conn;
	private String name; 

	public Purchase(Item item, String venderid, Connection conn) {
		super();
		name = Thread.currentThread().getName();
		this.folder = item.exportFolder;
		this.fixName=name+"_"+item.exportFixName;
		this.startTouchDate = item.startTouchDate;
		this.venderid = venderid;
		this.conn = conn;
	}
	
	public int work() {
		int rows = 0;
		Log.event("EDI."+name+"."+fixName+" vender:"+this.venderid, "START");
		try {
			String[] arr_sheetid;
			do {
				arr_sheetid = scanSheetid();
				
				if (arr_sheetid == null || arr_sheetid.length==0)
					break;
				
				rows+=arr_sheetid.length;
				for (int i = 0; i < arr_sheetid.length; i++) {
					try {
						conn.setAutoCommit(false);
						export(arr_sheetid[i]);
						conn.commit();
					}
					catch (Exception e) {
						e.printStackTrace();
						Log.event("EDI ERR", e.getMessage());
						conn.rollback();
						upStatusForErr(arr_sheetid[i]);
						conn.commit();
					}
					finally {
						conn.setAutoCommit(true);
					}
				}
				Log.event("EDI "+fixName+" vender:"+this.venderid, "EDI.export: " + arr_sheetid.length + "sheets");
			}
			while (arr_sheetid.length > 0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Log.event("EDI."+name+"."+fixName+" vender:"+this.venderid, "END");
		return rows;
	}
	
	public void export( String sheetid ) throws SQLException, IOException, InvalidDataException {
		String fileName = sheetid;
		int logistics = getlogistics(sheetid);
		
		if(logistics!=2){
			//订货通知单
			ArrayList list = getOrderSheetid(sheetid);
			for (Iterator it = list.iterator(); it.hasNext();) {
				String orderid = (String) it.next();
				fileName = orderid;
				// 创建文件
				String filePatch = this.folder + "/" +this.fixName+"_"+ fileName+".txt";
				FileHandle.createFolder(this.folder);
				File file = new File(filePatch);
				if (file.exists()) {
					file.delete();
				}
				else {
					file.createNewFile();
				}
				
				FileHandle.appendLineStringToFile("H;"+getOrderHead(orderid), filePatch, "GBK");
				
				ArrayList items = getOrderItem(orderid);
				for (Iterator its = items.iterator(); its.hasNext();) {
					String out = (String) its.next();
					FileHandle.appendLineStringToFile("D;"+out, filePatch, "GBK");
				}
			}
		}else{
			//直通订单，导出订货审批单
			// 创建文件
			String filePatch = this.folder + "/" +this.fixName+"_"+ fileName+"XD.txt";
			FileHandle.createFolder(this.folder);
			File file = new File(filePatch);
			if (file.exists()) {
				file.delete();
			}
			else {
				file.createNewFile();
			}
			
			FileHandle.appendLineStringToFile("H;"+getHead(sheetid), filePatch, "GBK");
			
			ArrayList list = getItem(sheetid);
			for (Iterator it = list.iterator(); it.hasNext();) {
				String out = (String) it.next();
				FileHandle.appendLineStringToFile("D;"+out, filePatch, "GBK");
			}
			
			list = getOrderSheetid(sheetid);
			for (Iterator it = list.iterator(); it.hasNext();) {
				String orderid = (String) it.next();
			
				ArrayList items = getOrderItem2(orderid);
				for (Iterator its = items.iterator(); its.hasNext();) {
					String out = (String) its.next();
					FileHandle.appendLineStringToFile("S;"+out, filePatch, "GBK");
				}
			}
		}
		
		upStatus(sheetid);
		Log.event("EDI."+name+"."+fixName+" vender:"+this.venderid, "export sheet:" + sheetid);
	}

	private int getlogistics(String sheetid) throws SQLException, InvalidDataException{
		int logistics = 0;
		String sql = " select logistics from  purchase0 where sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			logistics = rs.getInt(1);
		}else {
			Log.event("EDI."+name+"."+fixName+this.venderid, "not this sheet info:" + sheetid);
			throw new InvalidDataException("没有找到需要导出的数据，审批单号为"+sheetid);
		}
		rs.close();
		pstmt.close();
		return logistics;
	}
	/**
	 * 按审批单号导出
	 * @param sheetid
	 * @return
	 * @throws SQLException
	 * @throws InvalidDataException
	 */
	private String getHead( String sheetid) throws SQLException, InvalidDataException {
		String rel = "";

		String sql_head = " select p0.sheetid,p0.logistics,p.destshopid,s.shopname,p0.sgroupid, "
				+ " TO_CHAR(p0.vdeliverdate,'YYYYMMDD') vdeliverdate,TO_CHAR(p0.deadline,'YYYYMMDD') deadline, "
				+ " p0.venderid,v.vendername,p0.purchasetype,p0.paytypeid,p0.note,p0.validdays  " 
				+ " from purchase0 p0 "
				+ " join purchase p on p.refsheetid=p0.sheetid  join purchaseitem i on i.sheetid=p.sheetid "
				+ " left join category c on c.categoryid=p0.sgroupid  join vender v on v.venderid=p0.venderid"
				+ " join shop s on s.shopid=p.destshopid where p0.sheetid=? ";
		
		PreparedStatement pstmt = conn.prepareStatement(sql_head);
		pstmt.setString(1, sheetid);
		pstmt.setFetchSize(1);
		ResultSet rs = pstmt.executeQuery();
		int colsCount = rs.getMetaData().getColumnCount();
		if (rs.next()) {
			for (int i = 1; i <= colsCount; i++) {
				if (i == colsCount) {
					rel += (rs.getString(i) != null ? rs.getString(i).trim() : "");
				}else if(i==1){
					rel += (rs.getString(i) != null ? rs.getString(i).trim()+"XD;" : ";");
				}
				else {
					if (i == 2) {
						int logistics = rs.getInt(i);
						if (logistics == 1) {
							rel += "C" + ";";
						}
						else {
							rel += "D" + ";";
						}
					}
					else {
						rel += (rs.getString(i) != null ? rs.getString(i).trim() : "") + ";";
					}
				}
			}
		}
		else {
			Log.event("EDI."+name+"."+fixName+" vender:"+this.venderid, "not this sheet info:" + sheetid);
			throw new InvalidDataException("没有找到需要导出的数据，审批单号为"+sheetid);
		}
		rs.close();
		pstmt.close();

		return rel+";"+this.fixName;
	}
	
	private ArrayList getOrderSheetid(String sheetid) throws SQLException{
		ArrayList rel = new ArrayList();
		String sql = "select sheetid from purchase where refsheetid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()){
			String orderid = rs.getString(1);
			if(orderid!=null && orderid.length()>0){
				rel.add(orderid.trim());
			}
		}
		
		rs.close();
		pstmt.close();

		return rel;
	}
	private String getOrderHead( String sheetid) throws SQLException, InvalidDataException {
		String rel = "";

		String sql_head = " select p.sheetid,p0.logistics,s.shopid,s.shopname,p0.sgroupid, "
				+ " TO_CHAR(p0.vdeliverdate,'YYYYMMDD') vdeliverdate,TO_CHAR(p0.deadline,'YYYYMMDD') deadline, "
				+ " p0.venderid,v.vendername,p0.purchasetype,p0.paytypeid,p.note,p.validdays  " 
				+ " from purchase p "
				+ " join purchase0 p0 on p.refsheetid=p0.sheetid  join purchaseitem i on i.sheetid=p.sheetid "
				+ " left join category c on c.categoryid=p0.sgroupid  join vender v on v.venderid=p0.venderid"
				+ " join shop s on s.shopid=p.destshopid where p.sheetid=? ";
		
		PreparedStatement pstmt = conn.prepareStatement(sql_head);
		pstmt.setString(1, sheetid);
		ResultSet rs = pstmt.executeQuery();
		int colsCount = rs.getMetaData().getColumnCount();
		if (rs.next()) {
			for (int i = 1; i <= colsCount; i++) {
				if (i == colsCount) {
					rel += (rs.getString(i) != null ? rs.getString(i).trim() : "");
				}
				else {
					if (i == 2) {
						int logistics = rs.getInt(i);
						if (logistics == 1) {
							rel += "C" + ";";
						}
						else {
							rel += "D" + ";";
						}
					}
					else {
						rel += (rs.getString(i) != null ? rs.getString(i).trim() : "") + ";";
					}
				}
			}
		}
		else {
			Log.event("EDI."+name+"."+fixName+" vender:"+this.venderid, "not this sheet info:" + sheetid);
			throw new InvalidDataException("没有找到需要导出的数据，通知单号为:"+sheetid);
		}
		rs.close();
		pstmt.close();

		return rel+";"+this.fixName;
	}
	
	//导出门店明细
	private ArrayList getOrderItem(String sheetid) throws SQLException{
		ArrayList rel = new ArrayList();
		String sql_detail = " select i.goodsid,i.barcode,g.goodsname,g.spec,i.pkgvolume, "
			+ " i.qty,round(i.qty / i.pkgvolume,2) qtyg,i.cost,g.deptid from purchaseitem i  "
			+ " join purchase p on p.sheetid=i.sheetid join goods g on g.goodsid=i.goodsid "
			+ " where p.sheetid=? order by p.shopid,g.deptid,i.goodsid ";
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
	
	//导出门店明细带门店
	private ArrayList getOrderItem2(String sheetid) throws SQLException{
		ArrayList rel = new ArrayList();
		String sql_detail = " select p.shopid,i.goodsid,i.barcode,g.goodsname,g.spec,i.pkgvolume, "
			+ " i.qty,round(i.qty / i.pkgvolume,2) qtyg,i.cost,g.deptid from purchaseitem i  "
			+ " join purchase p on p.sheetid=i.sheetid join goods g on g.goodsid=i.goodsid "
			+ " where p.sheetid=? order by p.shopid,g.deptid,i.goodsid ";
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
	
	//导出商品汇总明细
	private ArrayList getItem(String sheetid) throws SQLException{
		ArrayList rel = new ArrayList();
		String sql_detail = " select i.goodsid,i.barcode,g.goodsname,g.spec,i.pkgvolume, "
			+ " SUM(i.qty),round(sum(i.qty) / i.pkgvolume,2) qtyg,i.cost,g.deptid from purchaseitem i  "
			+ " join purchase p on p.sheetid=i.sheetid join goods g on g.goodsid=i.goodsid "
			+ " where p.refsheetid=? group by i.goodsid,i.barcode,g.goodsname,g.spec,i.pkgvolume,i.cost,g.deptid " +
					" order by i.goodsid  ";
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

	private String[] scanSheetid() throws SQLException {
		String[] arr_tmp = new String[1000];

		String sql_sheetid = " select c.sheetid from cat_order c " +
				" join purchase0 p0 on p0.sheetid=c.sheetid" +
				" where c.edi_status=0 and c.venderid=? and trunc(touchtime)>=? and ROWNUM<=1000";

		PreparedStatement pstmt = conn.prepareStatement(sql_sheetid);
		
		pstmt.setString(1, this.venderid);
		pstmt.setDate(2, this.startTouchDate);
		ResultSet rs = pstmt.executeQuery();

		int rows = 0;
		for (int i = 0; rs.next() && i < arr_tmp.length; i++) {
			String sheetid = rs.getString(1);
			if (sheetid == null)
				break;
			arr_tmp[i] = sheetid.trim();
			rows++;
		}

		String[] arr_sheetid = new String[rows];
		System.arraycopy(arr_tmp, 0, arr_sheetid, 0, rows);

		rs.close();
		pstmt.close();

		return arr_sheetid;
	}

	public void upStatus( String sheetid ) throws SQLException {
		String sql = "update cat_order set edi_status=100 where sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		pstmt.executeUpdate();
		pstmt.close();
	}
	public void upStatusForErr( String sheetid ) throws SQLException {
		String sql = "update cat_order set edi_status=-100 where sheetid=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sheetid);
		pstmt.executeUpdate();
		pstmt.close();
	}
}
