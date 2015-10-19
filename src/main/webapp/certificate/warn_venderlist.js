//浏览全部证照
function search(){
	setLoading( true );
	var parms = new Array();
	parms.push("action=getWarnVenderCertificateList");
	parms.push("operation=getWarnVenderCertificateListExcel");
	if($("txt_flag").value==""){
		parms.push("itemflag=-10");
		parms.push("itemflag=-11");
		parms.push("itemflag=-100");
	}else{
		parms.push("itemflag="+$("txt_flag").value);
	}
	parms.push("venderid="+$("txt_venderid").value);
	if($('txt_isLike').checked){
		parms.push("isLike=true");
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

	table.setColumns(["venderid","vendername","flag","count"]);	
	var columnNames = ["供应商编码","供应商名称","状态","数目"];	
	grid.setId( "grid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	//grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	
	//数据统计信息
	var totalCount = table.getXMLText("xdoc/xout/list/row_count");
	var htmlCountInfo = " 共显示"+row_count+"行。<br>";
	if(row_count<totalCount){
		var htmlCountInfo = "记录总数"+totalCount+"行；当前显示"+row_count+"行。请尝试修改查询条件查看未显示数据<br>";
	}
	return htmlCountInfo+grid.toString();
}

function downLoad(){
	
	
	var parms = new Array();
	parms.push("action=getWarnVenderCertificateList");
	parms.push("operation=getWarnVenderCertificateListExcel");
	if($("txt_flag").value==""){
		parms.push("itemflag=-10");
		parms.push("itemflag=-11");
		parms.push("itemflag=-100");
	}else{
		parms.push("itemflag="+$("txt_flag").value);
	}
	parms.push("venderid="+$("txt_venderid").value);
	if($('txt_isLike').checked){
		parms.push("isLike=true");
	}
	var url  = "../DaemonDownloadExcel?"+parms.join('&');
	
	window.location.href = url;
}