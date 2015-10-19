<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	
	
	String dccode = request.getParameter("dccode");
	String logistics = request.getParameter("logistics");
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
<title>修改参数设置</title>
<script language="javascript" type="text/javascript">
	function save() {
		
		var table_add = new AW.XML.Table;
		var parms = new Array();
		parms.push("action=upddate");
		parms.push("dccode=" + $("txt_dccode").value);
		
		if($("txt_logistics").value=="直送"){
			parms.push("logistics=1");
		}else if($("txt_logistics").value=="直通"){
			parms.push("logistics=2");
		}else if ($("txt_logistics").value=="配送"){
			parms.push("logistics=3");
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
				alert("修改成功！");
				window.close();
			}
		};
	}

	function init(dccode,logistics){
		setLoading( true );
		var parms = new Array();
		parms.push("action=netparamdate_list");
		parms.push("dccode="+dccode);
		parms.push("logistics="+logistics);
		var url  = "../DaemonNetOrderParam?"+parms.join('&');
		var table = new AW.XML.Table;
		table.setURL(url);
		table.request();
		table.response = function(text){
			table.setXML(text);
			var xcode = table.getErrCode();
			if( xcode != '0' ){//处理xml中的错误消息
				alert( xcode+table.getErrNote());
			}else{
				$('txt_dccode').innerText=table.getXMLText("/xdoc/xout/netorderparamdate/row/dccode");
				if(table.getXMLText("/xdoc/xout/netorderparamdate/row/logistics")=="1"){
					$('txt_logistics').innerText="直送";
				}else if(table.getXMLText("/xdoc/xout/netorderparamdate/row/logistics")=="2"){
					$('txt_logistics').innerText="直通";
				}else if(table.getXMLText("/xdoc/xout/netorderparamdate/row/logistics")=="3"){
					$('txt_logistics').innerText="配送";
				}
				if(table.getXMLText( "/xdoc/xout/netorderparamdate/row/monday")=="Y"){
					$('txt_monday').checked=true;
				}else{
					$('txt_monday').checked=false;
				}
				if(table.getXMLText( "/xdoc/xout/netorderparamdate/row/tuesday")=="Y"){
					$('txt_tuesday').checked=true;
				}else{
					$('txt_tuesday').checked=false;
				}
				if(table.getXMLText( "/xdoc/xout/netorderparamdate/row/wednesday")=="Y"){
					$('txt_wednesday').checked=true;
				}else{
					$('txt_wednesday').checked=false;
				}
				if(table.getXMLText( "/xdoc/xout/netorderparamdate/row/thursday")=="Y"){
					$('txt_thursday').checked=true;
				}else{
					$('txt_thursday').checked=false;
				}
				if(table.getXMLText( "/xdoc/xout/netorderparamdate/row/friday")=="Y"){
					$('txt_friday').checked=true;
				}else{
					$('txt_friday').checked=false;
				}
				if(table.getXMLText( "/xdoc/xout/netorderparamdate/row/saturday")=="Y"){
					$('txt_saturday').checked=true;
				}else{
					$('txt_saturday').checked=false;
				}
				if(table.getXMLText( "/xdoc/xout/netorderparamdate/row/sunday")=="Y"){
					$('txt_sunday').checked=true;
				}else{
					$('txt_sunday').checked=false;
				}
				$('txt_note').innerText=table.getXMLText( "/xdoc/xout/netorderparamdate/row/note" );
				$("txt_dccode").disabled =true;
				$("txt_logistics").disabled =true;
			}
			setLoading( false );
		};
	}
</script>
</head>
<body onload="init('<%=dccode%>','<%=logistics%>')">
	<div id="divTitle">修改参数设置</div>

	<hr size="1" />
	<div id="div_tiemadd">
		<div style="margin-bottom: 10px;">
			DC编码&nbsp;&nbsp;：<input type="text" size="20" maxlength="20"
				id="txt_dccode"></input>
		</div>
		<div style="margin-bottom: 10px;">
			物流模式：<input type="text" size="20" maxlength="20" id="txt_logistics"></input>
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
			<input type="button" onclick="save()" value="保存" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</div>
	</div>
</body>
</html>
