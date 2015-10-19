<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%

request.setCharacterEncoding( "UTF-8" );
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>结算限额</title>

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>

<xml id="island4result" />

<script language="javascript">

/**
 * 此函数从后台取历史的结算限额
 */	
function search_his()
{
	var parms = new Array();
	if( $("txt_venderid").value != '' ){
		parms.push( "venderid=" + $("txt_venderid").value );
	}else{
		alert("请输入供应商编码");
		return;
	}	
	parms.push( "focus=payadvice_history" );
	parms.push( "timestamp=" + new Date().getTime() );
	var url = "../DaemonReportFiscal?" + parms.join( '&' );
	var courier = new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= function(text){
		
	};
	courier.call();
	setLoading( true );
}
	

</script>
<body onload="init()">
	<div id="title">查询供应商结算限额 －－ 零售商专用</div>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr>
			<td class="tableheader" width="30%">供应商编码</td>
			<td class="tableheader">查询</td>
		</tr>
		<tr id="header0_toggle" style="DISPLAY: block">
			<td class="altbg2"><input type="text" id="txt_venderid"></input></td>
			<td><script type="text/javascript">
					var button = new AW.UI.Button;
					button.setControlText("查询");
					document.write(button);
					button.onClick = search_his;
				</script></td>
		</tr>
	</table>
	<div id="div_report"></div>
</body>

</html>