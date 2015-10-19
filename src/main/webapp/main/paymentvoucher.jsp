<%@page contentType="text/html;charset=UTF-8" session="false"%>
<%@include file="../include/common.jsp"%>
<%@include file="../include/token.jsp"%>
<%@include file="../include/permission.jsp"%>
<%
    //用户的查询权限.
	token.checkPermission(moduleid,Permission.READ);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>付款单查询</title>
<!-- ActiveWidgets stylesheet and scripts -->
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>

<style type="text/css">
</style>
<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/ReportGrid.js"> </script>
<script language="javascript" src="../js/MainSheet.js"></script>
<script language="javascript" src="../js/MainHTML.js"> </script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" type="text/javascript">
	var service = "Paymentvoucher";
	var s = new Sheet(service);
	var cmid = window.location.href.getQuery("cmid");

	var BU = new HTML("BU");
	var PayShop = new HTML("PayShop");
	
	window.onload = function(){
		s.init();
		s.allowPrint();
		s.disabledRead();
		s.disabledConfirm();

		BU_PayShop_init();
		
	};


</script>
</head>

<body>
	<div id="divTitle"></div>
	<div id="div_tabs" style="width: 100%;"></div>
	<div id="div1"></div>
	<div id="div2" style="display: none;">
		<div id="div_navigator" style="margin: 4px;">
			<input type="button" value="上一单" onclick="s.sheet_navigate(-1)" /> <input
				type="button" value="下一单" onclick="s.sheet_navigate(1)" /> <input
				type="button" value="打印单据" onclick="s.open_win_print()"
				style="display: none" id="btn_print" /> <span id="offset_current"></span>
		</div>
		<div id="div_warning" class="warning"></div>
		<div id="div_sheetshow"></div>
	</div>
	<div id="div0" class="search_main">
		<table cellspacing="1" cellpadding="2">
			<tr class="search_main_head">
				<% if(!token.isVender){ %><td>供应商编码</td>
				<% } %>
				<td>BU</td>
				<td>结算主体</td>
				<td>状态</td>
				<td>单据号(可选)</td>
				<td>计划付款日期(可选)</td>
			</tr>
			<tr>
				<% if(!token.isVender){ %>
				<td><input type="text" id="txt_venderid" name="txt_parms"
					size="16" split="," alt="供应商编码" /></td>
				<% } %>
				<td><span id="span_buid"></span></td>
				<td><span id="span_payshop"></span></td>
				<td><select name="txt_parms" id="txt_flag">
						<option value="">全部</option>
						<option value="1">新建</option>
						<option value="2">制单审核</option>
						<option value="3">已审定</option>
						<option value="4">已付款确认</option>
						<option value="5">已支付</option>
				</select></td>
				<td><input type="text" id="txt_sheetid" name="txt_parms"
					size="16" /></td>
				<td>从<input type="text" id="txt_date_min" size="14"
					class="Wdate" onFocus="WdatePicker()" name="txt_parms"
					alt="最小计划付款日期" /> 到<input type="text" id="txt_date_max" size="14"
					class="Wdate" onFocus="WdatePicker()" name="txt_parms"
					alt="最大计划付款日期" />
				</td>
			</tr>
		</table>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>