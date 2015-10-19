﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 9999904;
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
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>退货单-供应商专用</title>

<style type="text/css">
.error_message {
	width: 15em;
	text-align: center;
	background: #FFA826;
	color: #000;
}

div#div_loading {
	width: 15em;
	text-align: center;
	background: #FFA826;
	color: #FFF;
	position: absolute;
	top: 100px;
	left: 10px;
}

#tag_grp {
	width: 100%;
	height: 25px;
}
</style>

<style>
#grid_cat {
	background-color: #F9F8F4;
	width: 100%;
}

#grid_cat .aw-column-0 {
	width: 130px;
}

#grid_cat .aw-column-1 {
	width: 50px;
}

#grid_cat .aw-column-2 {
	width: 70px;
}

#grid_cat .aw-column-3 {
	width: 50px;
}

#grid_cat .aw-column-4 {
	width: 160px;
}

#grid_cat .aw-column-5 {
	width: 50px;
}
</style>


<xml id="island4result" />
<xml id="island4purchase" />
<xml id="island4cat" />
<xml id="island4head" />
<xml id="island4body" />
<xml id="format4head" src="retnotice_print.xsl" />


<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<style>
.aw-grid-control {
	height: 80%;
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

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<script language="javascript">
extendDate();
extendNumber();
</script>


<script language="javascript">
var arr_sheetid 	= null;
var current_sheetid 	= "";
var tag_grp 		= new AW.UI.Tabs;
</script>

<script language="javascript">
var btn_search = new AW.UI.Button;
btn_search.setControlText( "查询" );
btn_search.setId( "btncheck" );
btn_search.setControlImage( "search" );	

var btn_sheetid = new AW.UI.Button;
btn_sheetid.setControlText( "查询" );
btn_sheetid.setControlImage( "search" );	

var print4da = new AW.UI.Button;
print4da.setControlText( "打印预览" );
print4da.setId( "print4da" );
</script>

<script language="javascript">
function init()
{
	window.status = "Init ...";
	install_tag_sheets();
	window.status = "下载单据目录 ...";
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
	print4detail.style.display = 'none';
	div_search.style.display = 'block';
	div_cat.style.display 	 = 'none';
	div_detail.style.display = 'none';
}

function enactive_catalogue()
{
	print4detail.style.display = 'none';
	div_search.style.display = 'none';
	div_cat.style.display 	 = 'block';
	div_detail.style.display = 'none';
}

function enactive_detail()
{
	div_search.style.display = 'none';
	div_cat.style.display 	 = 'none';
	div_detail.style.display = 'block';
}
</script>

<script language="javascript">
function search_sheet (  )
{
	var parms = new Array();
	
	if( txt_venderid.value != '' )  parms.push( "venderid=" + txt_venderid.value 	);
	if( txt_shopid.value != ''  )   parms.push( "shopid=" + txt_shopid.value 	);
	if( txt_editor.value   != '' )  parms.push( "editor="   + txt_editor.value 	);
	if( txt_checker.value  != '' )  parms.push( "checker="  + txt_checker.value 	);
	if( txt_editdate_min.value  != '' )   parms.push( "editdate_min="  + txt_editdate_min.value 	);
	if( txt_editdate_max.value  != '' )   parms.push( "editdate_max="  + txt_editdate_max.value 	);
	if( txt_checkdate_min.value != '' )   parms.push( "checkdate_min="  + txt_checkdate_min.value 	);
	if( txt_checkdate_max.value != '' )   parms.push( "checkdate_max="  + txt_checkdate_max.value 	);

	if( parms.length == 0 ) {
		alert( '至少要提供一个查询条件!' );
		return;
	} 

	setLoading( true );

	load_catalogue( parms );
	tag_grp.setSelectedItems( [1] );
	courier.call();	
}

function search_by_sheetid()
{
	var str = txt_sheetid.value;
	str = str.replace( / /g, '' );
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
		div_cat.innerHTML = " ";
		var elm_row 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	  	var row_count=elm_row.childNodes.length;
	  	if( row_count== "0" )
	  	{
	  		div_cat.innerHTML = "";
			enactive_catalogue();
	  		var error_hint = xerr.note.split(":");
	  		if( error_hint[1]!= null ) var erro_hi = error_hint[1];
			div_cat.innerHTML = xerr.code + ";" + erro_hi;
	  		setLoading( false );
	  	}else{
	  		var node = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );	
	  		var table = new AW.XML.Table;
	  		island4cat.loadXML( table.getXMLContent(node) );
			show_catalogue();
		}	
	}
	else {	
		div_cat.innerHTML = "";
		enactive_catalogue();
		div_cat.innerHTML = xerr.note;
	  	setLoading( false );
	}
}

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

	table.setColumns( [ "sheetid", "status","rettype", "venderid", "vendername", "shopid", "shopname", "askshopname",
	 "retdate", "operator", "editor", "editdate", "checker", "checkdate","readtime" ] );

	var columnNames = [ "单据号", "状态", "退货类型","供应商", "供应商名称", "门店", "门店名称", "申请地",
		 "退货日期", "业务员", "编辑人", "编辑日期", "审核人", "审核日期","供应商阅读时间" ];
	
	var obj = new AW.UI.Grid;
	obj.setId( "grid_cat" );
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );
		
	var number	= new AW.Formats.Number;
	var number_qty	= new AW.Formats.Number;
	var number_val	= new AW.Formats.Number;
	number_val.setTextFormat( "#,###.##" );
	number_qty.setTextFormat( "#,###." );

	var str		= new AW.Formats.String;	
	
	obj.setCellFormat( [ str, str, str, str, str, str, str, str, str, str, str ] );

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
	
	var sumcount = node_body.getAttribute("count");
	div_cat.innerHTML = "<div>查询结果：总计："+sumcount+"行， 显示："+row_count+"行</div>"+obj.toString();
	
	window.status = "OK";
	setLoading( false );
}
</script>

<script language="javascript">
function open_sheet_detail( sheetid )
{
	current_sheetid = sheetid;
	tag_grp.setSelectedItems( [2] );
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

	setLoading( true );
	div_sheethead.innerHTML = island4purchase.transformNode( format4head.documentElement );
	setLoading( false );
}
function openPrintWnd()
{	
	var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);	
 	var sheetid = current_sheetid;
 	window.open( "retnotice_retail_print.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);		
}


function downExcel(){
	var parms = new Array();
	
	if( txt_venderid.value != '' )  parms.push( "venderid=" + txt_venderid.value 	);
	if( txt_shopid.value != ''  )   parms.push( "shopid=" + txt_shopid.value 	);
	if( txt_editor.value   != '' )  parms.push( "editor="   + txt_editor.value 	);
	if( txt_checker.value  != '' )  parms.push( "checker="  + txt_checker.value 	);
	if( txt_editdate_min.value  != '' )   parms.push( "editdate_min="  + txt_editdate_min.value 	);
	if( txt_editdate_max.value  != '' )   parms.push( "editdate_max="  + txt_editdate_max.value 	);
	if( txt_checkdate_min.value != '' )   parms.push( "checkdate_min="  + txt_checkdate_min.value 	);
	if( txt_checkdate_max.value != '' )   parms.push( "checkdate_max="  + txt_checkdate_max.value 	);

	if( parms.length == 0 ) {
		alert( '至少要提供一个查询条件!' );
		return;
	}
	parms.push( "operation=retnotice" );
	var url = "../DaemonDownloadHisExcel?" + parms.join( '&' );
	window.location.href = url;

}

</script>

</head>

<body onload="init()">
	<div id="title" align="center"
		style="font-size: 16px; font-family: '楷体_GB2312, 黑体'; color: #42658D; font-weight: bold";>退货通知单查询</div>
	<div id="div_loading" style="display: none;">
		<img src="../img/loading.gif"></img> <font color=#003>正在下载,请等候……</font>
	</div>

	<div id="div_tabs" style="width: 100%;"></div>

	<div id="div001">
		<div id="div_cat" style="display: block;"></div>
		<div id="div_detail" style="display: none;">
			<div id="div_navigator">
				<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
					type="button" value="下一单" onclick="sheet_navigate(1)" />
			</div>
			<div id="print4detail" style="display: none;" align="center">
				<script>
					document.write( print4da );
					print4da.onClick = openPrintWnd;
	   			</script>
			</div>
			<div id="div_sheethead"></div>
			<div id="div_sheetbody"></div>
		</div>

		<div id="div_search">
			<table cellspacing="1" cellpadding="2" width="80%"
				class="tablecolorborder">
				<tr>
					<td><label> 供应商编码 </label></td>
					<td><input type="text" id="txt_venderid" size="12" /></td>
					<td><label> 退货地 </label></td>
					<td class=altbg2><%=sba%></td>
				</tr>


				<tr>
					<td><label> 制单日期 </label></td>
					<td class=altbg2><input type="text" id="txt_editdate_min"
						onblur="checkDate(this)" size="12" /> - <input type="text"
						id="txt_editdate_max" onblur="checkDate(this)" size="12" /></td>

					<td><label> 制单人 </label></td>
					<td class=altbg2><input type="text" id="txt_editor" size="16" /></td>
				</tr>

				<tr>
					<td><label> 审核日期 </label></td>
					<td class=altbg2><input type="text" id="txt_checkdate_min"
						onblur="checkDate(this)" size="12" /> - <input type="text"
						id="txt_checkdate_max" onblur="checkDate(this)" size="12" /></td>

					<td><label> 审核人 </label></td>
					<td class=altbg2><input type="text" id="txt_checker" size="16" /></td>
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
					<td><a href="javascript:downExcel();">导出查询结果</a></td>
					<td colspan="4"></td>
				</tr>
			</table>

			<br />

			<table cellspacing="1" cellpadding="2" width="50%"
				class="tablecolorborder">
				<tr>
					<td><label> 单据号 </label></td>
					<td class=altbg2><input type="text" id="txt_sheetid" size="60" /></td>
					<td></td>
					<td></td>
				</tr>
				<tr class="header">
					<td><script>
			document.write( btn_sheetid );
			btn_sheetid.onClick = search_by_sheetid;
			</script></td>
					<td></td>
					<td colspan="3" class=altbg2></td>
				</tr>
			</table>
		</div>
		<!-- end of div_search -->
	</div>
	<!-- end of div001 -->
</body>
</html>
