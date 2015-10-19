<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 9999912;
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
	SelBranchGroup sel_branch = new SelBranchGroup(token, shoptype, note );
	sel_branch.setAttribute( "id", "txt_shopid" );
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>零售商促销明细查询</title>
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
	width: 100px;
}

.aw-column-4 {
	width: 200px;
}

.aw-column-5 {
	width: 80px;
	text-align: right;
}

.aw-column-6 {
	width: 80px;
	text-align: right;
}

.aw-column-7 {
	width: 80px;
	text-align: right;
}

.aw-column-8 {
	width: 80px;
}

.aw-column-9 {
	
}
</style>

<xml id="island4result" />

<script language="javascript" type="text/javascript">
extendDate();
extendNumber();

function check_date()
{
	try{
		var sdate_min = $("txt_sdate_min").value;
		var sdate_max = $("txt_sdate_max").value;
		if( sdate_min == '' ){
			alert("必须指定最小销售日期");
			$("txt_sdate_min").focus();
			return;
		}
		if( sdate_max == '' ){
			alert("必须指定最大销售日期");
			$("txt_sdate_max").focus();
			return;
		}
		var min_sdate = parseDate(sdate_min);
		var max_sdate = parseDate(sdate_max);
		
		if(min_sdate > max_sdate){
			alert("起始日期不能大于结束日期！");
			return;
		}
		
		if((max_sdate-min_sdate)/86400000 > 60 ){	
			alert("只能查询60天内数据，请修改日期范围！");
			return;
		}
		
		return true;
	
	} catch( e ) {
		alert( e );
		return false;
	}
}


/**
 * 提交查询
 */
function search_report()
{
	if( !check_date() ) return;
	
	var parms = new Array();
	var shopid = $("txt_shopid").value;
	if( shopid != '' ){
		var arrShopid = shopid.split(',');
		for(var i=0; i<arrShopid.length; i++){
			parms.push( "shopid="+arrShopid[i]);
		}
	}
	if( $("txt_venderid").value != '' ){
		parms.push( "venderid="+ $("txt_venderid").value );
	}else{
		alert("必须填写供应商编码");
		return;
	}
	if( $("txt_sdate_min").value != '' ) parms.push( "sdate_min="+ $("txt_sdate_min").value );
	if( $("txt_sdate_max").value != '' ) parms.push( "sdate_max="+ $("txt_sdate_max").value );
	var url  = "../DaemonSearchHisSheet?sheetname=promshare&" + parms.join( "&" );
	var courier = new AjaxCourier( url );
	courier.reader.read 	= analyse_catalogue;
	courier.call();
		setLoading( true );
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
	var node_list = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xout/promshare" );
	var row_count = node_list.childNodes.length;
	if( row_count == 0 ) return "没有符合要求的记录";
	var grid = new AW.Grid.Extended;	
	var table = new AW.XML.Table;
	table.setXML( table.getXMLContent(node_list) );
	table.setColumns(
	["shopid","shopname", "goodsid", "barcode", "goodsname","qty",
	"costv", "disccostv","disctype", "categoryid","categoryname","sdate"]
	);	
	var columnNames = ["门店编码", "门店名称", "商品编码", "商品条码", "商品名称", "销售数量",
	"成本金额", "促销成本", "促销类型", "品类编码", "品类名称","销售日期"]
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );
	grid.setHeaderText(columnNames);
	var num	= new AW.Formats.Number;
	var num1= new AW.Formats.Number;
	var num2= new AW.Formats.Number;
	num1.setTextFormat( "#." );
	num2.setTextFormat( "#,###.00" );
	var str	= new AW.Formats.String;	
	grid.setCellFormat( [ str, str, num, num, str, num2,num2,num2 ] );
	grid.setCellModel(table);
	grid.setFooterVisible(true);
	grid.setFooterText(["小计:","","","","",normalize_number(table.getCellSum(5)),normalize_number(table.getCellSum(6)), normalize_number(table.getCellSum(7))]);
	grid.setFooterVisible(true);
	grid.setSelectionMode("single-row");

	var sumcount = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xout/promshare" ).getAttribute("count");
	
	return "查询结果：总计："+sumcount+"行， 显示："+row_count+"行"+ grid.toString();
}

function downExcel(){

	if( !check_date() ) return;
	
	var parms = new Array();
	var shopid = $("txt_shopid").value;
	if( shopid != '' ){
		var arrShopid = shopid.split(',');
		for(var i=0; i<arrShopid.length; i++){
			parms.push( "shopid="+arrShopid[i]);
		}
	}
	if( $("txt_venderid").value != '' ){
		parms.push( "venderid="+ $("txt_venderid").value );
	}else{
		alert("必须填写供应商编码");
		return;
	}
	if( $("txt_sdate_min").value != '' ) parms.push( "sdate_min="+ $("txt_sdate_min").value );
	if( $("txt_sdate_max").value != '' ) parms.push( "sdate_max="+ $("txt_sdate_max").value );
	var url  = "../DaemonDownloadHisExcel?operation=promshare&" + parms.join( "&" );
	
	window.location.href = url;

}
</script>
</head>
<body>
	<div id="title">零售商转类前促销明细查询</div>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader" width="20%">供应商 <span
				style="color: red; font: bold">*</span>
			</td>
			<td class="tableheader" width="20%">选择门店</td>
			<td class="tableheader" width="30%">销售日期（限定为60天以内）</td>
			<td class="tableheader">查询</td>
		</tr>
		<tr>
			<td class="altbg2"><input id="txt_venderid" value="" type="text"
				size="15" /></td>
			<td class="altbg2"><%=sel_branch %></td>
			<td class="altbg2"><input type="text" size="12"
				id="txt_sdate_min" onblur="checkDate(this)" /> - <input type="text"
				id="txt_sdate_max" onblur="checkDate(this)" size="12" /></td>
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
