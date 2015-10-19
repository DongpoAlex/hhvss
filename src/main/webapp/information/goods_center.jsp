﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3080112;
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
	height: 450px;
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

#grid_cat .aw-column-0 {
	width: 80px;
}

#grid_cat .aw-column-2 {
	width: 260px;
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
	top: 105px;
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
function search_goods()
{
	set_loading_notice( true );
	var parms = new Array();
	if( txt_goodsid_min.value != '' )    parms.push( "goodsid_min=" + txt_goodsid_min.value );
	if( txt_goodsid_max.value != '' )    parms.push( "goodsid_max=" + txt_goodsid_max.value );
	if( txt_deptid.value != '' )   	     parms.push( "deptid=" + txt_deptid.value );
	if( txt_goodsname.value != '' )      parms.push( "goodsname=" + encodeURIComponent(txt_goodsname.value) );

	parms.push( "focus=goods" );
	parms.push( "timestamp=" + new Date().getTime() );
	set_loading_notice( true );
		
	var url = "../DaemonInformation?" + parms.join( '&' );
	var courier = new AjaxCourier( url );
	//alert(url)
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
		if( row_count==0 ){
			div_cat.innerHTML = "没有找到你要的商品资料。";
			set_loading_notice( false );
		}else{
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
	var columnNames=[ "商品编码", "条形码", "商品名称", "商品简写", "规格", "单位", "类别", 
		"类别名称", "生产厂家","批准文号","状态"];
	table.setColumns(["goodsid","barcode","goodsname","shortname","spec","unitname","deptid","majorid","manufacturer","approvalnum"]);
	var obj = new AW.UI.Grid;
	obj.setId( "grid_cat" );
	obj.setColumnCount( columnNames.length );
	obj.setRowCount( row_count );	
	obj.setHeaderText( columnNames );	
	obj.setCellModel(table);
	div_cat.innerHTML = obj.toString();
	window.status = "OK";
	set_loading_notice( false );
}
</script>

<script language="javascript">
function check_goodsid( ctrl )
{
	var value = ctrl.value;
	if( value == '' ) return true;
	if( isNaN ( value ) ) {
		alert( '必须是数字！' );
		return false;
	}
	var id = parseInt( value )
	ctrl.value = '' + id;
	return true;
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
				<legend>
					<font size="2px">商品查询 
				</legend>
				<table cellspacing="1" cellpadding="2" width="50%" align="left"
					class="tablecolorborder">
					<tr>
						<td class=altbg2><label>商品编码</label></td>
						<td class=altbg2><input type="text" id="txt_goodsid_min"
							size="16" onblur="check_goodsid(this);" /> --<input type="text"
							id="txt_goodsid_max" size="16" onblur="check_goodsid(this)" /></td>
					</tr>
					<tr>
						<td class=altbg2><label>品类编码</label></td>
						<td class=altbg2><input type="text" id="txt_deptid" size="16" /></td>
						<td class=altbg2><label>商品名称</label></td>
						<td class=altbg2><input type="text" id="txt_goodsname"
							size="16" /></td>
					</tr>
					<tr>
						<td><script>
					document.write( btn_search );
					btn_search.onClick = search_goods;
				</script></td>
					</tr>
				</table>
				<fieldset style="text-align: left;">提示：
					1.商品范围查询时，如果你只输入商品范围的最小值，而没有输入商品范围的最大值，得到的结果可能很大，这时系统只显示1000
					条记录。2.商品名称，支持模糊匹配。例如你输入"面包"，则会查询商品名称中有"面包"字样的所有商品。</fieldset>
			</fieldset>
		</div>

		<div id="div_loading" style="display: none;">
			<img src="../img/loading.gif"></img><font color=#003>正在下载,请等候……</font>
		</div>
		<div id="div_cat" style="display: block;"></div>
</body>
</html>