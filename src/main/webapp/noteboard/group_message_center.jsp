<%@page contentType="text/html;charset=utf-8" session="false"
	errorPage="../errorpage/errorpage.jsp" import="java.lang.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"%>

<%

	final int moduleid=5030103;
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
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>群发消息管理</title>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script src="../AW/runtime/lib/aw.js"></script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<style>
.divmess {
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
	height: 70%;
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

.aw-mouseover-row {
	background: #efefff;
}

.aw-mouseover-cell {
	
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
	text-align: center;
	width: 45px;
	color: red;
	cursor: hand;
}

#myGrid .aw-column-2 {
	text-align: left;
	width: 200px;
}

#myGrid .aw-column-3 {
	text-align: center;
}

#myGrid .aw-column-4 {
	text-align: center;
}

#myGrid .aw-column-5 {
	width: 0px;
}

#myGrid .aw-column-8 {
	color: #060;
	cursor: hand;
}

#myGrid .aw-column-9 {
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

<script language="javascript">

function $(id){
	return document.getElementById(id);
}


function init(){
	setLoading( true ,"正在下载目录" );
	var parms = new Array();
		parms.push( "operation=sendslist" );
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
	var table = new AW.XML.Table;
	table.setXML(table.getXMLContent(elm_body));
	table.setColumns(["","","title","editdate","expiredate","msgid","editor","call_number","下载","content"]);
	var header = ["查看","删除","消息标题","发送日期","有效日期","消息id","发送者","阅读情况","导出阅读情况"];
	var grid = new AW.UI.Grid;
	grid.setId("myGrid");
	grid.setColumnCount(header.length);
	grid.setHeaderText(header);
	grid.setRowCount(rows);
	grid.setSelectorVisible(true);
	grid.setSelectorWidth(30);
	grid.setSelectorText(function(i){return this.getRowPosition(i)+1});
	grid.setCellModel(table);
	grid.setCellText("查看",0);
	grid.setCellText("删除",1);
	grid.setCellText("导出",8);
	for( var i=0; i<rows; i++ )
	{
		var call_number = grid.getCellText(7, i);
		var str = (call_number == '' )?"没有人阅读":"已有"+call_number+"人阅读";
		grid.setCellText(str, 7, i);
	}
	grid.onCellClicked = function clickFun(event, column, row){
		var msgid = grid.getCellValue(5,row);
		if( column == 0 ){
		
		$("divContent").innerHTML = "<input type='button' id='btn_down' value='下载' onclick='exportXLS(\""+msgid+"\")'/>"+
		"<br/> 消息说明:<textarea rows='5' cols='100'>"+
		grid.getCellValue(9,row)
		+"</textarea>"
		}else if( column == 1){
			if(confirm("您确定要删除么？")){
				delMsg(msgid);
			}
		}else if( column == 8 ){
			exportLog(msgid);
		}
	};
	grid.sort(3,"descending"); //按editdate降序
	return grid;
}




function delMsg(msgid){
	setLoading( true ,"正在删除" );
	var parms = new Array();
		parms.push( "moduleid=<%=moduleid%>" );
		parms.push( "operation=delmsg" );
		parms.push( "msgid="+msgid );
	var url = "../DaemonVenderMsg?" + parms.join( "&" );
	var courier 			= new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= function(text){
		setLoading(false);
		island4result.loadXML( "" );
		island4result.loadXML( text );
		var elm_err = island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
		var xerr = parseXErr( elm_err );
		if(xerr.code != "0" ){
			alert("删除失败！"+ xerr.note);
			return false;
		}
		else {
			alert("删除成功！");
			init();
			return true;			
		}	
	
	};
	courier.call();


}
function exportXLS(msgid){
	var url = "../DaemonVenderMsg?operation=downexcel4sender&msgid="+msgid;
	window.location.href = url;
}

function exportLog(msgid){
	var url = "../DaemonVenderMsg?operation=downlog&msgid="+msgid;
	window.location.href = url;
}
</script>

<xml id="island4req"> <xdoc> <xout> </xout> </xdoc> </xml>
<xml id="island4result" />

</head>
<body onload="init()">
	<div id="divloading" class="divmess"></div>
	<div id="title">已发送消息目录</div>
	<div>
		<input type="button" value="发送新消息"
			onclick="javascript:location = 'group_message_upload.jsp'"
			target="_self" />
	</div>
	<hr size="1" />
	<div id="divResult"></div>
	<div id="divContent" style="border: 1px solid #efefef; padding: 10px;"></div>

</body>

</html>
