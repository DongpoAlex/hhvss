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
<title>销售日报查询</title>
<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/MainReportGrid.js"> </script>
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
var service = 'ChargeSum';
var report = new ReportGrid(service);
window.onload=function(){
	report.init();
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
		<div class="search_parms">BU:</div>
		<div class="search_parms">
			门店:<input type="text" id="txt_shopid" size="12" split=","
				name="txt_parms" /><a href="javascript:showShopList('txt_shopid')">选择门店</a>
		</div>
		<div class="search_parms">
			商品编码: <input type="text" size="10" id="txt_goodsid" name="txt_parms" />
		</div>
		<div class="search_parms">
			课类编码: <input type="text" size="4" id="txt_categoryid"
				name="txt_parms" onchange="load_major(this.value)" /><span
				id="span_majorname"></span>
		</div>
		<div class="search_parms">
			销售日期范围（60天内）: <input type="text" id="txt_sdate_min" class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_sdate_max\',{d:-60});}',maxDate:'#F{$dp.$D(\'txt_sdate_max\',{d:0});}'})"
				name="txt_parms" alt="最小日期" /> - <input type="text"
				id="txt_sdate_max" class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_sdate_min\',{d:0});}',maxDate:'#F{$dp.$D(\'txt_sdate_min\',{d:60});}'})"
				name="txt_parms" alt="最大日期" />
		</div>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>