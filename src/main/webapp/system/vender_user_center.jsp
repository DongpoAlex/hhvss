﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.util.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.util.component.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
final int moduleid = 9010202;
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
<link rel="stylesheet" href="../css/system.css" type="text/css" />


<xml id="island4reqest" />
<xml id="island4result" />
<xml id="format4list" src="vender_user_center.xsl"></xml>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>

<script language="javascript">


function init()
{
}


function search()
{
	load_list();
}

function load_list()
{
	divMain.innerHTML = "请稍候 ..." ;
	
	var url = "../DaemonVenderUserAdm?operation=list_vender_user";
	if( loginid_min.value != "" ) url += "&loginid_min=" + loginid_min.value;
	if( loginid_max.value != "" ) url += "&loginid_max=" + loginid_max.value;
	if( loginid.value != "" ) url += "&loginid=" + loginid.value;
	island4result.async = false;
	island4result.load( url );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );

	divMain.innerHTML = island4result.transformNode( format4list.documentElement );
	
	var xerr = parseXErr( elm_err );
	if ( xerr.code == "0" ){
		return true;
	} else {
		alert( xerr.note );
		return false;
	}

}

function user_add()
{
	window.location = "vender_user_add.jsp" ;
}


function toUpperCase(ctrl)
{
	ctrl.value = ctrl.value.toUpperCase();
}

</script>

</head>

<body onload="init()">
	<div>
		<label>登录名(模糊查询): </label> <input type="text" name="loginid" size="10"
			maxlength="10" onkeyup="toUpperCase(this)" />
		&nbsp;&nbsp;&nbsp;&nbsp; <label>登录名(范围查询): </label> <input type="text"
			name="loginid_min" size="10" maxlength="10"
			onkeyup="toUpperCase(this)" /> - <input type="text"
			name="loginid_max" size="10" maxlength="10"
			onkeyup="toUpperCase(this)" />
	</div>
	<div>
		<input type="button" value=" 查询 " onclick="search()" /> <input
			type="button" value=" 新建 " onclick="user_add()" />
	</div>
	<div id="divMain"></div>
</body>
</html>
