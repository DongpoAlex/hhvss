<%@page contentType="text/html;charset=UTF-8" session="false"%>
<%@include file="../include/common.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title></title>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<style>
BODY {
	BACKGROUND-COLOR: #fff;
}

table {
	background-color: #000;
	width: 100%;
	padding: 0px;
	border: #000 1px solid;
}

td,th {
	background-color: #ffffff;
	font-size: 12px;
}

.center td,th {
	text-align: center;
}

#div_printhelp {
	width: 400px;
	border: 1px green dotted;
	padding: 16px;
	color: white;
	display: none;
	position: absolute;
	top: 0;
	left: 300;
	z-index: 100;
	background-color: blue;
	filter: progid:DXImageTransform.Microsoft.Alpha(style=0, opacity=90,
		finishOpacity=90);
}
</style>

<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" src="../js/MainPrint.js"> </script>
<script>
var p = new Print();
p.load = function(sheetid){
	var url = "../DaemonMain?cmid="+p.cmid+"&operation="+p.operation+"&service="+p.service+"&sheetid=" + sheetid;
	var table4detail = new AW.XML.Table;
	table4detail.setURL( url );
	table4detail.request();
	table4detail.response = function(text){
		setLoading(false);
		table4detail.setXML(text);
		var contracttype = Number(table4detail.getXMLText("xdoc/xout/sheet/head/row/contracttype"));
		var html = "";
		if(contracttype<=2){
			html = "<div id='div_print'><a href='javascript:doPrint()'>[打印本页]</a></div>";
		}else{
			html = "<div id='div_print'><a href='javascript:doPrint()'>[打印本页]</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:showDetail()' >[ 明细打印 ]</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:showAllGroupByCharge()' >[ 联营汇总打印 ]</a></div>";
		}
		if( table4detail.getErrCode() != 0 ){	//处理xml中的错误消息
			html = table4detail.getErrNote() ;
		}else{
			var xsl = table4detail.getXMLNode("/xdoc/xout/sheet").getAttribute("xslprint");
			var title = table4detail.getXMLNode("/xdoc/xout/sheet").getAttribute("title");
			window.document.title = title;
			html += table4detail.transformNode( xsl );
		}
		if($("div_show")){
			$("div_show").innerHTML = html;
		}else{
			var obj = $new("div");
			obj.innerHTML = html;
			obj.id="div_show";
			document.body.appendChild( obj );
		}
	};
	setLoading(true);
};
window.onload = function(){
	p.init();
};


function showAllGroupByCharge(){
	p.operation = "showGroupBy";
	p.load(p.arr_sheetid[0]);
}

function showDetail(){
	p.operation = "print";
	p.load(p.arr_sheetid[0]);
}

function toPrint()
{
	$("div_printhelp").style.display = "none";
	$("div_print").style.display="none";
	window.print();
}
function doPrint(){
	var hiddenPrintMemo = getCookie("hiddenPrintMemo");
	if(hiddenPrintMemo){
		toPrint();
	}else{
		$("div_printhelp").style.display = "block";
	}
}
function noshow(value){
	setCookie("hiddenPrintMemo",value);
}
</script>

</head>

<body>
	<div id="div_printhelp">
		<div style="font-size: 16px; font-weight: bold">
			为提高结算效率，请在打印前确认如下设置。 <br />注意，对于不符合规定格式的打印将拒收。
		</div>
		<input type="button" onclick="toPrint();" value="已设置完毕，开始【打印】" />
		<p />
		操作步骤如下：
		<p />
		1、打开IE浏览器，如下图进入页面设置区：
		<p />
		<img src="../img/print1.jpg" alt="打印设置"></img>
		<p />
		2、进入页面设置区后，见下图：
		<p />
		在页眉出输入：&w&b页码，&p/&P
		<p />
		在页脚处输入：&u&b&d
		<p />
		<img src="../img/print2.jpg" alt="打印设置"></img>
		<p />
		3、按2中的设置后，点确定。
		<p />
		<input type="button" onclick="toPrint();" value="已设置完毕，开始【打印】" /><br>
		<label for="noshow"><input type="checkbox" id="noshow"
			onclick="noshow(this.checked)">我已按要求设置，不再显示该提示</label>
	</div>
</body>
</html>