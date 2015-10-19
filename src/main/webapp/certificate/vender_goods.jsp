<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid=8000018;
%>

<%
request.setCharacterEncoding( "UTF-8" );

HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
//查询用户的权限.
token.checkPermission(moduleid,Permission.READ);

String cmid = request.getParameter("cmid");
if(cmid.equals("0")){
	throw new Exception("菜单没有定义合适的显示方案，该模块暂停访问");
}

SelBook sel_book = new SelBook(token);
sel_book.setAttribute( "id", "txt_bookno" );
sel_book.setAttribute("name","txt_parms");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>供货关系证照一览</title>
<script language="javascript" src="../js/Date.js" type="text/javascript"></script>
<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/ReportGrid.js"> </script>
<script language="javascript" src="../js/common.js"> </script>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<!-- grid format -->
<style type="text/css">
.aw-grid-control {
	height: 75%;
	width: 100%;
	border: none;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

.aw-mouseover-row {
	background: #efefff;
}
</style>

<script language="javascript" type="text/javascript">
var report = new ReportGrid();
var clazz = 'CerVenderGoods';
window.onload=function(){
	var button = new AW.UI.Button;
	button.setControlText("查询");
	button.setControlImage( "search" );
	button.onClick = search_report;
	$('div_button_search').innerHTML=button;
	$('div_button_excel').innerHTML="&rarr;<a href='javascript:downExcel()'>导出Excel</a>";
	var url="../DaemonCM?operation=cminit&cmid=<%=cmid%>";
	report.initHTML(url);
};

function search_report()
{
	if(!check()){return;}
	var url=cookQueryURL();
	report.load(url);
}

function downExcel(){
	if(!check()){return;}
	window.location.href = cookDownURL();

}

function check(){
	var rel = autoCheck();
	return rel;
}

function cookParms(){
	var parms = autoParms();
	return parms;
}
function cookQueryURL(){
	var url="../DaemonCM?operation=cmload&class="+clazz+"&cmid=<%=cmid%>&" + cookParms().join( "&" );
	return url;
}

function cookDownURL(){
	return url  = "../DaemonDownloadReport?reportname=cmexcel&class="+clazz+"&cmid=<%=cmid%>&" + cookParms().join( "&" );
}

</script>
</head>
<body>
	<div id="divTitle"></div>
	<div id="divSearch" class="search_main">
		<div class="search_parms">
			验收门店:<input type="text" id="txt_shopid" name="txt_parms"
				notnull="notnull" alt="门店" size="12" /><a
				href="javascript:showShopList('txt_shopid')">选择门店</a>
		</div>
		<% if(!token.isVender){ %>
		<div class="search_parms">
			供应商编码: <input type="text" id="txt_venderid" name="txt_parms"
				notnull="" alt="供应商编码" split="," />逗号分隔
		</div>
		<% } %>
		<div class="search_parms">
			商品条码: <input type="text" id="txt_barcode" name="txt_parms" notnull=""
				alt="商品条码" split="," />逗号分隔
		</div>
		<div class="search_parms">
			商品编码: <input type="text" id="txt_goodsid" name="txt_parms" notnull=""
				alt="商品编码" split="," />逗号分隔
		</div>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>