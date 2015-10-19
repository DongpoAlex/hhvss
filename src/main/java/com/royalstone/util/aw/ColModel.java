package com.royalstone.util.aw;

import java.util.ArrayList;
import java.util.List;

/**
 * @author baij AW控件封装类对于 列 定义类
 */
public class ColModel {
	private String	cmid;
	private long	moduleid;
	private String	smid;
	private String  hsmid;
	private String  bsmid;
	private String	title;
	private String	footer;
	private String	note;
	private String	xslView;
	private String	xslPrint;
	private List<ColModelDetail>	cmDetailList;
	private List<ColModelSearch>	searchList;

	public ColModel clone() {
		ColModel res = new ColModel();
		res.setCmid(this.cmid);
		res.setModuleid(this.moduleid);
		res.setSmid(this.smid);
		res.setHsmid(this.hsmid);
		res.setBsmid(this.bsmid);
		res.setTitle(this.title);
		res.setFooter(footer);
		res.setNote(note);
		res.setXslView(xslView);
		res.setXslPrint(xslPrint);
		ArrayList<ColModelDetail> tempList = new ArrayList<ColModelDetail>();
		tempList.addAll(cmDetailList);
		res.setCmDetailList(tempList);
		ArrayList<ColModelSearch> temp2List = new ArrayList<ColModelSearch>();
		temp2List.addAll(searchList);
		res.setSearchList(temp2List);
		return res;
	}

	public String getCmid() {
		return this.cmid;
	}

	public void setCmid(String cmid) {
		this.cmid = cmid;
	}

	public long getModuleid() {
		return this.moduleid;
	}

	public void setModuleid(long moduleid) {
		this.moduleid = moduleid;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFooter() {
		return this.footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public List<ColModelDetail> getCmDetailList() {
		return this.cmDetailList;
	}

	public void setCmDetailList(List<ColModelDetail> cmDetailList) {
		this.cmDetailList = cmDetailList;
	}

	public List<ColModelSearch> getSearchList() {
		return this.searchList;
	}

	public void setSearchList(List<ColModelSearch> searchList) {
		this.searchList = searchList;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getSmid() {
		return this.smid;
	}

	public void setSmid(String smid) {
		this.smid = smid;
	}

	public String getXslView() {
		return this.xslView;
	}

	public void setXslView(String xslView) {
		this.xslView = xslView;
	}

	public String getXslPrint() {
		return this.xslPrint;
	}

	public void setXslPrint(String xslPrint) {
		this.xslPrint = xslPrint;
	}

	public String getHsmid() {
		return this.hsmid;
	}

	public void setHsmid(String hsmid) {
		this.hsmid = hsmid;
	}

	public String getBsmid() {
		return this.bsmid;
	}

	public void setBsmid(String bsmid) {
		this.bsmid = bsmid;
	}
}
