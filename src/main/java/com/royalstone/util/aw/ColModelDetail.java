package com.royalstone.util.aw;

public class ColModelDetail {
	private String cmid;
	private int seqno;
	private String field;
	private String name;
	private String vtype;
	private String width;
	private String css;
	private int sum;
	private String render;
	private String note;
	public String getField() {
		return this.field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWidth() {
		return this.width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getCss() {
		return this.css;
	}
	public void setCss(String css) {
		this.css = css;
	}
	public int getSum() {
		return this.sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public String getRender() {
		return this.render;
	}
	public void setRender(String render) {
		this.render = render;
	}
	public String getVtype() {
		return this.vtype;
	}
	public void setVtype(String vtype) {
		this.vtype = vtype;
	}
	public int getSeqno() {
		return this.seqno;
	}
	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}
	public String getCmid() {
		return this.cmid;
	}
	public void setCmid(String cmid) {
		this.cmid = cmid;
	}
	public String getNote() {
		return this.note;
	}
	public void setNote(String note) {
		this.note = note;
	}
}
