<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 4010000;
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
	int[] shoptype = { 11, 22 };
	String note = "任意门店";
	SelBranchAll sel_branch = new SelBranchAll( token,shoptype, note );
	sel_branch.setAttribute( "id", "txt_shopid" );
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>促销人员考勤信息</title>
<script language="javascript" src="../js/ajax.js" type="text/javascript"></script>
<script language="javascript" src="../js/XErr.js" type="text/javascript"></script>
<script language="javascript" src="../js/Date.js" type="text/javascript"></script>
<script language="javascript" src="../js/Number.js"
	type="text/javascript"></script>
<!-- ActiveWidgets stylesheet and scripts -->
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css" />
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
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

.aw-column-0 {
	
}

.aw-column-1 {
	
}

.aw-column-2 {
	
}

.aw-column-3 {
	
}

.aw-column-4 {
	width: 80px;
}

.aw-column-5 {
	width: 60px;
	text-align: right;
}

.aw-column-6 {
	width: 60px;
	text-align: right;
}

.aw-column-7 {
	width: 80px;
	text-align: right;
}

.aw-column-8 {
	width: 60px;
}

.aw-column-9 {
	
}
</style>

<xml id="island4result" />

<script language="javascript" type="text/javascript">
extendDate();
extendNumber();
function init(){


}
/**
 * 提交查询
 */
function search_report()
{
	if( $("txt_kid").value == '' ){
		alert("必须指定考勤卡号");
		$("txt_kid").focus();
		return;
	}
	setLoading( true );
	var parms = new Array();
	
	if( $("txt_kid").value != '' ) parms.push( "kid="+ $("txt_kid").value );
	if( $("txt_start_date").value != '' ) parms.push( "startdate="+ $("txt_start_date").value );
	if( $("txt_end_date").value != '' ) parms.push( "enddate="+ $("txt_end_date").value );

	var url  = "../DaemonReport?reportname=checkwork&" + parms.join( "&" );
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
	else if( xerr.code == -206 ){
		$("div_report").innerHTML = "没有相关数据";
	}
	else{	
		$("div_report").innerHTML = xerr.note;
	}
	setLoading(false);
}

function getGrid(){
	var node_list = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xout/report" );
	var row_count = node_list.childNodes.length;
	$("div_count").innerText = "记录总数："+row_count+"条";
	if( row_count == 0 ) return "没有符合要求的记录";
	var grid = new AW.Grid.Extended;	
	var table = new AW.XML.Table;
	table.setXML( table.getXMLContent(node_list) );
	table.setColumns(
	["kh", "xm", "rq", "sj","shop"]
	);	
	var columnNames = ["考勤卡号", "姓名","刷卡日期","刷卡时间", "门店"]
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );
	grid.setHeaderText(columnNames);
	var num	= new AW.Formats.Number;
	var num1= new AW.Formats.Number;
	var num2= new AW.Formats.Number;
	num1.setTextFormat( "#." );
	num2.setTextFormat( "#,###.00" );
	var str	= new AW.Formats.String;	
	//grid.setCellFormat( [ str, str, num, num, str,num,num2,num ] );
	grid.setCellModel(table);
	grid.setSelectionMode("single-row");
	return grid.toString();
}

function downExcel(){
	if( $("txt_kid").value == '' ){
		alert("必须指定考勤卡号");
		$("txt_kid").focus();
		return;
	}
	var parms = new Array();
	
	if( $("txt_kid").value != '' ) parms.push( "kid="+ $("txt_kid").value );
	if( $("txt_start_date").value != '' ) parms.push( "startdate="+ $("txt_start_date").value );
	if( $("txt_end_date").value != '' ) parms.push( "enddate="+ $("txt_end_date").value );
	var url  = "../DaemonDownloadReport?reportname=checkwork&" + parms.join( "&" );
	
	alert( '下载数据需要一段时间，按下确定后请耐心等候。' );	
	window.location.href = url;

}
</script>
</head>
<body onload="init()">
	<div id="title">促销人员考勤信息查询</div>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader" width="20%">考勤卡号(必填) <span
				style="color: red; font: bold">*</span>
			</td>
			<td class="tableheader" width="30%">刷卡日期</td>
			<td class="tableheader">查询</td>
		</tr>
		<tr>
			<td class="altbg2"><input type="text" id="txt_kid" /></td>
			<td class="altbg2"><input type="text" id="txt_start_date"
				onblur="checkDate(this)" /> 至 <input type="text" id="txt_end_date"
				onblur="checkDate(this)" /></td>
			<td class="altbg2"><script type="text/javascript">
						var button = new AW.UI.Button;
						button.setControlText("查询");
						button.setControlImage( "search" );
						button.onClick = search_report;
						document.write( button );
					</script> &rarr;<a href="javascript:downExcel()">将查询结果导出到Excel文档</a></td>
		</tr>
		<tr id="header1_toggle" class="singleborder">
			<td colspan="4"></td>
		</tr>
		<tr id="header2_toggle" class="whiteborder">
			<td colspan="4"></td>
		</tr>
	</table>
	<div id="div_count">
		<span style="float: right">提示：本系统只显示查询结果前一千条记录，查询超过一千条请缩小查询范围或以导出查看</span>
	</div>
	<div id="div_report"></div>
</body>
</html>
