<%@page contentType="text/html;charset=UTF-8"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid=5010102;
%>
<%
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
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<script language="javascript" src="../js/String.js" type=""> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js" type=""> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>

<style type="text/css">
.aw-grid-control {
	width: 100%;
	height: 100%
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

.aw-mouseover-row {
	background: #efefff;
}

.aw-column-0 {
	width: 40px;
	cursor: pointer;
}

.aw-column-1 {
	display: none;
}

.aw-column-2 {
	width: 60px;
}

.aw-column-3 {
	width: 160px;
}

#receive_grid .aw-column-5 {
	width: 50px;
}

#receive_grid .aw-column-6 {
	width: 60px;
}

#receive_grid .aw-column-8 {
	width: 60px;
}

#send_grid .aw-column-2 {
	width: 0px;
}

#send_grid .aw-column-5 {
	width: 60px;
}

#send_grid .aw-column-6 {
	width: 50px;
}

#send_grid .aw-column-7 {
	width: 50px;
}

#send_grid .aw-column-9 {
	width: 60px;
}

#mail_main {
	width: 100%;
	height: 100%;
	padding: 4px;
	border: 1px solid #D0C8A8;
	float: left;
	clear: both;
}

#mail_menu {
	width: 18%;
	padding: 4px;
	border: 1px dotted #C4A891;
	float: left;
}

#mail_menu SPAN {
	margin-top: 3px;
	font-size: 12px;
	color: #a9a9a9;
	cursor: pointer;
	float: right;
}

#mail_menu UL {
	margin: 0px;
	padding: 0px;
	list-style-type: none;
	width: 100%;
}

#mail_menu LI {
	background-image: url(../img/foldersmall.gif);
	background-repeat: no-repeat;
	background-position: 4px center;
	padding: 4px 0px 2px 24px;
	width: 100%;
	height: 34px;
	font-size: 14px;
	text-decoration: none;
	border: 2px solid #F9F8F4;
}

#mail_menu LI A {
	float: left;
	padding: 3px;
}

#mail_menu LI A:link {
	
}

#mail_menu LI A:hover {
	font-weight: bold;
	color: #CC0000;
	border-style: outset;
	border-width: 1px;
	padding-left: 1px;
	padding-top: 1px;
}

#mail_menu LI A:visited {
	
}

#mail_conent {
	float: right;
	width: 81%;
}

#list_addr span {
	cursor: pointer;
	color: blue;
}

#list_addr table {
	background-color: #999;
	width: 100%;
}

#list_addr td {
	background-color: #fff;
}

#list_addr th {
	background-color: #fff;
}

.current {
	background-color: #FEFEC0;
	font-weight: bold;
	color: #009900;
}

.content {
	display: none;
	height: 460px;
}

.mail_head {
	background-color: #F0FDD5;
	padding: 6px;
	line-height: 20px;
}

.mail_body {
	padding: 6px;
	background-color: #FFFFFF;
	border: 1px inset #F6F6F6;
	margin: 2px;
	width: 100%;
	height: 200px;;
}

.mail_content {
	border: 1px solid #999966;
	margin-top: 2px;
}

.down_file {
	color: #0066CC;
	text-decoration: underline;
	cursor: pointer;
}

.write_mail {
	border: 1px #def solid;
	padding: 10px;
	background-color: #fff;
}

.tool {
	padding: 4px;
}

.space {
	width: 18px;
}

.mail_list {
	height: 40%;
}

.drag_up {
	background-color: #E7E8CA;
	background-image: url(../img/button.gif);
	background-repeat: no-repeat;
	background-position: center 0px;
	height: 9px;
	font-size: 0px;
	cursor: pointer;
}

.drag_down {
	background-color: #E7E8CA;
	background-image: url(../img/button.gif);
	background-repeat: no-repeat;
	background-position: center -9px;
	height: 9px;
	font-size: 0px;
	cursor: pointer;
}
</style>

<script type="text/javascript">

</script>
<script language="javascript" src="./mail.js" type=""> </script>
</head>
<xml id="island4result" />
<xml id="island4temp" />
<xml id="island4send" />
<xml id="format4mail" src="format4mail.xsl" />
<body onload="init()">
	<div id="mail_main">
		<div id="mail_menu">
			<ul>
				<li><a href="#" onClick="javascript:menu_click(this)"
					id="a_new" name="new">写邮件</a></li>
				<li><a href="#" onClick="javascript:menu_click(this)"
					id="a_receive" name="receive">收件箱</a> <span id="mail_new_count"
					style="float: left; color: red;"></span> <span
					onclick="delAllMail('ReceiptMail')">清空</span></li>
				<li><a href="#" onClick="javascript:menu_click(this)"
					id="a_send" name="send">发件箱</a> <span
					onclick="delAllMail('SendMail')">清空</span></li>
				<li><a href="#" onClick="javascript:menu_click(this)"
					id="a_draft" name="draft">草稿</a> <span
					onclick="delAllMail('DraftMail')">清空</span></li>
				<li><a href="#" onClick="javascript:menu_click(this)"
					id="a_recycle" name="recycle">回收站</a> <span
					onclick="delAllMail('DelCompletely')">清空</span></li>
				<!--  通讯录关闭
		<li><a href="#" onClick="javascript:menu_click(this)" id="a_addr" name="addr">通讯录</a></li>
		-->

			</ul>
		</div>

		<div id="mail_conent">
			<div id="list_new" name="new" class="content">
				<div class="write_mail">
					<table>
						<tr>
							<td width="100">收件人：</td>
							<td width=""><input type="text" id="txt_receiptor" size="40"
								maxlength="40" /></td>
							<td rowspan="3" width="300" valign="top">
								<div id="defaultPayer"
									style="border: 1px solid #FF9900; width: 98%; padding: 4px;"></div>
							</td>
						</tr>
						<tr>
							<td>抄送给：</td>
							<td><input type="text" id="txt_cc" size="40" /></td>
							<td></td>
						</tr>
						<tr>
							<td>邮件标题：</td>
							<td><input type="text" id="txt_title" size="40"
								maxlength="60" /></td>
							<td></td>
						</tr>
						<tr>
							<td valign="top">邮件内容：</td>
							<td colspan="2"><textarea rows="15" id="txt_content"
									cols="90"></textarea></td>
						</tr>
						<tr>
							<td>添加附件：</td>
							<td colspan="2"><iframe src="./upload.jsp" width="100%"
									height="45" name="frame_upload" frameborder="0"></iframe></td>
						</tr>
						<tr>
							<td colspan="3"><input type="button" id="btnMailSend"
								value="发&nbsp;&nbsp;送" onClick="SendMail('NewMail')" /> <span
								class="space"></span> <input type="button" id="btnMailDraft"
								value="暂存为草稿" onClick="SendMail('Draft')" /> <span
								class="space"></span> <input type="button" value="重置发送"
								onClick="resetSend()" /></td>
						</tr>
					</table>
				</div>
			</div>
			<div id="mail_receive" name="receive" class="content">
				<div id="search_receive">
					发件人:<input type="text" id="search_sender" /> 收件日期:<input
						type="text" id="search_minreceivedate"
						onfocus="WdatePicker({skin:'whyGreen'})" class="Wdate" /> - <input
						type="text" id="search_maxreceivedate"
						onfocus="WdatePicker({skin:'whyGreen'})" class="Wdate" /> <img
						alt="查询" src="../img/find.png" style="cursor: pointer;"
						onclick="search_receive()" />
				</div>
				<div id="list_receive" class="mail_list"></div>
				<div class="drag_up" onclick="drag(this)" title="收缩邮件列表"></div>
				<div id="content_receive" class="mail_content"></div>
				<div id="tool_receive" class="tool"></div>
			</div>
			<div id="mail_send" name="send" class="content">
				<div id="search_send">
					收件人:<input type="text" id="search_receiver" /> 发件日期:<input
						type="text" id="search_minsenddate"
						onfocus="WdatePicker({skin:'whyGreen'})" class="Wdate" /> - <input
						type="text" id="search_maxsenddate"
						onfocus="WdatePicker({skin:'whyGreen'})" class="Wdate" /> <img
						alt="查询" src="../img/find.png" style="cursor: pointer;"
						onclick="search_send()" />
				</div>
				<div id="list_send" class="mail_list"></div>
				<div class="drag_up" onclick="drag(this)" title="收缩邮件列表"></div>
				<div id="content_send" class="mail_content"></div>
				<div id="tool_send" class="tool"></div>
			</div>
			<div id="mail_draft" name="draft" class="content">
				<div id="list_draft" class="mail_list"></div>
				<div class="drag_up" onclick="drag(this)" title="收缩邮件列表"></div>
				<div id="content_draft" class="mail_content"></div>
				<div id="tool_draft" class="tool"></div>
			</div>
			<div id="mail_recycle" name="recycle" class="content">
				<div id="list_recycle" class="mail_list"></div>
				<div class="drag_up" onclick="drag(this)" title="收缩邮件列表"></div>
				<div id="content_recycle" class="mail_content"></div>
				<div id="tool_recycle" class="tool"></div>
			</div>
		</div>
	</div>
</body>
</html>
