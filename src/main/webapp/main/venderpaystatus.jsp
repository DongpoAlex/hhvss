<%@page contentType="text/html;charset=UTF-8" session="false"%>
<%@include file="../include/common.jsp"%>
<%@include file="../include/token.jsp"%>
<%@include file="../include/permission.jsp"%>
<%
    //用户的查询权限.
	token.checkPermission(moduleid,Permission.READ);
%>
<html>
<head>
<title>结算主体状态查询</title>
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
var service = 'VenderPayBalance';
window.onload=function(){
	query();
};

function query(){
	var url="../DaemonMain?operation=getFreaccPayshop&service="+service;

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
			var count = ajax.getCount();
			if(count==0){
				$("div_result").innerHTML="所有结算主体均可正常结算！";
			}else{
				var htmlOut = ajax.transformNode("../xsl/venderpaystatus.xsl");
				$("div_result").innerHTML=htmlOut;
			}
		}else{
			alert(ajax.getErrNote());
		}
	};
}

</script>

</head>

<body>
	<div id="title" align="center"
		style="font-size: 16px; font-family: '楷体_GB2312, 黑体'; color: blue; font-weight: bold";>供应商结算主体状态</div>
	<br></br>
	<div id="div_result" style="font-size: 14px;"></div>
</body>

</html>