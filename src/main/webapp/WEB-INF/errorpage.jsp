<%@ page contentType="text/html; charset=UTF-8" language="java"
	session="false" isErrorPage="true"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统错误报告</title>
<style>
table {
	font-family: Tahoma, Verdana, Arial, 宋体;
	font-size: 12px;
	color: #000000 border-style: double;
	border-color: purple;
	border-width: 3;
	font-size: 12px;
}

body {
	scrollbar-base-color: #F5F5FF;
	scrollbar-arrow-color: #98B2CC;
	font-size: 12px;
	background-color: #98B2CC font-family: Verdana, Arial, Courier,
		sans-serif;
	background-repeat: no-repeat;
	background-position: 0% 20%;
	background-attachment: fixed;
}

#err_msg {
	color: navy;
}

.warning {
	width: 90%;
	background-color: yellow;
	border-style: solid;
	border-color: red;
	border-width: medium;
	position: absolute;
	top: 50px;
	left: 40px;
}
</style>
</head>

<body>
	<table align="center">
		<tr>
			<th>系统错误报告</th>
		</tr>
	</table>
	<div class="warning" align="center">
		<table align="center">
			<tr>
				<td>出错原因：<span id="err_msg"><%=exception%></span>
				</td>
			</tr>
			<tr>
				<td>您可以<a
					href="javascript:window.top.location.replace('../logon/logon.jsp')">点击此处重新登录</a>，也可以<a
					href="javascript:window.top.location.reload()">点击这里刷新页面.</a></td>
			</tr>
		</table>
	</div>
</body>
</html>
