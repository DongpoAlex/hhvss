<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3030101;
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
	SelBranchGroup sel_branch = new SelBranchGroup( token,shoptype, note );
	sel_branch.setAttribute( "id", "txt_shopid" );
	
	String cols = "['shopid','shopname', 'goodsid', 'barcode', 'goodsname','qty','grosscostvalue','grosssalevalue', 'categoryid','categoryname','sdate']";
	String colsName="['门店编码', '门店名称', '商品编码','商品条码','商品名称', '销售数量', '成本金额', '销售金额','小类编码', '小类名称','销售日期']";
	String foot = "['小计:','','','','',table.getCellSum(5),normalize_number(table.getCellSum(6)),normalize_number(table.getCellSum(7))]";
	if(token.site.getSid()==2){
		cols = "['shopid','shopname', 'goodsid', 'barcode', 'goodsname','qty','grosscostvalue', 'categoryid','categoryname','sdate']";
		colsName="['门店编码', '门店名称', '商品编码','商品条码','商品名称', '销售数量', '成本金额','小类编码', '小类名称','销售日期']";
		foot = "['小计:','','','','',table.getCellSum(5),normalize_number(table.getCellSum(6))]";
	}else{
		
	}
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>供应商商品销售日报查询</title>
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

.aw-column-0 {
	width: 60px;
}

.aw-column-1 {
	width: 80px;
}

.aw-column-2 {
	width: 60px;
}

.aw-column-3 {
	
}

.aw-column-4 {
	width: 200px;
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
var win_download = null;
var columnNames = <%=colsName%>;
var columns = <%=cols%>;
function init(){
}
/**
 * 提交查询
 */
function search_report()
{
	if( $("txt_min_sdate").value == '' ){
		alert("必须指定最小销售日期");
		$("txt_min_sdate").focus();
		return;
	}
	if( $("txt_max_sdate").value == '' ){
		alert("必须指定最大销售日期");
		$("txt_max_sdate").focus();
		return;
	}
	var min_sdate = parseDate($("txt_min_sdate").value);
	var max_sdate = parseDate($("txt_max_sdate").value);
	
	if(min_sdate > max_sdate){
		alert("起始日期不能大于结束日期！");
		return;
	}
	if((max_sdate-min_sdate)/86400000 > 60 ){	
		alert("只能查询60天内数据，请修改日期范围！");
		return;
	}
	
	setLoading( true );
	var parms = new Array();
	//parms.push("sale_day="+(min_y*100+min_m+1) );
	var shopid = $("txt_shopid").value;
	if( shopid != '' ){
		var arrShopid = shopid.split(',');
		for(var i=0; i<arrShopid.length; i++){
			parms.push( "shopid="+arrShopid[i]);
		}
	}
	if( $("txt_goodsid").value != '' ) parms.push( "goodsid="+ $("txt_goodsid").value );
	if( $("txt_min_sdate").value != '' ) parms.push( "min_sdate="+ $("txt_min_sdate").value );
	if( $("txt_max_sdate").value != '' ) parms.push( "max_sdate="+ $("txt_max_sdate").value );
	var url  = "../DaemonSearchHisSheet?sheetname=sdaily&" + parms.join( "&" );
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
	else{
		$("div_report").innerHTML = xerr.note;
	}
	setLoading(false);
}

function getGrid(){
	var node_list = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xout/report" );
	var row_count = node_list.childNodes.length;
	
	if( row_count == 0 ) return "没有符合要求的记录";
	var grid = new AW.Grid.Extended;
	var table = new AW.XML.Table;
	table.setXML( table.getXMLContent(node_list) );
	table.setColumns(columns);	
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );
	grid.setHeaderText(columnNames);
	var num	= new AW.Formats.Number;
	var num1= new AW.Formats.Number;
	var num2= new AW.Formats.Number;
	num1.setTextFormat( "#." );
	num2.setTextFormat( "#,###.00" );
	var str	= new AW.Formats.String;	
	grid.setCellFormat( [ str, str, num, num, str,num,num2,num ] );
	grid.setCellModel(table);
	grid.setFooterVisible(true);
	grid.setFooterText( <%=foot%>);
	grid.setSelectionMode("single-row");

	var sumcount = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xout/report" ).getAttribute("count");
	return "查询结果：总计："+sumcount+"行， 显示："+row_count+"行"+grid.toString();
}

function downExcel(){
	if( $("txt_min_sdate").value == '' ){
		alert("必须指定最小销售日期");
		$("txt_min_sdate").focus();
		return;
	}
	if( $("txt_max_sdate").value == '' ){
		alert("必须指定最大销售日期");
		$("txt_max_sdate").focus();
		return;
	}
	var min_sdate = parseDate($("txt_min_sdate").value);
	var max_sdate = parseDate($("txt_max_sdate").value);
	
	if(min_sdate > max_sdate){
		alert("起始日期不能大于结束日期！");
		return;
	}
	if((max_sdate-min_sdate)/86400000 > 60 ){	
		alert("只能查询60天内数据，请修改日期范围！");
		return;
	}
	
	var parms = new Array();
	var shopid = $("txt_shopid").value;
	if( shopid !=null && shopid.length>1 ){
		var arrShopid = shopid.split(',');
		for(var i=0; i<arrShopid.length; i++){
			parms.push( "shopid="+arrShopid[i]);
		}
	}
	
	if( $("txt_goodsid").value != '' ) parms.push( "goodsid="+ $("txt_goodsid").value );
	if( $("txt_min_sdate").value != '' ) parms.push( "min_sdate="+ $("txt_min_sdate").value );
	if( $("txt_max_sdate").value != '' ) parms.push( "max_sdate="+ $("txt_max_sdate").value );
	var url  = "../DaemonDownloadHisExcel?operation=sdaily&" + parms.join( "&" );

	alert( '下载数据需要一段时间，按下确定后请耐心等候。' );	
	window.location.href = url;
	
}


</script>
</head>
<body onload="init()">
	<div id="title">供应商商品销售日报查询</div>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader" width="12%">选择门店</td>
			<td class="tableheader" width="12%">根据商品编码</td>
			<td class="tableheader" width="30%">销售日期范围（能查询60天内的数据）<span
				style="color: red; font: bold">*</span>
			</td>
			<td class="tableheader">查询</td>
		</tr>
		<tr>
			<td class="altbg2"><%=sel_branch%></td>
			<td class="altbg2"><input type="text" id="txt_goodsid" size="10" /></td>
			<td class="altbg2"><input type="text" id="txt_min_sdate"
				onblur="checkDate(this)" size="12" /> 至 <input type="text"
				id="txt_max_sdate" onblur="checkDate(this)" size="12" /></td>
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
