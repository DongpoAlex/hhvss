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

SelNetDCshop shop = new SelNetDCshop(token,"请选择");
shop.setAttribute("id","txt_dccode");
shop.setAttribute("onchange","getOrderTime()");


//SelNetOrderTime sel=new SelNetOrderTime(token,"请选择");
//sel.setAttribute("id","txt_time");

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
<script language="javascript" src="./netordervender_add.js">
</script>
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
<script language="javascript" type="text/javascript">
	

</script>
<title>供应商网上预约</title>
</head>
<body>
	<div id="divTitle">供应商网上预约</div>

	<hr size="1" />
	<table>
		<tr>
			<td width="50">D&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;C&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:</td>
			<td><%=shop%></td>
		</tr>
		<tr>
			<td>物&nbsp;流&nbsp;模&nbsp;式&nbsp;：</td>
			<td><select id="txt_logistics" onchange="getOrderTime()">
					<option value="1">&nbsp;&nbsp;&nbsp;直送&nbsp;&nbsp;&nbsp;</option>
					<option value="2">&nbsp;&nbsp;&nbsp;直通&nbsp;&nbsp;&nbsp;</option>
			</select></td>
		</tr>
		<tr>
			<td>预&nbsp;约&nbsp;日&nbsp;期&nbsp;：</td>
			<td><input type="text" id="txt_request_date" class="Wdate"
				onFocus="" onchange="checkDateIsvalid()" /></td>
		</tr>
		<tr>
			<td>预约时间段：</td>
			<td><select id="txt_time" onchange="queryt()"></select>(请先选择DC编码)</td>
		</tr>
		<tr>
			<td>实际送货箱数：</td>
			<td><input type="text" id="txt_pkgnum" maxlength="10" /></td>
		</tr>
		<tr>
			<td>备
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注&nbsp;&nbsp;&nbsp;&nbsp;：</td>
			<td><input type="text" id="txt_note" size="50" maxlength="50"></td>
		</tr>
	</table>
	<div id="div_ordersh">
		<div style="margin-bottom: 10px;">
			<input type="button" onclick="searchpo()" value="查看有效订单" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input
				type="button" onclick="initText()" value="清除" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" onclick="netordersave()" value="预约保存" />
		</div>
	</div>
	<div>
		<SPAN ID="sp_venderlist"></SPAN>
	</div>
	<div id="div_poitem"></div>
</html>