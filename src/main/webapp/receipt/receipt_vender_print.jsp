<%@page contentType="text/html;charset=UTF-8" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
%>
<%
	final int moduleid = 3010121;
%>
<%
	request.setCharacterEncoding( "UTF-8" );

	session = request.getSession( false );
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
<title>print_purchase</title>
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

<xml id="island4detail" />
<xml id="island4format" src="receipt_vender_detail.xsl" />
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

<style>
div.box {
	border-style: solid;
	border-color: black;
	border-width: 1;
	width: 740;
}

div.box div {
	margin: 5px;
}

div.sheethead {
	margin: 5px;
}

span.sheethead {
	padding: 10px;
}

label {
	font-size: 11px;
	font-weight: bold;
	color: navy;
}
</style>

<script>
function init()
{
	init_dateil();
}
</script>

<script>
function init_dateil()
{
	var url = "../DaemonViewSheet?sheet=receipt&sheetid=" + sheetid;
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= display_detail;
	courier.call();	
	window.status="装载完毕!";
}

function display_detail( text )
{
	island4detail.loadXML( text );
	div_detail.innerHTML = island4detail.transformNode( island4format.documentElement );
}
</script>


</head>

<body onload="init()">
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<b><img src="<%=token.site.getLogo()%>" /><a href="###"
		onclick="this.style.visibility='hidden';window.print();this.style.visibility='visible'">[打印本页]</a></b>
	<div id="div_title" align="center" style="font-size: 18px;";>
		<b><%=token.site.getTitle()%>－供应商收货单明细</b>
	</div>
	<div id="div_sheetname" align="right" style="font-size: 13px;";>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
	<div id="div_detail"></div>
</body>
</html>