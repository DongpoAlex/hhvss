﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../WEB-INF/errorpage.jsp"%>

<%
	final int moduleid = 3020208;
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );

	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );

	if ( !perm.include( Permission.READ ) ) throw new PermissionException( "您不具有操作此模块的权利，请与管理员联系!" );
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>结算限额查询-零售商</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<style>
.aw-grid-control {
	width: 100%;
	border: none;
	font: menu;
	height: 80%
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}
</style>

<style>
title_sec {
	font-size: 14px;
	color: blue;
	align
	="center"
}

div#div_loading {
	width: 15em;
	text-align: center;
	background: #FFA826;
	color: #FFF;
	position: absolute;
	top: 200px;
	left: 10px;
}

#div_month {
	
}

label {
	font-size: 11px;
	font-weight: bold;
	color: navy;
}
</style>

<script language="javascript" src="../js/XErr.js"></script>
<script language="javascript" src="../js/ajax.js"></script>
<script language="javascript" src="../AW/runtime/lib/aw.js"></script>

<script language="javascript">
var attributeOfNewWnd = "status=yes,toolbar=no,menubar=no,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);	

var btn_search_his = new AW.UI.Button;
btn_search_his.setControlText( "历史结算限额查询" );
btn_search_his.setId( "btn_search_his" );
btn_search_his.setControlImage( "search" );


function openhistoryWnd()
{	
	var venderid = $('venderid').value
	if(venderid==""){alert("请输入供应商编码");return;}
	var operation = "history" ;
 	window.open( "./advice_balance.jsp?venderid="+venderid+"&operation=" + operation, venderid, attributeOfNewWnd );		
}
</script>

</head>

<xml id="island4result" />
<xml id="island4format" src="advice_current.xsl" />

<body>
	<div style="margin-top: 15px;">
		供应商编码:<input id="venderid" type="text" />
		<script>
			document.write( btn_search_his );
			btn_search_his.onClick = openhistoryWnd ;
		</script>
	</div>
</body>

</html>