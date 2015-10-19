<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.sql.*" import="java.util.*"
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
<%
	String noteid = request.getParameter("noteid");
%>
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>对帐明细</title>
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

<style>
.aw-grid-control {
	height: 400px;
	width: 100%;
	font: menu;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

.aw-column-0 {
	width: 110px;
}

.tableborder {
	BORDER-COLOR: threedlightshadow 1px solid;
	BORDER-RIGHT: threedlightshadow 1px solid;
	BORDER-TOP: threedlightshadow 1px solid;
	BACKGROUND: threedlightshadow;
	BORDER-LEFT: threedlightshadow 1px solid;
	BORDER-BOTTOM: threedlightshadow 1px solid
}
</style>

<style>
#grid_cat .aw-column-0 {
	width: 50px;
}

#grid_cat .aw-column-3 {
	width: 250px;
}
</style>


<xml id="island4venderlist" />
<xml id="island4result" />

<script language="javascript">
extendDate();
extendNumber();
var noteid = <%=noteid%>;
function init()
{
	var parms = new Array();
	parms.push( "operation=vender_list" );
	parms.push( "noteid="+noteid );
	parms.push( "timestamp=" + new Date().getTime() );
	
	var url = "../DaemonBoardManager?" + parms.join( '&' );
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_catalogue;
	courier.call();	
}

/**
 * 错误检查，给予相应的提示
 */
function analyse_catalogue( text )
{
	island4venderlist.loadXML( text );
	island4result.loadXML( text );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	show_catalogue();
}

function show_catalogue()
{
	window.status = "显示目录 ...";
	div_venderlist.innerHTML = "";

	var node_body 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/vender_list" );	
	var row_count   = node_body.childNodes.length;
	var table = new AW.XML.Table;
	island4result.loadXML( table.getXMLContent(node_body) );	
	var xml, node = document.getElementById( "island4result" );	
	
	/**
	 * 判断浏览器的类型
	 */
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument( "", "", null);
		xml.appendChild( island4result.selectSingleNode( "*" ) );
	}		

	table.setXML( xml );
	table.setColumns([ "noteid", "logdate", "venderid", "vendername", "operator" ] );
	var columnNames = [ "公文号","访问日期","供应商编码","供应商名称","操作人员"];
	var obj = new AW.UI.Grid;
	obj.setId( "grid_cat" );
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );
	obj.setCellModel(table);
	div_venderlist.innerHTML = obj.toString();
	
	window.status = "OK";
}

</script>
</head>

<body onload="init()">
	<a href="noteboard_center.jsp">返回上一级页面</a>
	<br />
	<br />
	<div id="div_venderlist"></div>
</body>

</html>