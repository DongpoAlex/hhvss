﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.util.*"
	import="com.royalstone.util.component.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 9010201;
	request.setCharacterEncoding("UTF-8");
	HttpSession session = request.getSession(false);
	if (session == null)
		throw new TokenException("您尚未登录,或已超时.");
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new TokenException("您尚未登录,或已超时.");

	//查询用户的权限.
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ))
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:"
		+ moduleid);
%>

<%
	Input ctrl_loginid = new Input();
	ctrl_loginid.setAttribute("type", "text");
	ctrl_loginid.setAttribute("name", "ctrl_loginid");
	ctrl_loginid.setAttribute("size", "10");
	ctrl_loginid.setAttribute("maxlength", "10");

	String loginid = request.getParameter("loginid");
	String username = request.getParameter("username");

	loginid = (loginid == null || loginid.length() == 0) ? "''" : "'"
			+ loginid + "'";
%>


﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />


<xml id="island4reqest" />
<xml id="island4result" />
<xml id="format4list" src="user_list.xsl"></xml>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>

<script language="javascript">
var loginid=<%=loginid%>;

function init()
{
	ctrl_loginid.value = loginid;
}


function search()
{
	load_list();
}

function load_list()
{
	divMain.innerHTML = "请稍候 ..." ;
	
	var url = "../DaemonUserAdm?action=list_user";
	if( ctrl_loginid.value != "" ) url += "&loginid=" + ctrl_loginid.value;
	if( username.value != "" ) url += "&username=" + encodeURI(username.value);

	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_catalogue;
	courier.call();	
}

function analyse_catalogue( text ){
	island4result.loadXML( text );
	divMain.innerHTML = island4result.transformNode( format4list.documentElement );
}

function user_add()
{
	window.location = "user_add.jsp" ;
}
</script>

</head>

<body onload="init()">

	<label> 登录名: </label>
	<%=ctrl_loginid.toString()%>

	<label> 用户名: </label>
	<input type="text" name="username" value="" size="10" maxlength="10" />
	<input type="button" value=" 查询 " onclick="search()" />
	<input type="button" value=" 新建 " onclick="user_add()" />

	<div id="divMain"></div>

	<br />


</body>
</html>
