<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.certificate.SelCertificateType"
	import="com.royalstone.myshop.component.SelRegion"
	import="com.royalstone.security.Permission"
	import="com.royalstone.security.Token" import="com.royalstone.util.PermissionException"
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
	final int moduleid = 8000006;
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ))
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:"
				+ moduleid);

	SelCertificateType sel = new SelCertificateType(token, "全部");
	sel.setAttribute("id", "ctype");

	SelRegion regionSel = new SelRegion(token);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet">
<link href="../css/aw_style.css" rel="stylesheet" type="text/css">
<link href="./css.css" rel="stylesheet" type="text/css" />
<script src="../js/Date.js" type="text/javascript"></script>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>
<script src="./common.js" type="text/javascript"></script>
<script src="./certificate_checker_list.js" type="text/javascript"></script>
<style>
</style>
<title>待审核</title>
</head>
<body>
	<table class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader">供应商区域</td>
			<td class="tableheader">供应商编码</td>
			<td class="tableheader">审核状态</td>
			<td class="tableheader">证照类型</td>
			<td class="tableheader">证照种类</td>
			<td class="tableheader">商品名称</td>
			<td class="tableheader">模糊</td>
			<td class="tableheader">查询</td>
		</tr>
		<tr>
			<td class="altbg2"><%=regionSel%></td>
			<td class="altbg2"><input value="" type="text" id="txt_venderid"
				size="10"></td>
			<td class="altbg2"><select id="txt_flag" name="txt_flag">
					<option value="">全部</option>
					<option value="1">新提交</option>
					<option value="2">一审核</option>
					<option value="99">部分审核</option>
			</select></td>
			<td class="altbg2"><select id="txt_type" name="txt_type">
					<option value="">全部</option>
					<option value="1">基本证照</option>
					<option value="2">品类证照</option>
					<option value="3">旧品证照</option>
					<option value="4">新品证照</option>
			</select></td>
			<td class="altbg2"><%=sel%></td>
			<td class="altbg2"><input value="" type="text"
				id="txt_goodsname" size="10"></td>
			<td class="altbg2"><input id="txt_isLike" type="checkbox"></td>
			<td class="altbg2"><input class="button" value="查找"
				type="button" onclick="search()"></td>
		</tr>
	</table>
	<div id="div_result"></div>

	<div id="enlarge_images" style="position: absolute; z-index: 999"></div>
</body>
</html>
