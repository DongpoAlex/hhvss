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
	String starttime = request.getParameter("starttime");
	String endtime = request.getParameter("endtime");
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
<style>
.aw-column-0,.aw-column-2 {
	width: 80px;
	cursor: pointer;
	text-align: center
}

.aw-column-1 {
	width: 180px;
}

.aw-column-4,.aw-column-6,.aw-column-7,.aw-column-8,.aw-column-9,.aw-column-10
	{
	width: 80px;
}

.aw-column-3 {
	width: 100px;
}

.aw-column-3 {
	width: 200px;
}
</style>
<title>修改预约时间设置</title>
<script language="javascript" type="text/javascript"><!--

	
	function save() {
		var table_add = new AW.XML.Table;
		var parms = new Array();
		parms.push("action=upd");
		parms.push("dccode=" + $("txt_docode").value);
		parms.push("logistics=" + $("txt_logistics").value);
		parms.push("starttime=" + $("txt_starttime").value);
		parms.push("endtime=" + $("txt_endtime").value);
		parms.push("timejg=" + $("txt_timejg").value);
		parms.push("maxsku=" + $("txt_maxsku").value);
		parms.push("maxxs=" + $("txt_maxxs").value);
		parms.push("maxsupply=" + $("txt_maxsupply").value);
		parms.push("maxdzsupply=" + $("txt_maxdzsupply").value);
		parms.push("maxyssupply=" + $("txt_maxyssupply").value);
		parms.push("note=" + $("txt_note").value);
		setLoading(true);
		var url = "../DaemonNetOrderTime?" + parms.join('&');
		table_add.setURL(url);
		table_add.setTable("xdoc/xout/netnetordertime");
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

	function init(dccode,logistics,starttime,endtime){
		setLoading( true );
		var parms = new Array();
		parms.push("action=getordertiemList");
		parms.push("dccode="+dccode);
		parms.push("logistics="+logistics);
		parms.push("starttime="+starttime);
		parms.push("endtime="+endtime);
		var url  = "../DaemonNetOrderTime?"+parms.join('&');
		var table = new AW.XML.Table;
		table.setURL(url);
		table.request();
		table.response = function(text){
			table.setXML(text);
			var xcode = table.getErrCode();
			if( xcode != '0' ){//处理xml中的错误消息
				alert( xcode+table.getErrNote());
			}else{
				$('txt_docode').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/dccode");
				$('txt_logistics').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/logistics");
				$('txt_logisticsname').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/logisticsname");
				$('txt_starttime').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/starttime" );
				$('txt_endtime').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/endtime" );
				$('txt_timejg').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/timejg" );
				$('txt_maxsku').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/maxsku" );
				$('txt_maxxs').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/maxxs" );
				$('txt_maxsupply').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/maxsupply" );
				$('txt_maxdzsupply').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/maxdzsupply" );
				$('txt_maxyssupply').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/maxyssupply" );
				$('txt_note').innerText=table.getXMLText( "/xdoc/xout/netnetordertime/row/note" );
			}
			setLoading( false );
		};
	}
</script>
</head>
<body
	onload="init('<%=dccode%>','<%=logistics%>','<%=starttime%>','<%=endtime%>')">
	<div id="divTitle">修改预约时间设置</div>

	<hr size="1" />
	<div id="div_tiemadd">
		<div style="margin-bottom: 10px;">
			DC编码&nbsp;&nbsp;:<input type="text" size="20" maxlength="20"
				id="txt_docode" readonly="readonly"></input>
		</div>
		<div style="margin-bottom: 10px;">
			物流模式:<input type="hidden" id="txt_logistics"> <input
				type="text" size="20" maxlength="20" id="txt_logisticsname"
				readonly="readonly"></input>
		</div>
		<div style="margin-bottom: 10px;">
			开始时间:<input type="text" size="20" maxlength="10" id="txt_starttime"
				readonly="readonly"></input><font>(输入格式为01:00-24:00)</font>
		</div>
		<div style="margin-bottom: 10px;">
			结束时间:<input type="text" size="20" maxlength="10" id="txt_endtime"
				readonly="readonly"></input><font>(输入格式为01:00-24:00)</font>
		</div>
		<div style="margin-bottom: 10px;">
			时间间隔:<input type="text" size="20" maxlength="10" id="txt_timejg"
				readonly="readonly"></input>(分钟)
		</div>
		<div style="margin-bottom: 10px;">
			SKU数上限:<input type="text" size="20" maxlength="10" id="txt_maxsku"></input>（个）
		</div>
		<div style="margin-bottom: 10px;">
			箱数上限:<input type="text" size="20" maxlength="10" id="txt_maxxs"></input>（个）
		</div>
		<div style="margin-bottom: 10px;">
			供应商个数上限:<input type="text" size="20" maxlength="10"
				id="txt_maxsupply"></input>（个）
		</div>
		<div style="margin-bottom: 10px;">
			大宗供应商预约个数上限:<input type="text" size="20" maxlength="10"
				id="txt_maxdzsupply"></input>（个）
		</div>
		<div style="margin-bottom: 10px;">
			综合验收供应商数上限:<input type="text" size="20" maxlength="10"
				id="txt_maxyssupply"></input>（个）
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