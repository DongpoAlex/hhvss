<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.certificate.bean.Config"
	import="com.royalstone.certificate.*"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*" import="java.sql.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
request.setCharacterEncoding( "UTF-8" );
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );
//查询用户的权限.
final int moduleid=8000005;
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );

	String sheetid = request.getParameter("sheetid");
	String seqno = request.getParameter("seqno");
	String type = request.getParameter("type");
	String title =Config.getTypeName(type,token);
	
	//控件显示
	String dis_ctname="block";
	String dis_cname="none";
	String dis_certificateid="block";
	String dis_ctype="none";
	String dis_yeardate="none";
	String dis_expirydate="none";
	String dis_goodsname="none";
	String dis_barcodeid="none";
	String dis_note="block";
	String dis_approvalnum="none";
	String dis_papprovalnum="none";
	
	if("1".equals(type)){
		dis_ctype=dis_expirydate="block";
	}else if("2".equals(type)){
		dis_ctype=dis_expirydate="block";
	}else if("3".equals(type)){
		dis_expirydate=dis_barcodeid=dis_goodsname="block";
	}else if("4".equals(type)){
		dis_expirydate=dis_barcodeid=dis_goodsname="block";
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="./css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="./common.js"> </script>
<script language="javascript" src="./certificate_detail.js"> </script>
<style>
</style>
</head>
<body onload="init('<%=sheetid%>',<%=seqno%>)">
	<div class="main">
		<div class="title"><%=title %>明细
		</div>
		<div class="content">
			<table cellpadding="4px" cellspacing="1">
				<tr>
					<th>单据编码：</th>
					<td id="txt_sheetid"><%=sheetid%></td>
					<th>单据状态：</th>
					<td id="txt_flag"></td>
				</tr>
				<tr>
					<th>供应商编码：</th>
					<td id="txt_venderid"></td>
					<th>供应商名称：</th>
					<td id="txt_vendername"></td>
				</tr>
				<tr>
					<th>供应商地址：</th>
					<td colspan="3" id="txt_addr"></td>
				</tr>
				<tr>
					<th>联系电话：</th>
					<td id="txt_tel"></td>
					<th>联系人：</th>
					<td id="txt_contact"></td>
				</tr>
			</table>
			<div style="width: 100%; border: solid 1px #3f3f3f; padding: 12px;">
				<div class="tool" id="tool"></div>
				<div style='width: 100%'>
					<span id='input_ctid'
						style="margin-right:20px;float:left;display: <%=dis_ctname %>;">
						证照类型： <input class="lineinput" readonly="readonly" id='txt_ctname'
						type='text' size='20' maxlength='64' value='' />
					</span> <span id='input_cname'
						style="margin-right:20px;float:left;display: <%=dis_cname %>">
						证照名称： <input class="lineinput" readonly="readonly" id='txt_cname'
						type='text' size='20' maxlength='64' value='' />
					</span> <span id='input_certificateid'
						style="margin-right:20px;float:left;display: <%=dis_certificateid %>">
						证照编码： <input class="lineinput" readonly="readonly"
						id='txt_certificateid' type='text' size='20' maxlength='64'
						value='' />
					</span> <span id='input_ctype'
						style="margin-right:20px;float:left;display: <%=dis_ctype %>">
						证照性质： <span id='input_ctype' class='xiahuaxian'> <input
							class="lineinput" readonly="readonly" id='txt_ctype' type='text'
							size='20' maxlength='64' value='' />
					</span>
					</span> <span id='input_expirydate'
						style="margin-right:20px;float:left;display: <%=dis_expirydate %>">
						有效期至： <input class="lineinput" readonly="readonly"
						id='txt_expirydate' type='text' size='8' onblur='checkDate(this)'
						value='' />
					</span> <span id='input_yeardate'
						style="margin-right:20px;float:left;display: <%=dis_yeardate %>">
						年审日期： <input class="lineinput" readonly="readonly"
						id='txt_yeardate' type='text' size='8' onblur='checkDate(this)'
						value='' />
					</span>
				</div>
				<div style='width: 100%; margin-top: 4px;'>

					<span id='input_goodsname'
						style="margin-right:20px;float:left;display: <%=dis_goodsname %>">
						商品名称： <input class="lineinput" readonly="readonly"
						readonly="readonly" id='txt_goodsname' type='text' size='20'
						maxlength='64' value='' />
					</span> <span id='input_barcodeid'
						style="margin-right:20px;float:left;display: <%=dis_barcodeid %>">
						商品条码： <input class="lineinput" readonly="readonly"
						id='txt_barcodeid' type='text' size='20' maxlength='64' value='' />
					</span> <span id='input_approvalnum'
						style="margin-right:20px;float:left;display: <%=dis_approvalnum %>">
						批文号： <input class="lineinput" readonly="readonly"
						id='txt_approvalnum' type='text' size='20' maxlength='64' value='' />
					</span> <span id='input_papprovalnum'
						style="margin-right:20px;float:left;display: <%=dis_papprovalnum %>">
						生产日期： <input class="lineinput" readonly="readonly"
						id='txt_papprovalnum' type='text' size='20' maxlength='64'
						value='' />
					</span> <span id='input_note' style="float:left;display: <%=dis_note %>">
						备注： <input class="lineinput" readonly="readonly" id='txt_note'
						type='text' size='30' maxlength='128' value='' />
					</span>
				</div>
			</div>
		</div>
		<hr>
		<div>
			<div id="imagesList" style="display: none; margin-right: 20px;"></div>
			<div style="width: 400px;">
				<span><img src="./images/trunLeft.png"
					style="cursor: pointer; float: left; width: 27px; height: 27px;"
					onclick="imgTrunLeft()"></span> <span><img
					src="./images/imgdel.gif"
					style="cursor: pointer; float: left; width: 27px; height: 27px;"
					onclick="imgDel()"></span> <span><img
					src="./images/trunRight.png"
					style="cursor: pointer; float: right; width: 27px; height: 27px;"
					onclick="imgTrunRight()"></span> <span><img
					src="./images/imgadd.gif"
					style="cursor: pointer; float: right; width: 27px; height: 27px;"
					onclick="imgAdd()"></span>
			</div>

			<a id="imghref" href="" target="_blank"> <span id="spanImgShow">
			</span>
			</a>
		</div>
	</div>
</body>
</html>