﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3080111;
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


﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>

<style>
.aw-grid-control {
	height: 400px;
	width: 100%;
	font: menu;
	border: none;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
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
	left: 10px;
}

div#div_search {
	position: absolute;
	top: 20px;
	left: 10px;
	display: block;
}

div#div_cat {
	position: absolute;
	top: 100px;
	left: 10px;
	display: block;
}

label {
	font-size: 11px;
	font-weight: bold;
	color: navy;
}
</style>

<script language="javascript" src="../js/XErr.js"></script>
<script language="javascript" src="../js/ajax.js"></script>
<script language="javascript" src="../AW/runtime/lib/aw.js"></script>
<script language="javascript">
var loading_html='<img src="../img/loading.gif"></img><font color=#003>正在下载,请等候……</font>';

var btn_search = new AW.UI.Button;
btn_search.setControlText( "查询" );
btn_search.setId( "btncheck" );
btn_search.setControlImage( "search" );
</script>

<script language="javascript">
function search_dept()
{
	var parms = new Array();
	if( txt_deptid.value != '' )    parms.push( "deptid=" + txt_deptid.value );
	if( txt_deptname.value != '' )  parms.push( "deptname=" + txt_deptname.value );

	parms.push( "focus=dept" );
	parms.push( "timestamp=" + new Date().getTime() );
	set_loading_notice( true );
		
	var url = "../DaemonInformation?" + parms.join( '&' );
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
		if( row_count==0 )
			alert("数据库没有你要查找的数据!请检查你输入的数据是否正确。");
		else{
			var node = island4cat.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );	
			island4cat.loadXML( node.xml );
			show_catalogue();
		}	
	}
	else {	
		alert(xerr.toString());	
		div_cat.innerHTML="";
		set_loading_notice( false );
	}
}
	
function show_catalogue()
{
	window.status = "显示目录 ...";
	set_loading_notice( true );
	arr_sheetid = new Array();
	var node_sheetid 	= island4result.XMLDocument.selectNodes( "/xdoc/xout/catalogue/row/deptid" );	
	var next_node = node_sheetid.nextNode();
	while( next_node != null ) 
	{
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
	
	var table = new AW.XML.Table;
	table.setXML( xml );
	var columnNames=[ "品类编码", "品类名称", "税率", "备注" ];
	var columnOrder = [ 0, 1, 2, 3 ];
	var obj = new AW.UI.Grid;
	obj.setId( "grid_cat" );
	obj.setColumnCount( 4 );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );	
	obj.setColumnIndices( columnOrder );	
	
	obj.setCellModel(table);
	div_cat.innerHTML = obj.toString();
	window.status = "OK";
	set_loading_notice( false );
}
</script>

<script language="javascript">
function set_loading_notice( on )
{
	div_loading.innerHTML = loading_html;
	div_loading.style.display = on ? 'block' : 'none';
}
</script>
</head>

<xml id="island4result" />
<xml id="island4cat" />

<body>
	<div id="div_search">
		<div id="title" align="center">
			<fieldset>
				<legend>品类查询</legend>
				<table cellspacing="1" cellpadding="2" width="50%" align="left"
					class="tablecolorborder">
					<tr>
						<td class=altbg2><label>品类编码</label></td>
						<td class=altbg2><input type="text" id="txt_deptid" size="16" /></td>
						<td class=altbg2><label>品类名称</label></td>
						<td class=altbg2><input type="text" id="txt_deptname"
							size="16" /></td>
					</tr>
					<tr>
						<td><script>
					document.write( btn_search );
					btn_search.onClick = search_dept;
				</script></td>
					</tr>
				</table>
			</fieldset>
		</div>
	</div>
	<div id="div_loading" style="display: none;">
		<img src="../img/loading.gif"></img><font color=#003>正在下载,请等候……</font>
	</div>
	<div id="div_cat" style="display: block;"></div>
</body>
</html>