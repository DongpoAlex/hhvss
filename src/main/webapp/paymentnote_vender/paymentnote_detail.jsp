<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
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
	String sheetid = request.getParameter("sheetid");
%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>付款单明细</title>
<link rel="stylesheet" href="../css/aw_style.css" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css" />

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<xml id="island4head" />
<xml id="island4result" />
<xml id="island4format"
	src="<%=token.site.toXSLPatch("paymentnote_head.xsl") %>" />
<xml id="format_invoice"
	src="<%=token.site.toXSLPatch("invoice_format.xsl") %>" />

<style type="text/css">
#tag_grp {
	width: 100%;
	height: 26px;
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

.aw-grid-control {
	height: 90%;
	width: 98%;
	border: none;
	font: menu;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

.aw-column-0 {
	width: 130px;
}

.aw-column-5 {
	text-align: right;
}

#grid_sheetset .aw-column-5,#grid_sheetset .aw-column-6,#grid_sheetset .aw-column-4
	{
	text-align: right;
}
</style>

<script language="javascript">
var sheetid = "<%=sheetid%>";
var tag_grp = new AW.UI.Tabs;

function $(id){
	return document.getElementById(id);
}

function init()
{
	//显示tab	
	install_page_tag();

	//显示付款单头
	showPaymentHead();
}

function install_page_tag()
{
	tag_grp.setId( "tag_grp" );
	tag_grp.setItemText([ "表头信息", "对帐单据", "票扣扣项", "非票扣扣项","固定扣项（票扣）", "固定扣项（非票扣）", "发票信息" ]);
	tag_grp.setItemCount( 7 );
	tag_grp.setSelectedItems( [0] );
	tag_grp.onSelectedItemsChanged = changeTag;

	div_tabs.innerHTML = tag_grp.toString();
}

// tab 切换事件
function changeTag(idx)
{
	var tag0 = $("div_head");
	var tag1 = $("div_sheetset");
	var tag2 = $("div_with_tax");
	var tag3 = $("div_without_tax");
	var tag4 = $("div_cs_generated_with_tax");
	var tag5 = $("div_cs_generated_without_tax");
	var tag6 = $("div_invoice");
	for (var i=0; i<7; i++){
		if (idx == i ) 
			eval("tag"+i).style.display = "block"
		else
			eval("tag"+i).style.display = "none"
	}
	
	switch ( Number(idx) ) {
			case 1:
				show_sheet_set();
				break;
			case 2:
				show_with_tax();
				break;
			case 3:
				show_without_tax();
				break;
			case 4:
				show_cs_generated_tax()
				break;
			case 5:
				show_cs_generated();
				break;
			case 6:
				show_invoice();
				break;
			default:
	}
}

/**
 * 显示对帐单的表头
 */
function showPaymentHead()
{	
	var parms = new Array();
	parms.push( "sheetname=paymentnote" );
	parms.push( "section=head" );
	parms.push( "sheetid=" + sheetid );
	var url = "../DaemonShowPayment?" + parms.join( '&' );
	var courier = new AjaxCourier( url );
	courier.reader.read 	= function(text){
		island4head.loadXML(text);
		div_head.innerHTML = island4head.transformNode( island4format.documentElement );
		setLoading(false);
	};
	courier.call();
	
}

/**
 * 显示对帐业务单据
 */
function show_sheet_set()
{
	if ( div_sheetset.innerHTML == ''){
		read_sheet_set();
	}
}

/**
 * 从后台查询业务单据信息
 */
function read_sheet_set()
{
	setLoading(true);
	var parms = new Array();
	parms.push( "sheetname=paymentnote" );
	parms.push( "section=sheet_set" );
	parms.push( "sheetid=" + sheetid );
	var url = "../DaemonShowPayment?" + parms.join( '&' );
	var courier = new AjaxCourier( url );
	courier.reader.read 	= function(text){
		div_sheetset.innerHTML = get_sheet_set( text );
		setLoading(false);
	};
	courier.call();
	
}
/**
 *格式化数据岛，返回显示内容
*/
function get_sheet_set( text )
{
	island4result.loadXML(text);
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code != "0" ) return xerr.note;
	
	var table = new AW.XML.Table;
	var obj = new AW.UI.Grid;
	table.setTable( "/xdoc/xout/sheetset/unpaidsheet_list" );
	table.setXML( text );
	table.setColumns(["docno","doctypename", "shopid","shopname","paydocamt","paytaxamt17","paytaxamt13","docdate","majorid","logisticsid","noteremark"]);
	var columnNames = [ "单据号", "单据类型", "门店", "门店名", "对帐金额", "税额17%", "税额13%", "单据日期","课类","物流模式","单据说明" ];
	obj.setId( "grid_sheetset" );
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( table.getCount() );	
	obj.setHeaderText( columnNames );

	obj.setCellModel(table);

	obj.setFooterText(["金额小计:","","","",normalize_number(table.getCellSum(4)),normalize_number(table.getCellSum(5)),normalize_number(table.getCellSum(6))]);
	obj.setFooterVisible(true);
	obj.setFooterCount(columnNames.length);
	obj.setSelectionMode("single-row");
	return obj.toString();
}


/**
 * 显示票扣费用单据
 */
function show_with_tax()
{	
	if (div_with_tax.innerHTML == '')
	{
		div_with_tax.innerHTML = load_with_tax();
	}
	
}

/**
 * 从后台查询票扣费用
 */
function load_with_tax()
{

	var parms = new Array();
	parms.push( "sheetname=paymentnote" );
	parms.push( "section=charge_with_tax" );
	parms.push( "sheetid=" + sheetid );

	
	var url = "../DaemonShowPayment?" + parms.join( '&' );
	island4result.async = false;
	island4result.load( url );
	
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code != "0" ) return xerr.note;

	var elm_body 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/chargesum_with_tax" );	
	var row_count = elm_body.childNodes.length;
	if( row_count == 0 ) return " 该单据没有票扣扣项"

	var sum_chargeamt = 0;
	var children = elm_body.childNodes; 
	for( var i=0; i<row_count; i++){
		sum_chargeamt += Number(children[i].selectSingleNode("chargeamt").text);
	}
	
	var table = new AW.XML.Table;
 	
	table.setXML( table.getXMLContent(elm_body) );
	
	var columnNames = [ "单据号", "门店编号 ", "门店名称", "扣项代码", "扣项名称", "扣项金额", "票扣属性","扣项说明",];	
	var obj = new AW.UI.Grid;
	obj.setId( "grid_chargesum_tax" );
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );
	var val_number = new AW.Formats.Number;
	val_number.setTextFormat( "#,###.##" );		
	var str		= new AW.Formats.String;	
	obj.setCellFormat( [ str, str, str, str, str, val_number, str, str] );
	obj.setCellModel(table);
	
	obj.setFooterText(["金额小计:","","","","" ,normalize_number(sum_chargeamt)]);
	obj.setFooterVisible(true);
	obj.setFooterCount(columnNames.length);
	obj.setFooterHeight(30, 0);
	obj.setSelectionMode("single-row");
	return obj.toString();
}

/**
 * 显示非票扣扣项
 */
function show_without_tax()
{
	if (div_without_tax.innerHTML == '') {
		div_without_tax.innerHTML = load_without_tax();
	}
}

/**
 * 从后台查询非票扣扣项
 */
function load_without_tax()
{
	var parms = new Array();
	parms.push( "sheetname=paymentnote" );
	parms.push( "section=charge_without_tax" );
	parms.push( "sheetid=" + sheetid );

	var url = "../DaemonShowPayment?" + parms.join( '&' );
	island4result.async = false;
	island4result.load( url );	
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code != "0" ) return xerr.note;
	
	var elm_body 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/chargesum_without_tax" );
	var row_count   = elm_body.childNodes.length;

	if( elm_body.childNodes.length == 0 ) return "该单据没有非票扣扣项"
	

	var sum_chargeamt = 0;
	var children = elm_body.childNodes; 
	for( var i=0; i<row_count; i++){
		sum_chargeamt += Number(children[i].selectSingleNode("chargeamt").text);
	}
	
	var table = new AW.XML.Table;
 	
	table.setXML( table.getXMLContent(elm_body) );
	
	var columnNames = [ "单据号", "门店编号 ", "门店名称", "扣项代码", "扣项名称", "扣项金额", "扣项属性","扣项说明",];
	
	var obj = new AW.UI.Grid;
	obj.setId( "grid_chargesum" );
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );
	var val_number = new AW.Formats.Number;
	val_number.setTextFormat( "#,###.##" );		
	var str		= new AW.Formats.String;	
	obj.setCellFormat( [ str, str, str, str, str, val_number, str, str] );
	obj.setCellModel(table);
	
	obj.setFooterText(["金额小计:","","","","" ,normalize_number(sum_chargeamt)]);
	obj.setFooterVisible(true);
	obj.setFooterCount(columnNames.length);
	obj.setFooterHeight(30, 0);
	obj.setSelectionMode("single-row");
	return  obj.toString();
}

/**
 * 显示固定扣项
 */
function show_cs_generated_tax()
{	
	if (div_cs_generated_with_tax.innerHTML == '') {
		div_cs_generated_with_tax.innerHTML = load_generated_cs_tax();
	}
}

/**
 * 从后台查询固定扣项( 票扣 )
 */
function load_generated_cs_tax()
{
	var parms = new Array();
	parms.push( "sheetname=paymentnote" );
	parms.push( "section=charge_generated_with_tax" );
	parms.push( "sheetid=" + sheetid );

	var url = "../DaemonShowPayment?" + parms.join( '&' );
	island4result.async = false;
	island4result.load( url );	
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code != "0" ) return xerr.note;
	var elm_body 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/chargegenerated_with_tax" );
	var row_count   = elm_body.childNodes.length;
	if( row_count == 0 ) return "该单据没有固定扣项";
	
	var table = new AW.XML.Table;
 	
	table.setXML( table.getXMLContent(elm_body) );
	//table.setColumns(["docno","shopid","shopname","chargecodeid","chargename","chargeamt","invoicemode","noteremark",]);
	var columnNames = [ "单据号", "门店编号 ", "门店名称", "扣项代码", "扣项名称", "扣项金额", "扣项属性","扣项说明" ];
	
	var obj = new AW.UI.Grid;
	obj.setId( "grid_generated_cs_tax" );
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );
	var val_number = new AW.Formats.Number;
	val_number.setTextFormat( "#,###.00" );		
	var str	= new AW.Formats.String;	
	obj.setCellFormat( [ str, str, str, str, str, val_number, str, str] );
	obj.setSelectionMode("single-row");
	obj.setCellModel(table);
	return  obj.toString();

}

/**
 * 显示固定扣项
 */
function show_cs_generated()
{	
	if (div_cs_generated_without_tax.innerHTML == ''){
		div_cs_generated_without_tax.innerHTML = load_cs_generated();
	}
}

/**
 * 从后台查询固定扣项
 */
function load_cs_generated()
{
	var parms = new Array();
	parms.push( "sheetname=paymentnote" );
	parms.push( "section=charge_generated_without_tax" );
	parms.push( "sheetid=" + sheetid );

	var url = "../DaemonShowPayment?" + parms.join( '&' );
	island4result.async = false;
	island4result.load( url );	
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code != "0" ) return xerr.note;
	var elm_body 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/chargegenerated_without_tax" );
	var row_count   = elm_body.childNodes.length;
	if( row_count == 0 ) return "该单据没有固定扣项"
	
	var table = new AW.XML.Table;
 	
	table.setXML( table.getXMLContent(elm_body) );
	
	var columnNames = [ "单据号", "门店编号 ", "门店名称", "扣项代码", "扣项名称", "扣项金额", "扣项属性","扣项说明"];
	
	var obj = new AW.UI.Grid;
	obj.setId( "grid_cs_generated" );
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );
	var val_number = new AW.Formats.Number;
	val_number.setTextFormat( "#,###.00" );		
	var str		= new AW.Formats.String;	
	obj.setCellFormat( [ str, str, str, str, str, val_number, str, str] );
	obj.setSelectionMode("single-row");
	obj.setCellModel(table);
	return  obj.toString();
}

/**
 * 显示发票信息
 */
function show_invoice()
{
	if ( div_invoice.innerHTML == '' )
	{
		div_invoice.innerHTML = load_invoice();
	}
}

/**
 * 从后台取发票信息
 */
function load_invoice()
{	
	var parms = new Array();
	parms.push( "sheetid=" + sheetid );
	parms.push( "sheetname=paymentnote" );
	parms.push( "section=invoice_set" );
	parms.push( "timestamp=" + new Date().getTime() );
	var url = "../DaemonShowPayment?" + parms.join( '&' );
	
	island4result.async = false;
	island4result.load( url );	
	return island4result.transformNode( format_invoice.documentElement );
}

</script>
</head>

<body onload="init()">
	<div id="div_tabs"></div>
	<div id="div_head"></div>
	<div id="div_sheetset"></div>
	<div id="div_with_tax"></div>
	<div id="div_without_tax"></div>
	<div id="div_cs_generated_with_tax"></div>
	<div id="div_cs_generated_without_tax"></div>
	<div id="div_invoice"></div>
</body>
</html>
