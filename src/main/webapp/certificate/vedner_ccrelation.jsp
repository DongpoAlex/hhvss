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
final int moduleid=8000013;
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
<script language="javascript" src="./vender_ccrelation.js"> </script>
<style>
.aw-grid-control {
	height: 100%;
	width: 98%
}

#ctgrid .aw-column-0 {
	width: 30px;
}

#ctgrid .aw-column-1 {
	width: 40px;
}

#ctgrid .aw-column-2 {
	width: 160px;
}

#ctgrid .aw-column-3 {
	width: 60px;
}

#ctgrid .aw-column-4 {
	width: 60px;
}

#ccgrid .aw-column-0 {
	width: 30px;
}

#ccgrid .aw-column-1 {
	width: 30px;
}

#ccgrid .aw-column-2 {
	width: 60px;
}

#ccgrid .aw-column-3 {
	width: 120px;
	cursor: pointer;
}

#ccgrid .aw-column-4 {
	width: 0px;
}

#vendergrid .aw-column-0 {
	width: 100px;
}

#vendergrid .aw-column-1 {
	width: 300px;
}
</style>
</head>
<body>
	<div>说明：供应商与品类建立关联后，在新建品类证照时只能选择关联的品类。如果没有任何关联，在新建品类证照时可以选择全部品类</div>
	<div id="setup1" style="border: 1px solid #6e6e6e; padding: 4px;">
		<div class="tableheader">第一步：填写需要维护的供应商编码</div>
		<div>
			供应商编码：<input type="text" id="txt_venderid"
				onchange="checkVender(this.value)"></input> <span id="sp_vendername"></span>
		</div>
	</div>
	<div id="setup2"
		style="border: 1px solid #6e6e6e; padding: 4px; display: none;">
		<table width="98%">
			<tr>
				<td class="tableheader" width="50%"><span id="sp_cc"></td>
				<td class="tableheader" width="50%"><span id="sp_vender"></span></td>
			</tr>
			<tr height="360">
				<td><div id="div_cc"></div></td>
				<td><div id="div_vender"></div></td>
			</tr>
			<tr>
				<td>
					<div id="div_ccbtn">
						<input value="更新" type="button" onclick="updatecc()">
					</div>
				</td>
				<td></td>
			</tr>
		</table>
	</div>
</body>
</html>