<%@ page pageEncoding="utf-8" session="false"
	import="com.royalstone.certificate.bean.Config"
	import="com.royalstone.certificate.*"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*" import="java.sql.*"
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
	final int moduleid = 8000005;
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ))
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:"
				+ moduleid);

	String type = request.getParameter("type");
	String sheetid = request.getParameter("sheetid");
	String title = Config.getTypeName(type, token);

	SelCertificateCategory csel = new SelCertificateCategory(token,
			"请选择");
	csel.setAttribute("id", "txt_ccid");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="./css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="./common.js"> </script>
<script language="javascript" src="./certificate_show.js"> </script>
<style>
</style>
</head>
<body onload="init(<%=type%>,'<%=sheetid%>')">
	<%
		if (token.isVender) {
	%>
	<div class="nav">
		证照录入导航： <a href="./certificate_add.jsp?type=1" target="_self">基本证照</a>
		<a href="./certificate_add.jsp?type=2" target="_self">品类证照</a> <a
			href="./certificate_add.jsp?type=3" target="_self">旧品证照</a> <a
			href="./certificate_add.jsp?type=4" target="_self">新品证照</a>
	</div>
	<%
		}
	%>
	<div class="main">
		<div class="title"><%=title%>查看
		</div>
		<div class="content">
			<table cellpadding="4px" cellspacing="1">
				<tr>
					<th>单据编码：</th>
					<td id="txt_sheetid"><%=sheetid%></td>
					<th>单据状态：</th>
					<td id="txt_flag"></td>
				</tr>
				<tr>
					<th>供应商编码：</th>
					<td id="txt_venderid"></td>
					<th>供应商名称：</th>
					<td id="txt_vendername"></td>
				</tr>
				<tr>
					<th>供应商地址：</th>
					<td colspan="3" id="txt_addr"></td>
				</tr>
				<tr>
					<th>联系电话：</th>
					<td id="txt_tel"></td>
					<th>联系人：</th>
					<td id="txt_contact"></td>
				</tr>
			</table>
			<div class="tool" id="tool">
				<%
					if ("1".equals(type)) {
				%>
				供应商类型： <label for="voteoption1"> <input type="radio"
					checked="checked" name="venderType" value="1" id="voteoption1" />生产型
				</label> <label for="voteoption2"> <input type="radio"
					name="venderType" value="2" id="voteoption2" />代理或贸易型
				</label> <input class="lineinput" type="text" size="20" value=""
					id="txt_venderTypeName">
				<%
					} else if ("2".equals(type)) {
				%>
				索证品类选择：<%=csel%>
				供应商类型： <label for="voteoption1"> <input type="radio"
					checked="checked" name="venderType" value="1" id="voteoption1" />生产型
				</label> <label for="voteoption2"> <input type="radio"
					name="venderType" value="2" id="voteoption2" />代理或贸易型
				</label> <input class="lineinput" type="text" size="20" value=""
					id="txt_venderTypeName">
				<%
					} else {
				%>
				<%
					}
				%>
			</div>
			<div class="list" id="itemList"></div>
		</div>
	</div>
	<div id="enlarge_images" style="position: absolute; z-index: 999"></div>
</body>
</html>