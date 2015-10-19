<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.vss.net.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="java.sql.*" import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
HttpSession session = request.getSession(false);
if (session == null)
	throw new PermissionException(PermissionException.LOGIN_PROMPT);
Token token = (Token) session.getAttribute("TOKEN");
if (token == null)
	throw new PermissionException(PermissionException.LOGIN_PROMPT);


String order_serial = request.getParameter("order_serial");
String dccode = request.getParameter("dccode");

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../certificate/css.css" rel="stylesheet" type="text/css" />

<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/Date.js" type="text/javascript"> </script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" src="./netordervender_edit.js"></script>
<style>
.aw-column-0 {
	width: 80px;
	cursor: pointer;
	text-align: center;
}

.aw-column-6,.aw-column-7 {
	width: 130px;
	cursor: pointer;
	text-align: center;
}

.aw-column-2,.aw-column-3,.aw-column-4,.aw-column-5 {
	width: 80px;
	cursor: pointer;
	text-align: center;
}

.aw-column-1 {
	width: 130px;
	cursor: pointer;
	text-align: center;
	color: blue;
}

.aw-column-8,.aw-column-9 {
	width: 80px;
	cursor: pointer;
	text-align: right;
}
</style>
<script language="javascript" type="text/javascript"><!--
	
</script>
<title>供应商网上预约修改</title>
</head>
<body onload="init('<%=order_serial%>','<%=dccode%>')">
	<div id="divTitle">供应商网上预约修改</div>

	<hr size="1" />
	<div id="div_ordersh">
		<div style="margin-bottom: 10px;">
			预约流水号：<input type="text" size="20" maxlength="20"
				id="txt_order_serial"></input>
		</div>
		<div style="margin-bottom: 10px;">
			D&nbsp;C&nbsp;编&nbsp;码&nbsp;&nbsp;&nbsp;：<input type="text" size="20"
				maxlength="20" id="txt_dccode"></input>
		</div>
		<div style="margin-bottom: 10px;">
			物&nbsp;流&nbsp;模&nbsp;式&nbsp;： <select id="txt_logistics">
				<option value="1">&nbsp;&nbsp;&nbsp;直送&nbsp;&nbsp;&nbsp;</option>
				<option value="2">&nbsp;&nbsp;&nbsp;直通&nbsp;&nbsp;&nbsp;</option>
			</select>
		</div>
		<div style="margin-bottom: 10px;">
			预&nbsp;约&nbsp;日&nbsp;期&nbsp;：<input type="text" id="txt_request_date" />
		</div>
		<div style="margin-bottom: 10px;">
			预约时间段&nbsp;：<input type="text" id="txt_start_time" />至<input
				type="text" id="txt_end_time" />
		</div>
		<div style="margin-bottom: 10px;">
			实际送货箱数：<input type="text" id="txt_pkgnum" maxlength="10" />
		</div>
		<div style="margin-bottom: 10px;">
			备 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注&nbsp;：
			<textarea cols="60" rows="3" id="txt_note"></textarea>
			(最多50字)
		</div>

		<div style="margin-bottom: 10px;">
			<input type="button" onclick="addorderpovender()" value="加单" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input
				type="button" onclick="delorderpovender()" value="减单" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" onclick="save()" value="保存" />
		</div>
	</div>
	<div>
		<SPAN ID="sp_venderlist"></SPAN>
	</div>
	<div id="div_poitem"></div>
</html>