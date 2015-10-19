<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3010110;
	request.setCharacterEncoding("UTF-8");
	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);

	//查询用户的权限.
	token.checkPermission(moduleid,Permission.READ);
	
	String g_status = request.getParameter("g_status");
	if (g_status == null)
		g_status = "-1";
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>归档订货审批单查询</title>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"></script>
<script language="javascript" src="bakorder_vender_search.js"> </script>

<script language="javascript">
		var g_status = <%=g_status%>;
		var g_sid = <%=token.site.getSid()%>;
		var format_show_xsl = "<%=token.site.toXSLPatch("format4purchasechk.xsl")%>";
</script>

<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
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
	height: 24px;
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
</head>

<body>
	<div id="div_tag"></div>
	<!-- 单据查询 -->
	<div id="div_search" class="search_main">
		<div class="search_parms">
			收货地:<input type="text" id="txt_shopid" size="12" /><a
				href="javascript:showShopList('txt_shopid')">选择门店</a>
		</div>
		<div class="search_parms">
			订单状态:<select id="txt_status">
				<option>全部</option>
				<option value="0" selected="selected">未阅读</option>
				<option value="1">已阅读</option>
				<option value="10">已确认</option>
				<!-- <option value="100">已执行</option> -->
			</select>
		</div>
		<div class="search_parms">
			物流模式:<select id="txt_logistics">
				<option>全部</option>
				<option value="1">直送</option>
				<option value="2">直通</option>
			</select>
		</div>
		<div class="search_parms">
			订货日期: <input type="text" id="txt_orderdate_min"
				onblur="checkDate(this)" size="12" /> <span class="wdatepicker"
				onclick="WdatePicker({el:'txt_orderdate_min'})"></span> - <input
				type="text" id="txt_orderdate_max" onblur="checkDate(this)"
				size="12" /> <span class="wdatepicker"
				onclick="WdatePicker({el:'txt_orderdate_max'})"></span>
		</div>
		<div class="search_parms">
			上传日期: <input type="text" id="txt_releasedate_min"
				onblur="checkDate(this)" size="12" /> <span class="wdatepicker"
				onclick="WdatePicker({el:'txt_releasedate_min'})"></span> - <input
				type="text" id="txt_releasedate_max" onblur="checkDate(this)"
				size="12" /> <span class="wdatepicker"
				onclick="WdatePicker({el:'txt_releasedate_max'})"></span>
		</div>
		<div class="search_parms">
			订货审批单号: <input type="text" id="txt_sheetid" size="20" />
		</div>
		<div class="search_parms">
			订货通知单号: <input type="text" id="txt_sheetid_purchase" size="20" />
		</div>
		<div class="search_button" id="div_button_search"></div>
	</div>
	<div id="div_cat">还没有查询单据，请查询。</div>
	<!-- 单据明细 -->
	<div id="div_detail">
		<div class="div_navigator">
			<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
				type="button" value="下一单" onclick="sheet_navigate(1)" /> <input
				type="button" value="打印订单" onclick="open_win_print()" /> <input
				type="button" value="按门店打印订单"
				onclick="tag_grp.setSelectedItems( [4] );show_shop_catalotue()" />
			<input type="button" value="导出订单" onclick="downloadsheet()" /> <span
				id="offset_current"></span>
		</div>
		<div id="div_sheethead"></div>
	</div>
	<div id="div_confirm">
		<div class="div_navigator">
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
	&nbsp;
</body>
</html>
