﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3090101;
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
	if (!perm.include(Permission.READ))
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:"
				+ moduleid);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>供应商资料维护</title>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../css/venderinfo.css.css" rel="stylesheet"></link>

<script language="javascript" src="../js/ajax.js">
	
</script>
<script language="javascript" src="../js/Date.js">
	
</script>
<script language="javascript" src="../js/Number.js">
	
</script>
<script language="javascript" src="../js/XErr.js">
	
</script>
<script language="javascript" src="../AW/runtime/lib/aw.js">
	
</script>
<script type="text/javascript" src="../js/common.js"></script>

<style type="text/css">
.aw-grid-control {
	height: 70%;
	width: 98%;
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
	width: 80px;
}

#myGrid .aw-column-1 {
	width: 200px;
}

#myGrid



 



.aw-column-2
,
{
width






:






200
px




;
}
#myGrid .aw-column-3 {
	width: 80px;
}

#myGrid .aw-column-4,.aw-column-5 {
	text-align: left;
}

#myGrid .aw-column-8 {
	width: 150px;
}

#title {
	text-align: center;
	color: #42658D;
	font-size: 16px;
	font-family: '楷体_GB2312';
	font-weight: bold;
}
</style>
</head>

<script language="javascript">
	function toUpperCase(ctrl) {
		ctrl.value = ctrl.value.toUpperCase();
	}

	var grid = new AW.Grid.Extended;
	function searchVenderList() {
		var table = new AW.XML.Table;
		var parms = new Array();
		var txt_min = $("txt_min").value;
		var txt_max = $("txt_max").value;

		if (txt_min != '')
			parms.push("v_min=" + $("txt_min").value);
		if (txt_max != '')
			parms.push("v_max=" + $("txt_max").value);

		var url = "../DaemonVenderAdmin?focus=venderdiy&operation=listall&"
				+ parms.join('&');
		table.setURL(url);
		table.setTable("xdoc/xout/rowset");
		table.setRows("row");
		table.request();
		table.response = function(text) {
			setLoading(false);
			table.setXML(text);
			var xcode = table.getXMLText("xdoc/xerr/code");
			if (xcode != 0) {
				alert(xcode + table.getXMLText("xdoc/xerr/note"));
			} else {
				table.setColumns([ "venderid", "vendername", "postaddress",
						"postcode", "contactperson", "telno", "mobileno",
						"note", "updatedate","taxtype","taxname","taxno","taxaddrtel","taxbank" ]);
				var columnNames = [ "供应商编码", "供应商名称", "邮寄地址", "邮政编码", "联系人", "办公电话", "手机", "备注","更新时间" , "纳税人类型","开票名称","纳税识别号","税票地址电话","税票开户行及帐号" ];

				var row_count = table.getCount();
				if (row_count == 0) {
					$('div_list').innerHTML = "没有记录";
				}
				grid.setId("grid_cat");
				grid.setColumnCount(columnNames.length);
				grid.setRowCount(row_count);
				grid.setHeaderText(columnNames);

				grid.setSelectorVisible(true);
				grid.setSelectorWidth(30);
				grid.setSelectorText(function(i) {
					return this.getRowPosition(i) + 1;
				});
				grid.setCellModel(table);
				grid.refresh();
				var sumcount = table.getXMLNode("/xdoc/xout/rowset")
						.getAttribute("row_count");
				$('div_list').innerHTML = "查询结果：总计：" + sumcount + "行， 显示："
						+ row_count + "行" + grid.toString();
			}
		};
		setLoading(true);
	}

	function downExcel() {
		var parms = new Array();
		var txt_min = $("txt_min").value;
		var txt_max = $("txt_max").value;

		if (txt_min != '')
			parms.push("v_min=" + $("txt_min").value);
		if (txt_max != '')
			parms.push("v_max=" + $("txt_max").value);

		var url = "../DaemonDownloadExcel?operation=venderlist&"
				+ parms.join("&");
		url = encodeURI(encodeURI(url));
		window.location.href = url;
	}
</script>
<body onload="">
	<form>
		<fieldset>
			<legend>供应商资料信息查询</legend>
			<table border="0" width="90%">
				<tr>
					<td class="needed" width="10%">供应商编码范围（从小到大）:</td>
					<td><input type="text" id="txt_min" class="input"
						onkeyup="toUpperCase(this)"> -<input type="text"
						id="txt_max" class="input" onkeyup="toUpperCase(this)"></td>
				</tr>
				<tr>
					<td colspan="2" width="45%"><input type="button" value=" 查 询 "
						onclick="searchVenderList()" class="input" name="btninfo">
						<a href="javascript:downExcel();">导出查询结果到Excel</a></td>
					<td colspan="5" width="25%"></td>
				</tr>
			</table>
		</fieldset>
	</form>
	<div id="div_list"></div>
</body>
</html>
