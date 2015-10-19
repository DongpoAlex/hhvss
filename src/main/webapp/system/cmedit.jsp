﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.util.*"
	import="com.royalstone.security.*"
	import="com.royalstone.util.daemon.*"
	errorPage="../WEB-INF/errorpage.jsp"%>
<%
request.setCharacterEncoding( "UTF-8" );
HttpSession session = request.getSession( false );
if( session == null ) throw new TokenException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new TokenException( "您尚未登录,或已超时." );

String moduleid = request.getParameter("moduleid");
String menuid = request.getParameter("menuid");
String cmid = request.getParameter("cmid");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="../css/style.css" type="text/css">
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css" />
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>
<script language="javascript">

function init()
{	
	setLoading( true );
	var url = "../DaemonCM?operation=getCMByModuleid&moduleid=<%=moduleid%>";
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/cmdefinition");
	table.request();
	table.response = function(text){
		setLoading( false );
		table.setXML(text);
		//alert(text.xml);
		//检查有没有对应cm
		if(table.getCount()==0){
			divModule.innerHTML = "当前模块没有定义视图！<a href='../cm/colModleEditor.jsp?moduleid=<%=moduleid%>&cmid=<%=cmid%>'>添加</a>";
		}else{
			divModule.innerHTML = table.transformNode("list_cm.xsl");
			var obj = document.getElementsByName("selectcmid");
			var cmid = <%=cmid%>;
			if(obj){
				for ( var i = 0; i < obj.length; i++) {
					if(cmid == obj[i].value){
						obj[i].checked = true;
					}
				}
			}else{
				return;
			}
		}
	};
}

function updateMenuCM(){
	var obj = document.getElementsByName("selectcmid");
	var cmid = 0;
	if(obj){
		for ( var i = 0; i < obj.length; i++) {
			if(obj[i].checked){
				cmid = obj[i].value;
			}
		}
	}else{
		return;
	}
	if(cmid==0){alert("请选择！");return;}
	setLoading( true );
	var url = "../DaemonMenuAdm?action=updateCM&menuid=<%=menuid%>&cmid="+cmid;
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
		table.setXML(text);
		if(table.getErrCode()!=0){
			alert(table.getErrNote());
		}else{
			alert("保存成功");
		}
		setLoading( false );
	};
}

</script>

</head>
<body onload="init()">
	<div id="divModule"></div>
	<input value="保存" type="button" onclick="updateMenuCM()">
	<input value="取消" type="button" onclick="window.close()">
</body>
</html>
