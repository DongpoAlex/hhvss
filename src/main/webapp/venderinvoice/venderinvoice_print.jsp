<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	final int moduleid=3020105;
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
String sheetid = request.getParameter("sheetid");
if ( sheetid ==null || sheetid.length() == 1 )
	throw new InvalidDataException("没有发票id，请检查！");
%>


<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>发票信息列表</title>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
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
<xml id="island4result" />
<xml id="island4req"> <xdoc> <xout> </xout> </xdoc></xml>
<xml id="island4format" src="venderinvoice_print.xsl" />

<script language="javascript">
var uri = location.href;
function $(id){
	return document.getElementById(id);
}
//获得地址栏制定的参数
String.prototype.getQuery = function(name)
{
  var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
  var r = this.substr(this.indexOf("\?")+1).match(reg);
  if (r!=null) return unescape(r[2]); return null;
};
/*
*初始化，查询详细信息
*/
function init(){
	try{
		var parms = new Array();
			parms.push( "sheetid="+uri.getQuery("sheetid") );
			parms.push( "sheetname=venderinvoice" );
			parms.push( "operation=showdetail" );
			var url = "../DaemonInvoiceAdm?" + parms.join( "&" );
			var courier 			= new AjaxCourier( url );
			courier.island4req  	= island4req;
			courier.reader.read 	= showList;
			courier.call();	
	}catch(e){alert("showInoiveItem:"+e)}
}

function showList(text){
try{
	island4result.loadXML( "" );
	island4result.loadXML( text );
	
	var elm_booktypeid=island4result.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/booktypeid" );
	var elm_booktitle=island4result.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/booktitle" );
	var elm_booklogofname=island4result.XMLDocument.selectSingleNode( "/xdoc/xout/sheet/head/row/booklogofname" );
	div_title.innerHTML='<b>'+elm_booktitle.text+'－供应商发票录入明细单</b>';
	print_logo.src="../img/"+elm_booklogofname.text;
	
	
	var elm_err = island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr = parseXErr( elm_err );
	if(xerr.code != "0" ){
		$("invoiceList").innerHTML = "错误:"+xerr.note;
		return false;
	}else{
		$("invoiceList").innerHTML = island4result.transformNode(island4format.documentElement);
		
	}
}catch(e){alert("showInvoice:"+e)}


}

</script>

</head>
<body onload="init()">
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<b><img id="print_logo" src="" /><a href="###"
		onclick="this.style.visibility='hidden';window.print();this.style.visibility='visible'">[打印本页]</a></b>
	<div id="div_title" align="center" style="font-size: 18px;";></div>
	<div id="div_sheetname" style="font-size: 13px;">
		<br />

	</div>
	<div id="invoiceList"></div>
</body>
</html>