<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.certificate.bean.Config"
	import="com.royalstone.certificate.*"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*" import="java.sql.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
request.setCharacterEncoding( "UTF-8" );
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );
//查询用户的权限.
final int moduleid=8000016;
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );

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
<script language="javascript" src="./export_list.js"> </script>
<style type="text/css">
.aw-grid-control {
	height: 75%;
	width: 100%;
	border: none;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

.aw-mouseover-row {
	background: #efefff;
}

.aw-column-0 {
	width: 40px;
}

.aw-column-1 {
	width: 80px;
}

.aw-column-2 {
	width: 80px;
}

.aw-column-3 {
	width: 80px;
}

.aw-column-4 {
	width: 80px;
}

.aw-column-5 {
	width: 120px;
}

.aw-column-6 {
	width: 80px;
}

.aw-column-7 {
	width: 60px;
}

.aw-column-8 {
	width: 40px;
}

.aw-column-9 {
	
}
</style>
<title>待导出证照任务</title>
</head>
<body>
	<table cellspacing="1" cellpadding="2" width="98%"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader">状态</td>
			<td class="tableheader">查询</td>
			<td class="tableheader">出错任务重置</td>
		</tr>
		<tr id="header0_toggle" style="display: block">
			<td class="altbg2"><select id="txt_flag" name="txt_flag">
					<option value="">全部</option>
					<option value="0">待传</option>
					<option value="-1" selected="selected">出错</option>
			</select></td>
			<td class="altbg2"><input class="button" value="查找"
				type="button" onclick="search()"></td>
			<td class="altbg2"><input class="button" value="重置勾选的任务"
				type="button" onclick="redo()"></td>
		</tr>
	</table>
	<div id="div_result"></div>

	<div id="enlarge_images" style="position: absolute; z-index: 999"></div>
</body>
</html>
