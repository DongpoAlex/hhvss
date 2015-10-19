﻿<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	HttpSession session = request.getSession( false );	
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT ); 
	request.setCharacterEncoding("UTF-8");
	String fileid = request.getParameter("fileid");
	String info	  = request.getParameter("info");
	if(info != null){
		String a = request.getQueryString();
		String b = a.substring(a.indexOf("info"));
		String c = b.substring(b.indexOf("=")+1);
		info   = java.net.URLDecoder.decode(c, "UTF-8");
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style type="text/css">
body {
	margin: 0px;
	padding: 0px;
}

form {
	margin: 0px;
	padding: 0px;
}
</style>
<script type="text/javascript">
function checkUp(){
	if( formUpload.sfilename.value == ""){
		alert("请选择需上传的文件！");
		return false;
	}
	formUpload.submit();
}
</script>
</head>
<body>
	<%
	if( info == null || fileid == null )
	{
%>
	<form id="formUpload" enctype="multipart/form-data"
		action="../DaemonByeUpLoad?operation=new" method="post">
		<input name="sfilename" type="file" />&nbsp; <input type="button"
			id="btnsubmit" onclick="checkUp()" value="上传" /> &nbsp;&nbsp;附件最大为4M
	</form>
	<% 
	}else{
%>
	邮件中已包含附件：<%=info%><input id="up_fileid" type="hidden"
		value="<%=fileid%>" />
	<%	} %>
</body>
</html>