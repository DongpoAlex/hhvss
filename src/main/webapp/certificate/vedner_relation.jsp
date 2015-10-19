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
final int moduleid=8000012;
Permission perm = token.getPermission( moduleid );
//if ( !perm.include( Permission.READ ) ) 
//	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
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
<script language="javascript" src="./vender_relation.js"> </script>
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
	width: 0px;
	cursor: pointer;
}

#ccgrid .aw-column-4 {
	width: 0px;
}

#crgrid .aw-column-0 {
	width: 60px;
}

#crgrid .aw-column-1 {
	width: 120px;
}

#crgrid .aw-column-2 {
	width: 60px;
}

#crgrid .aw-column-3 {
	width: 120px;
}

#crgrid .aw-column-4 {
	width: 80px;
}

#crgrid .aw-column-5 {
	width: 100px;
}
</style>
</head>
<body>
	<div id="setup1" style="border: 1px solid #6e6e6e; padding: 4px;">
		<div class="tableheader">第一步：填写需要维护的供应商编码</div>
		<div>
			供应商编码：<input type="text" id="txt_venderid"
				onchange="checkVender(this.value)"></input> <span id="sp_vendername"></span>
		</div>
	</div>
	<div id="setup2"
		style="border: 1px solid #6e6e6e; padding: 4px; display: none;">
		<div class="tableheader">第 二步：选择维护的证照类型</div>
		<div>
			<label for="redio1">基本证照<input type="radio" name="ctradio"
				id="redio1" value="0" onclick="setup(this.value)"></input></label> <label
				for="redio2">品类证照<input type="radio" name="ctradio"
				id="redio2" value="1" onclick="setup(this.value)"></input></label> <label
				for="redio3">检验类证照<input type="radio" name="ctradio"
				id="redio3" value="2" onclick="setup(this.value)"></input></label>
		</div>
	</div>

	<div id="setup3"
		style="border: 1px solid #6e6e6e; padding: 4px; display: none;">
		<table width="98%">
			<tr>
				<td class="tableheader" width="20%">证照品类</td>
				<td class="tableheader" width="40%">证照种类</td>
				<td class="tableheader" width="40%">现有关系</td>

			</tr>
			<tr height="360">
				<td><div id="div_cc"></div></td>
				<td>
					<div id="div_ct"></div>
				</td>
				<td><div id="div_list"></div></td>
			</tr>
			<tr>
				<td>
					<div id="div_ccbtn" style="display: none;">
						<input value="更新" type="button" onclick="updatecc()">
					</div>
				</td>
				<td>
					<div id="div_ctbtn" style="display: none;">
						<input value="全选" type="button" onclick="selectAll()">&nbsp;&nbsp;&nbsp;&nbsp;
						<input value="全不选" type="button" onclick="selectNone()">&nbsp;&nbsp;&nbsp;&nbsp;
						<input value="更新" type="button" onclick="update()">
					</div>
				</td>
				<td></td>
			</tr>
		</table>
	</div>
</body>
</html>