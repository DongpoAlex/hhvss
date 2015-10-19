<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.certificate.bean.Config"
	import="com.royalstone.certificate.*"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.util.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	request.setCharacterEncoding("UTF-8");
	HttpSession session = request.getSession( false ); 
	if (session == null)
		throw new PermissionException("您尚未登录,或已超时.");
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException("您尚未登录,或已超时.");
	
	final int moduleid=8000014;
	Permission perm = token.getPermission( moduleid );
	if ( !perm.include( Permission.READ ) ) 
		throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
	%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="./css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<style>
TD {
	font-size: 12px;
}

.title {
	background-color: #e388e3;
	text-align: left;
}

.title2 {
	background-color: #aaa3e3;
	text-align: left;
}
</style>
<script type="text/javascript">
			<% if(token.isVender){ %>
			window.onload = init;
			<%}%>
			function init(){
				setLoading(true);
				var url 		= "../DaemonCertificate?action=getVenderCertificateList";
				var table = new AW.XML.Table;
				table.setURL(url);
				table.setTable("xdoc/xout/list");
				table.request();
				table.response = function(text){
					setLoading(false);
					table.setXML(text);
					var xcode = table.getErrCode();
					if( xcode != '0' ){//处理xml中的错误消息
						alert( xcode+table.getErrNote());
					}else{
						var htmlOut = table.transformNode("list.xsl");
						$('list').innerHTML = htmlOut;
					}
				};
			}

			function search(){
				var venderid = $("txt_venderid").value;
				if(venderid=="") return;
				setLoading(true);
				var url 		= "../DaemonCertificate?action=getVenderCertificateList&venderid="+venderid;
				var table = new AW.XML.Table;
				table.setURL(url);
				table.setTable("xdoc/xout/list");
				table.request();
				table.response = function(text){
					setLoading(false);
					table.setXML(text);
					var xcode = table.getErrCode();
					if( xcode != '0' ){//处理xml中的错误消息
						alert( xcode+table.getErrNote());
					}else{
						var htmlOut = table.transformNode("list.xsl");
						$('list').innerHTML = htmlOut;
					}
				};

			}
		</script>
</head>
<body>
	<% if(!token.isVender){ %>
	<div>
		供应商ID：<input id="txt_venderid" size="8" type="text"></input> <input
			type="button" value="查询" onclick="search()"></input>
	</div>
	<%} %>
	<div id="list"></div>
</body>
</html>