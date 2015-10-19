//初始
window.onload = function(){
	if(g_status!=-999){
		$("txt_flag").value = g_status;
		search();
	}
}




//浏览全部待处理证照
function search(){
	setLoading( true );
	var parms = new Array();
	parms.push("action=getList");
	
	parms.push("ctype="+$("ctype").value);
	parms.push("type="+$("txt_type").value);
	var flag = $("txt_flag").value;
	if(flag==""){
		parms.push("itemflag=uncheck");
	}else{
		parms.push("itemflag="+$("txt_flag").value);
	}
	
	var url  = "../DaemonCertificate?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/list");
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
	}
}

function getGrid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	var grid = new AW.UI.Grid;

//	table.setColumns(["aaaa","bbbb","flag","sheetid","type","venderid","vendertype","vendertypename","contact","ccname","checker","checktime","note"]);	
//	var columnNames = ["查看明细","修改","状态","单号","证照类型","供应商ID","供应商类型","代理厂家名称","联系人","证照品类","审核人","审核时间","备注"];	
	table.setColumns(["aaaa","bbbb","flag","sheetid","type","venderid","vendertype","vendertypename","contact","ccname", "submittime","note"]);	
	var columnNames = ["查看明细","修改","状态","单号","证照类型","供应商ID","供应商类型","代理厂家名称","联系人","证照品类", "最近提交日期","备注"];	
	grid.setId( "grid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	
	grid.setSelectorVisible(true);
	grid.setSelectorWidth(30);
	grid.setSelectorText(function(i){return this.getRowPosition(i)+1});
	
	grid.onCellClicked = function(event, column, row){
		var sheetid = grid.getCellValue(3,row);
		var type =grid.getCellValue(4,row);
		if(column==0){
				window.open("./certificate_show.jsp?sheetid="+sheetid+"&type="+type);
		}
		
		if(column==1){
			var flag = grid.getCellValue(2,row);
			if(flag==0 || flag==99){
				window.open("./certificate_edit.jsp?sheetid="+sheetid+"&type="+type);
			}
		}
		
	};
	//grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	
	grid.setCellText('查看',0);
	
	
	for(var i=0; i<row_count; i++){
		var flag = grid.getCellValue(2,i);
		var type = grid.getCellValue(4,i);
		var venderType = grid.getCellValue(6,i);
		if(flag==0 || flag==99){
			grid.setCellText('修改',1,i);
		}
		grid.setCellText(parseHeadFlag(flag),2,i);
		grid.setCellText(parseType(type),4,i);
		grid.setCellText(parseVenderType(venderType),6,i);
		
	}
	
	//数据统计信息
	var totalCount = table.getXMLText("xdoc/xout/list/totalCount");
	var htmlCountInfo = " 共显示"+row_count+"行。<br>";
	if(row_count<totalCount){
		var htmlCountInfo = "记录总数"+totalCount+"行；当前显示"+row_count+"行。请尝试修改查询条件查看未显示数据<br>";
	}
	return htmlCountInfo+grid.toString();
}