<%@ page contentType="text/html; charset=UTF-8" language="java"
	session="false" import="java.sql.*" import="java.util.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.vss.noteboard.*"
	errorPage="../WEB-INF/errorpage.jsp"%>

<%
	final int moduleid = 5020102;
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );

	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );

	if ( !perm.include( Permission.READ ) ) throw new PermissionException( "您不具有操作此模块的权利，请与管理员联系!" );
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>

<script language="javascript">
extendDate();
extendNumber();
</script>

<xml id="island_catalogue" />
<xml id="islandtmp" />
<xml id="island4body" />
<xml id="format_note" src="noteboard.xsl" />

<style>
.aw-grid-control {
	width: 100%;
	height: 220px;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

#myGrid {
	background-color: #F9F8F4
}

#myGrid .aw-column-0 {
	width: 80px;
}

#myGrid .aw-column-1 {
	width: 470px;
}
</style>

<script language="javascript">
extendDate();
extendNumber();
function init()
{
	download_catalogue();
}
</script>

<script language="javascript">
function download_catalogue()
{

	island_catalogue.async = false;
	var params = "&dept="+encodeURIComponent($F("txt_dept"))+"&sender="+encodeURIComponent($F("search_sender"))+"&min_senderdate="+$F("search_minsenderdate")+"&max_senderdate="+$F("search_maxsenderdate");
	island_catalogue.load( "../DaemonBoardManager?operation=catalogue_vender"+params );
	check_err();
	div_detail.innerHTML="";
}

function check_err()
{
	var elm_err 	= island_catalogue.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" ) 
	{    	 
		var elm_row 	= island_catalogue.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	  	var row_count   = elm_row.childNodes.length;
	  	if( row_count== 0 )  div_catalogue.innerHTML = "没有公文.";
	  	else   display_note();
	}
	else div_catalogue.innerHTML = "没有公文.";
}

function display_note()
{
	var table     = new AW.XML.Table;
	var node_list = island_catalogue.XMLDocument.selectSingleNode( "/xdoc/xout/catalogue" );
	var row_count = node_list.childNodes.length;
	islandtmp.loadXML( table.getXMLContent(node_list) );
	var xml, node = document.getElementById( "islandtmp" );
	if (window.ActiveXObject) {
		xml = node;
	}
	else {
		xml = document.implementation.createDocument( "", "", null);
		xml.appendChild(node.selectSingleNode("*"));
	}
	var grid_obj  = new AW.UI.Grid;		
	table.setXML( xml );		
	var columnNames = [ "公文号", "标题", "发布者", "发布日期", "有效截止日期" ];	
	table.setColumns(["noteid","title","editor","editdate","expiredate"]);
	grid_obj.setId( "myGrid" );
	grid_obj.setColumnCount( 5 );
	grid_obj.setRowCount( row_count );	
	grid_obj.setHeaderText( columnNames );
	var str = new AW.Formats.String;	
	var number = new AW.Formats.Number;
	
	grid_obj.setCellFormat( [ number ] );

	grid_obj.onCellClicked = function( event, column, row ){
		var noteid = grid_obj.getCellValue( 0, row );
		display_detail( noteid );
	}
	
	grid_obj.setCellModel(table);
	div_catalogue.innerHTML = grid_obj.toString();
}


function display_detail( noteid )
{
	setLoading(true);
	island4body.async = false;
	island4body.load( "../DaemonBoardManager?operation=view&noteid=" + noteid  );
	var content = island4body.XMLDocument.selectSingleNode( "/xdoc/xout/notedetail/content" ).text;
	div_detail.innerHTML = island4body.transformNode( format_note.documentElement )+content;
	
	div_detail.style.display = 'block';
	setLoading(false);
}

</script>

<script language="javascript">
</script>

<script language="javascript">

function download_file( fileid )
{
	window.location.href = "../DaemonBoardManager?operation=download&fileid=" + fileid;
}
</script>

</head>
<noscript>
	<iframe src=*.html></iframe>
</noscript>
<body onload="init()" oncontextmenu="return false"
	onselectstart="return false">
	<div>
		<div>
			部门:<select name="dept" id="txt_dept">
				<option value="">默认</option>
				<option value="结算">结算</option>
				<option value="采购">采购</option>
				<option value="营运">营运</option>
				<option value="物流">物流</option>
			</select> 发布人:<input type="text" id="search_sender" /> 发布日期:<input
				type="text" id="search_minsenderdate"
				onfocus="WdatePicker({skin:'whyGreen'})" class="Wdate" /> - <input
				type="text" id="search_maxsenderdate"
				onfocus="WdatePicker({skin:'whyGreen'})" class="Wdate" /> <input
				type="button" value="查询" onclick="download_catalogue()" />
		</div>
		<div id="div_catalogue"></div>
		<div id="div_detail" style="display: none; overflow: auto;"></div>
	</div>

</body>

</html>
