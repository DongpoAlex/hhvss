<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*" import="java.sql.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 6000004;
	request.setCharacterEncoding("UTF-8");

	HttpSession session = request.getSession(false);
	
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);

	//查询用户的权限.
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ)) {
		//throw new PermissionException("您未获得操作此模块的授权,请管理员联系.模块号:"+ moduleid);
	}
	String sheetid = request.getParameter("sheetid");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../certificate/css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/Date.js">
</script>
<script language="javascript" src="../AW/runtime/lib/aw.js">
</script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js">
</script>
<script language="javascript">
var format_show_xsl = "<%=token.site.toXSLPatch("format4purchasechk.xsl")%>";
function init(sheetid){
	var url = "../DaemonSheet?operation=show&clazz=Purchasechk&sheetid=" + sheetid;
	var table4detail = new AW.XML.Table;
	table4detail.setURL( url );
	table4detail.request();
	table4detail.response = function( text ){
		table4detail.setXML(text);
		$("div_sheet").innerHTML = table4detail.transformNode( format_show_xsl );
		setLoading(false);
	};
	setLoading(true);
}
</script>
<title>审批单商品明细</title>
</head>
<body onload="init('<%=sheetid%>')">
	<div id="div_sheet"></div>
</body>
</html>