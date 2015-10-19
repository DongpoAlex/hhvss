<%@ page contentType="text/html; charset=UTF-8" language="java"
	session="false" isErrorPage="true" import="java.io.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统错误报告</title>
<style>
body {
	font-size: 12px;
	margin: 0px;
}

A:HOVER {
	color: #456;
	background-color: #99eeff;
}
</style>
</head>
<body>
	<div style="text-align: center;">
		<div style="">
			<img alt="出错啦！" src="<%=request.getContextPath()%>/img/err.gif"
				height="50" width="50"> <span
				style="font-weight: bolder; color: #ff3333;">出错了！</span> <span
				style="margin-left: 20px;"><a
				href="javascript:window.top.location.replace( '../logon/logon.jsp' )">您可以点击此处重新登录</a></span>
		</div>
	</div>
</body>
</html>