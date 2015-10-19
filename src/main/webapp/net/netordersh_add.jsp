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
<script language="javascript" src="./netordersh_add.js">
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
<title>网上特殊预约</title>
</head>
<body>
	<div id="divTitle">网上特殊预约</div>

	<hr size="1" />
	<div id="div_ordersh">
		<div style="margin-bottom: 10px;">
			供应商编码：<input type="text" size="20" maxlength="20"
				id="txt_supplier_no" onchange="checkVender(this.value)"></input>&nbsp;&nbsp;<SPAN
				ID="sp_vendername"></SPAN>
		</div>

		<div style="margin-bottom: 10px;">
			DC：<%=shop%></div>
		<div style="margin-bottom: 10px;">
			物&nbsp;流&nbsp;模&nbsp;式&nbsp;：<select id="txt_logistics"
				onchange="getOrderTime()">
				<option value="1">直送</option>
				<option value="2">直通</option>
			</select>
		</div>
		<div style="margin-bottom: 10px;">
			预&nbsp;约&nbsp;日&nbsp;期&nbsp;：<input type="text" id="txt_request_date"
				class="Wdate" onFocus="WdatePicker()" />
		</div>
		<div style="margin-bottom: 10px;">
			预约时间段&nbsp;：<select id="txt_time">
			</select>(请先选择DC编码)
		</div>
		<div>
			实际送货箱数：<input type="text" id="txt_pkgnum" maxlength="10" />
		</div>
		<div style="margin-bottom: 10px;">
			备 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注&nbsp;：
			<textarea cols="60" rows="3" id="txt_note"></textarea>
			(最多500字)
		</div>

		<div style="margin-bottom: 10px;">
			<input type="button" onclick="searchpo()" value="查看有效订单" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input
				type="button" onclick="initText()" value="清除" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" onclick="netordersave()" value="预约保存" />
		</div>
	</div>
	<div id="div_poitem"></div>
</html>