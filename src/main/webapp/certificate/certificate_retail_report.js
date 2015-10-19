//浏览全部证照
function search(){
	setLoading( true );
	var parms = new Array();
	parms.push("action=getDetailList");
	parms.push("itemflag="+$("txt_flag").value);
	parms.push("ctype="+$("ctype").value);
	parms.push("type="+$("txt_type").value);
	parms.push("venderid="+$("txt_venderid").value);
	parms.push("ccid="+$("txt_ccid").value);
	parms.push("sheetid="+$("txt_sheetid").value);
	parms.push("goodsid="+$("txt_goodsid").value);
	parms.push("regionid="+$("txt_regionid").value);
	parms.push("barcodeid="+$("txt_barcodeid").value);
	parms.push("certificateid="+$("txt_certificateid").value);
	parms.push("checktime_min="+$("txt_checktime_min").value);
	parms.push("checktime_max="+$("txt_checktime_max").value);
	if($('txt_isLike').checked){
		parms.push("isLike=true");
	}
	var url  = "../DaemonCertificate?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/detail");
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

	table.setColumns(["aaa","bbb","flag","sheetid","seqno","type","venderid","vendertype","vendertypename","contact","ctname","ccname","certificateid","cname","expirydate","yeardate","barcodeid","goodsid","goodsname","approvalnum","papprovalnum","editor","edittime","submittime","checker","checktime","note","yearflag"]);	
	var columnNames = ["查看明细","整单查看","状态","单号","序号","证照类型","供应商ID","供应商类型","代理厂家名称","联系人","证照种类名","品类","证照编码","证照名称","有效截止日","年审日","商品条码","商品编码","商品名称","批文号","产品批号","编辑人","编辑日期","提交日期","审核人","审核时间","备注","年审标志"];	
	grid.setId( "grid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	
	grid.setSelectorVisible(true);
	grid.setSelectorWidth(30);
	grid.setSelectorText(function(i){return this.getRowPosition(i)+1});
	
	grid.onCellClicked = function(event, column, row){
		var sheetid = grid.getCellValue(3,row);
		var type =grid.getCellValue(5,row);
		if(column==0){
				var seqno =grid.getCellValue(4,row);
				window.open("./certificate_detail.jsp?sheetid="+sheetid+"&type="+type+"&seqno="+seqno);
		}
		
		if(column==1){
			var flag = grid.getCellValue(2,row);
			var venderid=grid.getCellValue(5,row);
			window.open("./certificate_show.jsp?venderid="+venderid+"&sheetid="+sheetid+"&type="+type);
		}
		
	};
	//grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	
	grid.setCellText('查看',0);
	grid.setCellText('浏览',1);
	
	for(var i=0; i<row_count; i++){
		var yearflag= grid.getCellValue(22,i);
		if(yearflag!=1){
			grid.setCellText("无需年审",15,i);
		}
		
		var flag = grid.getCellValue(2,i);
		var type = grid.getCellValue(5,i);
		var venderType = grid.getCellValue(7,i);
		grid.setCellText(parseItemFlag(flag),2,i);
		grid.setCellText(parseType(type),5,i);
		grid.setCellText(parseVenderType(venderType),7,i);
	}
	//数据统计信息
	var totalCount = table.getXMLText("xdoc/xout/detail/totalCount");
	var htmlCountInfo = " 共显示"+row_count+"行。<br>";
	if(row_count<totalCount){
		var htmlCountInfo = "记录总数"+totalCount+"行；当前显示"+row_count+"行。请尝试修改查询条件查看未显示数据<br>";
	}
	return htmlCountInfo+grid.toString();
}

function downLoad(){
	
	
	var parms = new Array();
	parms.push("operation=certificateDetail");
	parms.push("itemflag="+$("txt_flag").value);
	parms.push("ctype="+$("ctype").value);
	parms.push("type="+$("txt_type").value);
	parms.push("venderid="+$("txt_venderid").value);
	parms.push("ccid="+$("txt_ccid").value);
	parms.push("sheetid="+$("txt_sheetid").value);
	parms.push("goodsid="+$("txt_goodsid").value);
	parms.push("barcodeid="+$("txt_barcodeid").value);
	parms.push("certificateid="+$("txt_certificateid").value);
	parms.push("checktime_min="+$("txt_checktime_min").value);
	parms.push("checktime_max="+$("txt_checktime_max").value);
	var url  = "../DaemonDownloadExcel?"+parms.join('&');
	
	window.location.href = url;
	
	
}