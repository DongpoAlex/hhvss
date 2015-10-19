﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.util.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.util.component.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../WEB-INF/errorpage.jsp"%>
<%
final int moduleid = 9010204;
request.setCharacterEncoding( "UTF-8" );

HttpSession session = request.getSession( false );
if( session == null ) throw new TokenException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new TokenException( "您尚未登录,或已超时." );

//查询用户的权限.
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );

Input input_user = new Input();
input_user.setAttribute( "name", "txt_userid" );

Input input_btn  = new Input();
input_btn.setAttribute( "type", "button" );
input_btn.setAttribute( "value", " 查询 " );
input_btn.setAttribute( "onclick", " refresh() " );
String ctrl_btn = "";

String userid = request.getParameter( "userid" );
if( userid != null && userid.length() > 0 ) {
	int i = Integer.parseInt( userid );
	userid = "" + i;
	input_user.setAttribute( "type", "hidden" );
	input_user.setAttribute( "value", userid );
	ctrl_btn = "";
} else {
	input_user.setAttribute( "type", "text" );
	input_user.setAttribute( "value", "" );
	ctrl_btn = input_btn.toString();
}

%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />

<xml id="island4req" />
<xml id="island4result" />
<xml id="format4role" src="role4user.xsl"></xml>

<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>

<script language="javascript">

function init()
{
	if( txt_userid.value != "" ) refresh();
}

function refresh()
{
	try{
		load_role_list();
	} catch (e) {
		alert(e);
	}
}

function load_role_list ()
{
	if( txt_userid.value == "" ) {
		alert( "请输入有效的用户编号!" );
		return false;
	}


	var url = "../DaemonRoleAdm?action=list_role4user&userid=" + txt_userid.value ;
	divMain.innerHTML = "Load data ..." ;
	
	island4result.async = false;
	island4result.load( url );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );

	divMain.innerHTML = island4result.transformNode( format4role.documentElement );
	
	var xerr = parseXErr( elm_err );
	if ( xerr.code == "0" ){
		return true;
	} else {
		alert( xerr.note );
		return false;
	}
}

function member_mgr( ctrl )
{
	//检查roletype
	if(ctrl.checked){
		var boxs = document.getElementsByName("box");
		for(var i=0;i<boxs.length;i++){
			if(boxs[i].checked && boxs[i].roletype != ctrl.roletype && ctrl.roletype!=0){
				alert("您选择的角色与已选的角色有冲突，请重新选择");
				ctrl.checked=false;
				return false;
			}
		}
	}
	
	window.status = "roleid:" + ctrl.roleid + "; userid:" + ctrl.userid;
	var action = "";
	if( ctrl.checked ) {
		action = "add_member";
	} else {
		action = "delete_member";
	}
	var url = "../DaemonRoleAdm?userid=" +ctrl.userid+ "&roleid=" +ctrl.roleid+ "&action=" + action;
	var courier 		= new AjaxCourier( url );
	courier.island4req  	= null;
	courier.reader.read 	= analyse4save;
	courier.call();

}


function analyse4save( text )
{
	island4result.loadXML( text );
	var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );

	var xerr = parseXErr( elm_err );
	if ( xerr.code == "0" ){
		window.status = "保存成功!" ;
		
	} else {
		alert( "保存失败: " + xerr.toString() );
	}
}


</script>

</head>

<body onload="init()">

	<%=input_user.toString()%>
	<%=ctrl_btn%>
	为用户ID为<%=userid %>的用户赋予权限组：
	<br />
	<br />
	<div id="divMain"></div>
	<br />
	<br />
	<a href="user_add.jsp" target="_self">添加新用户</a>
</body>
</html>
