﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 9999903;
%>
<%
	request.setCharacterEncoding("UTF-8");

	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);

	//查询用户的权限.
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ))
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:"
				+ moduleid);
%>
<%
	int[] shoptype = {11, 22};
	String note = "任意门店";
	SelBranchAll sel_branch = new SelBranchAll(token, shoptype, note);
	sel_branch.setAttribute("id", "txt_shopid");

	String g_status = request.getParameter("g_status");
	if (g_status == null)
		g_status = "-1";
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>退货通知单查询-供应商专用</title>
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
.error_message {
	width: 15em;
	text-align: center;
	background: #FFA826;
	color: #000;
}

div#login_info {
	color: #909090;
	position: absolute;
	top: 20px;
	right: 110px;
	left: auto;
}

div#div_loading {
	width: 15em;
	text-align: center;
	background: #FFA826;
	color: #FFF;
}

#tag_grp {
	width: 100%;
	height: 25px;
	margin-bottom: 10px;
}

.remind {
	color: #FFCC33;
}

.warn {
	color: #CC3300;
}

.warning {
	border: 2px solid #FF6600;
	padding: 4px;
	margin-bottom: 4px;
	font-weight: bold;
	background-color: #fff;
}
</style>

<style>
.aw-grid-control {
	height: 84%;
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
</style>

<style>
#grid_cat {
	background-color: #F9F8F4
}

#grid_cat .aw-column-0 {
	width: 130px;
}

#grid_cat .aw-column-1 {
	width: 50px;
}

#grid_cat .aw-column-4 {
	width: 100px;
}

#grid_cat .aw-column-5 {
	width: 100px;
}

#grid_cat .aw-column-6 {
	width: 100px;
}
</style>

<script language="javascript">
		var arr_sheetid 	= null;
		var current_sheetid 	= "";
		var tag_grp 		= new AW.UI.Tabs;
		var g_status = <%=g_status%>;
		
		var btn_search = new AW.UI.Button;
		btn_search.setControlText( "查询" );
		btn_search.setId( "btncheck" );
		btn_search.setControlImage( "search" );	
		
		var btn_sheetid = new AW.UI.Button;
		btn_sheetid.setControlText( "查询" );
		btn_sheetid.setControlImage( "search" );	
		
		</script>

<script language="javascript">
		function init()
		{
			install_tag_sheets();
			
			obj = document.getElementById("sheet_status");
			if( g_status != -1 ){
				for(var i=0;i<obj.length;i++){
					var tmp = obj.options[i].value
					if( tmp == g_status ){
						obj.options[i].selected = "selected";
					}
				}
				
				search_sheet();
			}
		}
		</script>

<script language="javascript">
		function install_tag_sheets()
		{
			tag_grp.setId( "tag_grp" );
			tag_grp.setItemText( [ "查询条件", "单据目录", "单据明细" ] );
			tag_grp.setItemCount( 3 );
			tag_grp.onSelectedItemsChanged = function( idx ) {
				if( idx == "0" ) enactive_search() ;
				if( idx == "1" ) enactive_catalogue();
				if( idx == "2" ) {
					load_sheet_detail( current_sheetid );
					enactive_detail();
				}
			};
		
			tag_grp.setSelectedItems( [0] );
			div_tabs.innerHTML=tag_grp.toString();
		}
		</script>

<script language="javascript">
		function enactive_search()
		{
			hide_all();
			div_search.style.display = 'block';
		}

		function enactive_catalogue()
		{
			hide_all();
			div_cat.style.display 	 = 'block';
		}

		function enactive_detail()
		{
			hide_all();
			div_detail.style.display = 'block';
		}

		function hide_all()
		{
			div_search.style.display = 'none';
			div_cat.style.display 	 = 'none';
			div_detail.style.display = 'none';
		}
		</script>

<script language="javascript">
function search_sheet()
{
	var parms = new Array();
	parms.push( "sheetname=retnotice" );

	var status = sheet_status.value;
	if( txt_shopid.value 	!= '' )      parms.push( "shopid="    + txt_shopid.value 	);
	if(  status != '' ){
		if( Number(status) == 0 || Number(status) == 1 )
	      parms.push( "status=" + status );
	    else
	      parms.push( "warnstatus=" + status );
	}
	if( txt_retdate_min.value  != '' )   parms.push( "retdate_min="  + txt_retdate_min.value );
	if( txt_retdate_max.value  != '' )   parms.push( "retdate_max="  + txt_retdate_max.value );

	window.status = "下载单据目录 ...";
	setLoading( true );
	
	load_catalogue( parms );
	tag_grp.setSelectedItems( [1] );
	window.status = "OK";
}

function search_by_sheetid()
{
	var str = txt_sheetid.value;
	str = str.replace( / /g, ',' );
	str = str.replace( /,+/g, ',' );
	if( str == 0 ) { alert( "未输入单据号!" ); return; }
	var arr_id = str.split ( ',' );
	
	var parms = new Array();
	for( var i=0; i<arr_id.length; i++ ) {
		var s = arr_id[ i ];
		if( s != '' ) parms.push( "sheetid=" + s );
	}

	load_catalogue( parms );
	tag_grp.setSelectedItems( [1] );
	enactive_catalogue();
}

function load_catalogue ( parms )
{
	div_cat.innerHTML = " ";

	parms.push( "sheetname=retnotice" );
	parms.push( "timestamp=" + new Date().getTime() );
	setLoading( true );
	var url = "../DaemonSearchHisSheet?" + parms.join( '&' );
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_catalogue;
	courier.call();
}

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
	  	if( row_count== "0" )
	  	{
	  		enactive_catalogue();
			div_cat.innerHTML = "没有找到您要的退货通知单.";
	  		setLoading( false );
	  	}else{
	  		var node = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );	
	  		var table = new AW.XML.Table;
	  		island4cat.loadXML( table.getXMLContent(node) );
			show_catalogue();
		}	
	}else if( xerr.code == "0" ){
	  	enactive_catalogue();
		div_cat.innerHTML = "符合条件的单据较多，请缩小查询范围。";
	  	setLoading( false );
	}else{	
		div_cat.innerHTML = "";
	  	enactive_catalogue();
		div_cat.innerHTML = xerr.note;
	  	setLoading( false );
	}
}


function show_catalogue()
{
	window.status = "显示目录 ...";
	arr_sheetid = new Array();
	var node_sheetid 	= island4result.XMLDocument.selectNodes( "/xdoc/xout/catalogue/row/sheetid" );	
	var next_node = node_sheetid.nextNode();
	while( next_node != null ) {
		arr_sheetid[ arr_sheetid.length ] = next_node.text;
		next_node = node_sheetid.nextNode();
	}
	
	if( arr_sheetid !=null && arr_sheetid.length >0 ) current_sheetid = arr_sheetid[0];


	var node_catalogue 	= island4result.XMLDocument.selectNodes( "/xdoc/xout/catalogue" );

	var node_body 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );	
	
	var row_count   = node_body.childNodes.length;
	var table = new AW.XML.Table;
	island4cat.loadXML( table.getXMLContent(node_body) );
	
	var xml, node = document.getElementById( "island4cat" );		
	
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument( "", "", null);
		xml.appendChild( island4cat.selectSingleNode( "*" ) );
	}		

	var table = new AW.XML.Table;
	table.setXML( xml );


	table.setColumns( [ "sheetid", "status", "rettype","shopid", "shopname","askshopname", 
	 "retdate", "operator", "editor", "editdate", "checker", "checkdate" ] );

	var columnNames = [ "单据号", "状态", "退货类型","门店", "门店名称","申请地",
		 "退货日期", "业务员", "编辑人", "编辑日期", "审核人", "审核日期" ];
	
	var obj = new AW.UI.Grid;
	obj.setId( "grid_cat" );
	obj.setColumnCount( 10 );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );
		
	obj.setSelectorVisible(true);
	obj.setSelectorWidth(30);
	obj.setSelectorText(function(i){return this.getRowPosition(i)+1});

	var obj_link = new AW.Templates.Link;
	obj_link.setEvent( "onclick",
	function(){
		var current_row = obj.getCurrentRow();
		var sheetid = obj.getCellValue( 0, current_row );
		open_sheet_detail( sheetid );
	}
	);
	obj.setCellTemplate( obj_link, 0 ); 

	
	obj.setCellModel(table);
	
	
	for(var i=0;i<row_count;i++){
		var type = Number(obj.getCellValue(2,i));
		var labe="";
		if(type==0)
			labe="普通退货";
		else if(type==1)
			labe="特殊退货";
		else if(type==2)
			labe="清场退货";
		else if(type==3)
			labe="清品退货";
		obj.setCellText(labe,2,i);
	}
	obj.sort(0,"descending");		// 目录按单据号逆序排列
	var sumcount = node_body.getAttribute("count");
	div_cat.innerHTML = "<div>查询结果：总计："+sumcount+"行， 显示："+row_count+"行</div>"+obj.toString();
	
	window.status = "OK";
	setLoading( false );
}
</script>


<script language="javascript">
function sheet_navigate(step)
{
	var offset = 0;
	if( arr_sheetid == null || arr_sheetid.length == 0 ) { alert( "目录是空的!" ); return false; }
	for( var i = 0; i<arr_sheetid.length; i++ ) {
		if( current_sheetid == arr_sheetid[ i ] ) {
			offset = i;
			break;
		}
	}
	
	offset += step;
	if( offset < 0 ) { alert ( "已经是第一份单据!" ); return false; }
	if( offset >= arr_sheetid.length ) { alert ( "已经是最后一份单据!" ); return false; }
	
	var sheetid = arr_sheetid[offset];
	open_sheet_detail( sheetid );
}
</script>


<script language="javascript">
function load_sheet_detail( sheetid )
{
	if( sheetid == null || sheetid.length == 0 ) return false;
	window.status = "加载单据明细: " + sheetid;
	current_sheetid = sheetid;
	
	var url = "../DaemonViewHisSheet?sheet=retnotice&sheetid=" + sheetid;
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_detail;
	courier.call();	
}


function analyse_detail( text )
{
	island4purchase.loadXML( text );
	show_ret();	
}


function show_ret()
{
	div_navigator.style.display = 'block';
	div_sheethead.innerHTML = island4purchase.transformNode( format4head.documentElement );
	catchWarning();
	var status = island4purchase.XMLDocument.selectSingleNode("xdoc/xout/sheet/head/row/status").text;
	if(status==0){
		read_already( current_sheetid );
	}
}

function catchWarning(){
	div_warning.innerHTML = "";
	div_warning.style.display="none";
	//根据单据的不同时间，和状态显示相应的预警信息
	var warnstatus = island4purchase.XMLDocument.selectSingleNode("xdoc/xout/sheet/head/row/warnstatus").text;
	var releasedate = island4purchase.XMLDocument.selectSingleNode("xdoc/xout/sheet/head/row/releasedate").text;
	if( Number(warnstatus) != 100 && Number(warnstatus) != 0 ){
		var warn3  = "根据合同约定，贵司需在退货通知单发出之后的7天内完成退货，逾期未退，需向商业有限公司交纳逾期储位占用费！";
		var warn14 = "根据合同约定，贵司需在退货通知单发出之后的21天内完成退货,逾期未退，则视为贵司自动放弃本单所列商品及数量的所有权和处置权并承担相应法律责任！"
		if( Number(warnstatus)==90 ){//退货通知单发出第3天到第14天
			div_warning.innerHTML = "<span class='remind'>提醒：请尽快执行！发布日期："+releasedate+"</span><br/>"+warn3;
			div_warning.style.display="block";
		}else if( Number(warnstatus)==91 ){//退货通知单发出第14天
			div_warning.innerHTML = "<span class='warn'>警告：您的退货通知已过期！发布日期："+releasedate+"</span><br/>"+warn14;
			div_warning.style.display="block";
		}
	}
}
function open_sheet_detail( sheetid )
{
	current_sheetid = sheetid;
	tag_grp.setSelectedItems( [2] );
}


function read_already( sheetid )
{
	var parms = new Array();
	parms.push( "sheet=retnoticeread" );
	parms.push( "sheetid=" + sheetid );
	
	var url = "../DaemonViewHisSheet?timestamp=" + new Date().getTime()+"&"+ parms.join("&");
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_read;
	courier.call();	
}


function analyse_read( text )
{
	island4err.loadXML( text );
	
	var elm_err 	= island4err.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" ) {    
		window.status = "单据状态已经改为：已阅读。";
	}
	
}
</script>

<script language="javascript">
function open_win_print()
{		
	var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
 	var sheetid = current_sheetid;
 	window.open( "retnotice_print.jsp?sheetid="+sheetid, sheetid, attributeOfNewWnd );		
}
</script>

<xml id="island4result" />
<xml id="island4purchase" />
<xml id="island4cat" />
<xml id="island4err" />
<xml id="format4head" src="retnotice_print.xsl" />

</head>

<body onload="init()">

	<div id="div_loading" style="display: none;">
		<img src="../img/loading.gif"></img> <font color=#003>正在下载,请等候……</font>
	</div>

	<div id="div_tabs" style="width: 100%;"></div>

	<div id="div001">
		<div id="div_cat" style="display: block;"></div>
		<div id="div_detail" style="display: none;">
			<div id="div_navigator" style="display: none;">
				<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
					type="button" value="下一单" onclick="sheet_navigate(1)" /> <input
					type="button" value="打印单据" onclick="open_win_print()" />
			</div>
			<br />
			<div id="div_warning" style="" class="warning"></div>
			<div id="div_sheethead"></div>
		</div>

		<div id="div_search">
			<table cellspacing="1" cellpadding="2" width="60%"
				class="tablecolorborder">
				<tr>
					<td><label> 退货地: </label></td>
					<td class=altbg2><%=sel_branch%></td>

					<td><label> 单据状态: </label></td>
					<td class="altbg2"><select id="sheet_status">
							<option>全部</option>
							<option value="0" selected="selected">未阅读</option>
							<option value="1">已阅读</option>
							<option value="90">预警</option>
							<option value="91">过期</option>
							<option value="100">已执行</option>
					</select></td>
				</tr>
				<tr>
					<td><label> 退货日期: </label></td>
					<td class=altbg2><input type="text" id="txt_retdate_min"
						onblur="checkDate(this)" size="12" /> - <input type="text"
						id="txt_retdate_max" onblur="checkDate(this)" size="12" /></td>
				</tr>
				<tr class="singleborder">
					<td colspan=4></td>
				</tr>
				<tr class="whiteborder">
					<td colspan=4></td>
				</tr>
				<tr class="header">
					<td><script>
		document.write( btn_search );
		btn_search.onClick =search_sheet;
		</script></td>
				</tr>
			</table>

			<br />

			<table cellspacing="1" cellpadding="2" width="50%"
				class="tablecolorborder">
				<tr>
					<td><label> 单据号 </label></td>
					<td class=altbg2><input type="text" id="txt_sheetid" size="60" /></td>
				</tr>
				<tr class="header">
					<td><script>
			document.write( btn_sheetid );
			btn_sheetid.onClick = search_by_sheetid;
			</script></td>
					<td></td>
				</tr>
			</table>
		</div>
		<!-- end of div_search -->
	</div>
	<!-- end of div001 -->
</body>
</html>
