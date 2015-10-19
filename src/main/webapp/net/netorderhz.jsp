<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.vss.net.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="java.sql.*" import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	final int moduleid=6000007;
%>
<%
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
	
	
	SelNetDCshop shop = new SelNetDCshop(token,"请选择");
	shop.setAttribute("id","txt_dccode");
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title>供应商网上预约汇总查询</title>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../certificate/css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/Date.js"></script>
<script language="javascript" src="../AW/runtime/lib/aw.js"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"></script>
<script language="javascript" src="../js/ajax.js" type="text/javascript"> </script>
<script language="javascript" src="../js/XErr.js" type="text/javascript"> </script>

<style type="text/css">
.aw-grid-control {
	height: 70%;
	width: 100%;
	border: none;
	font: menu;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

#myGrid {
	background-color: #F9F8F4
}

#myGrid .aw-column-0 {
	width: 130px;
	cursor: pointer;
}

#title {
	text-align: center;
	color: #42658D;
	font-size: 16px;
	font-family: '楷体_GB2312';
	font-weight: bold;
}

.aw-column-0,.aw-column-6 {
	width: 100px;
	cursor: pointer;
	text-align: left
}

.aw-column-2 {
	width: 150px;
	cursor: pointer;
	text-align: left
}

.aw-column-1,.aw-column-3 {
	width: 80px;
	cursor: pointer;
	text-align: center
}

.aw-column-4,.aw-column-5,.aw-column-7 {
	width: 70px;
	text-align: right;
}

.aw-column-8,.aw-column-9 {
	width: 70px;
	text-align: center;
}

.aw-column-10 {
	width: 120px;
}
</style>

<script language="javascript" type="text/javascript">
 function search_sheet(){
	 
	    setLoading(true);
		var parms = new Array();
		parms.push("action=netorderhz");

		if ($("txt_dccode").value != undefined && $("txt_dccode").value != "") {
			parms.push("dccode=" + $("txt_dccode").value);
		}
		if ($("txt_logistics").value != undefined && $("txt_logistics").value != "") {
			parms.push("logistics=" + $("txt_logistics").value);
		}
		if ($("txt_request_date_min").value != undefined
				&& $("txt_request_date_min").value != "") {
			parms.push("request_date_min=" + $("txt_request_date_min").value);
		}
		if ($("txt_request_date_max").value != undefined
				&& $("txt_request_date_max").value != "") {
			parms.push("request_date_max=" + $("txt_request_date_max").value);
		}
		if ($("txt_order_serial").value != undefined && $("txt_order_serial").value != "") {
			parms.push("order_serial=" + $("txt_order_serial").value);
		}
		if ($("txt_floor").value != undefined && $("txt_floor").value != "") {
			parms.push("floor=" + $("txt_floor").value);
		}
		if ($("txt_supplier_no").value != undefined && $("txt_supplier_no").value != "") {
			parms.push("supplier_no=" + $("txt_supplier_no").value);
		}
		if ($("txt_flag").value != undefined && $("txt_flag").value != "") {
			parms.push("flag=" + $("txt_flag").value);
		}
		
		var url = "../DaemonNetOrder?" + parms.join('&');
		var table = new AW.XML.Table;
		table.setURL(url);
		table.setTable("xdoc/xout/netorderhz");
		table.setRows("row");
		table.request();
		table.response = function(text) {
			table.setXML(text);
			var xcode = table.getErrCode();
			if (xcode != '0') {// 处理xml中的错误消息
				alert(xcode + table.getErrNote());
			} else {
				$("div_report").innerHTML = getGrid(table);
			}
			setLoading(false);
		};
 }
 
 function getGrid(table) {
		var row_count = table.getCount();
		if (row_count == 0)
			return "没有记录";
		var grid = new AW.UI.Grid;

		table.setColumns([ "request_date","supplier_no","vendername","time","yynum","yyds","order_serial","pkgqty","logistics","floor","rgst_date"]);
		var columnNames = ["预约送货日期", "供应商编码", "供应商名称", "预约时段","预约次数", "预约单数","预约流水号","箱数","物流模式","楼层","VSS预约时间" ];
		grid.setId("grid");
		grid.setColumnCount(columnNames.length);
		grid.setRowCount(row_count);
		grid.setHeaderText(columnNames);
		grid.setSelectorVisible(true);
		grid.setSelectorWidth(30);
		grid.setSelectorText(function(i) {
			return this.getRowPosition(i) + 1;
		});
		grid.setSelectionMode("single-row");
		grid.setCellModel(table);

		for ( var i = 0; i < row_count; i++) {

			if (grid.getCellText(8, i) == 1) {
				lable = "直送";
				grid.setCellText(lable,8, i);
			}
			if (grid.getCellText(8, i) == 2) {
				lable = "直通";
				grid.setCellText(lable,8, i);
			} else if (grid.getCellText(8, i) == 3) {
				lable = "配送";
				grid.setCellText(lable,8, i);
			}
		}
		grid.refresh();
		// 数据统计信息
		var totalCount = table.getXMLText("xdoc/xout/netorderhz/totalCount");
		var htmlCountInfo = " 共显示" + row_count + "行。<br>";
		if (row_count < totalCount) {
			var htmlCountInfo = "记录总数" + totalCount + "行；当前显示" + row_count
					+ "行。请尝试修改查询条件查看未显示数据<br>";
		}
		return htmlCountInfo + grid.toString();
	}
 
 function downExcel(){

	 	var parms = new Array();
	 	parms.push("action=netorderhzExcel");

		if ($("txt_dccode").value != undefined && $("txt_dccode").value != "") {
			parms.push("dccode=" + $("txt_dccode").value);
		}
		if ($("txt_logistics").value != undefined && $("txt_logistics").value != "") {
			parms.push("logistics=" + $("txt_logistics").value);
		}
		if ($("txt_request_date_min").value != undefined
				&& $("txt_request_date_min").value != "") {
			parms.push("request_date_min=" + $("txt_request_date_min").value);
		}
		if ($("txt_request_date_max").value != undefined
				&& $("txt_request_date_max").value != "") {
			parms.push("request_date_max=" + $("txt_request_date_max").value);
		}
		if ($("txt_order_serial").value != undefined && $("txt_order_serial").value != "") {
			parms.push("order_serial=" + $("txt_order_serial").value);
		}
		if ($("txt_floor").value != undefined && $("txt_floor").value != "") {
			parms.push("floor=" + $("txt_floor").value);
		}
		if ($("txt_supplier_no").value != undefined && $("txt_supplier_no").value != "") {
			parms.push("supplier_no=" + $("txt_supplier_no").value);
		}
		if ($("txt_flag").value != undefined && $("txt_flag").value != "") {
			parms.push("flag=" + $("txt_flag").value);
		}
		var url = "../DaemonNetOrder?" + parms.join('&');
		alert( '下载数据需要一段时间，按下确定后请耐心等候。' );	
		window.location.href = url;
	}
</script>
</head>
<body>
	<div id="title">供应商网上预约汇总查询</div>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr style="CURSOR: hand" class="tableheader" title="展开/折叠"
			onclick="ToggleTableHeader(0,7);">
			<td class="tableheader">DC</td>
			<td class="tableheader">预约流水号(可选)</td>
			<td class="tableheader">预约送货日期(可选)</td>
			<td class="tableheader">物流模式(可选)</td>
			<td class="tableheader">楼层(可选)</td>
			<td class="tableheader">供应商编码(可选)</td>
			<td class="tableheader">标志(可选)</td>
		</tr>
		<tr id="header0_toggle" style="DISPLAY: block">
			<td class="altbg2"><%=shop%></td>
			<td class="altbg2"><input type="text" name="txt_order_serial"
				id="txt_order_serial" size="16" /></td>
			<td class="altbg2"><input type="text" id="txt_request_date_min"
				class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_request_date_max\',{d:-60});}',maxDate:'#F{$dp.$D(\'txt_request_date_max\',{d:0});}'})"
				name="txt_parms" alt="最小日期" /> - <input type="text"
				id="txt_request_date_max" class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_request_date_min\',{d:0});}',maxDate:'#F{$dp.$D(\'txt_request_date_min\',{d:60});}'})"
				name="txt_parms" alt="最大日期" /></td>
			<td class="altbg2"><select id="txt_logistics">
					<option>全部</option>
					<option value="1">直送</option>
					<option value="2">直通</option>
			</select></td>
			<td class="altbg2"><input type="text" name="txt_floor"
				id="txt_floor" size="16" /></td>
			<td class="altbg2"><input type="text" name="txt_supplier_no"
				id="txt_supplier_no" size="16" /></td>
			<td class="altbg2"><select id="txt_flag">
					<option value="Y">已预约</option>
					<option value="N">已取消</option>
			</select></td>
		</tr>
		<tr id="header1_toggle" style="DISPLAY: block" class="singleborder">
			<td colspan="7"></td>
		</tr>
		<tr id="header2_toggle" style="DISPLAY: block" class="whiteborder">
			<td colspan="7"></td>
		</tr>
		<tr>
			<td><script type="text/javascript">
			var button = new AW.UI.Button;
			button.setControlText("查询");
			button.setControlImage("search");		
			document.write(button);
			button.onClick = search_sheet;
		</script> &rarr;<a href="javascript:downExcel()">将查询结果导出到Excel文档</a></td>
		</tr>
	</table>
	<br />
	<div id="div_report"></div>
</body>
</html>