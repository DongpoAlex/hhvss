<%@ page contentType="text/html; charset=UTF-8" language="java"
	session="false" import="java.sql.*" import="java.util.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.vss.noteboard.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	final int moduleid = 5020102;
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );

	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );

	if ( !perm.include( Permission.READ ) ) throw new PermissionException( "您不具有操作此模块的权利，请与管理员联系!" );
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/String.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>

<xml id="island4body" />
<xml id="format_note" src="noteboard.xsl" />

<script language="javascript">
	var uri = location.href;
	function init()
	{
		var note_id = uri.getQuery("note_id");
		if( note_id == null )
		{
			alert("没有这个公文！");
		}else{
			display_detail( note_id);
		}
		
	}
	
	function display_detail( noteid )
	{
		island4body.async = false;
		island4body.load( "../DaemonBoardManager?operation=view&noteid=" + noteid  );
		var content = island4body.XMLDocument.selectSingleNode( "/xdoc/xout/notedetail/content" ).text;
		div_detail.innerHTML = island4body.transformNode( format_note.documentElement )+content;
	}

</script>

<script language="javascript">

	function download_file( fileid )
	{
		window.location.href = "../DaemonBoardManager?operation=download&fileid=" + fileid;
	}
</script>

</head>
<noscript>
	<iframe src=*.html></iframe>
</noscript>
<body onload="init()" oncontextmenu="return false"
	onselectstart="return false">
	<div id="div_detail"></div>

</body>

</html>
