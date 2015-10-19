﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%

final int moduleid = 3020101;
request.setCharacterEncoding( "UTF-8" );

HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );

// 查询用户的权限.
Permission perm = token.getPermission( moduleid );
String msg = "您未获得使用此模块的授权,请与管理员联系. 模块号: " + moduleid;
if( !perm.include( Permission.READ ) ) throw new PermissionException( msg );

%>


<%
	String operation = request.getParameter("operation");
%>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>结算限额</title>

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
body {
	BACKGROUND-COLOR: #ffffff
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
</style>

<xml id="island4result" />
<xml id="island4cat" />
<xml id="island4format" src="format_advice.xsl" />

<script language="javascript">
var loading_html='<img src="../img/loading.gif"></img> <font color=#003>正在下载,请等候……</font>';
var operation = "<%=operation%>";
function init()
{
	if( operation == "history" )	search_his();
	else 	search_thismonth();
}

function search_thismonth()
{
	var parms = new Array();
	parms.push( "focus=payadvice_current" );
	parms.push( "timestamp=" + new Date().getTime() );
	var url = "../DaemonReportFiscal?" + parms.join( '&' );
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_catalogue;
	courier.call();	
}

/**
 * 此函数从后台取历史的结算限额
 */	
function search_his()
{

	set_loading_notice( true );
	var parms = new Array();
	parms.push( "focus=payadvice_history" );
	parms.push( "timestamp=" + new Date().getTime() );
	var url = "../DaemonReportFiscal?" + parms.join( '&' );
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_catalogue_his;
	courier.call();	
}
	
function analyse_catalogue( text )
{
	island4cat.loadXML( text );
	island4result.loadXML( text );
	var elm_err 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code == "0" ) 
	{    	 
		var elm_row 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/report" );
		var row_count=elm_row.childNodes.length;
		if( row_count==0 )
			div_cat.innerHTML = "没有找到你要查询的结算.";
		else{
			var node = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/report" );	
			var table = new AW.XML.Table;
			island4cat.loadXML( table.getXMLContent(node) );
			show_cat();
		}	
	}
	else {	
		alert(xerr.toString());	
	}
	set_loading_notice( false );
}
	
function analyse_catalogue_his( text )
{
	
	island4cat.loadXML( text );
	island4result.loadXML( text );
	var elm_err 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code == "0" ) 
	{    	 
		var elm_row 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/report" );		
		var row_count=elm_row.childNodes.length;
		if( row_count==0 )
			div_cat.innerHTML = " 没有结算限额数据 ";
		else{
			var elm_booktitle=island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/report/row/booktitle" );
			var elm_booktypeid=island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/report/row/booktypeid" );
			var elm_booklogofname=island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/report/row/booklogofname" );
			title.innerHTML='商业—供应商结算限额单';
			print_logo.src="../img/"+elm_booklogofname.text;
			var node = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/report" );	
			var table = new AW.XML.Table;
			island4cat.loadXML( table.getXMLContent(node) );
			show_cat();
		}	
	}
		else {	
			alert(xerr.toString());	
		}
		set_loading_notice( false );
	}

function show_cat()
{
	div_cat.innerHTML = island4result.transformNode( island4format.documentElement );
}
	
function set_loading_notice( on )
{
	div_loading.innerHTML = loading_html;
	div_loading.style.display = on ? 'block' : 'none';
}
</script>
<body onload="init()">
	<div id="div_loading" style="display: none;">
		<img src="../img/loading.gif"></img><font color=#003>正在下载,请等候……</font>
	</div>
	<br /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<b><img id="print_logo" src="<%=token.site.getLogo()%>" /></b>
	<div id="title" align="center"
		style="font-size: 16px; font-family: '楷体_GB2312, 黑体'; color: blue; font-weight: bold";></div>
	<div id="div_cat" style="display: block;"></div>
</body>

</html>