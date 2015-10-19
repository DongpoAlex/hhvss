<%@ page pageEncoding="utf-8" contentType="text/html;charset=UTF-8"
	session="false"
	import="com.royalstone.util.PermissionException,com.royalstone.security.Token,com.royalstone.security.Permission"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	request.setCharacterEncoding("UTF-8");
	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException("您尚未登录,或已超时.");
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException("您尚未登录,或已超时.");

	//查询用户的权限.
	final int moduleid = 8000011;
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ))
		throw new PermissionException(" 您未获得操作此模块的授权,请管理员联系.模块号:"
				+ moduleid);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="./css.css" rel="stylesheet" type="text/css" />
<script src="../js/Date.js" type="text/javascript"></script>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>
<script src="./common.js" type="text/javascript"></script>
<script src="./certificate_base.js" type="text/javascript"></script>
<style>
.aw-grid-control {
	height: 100%;
	width: 98%
}

.aw-column-0 {
	width: 30px;
	cursor: pointer;
}

#ctgrid .aw-column-1 {
	width: 140px;
}

#ctgrid .aw-column-2 {
	width: 60px;
}

#ctgrid .aw-column-3 {
	width: 60px;
}

#ctgrid .aw-column-4 {
	width: 70px;
}

#ctgrid .aw-column-5 {
	width: 170px;
}

#ctcgrid .aw-column-0 {
	width: 140px;
}

#ctcgrid .aw-column-1 {
	width: 100px;
}

#ctcgrid .aw-column-1 {
	width: 80px;
}

.ctcedit td {
	font-size: 12px;
}

.ctcedit input {
	border: none;
}
</style>
</head>
<body>
	<table cellspacing="1" cellpadding="2" width="100%"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader" width="470">证照类型</td>
			<td class="tableheader" width="220">证照品类</td>
			<td class="tableheader" width="300">类型品类关系</td>
		</tr>
		<tr id="header0_toggle" style="display: block">
			<td class="altbg2"><input class="button" value="查看证照类型"
				type="button" onclick="searchCT()"></td>
			<td class="altbg2"><input class="button" value="查看证照品类"
				type="button" onclick="searchCC()"></td>
			<td></td>
		</tr>
		<tr>
			<td height="300" class="altbg2">
				<div id="div_ct"></div>
			</td>
			<td class="altbg2">
				<div id="div_cc"></div>
			</td>
			<td>
				<div id="div_ctc"></div>
			</td>
		</tr>
		<tr>
			<th>编辑</th>
			<th></th>
			<th></th>
		</tr>
		<tr>
			<td>名称：<input type="text" size="30" id="txt_ctname"> 备注：<input
				type="text" size="35" id="txt_ctnote"> <input type="hidden"
				id="txt_ctid" value="0">
			</td>
			<td>名称：<input type="text" size="30" id="txt_ccname">
			</td>
			<td></td>
		</tr>
		<tr>
			<td>是否必备证照： <select id="txt_ctflag">
					<option value="0">基本证照</option>
					<option value="1">可选择证照[品类用]</option>
					<option value="2">检验型[新旧品用]</option>
			</select> 是否年审： <select id="txt_ctyearflag">
					<option value="0">不年审</option>
					<option value="1">需年审</option>
			</select>

			</td>
			<td>备注：<input type="text" size="30" id="txt_ccnote"> <input
				type="hidden" id="txt_ccid" value="0">
			</td>
			<td></td>
		</tr>
		<tr>
			<td>是否录入批文号： <select id="txt_appflag">
					<option value="0">不录入</option>
					<option value="1">需录入</option>
			</select> 是否维护批次： <select id="txt_whFlag">
					<option value="0">否</option>
					<option value="1">是</option>
			</select>
			</td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td><input type="button" value="新增" style="float: right;"
				onclick="addct()" id="addct"> <input type="button"
				value="保存" style="float: right; display: none" onclick="savect()"
				id="savect"></td>
			<td><input type="button" value="新增" style="float: right;"
				onclick="addcc()" id="addcc"> <input type="button"
				value="保存" style="float: right; display: none" onclick="savecc()"
				id="savecc"></td>
			<td></td>
		</tr>
	</table>
</body>
</html>