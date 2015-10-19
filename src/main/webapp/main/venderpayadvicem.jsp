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
<title>供应商结算限额</title>
<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/MainReportGrid.js"> </script>
<script language="javascript" src="../js/MainHTML.js"> </script>
<script language="javascript" src="../js/common.js"> </script>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>

<style type="text/css">
.aw-grid-control {
	height: 75%;
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
<script language="javascript" type="text/javascript">
var service = 'VenderPayadvicem';
var report = new ReportGrid(service);
var BU = new HTML("BU");
var PayShop = new HTML("PayShop");

window.onload=function(){
	report.init();
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
			BU:<span id="span_buid"></span>
		</div>
		<div class="search_parms">
			结算主体:<span id="span_payshop"></span>
		</div>
		<div class="search_parms">
			月份: <input type="text" id="txt_month"
				onfocus="WdatePicker({skin:'whyGreen',dateFmt:'yyyyMM'})"
				class="Wdate" name="txt_parms" alt="月份" />
		</div>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>