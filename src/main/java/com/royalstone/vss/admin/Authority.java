/*
 * Created on 2005-12-9
 *
 */
package com.royalstone.vss.admin;

import java.util.List;

import org.jdom.Element;

import com.royalstone.security.Permission;
import com.royalstone.util.InvalidDataException;

/**
 * @author meng
 *
 */
public class Authority {

	
	public Authority( int roleid, int moduleid )
	{
		this.roleid = roleid;
		this.moduleid = moduleid;
	}

	public Authority( Element elm_au ) throws InvalidDataException
	{
		String strYES = "1";

		String module 	= elm_au.getAttributeValue( "moduleid" );
		if( module == null || module.length() == 0 ) throw new InvalidDataException( "Invalid moduleid:" + module );
		String role 		= elm_au.getAttributeValue( "roleid" );
		if( role == null || role.length() == 0 ) throw new InvalidDataException( "Invalid roleid:" + role );
		
		int id_role = Integer.parseInt(role);
		int id_module = Integer.parseInt(module);
		String read 	= elm_au.getAttributeValue( "read" );
		String edit 	= elm_au.getAttributeValue( "edit" );
		String insert 	= elm_au.getAttributeValue( "insert" );
		String delete 	= elm_au.getAttributeValue( "delete" );
		String print 	= elm_au.getAttributeValue( "print" );
		String check 	= elm_au.getAttributeValue( "check" );
		String confirm 	= elm_au.getAttributeValue( "confirm" );
		String verify 	= elm_au.getAttributeValue( "verify" );
		
		this.moduleid = id_module;
		this.roleid   = id_role;
		this.perm_role = new Permission(0);
		
		if( strYES.equals( read ) ) 	this.perm_role.add( Permission.READ );
		if( strYES.equals( edit ) ) 	this.perm_role.add( Permission.EDIT );
		if( strYES.equals( insert ) ) 	this.perm_role.add( Permission.INSERT );
		if( strYES.equals( delete ) ) 	this.perm_role.add( Permission.DELETE );
		if( strYES.equals( print ) ) 	this.perm_role.add( Permission.PRINT );
		if( strYES.equals( check ) ) 	this.perm_role.add( Permission.CHECK );
		if( strYES.equals( confirm ) ) 	this.perm_role.add( Permission.CONFIRM );
		if( strYES.equals( verify ) ) 	this.perm_role.add( Permission.VERIFY );
	}
	
	public static Authority[] parseList( Element elm_lst ) throws InvalidDataException
	{
		int len = 0;
		List<?> list = elm_lst.getChildren( "authority" );
		if( list != null ) len = list.size();
		
		Authority[] arr_au = new Authority[ len ];
		for( int i=0; i<arr_au.length; i++ ){
			Element elm = (Element) list.get(i);
			Authority au = new Authority( elm );
			arr_au[i] = au;
		}
		return arr_au;
	}
	
	public static Element toElement(Authority[] au_lst)
	{
		Element elm_lst = new Element( "authority_list" );
		for( int i=0; i<au_lst.length; i++ ){
			Element elm = au_lst[i].toElement();
			elm_lst.addContent( elm );
		}
		return elm_lst;
	}
	
	public Element toElement()
	{
		Element elm = new Element( "authority" );
		elm.setAttribute( "roleid",   "" + roleid );
		elm.setAttribute( "moduleid", "" + moduleid );
		elm.setAttribute( "modulename", "" + modulename );
		elm.setAttribute( "rolename", "" + rolename );

		if( perm_module.include(Permission.READ) ){
			String flag = (perm_role.include(Permission.READ)) ? "1" : "0";
			elm.setAttribute( "read", flag );
		}
		
		if( perm_module.include(Permission.EDIT) ){
			String flag = (perm_role.include(Permission.EDIT)) ? "1" : "0";
			elm.setAttribute( "edit", flag );
		}
		
		if( perm_module.include(Permission.INSERT) ){
			String flag = (perm_role.include(Permission.INSERT)) ? "1" : "0";
			elm.setAttribute( "insert", flag );
		}
		
		if( perm_module.include(Permission.DELETE) ){
			String flag = (perm_role.include(Permission.DELETE)) ? "1" : "0";
			elm.setAttribute( "delete", flag );
		}
		
		if( perm_module.include(Permission.PRINT) ){
			String flag = (perm_role.include(Permission.PRINT)) ? "1" : "0";
			elm.setAttribute( "print", flag );
		}
		
		if( perm_module.include(Permission.CHECK) ){
			String flag = (perm_role.include(Permission.CHECK)) ? "1" : "0";
			elm.setAttribute( "check", flag );
		}
		
		if( perm_module.include(Permission.CONFIRM) ){
			String flag = (perm_role.include(Permission.CONFIRM)) ? "1" : "0";
			elm.setAttribute( "confirm", flag );
		}
		
		if( perm_module.include(Permission.VERIFY) ){
			String flag = (perm_role.include(Permission.VERIFY)) ? "1" : "0";
			elm.setAttribute( "verify", flag );
		}
		
		return elm;
	}
	

	public Permission perm_module = new Permission();
	public Permission perm_role   = new Permission();
	public int moduleid = 0;
	public int roleid = 0;
	public String rolename = "";
	public String modulename = "";
}
