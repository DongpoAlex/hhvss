﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="java.sql.*" import="com.royalstone.util.*"
	import="com.royalstone.vss.admin.*" import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
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
boolean succeed = false;
String msg     = "''";
%>

<%
String operation = request.getParameter( "operation" );
if( operation != null && operation.equals( "add_user" ) ) {
	String venderid = request.getParameter( "venderid" );
	String loginid  = request.getParameter( "loginid" );
	String username = request.getParameter( "username" );
	String password = request.getParameter( "password" );
	String menuroot = request.getParameter( "menuroot" );
	
	int menuid = Integer.parseInt( menuroot );
	
	Connection conn = XDaemon.openDataSource( token.site.getDbSrcName() );
	
	VenderUserAdm adm = new VenderUserAdm ( conn );
	adm.addVenderUser( -1, username, loginid, venderid, password, menuid );
	succeed = true;
	msg     = "'用户添加成功:" + loginid + "'";
	
	XDaemon.closeDataSource( conn );
}
%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />
<xml id="island4request" />
<xml id="island4result" />
<xml id="format4list" src="user_list.xsl"></xml>
<xml id="format4menu" src="radio_menuroot.xsl"></xml>

<xml id="island4vender"> </xml>
<xml id="format4vender" src="../xsl/vender.xsl"></xml>
<xml id="island4user"> </xml>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="User.js"> </script>


<script language="javascript">

function init()
{
	if( <%=succeed%> ) alert( <%=msg%> );
	load_menuroot();
}


function load_menuroot()
{	
	divMenu.innerHTML = "请稍候 ..." ;
	
	var url = "../DaemonMenuAdm?action=get_roletype_root&roletype=2";
	island4result.async = false;
	island4result.load( url );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );

	divMenu.innerHTML = island4result.transformNode( format4menu.documentElement );
	
}



function save_info()
{
	if( !check_input() ) return;
	form_user.submit();
}



function analyse_reply( text )
{
	island4result.loadXML( text );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" ) alert ( " 添加成功! " );
	else {
		alert( " 添加失败! " );
		alert( xerr.toString() );
	}
	
	// Go back.
	window.location.reload();
}


function check_input()
{
	
	if( form_user.venderid.value 	== "" ) {
		alert( "供应商编号无效 ! " ) ;
		return false;
	}
	
	if( form_user.loginid.value 	== "" ) {
		alert( "登录名无效 ! " ) ;
		return false;
	}

	if( form_user.username.value == "" ) {
		alert( "用户名无效 ! " ) ;
		return false;
	}

	if( form_user.menuroot.value == "" ) {
		alert( "请选择根菜单! " ) ;
		return false;
	}
	
	if( form_user.password.value != form_user.password2.value ){
		alert( "两次输入的密码不一致! " );
		return false;
	}
	
	return true;
}


function set_menuroot( ctrl )
{
	form_user.menuroot.value = "" + ctrl.menuid;
}

function vender_change()
{
window.status = "vender_change";
	var ok = load_vender( form_user.venderid.value );
	if( ok ) {
		form_user.loginid.value  = form_user.venderid.value;
		form_user.username.value = form_user.venderid.value;
	} else {
		form_user.venderid.value = "";
	}
	
	check_user();
}

function toUpperCase(ctrl)
{
	ctrl.value = ctrl.value.toUpperCase();
}

function search_user()
{
	window.location.href = "vender_user_center.jsp";
}



// 访问后台, 查询供应商资料.
function load_vender(id)
{
window.status = "load_vender " + id ;
	divVender.innerHTML = "";
	if( "" == id ) return false;

	var url = "../DaemonInformation?focus=vender&venderid=" + id;
	island4vender.async = false;
	island4vender.load( url );

	divVender.innerHTML = island4vender.transformNode( format4vender.documentElement );
	
	var elm_err 	= island4vender.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr = parseXErr( elm_err );
	if ( xerr.code == "0" ){
		window.status = xerr.toString();
		return true;
	} else {
		alert( xerr.note );
		return false;
	}
}

/**
此函数用于检查用户是否已经存在.
return	true:  已经存在;
	false: 尚未建立;
*/
function user_exists (id)
{
	if( "" == id ) return false;

	var url = "../DaemonVenderUserAdm?operation=get_user_by_loginid&loginid=" + id;
	island4user.async = false;
	island4user.load( url );

	var elm_err 	= island4user.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr = parseXErr( elm_err );
	if ( xerr.code == "0" ){
		window.status = xerr.toString();
		return true;
	} else {
		window.status = xerr.toString();
		return false;
	}
}

function check_user()
{
	var loginid = form_user.loginid.value;
	if( user_exists( loginid ) ) {
		alert( '其他用户已经占用此登录名:' + loginid + ", 请重新输入登录名." );
		form_user.loginid.value = "" ;
	}
}


</script>

</head>

<body onload="init()">

	<form id="form_user" method="post">
		<input type="hidden" name="operation" value="add_user" />
		<table class="noborder">
			<caption>添加供应商用户</caption>

			<tr>
				<td><label>供应商号</label></td>
				<td><input type="text" name="venderid" value=""
					onkeyup="toUpperCase(this)" onblur="vender_change()" size="10"
					maxlength="10" /></td>
				<td><span id="divVender" /></td>
			</tr>

			<tr>
				<td><label>登录名</label></td>
				<td><input type="text" name="loginid" value=""
					onkeyup="toUpperCase(this)" onblur="check_user()" size="10"
					maxlength="10" /></td>
			</tr>

			<tr>
				<td><label>用户名</label></td>
				<td><input type="text" name="username" value="" size="10"
					maxlength="10" /></td>
			</tr>


			<tr>
				<td><label>密码</label></td>
				<td><input type="password" name="password" value="" size="12"
					maxlength="12" /></td>
			</tr>



			<tr>
				<td><label>密码确认</label></td>
				<td><input type="password" name="password2" value="" size="12"
					maxlength="12" /></td>
			</tr>


			<tr>
				<td><label>根菜单</label></td>
				<td><input type="text" name="menuroot" value="" readonly
					size="10" /></td>
			</tr>

		</table>


		<input type="button" value=" 保存 " onclick="save_info()" /> <input
			type="button" value=" 查询 " onclick="search_user()" />

	</form>

	<div id="divMenu"></div>

</body>
</html>
