<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
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
<title>库存进价调整单</title>

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
<script>
var table4detail = new AW.XML.Table;
var sheetid = "<%=sheetid%>";
var format_show_xsl="<%=token.site.toXSLPatch("stkcostadj.xsl") %>";
function init()
{
	if( sheetid == null || sheetid.length == 0 ) return false;
	var url = "../DaemonStkCostAdjItem?sheet=stkcostadj&sheetid=" + sheetid;
	table4detail.setURL( url );
	table4detail.request();
	table4detail.response = function(text){
		setLoading(false);
		table4detail.setXML(text);
		$("div_detail").innerHTML = table4detail.transformNode( format_show_xsl );
		div_title.innerHTML='<b>库存进价调整单 </b>';
		print_logo.src="<%=token.site.getLogo()%>";
	};
	setLoading(true);
}
</script>


</head>

<body onload="init()">
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<b><img id="print_logo" src="" /> <a href="###"
		onclick="this.style.visibility='hidden';window.print();this.style.visibility='visible'">[打印本页]</a>
	</b>
	<div id="div_title" align="center" style="font-size: 18px;";></div>
	<div id="div_sheetname" align="right" style="font-size: 13px;";>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
	<div id="div_detail"></div>
</body>

</html>