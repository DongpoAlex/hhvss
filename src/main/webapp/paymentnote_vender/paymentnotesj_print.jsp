﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );
	
%>
<%

	SelBook sel_book = new SelBook(token);
	sel_book.setAttribute( "name", "txt_bookno" );
	
	String sheetid = request.getParameter("sheetid");
	sheetid = ( sheetid == null || sheetid.length() == 0 )? "" : sheetid.trim();

%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%=token.site.getTitle()%>－供应商对帐单表头</title>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/common.js"> </script>
<style>
BODY {
	padding: 4px;
	SCROLLBAR-ARROW-COLOR: #ecebeb;
	SCROLLBAR-BASE-COLOR: #ffffff;
	BACKGROUND-COLOR: #ffffff;
}

table {
	background-color: black;
	width: 100%;
	padding: 0px;
	border: #000000 1px solid;
}

td,th {
	background-color: #ffffff;
	FONT-SIZE: 12px;
}

.foot {
	width: 100%;
	text-align: center;
	margin-top: 4px;
	font-size: 12px;
	page-break-after: always;
}

.nofoot {
	width: 100%;
	text-align: center;
	margin-top: 4px;
	font-size: 12px;
}
</style>
<script>
var sheetid = "<%=sheetid%>";
var format_show_xsl = "<%=token.site.toXSLPatch("paymentnotesj.xsl") %>";

window.onload = function(){
	var arr_sheetid = sheetid.split(",");
	for( var i=0; i<arr_sheetid.length;i++ ){
		load(arr_sheetid[i]);
	}
}
function load(sheetid)
{
	var url = "../DaemonShowPayment?sheetname=paymentnote&section=allgroupbycharge&sheetid=" + sheetid;
	var table4detail = new AW.XML.Table;
	table4detail.setURL( url );
	table4detail.request();
	table4detail.response = function(text){
		setLoading(false);
		table4detail.setXML(text);
		var elm_booktitle=table4detail.getXMLText( "/xdoc/xout/head/rows/booktitle" );
		var elm_booklogofname=table4detail.getXMLText( "/xdoc/xout/head/rows/booklogofname" );
		div_title.innerHTML='<b>'+elm_booktitle+'扣项收据<b>';
		print_logo.src="../img/"+elm_booklogofname;
 		var html = table4detail.transformNode( format_show_xsl );
		var elm_sheet = document.createElement("div");
		elm_sheet.innerHTML = html;
		document.body.appendChild( elm_sheet );
	};
	setLoading(true);
}
function toPrint()
{
	var url = "../DaemonShowPayment?sheetname=paymentnote&section=sjprint&sheetid=" + sheetid;
	var table4detail = new AW.XML.Table;
	table4detail.setURL( url );
	table4detail.request();
	table4detail.response = function(text){
	}
	$("toPrint").style.display = "none";
	window.print();
}
</script>
</head>
<body>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<img id="print_logo" src="" />
	<span id="toPrint"> <a href="javascript:toPrint();">[打印本页]</a>
		<div id="div_title" align="center" style="font-size: 18px;";></div>
		<div id="div_detail"></div>
</body>
</html>