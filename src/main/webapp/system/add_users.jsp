<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<style>
.aw-grid-control {
	width: 100%;
	height: 300;
	border: none;
	font: menu;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

.aw-column-1 {
	width: 170px;
}
</style>
<style>
div#div_loading {
	width: 15em;
	text-align: center;
	background: #FFA826;
	color: #FFF;
	position: absolute;
	top: 125px;
	left: 50px;
}

div#div_search {
	position: absolute;
	top: 20px;
	left: 10px;
	display: block;
}

div#div_cat {
	position: absolute;
	top: 105px;
	left: 10px;
	display: block;
}
</style>
<xml id="island4result" />
<xml id="island4cat" />
<xml id="island4send" />
<xml id="island4reply" />
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../js/ajax.js"></script>
<script language="javascript" src="../AW/runtime/lib/aw.js"></script>
<script language="javascript">
	var loading_html='<img src="../img/loading.gif"></img><font color=#003>正在下载,请等候……</font>';	
</script>
<script language="javascript">
var table = new AW.XML.Table;
var obj = new AW.UI.Grid;
	function search_sheet(){
		var parms = new Array();
		if( txt_venderid_s.value != '' )  parms.push( "venderid_start=" + txt_venderid_s.value );
		if( txt_venderid_e.value != '' )  parms.push( "venderid_end=" + txt_venderid_e.value );		
		if( parms.length == 0 ) {
			alert( "请设置查询过滤条件" );
			return;
		}
		load_catalogue( parms );
	}
	function load_catalogue ( parms ){		
		set_loading_notice( true );
		parms.push( "sheetname=vender" );
		parms.push( "timestamp=" + new Date().getTime() );
		var url = "../DaemonSearchVender?" + parms.join( '&' );
		var courier = new AjaxCourier( url );
		courier.island4req  	= null;
		courier.reader.read 	= analyse_catalogue;
		courier.call();	
	}
	function analyse_catalogue( text ){		
		island4cat.loadXML( text );
		island4result.loadXML( text );
		var elm_err 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xerr" );
		var xerr 	= parseXErr( elm_err );		
		if( xerr.code == "0" ) {    	 			
		var node = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );	
		island4cat.loadXML( node.xml );
		show_catalogue();
		btn_add.disabled=false;				
		}
		else if(xerr.code=="100"){	
			
			btn_add.disabled=true;
			div_cat.innerHTML="";
			set_loading_notice( true,"数据库没有你要查找的数据!" );
		}
		else{
			alert(xerr.toString());
			div_cat.innerHTML="";
			set_loading_notice( false );
		}
	}
	function show_catalogue(){
		window.status = "显示目录 ...";
		set_loading_notice( true );
		arr_sheetid = new Array();
		var node_sheetid 	= island4result.XMLDocument.selectNodes( "/xdoc/xout/catalogue/row/venderid" );	
		var next_node = node_sheetid.nextNode();
		while( next_node != null ) {
			arr_sheetid[ arr_sheetid.length ] = next_node.text;
			next_node = node_sheetid.nextNode();
		}
		if( arr_sheetid !=null && arr_sheetid.length >0 ) current_sheetid = arr_sheetid[0];
		
		var node_body 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );	
		var row_count   = node_body.childNodes.length;
		island4cat.loadXML( node_body.xml );
		
		var xml, node = document.getElementById( "island4cat" );		
	
		if (window.ActiveXObject) {
			xml = node;
		}
		else {
			xml = document.implementation.createDocument( "", "", null);
			xml.appendChild( island4cat.selectSingleNode( "*" ) );
		}					
		table.setXML( xml );
		var columnNames=["供应商编码","供应商名称","供应商缩写","地址","邮政编码","网址",
				"邮箱","公司电话","公司传真","供应商状态","最近修改时间","最近修改人"];
		var columnOrder = [ 0,1,2,3,4,5,6,7,8,9,10,11];		
		obj.setId( "grid_cat" );
		obj.setColumnCount( 12 );
		obj.setRowCount( row_count );	
		obj.setHeaderText( columnNames );	
		obj.setColumnIndices( columnOrder );	
	
		obj.setCellModel(table);
		div_cat.innerHTML ='查询到'+obj.getRowCount()+'条记录，点击添加用户将创建这批供应商用户。<br/>';
		div_cat.innerHTML += obj.toString();
		window.status = "OK";
		set_loading_notice( false );
	}
	function to_elm(){
		var tmp;
		var doc = island4send.XMLDocument;
		var elm_root = doc.selectSingleNode("/") ;		
		var elm_xdoc = doc.createElement("xdoc");
		var elm_xout = doc.createElement("xout");
		elm_xdoc.appendChild( elm_xout );
		var elm_send = doc.createElement("List");
		for(var i=0;i<obj.getRowCount();i++){
		var elm_record=doc.createElement("Vender");	
		var elm = doc.createElement( "venderid" );
		elm.appendChild( doc.createTextNode(obj.getCellText(0,i)));
		elm_record.appendChild( elm );
		var elm_status = doc.createElement( "status" );
		elm_status.appendChild( doc.createTextNode(obj.getCellText(9,i)));
		elm_record.appendChild( elm_status );
		elm_send.appendChild( elm_record );
		}
		elm_xout.appendChild( elm_send );
		elm_xdoc.appendChild(elm_xout);
		elm_root.appendChild(elm_xdoc);
		
		var courier = new AjaxCourier( "../DaemonAddUsers");
		    courier.island4req  	= island4send;
		    courier.reader.read 	= analyseReply;
		    courier.call();
	}
	function analyseReply(text){
		island4reply.loadXML( text );
		var elm_err 	= island4reply.XMLDocument.selectSingleNode( "/xdoc/xerr" );	
		var xerr 	= parseXErr( elm_err );
		if( xerr.code == "0" ) {    	   	    	   	    
		}
		else {		
			alert( xerr.toString() );
		}
	}
	function set_loading_notice( on,msg ){	
		if(typeof(msg)=="undefined")
		div_loading.innerHTML = loading_html;
		else
		div_loading.innerHTML = msg;
		div_loading.style.display = on ? 'block' : 'none';
	}
	function Validate(){	
		if(txt_venderid_s.value!=""&&txt_venderid_e.value!="")
		btn_search.disabled=false;
		else
		btn_search.disabled=true;
	}
	</script>
</head>
<body>

	<div id="title" align="center"
		style="font-size: 16px; font-family: '楷体_GB2312, 黑体'; color: #42658D; font-weight: bold";>批量添加用户</div>
	<br />
	<table cellspacing="1" cellpadding="2" width="50%" align="center"
		class="tablecolorborder">
		<tr>
			<td class=altbg2>开始ID</td>
			<td class=altbg2><input type="text" id="txt_venderid_s"
				size="16" onpropertychange="Validate()" /></td>
			<td class=altbg2>结束ID</td>
			<td class=altbg2><input type="text" id="txt_venderid_e"
				size="16" onpropertychange="Validate()" /></td>
		</tr>
		<tr>
			<td><input type="button" id="btn_search" value="查&nbsp;询"
				onclick="search_sheet()" disabled="true" /></td>
			<td><input type="button" id="btn_add" value="添加用户"
				onclick="to_elm()" disabled="true" /></td>
		</tr>
	</table>
	<div id="div_loading" style="display: none;">
		<img src="../img/loading.gif"></img><font color=#003>正在下载,请等候……</font>
	</div>
	<div id="div_cat" style="display: block;"></div>
</body>
</html>