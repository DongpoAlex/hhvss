package com.royalstone.vss.admin;

import org.jdom.Element;

public 
/**
 * CyberUser 表示用户信息, 用于解析前台传到后台的数据.
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
		
		str 			= elm_user.getChildTextTrim( "status" );
		if(str!=null && str.length()>0){
			status			= Integer.parseInt( str );
		}
	}
	
	public String username;
	public String shopid;
	public String loginid;
	public String password;
	public int userid = -1;
	public int menuroot = 0;
	public int status = 0;
}

