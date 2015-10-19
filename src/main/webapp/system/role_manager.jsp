﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="java.sql.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.vss.admin.*"
	import="com.royalstone.util.component.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
final int moduleid = 9010204;			// 角色管理
request.setCharacterEncoding( "UTF-8" );

HttpSession session = request.getSession( false );
if( session == null ) throw new TokenException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new TokenException( "您尚未登录,或已超时." );

//查询用户的权限.
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
%>

<%
String operation = request.getParameter( "operation" );
String roleid 	= request.getParameter( "roleid" );
String envname 	= request.getParameter( "envname" );
String envvalue = request.getParameter( "envvalue" );
String rolename = request.getParameter( "rolename" );
String note 	= request.getParameter( "note" );
String rt 	= request.getParameter( "roletype" );
rt=rt==null?"":rt.trim();
int roletype = (rt.length() >0 ) ? Integer.parseInt( rt ) : 0;

operation 	= ( operation == null ) ? "" : operation.trim();
roleid 		= ( roleid == null )    ? "" : roleid.trim();
envname 	= ( envname == null )   ? "" : envname.trim();
envvalue 	= ( envvalue == null )  ? "" : envvalue.trim();

rolename 	= ( rolename == null )  ? "" : rolename.trim();
note 		= ( note == null )  	? "" : note.trim();

if( roleid.length() == 0 && operation.length() == 0 ) operation = "init";
else if( roleid.length() >0 && operation.length()== 0 ) operation = "load";

if( operation.equalsIgnoreCase( "init" ) ) {
	rolename = "";
	note	 = "";
}

boolean init_view = true;

Element elm_doc = new Element( "xdoc" );
Element elm_err = new XErr( 0,"OK" ).toElement();
Connection conn = XDaemon.openDataSource( token.site.getDbSrcName() );
RoleAdm adm = new RoleAdm(conn);
int id = (roleid.length() >0 ) ? Integer.parseInt( roleid ) : -1;

%>

<%
	
try{
	// 删除角色
	if( operation.equalsIgnoreCase( "del_role" ) && roleid.length() > 0 ) {
		adm.deleteRole( id );
		roleid   = "";
		rolename = "";
		note	 = "";
		init_view = false;
	} else 		// 保存修改后的角色信息
	  if( operation.equalsIgnoreCase( "save_role" ) ) {
	  
	  	// 新建角色
	  	if( roleid.length() == 0 ) {
	  		String shopid="AAAA";
			id = adm.addRole( rolename, shopid, note,roletype );
			roleid = "" + id;
	  	} else {
	  	// 修改已经存在的角色
			if( rolename.length() == 0 ) throw new InvalidDataException( "Invalid rolename! " );
			String shopid="AAAA";
			adm.updateRole( id, rolename, shopid, note,roletype );
	  	}
	} else 		// 从后台数据库中取角色信息
	  if( operation.equalsIgnoreCase( "load" ) ) {
	  	Role role = adm.getRole( id );
	  	roleid   = "" + role.roleid;
	  	rolename = role.rolename;
	  	note     = role.note;
	  	roletype = role.roletype;
	}
} catch ( SQLException e ) {
	elm_err = new XErr( e.getErrorCode(), e.getMessage() ).toElement() ;
}
%>


<%

try{
	// 为角色添加环境变量
	if( operation.equalsIgnoreCase( "add_env" ) && roleid.length() > 0 ) {
		Environment.add( conn, id, envname, envvalue );
	} else 		// 删除角色的环境变量( 指定变量名与变量值 )
	  if( operation.equalsIgnoreCase( "del_env" ) && roleid.length() > 0 ) {
		Environment.delete( conn, id, envname, envvalue );
	} else
	
	// 以下两种情况下不需要取环境变量: 删除角色, 初始化模块.
	// 其他情况下(添加/修改/查询), 都应取出角色的环境变量, 并由前台显示. 
	if( !operation.equalsIgnoreCase( "del_role" ) && roleid.length() > 0 ) {
		Environment[] env_lst = Environment.getValue( conn, id );
		Element elm_env = Environment.toElement( env_lst );
		elm_doc = new Element( "xdoc" );
		Element elm_out = new Element( "xout" );
		XErr xerr = ( env_lst!=null && env_lst.length>0 ) ? ( new XErr( 0, "OK" ) ) : ( new XErr( 100, "该角色的环境尚未定义" ) );
		elm_err = xerr.toElement();
		elm_out.addContent( elm_env );
		elm_doc.addContent( elm_out );
	}
} catch ( SQLException e ) {
	elm_err = new XErr( e.getErrorCode(), e.getMessage() ).toElement() ;
}

elm_doc.addContent( elm_err );

XDaemon.closeDataSource( conn );
if ( operation.equalsIgnoreCase( "init" ) ) init_view = false;
init_view = !operation.equalsIgnoreCase( "init" ) && !operation.equalsIgnoreCase( "del_role" );
%>

<%
SelRoleType branchroletype = new SelRoleType(token);
branchroletype.setAttribute("name","roletype");
%>


﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />

<xml id="island4env"> <%
if( elm_doc != null ) XDaemon.output( elm_doc, out );
%> </xml>

<xml id="format4env" src="role_environment.xsl" />

<script language="javascript">

function init()
{
	try{
		display_role();
		if( <%=init_view%> ) display_env();
	} catch (e) {
		alert(e);
	}
}

function display_role()
{
	form_role.roleid.value 	 = '<%=roleid%>' ;
	form_role.rolename.value = '<%=rolename%>' ;
	form_role.note.value 	 = '<%=note%>' ;
	form_role.roletype.value = '<%=roletype%>';
	if( form_role.roleid.value == "" ) form_role.btn_del.disabled = true;
}

function display_env()
{
	div_environment.innerHTML = island4env.transformNode( format4env.documentElement );
}

function delete_role()
{
   try{
	var id = parseInt( form_role.roleid.value );
	if( id <0 ) {
		alert( "内嵌角色不可以删除!" );
		return;
	}
	var ok = confirm( "你确定要删除这个角色?" );
	if( !ok ) return;
	
	form_role.operation.value = "del_role";
	form_role.submit();
   } catch( e ) {
   	alert(e);
   }
}

function delete_env( name, value )
{
	form_env.operation.value = "del_env";
	form_env.roleid.value   = '<%=roleid%>';
	form_env.envname.value  = name;
	form_env.envvalue.value = value;
	form_env.submit();
}

</script>

<script language="javascript">
function add_environment()
{
	if( form_env.envname.value == "" || form_env.envvalue.value == "" ) {
		alert( "请输入有效的环境变量!" );
		return;
	} else {
		form_env.submit();
	}
}

function save_role_info ()
{
	if( form_role.rolename.value == "" ) {
		alert ( "角色名称无效!" );
		return;
	}
	form_role.submit();
}

function clear_for_new()
{
	window.location.href = "role_manager.jsp";
}

</script>

</head>

<body onload="init()">


	<a href="javascript:clear_for_new(); ">添加新角色</a>

	<form name="form_role" action="role_manager.jsp" method="post">
		<table class="noborder">
			<caption>系统角色维护</caption>

			<tr>
				<td><input type="hidden" name="roleid" value="<%=roleid%>" /></td>
				<td><input type="hidden" name="operation" value="save_role" /></td>
			</tr>

			<tr>
				<td><label>角色名称</label></td>
				<td><input type="text" name="rolename" size="10" maxlength="10" /></td>
			</tr>

			<tr>
				<td><label>角色类型</label></td>
				<td><%=branchroletype%></td>
			</tr>
			<tr>
				<td><label>角色描述</label></td>
				<td><input type="text" name="note" size="30" maxlength="20" /></td>
			</tr>

		</table>
		<input type="button" value=" 保存 " name="btn_save"
			onclick="save_role_info()" /> <input type="button" value=" 删除 "
			name="btn_del" onclick="delete_role()" /> <input type="reset"
			value=" 取消 " />
	</form>

	<hr />

	<form name="form_env" action="role_manager.jsp" method="post">
		<table class="noborder">
			<tr>
				<td><label>变量名: </label></td>
				<td><input type="text" name="envname" value="" maxlength="10" /></td>
			</tr>
			<tr>
				<td><label>变量值: </label></td>
				<td><input type="text" name="envvalue" value="" maxlength="10" /></td>
			</tr>
			<tr>
				<td><input type="button" value="添 加"
					onclick="add_environment()" /></td>
				<td><input type="reset" value="清 除" /></td>
				<input type="hidden" name="operation" value="add_env" />
				<input type="hidden" name="roleid" value="<%=roleid%>" />
			</tr>

		</table>

	</form>

	<div id="div_environment"></div>
</body>
</html>
