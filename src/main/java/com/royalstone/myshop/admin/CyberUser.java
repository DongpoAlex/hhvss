package com.royalstone.myshop.admin;

import org.jdom.Element;

public 
/**
 * CyberUser 表示用户信息.
 * @author meng
 *
 */
class CyberUser 
{
	
	public String toString()
	{
		return "";
	}

	public CyberUser( Element elm_user )
	{
		String str 		= elm_user.getChildTextTrim( "userid" );
		userid 			= Integer.parseInt( str );
		username 		= elm_user.getChildTextTrim( "username" );
		shopid			= elm_user.getChildTextTrim( "shopid" );
		loginid			= elm_user.getChildTextTrim( "loginid" );
		password		= elm_user.getChildTextTrim( "password" );
		
		str 			= elm_user.getChildTextTrim( "menuroot" );
		
		menuroot		= Integer.parseInt( str );
	}
	
	public String username;
	public String shopid;
	public String loginid;
	public String password;
	public int userid = -1;
	public int menuroot = 0;
}

