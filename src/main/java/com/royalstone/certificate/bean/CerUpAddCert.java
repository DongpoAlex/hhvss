package com.royalstone.certificate.bean;


public class CerUpAddCert {
	protected String CerCode;
    protected String CerType;
    protected String FrmDate;
    protected String ToDate;
    protected String Spath;
    
	protected String Ipccercode;
    protected String Procode;
    protected String Ipc;
    protected String Ipcmark;
    protected String ipcpath;
    protected byte[] Cerimage;
    protected String Pictype;
    protected String Pentitycode;
    protected String Pentityname;
    protected String Entitycode;
    protected String Entityname;
    
    protected String VenderID;
    
	public String getIpccercode() {
		return this.Ipccercode;
	}
	public void setIpccercode(String ipccercode) {
		this.Ipccercode = ipccercode;
	}
	public String getProcode() {
		return this.Procode;
	}
	public void setProcode(String procode) {
		this.Procode = procode;
	}
	public String getIpcpath() {
		return this.ipcpath;
	}
	public void setIpcpath(String ipcpath) {
		this.ipcpath = ipcpath;
	}
	public byte[] getCerimage() {
		return this.Cerimage;
	}
	public void setCerimage(byte[] cerimage) {
		this.Cerimage = cerimage;
	}
	public String getPictype() {
		return this.Pictype;
	}
	public void setPictype(String pictype) {
		this.Pictype = pictype;
	}
	public String getPentitycode() {
		return this.Pentitycode;
	}
	public void setPentitycode(String pentitycode) {
		this.Pentitycode = pentitycode;
	}
	public String getPentityname() {
		return this.Pentityname;
	}
	public void setPentityname(String pentityname) {
		this.Pentityname = pentityname;
	}
	public String getEntitycode() {
		return this.Entitycode;
	}
	public void setEntitycode(String entitycode) {
		this.Entitycode = entitycode;
	}
	public String getEntityname() {
		return this.Entityname;
	}
	public void setEntityname(String entityname) {
		this.Entityname = entityname;
	}
	public String getCerCode() {
		return this.CerCode;
	}
	public void setCerCode(String cerCode) {
		this.CerCode = cerCode;
	}
	public String getCerType() {
		return this.CerType;
	}
	public void setCerType(String cerType) {
		this.CerType = cerType;
	}
	public String getSpath() {
		return this.Spath;
	}
	public void setSpath(String spath) {
		this.Spath = spath;
	}
	public String getFrmDate() {
		return this.FrmDate;
	}
	public void setFrmDate(String frmDate) {
		this.FrmDate = frmDate;
	}
	public String getToDate() {
		return this.ToDate;
	}
	public void setToDate(String toDate) {
		this.ToDate = toDate;
	}
	public String getIpc() {
		return this.Ipc;
	}
	public void setIpc(String ipc) {
		this.Ipc = ipc;
	}
	public String getIpcmark() {
		return this.Ipcmark;
	}
	public void setIpcmark(String ipcmark) {
		this.Ipcmark = ipcmark;
	}
	public String getVenderID() {
		return this.VenderID;
	}
	public void setVenderID(String venderID) {
		this.VenderID = venderID;
	}
}
