var init = function(po_no){
	setLoading( true );
	var parms = new Array();
	parms.push("action=netorderpo");
	parms.push("po_no="+po_no);
	var url = "../DaemonNetOrder?" + parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/netnetorderpo");
	table.setRows("row");
	table.request();
	table.response = function(text) {
		table.setXML(text);
		var xcode = table.getErrCode();
		if (xcode != '0') {// 处理xml中的错误消息
			alert(xcode + table.getErrNote());
		}else{
			$('txt_sheetid').innerText=table.getXMLText( "/xdoc/xout/netnetorderpo/row/sheetid");
			$('txt_orderdate').innerText=table.getXMLText( "/xdoc/xout/netnetorderpo/row/orderdate" );
			$('txt_validdays').innerText=table.getXMLText( "/xdoc/xout/netnetorderpo/row/validdays" )+"天";
			$('txt_venderid').innerText=table.getXMLText( "/xdoc/xout/netnetorderpo/row/venderid" );
			$('txt_vendername').innerText=table.getXMLText( "/xdoc/xout/netnetorderpo/row/vendername" );
			$('txt_vdeliverdate').innerText=table.getXMLText( "/xdoc/xout/netnetorderpo/row/vdeliverdate" );
			$('txt_deadline').innerText=table.getXMLText( "/xdoc/xout/netnetorderpo/row/deadline" );
			$('txt_shopid').innerText=table.getXMLText( "/xdoc/xout/netnetorderpo/row/destshopid" ) +" | "+ table.getXMLText( "/xdoc/xout/netnetorderpo/row/shopname" );
			if(table.getXMLText( "/xdoc/xout/netnetorderpo/row/logistics")=="1"){
				$('txt_logistics').innerText = "直送";
			}else if(table.getXMLText( "/xdoc/xout/netnetorderpo/row/logistics")=="2"){
				$('txt_logistics').innerText = "直通";
			}else if (table.getXMLText( "/xdoc/xout/netnetorderpo/row/logistics")=="3"){
				$('txt_logistics').innerText = "配送";
			};
			$('txt_paytypename').innerText=table.getXMLText( "/xdoc/xout/netnetorderpo/row/paytypeid" )+" | "+table.getXMLText( "/xdoc/xout/netnetorderpo/row/paytypename" );
			$('txt_refsheetid').innerText=table.getXMLText( "/xdoc/xout/netnetorderpo/row/refsheetid" );
		}
		setLoading(false);
	};
	
	setLoading( true );
	var parms = new Array();
	parms.push("action=netorderpoitem");
	parms.push("po_no="+po_no);
	var url = "../DaemonNetOrder?" + parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/netnetorderpoitem");
	table.setRows("row");
	table.request();
	table.response = function(text) {
		table.setXML(text);
		var xcode = table.getErrCode();
		if (xcode != '0') {// 处理xml中的错误消息
			alert(xcode + table.getErrNote());
		} else {
			$("div_poitem").innerHTML = getGridDetail(table);
		}
		setLoading(false);
	};
};

//加载订单商品明细
function getGridDetail(table) {
	var row_count = table.getCount();
	if (row_count == 0)
		return "没有记录";
	var grid = new AW.UI.Grid;

	table.setColumns(["goodsid", "goodsname", "barcode",
			"qty", "pkgqty", "pkgvolume", "concost","firstdisc", "memo" ]);
	var columnNames = ["商品编码","商品名称", "商品条码", "订货数", "订货箱数", "箱规格","订货成本", "折扣率", "备注" ];
	grid.setId("grid");
	grid.setColumnCount(columnNames.length);
	grid.setRowCount(row_count);
	grid.setHeaderText(columnNames);
	grid.setSelectorVisible(true);
	grid.setSelectorWidth(30);
	grid.setSelectorText(function(i) {
		return this.getRowPosition(i) + 1;
	});
	
	//grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	grid.refresh();
	// 数据统计信息
	var totalCount = table.getXMLText("xdoc/xout/netnetorderpoitem/totalCount");
	var htmlCountInfo = " 共显示" + row_count + "行。<br>";
	if (row_count < totalCount) {
		var htmlCountInfo = "记录总数" + totalCount + "行；当前显示" + row_count
				+ "行。请尝试修改查询条件查看未显示数据<br>";
	}
	return htmlCountInfo + grid.toString();
}