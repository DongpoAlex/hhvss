<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.certificate.bean.Config"
	import="com.royalstone.certificate.*"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*" import="java.sql.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
request.setCharacterEncoding( "UTF-8" );
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );
//查询用户的权限.
final int moduleid=8000004;
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );

SelCertificateType sel = new SelCertificateType(token,"全部");
sel.setAttribute("id","ctype");

String g_status = request.getParameter("g_status");
if( g_status == null ) g_status="-999";

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="./css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="./common.js"> </script>
<script language="javascript" src="./certificate_vender_list.js"> </script>
<script type="text/javascript">
		var g_status = <%=g_status%>
		</script>
<style>
</style>
<title>浏览</title>
</head>
<body>
	<table cellspacing="1" cellpadding="2" width="98%"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader" width="20%">状态</td>
			<td class="tableheader" width="20%">证照类型</td>
			<td class="tableheader" width="20%">证照种类</td>
			<td class="tableheader" width="40%">查询</td>
		</tr>
		<tr>
			<td class="altbg2"><select id="txt_flag" name="txt_flag">
					<option value="">全部</option>
					<option value="0">未提交</option>
					<option value="-1">审核返回</option>
					<option value="-10">年审预警</option>
					<option value="-11">有效期预警</option>
					<option value="-100">已过期</option>
			</select></td>
			<td class="altbg2"><select id="txt_type" name="txt_type">
					<option value="">全部</option>
					<option value="1">基本证照</option>
					<option value="2">品类证照</option>
					<option value="3">旧品证照</option>
					<option value="4">新品证照</option>
			</select></td>
			<td class="altbg2"><%=sel %></td>
			<td class="altbg2"><input class="button" value="查找"
				type="button" onclick="search()"></td>
		</tr>
	</table>
	<div id="div_result"></div>

	<div id="enlarge_images" style="position: absolute; z-index: 999"></div>
</body>
</html>
