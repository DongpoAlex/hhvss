<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	final int moduleid=3020201;
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
<script language="javascript" src="../js/Number.js"
	type="text/javascript"> </script>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>

<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<style type="text/css">
.aw-grid-control {
	height: 76%;
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
</style>

<script language="javascript" type="text/javascript">
extendDate();

/**
 * 根据用户输入的过滤条件, 查询单据目录.
 */
function search_sheet()
{
	var parms = new Array();
	if( $("txt_venderid").value != '' )
		parms.push( "venderid=" + $("txt_venderid").value );
		
	if( $("txt_bookno").value != '' )
		parms.push( "bookno=" + $("txt_bookno").value );
		
	if( $("txt_sheetid").value != '' ) 
		parms.push( "sheetid=" + $("txt_sheetid").value );
		
	if( $("txt_editdate_min").value != '' ) 
		parms.push( "editdate_min=" + $("txt_editdate_min").value );
		
	if( $("txt_editdate_max").value != '' ) 
		parms.push( "editdate_max=" + $("txt_editdate_max").value );
		
	if( parms.length == 0 ){
		alert( "请至少选择一个查询条件！" );
		return;
	}
	
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
	island4result.loadXML( text );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	
	if( xerr.code == "0" ) 
	{    	 
		var elm_row 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
			var row_count=elm_row.childNodes.length;
			if( row_count==0 )
				div_report.innerText = "数据库没有你要查找的数据!请检查你输入的数据是否正确。";
			else{
				show_catalogue();
		}	
	}
	else {	
		div_report.innerText = xerr.note;
	}
	
	setLoading( false );
}

/**
 * 显示单据目录
 */
function show_catalogue()
{
	var table = new AW.XML.Table;
	table.setTable("/xdoc/xout/catalogue");
	table.setXML(island4result.xml);
	table.setColumns(["sheetid","bookname","editdate","refsheetid","lognote","venderid"]);
	var row_count = table.getCount();
	var columnNames=[ "单据号", "分公司", "制单日期", "付款单编码", "备注", "供应商编码"];
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
	obj.sort(0,"descending");
	obj.setFooterText(["查询结果:"+row_count+"条"]);
	obj.setFooterVisible(true);
	div_report.innerHTML = obj.toString();
	setLoading( false );
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


function reportLiquidationLog(){
	var parms = new Array();
	if( $("txt_editdate_min").value != '' ) 
		parms.push( "editdate_min=" + $("txt_editdate_min").value );
		
	if( $("txt_editdate_max").value != '' ) 
		parms.push( "editdate_max=" + $("txt_editdate_max").value );
		
	if( parms.length == 0 ){
		alert( "请填写日期范围！" );
		return;
	}
	
	var url = "../DaemonDownloadExcel?operation=liquidationlog&"+ parms.join( '&' );
	
	window.location.href = url;

}
</script>

<xml id="island4result" />
</head>
<body>
	<div id="title">对帐单查询－零售商专用</div>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr>
			<td class="tableheader">对帐申请单号</td>
			<td class="tableheader">供应商编码</td>
			<td class="tableheader">分公司</td>
			<td class="tableheader">制单日期</td>
		</tr>
		<tr id="header0_toggle" style="DISPLAY: block">
			<td class="altbg2"><input type="text" id="txt_sheetid" /></td>
			<td class="altbg2"><input type="text" id="txt_venderid"></input></td>
			<td class="altbg2"><%=sel_book%></td>
			<td class="altbg2">从<input type="text" id="txt_editdate_min"
				onchange="checkDate(this)" /> 到<input type="text"
				id="txt_editdate_max" onchange="checkDate(this)" />
			</td>
		</tr>
		<tr class="singleborder">
			<td colspan="4"></td>
		</tr>
		<tr class="whiteborder">
			<td colspan="4"></td>
		</tr>
		<tr class="header">
			<td><script type="text/javascript">
				var button = new AW.UI.Button;
				button.setControlText("查询");
				document.write(button);
				button.onClick = search_sheet;
			</script> <a href="javascript:reportLiquidationLog()">按制单日期范围导出对帐情况列表</a></td>
			<td colspan="3" class="altbg2"></td>
		</tr>
	</table>
	<div id="div_report"></div>
</body>
</html>
