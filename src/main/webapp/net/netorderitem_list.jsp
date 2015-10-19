<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*" import="java.sql.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 6000004;
	request.setCharacterEncoding("UTF-8");

	HttpSession session = request.getSession(false);
	
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);

	//查询用户的权限.
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ)) {
		//throw new PermissionException("您未获得操作此模块的授权,请管理员联系.模块号:"+ moduleid);
	}
	String po_no = request.getParameter("po_no");
	
	
	String vflag="";
//	if("Y".equals(flag)){
//		vflag="已预约";
//	}else if("N".equals(flag)){
//		vflag="已取消";
//	}
	
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../certificate/css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/Date.js">
</script>
<script language="javascript" src="../AW/runtime/lib/aw.js">
</script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js">
</script>
<script language="javascript" src="./netorderitem_list.js">
</script>
<style>
.aw-column-0,.aw-column-2 {
	width: 120px;
	cursor: pointer;
	text-align: center;
}

.aw-column-1 {
	width: 230px;
	cursor: pointer;
	text-align: left;
}

.aw-column-3,.aw-column-4,.aw-column-5,.aw-column-6,.aw-column-7 {
	width: 90px;
	text-align: right;
}

.aw-column-8 {
	width: 200px;
	text-align: left;
}
</style>
<title>网上预约订单商品明细</title>
</head>
<body onload="init('<%=po_no%>')">
	<div class="main">
		<br>
		<div class="title">网上预约订单商品明细</div>
		<br>
		<div class="content">
			<table cellpadding="4px" cellspacing="1">
				<tr>
					<th>订单号：</th>
					<td id="txt_sheetid"></td>
					<th>订货日期：</th>
					<td id="txt_orderdate"></td>
					<th>有效期：</th>
					<td id="txt_validdays"></td>
				</tr>
				<tr>
					<th>供应商编码：</th>
					<td id="txt_venderid"></td>
					<th>供应商名称：</th>
					<td colspan="3" id="txt_vendername"></td>
				</tr>
				<tr>
					<th>送货时间：</th>
					<td id="txt_vdeliverdate"></td>
					<th>取消日期：</th>
					<td id="txt_deadline"></td>
					<th>送货门店：</th>
					<td id="txt_shopid"></td>
				</tr>
				<tr>
					<th>物流模式：</th>
					<td id="txt_logistics"></td>
					<th>结算方式：</th>
					<td id="txt_paytypename"></td>
					<th>订货审批单：</th>
					<td id="txt_refsheetid"></td>
				</tr>
			</table>
		</div>
	</div>
	<div id="div_poitem"></div>
</body>
</html>