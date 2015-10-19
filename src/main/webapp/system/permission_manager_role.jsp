<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="java.sql.*" import="com.royalstone.util.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.vss.admin.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
final int moduleid = 9010502;
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
String roleid = request.getParameter( "roleid" );
if( roleid == null || roleid.length() == 0 ) throw new InvalidDataException( "roleid is not set!" );

Connection conn = XDaemon.openDataSource( token.site.getDbSrcName() );
RoleAdm adm = new RoleAdm( conn );
Role role   = adm.getRole( Integer.parseInt( roleid ) );
XDaemon.closeDataSource( conn );
%>

<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />
<xml id="island4perm"></xml>
<xml id="island4req" />
<xml id="island4result"></xml>
<xml id="island4reply"></xml>
<xml id="format4perm" src="format4roleperm.xsl"></xml>


<script language="JavaScript" src="../js/XErr.js"> </script>
<script language="JavaScript" src="../js/ajax.js"> </script>
<script language="JavaScript" src="Authority.js"> </script>

<script language="javascript">

// 全局对象, 记录模块权限.
var authority_list = new AuthorityList();
var roleid         = <%=roleid%> ;

// 用户修改权限时调用此函数, 修改JS对象 authority_list.
function clickbox(ctrl)
{
	var value = ( ctrl.checked )? "1" : "0";
	var lst = authority_list.permission_list;
	for( var i=0; i<lst.length; i++ ) {
		var au = lst[i];
		if( au.roleid == ctrl.roleid && au.moduleid == ctrl.moduleid ) {
			au.setAttribute( ctrl.operation, value );
			window.status = "Match: " + au.roleid + "  " + au.moduleid;
		}
	}
	btn_save.disabled = false;
}

</script>

<script language="javascript">
function init()
{
	fetch_permission(); // 取得roleid对应的角色并显示在divMain层中	
}
</script>


<script language="javascript">


function fetch_permission()
{	
	
	var url 		= "../DaemonAuthority?action=list4role&roleid="+ roleid;
	var courier 		= new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse_permission;
	courier.call();
	
}

function analyse_permission( text )
{	
	island4perm.loadXML( "" );
	island4perm.loadXML( text );
	
		
	var elm_err 	= island4perm.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr = parseXErr( elm_err );
	if ( xerr.code != "0" ){
		alert( xerr.toString() );
		return;
	}
		
	// 解析后台发来的帐套信息,并设置控件的值.
	if ( xerr.code == "0" ){
		var elm_lst 	= island4perm.XMLDocument.selectSingleNode( "/xdoc/authority_list" );
		authority_list = parseAuthorityList( elm_lst );
			
		divMain.innerHTML = island4perm.transformNode( format4perm.documentElement );

	}
}


</script>

<script language="javascript">

function save_permission()
{
	window.status = "Save permission for module " + roleid ;
	island4req.loadXML( "" );
	var doc      	= island4req.XMLDocument;
	var elm_root 	= doc.selectSingleNode("/");
	var elm_req     =  authority_list.toElement( doc );
	elm_root.appendChild( elm_req );
	
	var url = "../DaemonAuthority?action=save4role&roleid=" +roleid;
	var courier 		= new AjaxCourier( url );
	courier.island4req  	= island4req;
	courier.reader.read 	= analyse4save;
	courier.call();

}

function analyse4save( text )
{
	island4result.loadXML( text );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );

	var xerr = parseXErr( elm_err );
	if ( xerr.code == "0" ){
		alert( "保存成功!" );
		btn_save.disabled = true;
	} else {
		alert( "保存失败: " + xerr.toString() );
	}
}

function clear_info()
{
	divRoleinfo.innerHTML = "";
	divMain.innerHTML = "";
	btn_save.disabled = true;
}
</script>
</head>

<body onload="init()">
	<div>

		<label>角色号 </label>
		<%=role.roleid%>
		<label>角色名称 </label>
		<%=role.rolename%>
		<label>角色描述 </label>
		<%=role.note%>

	</div>


	<input type="button" name="btn_save" disabled value=" 保存修改 "
		onclick="save_permission()" />
	<input type="button" value=" 返回 " onclick="window.history.back()" />

	<div id="divRoleinfo"></div>
	<br />
	<div id="divMain"></div>
	<br />
</body>
</html>
