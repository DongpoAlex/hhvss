<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.SelBook"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid=3020105;
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
	SelBook ctrl_book = new SelBook(token);
	ctrl_book.setAttribute( "name", "txt_bookno" );
%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>为付款单录入发票</title>
<script language="javascript" src="../js/ajax.js" type="text/javascript"></script>
<script language="javascript" src="../js/XErr.js" type="text/javascript"></script>
<script language="javascript" src="../js/Date.js" type="text/javascript"></script>
<!-- ActiveWidgets stylesheet and scripts -->
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css" />
<!-- grid format -->
<style type="text/css">
.aw-grid-control {
	height: 100%;
	width: 100%;
	border: none;
	font: menu;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

#myGrid {
	background-color: #F9F8F4
}

#myGrid .aw-column-0 {
	width: 130px;
	cursor: pointer;
}

#myGrid .aw-column-1,#myGrid .aw-column-2 {
	text-align: right;
}

#title {
	text-align: center;
	color: #42658D;
	font-size: 16px;
	font-family: '楷体_GB2312';
	font-weight: bold;
}
</style>

<xml id="data4book" />
<xml id="island4cat" />

<script language="javascript" type="text/javascript">
extendDate();

var venderid=null;  
var g_venderid=null;
var g_sheetid=null;
var g_bookno=null;
var c_sheetid;
var obj = new AW.Grid.Extended;

/////////////////////////////////////////////////////////////////

/**
 * 查询加载数据
 */
function load_catalogue()
{	

	var sheetid  	= txt_sheetid.value;
	var bookno   	= txt_bookno.value;
	var start_date  = txt_editdate_min.value;
	var end_date   	= txt_editdate_max.value;
	setLoading(true,"正在查询");	
	var parms = new Array();
	parms.push( "sheetname=pn4vi" );
	if( sheetid != "" ) 	parms.push( "sheetid="  + sheetid );
	if( bookno != "" ) 	parms.push( "bookno="   + bookno );
	if( start_date != "" ) 	parms.push( "editdate_min="   + start_date );
	if( end_date != "" ) 	parms.push( "editdate_max="   + end_date );

	var url_pay  = "../DaemonSearchSheet?" + parms.join( '&' );	

	var courier = new AjaxCourier( url_pay );
	courier.reader.read 	= analyse_catalogue;
	courier.call();	
}

function analyse_catalogue( text )
{
	island4cat.loadXML( text );
	
	var elm_err = island4cat.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" ) 
	{    	 
		var elm_row 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	  	var row_count   = elm_row.childNodes.length;
	  	if( row_count== 0 )
	  		div_report.innerText = "没有找到需要处理的数据。";
	  	else{
			display();
		}	
	}
	else {	
		div_report.innerText = xerr.note;	
	}
	setLoading(false);
}

/**
* Display ActiveWidgets DataGrid
*/
function display()
{
	var node_list = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	var row_count = node_list.childNodes.length;
	data4book.loadXML( node_list.xml );
	var node = document.getElementById( "data4book" );
	var table = new AW.XML.Table;
	table.setXML( node );
	table.setColumns(["sheetid","chargeamt","payamt","planpaydate","editor","payflag"]);	
	var columnNames = ["单据号","本单费用合计","本单应付金额","计划付款日期","制单人",""];	

	obj.setId( "myGrid" );
	obj.setColumnCount(5);
	obj.setRowCount( row_count );
	obj.setHeaderText(columnNames);
	var str = new AW.Formats.String;	
	var number = new AW.Formats.Number;
	number.setTextFormat("#,###.##");
	obj.setCellFormat([str,number,number,str,str]);		

	obj.onCellClicked = function(event, column, row){
		var sheetid = obj.getCellValue(0,row);
		var payflag = obj.getCellValue(5,row);
		if(payflag==1){
			alert("该公司单据冻结，请联系采购!");
		}else{
			openVenderInvoiveWnd( sheetid );
		}
	};
	obj.setCellModel(table);
	obj.sort(0,"descending"); //按sheetid降序
	obj.setFooterText(["查询结果:"+row_count+"条"]);
	obj.setFooterVisible(true);
	div_report.innerHTML = obj.toString();	
}


/**
 * 打开一个新窗口, 供用户录入具体付款申请单的发票.
 */
function openVenderInvoiveWnd(sheetid)
{
	var attributeOfNewWnd = "status=yes,toolbar=no,menubar=no,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
	window.open( "../venderinvoice/venderinvoice_input.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);
}

</script>
</head>
<body>
	<div id="title">供应商待录入发票查询</div>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader">请选择分公司</td>
			<td class="tableheader">请输入单据号(可选)</td>
			<td class="tableheader">请输入制单日期(可选)</td>
		</tr>
		<tr id="header0_toggle">
			<td class="altbg2"><%=ctrl_book %></td>
			<td class="altbg2"><input type="text" name="txt_sheetid"
				id="txt_sheetid" size="16" /></td>
			<td class="altbg2">从 <input type="text" name="txt_editdate_min"
				id="txt_editdate_min" onblur="checkDate(this)" /> 到 <input
				type="text" name="txt_editdate_max" id="txt_editdate_max"
				onblur="checkDate(this)" />
			</td>
		</tr>
		<tr id="header1_toggle" class="singleborder">
			<td colspan="4"></td>
		</tr>
		<tr id="header2_toggle" class="whiteborder">
			<td colspan="4"></td>
		</tr>
		<tr id="header3_toggle" class="header">
			<td><script type="text/javascript">
					var button = new AW.UI.Button;
					button.setControlText("查询");
					button.setControlImage( "search" );
					button.onClick = load_catalogue;
					document.write( button );
					</script></td>
			<td colspan="4" class="altbg2">
				<div id="divVDinfo" class="smalltxt"></div>
			</td>
		</tr>
	</table>
	<br />
	<div id="div_report"></div>
</body>
</html>
