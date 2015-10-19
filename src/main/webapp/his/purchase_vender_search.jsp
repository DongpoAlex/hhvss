﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%

	final int moduleid = 9999901;
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
	SelBranchAll sel_branch = new SelBranchAll(token, shoptype, note );
	sel_branch.setAttribute( "id", "txt_shopid" );
%>
<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>订货通知单查询-供应商专用</title>
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
#tag_grp {
	width: 100%;
	height: 25px;
	margin-bottom: 2px;
}

.aw-grid-control {
	height: 400px;
	width: 100%;
	border: none;
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
	background-color: #F9F8F4;
}

#grid_cat .aw-column-0 {
	width: 130px;
	cursor: pointer;
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
			var current_sheetid = "";
			var tag_grp 		= new AW.UI.Tabs;
	
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
				div_tabs.innerHTML=tag_grp.toString();
			}

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
				parms.push( "sheetname=purchase" );
				if( $("txt_shopid").value 	!= '' ) 	 parms.push( "shopid="    + $("txt_shopid").value );
				if( $("txt_refsheetid").value 	 != '' ) parms.push( "refsheetid="    + $("txt_refsheetid").value );
				if( $("txt_orderdate_min").value != '' ) parms.push( "orderdate_min="  + $("txt_orderdate_min").value );
				if( $("txt_orderdate_max").value != '' ) parms.push( "orderdate_max="  + $("txt_orderdate_max").value );
				if( $("txt_deadline_min").value  != '' ) parms.push( "deadline_min="  + $("txt_deadline_min").value );
				if( $("txt_deadline_max").value  != '' ) parms.push( "deadline_max="  + $("txt_deadline_max").value );
				load_catalogue( parms );
				tag_grp.setSelectedItems( [1] );
			}
			
			function search_by_sheetid()
			{
				var str = $("txt_sheetid").value;
				str = str.replace( / /g, ',' );
				str = str.replace( /,+/g, ',' );
				if( str == 0 ) { alert( "请输入单据号!" ); return; }
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
				setLoading(true);
				parms.push( "sheetname=purchase" );
				parms.push( "timestamp=" + new Date().getTime() );
				var url = "../DaemonSearchHisSheet?" + parms.join( '&' );
				var courier = new AjaxCourier( url );
				courier.reader.read = analyse_catalogue;
				courier.call();	
			}
			
			function analyse_catalogue( text )
			{
				$("island4result").loadXML( text );
				var elm_err = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xerr" );
				var xerr 	= parseXErr( elm_err );
				if( xerr.code == "0" ) 
				{    	 
					$("div_cat").innerHTML = "";
					var elm_row = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
				  	var row_count=elm_row.childNodes.length;
				  	if( row_count== "0" )
				  	{
				  		enactive_catalogue();
						$("div_cat").innerHTML = "没有找到您要的退货通知单.";
				  	}else{
						show_catalogue();
					}
				}else{	
					div_cat.innerHTML = "";
				  	enactive_catalogue();
				  	var error_hint = xerr.note.split(":");
				  	if( error_hint[1]!= null ) var erro_hi = error_hint[1];
					$("div_cat").innerHTML = xerr.code + ";" + erro_hi;
				}
				setLoading(false);
			}
			
			function show_catalogue()
			{
				arr_sheetid = new Array();
				var node_sheetid 	= $("island4result").XMLDocument.selectNodes( "/xdoc/xout/catalogue/row/sheetid" );	
				var next_node = node_sheetid.nextNode();
				while( next_node != null ) {
					arr_sheetid[ arr_sheetid.length ] = next_node.text;
					next_node = node_sheetid.nextNode();
				}
				
				if( arr_sheetid !=null && arr_sheetid.length >0 ) current_sheetid = arr_sheetid[0];
				
				var node_body 	= $("island4result").XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );	
				var row_count   = node_body.childNodes.length;
				var table = new AW.XML.Table;
				table.setXML( table.getXMLContent(node_body) );
				
				table.setColumns( [ "sheetid", "shopid", "shopname", 
				 "logisticsname", "orderdate", "deadline", "editor", "checker" ] );
			
				var columnNames = [ "订货通知单号", "门店", "门店名称",
					 "送货方式", "订货日期", "截止日期", "编辑人", "审核人" ];
				
				var obj = new AW.UI.Grid;
				obj.setId( "grid_cat" );
				obj.setColumnCount( columnNames.length );
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
				$("div_cat").innerHTML = obj.toString();
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
				setLoading(true);
				if( sheetid == null || sheetid.length == 0 ) return false;
				current_sheetid = sheetid;
				var url = "../DaemonViewHisSheet?sheet=purchase&sheetid=" + sheetid;
				var courier = new AjaxCourier( url );
				courier.reader.read = analyse_detail;
				courier.call();	
			}
			
			function analyse_detail( text )
			{
				setLoading(false);
				$("island4purchase").loadXML( text );
				$("div_navigator").style.display = 'block';
				$("div_sheethead").innerHTML = $("island4purchase").transformNode( format4sheet.documentElement );
			}

			function open_sheet_detail( sheetid )
			{
				current_sheetid = sheetid;
				tag_grp.setSelectedItems( [2] );
			}
		</script>

<script language="javascript">
			function open_win_print()
			{		
				var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
					",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
			 	var sheetid = current_sheetid;
			 	window.open( "purchaser_retail_print.jsp?sheetid="+sheetid, sheetid, attributeOfNewWnd );		
			}
		</script>
</head>
<xml id="island4result" />
<xml id="island4purchase" />
<xml id="format4sheet" src="format4purchase.xsl" />
<body onload="init()">
	<div id="div_tabs"></div>
	<div id="div_cat" style="display: block;"></div>
	<div id="div_detail" style="display: none;">
		<div id="div_navigator" style="display: none;">
			<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
				type="button" value="下一单" onclick="sheet_navigate(1)" /> <input
				type="button" value="打印单据" onclick="open_win_print()" />
		</div>
		<br />
		<div id="div_sheethead"></div>
	</div>

	<div id="div_search">
		<table cellspacing="1" cellpadding="2" width="70%"
			class="tablecolorborder">
			<tr>
				<td><label> 要货地: </label></td>
				<td><%=sel_branch%></td>
				<td>订货审批单号</td>
				<td><input type="text" id="txt_refsheetid" /></td>
			</tr>
			<tr>
				<td>订货日期:</td>
				<td class=altbg2><input type="text" id="txt_orderdate_min"
					onblur="checkDate(this)" size="12" /> - <input type="text"
					id="txt_orderdate_max" onblur="checkDate(this)" size="12" /></td>
				<td>截至日期</td>
				<td><input type="text" id="txt_deadline_min"
					onblur="checkDate(this)" size="12" /> - <input type="text"
					id="txt_deadline_max" onblur="checkDate(this)" size="12" /></td>
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
				<td colspan="3"></td>
			</tr>
		</table>
		<br />
		<table cellspacing="1" cellpadding="2" width="50%"
			class="tablecolorborder">
			<tr>
				<td><label> 订货通知单号 </label></td>
				<td><input type="text" id="txt_sheetid" size="60" /></td>
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
</body>
</html>
