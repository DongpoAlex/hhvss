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
		var ordertime = $("txt_ordertime").value;
		if (ordertime != "" && ordertime.length != 5) {
			alert("请输入正确格式，例如：上午8点请输入08:00,下午5点请输入17:00");
			$("txt_ordertime").focus();
			return;
		}
		var orderlastdate = $("txt_orderlastdate").value;
		if (orderlastdate != "" && orderlastdate.length != 5) {
			alert("请输入正确格式，例如：上午8点请输入08:00,下午5点请输入17:00");
			$("txt_orderlastdate").focus();
			return;
		}
		
		var table_add = new AW.XML.Table;
		var parms = new Array();
		parms.push("action=upd");
		parms.push("dccode=" + $("txt_dccode").value);
		if($('txt_isyesps').checked){
			parms.push("isyesps=Y");
		}else{
			parms.push("isyesps=N");
		}
		parms.push("ordertime=" + $("txt_ordertime").value);
		if ($("txt_orderkfts").value != undefined && $("txt_orderkfts").value != "") {
			parms.push("orderkfts=" + $("txt_orderkfts").value);
		}else{
			parms.push("orderkfts=0");
		}
		parms.push("stoporderdate=" + $("txt_stoporderdate").value);
		parms.push("orderlastdate=" + $("txt_orderlastdate").value);
		parms.push("ordernote=" + $("txt_ordernote").value);
		parms.push("dzsku=" + $("txt_dzsku").value);
		parms.push("dzpqty=" + $("txt_dzpqty").value);
		setLoading(true);
		var url = "../DaemonNetOrderParam?" + parms.join('&');
		table_add.setURL(url);
		table_add.setTable("xdoc/xout/netnetorderparam");
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
	function init(dccode){
		setLoading( true );
		var parms = new Array();
		parms.push("action=netparam_list");
		parms.push("dccode="+dccode);
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
				$('txt_dccode').innerText=table.getXMLText("/xdoc/xout/netnetorderparam/row/dccode");
				if(table.getXMLText( "/xdoc/xout/netnetorderparam/row/isyesps")=="Y"){
					$('txt_isyesps').checked=true;
				}else{
					$('txt_isyesps').checked=false;
				}
				$('txt_ordertime').innerText=table.getXMLText( "/xdoc/xout/netnetorderparam/row/ordertime" );
				$('txt_orderkfts').innerText=table.getXMLText( "/xdoc/xout/netnetorderparam/row/orderkfts" );
				$('txt_stoporderdate').innerText=table.getXMLText( "/xdoc/xout/netnetorderparam/row/stoporderdate" );
				$('txt_orderlastdate').innerText=table.getXMLText( "/xdoc/xout/netnetorderparam/row/orderlastdate" );
				$('txt_ordernote').innerText=table.getXMLText( "/xdoc/xout/netnetorderparam/row/ordernote" );
				$('txt_dzsku').innerText=table.getXMLText( "/xdoc/xout/netnetorderparam/row/dzsku" );
				$('txt_dzpqty').innerText=table.getXMLText( "/xdoc/xout/netnetorderparam/row/dzpqty" );
				$("txt_dccode").disabled =true;
			}
			setLoading( false );
		};
	}
</script>
</head>
<body onload="init('<%=dccode%>')">
	<div id="divTitle">修改参数设置</div>

	<hr size="1" />
	<div id="div_tiemadd">
		<div style="margin-bottom: 10px;">
			DC编码&nbsp;&nbsp;：<input type="text" size="20" maxlength="20"
				id="txt_dccode"></input>
		</div>
		<div style="margin-bottom: 10px;">
			是否允许非配送日预约：<input type="checkbox" size="20" maxlength="10"
				id="txt_isyesps"></input>
		</div>
		<div style="margin-bottom: 10px;">
			预约送货时间点：<input type="text" size="20" maxlength="10"
				id="txt_ordertime"></input><font>(输入格式为01:00-24:00)</font>
		</div>
		<div style="margin-bottom: 10px;">
			限制天数：<input type="text" size="10" maxlength="10" id="txt_orderkfts"></input>（天）
		</div>
		<div style="margin-bottom: 10px;">
			暂停预约日期：<input type="text" size="30" maxlength="50"
				id="txt_stoporderdate"></input>(日期之间用逗号隔开)
		</div>
		<div style="margin-bottom: 10px;">
			预约最晚时间点：<input type="text" size="20" maxlength="10"
				id="txt_orderlastdate"></input><font>(输入格式为01:00-24:00)</font>
		</div>
		<div style="margin-bottom: 10px;">
			判断是否大宗供应商SKU数：<input type="text" size="10" maxlength="10"
				id="txt_dzsku"></input>
		</div>
		<div style="margin-bottom: 10px;">
			判断是否大宗供应商箱数：<input type="text" size="10" maxlength="10"
				id="txt_dzpqty"></input>
		</div>
		<div style="margin-bottom: 10px;">
			预约备注：
			<textarea cols="60" rows="3" id="txt_ordernote"></textarea>
			(最多200字)
		</div>
		<div style="margin-bottom: 10px;">
			<input type="button" onclick="save()" value="保存" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</div>
	</div>
</body>
</html>
