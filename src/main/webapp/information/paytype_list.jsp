﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3080103;
%>
<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );
	if ( !perm.include( Permission.READ ) ) 
		throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
%>


<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title></title>
<link rel="stylesheet" href="../css/style.css" type="text/css" />

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<script language="javascript">
extendDate();
extendNumber();
</script>

<style>
.aw-grid-control {
	width: 92%;
	border: none;
	font: menu;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

#grid_cat {
	background-color: #F9F8F4;
	height: 450px;
}
</style>

<script language="javascript">
var loading_html='<img src="../img/loading.gif"></img><font color=#003>正在下载,请等候……</font>';
function init()
{
	var url = "../DaemonInformation?focus=paytype&timestamp=" + new Date().getTime();
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read = analyse_catalogue;
	courier.call();
}

function analyse_catalogue( text )
{
	island4result.loadXML( text );
	show_catalogue();
}
	
function show_catalogue()
{
	set_loading_notice( true );
		
	var node_body 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );	
	var row_count   = node_body.childNodes.length;
	
	island4cat.loadXML( node_body.xml );
	var xml, node = document.getElementById( "island4cat" );		
		
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument( "", "", null);
		xml.appendChild( island4cat.selectSingleNode( "*" ) );
	}		
	
	var table = new AW.XML.Table;
	table.setXML( xml );
	
	var columnNames = [ "付款方式编码", "付款方式名称" ];
	var columnOrder = [ 0, 1 ];
		
	var obj = new AW.UI.Grid;
	obj.setId( "grid_cat" );
	obj.setColumnCount( 2 );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );
	obj.setColumnIndices( columnOrder );	
		
	var str	= new AW.Formats.String;	
		
	obj.setCellModel(table);
	div_cat.innerHTML = obj.toString();
		
	window.status = "OK";
	set_loading_notice( false );
}
</script>

<script language="javascript">
function set_loading_notice( on )
{
	div_loading.innerHTML = loading_html;
	div_loading.style.display = on ? 'block' : 'none';
}
</script>
</head>

<xml id="island4result" />
<xml id="island4cat" />

<body onload="init()">
	<table>
		<tr>
			<td>结算方式清单</td>
		</tr>
	</table>
	<div id="div_loading" style="display: none;">
		<img src="../img/loading.gif"></img><font color=#003>正在下载,请等候……</font>
	</div>
	<div id="div_cat" style="display: block;">...</div>
</body>

</html>
