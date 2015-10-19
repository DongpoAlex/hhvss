<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid=3020105;
%>

<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	//查询用户的权限.
	token.checkPermission(moduleid,Permission.READ);
	
	String cmid = request.getParameter("cmid");
	if(cmid.equals("0")){
		throw new Exception("菜单没有定义合适的显示方案，该模块暂停访问");
	}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title></title>
<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" src="../js/EditGrid.js"> </script>
<script language="javascript" src="../js/EditSheet.js"> </script>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<!-- grid format -->
<style type="text/css">
.aw-grid-control {
	width: 100%;
	height: 40%;
	background-color: #F9F8F4;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
	hepadding-bottom: 3px;
	padding-top: 3px;
}

.aw-grid-row {
	border-bottom: 1px solid #ccc;
	font-size: 14px;
	height: 22px;
}

.aw-grid-headers {
	font-size: 14px;
}
</style>

<script language="javascript" type="text/javascript">
var sheet = new EditSheet();
var clazz = 'InvoiceInput';
var arr1 = ["0.17","0.13", "0.1","0.06","0.04","0"];
var current_row=0;
window.onload=function(){
	var url="../DaemonCM?operation=cminit&cmid=<%=cmid%>";
	sheet._grid.onCellClicked = function(e, col, row) {
		if(col==2) selTaxValue(arr1,col,row);
		if(col==1) selDateValue(e,col,row);
		var hang = Number(row)+1;
		$("txt_rowno").innerHTML='已选择第'+hang+'行数据　';
		current_row = row;
	};
	sheet._grid.onCellValidated = function(text, col, row){
		if(col==0){
			$('txt_invoiceno').value=text;
			checkNo(text);
		}
	};
	sheet.setCellEditable([0,1,2]);
	sheet.autoExc = search_report;
	sheet.setCount0Msg("<h3>没有需要录入的发票</h3>");
	sheet.initHTML(url);
};
/**
 * 提交查询
 */
function search_report()
{
	if(!check()){return;}
	var url=cookQueryURL();
	sheet.load(url);
}

function downExcel(){
	if(!check()){return;}
	window.location.href = cookDownURL();

}

function check(){
	var rel = autoCheck();
	return rel;
}

function cookParms(){
	var parms = autoParms();
	return parms;
}
function cookQueryURL(){
	var url="../DaemonCM?operation=cmload&class="+clazz+"&cmid=<%=cmid%>&" + cookParms().join( "&" );
	return url;
}

function cookDownURL(){
	return url  = "../DaemonDownloadReport?reportname=cmexcel&class="+clazz+"&cmid=<%=cmid%>&" + cookParms().join( "&" );
}

function comf(){
	var row = sheet._grid.getRowCount();
	if(row==0){alert("当前没有需要提交的发票。");return;}
	
	setLoading(true);
	var reqData = sheet.cookRequestData();
	//alert(reqData)
	var table = new AW.XML.Table;
	table.setURL("../DaemonInvoiceAdm?operation=confirm");
	table.setRequestMethod('POST');
	table.setRequestData(reqData.xml);
	table.request();
	table.response = function (text) {
		setLoading(false);
		table.setXML(text);
		var xcode = table.getXMLText("xdoc/xerr/code");
		if (xcode != 0) {
			alert(xcode + table.getXMLText("xdoc/xerr/note"));
		} else {
			var row = table.getXMLText("xdoc/xout/row");
			var sum = table.getXMLText("xdoc/xout/sum");
			alert("提交成功！\r\n本次共提交 "+row+" 条发票。\r\n总计开票金额："+sum);
			window.location.reload();
		}
		
	};
}

function selTaxValue(arr,col,row){
	if(arr==null) return;
	var cbValueType = new AW.UI.Combo;
	cbValueType.setItemText(arr);
	cbValueType.setItemCount(arr.length);
	sheet._grid.setCellTemplate(cbValueType, col, row);
	sheet._grid.refresh();
	var curvalue = sheet._grid.getCellText(col,row);
	var i;
	for ( i = 0; i < arr.length; i++) {
		if(curvalue==arr[i]){
			break;
		}
	}
	cbValueType.setCurrentItem(i);
	cbValueType.focus();
	cbValueType.onControlDeactivated = function(event){
		sheet._grid.setCellTemplate("",col,row);
		sheet._grid.setCellData(arr[cbValueType.getCurrentItem()],col,row);
		sheet._grid.setCellText(arr[cbValueType.getCurrentItem()],col,row);
		$('txt_tax').value=arr[cbValueType.getCurrentItem()];
	};
}

function selDateValue(e,col,row){
	$('txt_date').focus();
}

function setDate(value){
	sheet._grid.setCellData(value,1,current_row);
}

function setTax(value){
	sheet._grid.setCellData(value,2,current_row);
}

function setNo(value){
	sheet._grid.setCellData(value,0,current_row);
	checkNo(value);
}

function checkNo(value){
	/*
	var table = new AW.XML.Table;
	table.setURL("../DaemonInvoiceAdm?operation=checkno&handno="+value+"&sheettype=1");
	table.request();
	table.response = function (text) {
		setLoading(false);
		table.setXML(text);
		var xcode = table.getXMLText("xdoc/xerr/code");
		if (xcode != 0) {
			alert(table.getXMLText("xdoc/xerr/note"));
		} else {
		}
		
	};
	*/
}
</script>
</head>
<body>
	<div id="divTitle"></div>
	<div id="divBody">
		<div id="divSearch" class="search_main">
			<div class="search_button">
				<input type="button" value="送审" id="btn_comf" onclick="comf()">
				&nbsp;&nbsp;&nbsp;&nbsp;填写完毕后，点击送审按钮，发票信息将提交到财务。注意，没有填写发票信息或填写不完整的行将不发送到财务，您可以后再填写。
				<span id="div_button_search"></span> <span id="div_button_excel"></span>
			</div>
		</div>
		<div id="divEditSheet"></div>
		<div id="divInput">
			<br />
			<div>您可以在表格中直接填写，也可以点一下要填写的行，然后在下面输入框中填写</div>
			<br /> <span id="txt_rowno">没有选择要输入的行</span> 发票号：<input type="text"
				value="" id="txt_invoiceno" onchange="setNo(this.value)"></input>
			开票日期：<input type="text" value="" id="txt_date"
				onfocus="WdatePicker()" onblur="setDate(this.value)"></input> 税率：<select
				id="txt_tax" onchange="setTax(this.value)">
				<option value="0.17">0.17</option>
				<option value="0.13">0.13</option>
				<option value="0.1">0.1</option>
				<option value="0.06">0.06</option>
				<option value="0.04">0.04</option>
				<option value="0">0</option>
			</select>
		</div>
	</div>
	<div id="divFooter"></div>
</body>
</html>