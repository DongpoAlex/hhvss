<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid=3020203;
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
	sel_book.setAttribute("name","txt_parms");
	
%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>发票接收单查询</title>
<script src="../AW/runtime/lib/aw.js" type=""></script>
<script language="javascript" src="../js/ajax.js" type="text/javascript"> </script>
<script language="javascript" src="../js/XErr.js" type="text/javascript"> </script>
<script language="javascript" src="../js/Date.js" type="text/javascript"> </script>
<script language="javascript" src="../js/Number.js"
	type="text/javascript"> </script>

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<style type="text/css">
.aw-grid-control {
	height: 88%;
	width: 100%;
	border: none;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

#grid_sheet .aw-column-3 {
	text-align: right
}

#grid_sheet .aw-column-4 {
	text-align: right
}

#grid_sheet .aw-column-5 {
	text-align: right
}

#grid_sheet .aw-column-6 {
	width: 200px;
}

#grid_cat {
	background-color: #F9F8F4
}

#grid_cat .aw-column-0 {
	cursor: pointer;
	width: 130px;
}

#grid_cat .aw-column-1 {
	width: 130px;
}

#grid_sheet {
	background-color: #F9F8F4
}

div#div_sheethead {
	margin-top: 5px;
	width: 100%;
}

div#div_sheetbody {
	width: 100%;
}

.box {
	border-style: solid;
	border-color: black;
	border-width: 1px;
	width: 100%;
}

.box div {
	margin: 5px;
}

.sheethead {
	margin: 2px;
}

span.sheethead {
	padding: 8px;
}

label {
	font-size: 11px;
	font-weight: bold;
	color: navy;
}
</style>
<script language="javascript" type="text/javascript"> 
extendDate();
extendNumber();

var arr_sheetid 	= null;
var current_sheetid = "";
var tag_grp = new AW.UI.Tabs;

function init()
{
	install_tag_sheets();
}

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
	div_tabs.innerHTML = tag_grp.toString();
}


/*
 *组合查询条件，开始查询
 */
function search_catalogue()
{
	var parms = new Array();
	if( txt_sheetid.value  != '' )  	parms.push( "sheetid="  + txt_sheetid.value );
	if( txt_refsheetid.value  != '' )  	parms.push( "refsheetid="  + txt_refsheetid.value );
	if( txt_venderid.value  != '' )  	parms.push( "venderid="  + txt_venderid.value );
	if( txt_bookno.value  != '' )   	parms.push( "bookno="  + txt_bookno.value 	);
	if( txt_editdate_min.value  != '' ) parms.push( "editdate_min="   + txt_editdate_min.value 	);
	if( txt_editdate_max.value  != '' ) parms.push( "editdate_max="   + txt_editdate_max.value 	);
	if( parms.length == 0 ) {
		alert( "请至少选择一个查询条件" );
		return;
	}
	load_catalogue( parms );
	tag_grp.setSelectedItems( [1] );
	enactive_catalogue();
}

/**
 *根据单据号查询. 可以一次输入多个单据号,以逗号分隔. 
 */
 
function search_by_invoiceno()
{
	var str = txt_invoiceno.value;
	str = str.replace( / /g, '' );
	if( str == 0 ) { alert( "未输入发票号!" ); return; }
	var arr_id = str.split ( ',' );
	
	var parms = new Array();
	for( var i=0; i<arr_id.length; i++ ) {
		var s = arr_id[ i ];
		if( s != '' ) parms.push( "invoiceno=" + s );
	}

	load_catalogue( parms );
	tag_grp.setSelectedItems( [1] );
	enactive_catalogue();

}

function enactive_search()
{
	div_search.style.display = 'block';
	div_cat.style.display 	 = 'none';
	div_detail.style.display = 'none';
}

function enactive_catalogue()
{
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

/**
 *根据输入的单据号或供应商的信息，进行查询
 */ 
function load_catalogue( parms )
{
	setLoading( true );
	div_cat.innerHTML = "";
	parms.push( "sheetname=venderinvoice" );
	parms.push( "timestamp=" + new Date().getTime() );
	var url = "../DaemonSearchSheet?" + parms.join( '&' );
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_catalogue;
	courier.call();	
}

/**
 *处理返回数据，错误检查，给予相应的提示
 */

function analyse_catalogue( text )
{
	island4result.loadXML( text );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" ) 
	{    	 
		var elm_row 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	  	var row_count   = elm_row.childNodes.length;
	  	if( row_count== 0 )
	  	{
	  		div_cat.innerHTML="数据库没有你要查找的数据!";
	  	}
	  	else{
	  		show_catalogue();
		}	
	}
	else {	
		div_cat.innerHTML = xerr.note;
	}
	setLoading( false );
}

/**
 *用datagrid显示查到的单据信息
 */
function show_catalogue()
{
	arr_sheetid = new Array();
	var node_sheetid 	= island4result.XMLDocument.selectNodes( "/xdoc/xout/catalogue/row/sheetid" );	
	var next_node = node_sheetid.nextNode();
	while( next_node != null ) {
		arr_sheetid[ arr_sheetid.length ] = next_node.text;
		next_node = node_sheetid.nextNode();
	}
	
	if( arr_sheetid !=null && arr_sheetid.length >0 ) current_sheetid = arr_sheetid[0];

	var table = new AW.XML.Table;
	table.setTable( "/xdoc/xout/catalogue" );
	table.setXML( island4result.xml );
	table.setColumns(["sheetid","refsheetid","editor","editdate","checkdate","bookname","venderid"]);
	var columnNames = ["单据号", "付款单号", "制单人", "制单日期", "提交日期", "分公司", "供应商编码" ];
	var obj = new AW.Grid.Extended;
	var row_count = table.getCount();
	obj.setId( "grid_cat" );
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );		
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
	obj.setFooterText(["查询结果:"+row_count+"条"]);
	obj.setFooterVisible(true);
	
	div_cat.innerHTML = obj.toString();
}

function open_sheet_detail ( sheetid )
{
	current_sheetid = sheetid;
	tag_grp.setSelectedItems( [2] );
}

function sheet_navigate ( step )
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
	
	var sheetid = arr_sheetid[ offset ];
	open_sheet_detail( sheetid );
}

function load_sheet_detail ( sheetid )
{
	if( sheetid == null || sheetid.length == 0 ){
	 alert( "请先查询数据，再查看单据明细" );
	 return false;
	 }
	setLoading( true );
	window.status = "加载对帐申请单明细: " + sheetid;
	current_sheetid = sheetid;
	
	var url = "../DaemonViewSheet?sheet=venderinvoice&sheetid=" + sheetid;
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_sheet_detail;
	courier.call();	
	
}


function analyse_sheet_detail( text )
{
	div_sheethead.innerHTML = "";
	div_sheetbody.innerHTML = "";
	island4purchase.loadXML( text );
	show_detail();
	setLoading( false );
}

/**
 *利用xsl显示单据明细的表头，用datagrid显示表体
 */
function show_detail()
{
	setLoading( true );

	var node_head 	= island4purchase.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head" );
	island4head.loadXML( node_head.xml );
	div_sheethead.innerHTML = island4head.transformNode( format4head.documentElement );
	var table = new AW.XML.Table;
	table.setTable("/xdoc/xout/sheet/body");
	table.setXML( island4purchase.xml );
	table.setColumns(["invoiceno","invoicetype","invoicedate","taxrate","taxamt","taxableamt","goodsdesc"]);
	var columnNames = [ "发票号", "发票类型","开票日期","税率","税额","价额","发票描述"];
	var obj2 = new AW.Grid.Extended;
	var row_count = table.getCount();
	obj2.setId( "grid_sheet" );
	obj2.setColumnCount( columnNames.length );
	obj2.setRowCount( row_count );	
	obj2.setHeaderText( columnNames );
	obj2.setSelectorVisible(true);
	obj2.setSelectorWidth(30);
	obj2.setSelectorText(function(i){return this.getRowPosition(i)+1});
	var str	= new AW.Formats.String;
	var num = new AW.Formats.Number;
	obj2.setCellFormat( [  num, num, str, num, num, num ,str] );
	obj2.setCellModel( table );
	setLoading( false );
	div_sheetbody.innerHTML = obj2.toString();
}


</script>


<xml id="island4result" />
<xml id="island4purchase" />
<xml id="island4head" />
<xml id="island4body" />
<xml id="format4head" src="../venderinvoice/venderinvoice_head.xsl" />

</head>

<body onload="init()">
	<div id="title">发票接收单查询－零售商</div>
	<div id="div_tabs"></div>
	<div id="div_search">
		<table cellspacing="1" cellpadding="4" class="tablecolorborder">
			<tr>
				<td><label> 付款申请单号： </label></td>
				<td class="altbg2"><input type="text" id="txt_refsheetid" /></td>
				<td><label> 发票接收单号： </label></td>
				<td class="altbg2"><input type="text" id="txt_sheetid" /></td>
				<td><label> 供应商编码： </label></td>
				<td class="altbg2"><input type="text" id="txt_venderid" /></td>
			</tr>
			<tr>
				<td><label> 结算公司: </label></td>
				<td class="altbg2"><%=sel_book %></td>
				<td><label> 制单日期: </label></td>
				<td class="altbg2" colspan="3">从<input type="text"
					id="txt_editdate_min" onblur="checkDate(this)" size="12" /> 到<input
					type="text" id="txt_editdate_max" onblur="checkDate(this)"
					size="12" />
				</td>
			</tr>
			<tr class="singleborder">
				<td colspan="6"></td>
			</tr>
			<tr class="whiteborder">
				<td colspan="6"></td>
			</tr>
			<tr class="header">
				<td><script type="text/javascript">
			var btn_search = new AW.UI.Button;
			btn_search.setControlText( "查询" );
			btn_search.onClick = search_catalogue;
			document.write( btn_search );
		</script></td>
				<td colspan="4" class="altbg2">
					<div id="div_info" class="smalltxt"></div>
				</td>
			</tr>

		</table>

		<br />
		<table cellspacing="1" cellpadding="2" class="tablecolorborder">
			<tr>
				<td><label>按发票号查询：</label></td>
				<td class="altbg2"><input type="text" id="txt_invoiceno" /></td>
				<td><script type="text/javascript">
				var btn_sheetid = new AW.UI.Button;
				btn_sheetid.setControlText( "查询" );
				btn_sheetid.onClick = search_by_invoiceno;
				document.write( btn_sheetid );
			</script></td>

			</tr>
		</table>
	</div>


	<div id="div_cat" style="display: none;">...</div>

	<div id="div_detail" style="display: none;">
		<br />
		<div id="div_navigator">
			<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
				type="button" value="下一单" onclick="sheet_navigate(1)" /> <span
				id="span_print"></span>
		</div>
		<div id="div_sheethead"></div>
		<div id="div_sheetbody" style="height: 380px;"></div>
	</div>

</body>
</html>
