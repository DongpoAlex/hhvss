<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.SelBook"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	final int moduleid=3020102;
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
	String sheetid = request.getParameter("sheetid");
	sheetid = ( sheetid == null || sheetid.length() == 0 )? "" : sheetid.trim();
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>对帐申请单</title>
<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<xml id="island4head" />
<xml id="island4sheetset" />
<xml id="island4format" src="liquidation_print.xsl" />
<script>
var sheetid = "<%=sheetid%>";
</script>
<style>
BODY {
	FONT-SIZE: 12px;
	SCROLLBAR-ARROW-COLOR: #ecebeb;
	SCROLLBAR-BASE-COLOR: #ffffff;
	BACKGROUND-COLOR: #ffffff
}

.tableborder {
	BORDER-COLOR: black 1px solid;
	BORDER-RIGHT: black 1px solid;
	BORDER-TOP: black 1px solid;
	BACKGROUND: black;
	BORDER-LEFT: black 1px solid;
	BORDER-BOTTOM: black 1px solid
}
</style>

<script>
function init()
{
	var url = "../DaemonViewSheet?sheet=liquidation&sheetid=" + sheetid;
	island4sheetset.async = false;
	island4sheetset.load( url );
	var elm_booktitle=island4sheetset.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/booktitle" );
	var elm_booktypeid=island4sheetset.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/booktypeid" );
	var elm_booklogofname=island4sheetset.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/booklogofname" );
	div_title.innerHTML='<b>'+elm_booktitle.text+'－对帐申请单</b>';
	print_logo.src="../img/"+elm_booklogofname.text;	
	
	div_print_sheet.innerHTML = island4sheetset.transformNode( island4format.documentElement );
}

</script>

</head>

<body onload="init()">
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<b><img id="print_logo" src="" /><a href="###"
		onclick="this.style.visibility='hidden';window.print();this.style.visibility='visible'">[打印本页]</a></b>
	<div id="div_title" align="center" style="font-size: 18px;";></div>
	<div id="div_sheetname" align="right" style="font-size: 13px;";>
		<b>对帐单号:</b>
		<script language="javascript">document.write( sheetid );</script>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</div>
	<div id="div_print"></div>
	<div id="div_print_sheet"></div>
</body>
</html>