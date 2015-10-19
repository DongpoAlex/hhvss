<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3020104;
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );
	
	//检查用户操作权限.
	Permission perm = token.getPermission( moduleid );
	if ( !perm.include( Permission.READ ) ) throw new PermissionException( "您没有操作本模块的授权, 请与管理员联系." );

	SelBook sel_book = new SelBook(token);
	sel_book.setAttribute( "id", "txt_bookno" );
	int num_tag = 1;
	if(token.site.getSid()==1 || token.site.getSid()==2){ 
		num_tag=2;
	}
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
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<script language="javascript">
extendDate();
</script>

<style type="text/css">
.aw-grid-control {
	height: 76%;
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

#myGrid {
	background-color: #F9F8F4
}

#myGrid .aw-column-0 {
	width: 0px;
}

#myGrid .aw-column-1 {
	width: 130px;
	cursor: pointer;
}

#myGrid .aw-column-2 {
	cursor: pointer;
	width: 60px;
}

#myGrid .aw-column-3 {
	cursor: pointer;
	width: 80px;
}

#myGrid .aw-column-5,#myGrid .aw-column-6 {
	text-align: right;
}

#myGrid .aw-column-10 {
	width: 0px
}

#myGrid .aw-column-11 {
	width: 0px
}

#myGrid_req {
	background-color: #F9F8F4
}

#myGrid_req .aw-column-0 {
	width: 55px;
}

#myGrid_req .aw-column-1 {
	width: 130px;
	cursor: pointer;
}

#myGrid_req .aw-column-3,#myGrid .aw-column-4 {
	text-align: right;
}

#myGrid_req .aw-column-8 {
	width: 0px
}

#myGrid_req .aw-column-9 {
	width: 0px
}

#title {
	text-align: center;
	color: #42658D;
	font-size: 16px;
	font-family: '楷体_GB2312';
	font-weight: bold;
}
</style>

<xml id="island4reply" />
<xml id="island4cat" />
<xml id="island4result" />


<script language="javascript" type="text/javascript">

var attributeOfNewWnd = "status=yes,toolbar=no,menubar=no,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);

var tag_grp = new AW.UI.Tabs;


var arr_sheetid 	= null;                 //付款单单号
var row_selected	= null;                 //选择的付款单单号

//清空付款单数组
function init_data(){
	arr_sheetid     = null;
	row_selected	= null;
	
}

function init()
{
	
		install_tag_sheets();
}

function install_tag_sheets()
{
	tag_grp.setId( "tag_grp" );
	tag_grp.setItemText( [ "付款单信息", "开票申请" ] );
	tag_grp.setItemCount( <%=num_tag%> );
	tag_grp.onSelectedItemsChanged = function( idx ) {
		if( idx == "0" ) enactive_sheetlist() ;
		if( idx == "1" ) enactive_reqlist();
	};
	tag_grp.setSelectedItems( [0] );
	div_tabs.innerHTML=tag_grp.toString();
}

function enactive_sheetlist()
{
	div_report.style.display = 'block';
	div_reqlist.style.display = 'none';
	div_report_req.style.display = 'none';
}

function enactive_reqlist()
{
        div_report.style.display = 'none';
        div_report_req.style.display = 'block';
	div_reqlist.style.display = 'block';
}
	

/**
 * 根据用户指定的过滤条件,组织一个URL串, 访问后台模块, 取符合条件的单据目录.
 * 目前支持的过滤条件: 帐套号/单据状态.
 */
function search_catalogue()
{	
        init_data();
	setLoading(true);
	var parms = new Array();
	parms.push( "sheetname=paymentnote" );
	parms.push( "operation=load_catalogue" );
	var bookno   	= txt_bookno.value;
	var flag	= txt_flag.value;
	var requestflag	= txt_requestflag.value;
	var sjflag	= txt_sjflag.value;
	if( bookno != "" ) 	        parms.push( "bookno=" + bookno );
	if( flag   != "" ) 	        parms.push( "flag="   + flag );	
	if( requestflag   != "" ) 	parms.push( "requestflag="   + requestflag );	
	if( sjflag   != "" ) 	parms.push( "sjflag="   + sjflag );	
	
	var url_cat             = "../DaemonSearchSheet?" + parms.join( '&' );
	var courier             = new AjaxCourier( url_cat );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_catalogue;
	courier.call();	
}

/**
 * 对后台返回的数据进行解析
 */
function analyse_catalogue( text )
{
        arr_sheetid = new Array();
	row_selected = new Array();
	
	island4cat.loadXML( text );
	var elm_err 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" ) 
	{    	 
		var elm_row 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	  	var row_count   = elm_row.childNodes.length;
	  	if( row_count== 0 ){
	  		div_report.innerText = "没有符合条件的付款单, 请重新指定过滤条件.";
	  		div_reqlist.innerText = "没有符合条件的付款单, 请重新指定过滤条件.";
		}
	  	else{
	  	        //记录所有单据号到数组
                	var node_sheetid = island4cat.XMLDocument.selectNodes( "/xdoc/xout/catalogue/row/sheetid" );
                	var next_node = node_sheetid.nextNode();
                	while( next_node != null ) {
                		arr_sheetid[ arr_sheetid.length ] = next_node.text;
                		next_node = node_sheetid.nextNode();
                	}
                	
			display_catalogue();
			display_reqlist();
		}	
	}
	else {	
		div_report.innerText = "系统忙，请稍后再试！ info:"+xerr.note;
	}
	setLoading(false);
}

/**
 * 显示后台返回的付款单目录.
 */
function display_catalogue()
{
    div_report.innerHTML = "";	
	var table       = new AW.XML.Table;
	var node_list = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	var row_count = node_list.childNodes.length;
	island4reply.loadXML( table.getXMLContent(node_list) );
	var xml, node = document.getElementById( "island4reply" );
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument("","", null);
		xml.appendChild(node.selectSingleNode("*"));
	}
	
	var grid        = new AW.UI.Grid;
	table.setXML( xml );
	var str = new AW.Formats.String;	
	var number = new AW.Formats.Number;
	number.setTextFormat("#,###.##");
	
	var idxStatus = 10;
	var idxReqflag =11;
	
	<% if(token.site.getSid()==6 || token.site.getSid()==7 || token.site.getSid()==10){%>
	table.setColumns(["ccc","sheetid","aaa","bbb","ccc","flagname","chargeamt","payamt","planpaydate",
	      	        "bankdate","editor","flag","reqflag","reqdate"]);
  	var columnNames = ["申请开票", "单据号", "打印","导出到Execl","表头打印", "单据状态","本单费用合计", "本单应付金额", "计划付款日期", 
	      	        "网银支付日期", "制单人","单据状态","开票申请状态","申请日期" ];
  	grid.setCellFormat([str,str,str,str,str,str,number,number,str,str]);
  	idxStatus=11;
  	idxReqflag =12;
  	
  	<%} else if(token.site.getSid()==1 || token.site.getSid()==2){%>
  		table.setColumns(["ccc","sheetid","aaa","bbb","ccc","flagname","chargeamt","payamt","planpaydate",
  	      	        "bankdate","editor","flag","reqflag","reqdate","sjflag","sjprintdate"]);
    	var columnNames = ["申请开票", "单据号", "打印","导出到Execl","收据打印", "单据状态","本单费用合计", "本单应付金额", "计划付款日期", 
  	      	        "网银支付日期", "制单人","单据状态","开票申请状态","申请日期","收据打印状态","收据打印日期" ];
    	grid.setCellFormat([str,str,str,str,str,str,number,number,str,str]);
    	idxStatus=11;
    	idxReqflag =12;
	<% }else{%>
	table.setColumns(["ccc","sheetid","aaa","bbb","flagname","chargeamt","payamt","planpaydate",
	        "bankdate","editor","flag","reqflag","reqdate"]);
	var columnNames = ["申请开票", "单据号", "打印","导出到Execl", "单据状态","本单费用合计", "本单应付金额", "计划付款日期", 
	        "网银支付日期", "制单人","单据状态","开票申请状态","申请日期" ];
	grid.setCellFormat([str,str,str,str,str,number,number,str,str]);
	<%}%>
	grid.setId( "myGrid" );
	grid.setColumnCount( columnNames.length );
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	
	
	// 显示明细功能
	var obj_link = new AW.Templates.Link;
	obj_link.setEvent( "onclick",
        	function(){
        		var current_row = grid.getCurrentRow();
        		var sheetid = grid.getCellValue( 1, current_row );
        		open_detail_wnd( sheetid );
        	}
	);	
	grid.setCellTemplate( obj_link, 1 ); 
	
	// 打印功能
	var obj_print = new AW.Templates.Link; 	
	obj_print.setEvent( "onclick",
        	function(){
        		var current_row = grid.getCurrentRow();
        		var sheetid = grid.getCellValue( 1, current_row );
        		var status = grid.getCellValue( idxStatus, current_row );
        		if( status == 2 ) open_print_wnd( sheetid );
        	}
	);
	grid.setCellTemplate(obj_print,2);
	
	//表头打印
	<% if(token.site.getSid()==6 || token.site.getSid()==7 || token.site.getSid()==10){%>
	var obj_print2 = new AW.Templates.Link; 	
	obj_print2.setEvent( "onclick",
        	function(){
        		var current_row = grid.getCurrentRow();
        		var sheetid = grid.getCellValue( 1, current_row );
        		open_print_wnd2( sheetid );
        	}
	);
	grid.setCellTemplate(obj_print2,4);
	
	<%} else if(token.site.getSid()==1 || token.site.getSid()==2){%>
	var obj_print2 = new AW.Templates.Link; 	
	obj_print2.setEvent( "onclick",
        	function(){
        		var current_row = grid.getCurrentRow();
        		var sheetid = grid.getCellValue( 1, current_row );
        		var status = grid.getCellValue( idxStatus, current_row );
        		if( status >=6 ){
	        		open_print_wnd3( sheetid );
        		}
        	}
	);
	grid.setCellTemplate(obj_print2,4);
	<% }%>
	
	
	
	// 支持导出功能
	var obj_export = new AW.Templates.Link;
	obj_export.setEvent( "onclick",
        	function(){
        		var current_row = grid.getCurrentRow();
        		var sheetid = grid.getCellValue( 1, current_row );
        		var url = "../DaemonDownloadPayment?sheetname=paymentnote&sheetid=" + sheetid;
        		window.location.href = url;
        	}
	);
	
	grid.setCellTemplate( obj_export, 3 );
	
	grid.setCellModel(table);
	grid.setCellText("导出",3);
	<% if(token.site.getSid()==6 || token.site.getSid()==7 || token.site.getSid()==10){%>
	grid.setCellText("打印",4);
	<% }%>
	
	// 开票申请状态
	for( var i=0; i<row_count; i++ ){
		var lable = ( grid.getCellText(idxReqflag,i)==1 ) ? "已申请" : "未申请";
		grid.setCellText( lable, idxReqflag, i );
		var a = Number(idxStatus)+3;
		var lable = ( grid.getCellText(a,i)==1 ) ? "已打印" : "未打印";
		grid.setCellText( lable, a, i );
	}
	
	// 只有状态为 "制单审核" 的单据才显示"打印"
	for( var i=0; i<row_count; i++ ){
		var lable = ( grid.getCellText(idxStatus,i)==2 ) ? "打印" : "";
		grid.setCellText( lable, 2, i );
		
		<%if(token.site.getSid()==1 || token.site.getSid()==2){%>
		lable = ( grid.getCellText(idxStatus,i)>=6 ) ? "打印" : "";
		grid.setCellText( lable, 4, i );
		<%}%>
	}
	
	
	grid.sort(1,"descending");		// 目录按单据号逆序排列
	grid.setFooterText(["查询结果:"+row_count+"条"]);
	grid.setFooterVisible(true);
	div_report.innerHTML = grid.toString();	
}

/**
 * 显示后台返回的付款单目录.
 */
function display_reqlist()
{
        div_reqlist.innerHTML = "";	
	var node_list = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	var row_count = node_list.childNodes.length;
	var table       = new AW.XML.Table;
	island4reply.loadXML( table.getXMLContent(node_list) );
	var xml, node = document.getElementById( "island4reply" );
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument("","", null);
		xml.appendChild(node.selectSingleNode("*"));
	}
	
	var grid        = new AW.UI.Grid;
	table.setXML( xml );
	table.setColumns(["ccc","sheetid","flagname","chargeamt","payamt","planpaydate",
	        "bankdate","editor","flag","reqflag","reqflag","reqdate"]);
	var columnNames = ["申请开票", "单据号", "单据状态","本单费用合计", "本单应付金额", "计划付款日期", 
	        "网银支付日期", "制单人","单据状态","开票申请状态","开票申请状态","申请日期" ];
	grid.setId( "myGrid_req" );
	grid.setColumnCount( columnNames.length );
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	var str = new AW.Formats.String;	
	var number = new AW.Formats.Number;
	number.setTextFormat("#,###.##");
	grid.setCellFormat([str,str,str,number,number,str,str]);
	
	grid.setCellModel(table);
	
	// 开票申请状态
	for( var i=0; i<row_count; i++ ){
		var lable = ( grid.getCellText(9,i)==1 ) ? "已申请" : "未申请";
		grid.setCellText( lable, 10, i );
	}
	
	//checkbox选择框
	grid.setSelectionMode("multi-row-marker");
	grid.onSelectedRowsChanged = function(ids){
		for(var i=0; i<ids.length; i++) {
			if( grid.getCellValue(8,ids[i])!='6' && grid.getCellValue(8,ids[i])!='7' && grid.getCellValue(9,ids[i])=='0' ){
       	                        grid.setSelectedRows(row_selected);
				alert("已付款确认和已网银支付的付款单才能进行开票申请.");
				grid.setRowSelected(false,ids[i]);
				return false;
			}
			if( ( grid.getCellValue(8,ids[i])=='6' || grid.getCellValue(8,ids[i])=='7') && grid.getCellValue(9,ids[i])=='1' ){
       	                        grid.setSelectedRows(row_selected);
				alert("已经申请开票,不允许再次申请.");
				grid.setRowSelected(false,ids[i]);
				return false;
			}
		}
       	        row_selected = ids;
		return true;
        }
	
	
	grid.sort(1,"descending");		// 目录按单据号逆序排列
	grid.setFooterText(["查询结果:"+row_count+"条"]);
	grid.setFooterVisible(true);
	div_reqlist.innerHTML = grid.toString();	
}

//提交申请开票请求
function post_req()
{
	var array_selected = new Array();
	if(arr_sheetid==null)
	{
	        alert( "先查询付款单!" );
		return;   
	}
	for(var i=0;i<row_selected.length;i++)
	{
		array_selected.push( "sheetid="+arr_sheetid[row_selected[i]] );
	}
	if( array_selected.length == 0 ){
		alert( "请选择要确认的单据!" );
		return;
	}
	
	var url = "../DaemonSheetManager?sheetname=paymentnote&operation=req&" + array_selected.join("&");
	var courier = new AjaxCourier( url );
	courier.reader.read 	= function(text){
	        island4result.loadXML(text);
                var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
                var xerr 	= parseXErr( elm_err );
                
                if( xerr.code == "0" ) 
                {    	 
                	alert("申请成功");
                }
                else
                        alert("申请失败");
	};
	courier.call();
}

/**
 * 打开窗口, 显示付款单明细.
 */
function open_detail_wnd(sheetid)
{		
 	window.open( "paymentnote_detail.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);		
}

/**
 * 打开打印窗口
 */
function open_print_wnd(sheetid)
{		
 	window.open( "paymentnote_print.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);		
}

function open_print_wnd2(sheetid)
{		
 	window.open( "paymentnotehead_print.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);		
}

function open_print_wnd3(sheetid)
{		
 	window.open( "paymentnotesj_print.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);		
}
</script>
</head>

<body onload="init();">
	<div id="title">供应商付款单查询</div>

	<table cellspacing="1" cellpadding="2" width="98%"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader" width="20%">请选择分公司</td>
			<td class="tableheader" width="20%">请选择单据状态</td>
			<td class="tableheader" width="20%">开票申请状态</td>
			<td class="tableheader" width="20%">收据打印状态</td>
			<td class="tableheader" width="40%">查询</td>
		</tr>
		<tr id="header0_toggle" style="display: block">
			<td class="altbg2"><%=sel_book %></td>
			<td class="altbg2"><select id="txt_flag" name="txt_flag">
					<option value="0">全部</option>
					<option value="2">制单审核</option>
					<option value="3">发票确认</option>
					<option value="4">已审批</option>
					<option value="5">已审定</option>
					<option value="6">已付款确认</option>
					<option value="7">已网银支付</option>
			</select></td>
			<td class="altbg2"><select id="txt_requestflag"
				name="txt_requestflag">
					<option value="">全部</option>
					<option value="0">未申请</option>
					<option value="1">已申请</option>
			</select></td>
			<td class="altbg2"><select id="txt_sjflag" name="txt_sjflag">
					<option value="">全部</option>
					<option value="0">未打印</option>
					<option value="1">已打印</option>
			</select></td>
			<td class="altbg2"><script type="text/javascript">
				var button = new AW.UI.Button;
				button.setControlText("查询");	
				document.write(button);
				button.onClick = search_catalogue;
			</script></td>
		</tr>
	</table>

	<div id="div_tabs"></div>
	<div id="div_cat">
		<div id="div_report"></div>
		<div id="div_report_req">
			<input type="button" id="b_req_check" value="申请开票"
				onclick="post_req()" />
			<div id="div_reqlist"></div>
		</div>
	</div>

	<br />
</body>
</html>