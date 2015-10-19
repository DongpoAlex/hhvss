<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.util.*" import="com.royalstone.security.*"
	errorPage="../WEB-INF/errorpage.jsp"%>

﻿
<?xml version="1.0" encoding="UTF-8"?>

<%
try{
	HttpSession session = request.getSession( false );
	if( session != null ) session.invalidate(); 
} catch ( Exception e ) {}

%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />

<script language="javascript">

function main()
{
	parent.location.href = "../logon/logon.jsp" ;
}

</script>

</head>

<body onload="main()">退出系统...

</body>
</html>
