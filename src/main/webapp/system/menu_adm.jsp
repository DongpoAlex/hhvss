﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.util.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.util.component.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../WEB-INF/errorpage.jsp"%>
<%
final int moduleid = 9010105;
request.setCharacterEncoding( "UTF-8" );

HttpSession session = request.getSession( false );
if( session == null ) throw new TokenException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new TokenException( "您尚未登录,或已超时." );

//查询用户的权限.
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );


Input input_id = new Input();
input_id.setAttribute( "name", "ctrl_menuid" );

String menuid = request.getParameter( "menuid" );
if( menuid != null && menuid.length() > 0 ) {
	input_id.setAttribute( "type", "hidden" );
	input_id.setAttribute( "value", menuid );
} else {
	throw new InvalidDataException( "menuid not set! " );
}

%>



﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />

<xml id="island4reqest" />
<xml id="island4result" />
<xml id="format4list" src="menu_detail.xsl"></xml>
<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="Menu.js"> </script>


<script language="javascript">

function init()
{
	try{
		if( ctrl_menuid.value != "" ) load_list();
	} catch(e) {
		alert(e);
	}
}

function load_list()
{
	divMain.innerHTML = "请稍候 ..." ;
	
	var url = "../DaemonMenuAdm?action=get_detail";
	if( ctrl_menuid.value != "" ) url += "&menuid=" + ctrl_menuid.value;
	
	
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

function del_menu( id )
{
	var ok = confirm ( "你确定要删除此菜单: " + id + "?" );
	if( !ok ) return;

	var url = "../DaemonMenuAdm?action=delete";
	url += "&menuid=" + id;
	island4result.async = false;
	island4result.load( url );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );

	var xerr = parseXErr( elm_err );

	if ( xerr.code != "0" ) alert( xerr.note );

	window.location.reload();
}

function cmedit(moduleid,menuid,cmid){
	var url = "cmedit.jsp?moduleid="+moduleid+"&menuid="+menuid+"&cmid="+cmid;
	//ewin = showModelessDialog(url,window,"status:false;dialogWidth:900px;dialogHeight:500px;resizable:1;");

	window.open("cmedit.jsp?moduleid="+moduleid+"&menuid="+menuid+"&cmid="+cmid);
}
</script>

</head>

<body onload="init()">
	<%=input_id.toString()%>


	<a href="javascript:window.location.reload()">刷新</a>

	<div id="divMain"></div>
	<a target="_blank"></a>

	<input type="button" value=" 返回 " onclick="window.history.back()" />

</body>
</html>
