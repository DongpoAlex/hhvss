<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid=3020205;
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
<%
	SelBook sel_book = new SelBook(token);
	sel_book.setAttribute( "id", "txt_bookno" );

	int[] shoptype = { 11, 22 };
	String note = "任意门店";
	SelBranchAll sel_branch = new SelBranchAll(token, shoptype, note );
	sel_branch.setAttribute( "id", "txt_shopid" );
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>待结算扣项查询－零售商</title>
<script language="javascript" src="../js/ajax.js" type="text/javascript"></script>
<script language="javascript" src="../js/XErr.js" type="text/javascript"></script>
<script language="javascript" src="../js/Date.js" type="text/javascript"></script>
<script language="javascript" src="../js/Number.js"
	type="text/javascript"></script>
<!-- ActiveWidgets stylesheet and scripts -->
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css" />
<!-- grid format -->
<style type="text/css">
.aw-grid-control {
	height: 75%;
	width: 100%;
	border: none;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

.aw-mouseover-row {
	background: #efefff;
}

.aw-column-1 {
	width: 80px;
}

.aw-column-2 {
	width: 60px;
}

.aw-column-3 {
	width: 100px;
}

.aw-column-4 {
	width: 80px;
	text-align: right;
}

.aw-column-5 {
	width: 80px;
}

.aw-column-6 {
	width: 100px;
}

.aw-column-7 {
	width: 80px;
}

.aw-column-8 {
	width: 80px;
}

.aw-column-9 {
	width: 80px;
}

.aw-column-10 {
	width: 80px;
}

.aw-column-11 {
	width: 80px;
}

.aw-column-12 {
	width: 150px;
}

.aw-column-13 {
	width: 80px;
}
</style>

<xml id="island4result" />

<script language="javascript" type="text/javascript">
extendDate();


/**
 * 提交查询
 */
function search_chargesum()
{	
	setLoading( true );
	var parms = new Array();
	if( $("txt_venderid").value != '' ) parms.push( "venderid="+ $("txt_venderid").value );
	if( $("txt_bookno").value != '' ) parms.push( "bookno="+ $("txt_bookno").value );
	if( $("txt_shopid").value != '' ) parms.push( "shopid="+ $("txt_shopid").value );
	if( $("txt_date_min").value != '' ) parms.push( "sdate_min="+ $("txt_date_min").value );
	if( $("txt_date_max").value != '' ) parms.push( "sdate_max="+ $("txt_date_max").value );
	var url  = "../DaemonSearchSheet?sheetname=chargesum&" + parms.join( "&" );

	var courier = new AjaxCourier( url );
	courier.reader.read 	= analyse_catalogue;
	courier.call();	
}

function analyse_catalogue( text )
{
	$("island4result").loadXML( text );
	
	var elm_err = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" ) 
	{
		$("div_report").innerHTML = getGrid();
	}
	else {	
		$("div_report").innerHTML = xerr.note;
	}
	setLoading(false);
}

function getGrid(){
	var node_list = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xout/sheet" );
	var row_count = node_list.childNodes.length;
	$("div_count").innerText = "记录总数："+row_count+"条";
	if( row_count == 0 ) return "没有符合要求的记录";
	var grid = new AW.Grid.Extended;	
	var table = new AW.XML.Table;
	table.setXML( table.getXMLContent(node_list) );
	table.setColumns(
	["noteno","venderid","chargecodeid","chargename","chargeamt","bookname","shopname","majorid","categoryname", "flag",
	"docdate","reckoningdate","noteremark","settlemode","invoicemode","buyer"]
	);
	var columnNames = ["单据号","供应商","扣项代码","扣项名称","扣项金额","分公司","门店名","品类编码","品类名","扣项状态", 
	"制单日期","财务确认日期","扣项说明","帐扣属性","票扣属性","业务员"]
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	var num	= new AW.Formats.Number;
	num.setTextFormat( "#." );
	var num2	= new AW.Formats.Number;
	var str	= new AW.Formats.String;	
	grid.setCellModel(table);
	grid.setFooterText(["小计:","","","",normalize_number(table.getCellSum(4))]);
	grid.setFooterVisible(true);
	grid.setSelectionMode("single-row");
	return grid.toString();
}

function downExcel(){
var parms = new Array();
	if( $("txt_venderid").value != '' ) parms.push( "venderid="+ $("txt_venderid").value );
	if( $("txt_bookno").value != '' ) parms.push( "bookno="+ $("txt_bookno").value );
	if( $("txt_shopid").value != '' ) parms.push( "shopid="+ $("txt_shopid").value );
	if( $("txt_date_min").value != '' ) parms.push( "sdate_min="+ $("txt_date_min").value );
	if( $("txt_date_max").value != '' ) parms.push( "sdate_max="+ $("txt_date_max").value );
	var url  = "../DaemonDownloadExcel?operation=chargesum&" + parms.join( "&" );

window.location.href = url;
}
</script>
</head>
<body>
	<div id="title">待结算扣项查询－零售商</div>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader" width="15%">供应商编码</td>
			<td class="tableheader" width="15%">选择分公司</td>
			<td class="tableheader" width="15%">按门店</td>
			<td class="tableheader" width="30%">按应结日期范围</td>
			<td class="tableheader">查询</td>
		</tr>
		<tr>
			<td class="altbg2"><input id="txt_venderid" value="" type="text"
				size="15" /></td>
			<td class="altbg2"><%=sel_book %></td>
			<td class="altbg2"><%=sel_branch %></td>
			<td class="altbg2"><input type="text" id="txt_date_min"
				onblur="checkDate(this)" size="15" /> － <input type="text"
				id="txt_date_max" onblur="checkDate(this)" size="15" /></td>
			<td class="altbg2"><script type="text/javascript">
					var button = new AW.UI.Button;
					button.setControlText("查询");
					button.setControlImage( "search" );
					button.onClick = search_chargesum;
					document.write( button );
					</script> <a href="javascript:downExcel();">导出查询结果</a></td>
		</tr>
		<tr id="header1_toggle" class="singleborder">
			<td colspan="4"></td>
		</tr>
		<tr id="header2_toggle" class="whiteborder">
			<td colspan="4"></td>
		</tr>
	</table>
	<div id="div_count"></div>
	<div id="div_report"></div>
</body>
</html>
