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
	String operation	= request.getParameter( "operation" );
	String expiredate	= request.getParameter( "expiredate" );
	String note_title 	= request.getParameter( "note_title" );
	String note_content 	= request.getParameter( "note_content" );
	String istop 	= request.getParameter( "istop" );
	String ismobile 	= request.getParameter( "ismobile" );
	String dept 	= request.getParameter( "dept" );
	istop 	= ( istop==null || istop.length()==0 ) ? "0" : istop;
	ismobile 	= ( ismobile==null || ismobile.length()==0 ) ? "0" : ismobile;
	expiredate 	= ( expiredate==null ) ? "" : expiredate.trim();
	operation 	= ( operation==null ) ? "" : operation.trim();
	note_title 	= (note_title==null) ? 	"" : note_title.trim();
	note_content 	= (note_content==null) ? "" : note_content.trim();
	
	String ok = "false";
	
	
	Map   map_file = ( Map ) session.getAttribute( "FILEMAP" );
	
	int   file_count = 0;
	String lst_file = "" ;
	if( map_file != null ) {
		file_count = map_file.size();
		lst_file   = BoardManager.toListString( map_file );
	}

	if( operation.equals ( "save" ) ) 
	{
		BoardManager manager = new BoardManager( token );
		Connection conn = XDaemon.openDataSource( token.site.getDbSrcName() );
		conn.setAutoCommit(false);
		int noteid = manager.saveTitle( conn, note_title, expiredate,istop,dept,ismobile );
		manager.saveContent( conn, noteid, note_content );

		if( map_file != null ) manager.saveNoteFile( conn, noteid, map_file );
		ok = "true";
		conn.commit();
		conn.setAutoCommit(true);
		conn.close();
	}
	
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link rel="stylesheet" href="../css/style.css" type="text/css" />

<script language="javascript" src="../js/Date.js"></script>
<script type="text/javascript" src="../ckeditor/ckeditor.js"></script>
<script language="javascript">
extendDate();
</script>


<script language="javascript">
function init()
{
	if( <%=ok%> ) {
		alert( "上传成功!" );
		location.href = "noteboard_center.jsp";
	} else {
		div_main.style.display = 'block';
	}
}

</script>

<script language="javascript">
function check_null()
{
	if( save_content.expiredate.value == "" )
	{
		alert( "有效日期不能为空." );
		return;
	}
	else if( save_content.note_title.value == "" ) {
		alert( "标题不能为空." );
		return;
	} 
	else
	{
		save_content.submit();
	}
}

</script>
</head>

<body onload="init()">

	<div id="div_main" style="display: none;">
		<form id="save_content" target="_self" method="post">
			<input type="hidden" name="operation" value="save" />
			<div>
				<label>公文标题:</label> <input id="note_title" name="note_title"
					type="text" value='' size="40" maxlength="30" />
			</div>
			<div>
				<label>截止日期:</label> <input id="expiredate" name="expiredate"
					type="text" size="12" onblur="checkDate(this)" />
				&nbsp;&nbsp;&nbsp;&nbsp; 部门:<select name="dept">
					<option value="">默认</option>
					<option value="结算">结算</option>
					<option value="采购">采购</option>
					<option value="营运">营运</option>
					<option value="物流">物流</option>
				</select> &nbsp;&nbsp;&nbsp;&nbsp; <label> 置顶：<input type="checkbox"
					value="1" name="istop">
				</label> &nbsp;&nbsp;&nbsp;&nbsp; <label> 是否上传移动平台:<input
					type="checkbox" value="1" name="ismobile">
				</label>
			</div>

			<textarea id="note_content" name="note_content" cols="80" rows="12"></textarea>

			<label>公文附件: <%=file_count==0?"没有挂载附件":file_count+"个附件"%></label>

			<%=lst_file%>
			<div>
				<input type="button" value=" 保存 " onclick="check_null()" />
			</div>
		</form>

	</div>

	<script type="text/javascript">
	CKEDITOR.replace( 'note_content',
	{
		skin : 'office2003'
	}
	);
</script>
</body>
</html>
