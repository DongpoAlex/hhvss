/**
 * 
 */
package com.royalstone.certificate.bean;

import java.sql.Date;

/**
 * @author BaiJian
 *
 */
public class Certificate {

	private String sheetid;
	
	private String venderid;
	
	private int type;
	
	private int venderType;
	
	private String venderTypeName;
	
	private String contact;
	
	private int categoryid;
	
	private int flag;
	
	private String checker;
	
	private Date checkTime;
	
	private Date submitTime;
	
	public Date getSubmitTime() {
		return this.submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	private String note;

	public String getSheetid() {
		return this.sheetid;
	}

	public void setSheetid( String sheetid ) {
		this.sheetid = sheetid;
	}

	public String getVenderid() {
		return this.venderid;
	}

	public void setVenderid( String venderid ) {
		this.venderid = venderid;
	}

	public int getType() {
		return this.type;
	}

	public void setType( int type ) {
		this.type = type;
	}

	public int getVenderType() {
		return this.venderType;
	}

	public void setVenderType( int venderType ) {
		this.venderType = venderType;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact( String contact ) {
		this.contact = contact;
	}

	public int getCategoryid() {
		return this.categoryid;
	}

	public void setCategoryid( int categoryid ) {
		this.categoryid = categoryid;
	}

	public int getFlag() {
		return this.flag;
	}

	public void setFlag( int flag ) {
		this.flag = flag;
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

	public String getNote() {
		return this.note;
	}

	public void setNote( String note ) {
		this.note = note;
	}

	public String getVenderTypeName() {
		return this.venderTypeName;
	}

	public void setVenderTypeName( String venderTypeName ) {
		this.venderTypeName = venderTypeName;
	}
}
