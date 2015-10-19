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
SelSgroup sel = new SelSgroup(token);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>供应商业绩表现卡</title>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"></script>

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css" />

<!-- grid format -->
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
window.onload= function(){
};

/**
 * 提交查询
 */
function search_report()
{	
	setLoading( true );
	var parms = new Array();
	parms.push( "monthid="+ $("txt_monthid").value );
	if( $("txt_sgroup").value != '' ) parms.push( "sgroupid="+ $("txt_sgroup").value );
	var url  = "../DaemonReport?reportname=kpi&" + parms.join( "&" );
	var table = new AW.XML.Table;
	table.setURL(url);
	//table.setAsync(false);
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
	};
}

function toPrint(){
	var parms = new Array();
	parms.push( "monthid="+ $("txt_monthid").value );
	if( $("txt_sgroup").value != '' ) parms.push( "sgroupid="+ $("txt_sgroup").value );
	var url  = "kpiPrint.jsp?reportname=kpi&" + parms.join( "&" );
	window.open(url);
}

function downExcel(){
	var parms = new Array();
	parms.push( "monthid="+ $("txt_monthid").value );
	if( $("txt_sgroup").value != '' ) parms.push( "sgroupid="+ $("txt_sgroup").value );
	var url  = "../DaemonDownloadExcel?operation=kpi&" + parms.join( "&" );
	
	window.location.href = url;
}
</script>
</head>
<body>
	<div id="title">供应商业绩表现卡</div>
	<table cellspacing="1" cellpadding="2" width="100%" align="center"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader" width="25%">选择年月</td>
			<td class="tableheader" width="25%">课类</td>
			<td class="tableheader">查询</td>
		</tr>
		<tr>
			<td class="altbg2"><input type="text" id="txt_monthid"
				onfocus="WdatePicker({skin:'whyGreen',dateFmt:'yyyyMM'})"
				class="Wdate" /></td>
			<td class="altbg2"><%=sel%></td>
			<td class="altbg2"><script type="text/javascript">
					var button = new AW.UI.Button;
					button.setControlText("查询");
					button.setControlImage( "search" );
					button.onClick = search_report;
					document.write( button );
					</script> &nbsp;&nbsp;&nbsp;&nbsp; <input onclick="toPrint()" type="button"
				value="打印" /> &nbsp;<input onclick="downExcel()" type="button"
				value="导出Excel" /></td>
		</tr>
		<tr id="header1_toggle" class="singleborder">
			<td colspan="3"></td>
		</tr>
		<tr id="header2_toggle" class="whiteborder">
			<td colspan="3"></td>
		</tr>
	</table>
	<!--  <div id="div_count"><span style="float: right">提示：本系统只显示查询结果前一千条记录，查询超过一千条请缩小查询范围或以导出查看</span></div>-->
	<div id="div_report"></div>
</body>
</html>
