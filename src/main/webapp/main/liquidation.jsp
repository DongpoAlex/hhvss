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
<title>对账申请单</title>
<link rel="stylesheet" href="../css/main.css" type="text/css" />

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>

<style type="text/css">
.aw-grid-control {
	height: 76%;
	width: 98%;
	border: none;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}
</style>

<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/ReportGrid.js"> </script>
<script language="javascript" src="../js/MainSheet.js"></script>
<script language="javascript" src="../js/MainHTML.js"> </script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" type="text/javascript">
var attributeOfNewWnd = "status=yes,toolbar=no,menubar=no,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);

	var service = "Liquidation";
	var s = new Sheet(service);
	var cmid = window.location.href.getQuery("cmid");

	var BU = new HTML("BU");
	var PayShop = new HTML("PayShop");

	s.endInitGrid = function(){
		var obj_link = new AW.Templates.Link;
		obj_link.setStyle("color:blue;cursor:pointer;");
		obj_link.setEvent( "onclick",
			function(){
				var row = s._grid.getCurrentRow();
				s.offset = row;
				var sheetid = s._grid.getCellValue( 2,row );
				s.open_win_show(3020301000,"Paymentsheet",sheetid);
			}
		);
		s._grid.setCellTemplate( obj_link, 2 ); 
	};
	
	window.onload = function(){
		s.init();
		//s.allowPrint();
		s.disabledRead();
		s.disabledConfirm();
		<% if(token.isVender){ %>
			var btn_new = new AW.UI.Button;
			btn_new.setControlText( "发起对账申请" );
			btn_new.setId( "btnnew" );
			btn_new.setControlImage( "favorites" );	
			btn_new.onClick = newSheet;
			$('div_button_new').innerHTML=btn_new;
		<%}%>

		BU_PayShop_init();
	};

	
	function newSheet(){
		var url = "liquidation_new.jsp?cmid="+cmid;
		var w = window.open( url, cmid, attributeOfNewWnd);
		w.focus();
	}
	
	function toExcel(){
		var url = "../DaemonMainDownload?operation=excelSheet&cmid="+cmid+"&service="+service+"&sheetid="+s.current_sheetid;
		window.location.href = url;
	}
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
				style="display: none" id="btn_print" /> <input type="button"
				value="导出到excel" onclick="toExcel()" id="btn_excel" /> <span
				id="offset_current"></span>
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
				<td>单据号(可选)</td>
				<td>对账日期(可选)</td>
			</tr>
			<tr>
				<% if(!token.isVender){ %>
				<td><input type="text" id="txt_venderid" name="txt_parms"
					size="16" split="," alt="供应商编码" /></td>
				<% } %>
				<td><span id="span_buid"></span></td>
				<td><span id="span_payshop"></span></td>
				<td><input type="text" id="txt_sheetid" name="txt_parms"
					size="16" /></td>
				<td>从<input type="text" size="10" id="txt_editdate_min"
					class="Wdate" onFocus="WdatePicker()" name="txt_parms" alt="最小对账日期" />
					到<input type="text" size="10" id="txt_editdate_max" class="Wdate"
					onFocus="WdatePicker()" name="txt_parms" alt="最大对账日期" />
				</td>
			</tr>
		</table>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
			<span id="div_button_new"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>