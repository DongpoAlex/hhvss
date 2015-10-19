package com.royalstone.vss;

import java.io.Serializable;

public class Module implements Serializable{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2892072191158991811L;
	
	private int		moduleID;
	private String	moduleName;
	private int		rightID;
	private String	modulePath;
	private int		roleTypeID;
	private String	cmID;

	public int getModuleID() {
		return this.moduleID;
	}

	public void setModuleID(int moduleID) {
		this.moduleID = moduleID;
	}

	public String getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public int getRightID() {
		return this.rightID;
	}

	public void setRightID(int rightID) {
		this.rightID = rightID;
	}

	public String getModulePath() {
		return this.modulePath;
	}

	public void setModulePath(String modulePath) {
		this.modulePath = modulePath;
	}

	public int getRoleTypeID() {
		return this.roleTypeID;
	}

	public void setRoleTypeID(int roleTypeID) {
		this.roleTypeID = roleTypeID;
	}

	public String getCmID() {
		return this.cmID;
	}

	public void setCmID(String cmID) {
		this.cmID = cmID;
	}
}
