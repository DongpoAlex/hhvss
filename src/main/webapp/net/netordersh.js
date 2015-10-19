//浏览所有预约信息
function search() {
	setLoading(true);
	var parms = new Array();
	parms.push("action=netordersh_list");

	if ($("txt_dccode").value != undefined && $("txt_dccode").value != "") {
		parms.push("dccode=" + $("txt_dccode").value);
	}
	if ($("txt_logistics").value != undefined && $("txt_logistics").value != "") {
		parms.push("logistics=" + $("txt_logistics").value);
	}
	if ($("txt_supplier_no").value != undefined
			&& $("txt_supplier_no").value != "") {
		parms.push("supplier_no=" + $("txt_supplier_no").value);
	}
	if ($("txt_request_date_min").value != undefined
			&& $("txt_request_date_min").value != "") {
		parms.push("request_date_min=" + $("txt_request_date_min").value);
	}
	if ($("txt_request_date_max").value != undefined
			&& $("txt_request_date_max").value != "") {
		parms.push("request_date_max=" + $("txt_request_date_max").value);
	}
	if ($("txt_flag").value != undefined && $("txt_flag").value != "") {
		parms.push("flag=" + $("txt_flag").value);
	}
	var url = "../DaemonNetOrder?" + parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/netnetordersh");
	table.setRows("row");
	table.request();
	table.response = function(text) {
		table.setXML(text);
		var xcode = table.getErrCode();
		if (xcode != '0') {// 处理xml中的错误消息
			alert(xcode + table.getErrNote());
		} else {
			$("div_result").innerHTML = getGrid(table);
		}
		setLoading(false);
	};
}

function getGrid(table) {
	var row_count = table.getCount();
	if (row_count == 0)
		return "没有记录";
	var grid = new AW.UI.Grid;

	table.setColumns([ "order_serial", "dccode", "logistics", "supplier_no",
			"vendername", "request_date", "start_time", "end_time", "flag",
			"rgst_name", "rgst_date", "updt_name", "updt_date", "note", "bbbb",
			"aaa","ccc" ]);
	var columnNames = [ "预约流水号", "DC编码", "物流模式", "供应商编码", "供应商名称", "预约日期",
			"开始时间", "结束时间", "标志", "建立人员", "建立日期", "更新人员", "更新日期", "备注", "", "","" ];
	grid.setId("grid");
	grid.setColumnCount(columnNames.length);
	grid.setRowCount(row_count);
	grid.setHeaderText(columnNames);
	grid.setSelectorVisible(true);
	grid.setSelectorWidth(30);
	grid.setSelectorText(function(i) {
		return this.getRowPosition(i) + 1;
	});

	grid.onCellClicked = function(event, column, row) {
		var order_serial = grid.getCellValue(0, row);
		var dccode = grid.getCellValue(1, row);
		var logistics = grid.getCellValue(2, row);
		var supplier_no = grid.getCellValue(3, row);
		var vendername = grid.getCellValue(4, row);
		var request_date = grid.getCellValue(5, row);
		var start_time = grid.getCellValue(6, row);
		var end_time = grid.getCellValue(7, row);
		var flag = grid.getCellValue(8, row);
		var note = grid.getCellValue(13, row);
		if (column == 14) {
			window.open("./netorder_list.jsp?order_serial=" + order_serial
					+ "&dccode=" + dccode + "&logistics=" + logistics
					+ "&supplier_no=" + supplier_no + "&vendername="
					+ vendername + "&request_date=" + request_date
					+ "&start_time=" + start_time + "&end_time=" + end_time
					+ "&flag=" + flag + "&note=" + note);
		} else if (column == 15) {
			if(flag=="Y"){
				window.open("./netordersh_edit.jsp?order_serial=" + order_serial
						+ "&dccode=" + dccode);
			}else{
				alert("已取消的预约单不能修改!!!");
			}
			
		}else if(column == 16){
			cancelorder(order_serial,dccode,flag);
		}
	};
	grid.setSelectionMode("single-row");
	grid.setCellModel(table);

	for ( var i = 0; i < row_count; i++) {

		if (grid.getCellText(2, i) == 1) {
			lable = "直送";
			grid.setCellText(lable, 2, i);
		}
		if (grid.getCellText(2, i) == 2) {
			lable = "直通";
			grid.setCellText(lable, 2, i);
		} else if (grid.getCellText(2, i) == 3) {
			lable = "配送";
			grid.setCellText(lable, 2, i);
		}
		lable = (grid.getCellText(8, i) == "Y") ? "已预约" : "已取消";
		grid.setCellText(lable, 8, i);

	}
	grid.refresh();
	grid.setCellText('查看明细', 14);
	grid.setCellText('修改预约', 15);
	grid.setCellText('取消预约', 16);

	// 数据统计信息
	var totalCount = table.getXMLText("xdoc/xout/netnetordersh/totalCount");
	var htmlCountInfo = " 共显示" + row_count + "行。<br>";
	if (row_count < totalCount) {
		var htmlCountInfo = "记录总数" + totalCount + "行；当前显示" + row_count
				+ "行。请尝试修改查询条件查看未显示数据<br>";
	}
	return htmlCountInfo + grid.toString();
}

function add() {
	 window.open("./netordersh_add.jsp");
}

//取消预约
function cancelorder(order_serial,dccode,flag){
	if(flag=="N"){
		alert("此预约单已取消！！");
		return;
	}
	if(!confirm("该操作将取消此预约，确认取消？")){
		return;
	}
	var table = new AW.XML.Table;
	var parms = new Array();
	parms.push("action=cancelorder");
	parms.push("order_serial="+order_serial);
	parms.push("dccode="+dccode);
	setLoading(true);
	var url = "../DaemonNetOrder?"+parms.join('&');
	table.setURL(url);
	table.setTable("xdoc/xout/cancelorder");
	table.setRows("row");
	table.request();
	table.response = function (text) {
		setLoading(false);
		table.setXML(text);
		var xcode = table.getXMLText("xdoc/xerr/code");
		if (xcode != 0) {
			alert(xcode + table.getXMLText("xdoc/xerr/note"));
		} else {
			 alert("取消成功！");
			 search();
		}
	};
	
}




