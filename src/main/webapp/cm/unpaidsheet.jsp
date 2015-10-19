<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 9000100;
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
	
	SelPayShop sel_payshop = null;
	SelBook sel_book = null;
	if(token.site.getSid()==11){
		sel_payshop = new SelPayShop(token);
		sel_payshop.setAttribute( "id", "txt_payshopid" );
		sel_payshop.setAttribute("name","txt_parms");
	}else{
		sel_book = new SelBook(token);
		sel_book.setAttribute( "id", "txt_bookno" );
		sel_book.setAttribute("name","txt_parms");
	}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>结算单据查询</title>
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
var clazz = 'Unpaidsheet';
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
	return autoCheck();
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
		<% if(!token.isVender){ %>
		<div class="search_parms">
			供应商编码: <input type="text" id="txt_venderid" name="txt_parms"
				split="," alt="供应商编码" />
		</div>
		<% } %>
		<div class="search_parms">
			单据类型: <select id="txt_sheettype" name="txt_parms">
				<% if(token.site.getSid()==5){ %>
				<option value="2301">验收单</option>
				<option value="2323">退货单</option>
				<option value="2506">促销折让单</option>
				<option value="2445">批次更正单</option>
				<option value="5205">联营销售单</option>
				<% }if(token.site.getSid()==7 || token.site.getSid()==6){ %>
				<option value="2301">验收单</option>
				<option value="2323">退货单</option>
				<option value="2506">促销折让单</option>
				<option value="2445">批次更正单</option>
				<option value="5205">联营销售单</option>
				<option value="5210">结算调整单</option>
				<option value="5211">成本调整</option>
				<option value="5207">租赁租金</option>
				<option value="5258">MALL费用</option>
				<% }if(token.site.getSid()==10){ %>
				<option value="2301">验收单</option>
				<option value="2323">退货单</option>
				<option value="5205">联营销售单</option>
				<option value="5210">结算调整单</option>
				<option value="5211">成本调整</option>
				<option value="5207">租赁租金</option>
				<option value="5258">MALL费用</option>
				<% }if(token.site.getSid()==11){ %>
				<option value="">全部</option>
				<option value="5205">联营租赁销售结算单</option>
				<option value="5211">成本调整单</option>
				<option value="5216">结算调整单</option>
				<option value="2506">促销折让分摊单</option>
				<% }else{ %>
				<option value="2301">验收单</option>
				<option value="2323">退货单</option>
				<%} %>
			</select>
		</div>
		<%if(token.site.getSid()==11){ %>
		<div class="search_parms">
			状态: <select name="txt_parms" id="txt_payflag">
				<option value="">全部</option>
				<option value="1">未结算</option>
				<option value="2">已抽单</option>
				<option value="6">已结算</option>
				<option value="0">冻结</option>
			</select>
		</div>
		<div class="search_parms">
			结算公司:<%=sel_payshop%></div>
		<%}else{ %>
		<div class="search_parms">
			公司:<%=sel_book%></div>
		<%} %>
		<div class="search_parms">
			门店:<input type="text" id="txt_shopid" size="12" split=","
				name="txt_parms" /><a href="javascript:showShopList('txt_shopid')">选择门店</a>
		</div>
		<div class="search_parms">
			单据日期范围（60天内）: <input type="text" id="txt_docdate_min" class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_docdate_max\',{d:-60});}',maxDate:'#F{$dp.$D(\'txt_docdate_max\',{d:0});}'})"
				name="txt_parms" notnull="notnull" alt="最小单据日期" /> - <input
				type="text" id="txt_docdate_max" class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_docdate_min\',{d:0});}',maxDate:'#F{$dp.$D(\'txt_docdate_min\',{d:60});}'})"
				name="txt_parms" notnull="notnull" alt="最大单据日期" />
		</div>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>