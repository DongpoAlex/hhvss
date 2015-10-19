var g_row_selected=new Array;
var g_cc_selected=new Array;
var vrGrid;
var ctGrid;
var ccGrid;
var g_type;
var g_ccid;
function checkVender(v){
	if( v != "" ){
		setLoading(true,'正在初始化供应商信息');
		var url 		= "../DaemonVender?venderid=" + v;
		var table = new AW.XML.Table;
		table.setURL(url);
		table.setTable("xdoc/xout/list");
		table.request();
		table.response = function(text){
			setLoading(false);
			table.setXML(text);
			var xcode = table.getErrCode();
			if( xcode != '0' ){//处理xml中的错误消息
				alert( xcode+table.getErrNote());
			}else{
				$('sp_vendername').innerText=table.getXMLText( "/xdoc/xout/vender/vendername" );
				$('setup2').style.display="block";
				$('txt_venderid').disabled = true;
				
				searchCC();
			}
		}
	}else{
		$('sp_vendername').innerText='';
	}
}


function searchCC(){
	setLoading( true );
	var parms = new Array();
	parms.push( "action=getVRCCList" );
	parms.push( "venderid="+$('txt_venderid').value );
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
			$("sp_cc").innerHTML = "供应商"+$("txt_venderid").value+"所属品类列表";
		}
		setLoading( false );
	}
	
}

function getCCGrid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";

	table.setColumns(["checkbox","ccid","ccname","aaa","venderid"]);	
	var columnNames = ["","编码","名称","查看关联供应商",""];
	ccGrid = new AW.UI.Grid;
	ccGrid.setId( "ccgrid" );
	ccGrid.setColumnCount(columnNames.length);
	ccGrid.setRowCount( row_count );	
	ccGrid.setHeaderText(columnNames);
	ccGrid.setSelectionMode("multi-row-marker");
	ccGrid.setCellModel(table);
	
	ccGrid.onSelectedRowsChanged = function(arrayOfRowIndices){
		window.status=arrayOfRowIndices;
		g_cc_selected = arrayOfRowIndices;
    };
    ccGrid.onCellClicked = function(event, col, row){
		if(col==3){
			g_ccid =ccGrid.getCellValue(1,row);
			var ccname = ccGrid.getCellValue(2,row);
			searchVenders();
			$('sp_vender').innerHTML = "品类 【"+ccname+"】关联的供应商";
		}
	};
	
	g_cc_selected=[];
    for ( var i = 0; i < ccGrid.getRowCount(); i++) {
    	ccGrid.setCellText("点击查看",3,i);
    	var v = ccGrid.getCellValue(4,i);
    	if(v!=0){
    		g_cc_selected.push(i);
    	}
	}
    ccGrid.setSelectedRows(g_cc_selected);
	return ccGrid.toString();
}


function updatecc(){
	var venderid = $('txt_venderid').value;
	setLoading(true,"正在更新");
	var url = "../DaemonCertificate?action=updateVenderCCRelation";
	var ajax = cookCCData(venderid,g_ccid);
	ajax.setURL(url);
	ajax.setRequestMethod('POST');
	var str  = '<?xml version="1.0" encoding="UTF-8" ?>\n';
	str += ajax.getXMLContent();
	ajax.setRequestData(str);
	ajax.request();
	ajax.response = function( text ){
		ajax.setXML(text);
		var xcode = ajax.getErrCode();
		if( xcode != '0' ){
			alert(xcode+ajax.getErrNote());
		}else{
			searchCC();
		}
		setLoading(false);
	};
}

function cookCCData(venderid, ccid){
	var ajax = new AW.XML.Table;
	ajax.setXML("");
	var doc = ajax.getXML();
	var elm_root = doc.selectSingleNode("/") ;		
	var elm_xdoc = doc.createElement("xdoc");
	var elm_xparam = doc.createElement("xparms");
	var elm_head = doc.createElement("head");
	var elm_venderid = doc.createElement("venderid");
	elm_venderid.appendChild( doc.createTextNode(venderid));
	elm_head.appendChild(elm_venderid);
	elm_xparam.appendChild( elm_head );
	
	
	var elm_dataset = doc.createElement("dataset");
	for ( var i = 0; i < g_cc_selected.length; i++) {
		var ccid = ccGrid.getCellValue(1,g_cc_selected[i]);
		var elm_row = doc.createElement("row");
		
		var elm_ccid = doc.createElement("ccid");
		elm_ccid.appendChild( doc.createTextNode(ccid));
		elm_row.appendChild(elm_ccid);
		
		elm_dataset.appendChild(elm_row);
	}
	
	elm_xparam.appendChild( elm_dataset );
	elm_xdoc.appendChild( elm_xparam );
	elm_root.appendChild(elm_xdoc);
	return ajax;
}

function searchVenders(){
	setLoading( true );
	var parms = new Array();
	parms.push( "action=getVendersByCCID" );
	parms.push( "ccid="+g_ccid );
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
			$("div_vender").innerHTML = getVenderGrid(table);
		}
		setLoading( false );
	}
}

function getVenderGrid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";

	table.setColumns(["venderid","vendername","维护"]);	
	var columnNames = ["供应商编码","供应商名称","维护品类"];
	var grid = new AW.UI.Grid;
	grid.setId( "vendergrid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	
	grid.onCellClicked = function(event, col, row){
		if(col==2){
			var venderid = grid.getCellValue(0,row);
			$("txt_venderid").value=venderid;
			$("sp_cc").innerHTML = "供应商"+$("txt_venderid").value+"所属品类列表";
			checkVender(venderid);
		}
	};
	for ( var i = 0; i < row_count; i++) {
    	grid.setCellText("点击维护",2,i);
	}
	return grid.toString();
}