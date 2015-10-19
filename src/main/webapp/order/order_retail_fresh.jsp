<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	final int moduleid = 3010102;
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

<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>订货审批单接收情况查询-零售商用</title>
<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/ajax.js" type=""> </script>
<script language="javascript" src="../js/Date.js" type=""> </script>
<script language="javascript" src="../js/Number.js" type=""> </script>
<script language="javascript" src="../js/XErr.js" type=""> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js" type=""> </script>

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
	position: absolute;
	top: 220px;
	left: 10px;
}
</style>

<style type="text/css">
.aw-grid-control {
	height: 400px;
	width: 100%;
	font: menu;
	border: none;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}
</style>

<style type="text/css">
#grid_cat {
	background-color: #F9F8F4;
}

#grid_cat .aw-column-1 {
	width: 280px;
}

#grid_cat .aw-grid-headers {
	color: blue
}

#grid_cat .aw-alternate-even {
	background: #fff;
}

#grid_cat .aw-alternate-odd {
	background: #eee;
}

#grid_cat .aw-rows-selected {
	background: #316ac5;
}

#grid_cat .aw-rows-selected .aw-column-1 {
	background: #316ac5;
}
</style>

<script language="javascript">
		var loading_html='<img src="../img/loading.gif"></img><font color=#003>正在下载,请等候……</font>';

		</script>

<script language="javascript">
		/**
		 *检查最大值是否大于最小值
		 */
		function check_data()
		{
			if( total_min.value!='' && total_max.value!='' )
			{
				if( total_max.value < total_min.value )
				{
					alert( "订单总数的最大值应不小于订单总数的最小值!" );	
			  		div_cat.style.display = 'block';
			  		total_min.focus();
			  		return false;
				}	
			}
			
		}

		function check_fresh()
		{
			if( no_read_max.value!='' && no_read_min.value!='' )
			{
				if( no_read_max.value < no_read_min.value )
				{
					alert( "未阅读订单数的最大值应不小于未阅读订单数的最小值!" );	
			  		div_cat.style.display = 'block';
			  		fresh_min.focus();
			  		return false;
				}	
			}
			
		}
		
		function check_fresh1()
		{
			if( al_read_max.value!='' && al_read_min.value!='' )
			{
				if( al_read_max.value < al_read_min.value )
				{
					alert( "已阅读订单数的最大值应不小于已阅读订单数的最小值!" );	
			  		div_cat.style.display = 'block';
			  		fresh_min.focus();
			  		return false;
				}	
			}
			
		}
		
		function check_confirm()
		{
			if( confirm_min.value!='' && confirm_max.value!='' )
			{
				if( confirm_max.value < confirm_min.value )
				{
					alert( "已确认订单数的最大值应不小于已确认订单数的最小值!" );	
			  		div_cat.style.display = 'block';
			  		confirm_min.focus();
			  		return false;
				}	
			}
			
		}
		
		/**
		 *获得查询条件
		 */
		function search_sheet()
		{
			check_data();
			check_fresh();
			check_fresh1();
			check_confirm();
			
			div_cat.innerHTML = "";
			div_cat.style.display = 'none';
			var parms = new Array(); 
		
			if( total_max.value   != '' )       parms.push( "total_max="   + total_max.value 	);
			if( total_min.value   != '' )       parms.push( "total_min="   + total_min.value 	);
			if( no_read_max.value   != '' )     parms.push( "fresh_no_max="   + no_read_max.value 	);
			if( no_read_min.value   != '' )     parms.push( "fresh_no_min="   + no_read_min.value 	);
			if( al_read_max.value   != '' )     parms.push( "fresh_al_max="   + al_read_max.value 	);
			if( al_read_min.value   != '' )     parms.push( "fresh_al_min="   + al_read_min.value 	);
			if( confirm_max.value   != '' )     parms.push( "confirm_max="   + confirm_max.value 	);
			if( confirm_min.value   != '' )     parms.push( "confirm_min="   + confirm_min.value 	);
			
			load_catalogue( parms );
		}
		
		/**
		 *访问后台Daemon
		 */
		function load_catalogue ( parms )
		{
			parms.push( "sheetname=fresh_confirm" );
			parms.push( "timestamp=" + new Date().getTime() );
			set_loading_notice( true );
			
			var url = "../DaemonSearchSheet?" + parms.join( '&' );
			var courier = new AjaxCourier( url );
			courier.island4req  	= null;
			courier.reader.read 	= analyse_catalogue;
			courier.call();	
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
		
			if( xerr.code == "0" ) 
			{    	 
				var elm_row 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/sheet" );
			  	var row_count   = elm_row.childNodes.length;
			  	if( row_count== 0 )
			  	{
			  		div_cat.innerHTML = '<h3>没有你要的记录!</h3>';	
			  		div_cat.style.display = 'block';
			  		set_loading_notice( false );
			  	}else{
					show_catalogue();
				}	
			}
			else {	
				div_cat.innerHTML = '<h3>没有你要的记录!</h3>';	
				div_cat.style.display = 'block';
				set_loading_notice( false );
			}
		}
		</script>

<script language="javascript">
		/**
		 * 用datagrid显示查到的单据信息
		 */
		function show_catalogue()
		{
			window.status = "显示目录 ...";
			
			var table = new AW.XML.Table;
			var node_body 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/sheet" );	
			var row_count   = node_body.childNodes.length;
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
		
			table.setXML( xml );
			
			var columnNames = [ "供应商", "供应商名称", "未阅读单据", "已阅读单据", "已确认单据", "单据总数" ];
			var columnOrder = [ 0, 1, 2, 3, 4, 5 ];
			
			var obj = new AW.Grid.Extended;;
			obj.setId( "grid_cat" );
			obj.setColumnCount( 6 );
			obj.setRowCount( row_count );	
			obj.setHeaderText( columnNames );
			
			obj.setColumnIndices( columnOrder );	
				
			var number	= new AW.Formats.Number;
			var number_qty	= new AW.Formats.Number;
			var number_val	= new AW.Formats.Number;
			number_val.setTextFormat("#,###.##");
			number_qty.setTextFormat( "#,###." );
		
			var str		= new AW.Formats.String;	
			obj.setCellFormat( [ str, str, number, number, number, number ] );
		
			var obj_link = new AW.Templates.Link; 
			obj_link.setEvent( "onclick",
			function(){
				var current_row = obj.getCurrentRow();
				var venderid = obj.getCellValue( 0, current_row );
				openVenderWnd( venderid );
			}
			);
			obj.setCellTemplate( obj_link, 0 ); 
		
			obj.setCellModel(table);
			
			div_cat.innerHTML = obj.toString();
			div_cat.style.display = 'block';
			window.status = "OK";
			set_loading_notice( false );
		}
		
		function exportExcel(){
			check_data();
			check_fresh();
			check_fresh1();
			check_confirm();
			
			var parms = new Array(); 
		
			if( total_max.value   != '' )       parms.push( "total_max="   + total_max.value 	);
			if( total_min.value   != '' )       parms.push( "total_min="   + total_min.value 	);
			if( no_read_max.value   != '' )     parms.push( "fresh_no_max="   + no_read_max.value 	);
			if( no_read_min.value   != '' )     parms.push( "fresh_no_min="   + no_read_min.value 	);
			if( al_read_max.value   != '' )     parms.push( "fresh_al_max="   + al_read_max.value 	);
			if( al_read_min.value   != '' )     parms.push( "fresh_al_min="   + al_read_min.value 	);
			if( confirm_max.value   != '' )     parms.push( "confirm_max="   + confirm_max.value 	);
			if( confirm_min.value   != '' )     parms.push( "confirm_min="   + confirm_min.value 	);
			
			var url = "../DaemonDownloadExcel?operation=fresh_confirm&" + parms.join( '&' );
			
			window.location.href = url;
			return ;
		
		}
		</script>

<script language="javascript">
		/**
		 * 显示下载进程
		 */
		function set_loading_notice( on )
		{
			div_loading.innerHTML = loading_html;
			div_loading.style.display = on ? 'block' : 'none';
		}
		/**
		 * 打开查看某个供应商的单据
		 */
		function openVenderWnd(venderid)
		{
			var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
				",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
		 	window.open( "order_retail.jsp?venderid="+venderid, venderid ,attributeOfNewWnd);	
		}
		

		</script>

<xml id="island4result" />
<xml id="island4cat" />

</head>

<body>
	<div id="div_search">
		<div id="title" align="center">
			<h4>订单接收情况查询</h4>
		</div>
		<fieldset>
			<legend>
				<font size="2px">订货单接收情况查询</font>
			</legend>
			<table width="50%" class="tablecolorborder">
				<tr>
					<td></td>
					<td>最小值</td>
					<td>最大值</td>
				</tr>
				<tr>
					<td>未阅读订单数:</td>
					<td><input type="text" id="no_read_min"
						style="text-align: right;" /></td>
					<td><input type="text" id="no_read_max"
						style="text-align: right;" /></td>
				</tr>
				<tr>
					<td>已阅读订单数:</td>
					<td><input type="text" id="al_read_min"
						style="text-align: right;" /></td>
					<td><input type="text" id="al_read_max"
						style="text-align: right;" /></td>
				</tr>
				<tr>
					<td>已确认订单数:</td>
					<td><input type="text" id="confirm_min"
						style="text-align: right;" /></td>
					<td><input type="text" id="confirm_max"
						style="text-align: right;" /></td>
				</tr>
				<tr>
					<td>订单合计数:</td>
					<td><input type="text" id="total_min"
						style="text-align: right;" /></td>
					<td><input type="text" id="total_max"
						style="text-align: right;" /></td>
				</tr>
			</table>
			<table width="50%" align="right">
				<tr>
					<td>说明：要查询最大值时即查询订单的数量小于等于你填入的数量; 要查询最小值时即查询订单的数量大于等于你填入的数量.</td>
				</tr>
			</table>
			<script>
				var btn_search = new AW.UI.Button;
				btn_search.setControlText( "查询" );
				btn_search.setId( "btn_search" );
				btn_search.setControlImage( "search" );
				document.write( btn_search );
				btn_search.onClick = search_sheet;
				</script>
			<a href="javascript:exportExcel();">导出查询结果到Excel文档</a>
		</fieldset>

		<div id="div_loading" style="display: none;">
			<img src="../img/loading.gif" alt=""></img> <font color="#003">正在下载,请等候……</font>
		</div>

		<fieldset>
			<div id="div_cat" style="display: none;">...</div>
		</fieldset>
	</div>
</body>
</html>
