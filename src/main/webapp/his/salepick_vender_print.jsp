<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="java.util.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
%>

<%
	String sheetid = request.getParameter("sheetid");
	sheetid = ( sheetid == null || sheetid.length() == 0 )? "" : sheetid.trim();
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>带货安装单查询-供应商用</title>

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<xml id="island4detail" />
<xml id="island4format" src="format4salepick.xsl" />
<script>
var str_sheetid = "<%=sheetid%>";
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

HR {
	margin-top: 20px;
	margin-bottom: 20px;
	border: 2px dashed #000000;
}
</style>

<script>
function init(){
	
	var arr_sheetid = str_sheetid.split(",");
	
	for( var i=0; i<arr_sheetid.length; i++ ){
		read_sheet( arr_sheetid[i] );
	}

}
function read_sheet( current_sheetid )
{
	setLoading(true);
	var url = "../DaemonViewHisSheet?sheet=salepick&sheetid=" + current_sheetid;
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= display_detail;
	courier.call();

}

function display_detail( text )
{
	island4detail.loadXML( text );
	var dhtml ="";
	var html = island4detail.transformNode( island4format.documentElement );
	var elm_sheet = document.createElement("div");
	elm_sheet.innerHTML = dhtml + html + "<hr>";
	document.body.appendChild( elm_sheet );
	setLoading(false);
}
</script>


</head>

<body onload="init()">
	<a href="###" onclick="window.print();this.style.display='none'"
		style="margin-left: 15px; font-weight: bold;">[打印本页]</a>
	<img src='<%=token.site.getLogo()%>' style='margin-left: 15px;' />
	<div align='center' style='font-size: 18px; font-weight: bold;';>带货安装通知单</div>
	<br />
</body>

</html>