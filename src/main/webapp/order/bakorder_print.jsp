<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="java.util.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.vss.VSSConfig"
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

<html">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>归档订货审批单打印</title>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/common.js"> </script>


<script>
var sheetid = "<%=sheetid%>";
var format_show_xsl = "<%=token.site.toXSLPatch("format4purchasechk4print.xsl") %>";

window.onload = function(){
	var arr_sheetid = sheetid.split(",");
	for( var i=0; i<arr_sheetid.length;i++ ){
		load(arr_sheetid[i]);
	}
}
function load(sheetid)
{
	var url = "../DaemonSheet?operation=show&clazz=PurchasechkBak&sheetid=" + sheetid;
	var table4detail = new AW.XML.Table;
	table4detail.setURL( url );
	table4detail.request();
	table4detail.response = function(text){
		setLoading(false);
		table4detail.setXML(text);
		var html = table4detail.transformNode( format_show_xsl );
		var elm_sheet = document.createElement("div");
		elm_sheet.innerHTML = html;
		document.body.appendChild( elm_sheet );
	};
	setLoading(true);
}
</script>
</head>
<body>
	<div id="div_detail"></div>
</body>
</html>