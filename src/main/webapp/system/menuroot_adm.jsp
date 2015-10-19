﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.util.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.util.component.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../WEB-INF/errorpage.jsp"%>
<%
	 /**
	 * 菜单维护模块. 显示根菜单目录, 提供修改菜单的入口.
	 *
	 */

	final int moduleid = 9010105;
	request.setCharacterEncoding("UTF-8");

	HttpSession session = request.getSession(false);
	if (session == null)
		throw new TokenException("您尚未登录,或已超时.");
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new TokenException("您尚未登录,或已超时.");

	//查询用户的权限.
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ))
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:"
		+ moduleid);

	SelRoleType branchroletype = new SelRoleType(token);
	branchroletype.setAttribute("id", "roletype");
%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />


<xml id="island4request" />
<xml id="island4result" />
<xml id="format4list" src="menuroot.xsl"></xml>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>
<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="Menu.js"> </script>

<script language="javascript">

function init()
{
		load_data();
}

function load_data()
{	
	
	var url = "../DaemonMenuAdm?action=get_root";
	setLoading( true );
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
		table.setXML(text);
		divMain.innerHTML = table.transformNode("menuroot.xsl");
		setLoading( false );
	};
}


function del_menuroot( id )
{
	var ok = confirm( "你确实要删除根菜单吗? " );
	if( ok ) {
		alert( "Delete menuroot " + id + " ... " );
		var url = "../DaemonMenuAdm?action=delete&menuid=" + id;
		island4result.async = false;
		island4result.load( url );
		var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	
		var xerr = parseXErr( elm_err );
		if ( xerr.code == "0" ){
			alert( "删除成功!" );
		} else {
			alert( xerr.note );
		}
	}
}


</script>


<script language="javascript">

function save_info()
{
window.status = " Save menu info ... ";
	if( !check_input() ) return;
	
	var var_menu = new CyberMenu();
	
	var_menu.menulabel 	= ctrl_menulabel.value;
	var_menu.moduleid 	= "0";
	var_menu.roletype   = roletype.value;
	island4request.loadXML( "" );
	var doc      	= island4request.XMLDocument;
	var elm_root 	= doc.selectSingleNode("/");
	var elm_user  	= var_menu.toElement( doc );
	elm_root.appendChild( var_menu.toElement(doc) );
	
	send_menuroot();
}




function check_input()
{
	if( ctrl_menulabel.value == "" ) {
		alert( "菜单名无效 ! " ) ;
		return false;
	}
	return true;
}


function send_menuroot()
{
	var url = "../DaemonMenuAdm?action=add_root";
	var courier 		= new AjaxCourier( url );
	courier.island4req  	= island4request;
	courier.reader.read 	= analyse_reply;
	courier.call();
}

function analyse_reply( text )
{
	island4result.loadXML( text );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );

	if( xerr.code == "0" ) alert ( " 添加成功! " );
	else {
		alert( " 添加失败! " );
		alert( xerr.toString() );
	}
	
	window.location.reload();
	
}


</script>


</head>

<body onload="init()">


	<a href="javascript:window.location.reload()">刷新</a>

	<div id="divMain"></div>


	<br />
	<input type="button" value=" 返回 " onclick="window.history.back()" />
	<br />
	<br />
	<br />
	<br />

	<div id="divAdd">


		<table class="noborder">
			<caption>新建根菜单</caption>


			<tr>
				<td><label> 菜单名： </label></td>
				<td><input type="text" name="ctrl_menulabel" value="" /></td>
				<td>角色类型：</td>
				<td><%=branchroletype%></td>
			</tr>


		</table>


		<input type="button" value=" 添加 " onclick="save_info()" />

	</div>


</body>
</html>
