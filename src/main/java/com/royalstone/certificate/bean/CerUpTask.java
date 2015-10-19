package com.royalstone.certificate.bean;

import java.util.Date;

public class CerUpTask {
	private int taskid;
	private String sheetid;
	private int seq;
	private int flag;
	private String checker;
	private int tries;
	private Date checktime;
	private Date exporttime;
	private String note;
	private String siteName;
	public int getTaskid() {
		return this.taskid;
	}
	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
	public String getSheetid() {
		return this.sheetid;
	}
	public void setSheetid(String sheetid) {
		this.sheetid = sheetid;
	}
	public int getSeq() {
		return this.seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public int getFlag() {
		return this.flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getChecker() {
		return this.checker;
	}
	public void setChecker(String checker) {
		this.checker = checker;
	}
	public int getTries() {
		return this.tries;
	}
	public void setTries(int tries) {
		this.tries = tries;
	}
	public Date getChecktime() {
		return this.checktime;
	}
	public void setChecktime(Date checktime) {
		this.checktime = checktime;
	}
	public Date getExporttime() {
		return this.exporttime;
	}
	public void setExporttime(Date exporttime) {
		this.exporttime = exporttime;
	}
	public String getNote() {
		return this.note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getSiteName() {
		return this.siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
}
