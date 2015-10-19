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
	
	String order_serial = request.getParameter("order_serial");
	String dccode = request.getParameter("dccode");
	String logistics=request.getParameter("logistics");
	String supplier_no;
	if(token.isVender){
		supplier_no=token.getBusinessid();
	}else{
		supplier_no = request.getParameter("supplier_no");
	}
	Connection conn=null;
	Vender vender=null;
	try{
		conn = XDaemon.openDataSource( token.site.getDbSrcName() );
		vender = Vender.getVender(conn,supplier_no);
		if(vender==null){
			throw new Exception("找不到供应商");
  		 }
	}catch(Exception e){
		throw e;
	}finally{
		XDaemon.closeDataSource( conn );
	}
//	String vendername=request.getParameter("vendername");
	String request_date=request.getParameter("request_date");
	String start_time=request.getParameter("start_time");
	String end_time=request.getParameter("end_time");
	String flag=request.getParameter("flag").toString();
	String note=request.getParameter("note");
	String shopname=request.getParameter("shopname");
	String vlogistics=logistics;
	String vflag="";
	if("Y".equals(flag)){
		vflag="已预约";
	}else if("N".equals(flag)){
		vflag="已取消";
	}
	
	
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
<script language="javascript" src="./netorder_list.js">
</script>
<style>
.aw-column-0 {
	width: 120px;
	text-align: center;
}

.aw-column-2 {
	width: 120px;
	cursor: pointer;
	text-align: center;
	color: blue;
}

.aw-column-6,.aw-column-9 {
	width: 120px;
	text-align: center;
}

.aw-column-1 {
	width: 70px;
	text-align: center;
}

.aw-column-3,.aw-column-7,.aw-column-8 {
	width: 70px;
	text-align: center;
}

.aw-column-4,.aw-column-5 {
	width: 70px;
	text-align: right;
}

.aw-column-10 {
	width: 200px;
	text-align: center;
}
</style>
<title>网上预约订单明细</title>
</head>
<body onload="init('<%=order_serial%>','<%=dccode%>')">
	<div class="main">
		<br>
		<div class="title">网上预约订单明细</div>
		<br>
		<div class="content">
			<table cellpadding="4px" cellspacing="1">
				<tr>
					<th>预约流水号：</th>
					<td id="txt_order_serial"><%=order_serial%></td>
					<th>DC：</th>
					<td id="txt_dccode"><%=dccode%> | <%=shopname%></td>
					<th>物流模式：</th>
					<td id="txt_logistics"><%=vlogistics%></td>
				</tr>
				<tr>
					<th>供应商编码：</th>
					<td id="txt_supplier_no"><%=supplier_no%></td>
					<th>供应商名称：</th>
					<td colspan="3" id="txt_vendername"><%=vender.vendername%></td>
				</tr>
				<tr>
					<th>预约日期：</th>
					<td id="txt_request_date"><%=request_date%></td>
					<th>开始时间：</th>
					<td id="txt_start_time"><%=start_time%></td>
					<th>结束时间：</th>
					<td id="txt_end_time"><%=end_time%></td>
				</tr>
				<tr>
					<th>标志：</th>
					<td id="txt_flag"><%=vflag%></td>
					<th>备注：</th>
					<td colspan="3" id="txt_note"><%=note%></td>
				</tr>
			</table>
		</div>
	</div>
	<div id="div_detail"></div>
</body>
</html>