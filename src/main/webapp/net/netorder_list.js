var init = function(order_serial,dccode){
	var ln = $('txt_logistics').innerText;
	var logistics = 1;
	if(ln=='直通'){
		logistics=2;
	}
	setLoading( true );
	var parms = new Array();
	parms.push("action=netordershDetail");
	parms.push("order_serial="+order_serial);
	parms.push("dccode="+dccode);
	parms.push("logistics="+logistics);
	var url = "../DaemonNetOrder?" + parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/netnetordershdetail");
	table.setRows("row");
	table.request();
	table.response = function(text) {
		table.setXML(text);
		var xcode = table.getErrCode();
		if (xcode != '0') {// 处理xml中的错误消息
			alert(xcode + table.getErrNote());
		} else {
			$("div_detail").innerHTML = getGrid(table);
		}
		setLoading(false);
	};
};
function getGrid(table) {
	var row_count = table.getCount();
	if (row_count == 0)
		return "没有记录";
	var grid = new AW.UI.Grid;

	table.setColumns([ "order_serial", "dccode", "po_no", "po_type",
			"pkgnum", "skunum", "rgst_date", "rgst_name", "upper",
			"uppdate", "note" ]);
	var columnNames = [ "预约流水号", "DC编码", "通知单号", "采购类型", "商品箱数", "SKU个数",
			"建立日期", "建立人员", "修改人员", "修改日期", "备注" ];
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
		var po_no = grid.getCellValue(2, row);
		if (column == 2) {
			window.open("./netorderitem_list.jsp?po_no=" + po_no);
		} 
	};
	//grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	for ( var i = 0; i < row_count; i++) {

		if (grid.getCellText(3, i) == 1) {
			lable = "直送";
			grid.setCellText(lable, 3, i);
		}
		if (grid.getCellText(3, i) == 2) {
			lable = "直通";
			grid.setCellText(lable, 3, i);
		} else if (grid.getCellText(3, i) == 3) {
			lable = "配送";
			grid.setCellText(lable, 3, i);
		}

	}
	grid.refresh();
	// 数据统计信息
	var totalCount = table.getXMLText("xdoc/xout/netnetordershdetail/totalCount");
	var htmlCountInfo = " 共显示" + row_count + "行。<br>";
	if (row_count < totalCount) {
		var htmlCountInfo = "记录总数" + totalCount + "行；当前显示" + row_count
				+ "行。请尝试修改查询条件查看未显示数据<br>";
	}
	return htmlCountInfo + grid.toString();
}