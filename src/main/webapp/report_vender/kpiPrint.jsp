<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3030110;
%>

<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );
	if ( !perm.include( Permission.READ ) ) 
		throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
%>
<%

	String monthid = request.getParameter("monthid");
	if(monthid==null) throw new Exception("没有指定月份");
	String sgroupid = request.getParameter("sgroupid");
	if(sgroupid==null) {
		sgroupid="";
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>供应商业绩表现卡</title>
<script language="javascript" src="../js/ajax.js" type="text/javascript"></script>
<script language="javascript" src="../js/XErr.js" type="text/javascript"></script>
<script language="javascript" src="../js/Date.js" type="text/javascript"></script>
<!-- ActiveWidgets stylesheet and scripts -->
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css" />
<style type="text/css">
.tdHead {
	white-space: nowrap;
	text-align: left;
}

.number {
	text-align: right;
}

.report {
	background-color: #fff;
}

.report TH {
	background-color: #f3f3f3;
}

.item {
	font-size: 16px;
	font-weight: bold;
	background-color: #F0FEF8;
}

.title {
	font-size: 16px;
	fonfont-weight: bold;
	font-family: '黑体'
}
</style>

<script language="javascript" type="text/javascript">
function search(){
	setLoading( true );
	var parms = new Array();
	parms.push( "monthid=<%=monthid%>" );
	parms.push( "sgroupid=<%=sgroupid%>");
	var url  = "../DaemonReport?reportname=kpi&" + parms.join( "&" );
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/report");
	table.request();
	table.response = function(text){
		setLoading(false);
		table.setXML(text);
		if( table.getErrCode() != '0' ){
			alert( table.getErrNote() );
			return;
		}
		if(table.getCount()==0){
			$("div_report").innerHTML = "没有本月数据";
			return;
		}
		$("div_report").innerHTML = table.transformNode("kpi.xsl");
		print_title.innerHTML='<%=token.site.getTitle()%>供应商业绩表现卡';
		print_logo.src='<%=token.site.getLogo()%>';
	};
}

window.onload= function(){
	search();
};


</script>
</head>
<body>
	<a href="###" onclick="window.print();this.style.display='none';"
		style="margin-left: 15px; font-weight: bold;"> [打印本页]</a>
	<table width="100%" border="0">
		<tr>
			<td width="170"><img id="print_logo" src=""
				style="margin-left: 15px;" /></td>
			<td align="center">
				<div id="print_title" style="font-size: 18px; font-weight: bold;"></div>
			</td>
		</tr>
	</table>
	<div id="div_report"></div>
</body>
</html>
