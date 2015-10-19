<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3010112;
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
%>
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>订货审批单-零售商专用</title>
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
<style>
#tag_grp {
	margin-bottom: 10px;
}

.div_navigator {
	margin-bottom: 10px;
}

.aw-grid-control {
	height: 400px;
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

#grid_cat {
	background-color: #F9F8F4
}

#grid_cat.aw-column-0 {
	width: 120 px;
}

#grid_cat .aw-column-11 {
	width: 246 px;
}

#grid_cat .aw-column-6 {
	width: 160 px;
}

#grid_sheet {
	background-color: #F9F8F4
}

#grid_print .aw-column-0 {
	width: 60 px;
}

#grid_print .aw-column-1 {
	width: 120 px;
}

#grid_print .aw-column-2 {
	width: 120 px;
}

#grid_print .aw-column-3 {
	width: 80 px;
}
</style>
<script language="javascript">
var g_logistics=0;
var g_orderid = new Array();
var arr_sheetid 		= null;
var current_sheetid 	= null;
var row_selected		= null;
var print_selected		= null;

var btn_search = new AW.UI.Button;
btn_search.setControlText( "查询" );
btn_search.setId( "btn_search" );
btn_search.setControlImage( "search" );
btn_search.onClick = search_by_parms;

var btn_sheetid = new AW.UI.Button;
btn_sheetid.setControlText( "查询" );
btn_sheetid.setId( "btn_sheetid" );
btn_sheetid.setControlImage( "search" );
btn_sheetid.onClick = search_by_sheetid;

var tag_grp = new AW.UI.Tabs;
var table4search = new AW.XML.Table;
var table4detail = new AW.XML.Table;
var table4confirm = new AW.XML.Table;
var table4shopprint = new AW.XML.Table;

function init(){
	install_tag();
	tag_grp.setSelectedItems( [0] );
}
function init_data(){
	arr_sheetid = null;
	current_sheetid 	= null;
	row_selected		= null;
	print_selected		= null;
	div_cat.innerHTML = "";
	div_sheethead.innerHTML = "";
	div_confirm4all.innerHTML = "";
	div_shop_catalotue.innerHTML = "";
}
//装载卡片
function install_tag()
{
	tag_grp.setId( "tag_grp" );
	tag_grp.setItemText( [ "查询","单据目录", "单据明细","" ] );
	tag_grp.setItemCount( 4 );
	tag_grp.onSelectedItemsChanged = change_tag;
	div_tabs.innerHTML = tag_grp.toString();
}
//卡片切换事件
function change_tag( idx ){
	var div_id =[ "div_search","div_cat", "div_detail", "div_printbyshop" ];
	for (var i=0; i<div_id.length; i++){
		if (idx == i ) 
			$(div_id[i]).style.display = "block";
		else
			$(div_id[i]).style.display = "none";
	}
	//调用的方法
	switch ( Number(idx) ) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			load_purchase();
			break;
		case 3:
			show_shop_catalotue();
			break;
		default:
	}
}

function search_by_sheetid(){
	var parms = new Array();
	
	var str = txt_sheetid.value;
	str = str.replace( / +/g, ',' );
	str = str.replace( /,+/g, ',' );
	if( str == 0 ) { alert( "未输入单据号!" ); return; }
	var arr_id = str.split ( ',' );
	
	var parms = new Array();
	for( var i=0; i<arr_id.length; i++ ) {
		var s = arr_id[ i ];
		if( s != '' ) parms.push( "sheetid=" + s );
	}
	
	search_sheet( parms );
}
//按条件过滤查询
function search_by_parms(){
	if( txt_sheetid.value  == '' ) {
		if( txt_editdate_min.value  == '' ){
			alert("起始制单日期必须填写！");
			return;
		}
	}
	var parms = cook_search_prams();
	search_sheet( parms );
}

function search_sheet( parms ){
	parms.push( "sheetname=ordersheet" );
	var url = "../DaemonSearchBakSheet?" + parms.join( '&' );
	table4search.setURL( url );
	table4search.request();
	table4search.response 	= analyse_search;
	
	setLoading(true);
	tag_grp.setSelectedItems( [1] );
	init_data();
}
//拼url
function cook_search_prams(){
	var parms = new Array();
	if( txt_venderid.value   != '' )       	parms.push( "venderid="   + txt_venderid.value 	);
	if( txt_shopid.value   != '' )       	parms.push( "shopid="   + txt_shopid.value 	);
	if( txt_editdate_min.value  != '' )   	parms.push( "editdate_min="   + txt_editdate_min.value 	);
	if( txt_editdate_max.value  != '' )   	parms.push( "editdate_max="   + txt_editdate_max.value 	);
	if( txt_checkdate_min.value  != '' )  	parms.push( "checkdate_min="  + txt_checkdate_min.value 	);
	if( txt_checkdate_max.value  != '' )  	parms.push( "checkdate_max="  + txt_checkdate_max.value 	);
	if( txt_deadline_min.value  != '' )   	parms.push( "deadline_min="   + txt_deadline_min.value 	);
	if( txt_deadline_max.value  != '' )   	parms.push( "deadline_max="   + txt_deadline_max.value 	);
	if( txt_release_min.value  != '' )    	parms.push( "releasedate_min="    + txt_release_min.value 	);
	if( txt_release_max.value  != '' )    	parms.push( "releasedate_max="    + txt_release_max.value 	);
	if( order_status.value  != '' )    	parms.push( "status="    + order_status.value 	);
	if( txt_sheetid.value  != '' )    	parms.push( "sheetid="    + txt_sheetid.value 	);
	return parms;
}
//处理查询结果
function analyse_search( text ){
	setLoading(false);
	table4search.setTable("xdoc/xout/rowset");
	table4search.setXML( text );
	if( table4search.getErrCode() != 0 ){	//处理xml中的错误消息
		div_cat.innerHTML = table4search.getErrNote() ;
		return;
	}
	
	//输出到页面
	div_cat.innerHTML = result_grid();
	
	//记录所有单据号到数组
	arr_sheetid = new Array();
	var node_sheetid 	= table4search.getXML().selectNodes( "/xdoc/xout/rowset/row/sheetid" );
	var next_node = node_sheetid.nextNode();
	while( next_node != null ) {
		arr_sheetid[ arr_sheetid.length ] = next_node.text;
		next_node = node_sheetid.nextNode();
	}
	if( arr_sheetid !=null && arr_sheetid.length >0 ) current_sheetid = arr_sheetid[0];
}
//将查询结果转换为grid
function result_grid(){

	table4search.setColumns([ "sheetid", "status", "purchasetype", "validdays", 
	"orderdate", "venderid", "vendername", "majorname", 
	"paytypename", "deadline", "logistics", "note", 
	"editor", "editdate", "checker", "checkdate", "readtime" ] );
	var columnNames =  [ "订货审批单号", "状态", "补货标识", "有效期（天）", "订货日期", "供应商编码", "供应商名称", "课类", "结算方式", 
		"截止日期", "物流模式", "备注",  
		"制单人", "制单日期", "审核人", "审核日期","供应商阅读时间" ];
	
	var grid = new AW.UI.Grid;
	var row_count = table4search.getCount();
	grid.setId( "grid_cat" );
	grid.setColumnCount( columnNames.length );
	grid.setRowCount( row_count );	
	grid.setHeaderText( columnNames );
	
	grid.setSelectorVisible(true);
	grid.setSelectorWidth(30);
	grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
	
	var grid_link = new AW.Templates.Link;
	grid_link.setEvent( "onclick",
		function(){
			var sheetid = grid.getCellValue( 0, grid.getCurrentRow() );
			open_sheet_detail( sheetid );
		}
	);	
	grid.setCellTemplate( grid_link, 0 ); 	
	grid.setCellModel(table4search);
	var sumcount = table4search.getXMLNode( "/xdoc/xout/rowset" ).getAttribute("row_total");
	return "查询结果：总计："+sumcount+"行， 显示："+row_count+"行"+grid.toString();
}

//显示单据明细
function open_sheet_detail ( sheetid )
{
	current_sheetid = sheetid;
	tag_grp.setSelectedItems( [2] );
}

//加载明细
function load_purchase(){
	if( current_sheetid == null || current_sheetid.length == 0 ) return ;
	var url = "../DaemonViewBakSheet?sheet=ordersheet&sheetid=" + current_sheetid;
	
	table4detail.setURL( url );
	table4detail.request();
	table4detail.response = function( text ){
		table4detail.setXML(text);
		div_sheethead.innerHTML = table4detail.getXML().transformNode( format4head.documentElement );
		setLoading(false);
		//记录送货模式和订货通知单号
		g_logistics = table4detail.getXMLText( "xdoc/xout/sheet/head/row/logistics" );
		var node_row 	= table4detail.getXML().selectNodes( "xdoc/xout/sheet/body/row" );
		var next_node = node_row.nextNode();
		g_orderid = new Array();
		while( next_node != null ) {
			g_orderid.push(next_node.selectSingleNode("sheetid").text);
			next_node = node_row.nextNode();
		}
	};
	setLoading(true);
}

// 浏览单据
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
	//显示位置
	offset_current.innerText = "当前第：" + (offset+1) + "单，共有：" + arr_sheetid.length + "单";	
	var sheetid = arr_sheetid[ offset ];
	open_sheet_detail( sheetid );
}

// 门店打印时浏览单据
function shop_sheet_navigate ( step )
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
	shop_offset_current.innerText = "当前第：" + (offset+1) + "单，共有：" + arr_sheetid.length + "单";	
	current_sheetid = arr_sheetid[ offset ];
	show_shop_catalotue();
}


//打开一个新窗口,显示打印视图
function open_win_print()
{		
		//直送订单直接打开订货通知单打印界面
	if(g_logistics==1){
		var url = "bakpurchase_print.jsp?sheetid=" + g_orderid.join(",");
		var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
	 	window.open( url, "batch_print",attributeOfNewWnd);	
	}else{
		var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
			",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
	
	 	var sheetid = current_sheetid;
	 	window.open( "bakorder_print.jsp?sheetid="+sheetid, sheetid,attributeOfNewWnd);		
	}	
}

//批量处理
function show_confirm_catalotue()
{
	var row_count;
	row_count = table4search.getCount();
	if( row_count == 0 ) return;
	
	row_selected = new Array();
	
	table4search.setColumns([ "", "sheetid", "status", "validdays", "orderdate", "majorname", "logistics", "note", "releasetime" ] );
	var columnNames = [ "", "订货审批单号", "状态", "有效期（天）", "订货日期", 
		"课类", "物流模式", "备注", "上传时间" ];

	var grid = new AW.UI.Grid;
	grid.setId( "grid_con" );
	grid.setColumnCount( columnNames.length );
	grid.setRowCount( row_count );
	grid.setHeaderText( columnNames );
	grid.setSelectionMode("multi-row-marker");
	grid.onSelectedRowsChanged = function(arrayOfRowIndices){
       	row_selected = arrayOfRowIndices;
		return true;
    };
	grid.setCellModel(table4search);
	grid.sort(1,"descending");		// 目录按单据号逆序排列
	div_confirm4all.innerHTML = "显示："+row_count+"条"+grid.toString();
}


//多份单据打印
function print_more(){
	var array_selected = new Array();
	for(var i=0;i<row_selected.length;i++)
	{
		array_selected.push( arr_sheetid[row_selected[i]] );
	}
	if( array_selected.length == 0 ){
		alert( "请选择要打印的单据!" );
		return;
	}

	var url = "bakorder_print.jsp?sheetid=" + array_selected.join(",");
	
	var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
	",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
 	window.open( url, "batch_print",attributeOfNewWnd);	
}

//显示按门店打印
function show_shop_catalotue()
{
	if( current_sheetid == null ){	return;	}
	var parms = new Array();
	parms.push( "refsheetid="+current_sheetid );
	parms.push( "sheetname=purchase" );
	var url = "../DaemonSearchBakSheet?" + parms.join( '&' );
	
	table4shopprint.setURL( url );
	table4shopprint.response = shopprint_grid;
	table4shopprint.request();
	
	setLoading(true);
}

function shopprint_grid( text ){
	setLoading(false);
	table4shopprint.setTable("xdoc/xout/rowset");
	table4shopprint.setXML( text );
	if( table4search.getErrCode() != 0 ){	//处理xml中的错误消息
		div_shop_catalotue.innerHTML = table4search.getErrNote() ;
		return;
	}
	
	var row_count;
	row_count = table4shopprint.getCount();
	if( row_count == 0 ) return;
	
	print_selected = new Array();
	
	table4shopprint.setColumns( [ "index","refsheetid","sheetid", "shopid", "shopname", 
				 "logisticsname", "orderdate", "deadline", "editor", "checker" ] );
			
	var columnNames = [ "","订货审批单号","订货通知单号", "门店", "门店名称",
					 "送货方式", "订货日期", "截止日期", "编辑人", "审核人" ];

	var grid = new AW.UI.Grid;
	grid.setId( "grid_print" );
	grid.setColumnCount( columnNames.length );
	grid.setRowCount( row_count );
	grid.setHeaderText( columnNames );
	grid.setSelectionMode("multi-row-marker");
	grid.onSelectedRowsChanged = function(arrayOfRowIndices){
       	print_selected = arrayOfRowIndices;
		return true;
    };
    	
	grid.setCellModel(table4shopprint);
	//grid.sort(1,"descending");		// 目录按单据号逆序排列
	grid.setFooterText(["","查询结果:"+row_count+"条"]);
	grid.setFooterVisible(true);
	div_shop_catalotue.innerHTML = grid.toString();
}

//多份订单据打印
function print_all(){
	if( print_selected == null || print_selected.length == 0 ) {
		alert( "请选择要打印的单据!" );
		return;
	}
	
	var array_selected = new Array();
	for(var i=0;i<print_selected.length;i++)
	{
		array_selected.push( table4shopprint.getData(2,print_selected[i]) );
	}
	if( array_selected.length == 0 ){
		alert( "请选择要打印的单据!" );
		return;
	}

	var url = "bakpurchase_print.jsp?sheetid=" + array_selected.join(",");
	
	var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
	",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
 	window.open( url, "batch_print",attributeOfNewWnd);	
}
function downloadsheet(){
	var url="../DaemonDownloadExcel?sheetid="+current_sheetid+"&operation=order_baksheet";
	window.location.href = url;
}
</script>
<xml id="format4head"
	src="<%=token.site.toXSLPatch("format4purchasechk.xsl")%>" />
</head>
<body onload="init()">
	<div id="div_tabs" style="width: 100%;"></div>
	<div id="div001">
		<div id="div_cat" style="display: block;">...</div>
		<div id="div_detail" style="display: none;">
			<div class="div_navigator">
				<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
					type="button" value="下一单" onclick="sheet_navigate(1)" /> <input
					type="button" value="打印订单" onclick="open_win_print()" /> <input
					type="button" value="按门店打印订单"
					onclick="tag_grp.setSelectedItems( [3] );show_shop_catalotue()" />
				<input type="button" value="导出订单" onclick="downloadsheet()" /> <span
					id="offset_current"></span>
			</div>
			<div id="div_sheethead"></div>
		</div>
		<div id="div_printbyshop">
			<div class="div_navigator">
				<input type="button" value="上一单" onclick="shop_sheet_navigate(-1)" />
				<input type="button" value="下一单" onclick="shop_sheet_navigate(1)" />
				<input type="button" value="打印选中订单" onclick="print_all()" /> <span
					id="shop_offset_current"></span>
			</div>
			<div id="div_shop_catalotue"></div>
		</div>
		<div id="div_search">
			<table cellspacing="1" cellpadding="2" width="70%"
				class="tablecolorborder">
				<tr>
					<td><label> 供应商编码: </label></td>
					<td><input type="text" id="txt_venderid" size="10" /></td>
					<td><label>订货审批单号: </label></td>
					<td><input type="text" id="txt_sheetid" size="20"></input></td>
				</tr>
				<tr>
					<td><label> 制单日期: </label></td>
					<td class=altbg2><input type="text" id="txt_editdate_min"
						onblur="checkDate(this)" size="12" /> - <input type="text"
						id="txt_editdate_max" onblur="checkDate(this)" size="12" /></td>
					<td><label> 审核日期: </label></td>
					<td class=altbg2><input type="text" id="txt_checkdate_min"
						onblur="checkDate(this)" size="12" /> - <input type="text"
						id="txt_checkdate_max" onblur="checkDate(this)" size="12" /></td>
				</tr>
				<tr>
					<td><label> 截止日期: </label></td>
					<td class=altbg2><input type="text" id="txt_deadline_min"
						onblur="checkDate(this)" size="12" /> - <input type="text"
						id="txt_deadline_max" onblur="checkDate(this)" size="12" /></td>
					<td><label> 上传日期: </label></td>
					<td class=altbg2><input type="text" id="txt_release_min"
						onblur="checkDate(this)" size="12" /> - <input type="text"
						id="txt_release_max" onblur="checkDate(this)" size="12" /></td>
				</tr>
				<tr>
					<td><label> 收货地: </label></td>
					<td class=altbg2><%=sel_branch%></td>
					<td><label> 订单状态: </label></td>
					<td class=altbg2><select id="order_status">
							<option>全部</option>
							<option value="0">未阅读</option>
							<option value="1">已阅读</option>
							<option value="10">已确认</option>
							<option value="100">已执行</option>
					</select></td>
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
			</script></td>
					<td></td>
					<td colspan="4"></td>
				</tr>
			</table>
			<br />
		</div>
		<!-- end of div_search -->
	</div>
	<!-- end of div001 -->
</body>
</html>
