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

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%=token.site.getTitle()%>－供应商对帐单表头</title>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/common.js"> </script>
<style>
.tableborder {
	BORDER-RIGHT: #ecebeb 1px solid;
	BORDER-TOP: #ecebeb 1px solid;
	BACKGROUND: #ecebeb;
	BORDER-LEFT: #ecebeb 1px solid;
	BORDER-BOTTOM: #ecebeb 1px solid
}

.tablecolorborder {
	BORDER-RIGHT: #AFC9EF 1px solid;
	BORDER-TOP: #FFFFFF;
	BACKGROUND: #ecebeb;
	BORDER-LEFT: #AFC9EF 1px solid;
	BORDER-BOTTOM: #AFC9EF 1px solid
}

.altbg2 {
	BACKGROUND: #FFFFFE
}
</style>
<script>
var sheetid = "<%=sheetid%>";
var format_show_xsl = "<%=token.site.toXSLPatch("paymentnotehead_print.xsl") %>";

window.onload = function(){
	var arr_sheetid = sheetid.split(",");
	for( var i=0; i<arr_sheetid.length;i++ ){
		load(arr_sheetid[i]);
	}
}
function load(sheetid)
{
	var url = "../DaemonShowPayment?sheetname=paymentnote&section=head&sheetid=" + sheetid;
	var table4detail = new AW.XML.Table;
	table4detail.setURL( url );
	table4detail.request();
	table4detail.response = function(text){
		setLoading(false);
		table4detail.setXML(text);
		var html = table4detail.transformNode( format_show_xsl );
		var elm_sheet = document.createElement("div");
		elm_sheet.innerHTML = html;
		document.body.appendChild( elm_sheet );
	};
	setLoading(true);
}
</script>
</head>
<body>
	<div id="div_detail"></div>
</body>
</html>