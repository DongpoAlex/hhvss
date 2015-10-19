/*
 * Created on 2006-11-02
 *
 */
package com.royalstone.vss.admin;

/**
 * 只有成员变量,没有方法, 用于传递数据.
 * @author meng
 */
public class Module {
	
	public Module( int moduleid, int rightid, String modulename, String action )
	{
		this.moduleid 	= moduleid;
		this.rightid  	= rightid;
		this.modulename = modulename;
		this.action 	= action;
	}
	
	final public int moduleid;
	final public int rightid;
	final public String modulename;
	final public String action;
}
