<%@page contentType="text/html;charset=utf-8" session="false"
	errorPage="../errorpage/errorpage.jsp" import="java.lang.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"%>

<%

	final int moduleid=5030102;
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

<?xml version="1.0" encoding="utf-8"?>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>群发消息列表</title>
<script src="../js/ajax.js"> </script>
<script src="../js/XErr.js"> </script>

<!-- ActiveWidgets stylesheet and scripts -->
<script src="../AW/runtime/lib/aw.js"></script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<style>
.divmsg {
	background-color: #EEFFF3;
	color: #006600;
	padding: 5px;
	border: 1px solid #006600;
	width: 200px;
	display: none;
	position: absolute;
	z-index: auto;
	background-image: url(../img/loading.gif);
	background-repeat: no-repeat;
	background-position: 2px 6px;
	padding-left: 26px;
}

.aw-grid-control {
	height: 50%;
	width: 100%;
	font: menu;
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
	text-align: center;
	width: 65px;
	color: #060;
	cursor: hand;
}

#myGrid .aw-column-1 {
	text-align: left;
	width: 200px;
}

#myGrid .aw-column-2 {
	text-align: center;
}

#myGrid .aw-column-3 {
	text-align: center;
}

#myGrid .aw-column-4 {
	text-align: center;
}

#myGrid .aw-column-5 {
	text-align: center;
}

#myGrid .aw-column-6 {
	width: 0px
}

#myGrid .aw-column-7 {
	width: 0px
}

#divTitle {
	font: bold 18px "楷体_GB2312";
	text-align: center;
	margin: 6px;
}

body,td,th {
	font-size: 12px;
}
</style>
<script >
var grid = new AW.UI.Grid;
var table = new AW.XML.Table;
function $(id){
	return document.getElementById(id);
}

function setLoading( on ,text ){
	if(on)
		divloading.style.display = "block";
	else
		divloading.style.display = "none";
	divloading.innerHTML = "数据读取中,请稍候……";
	divloading.style.left = (window.screen.availWidth) / 2 -200;
	divloading.style.top = (window.screen.availHeight) / 2 -200;
	if( text != null ){
		divloading.innerHTML = text +",请等候……";
	}
}

function init(){
	setLoading( true ,"正在下载目录" );
	var parms = new Array();
		parms.push( "operation=msglist" );
	var url = "../DaemonVenderMsg?" + parms.join( "&" );
	var courier 			= new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= showGrid;
	courier.call();
	
}

function showGrid( text ){
	setLoading(false);
	island4result.loadXML( "" );
	island4result.loadXML( text );
		
	var elm_err = island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr = parseXErr( elm_err );
	if(xerr.code != "0" ){
		$("divResult").innerHTML = "错误:"+xerr.note;
		return false;
	}
	else {
		var rel_grid = getGrid(text);
		if ( rel_grid != undefined ){
			$("divResult").innerHTML = rel_grid;
		}else{
			$("divResult").innerHTML = "服务器忙，请刷新重试";
		}
			
	}
}
function getGrid(text){
	
	//截取数据
	var elm_body = island4result.XMLDocument.selectSingleNode( "/xdoc/catalogue" );
	//取得elm_body中的行数
	var rows = Number(elm_body.childNodes.length);
	if(rows == 0){
		return "没有消息";
	}
	
	table.setXML(table.getXMLContent(elm_body));
	table.setColumns(["","title","editor","editdate","expiredate", "msgid", "content" ]);
	
	//grid
	grid.setId("myGrid");
	grid.setColumnCount(6);
	grid.setHeaderText([ "查看","消息标题","消息发送人","发送日期","有效日期", "消息编号" ]);
	grid.setRowCount(rows);
	grid.setSelectorVisible(true);
	grid.setSelectorWidth(30);
	grid.setSelectorText(function(i){return this.getRowPosition(i)+1});
	grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	grid.setCellText("查看",0);
	grid.onCellClicked = function(event, column, row){
		var msgid = grid.getCellValue(5,row);
	
		$("divContent").innerHTML = "<input type='button' id='btn_down' value='下载' onclick='exportXLS(\""+msgid+"\")'/>"+
		"<br/> 消息说明:<textarea rows='5' cols='100'>"+
		grid.getCellValue(6,row)
		+"</textarea>"

	};
	grid.sort(3,"descending"); //按editdate降序
	return grid;
}

function exportXLS(msgid){
	var url = "../DaemonVenderMsg?operation=downexcel4vender&msgid="+msgid;
	alert(url);
	window.location.href = url;
}
</script>

<xml id="island4req"> <xdoc> <xout> </xout> </xdoc> </xml>
<xml id="island4result" />

</head>
<body onload="init()">
	<div id="divloading" class="divmsg"></div>
	<div id="divTitle">群发消息目录</div>
	<hr size="1" />
	<div id="divResult"></div>
	<div id="divContent" style="border: 1px solid #efefef; padding: 10px;"></div>

</body>

</html>
