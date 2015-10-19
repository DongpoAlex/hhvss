<%@ page contentType="text/html; charset=UTF-8" language="java"
	session="false" import="java.sql.*" import="java.util.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.vss.noteboard.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 5020101;
%>
<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );
	if ( !perm.include( Permission.INSERT ) ) 
		throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
%>

<%
	String operation = request.getParameter( "operation" );
	operation = ( operation==null ) ? "" : operation.trim();
%>


<%	

	if( operation.equals( "upload" ) ) {
		Connection conn = XDaemon.openDataSource( token.site.getDbSrcName() );
		Map map_file = BoardManager.saveFileItems( conn, request );
		
		session.setAttribute( "FILEMAP", map_file );
		response.sendRedirect( "noteboard_add_02.jsp" );
		conn.close();
	}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link rel="stylesheet" href="../css/style.css" type="text/css" />

<script language="javascript">
</script>

</head>

<body>

	<h4>请上传公文挂载的附件，如果不需要挂载附件，直接点上传按钮</h4>

	<form enctype="multipart/form-data"
		action="noteboard_add_01.jsp?operation=upload" method="post">
		<input name="filename" TYPE="file" size="30" /> <br /> <input
			name="filename" TYPE="file" size="30" /> <br /> <input
			name="filename" TYPE="file" size="30" /> <br /> <input
			type="submit" value=" 上传 " />
	</form>


</body>
</html>
