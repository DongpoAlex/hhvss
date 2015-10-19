﻿
<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	import="java.util.* , java.text.DecimalFormat"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3020212;
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
		int[] shoptype = { 11, 12,13,21};
	String note = "任意门店";
	SelBranchGroup sel_branch = new SelBranchGroup( token,shoptype, note );
	sel_branch.setAttribute( "id", "txt_shopid" );
	sel_branch.setAttribute( "multiple", "true" );
	sel_branch.setAttribute( "size", "10" );
	sel_branch.setAttribute( "onchange", "onChange(this)" );
	
	String g_status = request.getParameter("g_status");
	if( g_status == null ) g_status="-1";
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>扣款查询</title>


<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/CalendarControl.css" rel="stylesheet" type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"></script>
<script language="javascript">
		var table4confirm = new AW.XML.Table;
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
	width: 50px;
}

#grid_cat .aw-column-1 {
	width: 50px;
}

#grid_cat .aw-column-2 {
	width: 80px;
}

#grid_cat .aw-column-3 {
	width: 80px;
}

#grid_cat .aw-column-4 {
	width: 80px;
}

#grid_cat .aw-column-5 {
	width: 80px;
}

#grid_cat .aw-column-6 {
	width: 80px;
}

#grid_cat .aw-column-7 {
	width: 80px;
}

#grid_cat .aw-column-8 {
	width: 100px;
}

#grid_cat .aw-column-9 {
	width: 80px;
}

#grid_cat .aw-column-10 {
	width: 50px;
}
</style>
<style>
div_sheethead.td {
	align: center
}
</style>
<script language="javascript">
		var arr_sheetid 	= null;
		var current_sheetid 	= "";
		var tag_grp 		= new AW.UI.Tabs;
		var g_status = "<%=g_status%>";
		</script>

<script language="javascript">
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
		function onChange(ctrl)
{
	
	var n = 0;
	var tmpshopid = new Array();
	var opt = ctrl.options;
	for(var i=0; i<opt.length; i++)
		if(opt[i].selected)		
		{
			tmpshopid[n] = opt[i].value;
			n++;
		}
		
	txtshopid.value = tmpshopid.join(",");
	//alert(txtshopid.value);
}
		</script>

<script language="javascript">
		function install_tag_sheets()
		{
			tag_grp.setId( "tag_grp" );
			tag_grp.setItemText( [ "查询条件",  "扣款明细" ] );
			tag_grp.setItemCount( 2 );
			tag_grp.onSelectedItemsChanged = function( idx ) {
				if( idx == "0" ) enactive_search() ;
				if( idx == "1" ) enactive_catalogue();
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
	parms.push( "sheetname=deduction" );

  if(txt_venderid.value  == '')
  {
  	alert("请输入供应商编号！");
  	return;
  }
	if( ctrl4date.value 	!= '' )      parms.push( "yearmonth="    + ctrl4date.value 	);
  if( txt_venderid.value  != '' )   parms.push( "venderid="  + txt_venderid.value );
	if( txt_goodsid.value  != '' )   parms.push( "goodsid="  + txt_goodsid.value );
	if( txt_goodsname.value  != '' )   parms.push( "goodsname="  + txt_goodsname.value );

	window.status = "下载信息 ...";
	set_loading_notice( true );

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

	parms.push( "sheetname=deduction" );
	parms.push( "timestamp=" + new Date().getTime() );
	set_loading_notice( true );
	var url = "../DaemonSearchSheet?" + parms.join( '&' );

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
			div_cat.innerHTML = "没有找到您要的扣款信息.";
	  		set_loading_notice( false );
	  	}else{
	  		var node = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	  		var table = new AW.XML.Table;
	  		island4cat.loadXML( table.getXMLContent(node) );
			show_catalogue();
		}	
	}else if( xerr.code == "0" ){
	  	enactive_catalogue();
		div_cat.innerHTML = "符合条件的单据较多，请缩小查询范围。";
	  	set_loading_notice( false );
	}else{	
		div_cat.innerHTML = "";
	  	enactive_catalogue();
	  	var error_hint = xerr.note.split(":");
	  	if( error_hint[1]!= null ) var erro_hi = error_hint[1];
		div_cat.innerHTML = xerr.code + ";" + erro_hi;
	  	set_loading_notice( false );
	}
}


function show_catalogue()
{
	window.status = "显示目录 ...";
	set_loading_notice( true );
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
	
	var xml, node = document.getElementById( "island4cat" );		
	
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument( "", "", null);
		xml.appendChild( island4cat.selectSingleNode( "*" ) );
	}		

	table.setXML( xml );

	table.setColumns( [ "monthid","shopid", "editor", "goodsid","goodsname",
	 "orderqty","receiptqty","nodeliverqty","nodelivercostvalue","receiptrate","punishvalue1","sheetid","ordershopid","cost"] );

	var columnNames = [ "月份", "区域","库控员", "商品编号", "商品名称", "订货数量","验收数量","未到货数量", 
	 "未到货金额", "单品到货满足率", "扣款金额","订单单号","订货门店","当前进货单价" ];
	
	var obj = new AW.UI.Grid;
	obj.setId( "grid_cat" );
	obj.setColumnCount( 14 );
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
	obj.sort(0,"descending");		// 目录按单据号逆序排列
	div_cat.innerHTML = obj.toString();
	
	window.status = "OK";
	set_loading_notice( false );
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
	var url = "../DaemonViewSheet?sheet=retnotice&sheetid=" + sheetid;
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
	read_already( current_sheetid );

}
function catchWarning(){	div_warning.innerHTML = "";
	div_warning.style.display="none";}
function catchWarning1(){
	div_warning.innerHTML = "";
	div_warning.style.display="none";
	//根据单据的不同时间，和状态显示相应的预警信息
	var warnstatus = island4purchase.XMLDocument.selectSingleNode("xdoc/xout/sheet/head/row/warnstatus").text;
	if( Number(warnstatus) != 100 ){
		var warn3  = "根据合同约定，贵司需在退货通知单发出之后的7天内完成退货，逾期未退，需向商业有限公司交纳逾期储位占用费！";
		var warn14 = "根据合同约定，贵司需在退货通知单发出之后的21天内完成退货,逾期未退，则视为贵司自动放弃本单所列商品及数量的所有权和处置权并承担相应法律责任！"
		var retdate = island4purchase.XMLDocument.selectSingleNode("xdoc/xout/sheet/head/row/retdate").text;
		var ymd = retdate.split('-');
		retdate = new Date(ymd[0],ymd[1]-1,ymd[2]);
		var _diffSe = new Date().getTime() - retdate.getTime();
		var _dSe = 86400000; //一天所含有的微秒 =24*60*60*1000
		var _diffDay = parseInt(_diffSe / _dSe); //相差的天p
		if( _diffDay >= 3 && _diffDay <= 14 ){//退货通知单发出第3天到第14天
			div_warning.innerHTML = "<span class='remind'>提醒：该单据 "+_diffDay+" 天前发出，请尽快执行！</span><br/>"+warn3;
			div_warning.style.display="block";
		}else if( _diffDay >= 14 ){//退货通知单发出第14天
			div_warning.innerHTML = "<span class='warn'>警告：该单据 "+_diffDay+" 天前发出，您的退货通知已过期！</span><br/>"+warn14;
			div_warning.style.display="block";
		}
	}
}
</script>

<script language="javascript">
function set_loading_notice( on )
{
	var loading_html='<img src="../img/loading.gif"></img><font color=#003>正在下载,请等候……</font>';
	div_loading.innerHTML = loading_html;
	div_loading.style.display = on ? 'block' : 'none';
}
</script>

<script language="javascript">
function open_sheet_detail( sheetid )
{
	current_sheetid = sheetid;
	tag_grp.setSelectedItems( [2] );
}


function read_already( sheetid )
{
	var parms = new Array();
	parms.push( "sheetname=retnotice" );
	parms.push( "operation=read" );
	parms.push( "sheetid=" + sheetid );
	
	var url = "../DaemonSheetManager?timestamp=" + new Date().getTime()+"&"+ parms.join("&");
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
 	
 	var ctrl = document.getElementById("status");
  var status = ctrl.status;

 	window.open( "retnotice_print.jsp?sheetid="+sheetid, sheetid, attributeOfNewWnd );		
}

//单份订单的确认
function confirm_sheet()
{
	var sheetid = current_sheetid;
	var url = "../DaemonSheetManager?sheetname=retnotice&operation=confirm&sheetid=" + sheetid;
	table4confirm.setURL( url );
	table4confirm.response = function( text ){
		table4confirm.setXML( text );
		if( table4confirm.getXMLText("xdoc/xerr/code") != 0 ){
			alert( "单据"+sheetid+"确认失败" );
		}else{
			alert( "单据"+sheetid+"确认成功" );
		}
	};
	table4confirm.request();
}
function downExcel(){
var parms = new Array();
	parms.push( "sheetname=deduction" );

	if( ctrl4date.value 	!= '' )      parms.push( "yearmonth="    + ctrl4date.value 	);
  if( txt_venderid.value  != '' )   parms.push( "venderid="  + txt_venderid.value );
	if( txt_goodsid.value  != '' )   parms.push( "goodsid="  + txt_goodsid.value );
	if( txt_goodsname.value  != '' )   parms.push( "goodsname="  + txt_goodsname.value );
	var url  = "../DaemonDownloadReport?reportname=deduction&" + parms.join( "&" );
	
	window.location.href = url;

}
</script>

<xml id="island4result" />
<xml id="island4purchase" />
<xml id="island4cat" />
<xml id="island4err" />
<xml id="format4head" src="retnotice_view.xsl" />

</head>

<body onload="init()">

	<div id="div_loading" style="display: none;">
		<img src="../img/loading.gif"></img> <font color=#003>正在下载,请等候……</font>
	</div>

	<div id="div_tabs" style="width: 100%;"></div>

	<div id="div001">
		<!--<a href="javascript:downExcel()">将查询结果导出到Excel文档</a> -->
		<div id="div_cat" style="display: block;"></div>
		<div id="div_detail" style="display: none;">
			<div id="div_navigator" style="display: none;">
				<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
					type="button" value="下一单" onclick="sheet_navigate(1)" /> <input
					type="button" value="确认单据" onclick="confirm_sheet()" /> <input
					type="button" value="打印单据" onclick="open_win_print()" />
			</div>
			<br />
			<div id="div_warning" style="" class="warning"></div>

			<div id="div_sheethead"></div>
		</div>

		<div id="div_search">
			<table cellspacing="1" cellpadding="2" width="350"
				class="tablecolorborder">
				<tr>
					<td>月份</td>
					<td><input type="text" id="ctrl4date"
						onfocus="WdatePicker({skin:'whyGreen',dateFmt:'yyyyMM'})"
						class="Wdate" /></td>
				</tr>
				<tr>
					<td>供应商编码</td>
					<td><input type="text" name="txt_venderid" size="10" /></td>
				</tr>
				<tr>
					<td>商品编码类似</td>
					<td><input type="text" name="txt_goodsid" size="10" /></td>
				</tr>
				<tr>
					<td>商品名称类似</td>
					<td><input type="text" id="txt_goodsname" size="20" /></td>
				</tr>
				<tr class="singleborder">
					<td colspan=2></td>
				</tr>
				<tr class="whiteborder">
					<td colspan=2></td>
				</tr>
				<tr class="header">
					<td colspan=2><script>
							document.write( btn_search );
							btn_search.onClick =search_sheet;
							</script></td>
				</tr>
			</table>

			<br />
		</div>
		<!-- end of div_search -->
	</div>
	<!-- end of div001 -->
</body>
</html>
