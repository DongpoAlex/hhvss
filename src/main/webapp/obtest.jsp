<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	SessionID:<%=session.getId()%>
	<BR> SessionIP:<%=request.getServerName()%>
	<BR> SessionPort:<%=request.getServerPort()%>
	<%  
out.println("This is Tomcat Server "+request.getLocalName()+":"+request.getLocalPort()+"！");  
%>
</body>
</html>