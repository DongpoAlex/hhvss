<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.certificate.bean.Config"
	import="com.royalstone.certificate.*"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*" import="java.sql.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
request.setCharacterEncoding( "UTF-8" );
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );
//查询用户的权限.
final int moduleid=8000008;
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException("您未获得操作此模块的授权,请管理员联系.模块号:" + moduleid );

SelCertificateType sel = new SelCertificateType(token,"全部");
sel.setAttribute("id","ctype");
SelCertificateCategory csel = new SelCertificateCategory(token,"全部");
csel.setAttribute("id","txt_ccid");

SelRegion regionSel = new SelRegion(token);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="./css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="./common.js"> </script>
<script language="javascript" src="./certificate_retail_report.js"> </script>
<style>
.aw-column-0,.aw-column-1 {
	width: 60px;
	cursor: pointer;
}

.aw-column-2,.aw-column-5,.aw-column-6,.aw-column-11 {
	width: 60px;
}

.aw-column-3,.aw-column-9 {
	width: 80px;
}

.aw-column-4 {
	width: 30px;
}
</style>
<title></title>
</head>
<body>
	<table cellspacing="1" cellpadding="2" width="98%"
		class="tablecolorborder">
		<tr class="tableheader">
			<td class="tableheader">供应商</td>
			<td class="tableheader">模糊</td>
			<td class="tableheader">供应商区域</td>
			<td class="tableheader">证照类型</td>
			<td class="tableheader">证照种类</td>
			<td class="tableheader">品类</td>
			<td class="tableheader">状态</td>
		</tr>
		<tr id="header0_toggle" style="display: block">
			<td class="altbg2"><input id='txt_venderid' type="text"
				size='10' /></td>
			<td class="altbg2"><input id="txt_isLike" type="checkbox"></td>
			<td class="altbg2"><%=regionSel %></td>
			<td class="altbg2"><select id="txt_type" name="txt_type">
					<option value="">全部</option>
					<option value="1">基本证照</option>
					<option value="2">品类证照</option>
					<option value="3">旧品证照</option>
					<option value="4">新品证照</option>
			</select></td>
			<td class="altbg2"><%=sel %></td>
			<td class="altbg2"><%=csel %></td>
			<td class="altbg2"><select id="txt_flag" name="txt_flag">
					<option value="0">未提交</option>
					<option value="1">提交审核</option>
						<option value="2">审核一通过</option>
					<option value="valid">有效证照</option>
					<option value="100" selected="selected">已审核</option>
					<option value="-1">审核返回</option>
					<option value="-10">年审预警</option>
					<option value="-11">过期预警</option>
					<option value="-100">过期作废</option>
			</select></td>
		</tr>
		<tr class="tableheader">
			<td class="tableheader">单号</td>
			<td class="tableheader">审核日期范围</td>
			<td class="tableheader">商品条码</td>
			<td class="tableheader">商品编码</td>
			<td class="tableheader">证照编码</td>
			<td class="tableheader" colspan="2"></td>
		</tr>
		<tr id="header0_toggle" style="display: block">
			<td class="altbg2"><input id='txt_sheetid' type="text" size='10' /></td>
			<td class="altbg2"><input type="text" id="txt_checktime_min"
				class="Wdate" onFocus="WdatePicker()" name="txt_parms" alt="最小审核日期"
				size="12" /> - <input type="text" id="txt_checktime_max"
				class="Wdate" onFocus="WdatePicker()" name="txt_parms" alt="最大审核日期"
				size="12" /></td>
			<td class="altbg2"><input id='txt_barcodeid' type="text"
				size='16' /></td>
			<td class="altbg2"><input id='txt_goodsid' type="text" size='10' /></td>
			<td class="altbg2"><input id='txt_certificateid' type="text"
				size='16' /></td>
			<td class="altbg2" colspan="2"><input class="button" value="查找"
				type="button" onclick="search()"> <input class="button"
				value="导出" type="button" onclick="downLoad()"></td>
		</tr>
	</table>
	<div id="div_result"></div>

	<div id="enlarge_images" style="position: absolute; z-index: 999"></div>
</body>
</html>
