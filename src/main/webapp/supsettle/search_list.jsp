<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3020215;
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
			var service = 'SupsettleList';
			var report = new ReportGrid(service);

			window.onload=function(){
				report.init();
			};
			
			s.open_win_print = function(){
				/*var flag = this._table4detail.getXMLText("xdoc/xout/sheet/head/row/flag");
				if(flag!='G'){
					alert("仅审核状态结算单可打印。");
					return;
				}*/
				var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
				",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
		 		window.open( "../page/print.jsp?cmid="+s.cmid+"&clazz=Supsettle&sheetid="+s.current_sheetid, s.current_sheetid, attributeOfNewWnd );
		 	
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
			结算单号: <input type="text" id="txt_sheetid" size="20" name="txt_parms" />
		</div>
		<div class="search_parms">
			费用代码: <input type="text" id="txt_chargeid" size="20" name="txt_parms" />
		</div>
		<div class="search_parms">
			收取方式:<select id="txt_skmode" name="txt_parms">
				<option>全部</option>
				<option value="Y" selected="selected">帐扣</option>
				<option value="N">收现</option>
			</select>
		</div>
		<div class="search_parms">
			单据状态:<select id="txt_status" name="txt_parms">
				<option>全部</option>
				<option value="Y" selected="selected">未结算或未收现</option>
				<option value="T">已结算或已收现</option>
			</select>
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
