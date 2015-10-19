<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3010121;
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
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>验收单查询</title>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"></script>
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
</style>
<script language="javascript">
		var arr_sheetid 	= null;
		var current_sheetid 	= "";
		var g_status = -1;
		var format_show_xsl = "<%=token.site.toXSLPatch("receipt.xsl") %>";
		
		var tag_grp 	 = new AW.UI.Tabs;
		var table4search = new AW.XML.Table;
		var table4detail = new AW.XML.Table;
		var table4confirm = new AW.XML.Table;
		var btn_search = new AW.UI.Button;
		btn_search.setControlText( "查询" );
		btn_search.setId( "btncheck" );
		btn_search.setControlImage( "search" );	
		btn_search.onClick = search_sheet;
		
		window.onload = function(){
			$('div_button_search').innerHTML=btn_search;
			
			install_tags();
			
			obj = $("txt_status");
			if( g_status != -1 ){
				obj.value = g_status;
				search_sheet();
			}
		};
	
		function install_tags()
		{
			var tags = [ "查询条件", "单据目录", "单据明细" ];
			tag_grp.setId( "tag_grp" );
			tag_grp.setItemText(tags);
			tag_grp.setItemCount(tags.length );
			tag_grp.setSelectedItems( [0] );
			$('div_tabs').innerHTML=tag_grp.toString();
			
			tag_grp.onSelectedItemsChanged = function( idx ) {
				enactive(idx);
				if( idx == 2 ) {load_sheet_detail( current_sheetid );}
			};
		}
		//切换显示层
		function enactive(idx){
			for ( var i = 0; i < 3; i++) {
				if(idx==i){
					$('div'+i).style.display='block';
				}else{
					$('div'+i).style.display='none';
				}
			}
		}

		function cookParms(){
			var parms = new Array();

			parms.push("clazz=Receipt");
			parms.push("operation=search");
			
			if(  $F('txt_editdate_min')  != '' )   parms.push( "editdate_min="  + $F('txt_editdate_min') );
			if(  $F('txt_editdate_max')  != '' )   parms.push( "editdate_max="  + $F('txt_editdate_max') );
			if(  $F('txt_status')  != '' )   parms.push( "status="  + $F('txt_status') );
			
			var str = $F('txt_shopid');
			if(str!='') {
				str = str.replace( / +/g, ',' );		// 把空格换成逗号
				str = str.replace( /,+/g, ',' );		// 把多个逗号换成一个逗号
				var arr_id = str.split ( ',' );
				for( var i=0; i<arr_id.length; i++ ) {
					var s = arr_id[ i ];
					if( s != '' ) parms.push( "shopid=" + s );
				};
			}
			
			var str = $F('txt_sheetid');
			if(str!='') {
				str = str.replace( / +/g, ',' );		// 把空格换成逗号
				str = str.replace( /,+/g, ',' );		// 把多个逗号换成一个逗号
				var arr_id = str.split ( ',' );
				for( var i=0; i<arr_id.length; i++ ) {
					var s = arr_id[ i ];
					if( s != '' ) parms.push( "sheetid=" + s );
				};
			}

			var str = $F('txt_refsheetid');
			if(str!='') {
				str = str.replace( / +/g, ',' );		// 把空格换成逗号
				str = str.replace( /,+/g, ',' );		// 把多个逗号换成一个逗号
				var arr_id = str.split ( ',' );
				for( var i=0; i<arr_id.length; i++ ) {
					var s = arr_id[ i ];
					if( s != '' ) parms.push( "refsheetid=" + s );
				};
			}
			
			return parms;
		}
		
		function search_sheet()
		{
			tag_grp.setSelectedItems( [1] );
			setLoading( true );
			$("div1").innerHTML="正在查询，请稍后……";
			var url = "../DaemonSheet?" + cookParms().join( '&' );
			table4search.setURL( url );
			table4search.request();
			table4search.response 	= analyse_search;
		}
		
		function analyse_search( text )
		{
			setLoading(false);
			table4search.setTable("xdoc/xout/rowset");
			table4search.setRows("row");
			table4search.setXML( text );
			if( table4search.getErrCode() != 0 ){	//处理xml中的错误消息
				$("div1").innerHTML = table4search.getErrNote() ;
				return;
			}
		
			show_catalogue();
		
		
			//记录所有单据号到数组
			arr_sheetid = new Array();
			var node_sheetid 	= table4search.getXML().selectNodes( "/xdoc/xout/rowset/row/sheetid" );
			var next_node = node_sheetid.nextNode();
			while( next_node != null ) {
				arr_sheetid.push(next_node.text);
				next_node = node_sheetid.nextNode();
			}
			if( arr_sheetid.length >0 ) current_sheetid = arr_sheetid[0];
			
		}
		
		
		function show_catalogue()
		{
			table4search.setColumns( [ "sheetid","refsheetid", "statusname","shopid", "shopname", 
			  "operator", "editor", "editdate", "checker", "checkdate" ] );
		
			var columnNames = [ "单据号","订单号", "状态","门店", "门店名称",
				  "业务员", "编辑人", "编辑日期", "审核人", "审核日期" ];
			
			var grid = new AW.UI.Grid;
			var row_count = table4search.getCount();
			grid.setId( "grid_cat" );
			grid.setColumnCount( columnNames.length );
			grid.setRowCount( row_count );	
			grid.setHeaderText( columnNames );
				
			grid.setSelectorVisible(true);
			grid.setSelectorWidth(30);
			grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
		
			var obj_link = new AW.Templates.Link;
			obj_link.setEvent( "onclick",
				function(){
					var current_row = grid.getCurrentRow();
					var sheetid = grid.getCellValue( 0, current_row );
					open_sheet_detail( sheetid );
				}
			);
			grid.setCellTemplate( obj_link, 0 ); 
			grid.setCellModel(table4search);
		
			var row_total = table4search.getXMLNode("/xdoc/xout/rowset").getAttribute("row_total");
			var htmlCountInfo = "记录总数"+row_total+"行；当前显示"+row_count+"行。<br>";
			if(row_count<row_total){
				var htmlCountInfo = "记录总数"+row_total+"行；当前显示"+row_count+"行。请尝试修改查询条件查看未显示数据<br>";
			}
			
			$("div1").innerHTML = htmlCountInfo+grid.toString();
		}
		
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
			$("offset_current").innerText = "当前第：" + (offset+1) + "单，共有：" + arr_sheetid.length + "单";
			open_sheet_detail( arr_sheetid[offset] );
		}
		
		function load_sheet_detail( sheetid )
		{
			if( sheetid == null || sheetid.length == 0 ) return false;
			var url = "../DaemonSheet?operation=show&clazz=Receipt&sheetid=" + sheetid;
			table4detail.setURL( url );
			table4detail.request();
			table4detail.response = function(text){
				setLoading(false);
				table4detail.setXML(text);
				$("div_sheetshow").innerHTML = table4detail.transformNode( format_show_xsl );
				//修改单据状态为已阅读
				if(table4detail.getXMLText("xdoc/xout/sheet/head/row/status")==0){
					read_already( current_sheetid );
				}
			};
			setLoading(true);
		}
		
		function open_sheet_detail( sheetid )
		{
			current_sheetid = sheetid;
			tag_grp.setSelectedItems( [2] );
		}
		
		
		function read_already( sheetid )
		{
			var url = "../DaemonSheet?operation=doRead&clazz=Receipt&sheetid=" + sheetid;
			table4confirm.setURL( url );
			table4confirm.request();
		}
		
		function confirm_already( sheetid )
		{
			var url = "../DaemonSheet?operation=doConfirm&clazz=Receipt&sheetid=" + sheetid;
			table4confirm.setURL( url );
			table4confirm.request();
		}
		
		function open_win_print()
		{	
			var status =table4detail.getXMLText("xdoc/xout/sheet/head/row/status");
			if(status==1 || status==0){
			 	confirm_already(current_sheetid);
			}
			var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
				",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
		 	window.open( "receipt_print.jsp?sheetid="+current_sheetid, current_sheetid, attributeOfNewWnd );		
		}
		</script>
</head>
<body>
	<div id="div_tabs" style="width: 100%;"></div>
	<div id="div1"></div>
	<div id="div2" style="display: none;">
		<div id="div_navigator">
			<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
				type="button" value="下一单" onclick="sheet_navigate(1)" /> <input
				type="button" value="打印单据" onclick="open_win_print()" /> <span
				id="offset_current"></span>
		</div>
		<div id="div_warning" class="warning"></div>
		<div id="div_sheetshow"></div>
	</div>

	<div id="div0" class="search_main">
		<div class="search_parms">
			验收门店:<input type="text" id="txt_shopid" size="12" /><a
				href="javascript:showShopList('txt_shopid')">选择门店</a>
		</div>

		<div class="search_parms">
			单据状态:<select id="txt_status">
				<option>全部</option>
				<option value="0" selected="selected">未阅读</option>
				<option value="1">已阅读</option>
				<option value="10">已确认</option>
			</select>
		</div>
		<div class="search_parms">
			验收日期: <input type="text" id="txt_editdate_min"
				onblur="checkDate(this)" size="12" /> <img
				src="../img/datePicker.gif" align="absmiddle"
				style="cursor: pointer"
				onclick="WdatePicker({el:'txt_editdate_min'})" /> - <input
				type="text" id="txt_editdate_max" onblur="checkDate(this)" size="12" />
			<img src="../img/datePicker.gif" align="absmiddle"
				style="cursor: pointer"
				onclick="WdatePicker({el:'txt_editdate_max'})" />
		</div>
		<div class="search_parms">
			验收单号: <input type="text" id="txt_sheetid" size="20" />
		</div>
		<div class="search_parms">
			订单单号: <input type="text" id="txt_refsheetid" size="20" />
		</div>
		<div class="search_button" id="div_button_search"></div>
	</div>
	&nbsp;
</body>
</html>