<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
%>

<%
String sheetid = request.getParameter("sheetid");
if ( sheetid ==null || sheetid.length() == 1 )
	throw new InvalidDataException("没有对帐申请单 id，请检查！");
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>对帐申请单明细</title>

<link rel="stylesheet" href="../css/aw_style.css" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css" />
<style>
.aw-grid-control {
	height: 98%;
	width: 400px;
}

#grid_cat .aw-alternate-even {
	background: #fff;
} /* 偶数 */
#grid_cat .aw-alternate-odd {
	background: #eee;
}

#grid_cat .aw-column-0 {
	width: 130px;
}

#grid_cat .aw-column-2 {
	text-align: right;
}

#div_print {
	position: absolute;
	top: 20px;
	left: 450px;
}

#div_export {
	position: absolute;
	top: 50px;
	left: 450px;
}
</style>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>
<script src="../AW/runtime/lib/aw.js"></script>
<script language="javascript">

var sheetid = "<%=sheetid%>";
extendNumber();

function init()
{
	load_sheetset();
}

function load_sheetset(){
	setLoading( true );
	var parms = new Array();
	parms.push( "sheet=liquidation" );
	parms.push( "sheetid=" + sheetid );
	var url = "../DaemonViewSheet?" + parms.join( '&' );
	var courier = new AjaxCourier( url );
	courier.reader.read 	= analyse_catalogue;
	courier.call();	
}

function analyse_catalogue( text ){
	island4result.loadXML( text );
	var elm_err = island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code == "0" ) 	show_catalogue( text );	
	else div_report.innerHTML = xerr.toString();
	setLoading( false );
}

function show_catalogue( text ){
	var table = new AW.XML.Table;
	table.setTable( "/xdoc/xout/sheet/body" );
	table.setXML( text );
	table.setColumns(["noteno","name","notevalue"]);
	var row_count = table.getCount();
	var sum_value 	= 0;
	for( var i=0; i<row_count; i++)
		sum_value += Number(table.getData(2,i)) ;
	
	var columnNames=["对帐单据号","对帐单据类型","对帐单据金额"];
	var obj = new AW.UI.Grid;
	obj.setId( "grid_cat" );
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( row_count );
	obj.setHeaderText( columnNames );	
	var number_val	= new AW.Formats.Number;
	number_val.setTextFormat( "#,###.##" );
	var str	= new AW.Formats.String;	
	obj.setCellFormat( [ str, str, number_val ] );
	
	obj.setFooterText(["金额小计:", "", normalize_number(sum_value)]);
	obj.setFooterVisible(true);
	obj.setFooterCount(2);
	obj.setFooterHeight(30, 0);

	obj.setCellModel(table);
	div_report.innerHTML = obj.toString();
}

function b_print()
{
	var attributeOfNewWnd = "status=yes,toolbar=no,menubar=no,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
	window.open( "../liquidation/liquidation_print.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);
}

function export_xls()
{
	var parms = new Array();
	parms.push( "operation=liquidationitem" );
	parms.push( "sheetid=" + sheetid );
	var url = "../DaemonDownloadExcel?" + parms.join( '&' );
	
	window.location.href = url;
}
</script>

</head>

<xml id="island4result" />
<body onload="init()">
	<div id="div_print">

		<input type="button" value="打印对帐申请单" onclick="b_print()" /> <br /> <br />
		<input type="button" value="导出到Excel表格" onclick="export_xls()" />
	</div>


	<div id="div_report"></div>
</body>
</html>