<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3010141;
%>
<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
	//查询用户的权限.
	token.checkPermission(moduleid,Permission.READ);
%>
<%
	String sheetid = request.getParameter("sheetid");
	sheetid = ( sheetid == null || sheetid.length() == 0 )? "" : sheetid.trim();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>退货单打印</title>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<script>
var sheetid = "<%=sheetid%>";
var format_show_xsl = "<%=token.site.toXSLPatch("ret_print.xsl") %>";

window.onload = function(){
	load();
}
function load()
{
	var url = "../DaemonSheet?operation=show&clazz=Ret&sheetid=" + sheetid;
	var table4detail = new AW.XML.Table;
	table4detail.setURL( url );
	table4detail.request();
	table4detail.response = function(text){
		setLoading(false);
		table4detail.setXML(text);
		$("div_detail").innerHTML = table4detail.transformNode( format_show_xsl );
	};
	setLoading(true);
}
</script>

</head>

<body>
	<div id="div_detail"></div>
</body>
</html>