﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3010141;
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
	int[] shoptype = { 11,22 };
	String note = "任意门店";
	SelBranchAll sba = new SelBranchAll( token,shoptype, note );
	sba.setAttribute( "id", "txt_shopid" );
	
	SelBook sel_book = new SelBook(token);
	sel_book.setAttribute( "id", "txt_bookno" );
%>
<?xml version="1.0" encoding="UTF-8"?>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>退货单查询-供应商专用</title>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<script language="javascript" src="../js/ajax.js" type="text/javascript"></script>
<script language="javascript" src="../js/XErr.js" type="text/javascript"></script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<xml id="island4result" />
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
	width: 60px;
}

.aw-column-2 {
	width: 80px;
}

.aw-column-3 {
	width: 60px;
}

.aw-column-4 {
	
}

.aw-column-5 {
	width: 60px;
}

.aw-column-6 {
	
}

.aw-column-7 {
	width: 80px;
}

.aw-column-8 {
	width: 120px;
}

.aw-column-9 {
	text-align: right;
	color: blue
}

.aw-column-10 {
	
}

.aw-column-11 {
	width: 400px;
}

.aw-column-12 {
	width: 200px;
}
</style>
<script type="text/javascript">
		extendDate();
		
		function praseURL(){
			var parms = new Array();
			if($("txt_bookno").value!='') parms.push("bookno="+$("txt_bookno").value);
			if($("txt_shopid").value!='') parms.push("shopid="+$("txt_shopid").value);
			if($("txt_sheetid").value!='') parms.push("sheetid="+$("txt_sheetid").value);
			if($("txt_docdatemin").value!='') parms.push("docdate_min="+$("txt_docdatemin").value);
			if($("txt_docdatemax").value!='') parms.push("docdate_max="+$("txt_docdatemax").value);
			parms.push( "sheetname=ret" );
			var url="../DaemonSearchSheet?"+ parms.join( '&' );
			
			return url;
		}
		
		function search_receipt(){
			var url =praseURL();
			var courier = new AjaxCourier( url );
			courier.reader.read 	= analyse_catalogue;
			courier.call();
			setLoading( true );
		}
		
		function analyse_catalogue( text ){
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
			var node_list = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
			var row_count = node_list.childNodes.length;
			$("div_count").innerText = "记录总数："+row_count+"条";
			if( row_count == 0 ) return "没有符合要求的记录";
			var grid = new AW.Grid.Extended;	
			var table = new AW.XML.Table;
			table.setXML( node_list.xml );
			table.setColumns(
			["bookname", "shopid", "shopname", "majorid","categoryname","logisticsid","paytypename","docdate","sheetid","unpaidamt"]
			);	
			var columnNames = ["分公司", "门店编码", "门店名称", "课类编码","课类名称","物流模式","结算方式","发生日期","单据编码","金额（含税）"];
			grid.setColumnCount(columnNames.length);
			grid.setRowCount( row_count );
			grid.setHeaderText(columnNames);
			var num	= new AW.Formats.Number;
			var num1= new AW.Formats.Number;
			var num2= new AW.Formats.Number;
			num1.setTextFormat( "#." );
			num2.setTextFormat( "#,###.00" );
			var str	= new AW.Formats.String;	
			grid.setCellFormat( [ str,str, str, str, str, str,str,str,str,num2,str ] );
			grid.setCellModel(table);
			grid.setFooterVisible(true);
			grid.setFooterText(["小计:","","","","","","","","",num2.dataToText(table.getCellSum(9))]);
			grid.setFooterVisible(true);
			grid.setSelectionMode("single-row");
			return grid.toString();
		}
		
		function downExcel(){
			var parms = new Array();
			if($("txt_bookno").value!='') parms.push("bookno="+$("txt_bookno").value);
			if($("txt_shopid").value!='') parms.push("shopid="+$("txt_shopid").value);
			if($("txt_sheetid").value!='') parms.push("sheetid="+$("txt_sheetid").value);
			if($("txt_docdatemin").value!='') parms.push("docdate_min="+$("txt_docdatemin").value);
			if($("txt_docdatemax").value!='') parms.push("docdate_max="+$("txt_docdatemax").value);
			parms.push( "operation=ret" );
			var url="../DaemonDownloadExcel?"+ parms.join( '&' );
			
			window.location.href = url;
		}
		
		</script>
</head>

<body>

	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader" width="15%">选择分公司</td>
			<td class="tableheader" width="15%">选择门店</td>
			<td class="tableheader" width="15%">根据单据编码</td>
			<td class="tableheader" width="25%">验收单日期范围</td>
		</tr>
		<tr>
			<td class="altbg2"><%=sel_book%></td>
			<td class="altbg2"><%=sba%></td>
			<td class="altbg2"><input type="text" id="txt_sheetid" /></td>
			<td class="altbg2"><input type="text" id="txt_docdatemin"
				onblur="checkDate(this)" /> — <input type="text"
				id="txt_docdatemax" onblur="checkDate(this)" /></td>
		</tr>
		<tr id="header1_toggle" class="singleborder">
			<td colspan="4"></td>
		</tr>
		<tr id="header2_toggle" class="whiteborder">
			<td colspan="4"></td>
		</tr>

		<tr>
			<td colspan="4"><script type="text/javascript">
						var button = new AW.UI.Button;
						button.setControlText("查询");
						button.setControlImage( "search" );
						button.onClick = search_receipt;
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
