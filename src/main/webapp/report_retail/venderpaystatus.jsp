﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../WEB-INF/errorpage.jsp"%>

<%
	final int moduleid = 3020111;
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
<title>供应商结算公司状态</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<style>
</style>

<script language="javascript" src="../AW/runtime/lib/aw.js"></script>
<script language="javascript" src="../js/ReportGrid.js"> </script>
<script language="javascript" src="../js/common.js"></script>
<script language="javascript">
var report = new ReportGrid();
var clazz = 'VenderPaystatus';
window.onload=function(){
};

function query(){
	var venderid = $("txt_venderid").value;
	if(venderid==''){
		alert("必须填写供应商编码");
		return;
	}
	var url="../DaemonSearchSheet?sheetname=VenderPaystatus&venderid="+venderid;

	setLoading(true);
	var parms = new Array();
	var ajax = new AW.XML.Table;
	ajax.setURL(url);
	ajax.setTable("xdoc/xout/rowset");
	ajax.request();
	ajax.response = function(text){
		setLoading(false);
		ajax.setXML(text);
		var errCode= ajax.getErrCode();
		if(errCode==0){
			var htmlOut = ajax.transformNode("venderpaystatus.xsl");
			$("div_result").innerHTML=htmlOut;
		}else{
			alert(ajax.getErrNote());
		}
	};
}
</script>

</head>
<body>
	<div id="title" align="center"
		style="font-size: 16px; font-family: '楷体_GB2312, 黑体'; color: blue; font-weight: bold";>供应商结算公司状态</div>

	<br></br>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader" width="25%">供应商编码</td>
			<td class="tableheader">查询</td>
		</tr>
		<tr>
			<td class="altbg2"><input type="text" id="txt_venderid" /></td>
			<td class="altbg2"><script type="text/javascript">
					var button = new AW.UI.Button;
					button.setControlText("查询");
					button.setControlImage( "search" );
					button.onClick = query;
					document.write( button );
					</script></td>
		</tr>
		<tr id="header1_toggle" class="singleborder">
			<td colspan="3"></td>
		</tr>
		<tr id="header2_toggle" class="whiteborder">
			<td colspan="3"></td>
		</tr>
	</table>
	<br></br>
	<div id="div_result" style="font-size: 14px;"></div>
</body>

</html>