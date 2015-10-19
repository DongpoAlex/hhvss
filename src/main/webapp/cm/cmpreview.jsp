<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.ex.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	String cmid = request.getParameter("cmid");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title></title>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/ReportGrid.js"> </script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<style type="text/css">
.aw-grid-control {
	height: 500px;
	width: 100%;
}

.aw-grid-row {
	height: 20px;
	border-bottom: 1px dashed #ccc;
}

.aw-grid-cell {
	border-right: 1px solid #eee;
}
</style>

<script language="javascript" type="text/javascript">
	var report = new ReportGrid();
	window.onload=function(){
		var url="../DaemonCM?operation=cminit&cmid=<%=cmid%>";
		report.initHTML(url);
	};

	function search(){
		var url="../DaemonCM?operation=cmpreview&cmid=<%=cmid%>";
		//var url="data";
		report.load(url);
	}
</script>

</head>
<body>
	<div id="divTitle"></div>
	<div id="divSearch">
		<input type='button' onclick='search()' value='查询'>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>
