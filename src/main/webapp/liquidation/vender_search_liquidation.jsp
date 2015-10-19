<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	final int moduleid=3020103;
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
	sel_book.setAttribute( "name", "txt_bookno" );
%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title>对帐单查询</title>
<script language="javascript" src="../js/ajax.js" type="text/javascript"> </script>
<script language="javascript" src="../js/XErr.js" type="text/javascript"> </script>
<script language="javascript" src="../js/Date.js" type="text/javascript"> </script>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>

<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<style type="text/css">
.aw-grid-control {
	height: 70%;
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

#title {
	text-align: center;
	color: #42658D;
	font-size: 16px;
	font-family: '楷体_GB2312';
	font-weight: bold;
}
</style>

<script language="javascript" type="text/javascript">
extendDate();

/**
 * 根据用户输入的过滤条件, 查询单据目录.
 */
function search_sheet()
{
	var parms = new Array();
	if( txt_bookno.value != '' )
		parms.push( "bookno=" + txt_bookno.value );
		
	if( txt_sheetid.value != '' ) 
		parms.push( "sheetid=" + txt_sheetid.value );
		
	if( txt_edit_startdate.value != '' ) 
		parms.push( "editdate_min=" + txt_edit_startdate.value );
		
	if( txt_edit_enddate.value != '' ) 
		parms.push( "editdate_max=" + txt_edit_enddate.value );
		
	load_catalogue( parms );
}


/**
 * 访问后台, 查询单据目录.
 */
function load_catalogue ( parms )
{
	setLoading( true );
	parms.push( "sheetname=liquidation" );
	parms.push( "timestamp=" + new Date().getTime() );
	
	var url = "../DaemonSearchSheet?" + parms.join( '&' );
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_catalogue;
	courier.call();	
}

/**
 * 解析后台发来的数据, 取出单据目录.
 */
function analyse_catalogue( text )
{
	island4cat.loadXML( text );
	island4result.loadXML( text );
	var elm_err 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	
	if( xerr.code == "0" ) 
	{    	 
		var elm_row 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
			var row_count=elm_row.childNodes.length;
			if( row_count==0 )
				div_report.innerText = "数据库没有你要查找的数据!请检查你输入的数据是否正确。";
			else{
				var node = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
				var table = new AW.XML.Table;
				island4cat.loadXML( table.getXMLContent(node) );
				show_catalogue();
		}	
	}
	else {	
		div_report.innerText = "系统忙，请稍后再试！info:"+xerr.note;
	}
	setLoading( false );
}

/**
 * 显示单据目录
 */
function show_catalogue()
{
	window.status = "显示目录 ...";
	setLoading( true );
	arr_sheetid = new Array();
	var node_sheetid 	= island4result.XMLDocument.selectNodes( "/xdoc/xout/catalogue/row/sheetid" );	
	var next_node = node_sheetid.nextNode();
	while( next_node != null ) {
		arr_sheetid[ arr_sheetid.length ] = next_node.text;
		next_node = node_sheetid.nextNode();
	}
	if( arr_sheetid !=null && arr_sheetid.length >0 ) current_sheetid = arr_sheetid[0];
	
	var node_body 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );	
	var row_count   = node_body.childNodes.length;
	var table = new AW.XML.Table;
	island4cat.loadXML( table.getXMLContent(node_body) );
	
	var node = document.getElementById( "island4cat" );		
	
	table.setXML(node);
	table.setColumns(["sheetid","bookname","editdate","refsheetid","lognote"]);
	var columnNames=[ "单据号", "分公司", "制单日期", "付款单编码", "备注"];

	var obj = new AW.UI.Grid;
	obj.setId( "myGrid" );
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );	
	var obj_link = new AW.Templates.Link;
		obj_link.setEvent( "onclick",
		function(){
			var current_row = obj.getCurrentRow();
			var sheetid = obj.getCellValue( 0, current_row );
			open_detail( sheetid );
		}
	);
	obj.setCellTemplate( obj_link, 0 ); 
	obj.setCellModel(table);
	obj.sort(0,"descending")
	obj.setFooterText(["查询结果:"+row_count+"条"]);
	obj.setFooterVisible(true);
	div_report.innerHTML = obj.toString();
	window.status = "OK";
}

/**
 * 打开新窗口, 显示单据明细.
 */
function open_detail(sheetid)
{		
	var attributeOfNewWnd = "status=yes,toolbar=no,menubar=no,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width)+",height="+(window.screen.height);
 	window.open( "../liquidation/liquidation_detail.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);		
}
</script>


</head>
<xml id="island4result" />
<xml id="island4cat" />

<body>
	<div id="title">供应商对帐单查询</div>


	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr style="CURSOR: hand" class="tableheader" title="展开/折叠"
			onclick="ToggleTableHeader(0,3);">
			<td class="tableheader">请选择分公司</td>
			<td class="tableheader">请输入单据号(可选)</td>
			<td class="tableheader">请输入制单日期(可选)</td>
		</tr>
		<tr id="header0_toggle" style="DISPLAY: block">
			<td class="altbg2"><%=sel_book%></td>
			<td class="altbg2"><input type="text" name="txt_sheetid"
				id="txt_sheetid" size="16" /></td>
			<td class="altbg2">从<input type="text" name="txt_edit_startdate"
				id="txt_edit_startdate" onchange="checkDate(this)" /> 到<input
				type="text" name="txt_edit_enddate" id="txt_edit_enddate"
				onchange="checkDate(this)" /></td>
		</tr>
		<tr id="header1_toggle" style="DISPLAY: block" class="singleborder">
			<td colspan="3"></td>
		</tr>
		<tr id="header2_toggle" style="DISPLAY: block" class="whiteborder">
			<td colspan="3"></td>
		</tr>
		<tr id="header3_toggle" style="DISPLAY: block" class="header">
			<td><script type="text/javascript">
			var button = new AW.UI.Button;
			button.setControlText("查询");
			button.setControlImage("search");		
			document.write(button);
			button.onClick = search_sheet;
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
