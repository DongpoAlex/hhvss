<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	final int moduleid = 6000010;
%>

<%
	request.setCharacterEncoding("UTF-8");
	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	String venderid = token.getBusinessid();
	//查询用户的权限.
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ)) {
		//throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
	}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style>
.wrong {
	width: 220px;
	background-color: yellow;
	border-style: solid;
	border-color: red;
	border-width: 1px;
}

.warning {
	color: #F00;
	background-color: #FFF;
}

.ok {
	color: #060;
	background-color: #FFF;
}

.ok {
	
}

.loading1 {
	width: 15em;
	text-align: center;
	background: #FFA826;
	color: #FFF;
}
</style>

<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/ajax.js" type="text/javascript">
	
</script>
<script language="javascript" src="../js/XErr.js" type="text/javascript">
	
</script>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>

<script language="javascript" type="text/javascript">
	var g_order_serial = "";
	var g_incarno = "";

	function init() {
		data_input.style.display = "block";
		data_result.style.display = "none";
		divResult.innerHTML = "";
		//$('txt_incarno').value="";
		//$('txt_order_serial').value = "";
	}

	function upload() {
		try {
			var incarno = $('txt_incarno').value;
			var order_serial = $('txt_order_serial').value;
			var source = $('txt_source').value;
			if (incarno == "") {
				//alert("装车单号必须填写！");
				//$("txt_incarno").focus();
				//return;
			}
			if (order_serial == "") {
				//alert("预约流水号必须填写！");
				//$("txt_order_serial").focus();
				//return;
			}
			if (source == "") {
				alert("导入数据必须填写！");
				$("txt_source").focus();
				return;
			}

			g_incarno = incarno;
			g_order_serial = order_serial;

			data_input.style.display = "none";
			data_result.style.display = "block";

			setLoading(true);
			var parms = new Array();
			parms.push("operation=validate");
			parms.push("incarno=" + incarno);
			parms.push("order_serial=" + order_serial);
			var url = "../DaemonNetInCar?" + parms.join('&');

			var arr_name = new Array('seqno', 'goodsid', 'spec', 'qty','packageid');
			var uploader = new AjaxUploader(url, arr_name);
			uploader.text = source;

			//对帐数据限制在 5000 条内
			uploader.rows_limit = 5000;
			uploader.island4req = island4req;
			uploader.reader.read = analyse_response;
			uploader.call();
		} catch (e) {
			alert(e);
			setLoading(false);
		}
	}

	function analyse_response(text) {
		setLoading(false);
		island4result.loadXML(text);
		var elm_err = island4result.XMLDocument.selectSingleNode("/xdoc/xout/xerr");
		var xerr = parseXErr(elm_err);
		if (xerr.code != "0") {
			set_illegalinfo();
			btn_return.value = "返回";
		} else {
			set_llegalinfo();
		}
		divResult.innerHTML = island4result.transformNode(format4list.documentElement);
	}

	function getBookno() {
		return txt_bookno.value;
	}

	//生成装车单信息
	function sendResult() {
		try {
			setLoading(true);
			btn_create.disabled = true;
			var parms = new Array();
			parms.push("operation=create_netorderzc");
			parms.push("incarno=" + g_incarno);
			parms.push("order_serial=" + g_order_serial);
			var arr_name = new Array('seqno', 'goodsid', 'spec', 'qty',
					'packageid');
			var url = "../DaemonNetInCar?" + parms.join('&');

			var uploader = new AjaxUploader(url, arr_name);
			uploader.text = txt_source.value;
			uploader.rows_limit = 5000;
			uploader.island4req = island4req;
			uploader.reader.read = analyseReply;
			uploader.call();

		} catch (e) {
			alert(e);
			setLoading(false);
		}
	}

	function analyseReply(text) {
		setLoading(false);
		island4result.loadXML(text);
		var elm_err = island4result.XMLDocument.selectSingleNode("/xdoc/xerr");
		var elm_sheetid = island4result.XMLDocument
				.selectSingleNode("/xdoc/xout");
		var xerr = parseXErr(elm_err);
		if (xerr.code == "0") {
			div_info.innerHTML = '已成功生成装车单：' + elm_sheetid.text;
			div_info.style.display = "block";
			txt_source.value = '';
			btn_return.value = "录入下一单";
			$('txt_incarno').value="";
			$('txt_order_serial').value = "";
		} else {
			alert("生成单据失败：" + xerr.note);
			div_info.style.display = "none";
		}
	}

	function set_illegalinfo() {
		div_info.style.display = "none";
		btn_create.disabled = true;
	}

	function set_llegalinfo() {
		div_info.style.display = "none";
		btn_create.disabled = false;
	}

	//得到流水号
	function getorderserial() {
		return txt_order_serial.value;
	}

	function check_netorder() {
		var orderserial = getorderserial();
		if (orderserial != "") {
			//btn_upload.disabled=( txt_source.value!="" )?false:true;
		}
	}
	//检查此预约流水号是否存在并有效
	function load_order_serial(id) {
		return;
		setLoading(true);
		var parms = new Array();
		parms.push("operation=netcheckorderserial");
		parms.push("order_serial=" + id);
		var url = "../DaemonNetInCar?" + parms.join('&');
		var table = new AW.XML.Table;
		table.setURL(url);
		table.request();
		table.response = function(text) {
			table.setXML(text);
			setLoading(false);
			var xcode = table.getErrCode();
			if (xcode != '0') {//处理xml中的错误消息
				alert(xcode + table.getErrNote());
			} else {
				var order_serial = table
						.getXMLText("/xdoc/xout/netcheckorderserial/row/order_serial");
				var flag = table
						.getXMLText("/xdoc/xout/netcheckorderserial/row/flag");
				if (order_serial == "") {
					alert("此预约流水号不存在，请重新输入！！");
					$("txt_order_serial").value = "";
					$("txt_order_serial").focus();
					return;
				} else if (flag == "N") {
					alert("此预约流水号已取消，请重新输入！！");
					$("txt_order_serial").value = "";
					$("txt_order_serial").focus();
					return;
				}
				//检查此预约流水号是否已经装车
				/*
				setLoading( true );
				var parms_zc = new Array();
				parms_zc.push("operation=netcheckorderserialzc");
				parms_zc.push("order_serial="+id);
				var url  = "../DaemonNetInCar?"+parms_zc.join('&');
				var table_zc = new AW.XML.Table;
				table_zc.setURL(url);
				table_zc.request();
				table_zc.response = function(text){
					table.setXML(text);
					setLoading( false );
					var xcode = table.getErrCode();
					if( xcode != '0' ){//处理xml中的错误消息
						alert( xcode+table.getErrNote());
					}else{
						var order_serial = table.getXMLText("/xdoc/xout/netcheckorderserialzc/row/order_serial");
						var incarno = table.getXMLText("/xdoc/xout/netcheckorderserialzc/row/incarno");
						if(incarno.length>0){
							alert("此预约流水号已装车，装车单号为"+incarno+"。");
							$("txt_order_serial").value="";
							$("txt_order_serial").focus();
							return;
						}
					}
				};
				 */
			}
		};
	}
</script>
<xml id="island4req"></xml>
<xml id="island4orderserial" />
<xml id="island4result" />
<xml id="format4list" src="upload_netincar.xsl" />
</head>

<body onLoad="init()">

	<br />
	<br />
	<div id="data_input">
		装车单号： &nbsp;&nbsp;<input type="text" id="txt_incarno"
			value="无需填写，提交后生成" readonly="readonly" /> <br /> <br /> <span>请输入预约流水号：</span><input
			type="text" id="txt_order_serial" value=""
			onchange="load_order_serial(this.value)" /> <br /> <br /> <input
			type="button" id="btn_upload" value="导入数据" onClick="upload()" />&nbsp;&nbsp;<input
			type="button" value="清空数据" onclick="javascript:txt_source.value=''" />&nbsp;&nbsp;<span>请在下面复制粘贴excel内容，并检查数据:</span>
		<br />
		<div id="divExcel">
			<br /> &nbsp;&nbsp;&nbsp;&nbsp;装车顺序&nbsp;&nbsp;&nbsp;&nbsp;|
			&nbsp;&nbsp;&nbsp;&nbsp;商品条码&nbsp;&nbsp;&nbsp;&nbsp;|
			&nbsp;&nbsp;&nbsp;&nbsp;商品规格&nbsp;&nbsp;&nbsp;&nbsp;|
			&nbsp;&nbsp;&nbsp;&nbsp;商品数量&nbsp;&nbsp;&nbsp;&nbsp;|
			&nbsp;&nbsp;&nbsp;&nbsp;外箱码&nbsp;&nbsp;&nbsp;&nbsp;| <br />
			<textarea rows="10" cols="120" id="txt_source" name="txt_source"></textarea>
		</div>
		<br /> 注意： 1、每次上传最多5000行数据。
	</div>
	<br />
	<div id="data_result">
		<input type="button" id="btn_create" value="生成单据"
			onclick="sendResult()" disabled="disabled" /> <input type="button"
			id="btn_return" value="返回" onClick="init()" /> <br /> <br />
		<div id="div_info" class=wrong style="display: none;"></div>
		<br />
		<div id="divResult"></div>
	</div>
</body>
</html>
