<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%

	final int moduleid = 3010135;
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
	SelBranchAll sel_branch = new SelBranchAll( token,shoptype, note );
	sel_branch.setAttribute( "id", "txt_shopid" );
%>
<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>带货安装单查询-供应商专用</title>
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
	height: 78%;
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
	width: 110px;
	cursor: pointer;
	color: blue;
}

#grid_cat .aw-column-1 {
	
}

#grid_cat .aw-column-2 {
	
}

#grid_cat .aw-column-5 {
	
}

#grid_cat .aw-column-6 {
	
}

#grid_cat .aw-column-7 {
	
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
					if( idx == "2" ) {enactive_detail();
						if ( arr_sheetid != null )load_sheet_detail( current_sheetid );	
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
				if($("txt_venderid").value 	== ''){
					alert('供应商编码必须填写!');
					return;
				}
				var parms = new Array();
				parms.push( "sheetname=salepick" );
				if( $("txt_venderid").value 	!= '' ) parms.push( "venderid="    + $("txt_venderid").value );
				if( $("txt_shopid").value 	!= '' ) parms.push( "shopid="    + $("txt_shopid").value );
				if( $("txt_sheetid").value 	!= '' ) parms.push( "sheetid="    + $("txt_sheetid").value );
				if( $("txt_status").value 	!='')	parms.push( "status="    + $("txt_status").value );
				load_catalogue( parms );
				tag_grp.setSelectedItems( [1] );
			}
			
			
			function load_catalogue ( parms )
			{
				setLoading(true);
				var url = "../DaemonSearchSheet?" + parms.join( '&' );
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
					$("div_sheet").innerHTML = "";
					var elm_row = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
				  	var row_count=elm_row.childNodes.length;
				  	if( row_count== "0" )
				  	{
				  		enactive_catalogue();
						$("div_sheet").innerHTML = "没有找到您要的单据.";
				  	}else{
						show_catalogue();
					}
				}else{	
					$("div_sheet").innerHTML = "";
				  	enactive_catalogue();
				  	var error_hint = xerr.note.split(":");
				  	if( error_hint[1]!= null ) var erro_hi = error_hint[1];
					$("div_sheet").innerHTML = xerr.code + ";" + erro_hi;
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
				
				table.setColumns( [ "sheetid","flag", "venderid", 
				 "shopid","shopname", "saledate","operator", "editor","editdate","checker","checkdate","note" ] );
			
				var columnNames = [ "单号","单据状态", "供应商编码",
					 "门店","门店名称", "销售日期","业务员","制单人","制单日期","审核人","审核时间","备注" ];
				
				var obj = new AW.UI.Grid;
				obj.setId( "grid_cat" );
				obj.setColumnCount( columnNames.length );
				obj.setRowCount( row_count );
				obj.setHeaderText( columnNames );
			
				obj.onCellClicked = function(event, column, row){
					if(column==0){
						var sheetid = obj.getCellValue(0,row);
						open_sheet_detail( sheetid );
					}
				}

				obj.setCellModel(table);
				obj.setFooterText(["","查询结果:"+row_count+"条"]);
				obj.setFooterVisible(true);
				//obj.sort(1,"descending");	// 目录按单据号逆序排列
				
				for(var i=0;i<row_count;i++){
					var f = obj.getCellValue(1,i);
					var msg="";
					if(f==1){
						msg="门店审核";
					}else if(f==99){
						msg="取消";
					}else{
						msg="未知状态:"+f;
					}
					obj.setCellText(msg,1,i);
				}
				
				var sumcount = node_body.getAttribute("count");
				$("div_sheet").innerHTML = "<div>查询结果：总计："+sumcount+"行， 显示："+row_count+"行</div>"+obj.toString();
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
				//显示位置
				offset_current.innerText = "当前第：" + (offset+1) + "单，共有：" + arr_sheetid.length + "单";
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
				var url = "../DaemonViewSheet?sheet=salepick&sheetid=" + sheetid;
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
			 	window.open( "salepick_vender_print.jsp?sheetid="+sheetid, sheetid, attributeOfNewWnd );		
			}
		</script>
</head>
<xml id="island4result" />
<xml id="island4purchase" />
<xml id="format4sheet" src="format4salepick.xsl" />
<body onload="init()">
	<div id="div_tabs"></div>
	<div id="div_cat">
		<div id="div_sheet"></div>
	</div>
	<div id="div_detail" style="display: none;">
		<div id="div_navigator" style="display: none;">
			<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
				type="button" value="下一单" onclick="sheet_navigate(1)" /> <input
				type="button" value="打印单据" onclick="open_win_print()" /> <span
				id="offset_current"></span>
		</div>
		<br />
		<div id="div_sheethead"></div>
	</div>

	<div id="div_search">
		<table cellspacing="1" cellpadding="2" width="70%"
			class="tablecolorborder">
			<tr>
				<td>供应商编码<span style="color: red;">*</span></td>
				<td><input type="text" id="txt_venderid" /></td>
				<td>状态</td>
				<td><select id="txt_status">
						<option>全部</option>
						<option value="0" selected="selected">未阅读</option>
						<option value="1">已阅读</option>
				</select></td>
				<td>门店:</td>
				<td><%=sel_branch%></td>
				<td>单号</td>
				<td><input type="text" id="txt_sheetid" /></td>
			</tr>
			<tr>
				<td></td>
				<td class=altbg2></td>
				<td></td>
				<td></td>
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
	</div>
</body>
</html>
