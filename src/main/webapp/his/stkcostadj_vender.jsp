<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3020108;
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
	int[] shoptype = { 11, 22 };
	String note = "任意门店";
	SelBranchAll sel_branch = new SelBranchAll( token,shoptype, note );
	sel_branch.setAttribute( "id", "txt_branch" );
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>库存进价调整单-供应商专用</title>
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

<style>
.aw-grid-control {
	height: 88%;
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
	background-color: #F9F8F4;
	width: 100%;
}

#grid_cat .aw-column-0 {
	width: 110px;
}

#grid_cat .aw-column-1 {
	width: 60px;
}

#grid_cat .aw-column-2 {
	width: 90px;
}

#grid_cat .aw-column-3 {
	width: 110px;
}

#grid_cat .aw-column-4 {
	width: 80px;
	text-align: right;
}

#grid_cat .aw-column-5 {
	width: 80px;
	text-align: right;
}

#grid_cat .aw-column-6 {
	width: 80px;
	text-align: right;
}

#grid_cat .aw-column-7 {
	width: 80px;
	text-align: right;
}

#grid_cat .aw-column-8 {
	width: 80px;
}

#grid_cat .aw-column-9 {
	width: 80px;
}

#grid_cat .aw-column-10 {
	width: 80px;
}

.border {
	border: 1px solid #999;
	padding: 8px
}

h5 {
	margin: 0px;
	padding: 6px;
}
</style>

<script language="javascript">
		var tag_grp = new AW.UI.Tabs;
		
		var arr_sheetid 	= null;
		var current_sheetid 	= "";
		var row_selected	= "";
		
		var btn_search = new AW.UI.Button;
		btn_search.setControlText( "查询" );
		btn_search.setId( "btn_search" );
		btn_search.setControlImage( "search" );
		
		var btn_search_sheetid = new AW.UI.Button;
		btn_search_sheetid.setControlText( "查询" );
		btn_search_sheetid.setId( "btn_search_sheetid" );
		btn_search_sheetid.setControlImage( "search" );
		
		var btn_print = new AW.UI.Button;
		btn_print.setControlText( "打印单据" );
		btn_print.setId( "btn_print" );

		function init(){
			install_tag_sheets();
		}

		
		function show_catalogue()
		{
			setLoading( true );
			div_cat.innerHTML = "";
			
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
			
			/**
			 *判断浏览器的类型
			 */
			if (window.ActiveXObject) {
				xml = node;
			}
			else {
				xml = document.implementation.createDocument( "", "", null);
				xml.appendChild( island4cat.selectSingleNode( "*" ) );
			}		
		
			var table = new AW.XML.Table;
			table.setXML( xml );
			
			table.setColumns(["sheetid","shopid","shopname","paytypename","totalamt","totaltaxamt17","totaltaxamt13","totaltaxamt","editor","editdate","checker","checkdate","note"]);
			
			var columnNames = [ "单据编号", "门店编码","门店",	"结算方式", "未含税金额", "17税额", 
				"13税额", "价税合计", "制单人", "制单日期", "审核人", "审核日期", "备注" ];
			
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
			
			obj.setCellFormat( [ str, str, str, str, number_val, number_val, number_val, number_val ] );
			
			var obj_link = new AW.Templates.Link;
			obj_link.setEvent( "onclick",
				function(){
					var current_row=obj.getCurrentRow();
					var sheetid = obj.getCellValue( 0, current_row );
					open_sheet_detail( sheetid );
				}
			);	
			obj.setCellTemplate( obj_link, 0); 	
			obj.setCellModel(table);
			
			div_cat.innerHTML = obj.toString();
			setLoading( false );
		}
	</script>

<script language="javascript">
		function install_tag_sheets()
		{
			tag_grp.setId( "tag_grp" );
			tag_grp.setItemText( [ "查询条件", "单据目录", "单据明细"] );
			tag_grp.setItemCount( 3 );
			tag_grp.onSelectedItemsChanged = function( idx ) {
				if( idx == "0" ) enactive_search() ;
				if( idx == "1" ) {
					enactive_catalogue();
				}
				if( idx == "2" ){ 
					load_stkcostadjitem( current_sheetid );
					enactive_detail();
				}
			};
		
			tag_grp.setSelectedItems( [0] );
			div_tabs.innerHTML = tag_grp.toString();
		}
		
		//获得查询条件
		function search_sheet()
		{			
		 	var parms = new Array();
			
			if( txt_branch.value  != '' )   parms.push( "shopid="   + txt_branch.value 	);		
			if( txt_checkdatemin.value  != '' )   parms.push( "checkdate_min="   + txt_checkdatemin.value 	);
			if( txt_checkdatemax.value  != '' )   parms.push( "checkdate_max="   + txt_checkdatemax.value 	);
			
			load_catalogue( parms );
			
			enactive_catalogue();
			tag_grp.setSelectedItems( [1] );
		}
		
		
		function search_sheetid(){
			var sheetid = txt_sheetid.value;
			if( sheetid == '' ) {
				alert("请填写单据号！");return;
			}
			
			open_sheet_detail(sheetid);
		}
		//显示相应的div层
		function enactive_search()
		{
			div_confirm.style.display = 'none';
			div_search.style.display = 'block';
			div_detail.style.display = 'none';
			div_cat.style.display = 'none';
			div_hint0.style.display = 'none';
		}
		
		function enactive_catalogue()
		{
			div_confirm.style.display = 'none';
			div_search.style.display = 'none';
			div_cat.style.display 	 = 'block';
			div_detail.style.display = 'none';
			div_hint0.style.display = 'none';
		}
		
		
		
		function enactive_detail()
		{
			div_confirm.style.display = 'none';
			div_search.style.display = 'none';
			div_cat.style.display = 'none';
			div_detail.style.display = 'block';
			div_hint0.style.display = 'none';
		}
		
		
		/**
		 * 访问后台, 取单据目录
		*/
		function load_catalogue( parms )
		{
			try{
				parms.push( "sheetname=stkcostadj" );
				var url = "../DaemonSearchHisSheet?" + parms.join( '&' );
				var courier = new AjaxCourier( url );
				courier.island4req  	= null;
				courier.reader.read 	= analyse_catalogue;
				courier.call();	
				setLoading( true );
			}catch(e) {
				alert(e);
			}	
		}
		
		/**
		 *错误检查，给予相应的提示
		 */
		function analyse_catalogue( text )
		{
			island4cat.loadXML( text );
			island4result.loadXML( text );
			var elm_err 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xerr" );
			var xerr 	= parseXErr( elm_err );
		
			if( xerr.code == 0 ) 
			{    	 
				var elm_row 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
			  	var row_count   = elm_row.childNodes.length;
			  	if( row_count== 0 )
			  	{
			  		enactive_catalogue();
			  		div_cat.innerHTML = "";
			  		var clew = "没有库存进价调整单";
			  		div_cat.innerHTML += clew;
			  		setLoading( false );
			  	}else{
			  		var node = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
			  		var table = new AW.XML.Table;
					island4cat.loadXML( table.getXMLContent(node) );
					show_catalogue();
				}	
			}else if( xerr.code == "-1" )
			{
				div_cat.innerHTML = "";
			  	enactive_catalogue();
				div_cat.innerHTML = "你要查找的数据量太大，请缩小查询条件范围.";
			  	setLoading( false );
			}else {	
				enactive_catalogue();
				div_cat.innerHTML = "没有找到库存进价调整单.";
			  	setLoading( false );
			}
		}
		
		/**
		 * 显示单据明细
		 */
		function open_sheet_detail ( sheetid )
		{
			current_sheetid = sheetid;
			tag_grp.setSelectedItems( [2] );
		}
		
		/**
		 * 浏览单据
		 */
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
		
		//加载库存进价调整单明细
		function load_stkcostadjitem( sheetid )
		{
			
			if( sheetid == null || sheetid.length == 0 ) return false;
			
			current_sheetid = sheetid;
			
			var url = "../DaemonViewHisSheet?sheet=stkcostadj&sheetid=" + sheetid;
			var courier = new AjaxCourier( url );
			courier.island4req  	= null;
			courier.reader.read 	= analyse_stkcostadj;
			courier.call();	
			setLoading( true );
		}
		
		/**
		 * 处理后台发回的数据
		 */
		function analyse_stkcostadj( text )
		{
			setLoading( false );
			island4purchase.loadXML( text );
			show_stkcostadj();	
		}
		
		/**
		 *利用xsl显示单据明细的表头
		 */
		function show_stkcostadj()
		{	
			div_sheethead.innerHTML = island4purchase.transformNode( format4head.documentElement );
		}
		
		
		function openPrintWnd()
		{		
			var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
				",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
		
		 	var sheetid = current_sheetid;
		 	window.open( "stkcostadj_vender_print.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);		
		}
		
		function downExcel(){
			var url = "../DaemonDownloadHisExcel?operation=stk&sheetid=" + current_sheetid;
			
			window.location.href = url;
		}
		</script>
<xml id="island4result" />
<xml id="island4purchase" />
<xml id="island4cat" />
<xml id="format4head" src="stkcostadj_vender.xsl" />
<xml id="island4confirm" />

</head>

<body onload="init()">
	<div id="title">库存批次调整单</div>
	<div id="div_tabs"></div>
	<div id="div001">
		<div id="div_detail" style="display: none;">
			<br />
			<div id="div_navigator">
				<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
					type="button" value="下一单" onclick="sheet_navigate(1)" /> <a
					href="javascript:downExcel();">导出明细</a>
			</div>
			<br />
			<div id="makesure" align="center"></div>
			<div id="printfunction">
				<script language="javascript">
		   			document.write(btn_print);
		   			btn_print.onClick=openPrintWnd;
		   		</script>
			</div>
			<div id="div_hint0" style="display: none;"></div>
			<div id="div_sheethead"></div>
		</div>
		<div id="div_search" style="display: none;">
			<div class="border">
				<h5>组合条件查询</h5>
				发生门店:<%=sel_branch%>
				审核日期: <input type="text" id="txt_checkdatemin" value="" size="10"
					onchange="checkDate(this)" /> 至 <input type="text"
					id="txt_checkdatemax" value="" size="10" onchange="checkDate(this)" />

				<script>
						document.write( btn_search );
						btn_search.onClick = search_sheet;
					</script>

			</div>
			<div class="border">
				<h5>按单据号查询</h5>
				单据号：<input type="text" id="txt_sheetid" />
				<script>
						document.write( btn_search_sheetid );
						btn_search_sheetid.onClick = search_sheetid;
					</script>
			</div>
		</div>

		<div id="div_cat" style="display: none;"></div>

		<div id="div_confirm" style="display: none;">

			<br /> <br /> <br />

		</div>
	</div>
</body>
</html>
