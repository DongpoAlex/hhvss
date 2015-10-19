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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>供应商应付帐余额</title>
<!-- ActiveWidgets stylesheet and scripts -->
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>

<style type="text/css">
.aw-grid-control {
	height: 76%;
	width: 98%;
	border: none;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

#myGrid {
	background-color: #F9F8F4
}

#myGrid .aw-column-0 {
	width: 0px;
}

#myGrid .aw-column-1 {
	width: 130px;
	cursor: pointer;
}

#myGrid .aw-column-2 {
	cursor: pointer;
	width: 60px;
}

#myGrid .aw-column-3 {
	cursor: pointer;
	width: 80px;
}

#myGrid .aw-column-6,#myGrid .aw-column-7 {
	text-align: right;
}

#myGrid .aw-column-10 {
	width: 0px
}

#myGrid .aw-column-11 {
	width: 0px
}
</style>

<script language="javascript" type="text/javascript">

var attributeOfNewWnd = "status=yes,toolbar=no,menubar=no,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);

</script>
</head>

<body>
</body>
</html>