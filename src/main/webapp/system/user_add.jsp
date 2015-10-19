﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.util.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
final int moduleid = 9010201;
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
<link rel="stylesheet" href="../css/style.css" type="text/css" />
<xml id="island4request" />
<xml id="island4result" />
<xml id="format4list" src="user_list.xsl"></xml>
<xml id="format4menu" src="radio_menuroot.xsl"></xml>
<xml id="format4role" src="role4user.xsl"></xml>
<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="User.js"> </script>


<script language="javascript">

function init()
{
	load_menuroot();
}


function load_menuroot()
{	
	divMenu.innerHTML = "请稍候 ..." ;
	
	var url = "../DaemonMenuAdm?action=get_roletype_root&roletype=1";
	island4result.async = false;
	island4result.load( url );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );

	divMenu.innerHTML = island4result.transformNode( format4menu.documentElement );
	
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
	var loginid = ctrl_loginid.value;
	if( user_exists( loginid ) ) {
		alert( '其他用户已经占用此登录名:' + loginid + ", 请重新输入登录名." );
		ctrl_loginid.focus();
	}
}


function save_info()
{
	if( !check_input() ) return;
	
	var user = new User();
	user.userid 	= "-1" ;
	user.username 	= ctrl_username.value; 
	user.loginid	= ctrl_loginid.value; 
	user.password	= ctrl_password1.value; 
	user.menuroot	= ctrl_menuroot.value;

	island4request.loadXML( "" );
	var doc      	= island4request.XMLDocument;
	var elm_root 	= doc.selectSingleNode("/");
	var elm_user  	= user.toElement( doc );
	
	elm_root.appendChild( user.toElement(doc) );

	send_user_info();
}




function check_input()
{
	
	if( ctrl_loginid.value 	== "" ) {
		alert( "登录名无效 ! " ) ;
		return false;
	}

	if( ctrl_username.value == "" ) {
		alert( "用户名无效 ! " ) ;
		return false;
	}

	if( ctrl_menuroot.value == "" ) {
		alert( "请选择根菜单! " ) ;
		return false;
	}
	
	if( ctrl_password1.value != ctrl_password2.value ){
		alert( "两次输入的密码不一致! " );
		return false;
	}
	
	return true;
}


function send_user_info()
{
	var url = "../DaemonUserAdm?action=add_user";
	var courier 		= new AjaxCourier( url );
	courier.island4req  	= island4request;
	courier.reader.read 	= analyse_reply;
	courier.call();
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
	
	var userid = island4result.XMLDocument.selectSingleNode( "/xdoc/xout/user/userid" ).text;
	
	//转到用户Role添加
	url = "role4user.jsp?userid="+userid;
	window.location.href = url;
	// Go back.
	//window.location.reload();
}


function set_menuroot( ctrl )
{
	ctrl_menuroot.value = "" + ctrl.menuid;
}

</script>
<xml id="island4user" />
</head>

<body onload="init()">

	<table class="noborder">
		<caption>添加用户</caption>

		<tr>
			<td><label>登录名</label></td>
			<td><input type="text" name="ctrl_loginid" value="" size="10"
				maxlength="10" onblur="check_user()"
				onkeypress="if(event.keyCode<=91 && event.keyCode>=65)event.keyCode=event.keyCode+32" />
				登陆名请使用小写</td>
		</tr>

		<tr>
			<td><label>用户名</label></td>
			<td><input type="text" name="ctrl_username" value="" size="10"
				maxlength="10" /></td>
		</tr>

		<tr>
			<td><label>密码</label></td>
			<td><input type="password" name="ctrl_password1" value="" /></td>
		</tr>



		<tr>
			<td><label>密码确认</label></td>
			<td><input type="password" name="ctrl_password2" value="" /></td>
		</tr>


		<tr>
			<td><label>根菜单</label></td>
			<td><input type="text" name="ctrl_menuroot" value="" readonly
				size="10" /></td>
		</tr>

	</table>


	<input type="button" value=" 保存 " onclick="save_info()" />
	<input type="button" value=" 返回 " onclick="window.history.back()" />


	<div id="divMenu"></div>
	<div id="divRole"></div>
</body>
</html>
