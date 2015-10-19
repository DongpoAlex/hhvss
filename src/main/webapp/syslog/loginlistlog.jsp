<%@ page language="java" contentType="text/html; charset=UTF-8"
	session="false" pageEncoding="UTF-8"
	import="com.royalstone.security.Permission"
	import="com.royalstone.security.Token"
	import="com.royalstone.util.PermissionException"
	errorPage="../errorpage/errorpage.jsp"%>
<%
    final int moduleid = 9000020;
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
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>供应商登陆查询</title>
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

<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../css/main.css" rel="stylesheet" type="text/css" />
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
	background-color: #F9F8F4;
	height: 100%;
	width: 100%;
}
</style>


<script type="text/javascript">
        jQuery(function () {
            var date = new Date();
            jQuery(".datatext").val(date).datepicker(
                    {
                        dateFormat: 'yy-mm-dd', //日期格式，自己设置
                        clearText: '清除',//下面的就不用详细写注释了吧，呵呵，都是些文本设置
                        closeText: '关闭',
                        prevText: '前一月',
                        nextText: '后一月',
                        currentText: ' 请选择日期',
                        monthNames: [ '1月', '2月', '3月', '4月', '5月', '6月', '7月',
                            '8月', '9月', '10月', '11月', '12月' ]
                    });
        });
		function print_log(){
			var url_search = cookParms("print");
			window.location.href=url_search;			
		}
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
            var hegi=jQuery(document).height();
            jQuery("#div_report").css("height",hegi);

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
            var columnNames = [ "经营公司", "登陆帐号", "供应商名称", "收费标准", "收费金额" ];
            table.setColumns(["controltype", "loginid", "vendername", "dje", "sunmje" ]);

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
            var moduleid = 102;
            var logdate_min = jQuery("#txt_logdate_min").val();
            var logdate_max = jQuery("#txt_logdate_max").val();
            var jygs        =jQuery("#jygs").val();
            var parms = new Array();

            if (moduleid != '')
                parms.push("moduleid=" + moduleid);
            if (logdate_min != '')
                parms.push("logdate_min=" + logdate_min);
            if (logdate_max != '')
                parms.push("logdate_max=" + logdate_max);
            if (jygs != '' )
                parms.push("jygs=" + jygs);
            parms.push("operation=" + operation);

            var url = "../DaemonReportLoginlog?" + parms.join('&');
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
            courier.reader.read = function (text) {
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
		style="font-size: 16px; font-family: '楷体_GB2312, 黑体'; color: #42658D; font-weight: bold">供应商登陆明细</div>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr class="tableheader">
			<th class="tableheader">日期范围</th>
			<th class="tableheader">经营公司</th>
			<th class="tableheader">操作</th>
		</tr>
		<tr id="header0_toggle">
			<td class="altbg2">从<input type="date" class="datatext"
				name="txt_logdate_min" id="txt_logdate_min" onblur="checkDate(this)"
				onchange="on_date_min_change()" /> 到<input type="date"
				class="datatext" name="txt_logdate_max" id="txt_logdate_max"
				onblur="checkDate(this)" />
			</td>
			<td class="altbg2"><select id="jygs" name="jygs">
					<option value="">全部</option>
					<option value="2">宝商集团</option>
					<option value="3">民生家乐</option>
			</select></td>
			<td class="altbg2"><script type="text/javascript">
                var button = new AW.UI.Button;
                button.setControlText("查询");
                button.setId("btn_search");
                button.setControlImage("search");
                document.write(button);
                button.onClick = search_log;
            </script> <script type="text/javascript">
                var button = new AW.UI.Button;
                button.setControlText("导出");
                button.setId("btn_print");
                button.setControlImage("search");
                document.write(button);
                button.onClick = print_log;
            </script></td>
		</tr>
	</table>
	<br />

	<div id="div_report"></div>
</body>
</html>
