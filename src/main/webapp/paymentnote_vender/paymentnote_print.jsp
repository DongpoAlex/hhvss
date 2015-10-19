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
<title><%=token.site.getTitle()%>－供应商对帐明细单</title>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<xml id="island4head" />
<xml id="island4formatall"
	src="<%=token.site.toXSLPatch("paymentnote_print.xsl") %>" />
<xml id="island4formatallgroup"
	src="<%=token.site.toXSLPatch("paymentnotegroup_print.xsl") %>" />

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

#div_printhelp {
	width: 400px;
	border: 1px green dotted;
	padding: 16px;
	color: white;
	display: none;
	position: absolute;
	top: 0;
	left: 300;
	z-index: 100;
	background-color: blue;
	filter: progid:DXImageTransform.Microsoft.Alpha(style=0, opacity=90,
		finishOpacity=90);
}
</style>
<script>
var sheetid = "<%=sheetid%>";
</script>
<script>
function init()
{
showAllGroupByCharge();
}


function showAll(){
	setLoading(true);
	var url = "../DaemonShowPayment?sheetname=paymentnote&section=all&sheetid=" + sheetid;
	var courier = new AjaxCourier( url );
	courier.reader.read 	= function(text){
		setLoading(false);
		island4head.loadXML( text );
		var elm_booktitle=island4head.XMLDocument.selectSingleNode( "/xdoc/xout/head/rows/booktitle" );
		var elm_booktypeid=island4head.XMLDocument.selectSingleNode( "/xdoc/xout/head/rows/booktypeid" );
		var elm_booklogofname=island4head.XMLDocument.selectSingleNode( "/xdoc/xout/head/rows/booklogofname" );
		div_title.innerHTML='<b>'+elm_booktitle.text+'－供应商对账明细单<b>';
		print_logo.src="../img/"+elm_booklogofname.text;
		div_print.innerHTML = island4head.transformNode( island4formatall.documentElement );
	};
	courier.call();	
}

function showAllGroupByshop(){
	setLoading(true);
	var url = "../DaemonShowPayment?sheetname=paymentnote&section=allgroupbyshop&sheetid=" + sheetid;
	var courier = new AjaxCourier( url );
	courier.reader.read 	= function(text){
	//alert(text)
		setLoading(false);
		island4head.loadXML( text );
		var elm_booktitle=island4head.XMLDocument.selectSingleNode( "/xdoc/xout/head/rows/booktitle" );
		var elm_booktypeid=island4head.XMLDocument.selectSingleNode( "/xdoc/xout/head/rows/booktypeid" );
		var elm_booklogofname=island4head.XMLDocument.selectSingleNode( "/xdoc/xout/head/rows/booklogofname" );
		div_title.innerHTML='<b>'+elm_booktitle.text+'－供应商对账明细单<b>';
		print_logo.src="../img/"+elm_booklogofname.text;
		div_print.innerHTML = island4head.transformNode( island4formatallgroup.documentElement );
	};
	courier.call();	
}

function showAllGroupByCharge(){
	setLoading(true);
	var url = "../DaemonShowPayment?sheetname=paymentnote&section=allgroupbycharge&sheetid=" + sheetid;
	var courier = new AjaxCourier( url );
	courier.reader.read 	= function(text){
		//alert(text)
		setLoading(false);
		island4head.loadXML( text );
		var elm_booktitle=island4head.XMLDocument.selectSingleNode( "/xdoc/xout/head/rows/booktitle" );
		var elm_booktypeid=island4head.XMLDocument.selectSingleNode( "/xdoc/xout/head/rows/booktypeid" );
		var elm_booklogofname=island4head.XMLDocument.selectSingleNode( "/xdoc/xout/head/rows/booklogofname" );
		div_title.innerHTML='<b>'+elm_booktitle.text+'－供应商对账明细单<b>';
		print_logo.src="../img/"+elm_booklogofname.text;
		div_print.innerHTML = island4head.transformNode( island4formatallgroup.documentElement );
	};
	courier.call();	
}

function showDetail(){
	showAll();
}

function showGroupby(){
	showAllGroupByshop();
}

function toExport()
{
	var url = "../DaemonDownloadPayment?sheetname=paymentnote&sheetid=" + sheetid;
	window.location.href = url;
}

function toPrint()
{
	$("div_printhelp").style.display = "none";
	$("toPrint").style.display = "none";
	window.print();
}
function showPrint(){
	$("div_printhelp").style.display = "block";
}
</script>


</head>

<body onload="init()">
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<img id="print_logo" src="" />
	<span id="toPrint"> <a href="javascript:showPrint();">[打印本页]</a>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
		href="javascript:toExport()">[ 导出到EXCEL ]</a>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
		href="javascript:showDetail()">[ 明细打印 ]</a>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
		href="javascript:showAllGroupByCharge()">[ 联营汇总打印 ]</a>
	</span>
	<div id="div_title" align="center" style="font-size: 18px;";></div>
	<div id="div_sheetname" align="right" style="font-size: 13px;";>
		<b>付款申请单号:</b>
		<script language="javascript">document.write( sheetid );</script>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</div>
	<div id="div_print"></div>
	<div id="div_print_sheet"></div>
	<div id="div_chargetax"></div>
	<div id="div_generatedtax"></div>

	<div id="div_chargeoutax"></div>
	<div id="div_generatedouttax"></div>
	<div id="div_tail"></div>

	<div id="div_printhelp">
		<div style="font-size: 16px; font-weight: bold">
			为提高结算效率，请在打印前确认如下设置。 <br />注意，对于不符合规定格式的打印将拒收。
		</div>
		<p />
		操作步骤如下：
		<p />
		1、打开IE浏览器，如下图进入页面设置区：
		<p />
		<img src="../img/print1.jpg" alt="打印设置"></img>
		<p />
		2、进入页面设置区后，见下图：
		<p />
		在页眉出输入：&w&b页码，&p/&P
		<p />
		在页脚处输入：&u&b&d
		<p />
		<img src="../img/print2.jpg" alt="打印设置"></img>
		<p />
		3、按2中的设置后，点确定。
		<p />
		<center>
			<input type="button" onclick="toPrint();" value="已设置完毕，开始【打印】" />
		</center>
	</div>
</body>
</html>