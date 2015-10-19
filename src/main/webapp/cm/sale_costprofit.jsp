<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 9000010;
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
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title></title>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<style type="text/css">
.aw-grid-control {
	width: 100%;
	height: 60%;
	background-color: #F9F8F4;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
	hepadding-bottom: 3px;
	padding-top: 3px;
}

.aw-grid-row {
	border-bottom: 1px solid #ccc;
	font-size: 14px;
	height: 22px;
}

#shuiyin {
	font-size: 200px;
	width: 100%;
	text-align: center;
	position: absolute;
	top: 50%;
	letter-spacing: 200px;
	filter: progid:DXImageTransform.Microsoft.Alpha(style=0, opacity=50,
		finishOpacity=50);
}
</style>

<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/MainReportGrid.js"> </script>
<script language="javascript" src="../js/common.js"> </script>

<script language="javascript" type="text/javascript">
			var service = 'SaleCostProfit';
			var report = new ReportGrid(service);

			window.onload=function(){
				report.init();
			};
		</script>

</head>
<body>
	<div id="divTitle"></div>
	<div id="divSearch" class="search_main">
		<% if(!token.isVender){ %>
		<div class="search_parms">
			供 应 商: <input type="text" id="txt_venderid" name="txt_parms"
				notnull="notnull" alt="供应商编码" />
		</div>
		<% } %>
		<div class="search_parms">
			门店:<input type="text" id="txt_shopid" size="12" split=","
				name="txt_parms" /><a href="javascript:showShopList('txt_shopid')">选择门店</a>
		</div>
		<div class="search_parms">
			发生日期: <input type="text" id="txt_editdate_min" class="Wdate"
				onFocus="WdatePicker()" name="txt_parms" alt="最小日期" /> - <input
				type="text" id="txt_editdate_max" class="Wdate"
				onFocus="WdatePicker()" name="txt_parms" alt="最大日期" />
		</div>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>
