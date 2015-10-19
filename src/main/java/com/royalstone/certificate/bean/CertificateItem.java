/**
 * 
 */
package com.royalstone.certificate.bean;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.royalstone.util.InvalidDataException;

/**
 * @author BaiJian
 *
 */
public class CertificateItem {

	private String sheetid;
	
	private String certificateID;
	
	private String certificateName;

	private int ctid;
	
	private int seqno;
	
	private int flag;
	
	private Date expiryDate;
	private Date yearDate;
	
	private String goodsName;
	
	private String barcodeid;
	
	private String note;
	
	private String checker;
	
	private Date checkTime;
	
	private String approvalnum;
	private String papprovalnum;
	private String editor;
	private Date editTime;
	
	public Date getEditTime() {
		return editTime;
	}

	public void setEditTime(Date editTime) {
		this.editTime = editTime;
	}
	public void setEditTime( String editTime ) throws ParseException {
		if(editTime!=null && editTime.length()>0){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			this.editTime = new Date(df.parse(editTime).getTime());
		}
	}

	public String getEditor() {
		return this.editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getChecker() {
		return this.checker;
	}

	public void setChecker( String checker ) {
		this.checker = checker;
	}

	public Date getCheckTime() {
		return this.checkTime;
	}

	public void setCheckTime( Date checkTime ) {
		this.checkTime = checkTime;
	}

	public String getSheetid() {
		return this.sheetid;
	}

	public void setSheetid( String sheetid ) {
		this.sheetid = sheetid;
	}

	public String getCertificateID() {
		return this.certificateID;
	}

	public void setCertificateID( String certificateID ) {
		this.certificateID = certificateID;
	}

	public String getCertificateName() {
		return this.certificateName;
	}

	public void setCertificateName( String certificateName ) {
		this.certificateName = certificateName;
	}

	public int getCtid() {
		return this.ctid;
	}

	public void setCtid( int ctid ) {
		this.ctid = ctid;
	}

	public Date getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate( Date expiryDate ) {
		this.expiryDate = expiryDate;
	}

	public void setExpiryDate( String expiryDate ) throws ParseException, InvalidDataException {
		if(expiryDate!=null && expiryDate.length()>0){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			this.expiryDate = new Date(df.parse(expiryDate).getTime());
		}
	}
	
	public String getGoodsName() {
		return this.goodsName;
	}

	public void setGoodsName( String goodsName ) {
		this.goodsName = goodsName;
	}

	public String getBarcodeid() {
		return this.barcodeid;
	}

	public void setBarcodeid( String barcodeid ) {
		this.barcodeid = barcodeid;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote( String note ) {
		this.note = note;
	}

	public int getSeqno() {
		return this.seqno;
	}

	public void setSeqno( int seqno ) {
		this.seqno = seqno;
	}

	public int getFlag() {
		return this.flag;
	}

	public void setFlag( int flag ) {
		this.flag = flag;
	}

	public Date getYearDate() {
		return this.yearDate;
	}

	public void setYearDate( Date yearDate ) {
		this.yearDate = yearDate;
	}
	
	public void setYearDate( String yearDate ) throws ParseException {
		if(yearDate!=null && yearDate.length()>0){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			this.yearDate = new Date(df.parse(yearDate).getTime());
		}
	}

	public String getApprovalnum() {
		return this.approvalnum;
	}

	public void setApprovalnum(String approvalnum) {
		this.approvalnum = approvalnum;
	}

	public String getPapprovalnum() {
		return this.papprovalnum;
	}

	public void setPapprovalnum(String papprovalnum) {
		this.papprovalnum = papprovalnum;
	}
}
