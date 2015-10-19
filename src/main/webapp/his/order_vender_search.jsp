﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 9999901;
%>
<%
	request.setCharacterEncoding("UTF-8");

	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);

	//查询用户的权限.
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ))
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:"
				+ moduleid);
%>
<%
	int[] shoptype = {11, 22};
	String note = "任意门店";
	SelBranchAll sel_branch = new SelBranchAll(token, shoptype, note);
	sel_branch.setAttribute("id", "txt_branch");

	String g_status = request.getParameter("g_status");
	if (g_status == null)
		g_status = "-1";
%>
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>订货审批单查询-供应商专用</title>
<link rel="stylesheet" href="../css/style.css" type="text/css" />
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="order_vender_search.js"> </script>
<style>
#tag_grp {
	margin-bottom: 10px;
}

.div_navigator {
	margin-bottom: 10px;
}

.aw-grid-control {
	width: 100%;
	height: 78%;
	background-color: #F9F8F4;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

#grid_cat .aw-column-0 {
	width: 130px;
}

#grid_cat .aw-column-1 {
	width: 50px;
}

#grid_cat .aw-column-2 {
	width: 60px;
}

#grid_cat .aw-column-3 {
	width: 100px;
}

#grid_cat .aw-column-4 {
	width: 100px;
}

#grid_cat .aw-column-5 {
	width: 100px;
}

#grid_cat .aw-column-6 {
	width: 60px;
}

#grid_cat .aw-column-7 {
	width: 180px;
}

#grid_cat .aw-column-8 {
	width: 130px;
}

#grid_con .aw-column-0 {
	width: 30px;
}

#grid_con .aw-column-1 {
	width: 130px;
}

#grid_con .aw-column-11 {
	width: 280px;
}

#grid_print .aw-column-0 {
	width: 30px;
}

#grid_print .aw-column-1 {
	width: 130px;
}

#grid_print .aw-column-2 {
	width: 130px;
}
</style>
<script language="javascript">
		extendDate();
		extendNumber();
		var g_status = <%=g_status%>
		</script>
<xml id="format4head" src="format4purchasechk.xsl" />
</head>
<body onload="init()">
	<div id="div_tag"></div>
	<!-- 单据查询 -->
	<div id="div_search">
		<table cellspacing="1" cellpadding="2" width="70%"
			class="tablecolorborder">
			<tr>
				<td><label> 收货地: </label></td>
				<td class=altbg2><%=sel_branch%></td>
				<td><label> 订单状态: </label></td>
				<td class=altbg2><select id="order_status">
						<option>全部</option>
						<option value="0" selected="selected">未阅读</option>
						<option value="1">已阅读</option>
						<option value="10">已确认</option>
						<option value="100">已执行</option>
				</select></td>
			</tr>
			<tr>
				<td><label> 订货日期: </label></td>
				<td class=altbg2><input type="text" id="txt_orderdate_min"
					onblur="checkDate(this)" size="12" /> - <input type="text"
					id="txt_orderdate_max" onblur="checkDate(this)" size="12" /></td>
				<td><label> 上传日期: </label></td>
				<td class=altbg2><input type="text" id="txt_releasedate_min"
					onblur="checkDate(this)" size="12" /> - <input type="text"
					id="txt_releasedate_max" onblur="checkDate(this)" size="12" /></td>
				<td colspan="2"></td>
			</tr>
			<tr class="singleborder">
				<td colspan=4></td>
			</tr>
			<tr class="whiteborder">
				<td colspan=4></td>
			</tr>
			<tr class="header">
				<td><script>
							document.write( btn_search );
						</script></td>
				<td colspan="3"></td>
			</tr>
		</table>
		<br />
		<table cellspacing="1" cellpadding="2" class="tablecolorborder">
			<tr>
				<td><label> 订货审批单号: </label></td>
				<td class=altbg2><input type="text" id="txt_sheetid" size="40" /></td>
			</tr>
			<tr>
				<td><script>
			document.write( btn_sheetid );
		</script></td>
				<td></td>
			</tr>
		</table>
	</div>
	<div id="div_cat">还没有查询单据，请查询。</div>
	<!-- 单据明细 -->
	<div id="div_detail">
		<div class="div_navigator">
			<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
				type="button" value="下一单" onclick="sheet_navigate(1)" />
			<!--  <input type="button" value="确认订单" onclick="confirm_sheet()" /> -->
			<input type="button" value="打印订单" onclick="open_win_print()" /> <input
				type="button" value="按门店打印订单"
				onclick="tag_grp.setSelectedItems( [4] );show_shop_catalotue()" />
			<input type="button" value="导出订单" onclick="downloadsheet()" /> <span
				id="offset_current"></span>
		</div>
		<div id="div_sheethead"></div>
	</div>
	<div id="div_confirm">
		<div class="div_navigator">
			<!--  
	<input type="button" value="批量确认" onclick="confirm_all()" /> 
	-->
			<input type="button" value="批量打印" onclick="print_more()" />

		</div>
		<div id="div_confirm4all"></div>
	</div>
	<div id="div_printbyshop">
		<div class="div_navigator">
			请选择需要打印的门店的订货通知单<br /> <input type="button" value="上一单"
				onclick="shop_sheet_navigate(-1)" /> <input type="button"
				value="下一单" onclick="shop_sheet_navigate(1)" /> <input
				type="button" value="打印选中订单" onclick="print_all()" /> <span
				id="shop_offset_current"></span>
		</div>
		<div id="div_shop_catalotue"></div>
	</div>
</body>
</html>
