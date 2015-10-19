<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3010131;
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
	token.checkPermission(moduleid,Permission.READ);
%>
<%
	String g_status = request.getParameter("g_status");
	if (g_status == null)
		g_status = "-1";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>退货通知单查询</title>
<link rel="stylesheet" href="../css/style.css" type="text/css" />

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"></script>

<style type="text/css">
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
	display: none;
}
</style>

<style>
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
		var clazz = "Retnotice";
		var arr_sheetid 	= null;
		var current_sheetid 	= "";
		var g_status = <%=g_status%>;
		var format_show_xsl = "<%=token.site.toXSLPatch("retnotice.xsl") %>";
		
		var tag_grp 		= new AW.UI.Tabs;
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

		function check(){
			var rel = autoCheck();
			return rel;
		}

		function cookParms(){
			var parms = autoParms();
			return parms;
		}
		
function search_sheet()
{
	if(!check()){return;}
	var url = "../DaemonSheet?operation=search&clazz="+clazz+"&" + cookParms().join( '&' );
	table4search.setURL( url );
	table4search.request();
	table4search.response 	= analyse_search;
	tag_grp.setSelectedItems( [1] );
	setLoading( true );
	$("div1").innerHTML="正在查询，请稍后……";
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
	table4search.setColumns( [ "sheetid", "statusname", "rettype","shopid", "shopname","askshopname", 
	 "retdate", "operator", "editor", "editdate", "checker", "checkdate" ] );

	var columnNames = [ "单据号", "状态", "退货类型","门店", "门店名称","申请地",
		 "退货日期", "业务员", "编辑人", "编辑日期", "审核人", "审核日期" ];
	
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
	var url = "../DaemonSheet?operation=show&clazz="+clazz+"&sheetid=" + sheetid;
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
		catchWarning();
	};
	setLoading(true);
}

function catchWarning(){
	$("div_warning").innerHTML = "";
	$("div_warning").style.display="none";
	//根据单据的不同时间，和状态显示相应的预警信息
	var warnstatus = table4detail.getXMLText("xdoc/xout/sheet/head/row/warnstatus");
	var releasedate = table4detail.getXMLText("xdoc/xout/sheet/head/row/releasedate");
	if( Number(warnstatus) != 100 && Number(warnstatus) != 0 ){
		var warn3  = "根据合同约定，贵司需在退货通知单发出之后的7天内完成退货，逾期未退，需向商业有限公司交纳逾期储位占用费！";
		var warn14 = "根据合同约定，贵司需在退货通知单发出之后的21天内完成退货,逾期未退，则视为贵司自动放弃本单所列商品及数量的所有权和处置权并承担相应法律责任！";
		if( Number(warnstatus)==90 ){//退货通知单发出第3天到第14天
			$("div_warning").innerHTML = "<span class='remind'>提醒：请尽快执行！发布日期："+releasedate+"</span><br/>"+warn3;
			$("div_warning").display="block";
		}else if( Number(warnstatus)==91 ){//退货通知单发出第14天
			$("div_warning").innerHTML = "<span class='warn'>警告：您的退货通知已过期！发布日期："+releasedate+"</span><br/>"+warn14;
			$("div_warning").style.display="block";
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
	var url = "../DaemonSheet?operation=doRead&clazz="+clazz+"&sheetid=" + sheetid;
	table4confirm.setURL( url );
	table4confirm.request();
}

function confirm_already( sheetid )
{
	var url = "../DaemonSheet?operation=doConfirm&clazz="+clazz+"&sheetid=" + sheetid;
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
 	window.open( "print.jsp?sheetid="+current_sheetid, current_sheetid, attributeOfNewWnd );		
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
		<% if(!token.isVender){ %>
		<div class="search_parms">
			供应商编码: <input type="text" id="txt_venderid" name="txt_parms"
				notnull="notnull" alt="供应商编码" />
		</div>
		<% } %>
		<div class="search_parms">
			退货地:<input type="text" id="txt_shopid" size="12" name="txt_parms"
				split="," /><a href="javascript:showShopList('txt_shopid')">选择门店</a>
		</div>

		<div class="search_parms">
			订单状态:<select id="txt_status" name="txt_parms">
				<option>全部</option>
				<option value="0" selected="selected">未阅读</option>
				<option value="1">已阅读</option>
				<option value="10">已确认</option>
				<option value="90">预警</option>
				<option value="91">过期</option>
				<option value="100">已执行</option>
			</select>
		</div>
		<div class="search_parms">
			退货日期: <input type="text" id="txt_retdate_min" class="Wdate"
				onFocus="WdatePicker()" name="txt_parms" alt="最小日期" /> - <input
				type="text" id="txt_retdate_max" class="Wdate"
				onFocus="WdatePicker()" name="txt_parms" alt="最大日期" />
		</div>
		<div class="search_parms">
			单据号: <input type="text" id="txt_sheetid" size="20" name="txt_parms" />
		</div>
		<div class="search_button" id="div_button_search"></div>
	</div>
	&nbsp;
</body>
</html>
