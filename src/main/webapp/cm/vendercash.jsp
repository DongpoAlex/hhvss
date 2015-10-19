<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 9000103;
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
		sel_payshop.setAttribute("alt","结算公司");
	}else{
		sel_book = new SelBook(token);
		sel_book.setAttribute( "id", "txt_bookno" );
		sel_book.setAttribute("name","txt_parms");
	}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>预收款流水查询</title>
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

.warn {
	margin: 4px;
	padding: 6px;
	border: 1px solid #f00;
	font: bold;
	background-color: #fee
}
</style>

<script language="javascript" type="text/javascript">
var report = new ReportGrid();
var clazz = 'VenderCash';
window.onload=function(){
	var button = new AW.UI.Button;
	button.setControlText("查询");
	button.setControlImage( "search" );
	button.onClick = search_report;
	$('div_button_search').innerHTML=button;
	$('div_button_excel').innerHTML="&rarr;<a href='javascript:downExcel()'>导出Excel</a>";
	var url="../DaemonCM?operation=cminit&cmid=<%=cmid%>";
	report.initHTML(url);

	//预警状态链接过来，自动展示预警信息
	if(window.location.href.getQuery("g_status")==0){
		var url="../DaemonCM?operation=dynamicFunction&functionname=getWarn&class="+clazz+"&cmid=<%=cmid%>&";
		setLoading(true);
		var table = new AW.XML.Table;
		table.setURL( url );
		table.request();
		table.response = function( text ){
			//alert(text.xml)
			setLoading(false);
			table.setXML(text);
			var html = "提醒：<br>";
			var cols = table.getXML().selectNodes("xdoc/xout/rowset/row");
			if(cols==null){return;}
			for ( var i = 0; i < cols.length; i++) {
				var col = cols[i];
				var payshopid = col.selectSingleNode("payshopid").text;
				var payshopname = col.selectSingleNode("payshopname").text;
				var chargeamt = col.selectSingleNode("chargeamt").text;
				var recamt = col.selectSingleNode("recamt").text;
				var chay = Number(chargeamt) - Number(recamt);
				html += "贵公司于结算主体："+payshopid+" &nbsp; "+payshopname+"，应缴费用、滞纳金合计金额为："+chargeamt+"元，当前商户账户余额为："+recamt+"元，尚欠差异金额为："+chay+"。请及时补充账户余额，谢谢！<br>";
			}
			$("divWarn").innerHTML=html;
			$("divWarn").className="warn";
		};
		//search_report();
	}
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
				notnull="notnull" split="," alt="供应商编码" />
		</div>
		<% } %>
		<div class="search_parms">
			日期范围（60天内，必填）: <input type="text" id="txt_docdate_min" class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_docdate_max\',{d:-60});}',maxDate:'#F{$dp.$D(\'txt_docdate_max\',{d:0});}'})"
				name="txt_parms" notnull="notnull" alt="最小单据日期" /> - <input
				type="text" id="txt_docdate_max" class="Wdate"
				onFocus="WdatePicker({minDate:'#F{$dp.$D(\'txt_docdate_min\',{d:0});}',maxDate:'#F{$dp.$D(\'txt_docdate_min\',{d:60});}'})"
				name="txt_parms" notnull="notnull" alt="最大单据日期" />
		</div>
		<div class="search_parms">
			结算公司:<%=sel_payshop%></div>
		<div class="search_parms">
			单据编码: <input type="text" id="txt_sheetid" name="txt_parms" split=","
				alt="单据编码" />
		</div>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
		</div>
	</div>
	<div id="divWarn" style=""></div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>