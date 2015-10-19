<%@page contentType="text/html;charset=UTF-8" session="false"%>

<%@page import="com.royalstone.listener.OnlineUserListener"%>
<%@page import="com.royalstone.listener.SessionCountListenter"%>
<%@page import="java.util.*"%>
<%
int onlineCount=0,sessionCount=0,maxSessionCount=0,totalCount=0;
SessionCountListenter sessionListenter = (SessionCountListenter)application.getAttribute("sessionCounter");

if(sessionListenter!=null){
	sessionCount = sessionListenter.getCurrentSessionCount();
	maxSessionCount = sessionListenter.getMaxSessionCount();
}

Map onlineUserMap = (Map)application.getAttribute("onlineUserMap");
if(onlineUserMap!=null){
	onlineCount=onlineUserMap.size();
}

System.getenv("name");
%>
<%@page import="com.royalstone.security.Token"%><html>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>在线统计</title>
</head>
<body>
	<div>
		登陆人数：<%=onlineCount%></div>
	<div>
		最大登陆人数：<%=maxSessionCount%></div>
	<table border="1" cellpadding="2" cellspacing="0" width="100%">
		<caption>用户列表</caption>
		<tr>
			<th>站点</th>
			<th>用户</th>
			<th>操作模块</th>
			<th>上次操作时间</th>
			<th>登陆时间</th>
		</tr>
		<%
if(onlineUserMap!=null && onlineUserMap.size()>0){
	Set keySet = onlineUserMap.keySet();
	Iterator it = keySet.iterator();
	while(it.hasNext()){
		String key = (String)it.next();
		HttpSession sess = (HttpSession)onlineUserMap.get(key);
		Token token = (Token)sess.getAttribute("TOKEN");
		if(token!=null){
%>
		<tr>
			<td><%=token.site.getSiteName() %></td>
			<td><%=token.loginid %></td>
			<td></td>
			<td></td>
			<td><%=token.getLoginTime() %></td>
		</tr>
		<%
		}
	}
}
%>
	</table>
</body>
</html>