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
<title>结算业务单据查询</title>

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<!-- grid format -->
<style type="text/css">
.aw-grid-control {
	height: 70%;
	width: 100%;
	border: none;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

.aw-mouseover-row {
	background: #efefff;
}
</style>

<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/MainReportGrid.js"> </script>
<script language="javascript" src="../js/MainHTML.js"> </script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" type="text/javascript">
var service = 'UnpaidSheet';
var report = new ReportGrid(service);
var SheetType = new HTML("SheetType");
var BU = new HTML("BU");
var PayShop = new HTML("PayShop");
window.onload=function(){
	report.init();
	var params = ['attribute={"id":"txt_sheettype","name":"txt_parms"}','defaultValue=2301','showAll=true'];
	SheetType.toHTML("span_sheettype",params);
	BU_PayShop_init();
};
</script>
</head>

<body>
	<div id="divTitle"></div>
	<div id="divSearch" class="search_main">
		<% if(!token.isVender){ %>
		<div class="search_parms">
			供应商编码: <input type="text" id="txt_venderid" name="txt_parms"
				split="," alt="供应商编码" />
		</div>
		<% } %>
		<div class="search_parms">
			单据类型: <span id="span_sheettype"></span>
		</div>
		<div class="search_parms">
			BU:<span id="span_buid"></span>
		</div>
		<div class="search_parms">
			结算主体:<span id="span_payshop"></span>
		</div>
		<div class="search_parms">
			门店:<input type="text" id="txt_shopid" size="12" split=","
				name="txt_parms" /><a href="javascript:showShopList('txt_shopid')">选择门店</a>
		</div>
		<div class="search_parms">
			单号:<input type="text" id="txt_sheetid" name="txt_parms" size="16" />
		</div>
		<div class="search_parms">
			<span class="required">*</span>发生日期范围（90天内）: <input type="text"
				notnull="notnull" size="14" id="txt_docdate_min" class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_docdate_max\',{d:-90});}',maxDate:'#F{$dp.$D(\'txt_docdate_max\',{d:0});}'})"
				name="txt_parms" alt="最小单据日期" /> - <input type="text"
				notnull="notnull" size="14" id="txt_docdate_max" class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_docdate_min\',{d:0});}',maxDate:'#F{$dp.$D(\'txt_docdate_min\',{d:90});}'})"
				name="txt_parms" alt="最大单据日期" />
		</div>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>