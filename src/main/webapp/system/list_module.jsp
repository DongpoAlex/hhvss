﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.util.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	errorPage="../WEB-INF/errorpage.jsp"%>
<%
	request.setCharacterEncoding("UTF-8");
	HttpSession session = request.getSession(false);
	if (session == null)
		throw new TokenException("您尚未登录,或已超时.");
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new TokenException("您尚未登录,或已超时.");

	//查询用户的权限.
	final int moduleid = 9010101;
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ))
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:"
				+ moduleid);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/system.css" type="text/css">
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script type="text/javascript" src="../js/jquery.xslt.js"></script>

<script language="javascript">
	jQuery(document).ready(function() {
		init();
	});

	function init() {
		var url = "../DaemonModuleList?action=list_allModule";
		var xslPath = "list_module.xsl";
		jQuery.get(url,{},function(xml){
			var elmXerr = jQuery(xml).find('xerr');
			var code = elmXerr.children('code').text();
			if(code!=0){
				alert(elmXerr.find('note').text());
				return;
			}else{
				var xmlstr;
				if(jQuery.browser.msie){
					xmlstr = xml.xml;
				}else{
					xmlstr = (new XMLSerializer()).serializeToString(xml);
				}
				jQuery('#divModule').xslt(xmlstr,xslPath);
			}
		});
	}
</script>

</head>
<body>
	<div id="divModule"></div>
	<div id="cat_temp" style="display: none;"></div>
</body>
</html>
