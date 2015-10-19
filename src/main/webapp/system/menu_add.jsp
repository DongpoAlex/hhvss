﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.util.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.util.component.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../WEB-INF/errorpage.jsp"%>
<%
final int moduleid = 9010105;			// 菜单管理
request.setCharacterEncoding( "UTF-8" );
HttpSession session = request.getSession( false );
if( session == null ) throw new TokenException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new TokenException( "您尚未登录,或已超时." );

//查询用户的权限.
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
Input input_id = new Input();
input_id.setAttribute( "name", "ctrl_headmenuid" );
input_id.setAttribute( "readonly", "1" );

String headmenuid = request.getParameter( "headmenuid" );
if( headmenuid != null && headmenuid.length() > 0 ) {
	input_id.setAttribute( "type", "text" );
	input_id.setAttribute( "value", headmenuid );
	
} else {
	throw new InvalidDataException( "headmenuid not set! " );
}

%>

﻿
<?xml version="1.0" encoding="UTF-8"?>
<html lang=en xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />
<xml id="island4request" />
<xml id="island4result" />
<xml id="format4radio" src="radio_module.xsl"></xml>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="Menu.js"> </script>

<script language="javascript">

function init_module_list()
{
	try{
		load_module();
	} catch(e) {
		alert(e);
	}
}

function load_module()
{	
	divRadio.innerHTML = "请稍候 ..." ;
	
	var url = "../DaemonModuleList?action=list_roleTypeModule&headMenu=<%=headmenuid%>";
	island4result.async = false;
	island4result.load( url );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );

	divRadio.innerHTML = island4result.transformNode( format4radio.documentElement );
}


function save_info()
{
window.status = " Save menu info ... ";
	if( !check_input() ) return;
	
	var var_menu = new CyberMenu();
	
	var_menu.menulabel 	= ctrl_menulabel.value;
	var_menu.moduleid 	= ctrl_moduleid.value;
	
	island4request.loadXML( "" );
	var doc      	= island4request.XMLDocument;
	var elm_root 	= doc.selectSingleNode("/");
	var elm_user  	= var_menu.toElement( doc );
	
	elm_root.appendChild( var_menu.toElement(doc) );
	
	send_user_info();
}




function check_input()
{
	if( ctrl_menulabel.value == "" ) {
		alert( "菜单名无效 ! " ) ;
		return false;
	}

	if( ctrl_moduleid.value == "" ) {
		alert( "模块号无效 ! " ) ;
		return false;
	}
	
	return true;
}


function send_user_info()
{
	var url = "../DaemonMenuAdm?action=add_menu";
	url    += "&headmenuid=" + ctrl_headmenuid.value;
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
	
	// Go back.
	window.history.back();
}


function set_moduleid( ctrl )
{
	ctrl_moduleid.value = "" + ctrl.moduleid;
	ctrl_menulabel.value = "" +ctrl.menulabel;
}

function choose( n )
{
	if( n == 0 ) {
		ctrl_moduleid.value = "0";
		divRadio.innerHTML = "" ;
	} else {
		init_module_list();
	}
}

</script>

</head>

<body>

	<table class="noborder">
		<caption>新建菜单</caption>

		<tr>
			<td><label>上级模块</label></td>
			<td><%=input_id.toString()%></td>
		</tr>

		<tr>
			<td><label>功能模块号</label></td>
			<td><input type="text" name="ctrl_moduleid" value="0" readonly /></td>
			<td><input type="radio" name="rd_menu_module"
				onclick="choose( 0 )" checked />菜单组 <input type="radio"
				name="rd_menu_module" onclick="choose( 1 )" />业务模块</td>
		</tr>

		<tr>
			<td><label>菜单名</label></td>
			<td><input type="text" name="ctrl_menulabel" value="" /></td>
		</tr>

	</table>


	<input type="button" value=" 保存 " onclick="save_info()" />
	<input type="button" value=" 返回 " onclick="window.history.back()" />

	<div id="divRadio"></div>

</body>
</html>
