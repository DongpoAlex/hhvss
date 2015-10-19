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

	table.setColumns([ "order_serial", "dccode", "shopname","logisticsname","request_date", "start_time", "end_time", "flag",
			"rgst_date", "updt_date", "note", "bbbb", "aaa","ccc" ]);
	var columnNames = [ "预约流水号", "DC编码","DC名称", "物流模式","预约日期","开始时间", "结束时间", "标志", "建立时间", "更新时间", "备注", "", "","" ];
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
		var shopname = grid.getCellValue(2, row);
		var logistics = grid.getCellValue(3, row);
		var request_date = grid.getCellValue(4, row);
		var start_time = grid.getCellValue(5, row);
		var end_time = grid.getCellValue(6, row);
		var flag = grid.getCellValue(7, row);
		var note = grid.getCellValue(10, row);

		if (column == 0) {
			window.open("./netorder_list.jsp?order_serial=" + order_serial
					+ "&dccode=" + dccode + "&logistics=" + logistics
					 + "&request_date=" + request_date
					+ "&start_time=" + start_time + "&end_time=" + end_time
					+ "&flag=" + flag + "&note=" + note+"&shopname="+shopname);
		} 
		
		if (column == 11) {
			//检查是否操作最晚操作时间
			setLoading( true );
			var parms = new Array();
			parms.push("action=netparam_lastdate");
			parms.push("dccode="+dccode);
			var url  = "../DaemonNetOrderParam?"+parms.join('&');
			var table = new AW.XML.Table;
			table.setURL(url);
			table.request();
			table.response = function(text){
				table.setXML(text);
				var xcode = table.getErrCode();
				if( xcode != '0' ){//处理xml中的错误消息
					alert( xcode+table.getErrNote());
				}else{
					var lastdate = table.getXMLText("/xdoc/xout/netparamlastdate/row/orderlastdate");
					var curentdate =  table.getXMLText("/xdoc/xerr/time");
					curentdate = curentdate.substr(11,19);
					var currenttime = parseInt(curentdate.replace(':',""),10);
					
					var lasttime = parseInt(lastdate.replace(':',""));
					
					var requestdate = parseInt((request_date.replace('-','')).replace('-',''));
					
					if(currenttime>lasttime){
						alert("已超过预约操作限制时间,请第二天提前预约!!");
					}else if (requestdate<currentdate){
						alert("加单、减单必须在预约收货日期的前一天以前，现在不允许修改!!");
					}else{
						if(flag=="Y"){
							window.open("./netordervender_edit.jsp?order_serial=" + order_serial
									+ "&dccode=" + dccode);
						}else{
							alert("已取消的预约单不能修改!!!");
						}
					}
				}
				setLoading( false );
			};
		}else if(column == 12){
			//检查是否操作最晚操作时间
			setLoading( true );
			var parms = new Array();
			parms.push("action=netparam_lastdate");
			parms.push("dccode="+dccode);
			var url  = "../DaemonNetOrderParam?"+parms.join('&');
			var table = new AW.XML.Table;
			table.setURL(url);
			table.request();
			table.response = function(text){
				table.setXML(text);
				var xcode = table.getErrCode();
				if( xcode != '0' ){//处理xml中的错误消息
					alert( xcode+table.getErrNote());
				}else{
					var lastdate = table.getXMLText("/xdoc/xout/netparamlastdate/row/orderlastdate");
					var mydate = new Date();
					var h = mydate.getHours();
					var m = mydate.getMinutes();
					var lasttime = parseInt(lastdate.replace(':',""));
					var currenthm =  h*100+m;
					var currentdate = mydate.getFullYear()*10000+(mydate.getMonth()+1)*100+(mydate.getDate()+1);
					var requestdate = parseInt((request_date.replace('-','')).replace('-',''));
					if(currenthm>lasttime){
						alert("已超过预约操作限制时间,请第二天提前预约!!");
					}else if (requestdate<currentdate){
						alert("取消预约单必须在预约收货日期的前一天以前，现在不允许取消!!");
					}else{
						cancelorder(order_serial,dccode,flag,request_date);
					}
				}
				setLoading( false );
			};
		}
	};
	//grid.setSelectionMode("single-row");
	grid.setCellModel(table);

	for ( var i = 0; i < row_count; i++) {
		lable = (grid.getCellText(7, i) == "Y") ? "已预约" : "已取消";
		grid.setCellText(lable, 7, i);

	}
	grid.refresh();
	grid.setCellText('修改预约', 11);
	grid.setCellText('取消预约', 12);

	// 数据统计信息
	var totalCount = table.getXMLText("xdoc/xout/netnetordersh/totalCount");
	var htmlCountInfo = " 共显示" + row_count + "行。<br>";
	if (row_count < totalCount) {
		var htmlCountInfo = "记录总数" + totalCount + "行；当前显示" + row_count
				+ "行。请尝试修改查询条件查看未显示数据<br>";
	}
	return htmlCountInfo + grid.toString();
}
//获取参数信息
function getParam(dccode){
	setLoading( true );
	var parms = new Array();
	parms.push("action=netparam_lastdate");
	parms.push("dccode="+dccode);
	var url  = "../DaemonNetOrderParam?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			lastdate = table.getXMLText("/xdoc/xout/netparamlastdate/row/orderlastdate");
		}
		setLoading( false );
	};
	
	
}

function add() {
	 window.open("./netordervender_add.jsp");
}

//取消预约
function cancelorder(order_serial,dccode,flag,request_date){
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
	parms.push("request_date="+request_date);
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




