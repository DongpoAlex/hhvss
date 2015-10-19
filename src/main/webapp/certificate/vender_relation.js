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
			}
		};
	}else{
		$('sp_vendername').innerText='';
	}
}

function setup(type){
	g_type = type;
	$('setup3').style.display="block";
	$("div_cc").innerHTML="";
	$("div_ct").innerHTML="";
	$("div_list").innerHTML="";
	if(type==1){
		searchCC();
		$('div_ccbtn').style.display='block';
		$('div_ctbtn').style.display='none';
	}else{
		if(type==0){
			g_ccid=-1;
		}
		if(type==2){
			g_ccid=-2;
		}
		searchCT();
		$('div_ctbtn').style.display='block';
		$('div_ccbtn').style.display='none';
	}
}
function searchVR(){
	setLoading( true );
	var parms = new Array();
	parms.push( "action=getVenderRelation" );
	parms.push( "ctflag="+g_type );
	parms.push( "venderid="+$('txt_venderid').value );
	parms.push( "ccid="+g_ccid );
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setAsync=false;
	table.setTable("xdoc/xout/list");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			$("div_list").innerHTML = getVRGrid(table);
			initChecked();
		}
		setLoading( false );
	}
}
function getVRGrid( table )
{
	vrGrid = new AW.UI.Grid;
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	table.setColumns(["venderid","vendername","ctid","ctname","ccname","sheetid","sheetcount"]);	
	var columnNames = ["供应商编码","供应商名称","种类编码","证照种类","品类","已有单据","已有单据数目"];	
	vrGrid.setId( "crgrid" );
	vrGrid.setColumnCount(columnNames.length);
	vrGrid.setRowCount( row_count );	
	vrGrid.setHeaderText(columnNames);
	vrGrid.setSelectionMode("single-row");
	vrGrid.setCellModel(table);
	
	return vrGrid.toString();
}


function searchCT(){
	setLoading( true );
	var parms = new Array();
	parms.push( "action=getCTByFlag" );
	parms.push( "flag="+g_type );
	var url = ("../DaemonCertificate?"+parms.join( '&' ));
	var table = new AW.XML.Table;
	table.setAsync=true;
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
			//searchVR();
		}
		setLoading( false );
	}
	
}
function searchCTByCC(){
	setLoading( true );
	var parms = new Array();
	parms.push( "action=getCTC" );
	parms.push( "id="+g_ccid );
	parms.push( "by=ccid" );
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
			searchVR();
		}
		setLoading( false );
	}
	
}

function initChecked(){
	//取已有关系循环赋值到复选框
	g_row_selected=[];
	
	for ( var j = 0; j < vrGrid.getRowCount(); j++) {
		var ctid2 = vrGrid.getCellValue(2,j);
		for ( var i = 0; i < ctGrid.getRowCount(); i++) {
			var ctid = ctGrid.getCellValue(1,i);
			if(ctid==ctid2){
				g_row_selected.push( i );
			}
		}
	}
	ctGrid.setSelectedRows(g_row_selected);
}

function selectAll(){
	g_row_selected=[];
	for ( var i = 0; i < ctGrid.getRowCount(); i++) {
		g_row_selected.push( i );
	}
	ctGrid.setSelectedRows(g_row_selected);
}
function selectNone(){
	g_row_selected=[];
	ctGrid.setSelectedRows(g_row_selected);
}
function getCTGrid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	
	table.setColumns(["checkbox","ctid","ctname","yearflag","flag","note"]);	
	var columnNames = ["选择","编码","名称","是否年审","是否必备","备注"];
	ctGrid = new AW.UI.Grid
	ctGrid.setId( "ctgrid" );
	ctGrid.setColumnCount(columnNames.length);
	ctGrid.setRowCount( row_count );	
	ctGrid.setHeaderText(columnNames);
	ctGrid.setSelectionMode("multi-row-marker");
	
	ctGrid.onSelectedRowsChanged = function(arrayOfRowIndices){
		window.status=arrayOfRowIndices;
		g_row_selected = arrayOfRowIndices;
    }
	
	ctGrid.setCellModel(table);
	for(var i=0; i<row_count; i++){
		var note="";
		var yearflag= ctGrid.getCellValue(3,i);
		var flag = ctGrid.getCellValue(4,i);
		note = yearflag==1?"需要年审":"不年审";
		ctGrid.setCellText(note,3,i);
		note = flag==1?"非必备":"必备";
		ctGrid.setCellText(note,4,i);
	}
	
	return ctGrid.toString();
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
		}
		setLoading( false );
	}
	
}

function getCCGrid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";

	table.setColumns(["checkbox","ccid","ccname","","venderid"]);	
	var columnNames = ["","编码","名称","",""];
	ccGrid = new AW.UI.Grid;
	ccGrid.setId( "ccgrid" );
	ccGrid.setColumnCount(columnNames.length);
	ccGrid.setRowCount( row_count );	
	ccGrid.setHeaderText(columnNames);
	ccGrid.setSelectionMode("multi-row-marker");
	
	ccGrid.onCellClicked = function(event, column, row){
		if(column==3){
			g_ccid =ccGrid.getCellValue(1,row);
			searchCTByCC();
		}
	};
	ccGrid.onSelectedRowsChanged = function(arrayOfRowIndices){
		window.status=arrayOfRowIndices;
		g_cc_selected = arrayOfRowIndices;
    };
	
	ccGrid.setCellModel(table);
	
	g_cc_selected=[];
    for ( var i = 0; i < ccGrid.getRowCount(); i++) {
    	var v = ccGrid.getCellValue(4,i);
    	if(v!=0){
    		g_cc_selected.push(i);
    	}
    	ccGrid.setCellValue(3,i,"编辑");
	}
    ccGrid.setSelectedRows(g_cc_selected);
    
    
	return ccGrid.toString();
}

function update(){
	var venderid = $('txt_venderid').value;
	setLoading(true,"正在更新");
	var url = "../DaemonCertificate?action=updateVenderRelation";
	var ajax = cookPostData(venderid,g_ccid);
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
			if(g_type==1){
				searchCTByCC();
			}else{
				searchCT();
			}
		}
		setLoading(false);
	};
}


function cookPostData(venderid, ccid){
	var ajax = new AW.XML.Table;
	ajax.setXML("");
	var doc = ajax.getXML();
	var elm_root = doc.selectSingleNode("/") ;		
	var elm_xdoc = doc.createElement("xdoc");
	var elm_xparam = doc.createElement("xparms");
	var elm_head = doc.createElement("head");
	var elm_venderid = doc.createElement("venderid");
	var elm_ccid   = doc.createElement("ccid");
	elm_venderid.appendChild( doc.createTextNode(venderid));
	elm_ccid.appendChild( doc.createTextNode(ccid));
	elm_head.appendChild(elm_venderid);
	elm_head.appendChild(elm_ccid);
	elm_xparam.appendChild( elm_head );
	
	
	var elm_dataset = doc.createElement("dataset");
	for ( var i = 0; i < g_row_selected.length; i++) {
		var ctid = ctGrid.getCellValue(1,g_row_selected[i]);
		var elm_row = doc.createElement("row");
		
		var elm_ctid = doc.createElement("ctid");
		elm_ctid.appendChild( doc.createTextNode(ctid));
		elm_row.appendChild(elm_ctid);
		
		elm_dataset.appendChild(elm_row);
	}
	
	elm_xparam.appendChild( elm_dataset );
	elm_xdoc.appendChild( elm_xparam );
	elm_root.appendChild(elm_xdoc);
	return ajax;
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