var g_ccid;
function searchCT(){
	setLoading( true );
	var parms = new Array();
	parms.push( "action=getCT" );
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/list");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			$("div_ct").innerHTML = getCTGrid(table);
			$('div_ct').style.display='block';
			$('div_cc').style.display='none';
			$('div_ctc').style.display='none';
			$('txt_ccname').value='';
			$('txt_ccnote').value='';
			$('txt_ccid').value='';
			$('addcc').style.display='';
			$('savecc').style.display='none';
			$('addct').style.display='';
			$('savect').style.display='none';
			$('txt_ctname').value='';
			$('txt_ctnote').value='';
			$('txt_ctid').value='';
			
		}
		setLoading( false );
	};
	
}

function getCTGrid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	var grid = new AW.UI.Grid;

	table.setColumns(["ctid","ctname","flag","yearflag","appflag","whflag","note"]);	
	var columnNames = ["编码","名称","是否基本","是否年审","是否录入批文号","是否维护批次","备注"];	
	
//	table.setColumns(["ctid","ctname","flag","yearflag","appflag","note"]);	
//	var columnNames = ["编码","名称","是否基本","是否年审","是否录入批文号","备注"];	
	grid.setId( "ctgrid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	grid.onCellClicked = function(event, column, row){
		var ctid =grid.getCellValue(0,row);
		searchCTC('ctid',ctid);
		$('div_cc').style.display='none';
		$('txt_ctid').value=ctid;
		$('txt_ctname').value=grid.getCellValue(1,row);
		$('txt_ctflag').value=grid.getCellValue(2,row);
		$('txt_ctyearflag').value=grid.getCellValue(3,row);
		$('txt_appflag').value=grid.getCellValue(4,row);
		$('txt_whFlag').value=grid.getCellValue(5,row);
		$('txt_ctnote').value=grid.getCellValue(6,row);
		$('addct').style.display='none';
		$('savect').style.display='';
	};
	grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	
	//grid.setCellText('查看',0);
	
	for(var i=0; i<row_count; i++){
		var note="";
		var yearflag= grid.getCellValue(3,i);
		note = yearflag==1?"需要年审":"不年审";
		grid.setCellText(note,3,i);
		
		var flag = grid.getCellValue(2,i);
		note = flag==0?"基本":(flag==1?"可选择":"检验型");
		grid.setCellText(note,2,i);
		
		var appflag= grid.getCellValue(4,i);
		note = appflag==1?"需录入":"不录入";
		grid.setCellText(note,4,i);
		
		var whFlag= grid.getCellValue(5,i);
		note = whFlag==1?"是":"否";
		grid.setCellText(note,5,i);
	}
	return grid.toString();
}

function searchCC(){
	setLoading( true );
	var parms = new Array();
	parms.push( "action=getCC" );
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/list");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			$("div_cc").innerHTML = getCCGrid(table);
			$('div_cc').style.display='block';
			$('div_ct').style.display='none';
			$('div_ctc').style.display='none';
			$('txt_ctname').value='';
			$('txt_ctnote').value='';
			$('txt_ctid').value='';
			$('addct').style.display='';
			$('savect').style.display='none';
			$('addcc').style.display='';
			$('savecc').style.display='none';
			$('txt_ccname').value='';
			$('txt_ccnote').value='';
			$('txt_ccid').value='';
		}
		setLoading( false );
	};
	
}

function getCCGrid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	var grid = new AW.UI.Grid;

	table.setColumns(["ccid","ccname","note"]);	
	var columnNames = ["编码","名称","备注"];	
	grid.setId( "ccgrid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	grid.onCellClicked = function(event, column, row){
		var ccid =grid.getCellValue(0,row);
		getEditCTC(ccid);
		g_ccid = ccid;
		$('div_ct').style.display='none';
		$('txt_ccid').value=ccid;
		$('txt_ccname').value=grid.getCellValue(1,row);
		$('txt_ccnote').value=grid.getCellValue(2,row);
		$('addcc').style.display='none';
		$('savecc').style.display='';
	};
	grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	
	//grid.setCellText('查看',0);
	return grid.toString();
}

function searchCTC(by,id){
	setLoading( true );
	var parms = new Array();
	parms.push( "action=getCTC" );
	parms.push( "by="+by );
	parms.push( "id="+id );
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/list");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			$("div_ctc").innerHTML = getCTCGrid(table);
			$('div_ctc').style.display='';
		}
		setLoading( false );
	};
}

function getCTCGrid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	var grid = new AW.UI.Grid;

	table.setColumns(["ctname","ccname","flag"]);	
	var columnNames = ["类型名称","品类名称","标志"];	
	grid.setId( "ctcgrid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	grid.onCellClicked = function(event, column, row){
		var ccid =grid.getCellValue(0,row);
	};
	grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	for(var i=0; i<row_count; i++){
		var note="";
		var flag= grid.getCellValue(2,i);
		note = flag==1?"选择性":"必备";
		grid.setCellText(note,2,i);
		
	}
	//grid.setCellText('查看',0);
	return grid.toString();
}


function addct(){
	var ctname=$('txt_ctname').value;
	var ctnote=$('txt_ctnote').value;
	var flag = $('txt_ctflag').value;
	var yearFlag=$('txt_ctyearflag').value;
	var appFlag=$('txt_appflag').value;
	var whFlag=$('txt_whFlag').value;
	
	if(ctname==''){
		alert('必须填写名称');return;
	}
	setLoading( true );
	var parms = new Array();
	parms.push( "action=addCT" );
	parms.push( "ctname="+encodeURIComponent(ctname) );
	parms.push( "note="+encodeURIComponent(ctnote));
	parms.push( "flag="+flag);
	parms.push( "yearFlag="+yearFlag);
	parms.push( "appFlag="+appFlag);
	parms.push( "whFlag="+whFlag);
	
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/list");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			alert('已添加');
			searchCT();
			$('txt_ctname').value='';
			$('txt_ctnote').value='';
		}
		setLoading( false );
	}
}

function savect(){
	var ctid = $('txt_ctid').value;
	var ctname=$('txt_ctname').value;
	var ctnote=$('txt_ctnote').value;
	var flag = $('txt_ctflag').value;
	var yearFlag=$('txt_ctyearflag').value;
	var appFlag=$('txt_appflag').value;
	var whFlag=$('txt_whFlag').value;
	if(ctname==''){
		alert('必须填写名称');return;
	}
	setLoading( true );
	var parms = new Array();
	parms.push( "action=updateCT" );
	parms.push( "ctid="+ctid);
	parms.push( "ctname="+encodeURIComponent(ctname) );
	parms.push( "note="+encodeURIComponent(ctnote));
	parms.push( "flag="+flag);
	parms.push( "yearFlag="+yearFlag);
	parms.push( "appFlag="+appFlag);
	parms.push( "whFlag="+whFlag);
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/list");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			alert('已更新');
			searchCT();
			$('txt_ctname').value='';
			$('txt_ctnote').value='';
		}
		setLoading( false );
	}
}

function addcc(){
	var ccname=$('txt_ccname').value;
	var ccnote=$('txt_ccnote').value;
	if(ccname==''){
		alert('必须填写名称');return;
	}
	setLoading( true );
	var parms = new Array();
	parms.push( "action=addCC" );
	parms.push( "ccname="+encodeURIComponent(ccname) );
	parms.push( "note="+encodeURIComponent(ccnote));
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setRequestMethod("POST");
	table.setTable("xdoc/xout/list");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			alert('已添加');
			searchCC();
			$('txt_ccname').value='';
			$('txt_ccnote').value='';
		}
		setLoading( false );
	};
}

function savecc(){
	var ccname=$('txt_ccname').value;
	var ccnote=$('txt_ccnote').value;
	var ccid  =$('txt_ccid').value;
	if(ccname==''){
		alert('必须填写名称');return;
	}
	setLoading( true );
	var parms = new Array();
	parms.push( "action=updateCC" );
	parms.push( "ccname="+encodeURIComponent(ccname) );
	parms.push( "note="+encodeURIComponent(ccnote));
	parms.push( "ccid="+ccid);
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/list");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			alert('已更新');
			searchCC();
			$('txt_ccname').value='';
			$('txt_ccnote').value='';
		}
		setLoading( false );
	};
}

function getEditCTC(ccid){
	setLoading( true );
	var parms = new Array();
	parms.push( "action=getCTCByCCID" );
	parms.push( "ccid="+ccid );
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/list");
	table.request();
	table.response = function(text){
		table.setXML(text);
//		alert(text)
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			var htmlOut = table.transformNode("ctcedit.xsl");
			$("div_ctc").innerHTML = htmlOut ;
			$('div_ctc').style.display='';
		}
		setLoading( false );
	}
}


function saveCTC(e){
	setLoading( true );
	var ctid = e.ctid;
	var flag = $('flag_'+ctid).checked?0:1;
	var parms = new Array();
	parms.push( "ccid="+g_ccid );
	parms.push( "ctid="+ctid );
	parms.push( "flag="+flag );
	if(e.checked){
		$('ctid_'+ctid).checked=true;
		parms.push( "action=addCTCByCCID" );
	}else{
		parms.push( "action=delCTCByCCID" );
		$('flag_'+ctid).checked = false;
	}
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
		}
		setLoading( false );
	}
}

