<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	import="java.util.* , java.text.DecimalFormat"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3020214;
%>
<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );
	if ( !perm.include( Permission.READ ) ) 
		throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
%>
<%

	String venderid = token.isVender?token.getBusinessid():"";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>标超进货额月报</title>


<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/CalendarControl.css" rel="stylesheet" type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"></script>
<script language="javascript">
		var table4confirm = new AW.XML.Table;
		extendDate();
		extendNumber();
		</script>

<style type="text/css">
.error_message {
	width: 15em;
	text-align: center;
	background: #FFA826;
	color: #000;
}

div#login_info {
	color: #909090;
	position: absolute;
	top: 20px;
	right: 110px;
	left: auto;
}

div#div_loading {
	width: 15em;
	text-align: center;
	background: #FFA826;
	color: #FFF;
}

#tag_grp {
	width: 100%;
	height: 25px;
	margin-bottom: 10px;
}

.remind {
	color: #FFCC33;
}

.warn {
	color: #CC3300;
}

.warning {
	border: 2px solid #FF6600;
	padding: 4px;
	margin-bottom: 4px;
	font-weight: bold;
	background-color: #fff;
}
</style>

<style>
.aw-grid-control {
	height: 84%;
	width: 100%;
	border: none;
	font: menu;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}
</style>

<style>
#grid_cat {
	background-color: #F9F8F4
}

#grid_cat .aw-column-0 {
	width: 50px;
}

#grid_cat .aw-column-1 {
	width: 50px;
}

#grid_cat .aw-column-2 {
	width: 80px;
}

#grid_cat .aw-column-3 {
	width: 80px;
}

#grid_cat .aw-column-4 {
	width: 80px;
}

#grid_cat .aw-column-5 {
	width: 80px;
}

#grid_cat .aw-column-6 {
	width: 80px;
}

#grid_cat .aw-column-7 {
	width: 80px;
}

#grid_cat .aw-column-8 {
	width: 100px;
}

#grid_cat .aw-column-9 {
	width: 80px;
}

#grid_cat .aw-column-10 {
	width: 50px;
}
</style>
<style>
div_sheethead.td {
	align: center
}
</style>
<script language="javascript">
		var arr_sheetid 	= null;
		var current_sheetid 	= "";
		var tag_grp 		= new AW.UI.Tabs;
		</script>

<script language="javascript">
		var btn_search = new AW.UI.Button;
		btn_search.setControlText( "查询" );
		btn_search.setControlImage( "search" );	
		function init()
		{
			install_tag_sheets();
		}
		function onChange(ctrl)
		{
		
		var n = 0;
		var tmpshopid = new Array();
		var opt = ctrl.options;
		for(var i=0; i<opt.length; i++)
		if(opt[i].selected)		
		{
			tmpshopid[n] = opt[i].value;
			n++;
		}
		
	txtshopid.value = tmpshopid.join(",");
	//alert(txtshopid.value);
}
		function install_tag_sheets()
		{
			tag_grp.setId( "tag_grp" );
			tag_grp.setItemText( [ "查询条件",  "明细" ] );
			tag_grp.setItemCount( 2 );
			tag_grp.onSelectedItemsChanged = function( idx ) {
				if( idx == "0" ) enactive_search() ;
				if( idx == "1" ) enactive_catalogue();
			};
		
			tag_grp.setSelectedItems( [0] );
			div_tabs.innerHTML=tag_grp.toString();
		}
		function enactive_search()
		{
			hide_all();
			div_search.style.display = 'block';
		}

		function enactive_catalogue()
		{
			hide_all();
			div_cat.style.display 	 = 'block';
		}

		function enactive_detail()
		{
			hide_all();
			div_detail.style.display = 'block';
		}

		function hide_all()
		{
			div_search.style.display = 'none';
			div_cat.style.display 	 = 'none';
			div_detail.style.display = 'none';
		}
function search_sheet()
{
	if(txt_venderid.value==''){
		alert("必须输入供应商编码");
		return;
	}
	var parms = new Array();
	parms.push( "sheetname=inmonth" );

	if( ctrl4date.value 	!= '' )      parms.push( "yearmonth="    + ctrl4date.value 	);
	if( txt_venderid.value  != '' )   parms.push( "venderid="  + txt_venderid.value );
	setLoading( true );

	load_catalogue( parms );
	tag_grp.setSelectedItems( [1] );
	window.status = "OK";
}


function load_catalogue ( parms )
{
	div_cat.innerHTML = " ";

	parms.push( "sheetname=inmonth" );
	parms.push( "timestamp=" + new Date().getTime() );
	setLoading( true );
	var url = "../DaemonSearchSheet?" + parms.join( '&' );

	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_catalogue;
	courier.call();
}

function analyse_catalogue( text )
{
	island4cat.loadXML( text );
	island4result.loadXML( text );
	
	var elm_err 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" ) 
	{    	 
		var elm_row 	= island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	  	var row_count=elm_row.childNodes.length;
	  	if( row_count== "0" )
	  	{
	  		enactive_catalogue();
			div_cat.innerHTML = "没有找到您要的信息.";
	  		setLoading( false );
	  	}else{
	  		var node = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	  		var table = new AW.XML.Table;  
	  		island4cat.loadXML( table.getXMLContent(node) );
			show_catalogue();
		}	
	}else{	
		div_cat.innerHTML = "";
	  	enactive_catalogue();
		div_cat.innerHTML = xerr.code + ";" + xerr.note;
	  	setLoading( false );
	}
}


function show_catalogue()
{
	setLoading( true );
	arr_sheetid = new Array();

	var node_sheetid 	= island4result.XMLDocument.selectNodes( "/xdoc/xout/catalogue/row/sheetid" );	
	var next_node = node_sheetid.nextNode();
	while( next_node != null ) {
		arr_sheetid[ arr_sheetid.length ] = next_node.text;
		next_node = node_sheetid.nextNode();
	}
	
	if( arr_sheetid !=null && arr_sheetid.length >0 ) current_sheetid = arr_sheetid[0];
	
	var node_body 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );	
	var row_count   = node_body.childNodes.length;
	var table = new AW.XML.Table;  
	island4cat.loadXML( table.getXMLContent(node_body) );
	
	var xml, node = document.getElementById( "island4cat" );		
	
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument( "", "", null);
		xml.appendChild( island4cat.selectSingleNode( "*" ) );
	}		

	table.setXML( xml );

	table.setColumns( [ "monthid", "venderid","vendername","goodsid","goodsname","qty","taxcost","cost"] );

	var columnNames = [ "年月","供应商编号","供应商名称 ","商品编号","商品名称",
	        			"数量","含税进货单价","进货单价" ];
	
	var obj = new AW.UI.Grid;
	obj.setId( "grid_cat" );
	obj.setColumnCount( 14 );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );
		
	obj.setSelectorVisible(true);
	obj.setSelectorWidth(30);
	obj.setSelectorText(function(i){return this.getRowPosition(i)+1});
	obj.setCellModel(table);
	div_cat.innerHTML = obj.toString();
	window.status = "OK";
	setLoading( false );
}

function downExcel(){
var parms = new Array();
	if(txt_venderid.value==''){
		alert("必须输入供应商编码");
		return;
	}
	parms.push( "sheetname=deduction" );
	if( ctrl4date.value 	!= '' )      parms.push( "yearmonth="    + ctrl4date.value 	);
	if( txt_venderid.value  != '' )   parms.push( "venderid="  + txt_venderid.value );
	var url  = "../DaemonDownloadReport?reportname=inmonth&" + parms.join( "&" );
	
	window.location.href = url;

}
</script>

<xml id="island4result" />
<xml id="island4purchase" />
<xml id="island4cat" />
<xml id="island4err" />

</head>
<a href="javascript:downExcel()">将查询结果导出到Excel文档</a>
<body onload="init()">
	<div id="div_tabs" style="width: 100%;"></div>

	<div id="div001">
		<div id="div_cat" style="display: block;"></div>
		<div id="div_detail" style="display: none;">
			<div id="div_navigator" style="display: none;">
				<input type="button" value="上一单" onclick="sheet_navigate(-1)" /> <input
					type="button" value="下一单" onclick="sheet_navigate(1)" /> <input
					type="button" value="确认单据" onclick="confirm_sheet()" /> <input
					type="button" value="打印单据" onclick="open_win_print()" />
			</div>
			<br />
			<div id="div_warning" style="" class="warning"></div>

			<div id="div_sheethead"></div>
		</div>

		<div id="div_search">
			<table cellspacing="1" cellpadding="2" width="350"
				class="tablecolorborder">
				<tr>
					<td><span style="<%=(token.isVender?"display:none;":"")%>>">
							供应商编码<input type="text" id="txt_venderid" value="<%=venderid %>" />
					</span> 月份<input type="text" id="ctrl4date"
						onfocus="WdatePicker({skin:'whyGreen',dateFmt:'yyyyMM'})"
						class="Wdate" /></td>
				</tr>
				<tr class="singleborder">
					<td colspan=2></td>
				</tr>
				<tr class="whiteborder">
					<td colspan=2></td>
				</tr>
				<tr class="header">
					<td colspan=2><script>
							document.write( btn_search );
							btn_search.onClick =search_sheet;
							</script></td>
				</tr>
			</table>

			<br />
		</div>
		<!-- end of div_search -->
	</div>
	<!-- end of div001 -->
</body>
</html>
