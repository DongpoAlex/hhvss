<%@ page contentType="text/html; charset=UTF-8" language="java"
	session="false" import="java.util.*" import="java.sql.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.basic.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
request.setCharacterEncoding( "UTF-8" );

HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );
%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />

<script language="javascript">
</script>

</head>

<body>Welcome!
</body>
</html>
