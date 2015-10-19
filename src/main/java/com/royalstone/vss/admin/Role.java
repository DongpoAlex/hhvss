/*
 * Created on 2005-12-26
 *
 */
package com.royalstone.vss.admin;

/**
 * Role 只有成员变量,没有方法, 用于传递数据.
 * @author meng
 */
public class Role {

	
	public Role( int roleid, String rolename, String shopid, String note, int roletype )
	{
		this.roleid = roleid;
		this.rolename = rolename;
		this.shopid = shopid;
		this.note = note;
		this.roletype = roletype;
	}
	
	final public int roleid;
	final public String rolename;
	final public String shopid;
	final public String note;
	final public int roletype;
}
