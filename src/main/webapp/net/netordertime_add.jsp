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
	shop.setAttribute("id","txt_docode");
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
<title>新增预约时间设置</title>
<script language="javascript" type="text/javascript">
	function save() {
		
		var dccode = $("txt_docode").value;
		if (dccode == "" ) {
			alert("DC编码必须填写！");
			$("txt_docode").focus();
			return;
		}
		
		var starttime = $("txt_starttime").value;
		if (starttime == "" || starttime.length != 5) {
			alert("开始时间必须填写！请输入正确格式，例如：上午8点请输入08:00,下午5点请输入17:00");
			$("txt_starttime").focus();
			return;
		}
		var endtime = $("txt_endtime").value;
		if (endtime == "" || endtime.length != 5) {
			alert("结束时间必须填写！请输入正确格式，例如：上午8点请输入08:00,下午5点请输入17:00");
			$("txt_endtime").focus();
			return;
		}
		var table_add = new AW.XML.Table;
		var parms = new Array();
		parms.push("action=add");
		parms.push("dccode=" + $("txt_docode").value);
		var logistics = $("txt_logistics").value;
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
				alert("保存成功！");
				initText();
			}
		};
	}
	function initText(){
		txt_docode.value = "";
		txt_starttime.value = "";
		txt_endtime.value = "";
		txt_timejg.value = "";
		txt_maxsku.value = "";
		txt_maxxs.value = "";
		txt_maxsupply.value = "";
		txt_maxdzsupply.value = "";
		txt_note.value = "";
	}
	
	function timejg(obj){
		var starttime = $('txt_starttime').value;
		if(starttime==''){
			alert('必须先录入开始时间！');
			obj.value='';
			return;
		}
		var ss = starttime.split(':');
		if(starttime.length!=5 || ss.length!=2){
			alert('开始时间格式错误！');
			obj.value='';
			return;
		}
		var h = ss[0];
		var m = ss[1];
		var c = obj.value;
		var mc = Number(m)+Number(c);
		var mm= Math.floor(mc%60);
		var hc = Math.floor(mc/60);
		var hh = (Number(h)+hc)%24;
		if(hh<10){
			hh = "0"+hh;
		}
		if(mm<10){
			mm = "0"+mm;
		}
		$("txt_endtime").value = hh+":"+mm;
	}
</script>
</head>
<body>
	<div id="divTitle">新增预约时间设置</div>

	<hr size="1" />
	<div id="div_tiemadd">
		<div style="margin-bottom: 10px;">
			DC:<%=shop%></div>
		<div style="margin-bottom: 10px;">
			物流模式:<select id="txt_logistics">
				<option value="1">直送</option>
				<option value="2">直通</option>
			</select>
		</div>
		<div style="margin-bottom: 10px;">
			开始时间:<input type="text" size="20" maxlength="10" id="txt_starttime"></input><font>(输入格式为01:00-24:00)</font>
		</div>
		<div style="margin-bottom: 10px;">
			结束时间:<input type="text" size="20" maxlength="10" id="txt_endtime"
				readonly="readonly"></input><font>(输入格式为01:00-24:00)</font>
		</div>
		<div style="margin-bottom: 10px;">
			时间间隔:<input type="text" size="20" maxlength="10" id="txt_timejg"
				onchange="timejg(this)"></input>(分钟)
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
			<input type="button" onclick="save()" value="保存" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input
				type="button" onclick="initText()" value="清除" />
		</div>
	</div>
</body>
</html>