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
final int moduleid=8000006;
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );

SelCertificateType sel = new SelCertificateType(token,"全部");
sel.setAttribute("id","ctype");
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
<script language="javascript" src="./checked_venderlist.js"> </script>
<style>
</style>
<title>已审核供应商</title>
</head>
<body>
	<table cellspacing="1" cellpadding="2" width="98%"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader">供应商编码</td>
			<td class="tableheader">审核状态</td>
			<td class="tableheader">证照类型</td>
			<td class="tableheader">模糊</td>
			<td class="tableheader">查询</td>
		</tr>
		<tr id="header0_toggle" style="display: block">
			<td class="altbg2"><input value="" type="text" id="txt_venderid"
				size="10"></td>
			<td class="altbg2"><select id="txt_flag" name="txt_flag">
					<option value="100">已审核</option>
			</select></td>
			<td class="altbg2"><input type="checkbox" value="1"
				name="txt_type" checked="checked">基本 <input type="checkbox"
				value="2" name="txt_type" checked="checked">品类 <input
				type="checkbox" value="3" name="txt_type">新品 <input
				type="checkbox" value="4" name="txt_type">旧品</td>
			<td class="altbg2"><input id="txt_isLike" type="checkbox"></td>
			<td class="altbg2"><input class="button" value="查找"
				type="button" onclick="search()"> <input class="button"
				value="导出" type="button" onclick="downLoad()"></td>
		</tr>
	</table>
	<div id="div_result"></div>

	<div id="enlarge_images" style="position: absolute; z-index: 999"></div>
</body>
</html>
