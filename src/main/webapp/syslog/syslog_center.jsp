<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.Permission"
	import="com.royalstone.security.Token"
	import="com.royalstone.util.PermissionException"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	final int moduleid = 9020101;
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

<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>系统操作日志</title>
<script src="../js/ajax.js" type="text/javascript"></script>
<script src="../js/XErr.js" type="text/javascript"></script>
<script src="../js/Date.js" type="text/javascript"></script>
<script src="../js/Number.js" type="text/javascript"></script>
<!-- ActiveWidgets stylesheet and scripts -->
<script src="/resources/script/jquery-1.11.1.min.js"></script>

<script
	src="/resources/jquery-ui-1.10.4.custom/js/jquery-ui-1.10.4.custom.min.js"></script>

<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet"
	href="/resources/jquery-ui-1.10.4.custom/css/smoothness/jquery-ui-1.10.4.custom.min.css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css" />
<!-- grid format -->
<style type="text/css">
.aw-grid-control {
	height: 100%;
	width: 100%;
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
	width: 50px;
}

#myGrid .aw-column-2 {
	width: 130px;
}

#myGrid .aw-column-6 {
	width: 150px;
}

#myGrid .aw-column-7 {
	width: 330px;
}
</style>


<script type="text/javascript">
	jQuery(function() {
		jQuery(".datatext").datepicker(
				{
					dateFormat : 'yy-mm-dd', //日期格式，自己设置
					clearText : '清除',//下面的就不用详细写注释了吧，呵呵，都是些文本设置
					closeText : '关闭',
					prevText : '前一月',
					nextText : '后一月',
					currentText : ' 请选择日期',
					monthNames : [ '1月', '2月', '3月', '4月', '5月', '6月', '7月',
							'8月', '9月', '10月', '11月', '12月' ]
				});
	});

	function search_log() {
		setLoading(true);
		var url_search = cookParms("browse");
		var courier = new AjaxCourier(url_search);
		courier.reader.read = showSyslog;
		courier.call();

	}

	function showSyslog(text) {
		setLoading(false);
		$("island4result").loadXML(text);
		$("div_report").innerHTML = getGrid();
	}

	function getGrid() {
		//错误检测
		var elm_err = $("island4result").XMLDocument
				.selectSingleNode("/xdoc/xerr");
		var xerr = parseXErr(elm_err);
		if (xerr.code != "0")
			return xerr.note;

		var node_list = $("island4result").XMLDocument
				.selectSingleNode("/xdoc/xout/report");
		var row_count = node_list.childNodes.length;
		var table = new AW.XML.Table;
		table.setXML(node_list.xml);
		var moduleid = jQuery("#opt_moduleid").val();
		var columnNames = null;
		if (moduleid == 103) {
			table.setColumns([ "controltype", "loginid", "dje", "sunmje" ]);
			columnNames = [ "经营公司(2宝商集团,3民生家乐)", "登陆供应商数", "计算单数", "计算总数" ];
		} else {
			table.setColumns([ "logid", "logdate", "client", "loginid",
					"username", "vendername", "telno", "modulename", "note" ]);
			columnNames = [ "序列号", "日期", "登录IP", "登录ID", "用户名", "供应商名", "联系电话",
					"模块名称", "事件说明" ];
		}
		var grid = new AW.UI.Grid;
		grid.setId("myGrid");
		grid.setColumnCount(columnNames.length);
		grid.setRowCount(row_count);
		grid.setHeaderText(columnNames);

		grid.setCellModel(table);
		grid.setFooterText([ "", "", "记录总数:" + row_count + "条" ]);
		grid.setFooterVisible(true);
		return grid.toString();
	}

	function cookParms(operation) {
		var level = jQuery("#opt_level").val();
		var moduleid = jQuery("#opt_moduleid").val();
		var loginid = jQuery("#txt_loginid").val();
		var logdate_min = jQuery("#txt_logdate_min").val();
		var logdate_max = jQuery("#txt_logdate_max").val();
		var filtrate = jQuery("#filtrate").val();
		var jygs = jQuery("#opt_number").val();
		var parms = new Array();
		if (level != '')
			parms.push("level=" + level);
		if (moduleid != '')
			parms.push("moduleid=" + moduleid);
		if (loginid != '')
			parms.push("loginid=" + loginid);
		if (logdate_min != '')
			parms.push("logdate_min=" + logdate_min);
		if (logdate_max != '')
			parms.push("logdate_max=" + logdate_max);
		if (filtrate != '')
			parms.push("filtrate=" + filtrate);
		if (jygs != '')
			parms.push("opt_number=" + jygs);
		parms.push("operation=" + operation);

		var url = "../DaemonReportSyslog?" + parms.join('&');
		return url;
	}

	function on_date_min_change() {
		if (txt_logdate_max.value == '' && txt_logdate_min.value != '') {
			var logdate = parseDate(txt_logdate_min.value);
			txt_logdate_max.value = logdate.toString();
		}
	}

	function stat() {
		setLoading(true);
		var url_search = cookParms("stat");
		var courier = new AjaxCourier(url_search);
		courier.reader.read = function(text) {
			$("island4result").loadXML(text);
			var node_list = $("island4result").XMLDocument
					.selectSingleNode("/xdoc/xout/report");
			var row_count = node_list.childNodes.length;
			var table = new AW.XML.Table;
			table.setXML(node_list.xml);
			table.setColumns([ "moduleid", "modulename", "count" ]);
			var columnNames = [ "模块号", "模块名称", "数目" ];
			var grid = new AW.UI.Grid;
			grid.setColumnCount(columnNames.length);
			grid.setRowCount(row_count);
			grid.setHeaderText(columnNames);
			grid.setCellModel(table);
			var sum_count = 0;
			for (var i = 0; i < row_count; i++) {
				sum_count += Number(table.getData(2, i));
			}
			grid.setFooterText([ "总计", "", sum_count ]);
			grid.setFooterVisible(true);
			div_report.innerHTML = grid.toString();
			setLoading(false);
		};
		courier.call();

	}
</script>
</head>
<xml id="island4result" />

<body>
	<div id="title" align="center"
		style="font-size: 16px; font-family: '楷体_GB2312, 黑体'; color: #42658D; font-weight: bold";>系统操作日志</div>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader">消息级别</td>
			<td class="tableheader">操作事件</td>
			<td class="tableheader">日期范围(可选)</td>
			<td class="tableheader">计算单数</td>
			<td class="tableheader">登录ID</td>
		</tr>
		<tr id="header0_toggle">
			<td class="altbg2"><select id="opt_level">
					<option value="">全部</option>
					<option value="100" selected="selected">普通</option>
					<option value="600">警告</option>
					<option value="900">错误</option>
			</select></td>
			<td class="altbg2"><select id="opt_moduleid">
					<option value="0">全部</option>
					<option value="102">供应商登陆</option>
					<option value="101">零售商登陆</option>
					<OPTION value="103" selected="selected">供应商登陆统计</OPTION>
			</select></td>
			<td class="altbg2">从<input type="date" class="datatext"
				name="txt_logdate_min" id="txt_logdate_min" onblur="checkDate(this)"
				onchange="on_date_min_change()" /> 到<input type="date"
				class="datatext" name="txt_logdate_max" id="txt_logdate_max"
				onblur="checkDate(this)" />
			</td>
			<td class="altbg2"><input type="text" name="opt_number"
				id="opt_number" value="80" /></td>
			<td class="altbg2"><input type="text" name="txt_loginid"
				id="txt_loginid" /></td>
		</tr>
		<tr id="header1_toggle" class="singleborder">
			<td colspan="4"></td>
		</tr>
		<tr id="header2_toggle" class="whiteborder">
			<td colspan="4"></td>
		</tr>
		<tr id="header3_toggle" class="header">
			<td><script type="text/javascript">
				var button = new AW.UI.Button;
				button.setControlText("查询");
				button.setId("btn_search");
				button.setControlImage("search");
				document.write(button);
				button.onClick = search_log;
			</script></td>
			<td><a href="javascript:stat()">统计查询结果</a></td>
			<td><select id="filtrate">
					<option value="distinct">按人数统计</option>
					<option value="">按人次统计</option>
			</select></td>
			<td></td>
		</tr>
	</table>
	<br />

	<div id="div_report"></div>
</body>
</html>
