package com.royalstone.vss;

import java.io.Serializable;

/**
 * 站点信息
 * 
 * @author baijian
 * 
 */
public class Site implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8592350331035131439L;
	private int sid;
	private String siteName;
	private int isOpen;
	private String dbSrcName;
	private String logo;
	private String title;
	private String buid;

	public String getBuid() {
		return buid;
	}

	public void setBuid(String buid) {
		this.buid = buid;
	}

	/**
	 * // 输出xsl文件路径，实现不同站点不同配置
	 * 
	 * @param file
	 * @return
	 */
	public String toXSLPatch(String file) {
		return "../xsl/" + sid + "/" + file;
	}

	public int getSid() {
		return this.sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getSiteName() {
		return this.siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public boolean getIsOpen() {
		return this.isOpen==0?true:false;
	}

	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}

	public String getDbSrcName() {
		return this.dbSrcName;
	}

	public void setDbSrcName(String dbSrcName) {
		this.dbSrcName = dbSrcName;
	}

	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
