package com.royalstone.vss.outlayapply;

import java.sql.Date;

public class OutlayapplyItem {
	private String sheetid;
	private String shopid;
	private int qty;
	private double cost;
	private Date spStartDate;
	private Date spEndDate;
	private String chargeName;
	private String display;
	private int paymode;
	private int stampflag;
	private String note;
	public String getSheetid() {
		return this.sheetid;
	}
	public void setSheetid(String sheetid) {
		this.sheetid = sheetid;
	}
	public String getShopid() {
		return this.shopid;
	}
	public void setShopid(String shopid) {
		this.shopid = shopid;
	}
	public int getQty() {
		return this.qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public double getCost() {
		return this.cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public Date getSpStartDate() {
		return this.spStartDate;
	}
	public void setSpStartDate(Date spStartDate) {
		this.spStartDate = spStartDate;
	}
	public Date getSpEndDate() {
		return this.spEndDate;
	}
	public void setSpEndDate(Date spEndDate) {
		this.spEndDate = spEndDate;
	}
	public String getChargeName() {
		return this.chargeName;
	}
	public void setChargeName(String chargeName) {
		this.chargeName = chargeName;
	}
	public String getDisplay() {
		return this.display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public int getPaymode() {
		return this.paymode;
	}
	public void setPaymode(int paymode) {
		this.paymode = paymode;
	}
	public int getStampflag() {
		return this.stampflag;
	}
	public void setStampflag(int stampflag) {
		this.stampflag = stampflag;
	}
	public String getNote() {
		return this.note;
	}
	public void setNote(String note) {
		this.note = note;
	}
}
