﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.util.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.util.component.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
final int moduleid = 9010204;			// 角色管理( 入口模块 )
request.setCharacterEncoding( "UTF-8" );
HttpSession session = request.getSession( false );
if( session == null ) throw new TokenException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new TokenException( "您尚未登录,或已超时." );

//查询用户的权限.
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css">
<style>
body {
	font-family: Verdana, Arial, Courier, sans-serif;
	font-size: 12px;
}

label {
	font-size: 12px;
	color: navy;
}
</style>

<xml id="format4role" src="role_center.xsl" />
<xml id="island4rolelist" />
<xml id="island4result" />

<script language="JavaScript" src="../js/XErr.js"> </script>
<script language="javascript">

/**
 * 初始化, 取出所有角色.
 */
function init()
{	
	island4rolelist.loadXML("");
	island4rolelist.async = false;
	island4rolelist.load( "../DaemonRoleAdm?action=list_role_all" );
	divRole.innerHTML = island4rolelist.transformNode( format4role.documentElement );
}

/**
 * 直接删除一个角色
 */
function delete_role( roleid )
{
	try{
		var id = parseInt( roleid );
		if( id <0 ) {
			alert( "内嵌角色不可以删除!" );
			return;
		}
		var ok = confirm( "您确定要删除此角色: " + roleid + " ? " );
		if( !ok ) return;
	
		window.status = "Delete role: " + roleid;

		var url = "../DaemonRoleAdm?action=delete_role&roleid=" + roleid;
		island4result.async = false;
		island4result.load( url );
	
		var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
		var xerr = parseXErr( elm_err );
		if ( xerr.code == "0" ){
			window.status = xerr.toString();
		} else {
			alert( xerr.note );
		}
	} catch ( e ) {
		alert(e);
	}
	window.location.reload();

}



/**
 * 进入角色的权限维护页面
 */
function open_role_perm (roleid)
{
  	window.location ="permission_manager_role.jsp?"+roleid;	
}

/**
 * 添加角色
 */
function add_role( )
{	
	window.location.href ="role_manager.jsp?operation=init";
}
 
</script>

</head>
<body onload="init()">
	<input type="button" value="添加角色" onclick="add_role()" />
	<div id="divRole"></div>
</body>
</html>
