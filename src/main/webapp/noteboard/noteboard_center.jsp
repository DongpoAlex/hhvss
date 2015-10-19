<%@ page contentType="text/html; charset=UTF-8" language="java"
	session="false" import="java.sql.*" import="java.util.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.vss.noteboard.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 5020101;
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

<link rel="stylesheet" href="../css/style.css" type="text/css" />

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>

<xml id="island_catalogue" />
<xml id="islandtmp" />
<xml id="island4body" />
<xml id="island4del_result" />
<xml id="format_note" src="noteboard.xsl" />

<style>
.aw-grid-control {
	width: 100%;
	height: 40%;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

#myGrid {
	background-color: #F9F8F4
}

#myGrid .aw-column-0 {
	width: 50px;
	cursor: pointer;
}

#myGrid .aw-column-1 {
	width: 330px;
	cursor: pointer;
	color: blue;
}

#myGrid .aw-column-2 {
	width: 60px;
}

#myGrid .aw-column-6 {
	cursor: pointer;
	color: blue;
}

#myGrid .aw-column-7 {
	cursor: pointer;
	width: 60px;
}

#myGrid .aw-column-8 {
	cursor: pointer;
	color: red;
}

#myGrid .aw-column-9 {
	cursor: pointer;
}

.textarea_class {
	width: 100%;
	font: menu;
	height: 350px;
}
</style>

<script language="javascript">
function init()
{
	download_catalogue();
}
</script>

<script language="javascript">
function download_catalogue()
{
	island_catalogue.async = false;
	var params = "&dept="+encodeURIComponent($F("txt_dept"))+"&sender="+encodeURIComponent($F("search_sender"))+"&min_senderdate="+$F("search_minsenderdate")+"&max_senderdate="+$F("search_maxsenderdate");
	island_catalogue.load( "../DaemonBoardManager?operation=catalogue"+params );
	check_err();
	div_detail.innerHTML="";
}

function check_err()
{
	var elm_err 	= island_catalogue.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" ) 
	{    	 
		var elm_row 	= island_catalogue.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	  	var row_count   = elm_row.childNodes.length;
	  	if( row_count== 0 )  div_catalogue.innerHTML = "目前尚无有效公文.";
	  	else   display_note();
	}
	else div_catalogue.innerHTML = "目前尚无有效公文.";	
}

function display_note()
{
	var table     = new AW.XML.Table;
	var node_list = island_catalogue.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	var row_count = node_list.childNodes.length;
	islandtmp.loadXML( table.getXMLContent(node_list) );
	var xml, node = document.getElementById( "islandtmp" );
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument( "", "", null);
		xml.appendChild(node.selectSingleNode("*"));
	}
	var grid_obj  = new AW.UI.Grid;		
	table.setXML( xml );
	table.setColumns(["noteid","title","dept","editor","editdate","expiredate","call_number","","",""]);	
	var columnNames = [ "公文号", "标题", "部门","发布者", "发布日期", "截止日期" ,"点击查看列表", "导出", "删除","" ];	
	grid_obj.setId( "myGrid" );
	grid_obj.setColumnCount( columnNames.length );
	grid_obj.setRowCount( row_count );	
	grid_obj.setHeaderText( columnNames );
	var str = new AW.Formats.String;	
	var number = new AW.Formats.Number;
	
	grid_obj.setCellFormat( [ number ] );
	
	grid_obj.onCellClicked = function(event, column, row){
		var noteid = grid_obj.getCellValue(0,row);
		switch ( Number(column) ) {
			case 0:
				display_detail( noteid );
				break;
			case 1:
				display_detail( noteid );
				break;
			case 6:
				var vender_num = grid_obj.getCellText( 6, row );
				if( vender_num != 0 ) openVenderWnd( noteid );
				break;
			case 7:
				exportExcel( noteid );
				break;
			case 8:
				var str ="您确定要删除第 " + noteid +" 号公文么？";
				if (confirm(str)){
					delete_note( noteid );
				}
				break;
			case 9:
				edit( noteid );
				break;		
			default:
				break;
		}
	};


	grid_obj.setCellModel(table);

	for( var i=0; i<row_count; i++ ){
		var n = grid_obj.getCellText(6,i);
		var str = "共有 "+n+" 人已阅";
		grid_obj.setCellText(str,6,i);
		grid_obj.setCellText("导出",7,i);
		grid_obj.setCellText("删除",8,i);
		//grid_obj.setCellText("编辑",9,i);
	}

	div_catalogue.innerHTML = grid_obj.toString();
}

/**
 * 
 */
function exportExcel( noteid ){
	window.location.href = "../DaemonBoardManager?operation=down&noteid=" + noteid;
} 
function display_detail( noteid )
{
	island4body.async = false;
	island4body.load( "../DaemonBoardManager?operation=view&noteid=" + noteid  );
	var content = island4body.XMLDocument.selectSingleNode( "/xdoc/xout/notedetail/content" ).text;
	div_detail.innerHTML = island4body.transformNode( format_note.documentElement )+content;
	div_detail.style.display = 'block';
}
function edit(noteid){
	alert("开发中……");
}
</script>

<script language="javascript">
function openVenderWnd( noteid )
{	
	window.location.href = "noteboard_log.jsp?noteid=" + noteid;		
}
</script>

<script language="javascript">
function delete_note( noteid )
{	
	var parms = new Array();
	parms.push( "moduleid=<%=moduleid%>" );
	parms.push( "operation=delete" );
	parms.push( "noteid=" + noteid );
	parms.push( "timstamp=" + new Date() );			
	var url  = "../DaemonBoardManager?" + parms.join( '&' );	
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_delete;
	courier.call();	
}

function analyse_delete( text )
{
	island4del_result.loadXML( text );
	var elm_err 	= island4del_result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" )   	alert( "删除成功" );
	else 				alert( "删除失败"+xerr.note );

	window.location.reload();
}
</script>

<script language="javascript">
function download_file( fileid )
{
	window.location.href = "../DaemonBoardManager?operation=download&fileid=" + fileid;
}

function redirect_add()
{
	window.location.href = "noteboard_add_01.jsp";
}
</script>

</head>

<body onload="init()">
	<div>
		&nbsp;&nbsp;&nbsp;&nbsp; <a href="noteboard_add_01.jsp"></a> <input
			type="button" value="发布传新的公文" onclick="redirect_add()" />
		&nbsp;&nbsp;&nbsp;&nbsp; 部门:<select name="dept" id="txt_dept">
			<option value="">默认</option>
			<option value="结算">结算</option>
			<option value="采购">采购</option>
			<option value="营运">营运</option>
			<option value="物流">物流</option>
		</select> 发布人:<input type="text" id="search_sender" /> 发布日期:<input type="text"
			id="search_minsenderdate" onfocus="WdatePicker({skin:'whyGreen'})"
			class="Wdate" /> - <input type="text" id="search_maxsenderdate"
			onfocus="WdatePicker({skin:'whyGreen'})" class="Wdate" /> <input
			type="button" value="查询" onclick="download_catalogue()" /> <br></br>
		<div id="div_catalogue">正在查询, 请等候 . . .</div>
		<div id="div_detail" style="display: none; overflow: auto;"></div>
	</div>

</body>

</html>
