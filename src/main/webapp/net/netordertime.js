//浏览所有日期设置
function search(){
	setLoading( true );
	var parms = new Array();
	parms.push("action=getordertiemList");
	
	if($("txt_dccode").value != undefined && $("txt_dccode").value!=""){
		parms.push("dccode="+$("txt_dccode").value);
	}
	var url  = "../DaemonNetOrderTime?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/netnetordertime");
	table.setRows("row");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			$("div_result").innerHTML = getGrid(table);
		}
		setLoading( false );
	};
}

function getGrid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	var grid = new AW.UI.Grid;

	table.setColumns(["dccode","logistics","starttime","endtime","timejg","maxsku","maxxs","maxsupply","maxdzsupply","maxyssupply","note","aaa","bbb"]);	
	var columnNames = ["DC编码","物流模式","开始时间","结束时间","时间间隔","SKU数上限","箱数上限","供应商个数上限","大宗个数上限","综合个数上限","备注","",""];	
	grid.setId( "grid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	grid.setSelectorVisible(true);
	grid.setSelectorWidth(30);
	grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
	
	grid.onCellClicked = function(event, column, row){
		var dccode = grid.getCellValue(0,row);
		var logistics = grid.getCellValue(1,row);
		var logisticsname = grid.getCellText(1,row);
		var starttime =  grid.getCellValue(2,row);
		var endtime =grid.getCellValue(3,row);
		if(column==11){
				window.open("./netordertime_edit.jsp?dccode="+dccode+"&logistics="+logistics+"&starttime="+starttime+"&endtime="+endtime+"&logisticsname="+logisticsname);
		}else if(column==12){
			    del(dccode,logistics,starttime,endtime);
		}
	};
	grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	grid.setCellText('修改',11);
	grid.setCellText('删除',12);
	for ( var i = 0; i < row_count; i++) {
		var logistics = grid.getCellValue(1,i);
		grid.setCellText(logistics==1?'直送':'直通',1,i);
	}
	
	//数据统计信息
	var totalCount = table.getXMLText("xdoc/xout/netnetordertime/totalCount");
	var htmlCountInfo = " 共显示"+row_count+"行。<br>";
	if(row_count<totalCount){
		var htmlCountInfo = "记录总数"+totalCount+"行；当前显示"+row_count+"行。请尝试修改查询条件查看未显示数据<br>";
	}
	return htmlCountInfo+grid.toString();
}
function del(dccode,logistics,starttime,endtime){
	if(!confirm("该操作将删除文字信息，确认删除？")){
		return;
	}
	var table = new AW.XML.Table;
	var parms = new Array();
	parms.push("action=del");
	parms.push("dccode="+dccode);
	parms.push("logistics="+logistics);
	parms.push("starttime="+starttime);
	parms.push("endtime="+endtime);
	setLoading(true);
	var url = "../DaemonNetOrderTime?"+parms.join('&');
	table.setURL(url);
	table.setTable("xdoc/xout/netnetordertime");
	table.setRows("row");
	table.request();
	table.response = function (text) {
		setLoading(false);
		table.setXML(text);
		var xcode = table.getXMLText("xdoc/xerr/code");
		if (xcode != 0) {
			alert(xcode + table.getXMLText("xdoc/xerr/note"));
		} else {
			 alert("删除成功！");
			 search();
		}
	};
}
function add(){
	 window.open("./netordertime_add.jsp");
}