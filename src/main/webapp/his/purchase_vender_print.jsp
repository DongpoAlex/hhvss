<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="java.util.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
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


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>订单打印-供应商用</title>
<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<xml id="island4detail" />
<xml id="island4format" src="format4purchase4print.xsl" />
<script>
var str_sheetid = "<%=sheetid%>";
</script>
<style>
BODY {
	FONT-SIZE: 12px;
	SCROLLBAR-ARROW-COLOR: #ecebeb;
	SCROLLBAR-BASE-COLOR: #ffffff;
	BACKGROUND-COLOR: #ffffff;
	margin: 4px;
}

.tableborder {
	BORDER-COLOR: black 1px solid;
	BORDER-RIGHT: black 1px solid;
	BORDER-TOP: black 1px solid;
	BACKGROUND: black;
	BORDER-LEFT: black 1px solid;
	BORDER-BOTTOM: black 1px solid
}

HR {
	margin-top: 20px;
	margin-bottom: 20px;
	border: 2px dashed #000000;
}
</style>

<script>
function init(){
	
	var arr_sheetid = str_sheetid.split(",");
	
	for( var i=0; i<arr_sheetid.length; i++ ){
		read_sheet(arr_sheetid[i]);
	}

}
function read_sheet( current_sheetid )
{
	setLoading(true);
	var url = "../DaemonViewHisSheet?sheet=purchase&sheetid=" + current_sheetid;
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= display_detail;
	courier.call();

}

function display_detail( text )
{
	island4detail.loadXML( text );
	//alert(text)
	var elm_majorid=island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/sgroupid" );	
	var elm_printcompany=island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/printcompany" );
	var elm_yancaoFlag=island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/yancaoflag" );
	var dhtml ="";
	var sheetid = island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/sheetid" ).text;
	var logistics = island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/logistics" ).text;
	var controltype = island4detail.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/controltype" ).text;
	
		//如果是MR门店
	if(controltype==2 && logistics==1){
		dhtml = '<table width="100%" border="0"><tr><td width="170"><img id="print_logo" src="<%=VSSConfig.getInstance().getPrintTobatoLogo()%>"  style="margin-left:15px;"/></td>'+
			'<td align="center"><div id="title" align="center" style="font-size: 18px;font-weight: bold;";>MR订货通知单</div></td>'+
			'<td width="241" align="right"><div style="float: right;"><img id="barcode" src="../BarCode?code='+sheetid+'"  /></div></td></tr></table>';
	}else{
		/**
		*如果是烟草公司
		*/
		if(elm_majorid.text==28&&elm_yancaoFlag.text==1){
			dhtml = 
			'<table width="100%" border="0"><tr><td width="170"><img id="print_logo" src="<%=VSSConfig.getInstance().getPrintTobatoLogo()%>"  style="margin-left:15px;"/></td>'+
			'<td align="center"><div id="title" align="center" style="font-size: 18px;font-weight: bold;">'+elm_printcompany.text+'订货通知单'+'</div></td>'+
			'<td width="241"><div style="float: right;"><img id="barcode" src="../BarCode?code='+sheetid+'"  /></div></td></tr></table>';
		}
		else{
			dhtml = '<table width="100%" border="0"><tr><td width="170"><img id="print_logo" src="<%=token.site.getLogo()%>"  style="margin-left:15px;"/></td>'+
			'<td align="center"><div id="title" align="center" style="font-size: 18px;font-weight: bold;";><%=token.site.getTitle()%>订货通知单</div></td>'+
			'<td width="241" align="right"><div style="float: right;"><img id="barcode" src="../BarCode?code='+sheetid+'"  /></div></td></tr></table>';
		}
	}
	var html = island4detail.transformNode( island4format.documentElement );
	var elm_sheet = document.createElement("div");
	elm_sheet.innerHTML = dhtml + html + "<hr>";
	document.body.appendChild( elm_sheet );
	setLoading(false);
}
</script>
</head>

<body onload="init()">
	<a href="###" onclick="window.print();this.style.display='none'"
		style="margin-left: 15px; font-weight: bold;">[打印本页]</a>
</body>

</html>