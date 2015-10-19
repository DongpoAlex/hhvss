<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.vss.VSSConfig"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
 %>

<%
	String sheetid = request.getParameter("sheetid");
	sheetid = ( sheetid == null || sheetid.length() == 0 )? "" : sheetid.trim();
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>退货通知单明细-零售端</title>
<!-- ActiveWidgets stylesheet and scripts -->

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<script language="javascript">
extendDate();
extendNumber();
</script>

<xml id="island4detail" />
<xml id="island4format" src="retnotice_print.xsl" />
<script>
var sheetid = "<%=sheetid%>";
</script>
<style>
BODY {
	FONT-SIZE: 12px;
	SCROLLBAR-ARROW-COLOR: #ecebeb;
	SCROLLBAR-BASE-COLOR: #ffffff;
	BACKGROUND-COLOR: #ffffff
}

.tableborder {
	BORDER-COLOR: black 1px solid;
	BORDER-RIGHT: black 1px solid;
	BORDER-TOP: black 1px solid;
	BACKGROUND: black;
	BORDER-LEFT: black 1px solid;
	BORDER-BOTTOM: black 1px solid
}
</style>

<style>
div.box {
	border-style: solid;
	border-color: black;
	border-width: 1;
	width: 740;
}

div.box div {
	margin: 5px;
}

div.sheethead {
	margin: 5px;
}

span.sheethead {
	padding: 10px;
}

label {
	font-size: 11px;
	font-weight: bold;
	color: navy;
}
</style>

<script>
extendDate();
extendNumber();
function init()
{
	init_dateil();
}
</script>

<script>
function init_dateil()
{
	var url = "../DaemonViewHisSheet?sheet=retnotice&sheetid=" + sheetid;
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= display_detail;
	courier.call();	
	window.status="装载完毕!";
}

function display_detail( text )
{	
	island4detail.loadXML( text );
	var elm_majorid=island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/majorid" );
	var elm_printcompany=island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/printcompany" );
	var elm_yancaoFlag=island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/yancaoflag" );
	var controltype = island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/controltype" ).text;
	
		//如果是MR门店
	if(controltype==2 ){
		div_title.innerHTML='<b>MR－供应商退货通知单明细</b>';
		print_logo.src="<%=VSSConfig.getInstance().getPrintTobatoLogo()%>";
	}else{
		/**
		*如果是烟草公司
		*/
		if(elm_majorid.text==28&&elm_yancaoFlag.text==1){
		div_title.innerHTML='<b>'+elm_printcompany.text+'－供应商退货通知单明细</b>';
		print_logo.src="<%=VSSConfig.getInstance().getPrintTobatoLogo()%>";
		}
		else{
		div_title.innerHTML='<b><%=token.site.getTitle()%>－供应商退货通知单明细</b>';
		print_logo.src="../img/<%=token.site.getLogo() %>";
		}		
	}
	div_detail.innerHTML = island4detail.transformNode( island4format.documentElement );
}
</script>


</head>

<body onload="init()">
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<b><img id="print_logo" src="" /><a href="###"
		onclick="this.style.visibility='hidden';window.print();this.style.visibility='visible'">[打印本页]</a></b>
	<div id="div_title" align="center" style="font-size: 18px;";></div>
	<div id="div_sheetname" align="right" style="font-size: 13px;";>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
	<div id="div_detail"></div>
</body>
</html>