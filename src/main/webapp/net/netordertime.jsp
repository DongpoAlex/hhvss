<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.vss.net.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="java.sql.*" import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 6000002;
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );
	if ( !perm.include( Permission.READ ) ) {
		  //throw new PermissionException("您未获得操作此模块的授权，请管理员联系。模块号:"+moduleid );
		}
	SelNetDCshop shop = new SelNetDCshop(token,"请选择");
	shop.setAttribute("id","txt_dccode");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../certificate/css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" src="./netordertime.js"> </script>
<style>
.aw-column-0 {
	width: 60px;
	cursor: pointer;
	text-align: center
}

.aw-column-1,.aw-column-2,.aw-column-3 {
	width: 60px;
	text-align: center;
}

.aw-column-4,.aw-column-5 {
	width: 60px;
	text-align: right;
}

.aw-column-6,.aw-column-7,.aw-column-8,.aw-column-9 {
	width: 90px;
	text-align: right;
}

.aw-column-10 {
	width: 150px;
}

.aw-column-11 {
	width: 60px;
	text-align: center;
	color: blue
}

.aw-column-12 {
	width: 60px;
	text-align: center;
	color: red
}
</style>
<title></title>
</head>
<body>
	<table cellspacing="1" cellpadding="2" width="98%"
		class="tablecolorborder">
		<tr>
			<td class="altbg2">DC:<%=shop%>&nbsp;&nbsp;&nbsp;&nbsp; <input
				class="button" value="查找" type="button" onclick="search()" /> <input
				class="button" value="新增" type="button" onclick="add()" />
			</td>
		</TR>
	</table>
	<div id="div_result"></div>
	<div id="enlarge_images" style="position: absolute; z-index: 999"></div>
</body>
</html>
