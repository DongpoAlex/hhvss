﻿<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 3090100;
%>
<%
	request.setCharacterEncoding("UTF-8");

	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);

	//查询用户的权限.
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ))
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系. 模块号:"
				+ moduleid);

	int sid = token.site.getSid();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>供应商资料维护</title>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../css/venderinfo.css.css" rel="stylesheet"></link>
<script language="javascript" src="../AW/runtime/lib/aw.js">
	
</script>
<script type="text/javascript" src="../js/common.js"></script>
<script type="text/javascript" src="../js/jquery.min.js"></script>
</head>
<body>
	<fieldset style="padding: 6px;">
		<legend> 【供应商联系资料维护】 </legend>
		<span style="font-size: 14px; color: red;">请正确维护联系资料，邮寄费用发票或有问题的发票单据将以此地址为准邮寄给贵司；如有变更，请及时自行更新，如因贵司所提供资料有误造成的投递错误，将由贵司承担相应的责任。</span>
		<table border="0" width="90%">
			<tr>
				<td class="needed">*供应商邮寄地址:</td>
				<td><input type="text" id="c_postaddress" size="80"
					class="input"></td>
				<td class="needed">*邮编:</td>
				<td><input type="text" id="c_postcode" class="input"></td>
			</tr>
			<tr>
				<td class="needed">*联系人:</td>
				<td><input type="text" id="c_contactperson" class="input">
					*办公电话: <input type="text" id="c_telno" onblur="checkTeleno()"
					class="input"></td>
				<td class="needed">*手机:</td>
				<td><input type="text" id="c_mobileno" onblur="checkMobile()"
					class="input"></td>
			</tr>
			<tr>
				<td>备注信息:</td>
				<td><input type="text" id="c_note" size="80" class="input"></td>
				<td>更新日期:</td>
				<td><input type="text" disabled="disabled" style=""
					id="c_updatedate" class="input"></td>
			</tr>
		</table>

		<div style="margin-top: 10px;">
			<!--
			1.日期格式：(可输入如：2001/3/8,2004.10.1, 03\12\31, 2000-02-28, 10/31, 15等格式的日期)范围:1900--2007<br>
			2.邮编：采用六位编码制(例如：邮政编码“130021”“13”代表吉林省，“00”代表省会长春，“21”代表所在投递区)<br>
			3.移动电话不超过15位<br>
			-->
			1.联系电话:例：北京086(国码)-010(区号)-12345678(电话号码),香港852(国码)-区号-12345678(电话号码)（国内电话区号表）<br>
		</div>

	</fieldset>


	<fieldset style="padding: 6px;" id="tax_set">
		<legend> 【供应商增值税专用发票开票资料维护】 </legend>
		<span style="font-size: 14px; color: red;">如贵司为一般纳税人，请正确维护贵司的开票资料，我司为贵司开具增值税专用发票时，将以此信息为准；如有变更，请及时自行更新，如因贵司所提供资料有误造成的退票，将由贵司承担相应的责任。</span>
		<table border="0" width="90%">
			<tr>
				<td class="needed" colspan="2"><label for="t_0">一般纳税人：<input
						type="radio" id="t_0" value="0" name="t_tax" style="border: none;" /></label>&nbsp;&nbsp;&nbsp;&nbsp;
					<label for="t_1">小规模纳税人或自然人：<input type="radio" id="t_1"
						value="1" name="t_tax" style="border: none;" /></label></td>
			</tr>
			<tr>
				<td width="10%">供应商开票名称：</td>
				<td><input type="text" style="" id="t_taxname" class="input"
					size="80" maxlength="100"></td>
			</tr>
			<tr>
				<td width="10%">纳税识别号：</td>
				<td><input type="text" style="" id="t_taxno" class="input"
					size="80" maxlength="40"></td>
			</tr>
			<tr>
				<td width="10%">地址、电话：</td>
				<td><input type="text" style="" id="t_taxaddrtel" class="input"
					size="80" maxlength="200"></td>
			</tr>
			<tr>
				<td width="10%">开户行及账号：</td>
				<td><input type="text" style="" id="t_taxbank" class="input"
					size="80" maxlength="200"></td>
			</tr>
		</table>

		<div style="margin-top: 10px;"></div>

	</fieldset>

	<div class="button">
		<input type="button" value="保存资料" id="changeInfo"
			onclick="postVenderInfo()" class="input" name="btninfo"> <input
			type="button" value="清空资料" id="clearInfo" onclick="clearVenderData()"
			class="input" name="btninfo"> <span id="message"
			style="display: none;"></span>
	</div>



	<script language="javascript">
		var sid =<%=sid%>;
		jQuery(document).ready(function() {
			initinfo();

			if (sid == 1 || sid == 2 || sid == 11) {
				jQuery('#tax_set').show();
			} else {
				jQuery('#tax_set').hide();
			}

			jQuery('#t_1').change(function(event) {
				var obj = jQuery(event.target);
				if (obj.attr('checked') == 'checked') {
					jQuery("#t_taxname").val('');
					jQuery("#t_taxno").val('');
					jQuery("#t_taxaddrtel").val('');
					jQuery("#t_taxbank").val('');
					jQuery("#t_taxname").attr('disabled', true);
					jQuery("#t_taxno").attr('disabled', true);
					jQuery("#t_taxaddrtel").attr('disabled', true);
					jQuery("#t_taxbank").attr('disabled', true);
				}
			});

			jQuery('#t_0').change(function(event) {
				var obj = jQuery(event.target);
				if (obj.attr('checked') == 'checked') {
					jQuery("#t_taxname").attr('disabled', false);
					jQuery("#t_taxno").attr('disabled', false);
					jQuery("#t_taxaddrtel").attr('disabled', false);
					jQuery("#t_taxbank").attr('disabled', false);
				}
			});
		});

		function initinfo() {
			var url = "../DaemonVenderAdmin?focus=venderdiy&operation=list";
			jQuery.get(url, {}, function(xml) {
				init_data(xml);
			});
		}

		function init_data(xml) {
			var elm = jQuery(xml).find('rowset');
			var postaddress = elm.find("postaddress").text();
			jQuery("#c_postaddress").val(postaddress);
			var postcode = elm.find("postcode").text();
			jQuery("#c_postcode").val(postcode);
			var contactperson = elm.find("contactperson").text();
			jQuery("#c_contactperson").val(contactperson);
			var telno = elm.find("telno").text();
			jQuery("#c_telno").val(telno);
			var mobileno = elm.find("mobileno").text();
			jQuery("#c_mobileno").val(mobileno);
			var note = elm.find("note").text();
			jQuery("#c_note").val(note);
			var updatedate = elm.find("updatedate").text();
			jQuery("#c_updatedate").val(updatedate);

			var taxtype = elm.find("taxtype").text();
			if (taxtype != '' && taxtype == 0) {
				jQuery("#t_0").attr("checked", "true");
			} else {
				jQuery("#t_1").attr("checked", "true");
			}

			var taxname = elm.find("taxname").text();
			jQuery("#t_taxname").val(taxname);
			var taxno = elm.find("taxno").text();
			jQuery("#t_taxno").val(taxno);
			var taxaddrtel = elm.find("taxaddrtel").text();
			jQuery("#t_taxaddrtel").val(taxaddrtel);
			var taxbank = elm.find("taxbank").text();
			jQuery("#t_taxbank").val(taxbank);
		}

		function postVenderInfo() {
			var parms = new Array();
			var postaddress = jQuery("#c_postaddress").val();
			var postcode = jQuery("#c_postcode").val();
			var contactperson = jQuery("#c_contactperson").val();
			var telno = jQuery("#c_telno").val();
			var mobileno = jQuery("#c_mobileno").val();
			var note = jQuery("#c_note").val();

			var taxname = jQuery("#t_taxname").val();
			var taxno = jQuery("#t_taxno").val();
			var taxaddrtel = jQuery("#t_taxaddrtel").val();
			var taxbank = jQuery("#t_taxbank").val();

			var taxtype = '';
			if (jQuery("#t_0").attr("checked") == "checked") {
				taxtype = 0;
			}
			if (jQuery("#t_1").attr("checked") == "checked") {
				taxtype = 1;
			}

			if ((sid == 1 || sid == 2 || sid == 11) && taxtype == 0) {
				if (taxname == '') {
					alert("供应商开票名称不能为空");
					return;
				}
				if (taxno == '') {
					alert("纳税识别号不能为空");
					return;
				}
				if (taxaddrtel == '') {
					alert("地址、电话不能为空");
					return;
				}
				if (taxbank == '') {
					alert("开户行及账号不能为空");
					return;
				}
			}

			if (postaddress == '') {
				alert("邮寄地址不能为空");
				return;
			}
			if (postcode == '') {
				alert("邮政编码不能为空");
				return;
			}
			if (contactperson == '') {
				alert("联系人不能为空");
				return;
			}
			if (telno == '') {
				alert("办公电话不为空");
				return;
			}
			if (mobileno == '') {
				alert("手机不为空");
				return;
			}

			parms.push("postaddress=" + encodeURIComponent(postaddress));
			parms.push("postcode=" + encodeURIComponent(postcode));
			parms.push("contactperson=" + encodeURIComponent(contactperson));
			parms.push("telno=" + telno);
			parms.push("mobileno=" + mobileno);
			parms.push("note=" + encodeURIComponent(note));

			parms.push("taxtype=" + encodeURIComponent(taxtype));
			parms.push("taxname=" + encodeURIComponent(taxname));
			parms.push("taxno=" + encodeURIComponent(taxno));
			parms.push("taxaddrtel=" + encodeURIComponent(taxaddrtel));
			parms.push("taxbank=" + encodeURIComponent(taxbank));

			var url = "../DaemonVenderAdmin?focus=venderdiy&operation=update&"
					+ parms.join('&');
			jQuery.get(url, {}, function(xml) {
				var elm = jQuery(xml);
				var code = elm.find('code').text();
				var note = elm.find('note').text();
				if (code != 0) {
					alert(note);
				} else {
					alert("保存成功");
				}
			});
		}

		//清空
		function clearVenderData() {
			jQuery('input:text').each(function() {
				jQuery(this).val('');
			});
		}

		//检查手机
		function checkMobile() {
			var str = jQuery("#c_mobileno").val();
			if (str == '') {
				return;
			}
			if (!isTel(str)) {
				alert("手机格式错误，请修正！");
				jQuery("#c_mobileno").focus();
			}
		}

		//检查办公电话
		function checkTeleno() {
			var str = jQuery("#c_telno").val();
			if (str == '') {
				return;
			}

			if (!isTel(str)) {
				alert("电话格式错误！\r\n数字或×××-××××××××格式");
				jQuery("#c_telno").focus();
			}

			/*
			if (!isTel(str) && !isMobileTel(str)) {
				alert("无效");
				jQuery("c_telno").focus();
			}
			 */
		}
	</script>

</body>
</html>
