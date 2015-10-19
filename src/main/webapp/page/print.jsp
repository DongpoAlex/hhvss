<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	request.setCharacterEncoding( "UTF-8" );
	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title></title>
<style type="text/css">
.num-fmt {
	text-align: right;
}
</style>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet" type="text/css">
<link href="../css/main.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="../AW/runtime/lib/aw.js"> </script>
<script type="text/javascript" src="../js/common.js"> </script>
<script type="text/javascript" src="../js/Print.js"> </script>
<script type="text/javascript">
window.onload = function(){
	var p = new Print();
	p.init();
};

</script>

</head>

<body>
</body>
</html>