<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 9000001;
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
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title></title>
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
var clazz = 'Test';
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
	if( $("txt_min_sdate").value == '' ){
		alert("必须指定最小销售日期");
		$("txt_min_sdate").focus();
		return false;
	}
	if( $("txt_max_sdate").value == '' ){
		alert("必须指定最大销售日期");
		$("txt_max_sdate").focus();
		return false;
	}
	var min_sdate = parseDate($("txt_min_sdate").value);
	var max_sdate = parseDate($("txt_max_sdate").value);
	
	if(min_sdate > max_sdate){
		alert("起始日期不能大于结束日期！");
		return false;
	}
	
	if((max_sdate-min_sdate)/86400000 > 60 ){	
		alert("只能查询60天内数据，请修改日期范围！");
		return false;
	}
	if( $("txt_venderid").value == '' ){
		alert("必须填写供应商编码");
		return false;
	}
	return true;
}

function cookParms(){
	var parms = new Array();
	var shopid = $("txt_shopid").value;
	if( shopid !=null && shopid.length>1 ){
		var arrShopid = shopid.split(',');
		for(var i=0; i<arrShopid.length; i++){
			parms.push( "shopid="+arrShopid[i]);
		}
	}
	if( $("txt_venderid").value != '' ){
		parms.push( "venderid="+ $("txt_venderid").value );
	}
	if( $("txt_goodsid").value != '' ) parms.push( "goodsid="+ $("txt_goodsid").value );
	if( $("txt_min_sdate").value != '' ) parms.push( "min_sdate="+ $("txt_min_sdate").value );
	if( $("txt_max_sdate").value != '' ) parms.push( "max_sdate="+ $("txt_max_sdate").value );
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
			<span style="color: red; font: bold">*</span>供应商编码: <input
				type="text" id="txt_venderid" />
		</div>
		<div class="search_parms">
			门店:<input type="text" id="txt_shopid" size="12" /><a
				href="javascript:showShopList('txt_shopid')">选择门店</a>
		</div>
		<div class="search_parms">
			商品编码: <input type="text" id="txt_goodsid" />
		</div>
		<div class="search_parms">
			<span class="required">*</span>销售日期范围（60天内）: <input type="text"
				id="txt_min_sdate" onblur="checkDate(this)" size="12" /> <span
				class="wdatepicker" onclick="WdatePicker({el:'txt_min_sdate'})"></span>
			- <input type="text" id="txt_max_sdate" onblur="checkDate(this)"
				size="12" /> <span class="wdatepicker"
				onclick="WdatePicker({el:'txt_max_sdate'})"></span>
		</div>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>