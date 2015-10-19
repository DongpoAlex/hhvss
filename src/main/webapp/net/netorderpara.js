//浏览所有日期设置
function search(){
	setLoading( true );
	var parms = new Array();
	parms.push("action=netparam_list");
	if($("txt_dccode").value != undefined && $("txt_dccode").value!=""){
		parms.push("dccode="+$("txt_dccode").value);
	}
	var url  = "../DaemonNetOrderParam?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/netnetorderparam");
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

	table.setColumns(["dccode","isyesps", "ordertime", "orderkfts", "stoporderdate", "orderlastdate", "dzsku","dzpqty","ordernote","upper", "uppdate", "inputer", "inputdate","bbbb","aaa"]);	
	var columnNames = ["DC编码","是否允许非配送日预约","预约送货时间点","限制天数","暂停预约日期","最晚时间点","大宗SKU","大宗箱数","预约备注","修改人","修改时间","录入人","录入时间","",""];	
	grid.setId( "grid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	grid.setSelectorVisible(true);
	grid.setSelectorWidth(30);
	grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
	
	grid.onCellClicked = function(event, column, row){
		var dccode = grid.getCellValue(0,row);
		if(column==13){
				window.open("./netorderpara_edit.jsp?dccode="+dccode);
		}else if(column==14){
			    del(dccode);
		}
	};
	grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	
	for( var i=0; i<row_count; i++ ){
		lable = ( grid.getCellText(1,i)=="Y" ) ? "是" : "否";
		grid.setCellText( lable,1, i );
	}
	
	grid.setCellText('修改',13);
	grid.setCellText('删除',14);
	
	//数据统计信息
	var totalCount = table.getXMLText("xdoc/xout/netnetorderparam/totalCount");
	var htmlCountInfo = " 共显示"+row_count+"行。<br>";
	if(row_count<totalCount){
		var htmlCountInfo = "记录总数"+totalCount+"行；当前显示"+row_count+"行。请尝试修改查询条件查看未显示数据<br>";
	}
	return htmlCountInfo+grid.toString();
}
function del(dccode){
	if(!confirm("该操作将删除文字信息，确认删除？")){
		return;
	}
	var table = new AW.XML.Table;
	var parms = new Array();
	parms.push("action=del");
	parms.push("dccode="+dccode);
	setLoading(true);
	var url = "../DaemonNetOrderParam?"+parms.join('&');
	table.setURL(url);
	table.setTable("xdoc/xout/netnetorderparam");
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
	 window.open("./netorderpara_add.jsp");
}