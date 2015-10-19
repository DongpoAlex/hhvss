<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.vss.net.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="java.sql.*" import="java.util.Date"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 6000005;
	request.setCharacterEncoding("UTF-8");

	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);

	//查询用户的权限.
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ)) {
		//throw new PermissionException("您未获得操作此模块的授权,请管理员联系.模块号:"+ moduleid);
	}

	SelNetDCshop shop = new SelNetDCshop(token, "请选择");
	shop.setAttribute("id", "txt_dccode");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../certificate/css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/Date.js"></script>
<script language="javascript" src="../AW/runtime/lib/aw.js"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"></script>
<script language="javascript" src="./netordervender.js"></script>
<style>
.aw-column-0 {
	width: 100px;
	cursor: pointer;
	color: blue;
	text-align: center
}

.aw-column-3,.aw-column-7,.aw-column-8 {
	width: 80px;
	text-align: center;
}

.aw-column-1,.aw-column-3,.aw-column-5,.aw-column-6,.aw-column-7 {
	width: 60px;
	text-align: center;
}

.aw-column-11 {
	width: 60px;
	text-align: center;
	color: blue;
	cursor: pointer;
}

.aw-column-12 {
	width: 60px;
	text-align: center;
	color: red;
	cursor: pointer;
}
</style>
<title>供应商网上预约</title>
</head>
<body>
	<table cellspacing="1" cellpadding="2" width="98%"
		class="tablecolorborder">
		<tr>
			<td class="altbg2">DC:<%=shop%>&nbsp;&nbsp;&nbsp;&nbsp; 物流模式:<select
				id="txt_logistics">
					<option>全部</option>
					<option value="1">直送</option>
					<option value="2">直通</option>
			</select>&nbsp;&nbsp;&nbsp;&nbsp;预约日期范围：<input type="text"
				id="txt_request_date_min" class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_request_date_max\',{d:-60});}',maxDate:'#F{$dp.$D(\'txt_request_date_max\',{d:0});}'})"
				name="txt_parms" alt="最小应结日期" /> - <input type="text"
				id="txt_request_date_max" class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_request_date_min\',{d:0});}',maxDate:'#F{$dp.$D(\'txt_request_date_min\',{d:60});}'})"
				name="txt_parms" alt="最大应结日期" /> &nbsp;&nbsp;&nbsp;&nbsp;标志:<select
				id="txt_flag">
					<option>全部</option>
					<option value="Y">已预约</option>
					<option value="N">已取消</option>
			</select></td>
		</tr>
		<tr>
			<td class="altbg2">&nbsp;&nbsp;&nbsp;&nbsp;<input class="button"
				value="查找" type="button" onclick="search()" /> <input
				class="button" value="新增预约" type="button" onclick="add()" /></td>
		</tr>
	</table>
	<div id="div_result"></div>
	<div id="enlarge_images" style="position: absolute; z-index: 999"></div>
</body>
</html>