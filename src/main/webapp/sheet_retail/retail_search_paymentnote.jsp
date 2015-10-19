<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3020202;
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );
	
	//检查用户操作权限.
	Permission perm = token.getPermission( moduleid );

	SelBook sel_book = new SelBook(token);
	sel_book.setAttribute( "id", "txt_bookno" );
	
	if ( !perm.include( Permission.READ ) ) throw new PermissionException( "您没有操作本模块的授权, 请与管理员联系." );
%>
﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>付款单查询-供应商专用</title>

<link rel="stylesheet" href="../css/style.css" type="text/css" />

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<script language="javascript">
extendDate();
extendNumber();
</script>

<style type="text/css">
.aw-grid-control {
	height: 74%;
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
	width: 120px;
	cursor: pointer;
}

#myGrid .aw-column-1 {
	width: 50px;
}

#myGrid .aw-column-2 {
	width: 70px;
}

#myGrid .aw-column-3 {
	width: 150px;
}

#myGrid .aw-column-4 {
	width: 80px;
}

#myGrid .aw-column-5 {
	width: 80px;
	text-align: right;
}

#myGrid .aw-column-6 {
	width: 80px;
	text-align: right;
}

#myGrid .aw-column-9 {
	width: 60px
}

#myGrid .aw-column-10 {
	width: 0px
}

#myGrid .aw-column-11 {
	width: 80px
}
</style>

<xml id="island4reply" />
<xml id="island4cat" />

<script language="javascript" type="text/javascript">

var attributeOfNewWnd = "status=yes,toolbar=no,menubar=no,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);

/**
 * 根据用户指定的过滤条件,组织一个URL串, 访问后台模块, 取符合条件的单据目录.
 * 目前支持的过滤条件: 帐套号/单据状态.
 */
function load_catalogue()
{	
	var parms = new Array();
	if( $("txt_bookno").value != "" ) 	        parms.push( "bookno=" + $("txt_bookno").value );
	if( $("txt_flag").value != "" ) 	        parms.push( "flag=" + $("txt_flag").value );
	if( $("txt_sheetid").value != "" ) 	        parms.push( "sheetid=" + $("txt_sheetid").value );
	if( $("txt_venderid").value != "" ) 	        parms.push( "venderid=" + $("txt_venderid").value );
	if( $("txt_editdate_min").value != "" ) 	parms.push( "editdate_min=" + $("txt_editdate_min").value );	
	if( $("txt_editdate_max").value != "" ) 	parms.push( "editdate_max=" + $("txt_editdate_max").value );
	if( $("txt_requestflag").value != "" ) 	        parms.push( "requestflag=" + $("txt_requestflag").value );
	if( $("txt_sjflag").value != "" ) 	        parms.push( "sjflag=" + $("txt_sjflag").value );
	if( $("txt_reqdate_min").value != "" )          parms.push( "reqdate_min=" + $("txt_reqdate_min").value );	
	if( $("txt_reqdate_max").value != "" ) 	        parms.push( "reqdate_max=" + $("txt_reqdate_max").value );
	
	if( parms.length == 0 ){
		alert( "请至少选择一个查询条件！" );
		return;
	}
	parms.push( "sheetname=paymentnote" );
	parms.push( "operation=load_catalogue" );
	var url_cat  = "../DaemonSearchSheet?" + parms.join( '&' );

	setLoading(true);
	var courier = new AjaxCourier( url_cat );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_catalogue;
	courier.call();	
}

/**
 * 对后台返回的数据进行解析
 */
function analyse_catalogue( text )
{
	setLoading(false);
	island4cat.loadXML( text );
	var elm_err 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" ) 
	{    	 
		var elm_row 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	  	var row_count   = elm_row.childNodes.length;
	  	if( row_count== 0 ){
	  		div_report.innerText = "没有符合条件的付款单, 请重新指定过滤条件.";
		}
	  	else{
			display_catalogue();
		}	
	}
	else {	
		div_report.innerText = xerr.note;
	}
}

/**
 * 显示后台返回的付款单目录.
 */
function display_catalogue()
{
	var node_list = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	var row_count = node_list.childNodes.length;
	island4reply.loadXML( node_list.xml );
	var xml, node = document.getElementById( "island4reply" );
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument("","", null);
		xml.appendChild(node.selectSingleNode("*"));
	}
	var table       = new AW.XML.Table;
	var grid        = new AW.UI.Grid;
	table.setXML( xml );
	table.setColumns(["sheetid","aaa","venderid","vendername","flagname","chargeamt","payamt","planpaydate",
	        "bankdate","editor","flag","reqflag","reqdate","sjflag","sjprintdate"]);
	var columnNames = ["单据号", "打印", "供应商编码", "供应商名称", "单据状态", "本单费用合计", "本单应付金额", "计划付款日期", 
	        "网银支付日期", "制单人","单据状态","开票申请状态","申请日期","收据打印状态","收据打印日期" ];	
	grid.setId( "myGrid" );
	grid.setColumnCount( columnNames.length );
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	var str = new AW.Formats.String;	
	var number = new AW.Formats.Number;
	number.setTextFormat("#,###.##");
	grid.setCellFormat([str,str,str,str,str,number,number,str,str]);
	
	// 显示明细功能
	var obj_link = new AW.Templates.Link;
	obj_link.setEvent( "onclick",
        	function(){
        		var current_row = grid.getCurrentRow();
        		var sheetid = grid.getCellValue( 0, current_row );
        		open_detail_wnd( sheetid );
        	}
	);	
	grid.setCellTemplate( obj_link, 0 ); 
	
	// 支持打印功能
	var obj_print = new AW.Templates.Link; 	
	obj_print.setEvent( "onclick",
        	function(){
        		var current_row = grid.getCurrentRow();
        		var sheetid = grid.getCellValue( 0, current_row );
        		open_print_wnd( sheetid );
        	}
	);
	grid.setCellTemplate( obj_print,1 );
	
	grid.setCellModel(table);
	grid.setCellText( "打印", 1 );
	
	// 开票申请状态
	for( var i=0; i<row_count; i++ ){
		var lable = ( grid.getCellText(11,i)==1 ) ? "已申请" : "未申请";
		grid.setCellText( lable, 11, i );
		var lable = ( grid.getCellText(13,i)==1 ) ? "已打印" : "未打印";
		grid.setCellText( lable, 13, i );
	}

	grid.sort(0,"descending");		// 目录按单据号逆序排列
	grid.setFooterText(["查询结果:"+row_count+"条"]);
	grid.setFooterVisible(true);
	div_report.innerHTML = grid.toString();	
}

/**
 * 打开窗口, 显示付款单明细.
 */
function open_detail_wnd(sheetid)
{		
 	window.open( "../paymentnote_vender/paymentnote_detail.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);		
}

/**
 * 打开打印窗口
 */
function open_print_wnd(sheetid)
{		
 	window.open( "../paymentnote_vender/paymentnote_print.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);		
}


//导出查询列表
function downExcel(){
	var parms = new Array();
	if( $("txt_bookno").value != "" ) 	        parms.push( "bookno=" + $("txt_bookno").value );
	if( $("txt_flag").value != "" ) 	        parms.push( "flag=" + $("txt_flag").value );
	if( $("txt_sheetid").value != "" ) 	        parms.push( "sheetid=" + $("txt_sheetid").value );
	if( $("txt_venderid").value != "" ) 	        parms.push( "venderid=" + $("txt_venderid").value );
	if( $("txt_editdate_min").value != "" ) 	parms.push( "editdate_min=" + $("txt_editdate_min").value );	
	if( $("txt_editdate_max").value != "" ) 	parms.push( "editdate_max=" + $("txt_editdate_max").value );
	if( $("txt_requestflag").value != "" ) 	        parms.push( "requestflag=" + $("txt_requestflag").value );
	if( $("txt_sjflag").value != "" ) 	        parms.push( "sjflag=" + $("txt_sjflag").value );
	if( $("txt_reqdate_min").value != "" )          parms.push( "reqdate_min=" + $("txt_reqdate_min").value );	
	if( $("txt_reqdate_max").value != "" ) 	        parms.push( "reqdate_max=" + $("txt_reqdate_max").value );
	
	if( parms.length == 0 ){
		alert( "请至少选择一个查询条件！" );
		return;
	}
	
	var url  = "../DaemonDownloadExcel?operation=paymentnotelist&" + parms.join( "&" );
	window.location.href = url;
}

</script>
</head>

<body>
	<div id="title">付款单查询－零售商</div>

	<table cellspacing="1" cellpadding="2" width="100%"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader">付款申请单号</td>
			<td class="tableheader">供应商编码</td>
			<td class="tableheader">分公司</td>
			<td class="tableheader">单据状态</td>
			<td class="tableheader">制单日期</td>
			<td class="tableheader">申请状态</td>
			<td class="tableheader">申请日期</td>
			<td class="tableheader">收据打印状态</td>
		</tr>
		<tr>
			<td class="altbg2"><input type="text" id="txt_sheetid" /></td>
			<td class="altbg2"><input type="text" id="txt_venderid"></input></td>
			<td class="altbg2"><%=sel_book%></td>
			<td class="altbg2"><select id="txt_flag">
					<option value="">全部</option>
					<option value="2">制单审核</option>
					<option value="3">发票确认</option>
					<option value="4">已审批</option>
					<option value="5">已审定</option>
					<option value="6">已付款确认</option>
					<option value="7">网银支付</option>
			</select></td>
			<td class="altbg2">从<input type="text" id="txt_editdate_min"
				onchange="checkDate(this)" size="8" /> 到<input type="text"
				id="txt_editdate_max" onchange="checkDate(this)" size="8" />
			</td>
			<td class="altbg2"><select id="txt_requestflag"
				name="txt_requestflag">
					<option value="">全部</option>
					<option value="0">未申请</option>
					<option value="1">已申请</option>
			</select></td>
			<td class="altbg2">从<input type="text" id="txt_reqdate_min"
				onchange="checkDate(this)" size="8" /> 到<input type="text"
				id="txt_reqdate_max" onchange="checkDate(this)" size="8" />
			</td>
			<td class="altbg2"><select id="txt_sjflag" name="txt_sjflag">
					<option value="">全部</option>
					<option value="0">未打印</option>
					<option value="1">已打印</option>
			</select></td>
		</tr>
		<tr class="singleborder">
			<td colspan="5"></td>
		</tr>
		<tr class="whiteborder">
			<td colspan="5"></td>
		</tr>
		<tr class="header">
			<td class="altbg2"><script type="text/javascript">
				var button = new AW.UI.Button;
				button.setControlText("查询");
				document.write(button);
				button.onClick = load_catalogue;
			</script></td>
			<td><a href="javascript:downExcel();">导出查询结果到Excel</a></td>
			<td colspan="5" class="altbg2"></td>
		</tr>
	</table>

	<div id="div_report"></div>
</body>

</html>