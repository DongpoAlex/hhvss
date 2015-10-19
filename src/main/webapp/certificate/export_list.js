//浏览全部证照
function search(){
	setLoading( true );
	var parms = new Array();
	parms.push("action=getTaskList");
	parms.push("flag="+$("txt_flag").value);
	var url  = "../DaemonCertificate?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL((url));
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
	};
}

var grid;
var g_selected=new Array;
function getGrid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";

	table.setColumns(["checkbox","bbbb","flag","tries","taskid","note","sheetid","type","seqno","checker","checktime","venderid","vendername","certificateid","ctname"]);	
	var columnNames = ["选择","查看","状态","导出尝试次数","导出任务号","出错原因","单号","证照类型","序号","审核人","审核时间","供应商ID","供应商名称","证照编码","类型"];	
	grid = new AW.UI.Grid;
	grid.setId( "grid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	grid.setSelectionMode("multi-row-marker");
	grid.onCellClicked = function(event, column, row){
		var sheetid = grid.getCellValue(6,row);
		var type =grid.getCellValue(7,row);
		var seqno =grid.getCellValue(8,row);
		var flag =grid.getCellValue(2,row);
		if(column==1){
			window.open("certificate_detail.jsp?sheetid="+sheetid+"&seqno="+seqno+"&type="+type);
		}
		
	};
	
	g_selected = [];
	grid.onSelectedRowsChanged = function(arrayOfRowIndices){
		g_selected = arrayOfRowIndices;
    };
    
	//grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	
	grid.setCellText('查看',1);
	grid.refresh();
	
	for(var i=0; i<row_count; i++){
		var flag = grid.getCellValue(2,i);
		grid.setCellText(flag==0?"未导出":"导出出错",2,i);
		var type = grid.getCellValue(7,i);
		grid.setCellText(parseType(type),7,i);
	}
	//数据统计信息
	var totalCount = table.getXMLText("xdoc/xout/list/totalCount");
	var htmlCountInfo = " 共显示"+row_count+"行。<br>";
	if(row_count<totalCount){
		var htmlCountInfo = "记录总数"+totalCount+"行；当前显示"+row_count+"行。请尝试修改查询条件查看未显示数据<br>";
	}
	return htmlCountInfo+grid.toString();
}


function redo(){
	var parms = new Array();
	parms.push("action=taskListRedo");
	
	for ( var i = 0; i < g_selected.length; i++) {
		var flag = grid.getCellValue(2,g_selected[i]);
		var taskid = grid.getCellValue(4,g_selected[i]);
		if(flag==-1){
			parms.push("taskid="+taskid);
		}
	}
	
	if(parms.length==1){
		alert("只有状态为导出出错的的任务才能重新导出");
		return;
	}
	
	var url ="../DaemonCertificate?"+parms.join( '&' );
	alert(url)
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout");
	table.request();
	table.response = function(text){
		setLoading(false);
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( table.getErrNote());
		}else{
			search();
		}
	}
}