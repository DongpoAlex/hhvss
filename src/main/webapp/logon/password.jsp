﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="java.sql.*" import="org.jdom.*"
	import="com.royalstone.util.*" import="com.royalstone.security.*"
	import="com.royalstone.vss.admin.*"
	import="com.royalstone.util.daemon.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
/**
 * 此模块用于用户修改自己的密码.
 */

String visibility_warning = "hidden" ;
String visibility_note    = "hidden" ;

%>

<%
final int moduleid = 9090909;
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );

//查询用户的权限.
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );

request.setCharacterEncoding( "UTF-8" );
String msg = "";
boolean ok = true;
Connection conn = null;
try{
	conn = XDaemon.openDataSource( token.site.getDbSrcName() );
	UserAdm adm = new UserAdm( conn );
	String operation = request.getParameter( "operation" );
	if( operation == null ) operation = "";
	
	String password0 = request.getParameter( "password0" );
	String password1 = request.getParameter( "password1" );
	String password2 = request.getParameter( "password2" );
	if( password0 == null ) password0 = "";
	if( password1 == null ) password1 = "";
	if( password2 == null ) password2 = "";
	
	if( operation.equals( "newpass" ) ){
		if( ok && password1.length() <4 ) {
			ok = false;
			msg = "密码不得短于4位!";
		}
		
		if( ok && !password1.equals( password2 ) ) {
			ok = false;
			msg = "两次输入的密码不一致!";
		}
		
		if( ok ) if ( !adm.checkPassword( token.userid, password0 ) ) {
			ok = false;
			msg = "旧密码验证失败,不能修改密码!";
		}
		
		if( ok ) {
			adm.setPassword( token.userid, password1 );
			msg = "密码已经修改";
		}
		
		if( ok ) visibility_note  = "visible" ;
		  else visibility_warning = "visible" ;
		
	}
}finally{
	XDaemon.closeDataSource( conn );
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />

<script type="text/javascript" src="../js/jquery.min.js"></script>
<script type="text/javascript" src="../js/jquery.validator.js"></script>

<style>
.errorTip {
	background-image: url(../img/access_disallow.gif);
	background-repeat: no-repeat;
	padding-left: 16px;
}

.errorInput {
	background-color: #FFCC33;
}

.validTip {
	background-image: url(../img/access_allow.gif);
	background-repeat: no-repeat;
	background-position: left top;
	padding: 2px;
}

DIV {
	padding: 6px;
}

DIV SPAN {
	width: 200px;
	margin-right: 12px;
	font-size: 14px;
}

#div_warning {
	visibility: <%=visibility_warning%>;
}

#div_note {
	visibility: <%=visibility_note%>;
}
</style>
</head>
<body>

	<form id="form_input" name="form_input">
		<input type="hidden" name="operation" value="newpass" />
		<div>
			<h2>修改系统登录密码</h2>
		</div>
		<div>
			<span>登录名:</span><%=token.loginid%></div>
		<div>
			<span>用户名:</span><%=token.username%></div>
		<div>
			<span>旧密码:</span><input type="password" name="password0"
				require="true" value="" datatype="limit" msg="旧密码必须填写" />
		</div>
		<div>
			<span>新密码:</span><input type="password" name="password1"
				require="true" value="" datatype="limit" min="4" max="16"
				msg="新密码需4至16位字符串" />
		</div>
		<div>
			<span>确认新密码:</span><input type="password" name="password2"
				require="true" value="" datatype="repeat" to="password1"
				msg="两次输入必须匹配" />
		</div>
		<div>
			<input type="submit" value=" 确 认 " /> <input type="reset"
				value=" 取 消 " />
		</div>
	</form>

	<div id="div_warning" class="warning"><%=msg%>
	</div>
	<div id="div_note" class="info"><%=msg%>
	</div>
	<script type="text/javascript">
jQuery(document).ready(function(){
	var a = jQuery('#form_input').checkForm();
});
</script>
</body>
</html>
