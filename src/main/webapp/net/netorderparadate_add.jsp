<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.security.*"
	import="com.royalstone.vss.net.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	
	SelNetDCshop shop = new SelNetDCshop(token,"请选择");
	shop.setAttribute("id","txt_dccode");
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
<script language="javascript" src="../js/EditGrid.js">
	
</script>
<title>参数预约日期设置</title>
<script language="javascript" type="text/javascript">
	function save() {
		var dccode = $("txt_dccode").value;
		if (dccode == "" ) {
			alert("DC编码必须填写！");
			$("txt_dccode").focus();
			return;
		}
		var logistics = $("txt_logistics").value;
		if (logistics == "") {
			alert("物流模式必须填写");
			$("txt_logistics").focus();
			return;
		}
		var table_add = new AW.XML.Table;
		var parms = new Array();
		parms.push("action=adddate");
		if ($("txt_dccode").value != undefined && $("txt_dccode").value != "") {
			parms.push("dccode=" + $("txt_dccode").value);
		}
		
		if ($("txt_logistics").value != undefined
				&& $("txt_logistics").value != "") {
			parms.push("logistics=" + $("txt_logistics").value);
		}
		if($('txt_monday').checked){
			parms.push("monday=Y");
		}else{
			parms.push("monday=N");
		}
		if($('txt_tuesday').checked){
			parms.push("tuesday=Y");
		}else{
			parms.push("tuesday=N");
		}
		if($('txt_wednesday').checked){
			parms.push("wednesday=Y");
		}else{
			parms.push("wednesday=N");
		}
		if($('txt_thursday').checked){
			parms.push("thursday=Y");
		}else{
			parms.push("thursday=N");
		}
		if($('txt_friday').checked){
			parms.push("friday=Y");
		}else{
			parms.push("friday=N");
		}
		if($('txt_saturday').checked){
			parms.push("saturday=Y");
		}else{
			parms.push("saturday=N");
		}
		if($('txt_sunday').checked){
			parms.push("sunday=Y");
		}else{
			parms.push("sunday=N");
		}
		parms.push("note=" + $("txt_note").value);
		setLoading(true);
		var url = "../DaemonNetOrderParam?" + parms.join('&');
		table_add.setURL(url);
		table_add.setTable("xdoc/xout/netorderparamdate");
		table_add.setRows("row");
		table_add.request();
		table_add.response = function(text) {
			setLoading(false);
			table_add.setXML(text);
			var xcode = table_add.getXMLText("xdoc/xerr/code");
			if (xcode != 0) {
				alert(xcode + table_add.getXMLText("xdoc/xerr/note"));
			} else {
				alert("保存成功！");
				initText();
			}
		};
	}
	function initText(){
		txt_dccode.value = "";
		txt_logistics.value = "";
		$('txt_monday').checked=false;
		$('txt_tuesday').checked=false;
		$('txt_wednesday').checked=false;
		$('txt_thursday').checked=false;
		$('txt_friday').checked=false;
		$('txt_saturday').checked=false;
		$('txt_sunday').checked=false;
		txt_note.value = "";
	}
</script>
</head>
<body>
	<div id="divTitle">参数日期设置</div>

	<hr size="1" />
	<div id="div_tiemadd">
		<div style="margin-bottom: 10px;">
			DC：<%=shop%></div>
		<div style="margin-bottom: 10px;">
			物流模式：<select id="txt_logistics">
				<option value="1">直送</option>
				<option value="2">直通</option>
			</select>
		</div>
		<div style="margin-bottom: 10px;">
			可预约日期： 周一<input type="checkbox" size="20" maxlength="10"
				id="txt_monday"></input>&nbsp;&nbsp; 周二<input type="checkbox"
				size="20" maxlength="10" id="txt_tuesday"></input>&nbsp;&nbsp; 周三<input
				type="checkbox" size="20" maxlength="10" id="txt_wednesday"></input>&nbsp;&nbsp;
			周四<input type="checkbox" size="20" maxlength="10" id="txt_thursday"></input>&nbsp;&nbsp;
			周五<input type="checkbox" size="20" maxlength="10" id="txt_friday"></input>&nbsp;&nbsp;
			周六<input type="checkbox" size="20" maxlength="10" id="txt_saturday"></input>
			&nbsp;&nbsp; 周日<input type="checkbox" size="20" maxlength="10"
				id="txt_sunday"></input>
		</div>
		<div style="margin-bottom: 10px;">
			备注：
			<textarea cols="60" rows="3" id="txt_note"></textarea>
			(最多500字)
		</div>

		<div style="margin-bottom: 10px;">
			<input type="button" onclick="save()" value="保存" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input
				type="button" onclick="initText()" value="清除" />
		</div>
	</div>
</body>
</html>