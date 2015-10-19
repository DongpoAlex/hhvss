<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.SelBook"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid=5010103;
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
<title>邮件查看</title>
<script language="javascript" src="../js/Date.js" type="text/javascript"></script>
<!-- ActiveWidgets stylesheet and scripts -->
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css" />
<!-- grid format -->
<style type="text/css">
.aw-grid-control {
	height: 100%;
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

.aw-column-0 {
	width: 40px;
	cursor: pointer;
}

.aw-column-1 {
	width: 40px;
}

.aw-column-2 {
	width: 30px;
}

.aw-column-3 {
	width: 200px;
}

.aw-column-5 {
	width: 120px;
}

.aw-column-7 {
	width: 50px;
}

.current {
	background-color: #FEFEC0;
	font-weight: bold;
	color: #009900;
}

.content {
	display: none;
	height: 460px;
}

.mail_head {
	background-color: #F0FDD5;
	padding: 6px;
	line-height: 20px;
}

.mail_body {
	padding: 8px;
	background-color: #FFFFFF;
	border: 1px inset #F6F6F6;
	margin: 2px;
	width: 100%;
	height: 200px;
}

.mail_content {
	border: 1px solid #999966;
	margin-top: 4px;
}

.down_file {
	color: #0066CC;
	text-decoration: underline;
	cursor: pointer;
}

.write_mail {
	border: 1px #def solid;
	padding: 10px;
	background-color: #fff;
}

.tool {
	padding: 4px;
}

.space {
	width: 18px;
}

.mail_list {
	height: 200px;
}

.drag_up {
	background-color: #E7E8CA;
	background-image: url(../img/button.gif);
	background-repeat: no-repeat;
	background-position: center 0px;
	height: 9px;
	font-size: 0px;
	cursor: pointer;
}

.drag_down {
	background-color: #E7E8CA;
	background-image: url(../img/button.gif);
	background-repeat: no-repeat;
	background-position: center -9px;
	height: 9px;
	font-size: 0px;
	cursor: pointer;
}
</style>

<script language="javascript" type="text/javascript">
extendDate();
var arr_mid 	= null;
var current_mid = "";
var tag_grp = new AW.UI.Tabs;

function init()
{
	install_tag_sheets();
}

function install_tag_sheets()
{
	tag_grp.setId( "tag_grp" );
	tag_grp.setItemText( [ "查询条件", "邮件目录", "邮件明细" ] );
	tag_grp.setItemCount( 3 );
	tag_grp.onSelectedItemsChanged = function( idx ) {
		if( idx == "0" ) enactive_search() ;
		if( idx == "1" ) enactive_catalogue();
		if( idx == "2" ) {
			load_mail_detail( current_mid );
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
	if( txt_receiptor.value == '' ){
		alert("请输入要查看的邮箱地址");
		return;
	}else{
		parms.push( "receiptor=" + txt_receiptor.value );
		parms.push( "min_sendtime=" + txt_min_sendtime.value );
		parms.push( "max_sendtime=" + txt_max_sendtime.value );
		
	}
	parms.push( "type=" + txt_type.value );
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


function load_catalogue( parms )
{
	setLoading( true );
	parms.push( "operation=browse" );
	var url = "../DaemonMail?" + parms.join( '&' );
	var table = new AW.XML.Table;
	
	table.setURL( url );
	table.request();
	table.response = function(text){
		table.setTable("xdoc/xout/browseMail");
		table.setXML(text);
		analyse_catalogue( table );
		setLoading( false );
	}
}

/**
 *处理返回数据，错误检查，给予相应的提示
 */

function analyse_catalogue( table )
{
	if( table.getErrCode()=='0' ){
		show_catalogue( table );
	}else{
		alert( table.getErrNote() );
	}
}

/**
 *用datagrid显示查到的单据信息
 */
function show_catalogue( table )
{
	var row_count = table.getCount();
	if( row_count== 0 ){
		div_cat.innerHTML = "该用户没有这种类型邮件";
		return;
	}
	arr_mid = new Array();
	for(var i=0; i<row_count; i++){
		arr_mid.push( table.getData(0,i) );
	}
	if( arr_mid !=null && arr_mid.length >0 ) current_mid = arr_mid[0];

	table.setColumns(["aaaa","mailid","type_cn","title","sender","sendtime","fileid","status","readtime","ifback","backid"]);	
	var columnNames = ["查看","邮件ID","类型","邮件标题","发件人","发送时间","附件","状态","查阅时间","回复情况","回复邮件ID"];	
	var obj = new AW.Grid.Extended;
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );		
	var obj_link = new AW.Templates.Link;
	obj_link.setContent("查看");	
	obj_link.setEvent( "onclick",
		function(){
			var current_row = obj.getCurrentRow();
			var mid = obj.getCellValue( 1, current_row );
			open_mail_detail( mid );
		}
	);
	obj.setCellTemplate( obj_link, 0 ); 
	obj.setFooterText(["","","","查询结果:"+row_count+"条"]);
	obj.setFooterVisible(true);
	obj.setCellModel(table);
	div_cat.innerHTML = obj.toString();
}

function open_mail_detail ( mid )
{
	current_mid = mid;
	tag_grp.setSelectedItems( [2] );
}

function searchMailByID(){
	var mid = txt_mailid.value;
	
	if( mid == ''){
		alert("请填写邮件ID");
	}else{
		if(isNaN(parseInt(mid))){
			alert("邮件ID必须是数字");
		}else{
			open_mail_detail ( txt_mailid.value )
		}
	}
}
function sheet_navigate ( step )
{
	var offset = 0;
	if( arr_mid == null || arr_mid.length == 0 ) { alert( "目录是空的!" ); return false; }
	for( var i = 0; i<arr_mid.length; i++ ) {
		if( current_mid == arr_mid[ i ] ) {
			offset = i;
			break;
		}
	}
	offset += step;
	if( offset < 0 ) { alert ( "已经是第一封邮件!" ); return false; }
	if( offset >= arr_mid.length ) { alert ( "已经是最后一封邮件!" ); return false; }
	
	var mid = arr_mid[ offset ];
	open_mail_detail( mid );
}

function load_mail_detail ( mid )
{
	
	if( mid == null || mid.length == 0 ){
		alert( "请先查询数据，再查看明细" );
		return false;
	}	
	
	setLoading( true );
	var url = "../DaemonMail?operation=read&type=0&mailid=" + mid;
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
		table.setXML(text);
		div_content.innerHTML = table.transformNode("format4mail.xsl");
		setLoading( false );
	}
}

//下载文件
function load_file( fid ){
	var url = "../DaemonFileDownload?fileid="+fid;	
	window.location.href = url;
	return ;
}


function exportBackDetail(){
	var receiptor = $("txt_receiptor01").value;
	if( receiptor == "" ) {
		alert("必须填写邮件用户");
		return;
	}
	
	var parms = new Array();
	parms.push("operation=backDetail");
	parms.push("receiptor="+receiptor);
	
	parms.push("noback=yes");
	if($("txt_min_recetime").value != ""){
		parms.push( "min_recetime="+$("txt_min_recetime").value );
	}else{alert("请填写日期范围");return ;}
	if($("txt_max_recetime").value != "")	parms.push( "max_recetime="+$("txt_max_recetime").value )
	
	var url = "../DaemonDownloadMail?"+parms.join("&");

	window.location.href = url ;
	return;
}
function exportBackStatistic(){
	var parms = new Array();
	parms.push("operation=statisticBack");
	if($("txt_min_recetime").value != ""){
		parms.push( "min_recetime="+$("txt_min_recetime").value );
	}else{alert("请填写日期范围");return ;}
	if($("txt_max_recetime").value != "")	parms.push( "max_recetime="+$("txt_max_recetime").value )
	
	var url = "../DaemonDownloadMail?"+parms.join("&");

	window.location.href = url ;
	return;
}

function exportSendCount(){
	var parms = new Array();
	parms.push("operation=sendCount");
	if($("txt_min_recetime").value != ""){
		parms.push( "min_recetime="+$("txt_min_recetime").value )
	}else{alert("请填写日期范围");return ;}
	if($("txt_max_recetime").value != "")	parms.push( "max_recetime="+$("txt_max_recetime").value )
	var url = "../DaemonDownloadMail?"+parms.join("&");

	window.location.href = url ;
	return;
}

</script>
</head>
<body onload="init()">
	<div id="div_tabs"></div>
	<div id="div_search">
		<table cellspacing="1" cellpadding="2" class="tablecolorborder">
			<tr>
				<td><label> 邮件用户: </label></td>
				<td class="altbg2"><input id="txt_receiptor" type="text"
					value="" /></td>
				<td class="altbg2"><select id="txt_type">
						<option value="101">收件箱</option>
						<option value="102">发件箱</option>
						<option value="103">草稿箱</option>
						<option value="104">回收站</option>
						<option value="105">已删除</option>
						<option value="201">所有接收的邮件</option>
						<option value="202">所有发送的邮件</option>
				</select></td>
			</tr>
			<tr>
				<td>发送时间段：</td>
				<td><input type="text" size="10" onblur="checkDate(this)"
					id="txt_min_sendtime" /> — <input type="text" size="10"
					onblur="checkDate(this)" id="txt_max_sendtime" /></td>
				<td></td>
			</tr>
			<tr class="singleborder">
				<td colspan="3"></td>
			</tr>
			<tr class="whiteborder">
				<td colspan="3"></td>
			</tr>
			<tr class="header">
				<td><script type="text/javascript">
							var btn_search = new AW.UI.Button;
							btn_search.setControlText( "查询" );
							btn_search.setControlImage( "search" );	
							btn_search.onClick = search_catalogue;
							document.write( btn_search );
						</script></td>
				<td colspan="2" class="altbg2"></td>
			</tr>
		</table>
		<br />

		<div class="tablecolorborder" style="width: 40%; padding: 6px;">
			<h4>按邮件ID直接查看邮件内容</h4>
			邮件ID：<input id="txt_mailid" type="text" value="" size="8" />

			<script type="text/javascript">
					var btn_search2 = new AW.UI.Button;
					btn_search2.setControlText( "查看" );
					btn_search2.setControlImage( "search" );	
					btn_search2.onClick = searchMailByID;
					document.write( btn_search2 );
				</script>
		</div>
		<br />
		<div class="tablecolorborder" style="width: 40%; padding: 6px;">
			<h4>邮件默认对帐员管理</h4>
			<a href="./environment_upload.jsp">管理用户默认发件人</a>
		</div>
		<br />
		<div class="tablecolorborder" style="width: 40%; padding: 6px;">
			<h4>邮件回复情况管理</h4>
			邮件用户：<input type="text" id="txt_receiptor01" size="10" /><br /> 时间段
			：<input type="text" size="10" onblur="checkDate(this)"
				id="txt_min_recetime" /> — <input type="text" size="10"
				onblur="checkDate(this)" id="txt_max_recetime" />
			<p />
			<a href="javascript:exportBackDetail()">导出未回复邮件列表</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="javascript:exportBackStatistic()">导出邮件回复统计</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="javascript:exportSendCount()">导出邮件发送数统计</a>
		</div>
	</div>
	<div id="div_cat" style="display: none;">...</div>
	<div id="div_detail" style="display: none;">
		<br />
		<div id="div_navigator">
			<input type="button" value="上一封" onclick="sheet_navigate(-1)" /> <input
				type="button" value="下一封" onclick="sheet_navigate(1)" />
		</div>
		<div id="div_content"></div>
	</div>
</body>
</html>
