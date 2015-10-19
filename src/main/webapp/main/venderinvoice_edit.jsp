<%@page import="com.royalstone.myshop.component.InvoiceType"%>
<%@page contentType="text/html;charset=UTF-8" session="false"%>
<%@include file="../include/common.jsp"%>
<%@include file="../include/token.jsp"%>
<%@include file="../include/permission.jsp"%>
<%
    //用户的查询权限.
    moduleid = 3020305;
	token.checkPermission(moduleid,Permission.READ);
	
	if(!token.isVender){
		throw new PermissionException( "该模块为供应商专用！" );
	}
	
	//发票种类
	InvoiceType sel_invoicetype = new InvoiceType(token);
	sel_invoicetype.setAttribute( "id", "invoicetypeid" );
	sel_invoicetype.setAttribute("name","invoicetypeid");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>发票录入</title>
<!-- ActiveWidgets stylesheet and scripts -->
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>

<style type="text/css">
.aw-grid-control {
	height: 200px;
	width: 100%;
	border: none;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

#myGrid {
	background-color: #F9F8F4
}

#myGrid .aw-column-0 {
	text-align: center;
	width: 45px;
	color: #060;
	cursor: hand;
}

#myGrid .aw-column-1 {
	text-align: center;
	width: 45px;
	color: #f00;
	cursor: hand;
}

#myGrid .aw-column-2 {
	text-align: center;
}

#myGrid .aw-column-3 {
	text-align: center;
}

#myGrid .aw-column-4 {
	text-align: center;
}

#myGrid .aw-column-5 {
	text-align: right;
}

#myGrid .aw-column-6 {
	text-align: right;
}

#myGrid .aw-column-7 {
	text-align: right;
}

#myGrid .aw-column-8 {
	text-align: right;
}

#myGrid .aw-column-9 {
	text-align: center;
	width: 138px;
}

#myGrid .aw-column-10 {
	display: none;
}

.titlewarning {
	background-color: #FFFFCC;
	color: #f00;
	border: 1px solid #FFE8E8;
}

.warning {
	background-color: #FFFFCC;
	color: #f00;
	border: 2px solid #f00;
}

.ok {
	background-color: #EEFFF3;
	color: #006600;
}

.tablebg {
	background-color: #ECF8FF;
}

.tablebg th {
	border: 2px solid;
	border-color: #f2f1e2 #e2decd #e2decd #ebeadb;
	background: #ebeadb;
	padding: 0px 6px 2px 0px;
	background-image: url("../AW/runtime/styles/xp/tabs.png");
}

.tablebg td {
	background-color: #fff;
}

div.box {
	border-style: solid;
	border-color: black;
	border-width: 1;
	width: 740;
}

div.box div {
	margin: 5px;
}

label {
	font-size: 12px;
	font-weight: bold;
	color: navy;
}

#paymenthead {
	border: none;
	background-color: #ddd;
}

#paymenthead td {
	background-color: #F9F8F4;
}

.red {
	color: #FF3300;
	font-weight: bold;
}

.green {
	color: green;
	font-weight: bold;
}

.maintitle {
	font-weight: bold;
	color: #0066CC;
}

.hilite {
	color: #333;
}

.blue {
	color: #0000FF;
}

.note {
	color: #555;
}

.button_saveok {
	background-color: #E1FFD2;
}

.button_saveno {
	background-color: #FFC;
}
</style>

<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/ReportGrid.js"> </script>
<script language="javascript" src="../js/MainSheet.js"></script>
<script language="javascript" src="../js/Date.js" type="text/javascript"> </script>
<script language="javascript" src="../js/common.js"> </script>

<script language="javascript" type="text/javascript">
	var uri = location.href;
	var sheetid = uri.getQuery("sheetid");
	var refsheetid = uri.getQuery("refsheetid");
	var cmid = uri.getQuery("cmid");
	var service = "VenderInvoice";
	var grid_height = 200;
	var invoicelength=8;
	var g_invoicecode ="";
	var g_invoicetypeid = "";
	var g_j_taxableamt =0;	//建议开票金额
	var g_j_tax =0;	//建议开票税额
	var g_bleamt_sum =0;	//总计开票金额
	var g_tax_sum =0;	//总计开票税额
	var current_operation = "ADD";

	var _grid = new AW.UI.Grid;
	var _table = null;

	
	window.onload = function(){
		getInvoiceHead();
	};

	function getInvoiceHead(){
		var params = new Array();
		params.push( "sheetid="+sheetid );
		params.push( "operation=getHead" );
		params.push("service=VenderInvoice");
		var url = "../DaemonMain?" + params.join( "&" );
		_table = new AW.XML.Table;
		_table.setURL(url);
		_table.setTable("xdoc/xout/rowset");
		_table.setRows("row");
		_table.request();
		_table.response = function(text){
			setLoading(false);
			//alert(text.xml);
			_table.setXML(text);//读取服务器返回的xml信息
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( _table.getErrNote() );
				return;
			}
			var flag = _table.getXMLText("/xdoc/xout/rowset/row/flag");
			var payflag = _table.getXMLText("/xdoc/xout/rowset/row/payflag");
			if(flag!=0){
				alert("该单已提交或完结，不允许编辑。");
				window.close();
				return;
			}
			if(payflag!=2){
				alert("结算单非制单审核状态，不允许编辑。");
				window.close();
				return;
			}
			showContact();
		};
		setLoading(true, "正在检查发票信息");
	}

	function showContact(){
		var url = "../DaemonVenderAdmin?focus=venderdiy&operation=list";
		var table = new AW.XML.Table;
		table.setURL(url);
		table.setTable("/xdoc/xout/rowset");
		table.setRows("row");
		table.request();
		table.response = function(text){
			setLoading(false);
			//alert(text.xml);
			table.setXML(text);//读取服务器返回的xml信息
			if( table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( table.getErrNote() );
				//return;
			}
			$("txt_contact").value = table.getXMLText("/xdoc/xout/rowset/row/contactperson");
			$("txt_contacttel").value = table.getXMLText("/xdoc/xout/rowset/row/mobileno");

			var contact = $("txt_contact").value;
	        var mobile = $("txt_contacttel").value;
	        if(contact=='' || mobile==''){
				alert("供应商信息维护模块资料为空，请填写后再录入发票信息！");
				$("button_add").disabled = true;
				$("button_save").disabled = true;
				var url = "../information/vender_information.jsp";
				window.location.href = url ;
	        }
	        showPaymentHead();
		};
		setLoading(true, "正在加载联系人信息");
	}
	
	//加载结算单表头信息
	function showPaymentHead(){
		var params = new Array();
		params.push( "sheetid="+refsheetid );
		params.push( "operation=getHead" );
		params.push("service=Paymentsheet");
		var url = "../DaemonMain?" + params.join( "&" );

		setLoading(true,'正在查询结算单……');
		_table 	   = new AW.XML.Table;
		_table.setURL(url);
		_table.setTable("xdoc/xout/head");
		_table.setRows("row");
		_table.request();
		_table.response = function(text){
			setLoading(false);
			//alert(text.xml);
			_table.setXML(text);//读取服务器返回的xml信息
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( _table.getErrNote() );
				return;
			}
			var bankaccount = _table.getXMLText("/xdoc/xout/head/row/bankaccount");
			if(bankaccount==''){
				alert("该结算主体未维护贵司银行资料，我司将无法支付货款，请与采购联系！\r\n补充完整银行信息后，方可录入发票！");
				window.close();
				return;
			}
			var xsl="../xsl/paymentsheet_head.xsl";
			$("paymentheadinfo").innerHTML = _table.transformNode(xsl);
			//计算建议金额
			setAdviceAmt();
			//显示发票详细
			showInvoiceItem();
		};
	}
	//设置建议开票金额
	function setAdviceAmt(){
		var taxableamt17 = _table.getXMLText("/xdoc/xout/head/row/invtotalamt17");
		var taxableamt13 = _table.getXMLText("/xdoc/xout/head/row/invtotalamt13");
		var taxableamt0  = _table.getXMLText("/xdoc/xout/head/row/invtotalamt0");
		var tax17 = _table.getXMLText("/xdoc/xout/head/row/invtotaltaxamt17");
		var tax13 = _table.getXMLText("/xdoc/xout/head/row/invtotaltaxamt13");
		g_j_taxableamt = Number(taxableamt17) + Number(taxableamt13) + Number(taxableamt0);
		g_j_tax = Number(tax17) + Number(tax13);
		$("j_taxable_sum").innerText = financialNum.dataToText(g_j_taxableamt);
		$("j_tax_sum").innerText = financialNum.dataToText(g_j_tax);
	}
	
	//显示发票详细
	function showInvoiceItem(){
		var params = new Array();
		params.push( "sheetid="+sheetid );
		params.push( "operation=getItem" );
		params.push("service=VenderInvoice");
		var url = "../DaemonMain?" + params.join( "&" );
		_table = new AW.XML.Table;
		_table.setURL(url);
		_table.setTable("xdoc/xout/rowset");
		_table.setRows("row");
		_table.request();
		_table.response = function(text){
			setLoading(false);
			//alert(text.xml);
			_table.setXML(text);//读取服务器返回的xml信息
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( _table.getErrNote() );
				return;
			}
			//加载表格
			$("invoiceItem").innerHTML = getInvoiceGrid();
			accountTax();
		};
		setLoading(true, "正在装载发票信息");
	}
	//设置发票grid
	function getInvoiceGrid(){
		//取得elm_body中的行数
		var rows = _table.getCount();
		if(rows == 0){
			return "<span class=\"red\">还没有录入发票，请添加.</span>";
		}
		//table
		var cols = ["edit","delete","invoiceno","invoicecode","invoicedate","taxrate","taxamt","taxableamt","amttax","goodsdesc","seqno","invoicetypename","invoicetypeid"];
		_table.setColumns(cols);
		//grid
		_grid = new AW.UI.Grid;
		_grid.setId("myGrid");
		_grid.setColumnCount(cols.length);
		_grid.setHeaderText([" "," ","发票号","发票类型","发票日期","税率","税额","价额","价税合计","发票说明","发票流水号","发票种类","发票种类编码"]);
		_grid.setRowCount(rows);
		_grid.setSelectorVisible(true);
		_grid.setSelectorWidth(30);
		_grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
		//_grid.setSelectionMode("single-row");
		var str = new AW.Formats.String;	
		var number = new AW.Formats.Number;
		number.setTextFormat("#,###.00");
		var number2 = new AW.Formats.Number;
		_grid.setCellFormat([str,str,number2,number2,str,str,number,number,number,str,str,str]);
		_grid.setCellModel(_table);
		_grid.setCellText("修改",0);
		_grid.setCellText("删除",1);
		_grid.onCellClicked = clickFun;
		return _grid.toString();
	}
	function clickFun(event, column, row){
		var seqno = _grid.getCellValue(10,row);
		
		// 点击第1列, 表示要作修改.
		if( column == 0 ){
			initEditForm(row);
		}else if( column == 1 ){
		//删除
		if(confirm("您确认删除么?"))
			delInvoice(seqno);
			formReset();
		}
	}
	//计算显示录入发票差额
	function accountTax(){
		var elm_body = _table.getXMLNode( "/xdoc/xout/rowset" );
		var tax13 = 0;		//13税合计
		var tax17 = 0;		//17税合计
		var able_sum =0;	//总的价税合计
		if(elm_body!=null){
			var nodes = _table.getXMLNodes("/xdoc/xout/rowset/row");
			for ( var i = 0; i < nodes.length; i++) {
				var taxrate = Number(_table.getXMLText("taxrate",nodes[i]));
				var tax = Number(_table.getXMLText("taxamt",nodes[i]));
				var able= Number(_table.getXMLText("taxableamt",nodes[i]));
				able_sum += (able+tax);		//已输入总的价税合计
				if( 17 == taxrate ){
					tax17 += tax;
				}else{
					tax13 += tax;
				}
			}
		}
		g_bleamt_sum = able_sum;
		g_tax_sum = tax17+tax13;
		var margin_able = g_j_taxableamt - able_sum;
		$("taxable_sum").innerText = financialNum.dataToText(g_bleamt_sum);
		$("tax_sum").innerText = financialNum.dataToText(g_tax_sum);
		if( margin_able >0 ){
			setText("taxable_margin", "您还需录发票金额："+financialNum.dataToText(margin_able));
			setClass("taxable_margin", "red");
			setClass("button_save", "button_saveno");
		}else{
			setText("taxable_margin","点击输入完毕按钮,发票信息将提交到商业");
			setClass("taxable_margin", "green");
			setClass("button_save", "button_saveok");
		}
	}

	function initEditForm(row){
		$("invoiceno").value = cookInvoiceno(_grid.getCellValue(2,row));
		$("invoiceno").setAttribute("readOnly","readOnly");
		$("invoicecode").value = _grid.getCellValue(3,row);
		$("invoicecode").setAttribute("readOnly","readOnly");
		$("invoicetypeid").value = _grid.getCellValue(12,row);
		$("invoicedate").value = _grid.getCellValue(4,row);
		$("taxrate").value =  _grid.getCellValue(5,row);
		$("taxamt").value = _grid.getCellValue(6,row);
		$("taxableamt").value = _grid.getCellValue(7,row);
		$("goodsdesc").value = _grid.getCellValue(9,row);
		$("seqno").value = _grid.getCellValue(10,row);
		
		current_operation = "REPLACE";
		$("button_reset").value = "取消修改";
		$("button_ok").value = "保存修改";
		$("button_reset").onclick = function(){
			$("button_reset").value = "清空";
			$("button_ok").value = "保存";
			formReset();
		};
	}

	//删除一行数据
	function delInvoice(seqno){
		setLoading(true,"删除中");
		var params = new Array();
		params.push( "sheetid="+sheetid );
		params.push( "seqno="+seqno );
		params.push( "operation=delItem" );
		params.push("service=VenderInvoice");
		var url = "../DaemonMain?" + params.join( "&" );
		_table = new AW.XML.Table;
		_table.setURL(url);
		_table.setTable("xdoc/xout/rowset");
		_table.setRows("row");
		_table.request();
		_table.response = function(text){
			setLoading(false);
			//alert(text.xml);
			_table.setXML(text);//读取服务器返回的xml信息
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( _table.getErrNote() );
				return;
			}
			$("invoiceItem").innerHTML = getInvoiceGrid();
			accountTax();
		};
	}

	function save_invoice_item()
	{
		if( !checkSubmit() ) return;
	
		if( current_operation == "ADD" ) add_invoice_item(); 
		else if( current_operation == "REPLACE" ) replace_invoice_item();
	}

	function add_invoice_item()
	{
		setLoading(true,"新增中");
		var params = new Array();
		params.push( "invoiceno="+ $("invoiceno").value );
		params.push( "invoicecode="+ $("invoicecode").value );
		params.push( "invoicetypeid="+ $("invoicetypeid").value );
		params.push( "invoicedate="+ $("invoicedate").value );
		params.push( "taxrate="+ $("taxrate").value );
		params.push( "taxamt="+ $("taxamt").value );
		params.push( "taxableamt="+ $("taxableamt").value );
		params.push( "goodsdesc="+ encodeURI( $("goodsdesc").value ) );
		params.push( "sheetid="+sheetid );
		params.push( "operation=addItem" );
		params.push( "service=VenderInvoice" );
		var url = "../DaemonMain?" + params.join( "&" );
		_table = new AW.XML.Table;
		_table.setURL(url);
		_table.setTable("xdoc/xout/rowset");
		_table.setRows("row");
		_table.request();
		_table.response = function(text){
			setLoading(false);
			//alert(text.xml);
			_table.setXML(text);//读取服务器返回的xml信息
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( _table.getErrNote() );
				return;
			}
			$("invoiceItem").innerHTML = getInvoiceGrid();
			accountTax();
			formReset();
		};
	}

	function replace_invoice_item () 
	{
		setLoading(true,"更新中");
		var params = new Array();
		params.push( "seqno="+$("seqno").value );
		params.push( "invoiceno="+ $("invoiceno").value );
		params.push( "invoicecode="+ $("invoicecode").value );
		params.push( "invoicetypeid="+ $("invoicetypeid").value );
		params.push( "invoicedate="+ $("invoicedate").value );
		params.push( "taxrate="+ $("taxrate").value );
		params.push( "taxamt="+ $("taxamt").value );
		params.push( "taxableamt="+ $("taxableamt").value );
		params.push( "goodsdesc="+ encodeURI( $("goodsdesc").value ) );
		params.push( "sheetid="+sheetid );
		params.push( "operation=updateItem" );
		params.push( "service=VenderInvoice" );
		var url = "../DaemonMain?" + params.join( "&" );
		_table = new AW.XML.Table;
		_table.setURL(url);
		_table.setTable("xdoc/xout/rowset");
		_table.setRows("row");
		_table.request();
		_table.response = function(text){
			setLoading(false);
			//alert(text.xml);
			_table.setXML(text);//读取服务器返回的xml信息
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( _table.getErrNote() );
				return;
			}
			$("invoiceItem").innerHTML = getInvoiceGrid();
			accountTax();
			formReset();
		};
	}
	
	//提交前的空值验证
	function checkSubmit(){
		if(
		$("invoiceno").value == "" ||
		$("invoicecode").value == "" ||
		$("invoicedate").value == "" ||
		$("taxrate").value == "" ||
		$("taxamt").value == "" ||
		$("taxableamt").value == "" ||
		$("invoicetypeid").value == ""
		){
			alert("请填写完整信息！");
			return false;
		}
		return true;
	}
	
	//表单重置
	function formReset(){
		$("invoiceno").value = "";
		$("invoiceno").removeAttribute("readOnly");
		$("invoiceno").focus();
		$("invoicecode").removeAttribute("readOnly");
		$("invoicedate").value = "";
		$("taxamt").value = "";
		$("taxableamt").value = "";
		$("taxamtvalue").value = "";
		$("goodsdesc").value = "";
		
		current_operation = "ADD";
		$( "button_ok" ) .value = "保存";
		$( "button_reset" ) .value = "清空";
	}


	function confirm_invoice()
	{
		//如果没有填写发票信息不能提交
		var rows = Number(_table.getCount());
		if ( rows == 0 ){
			alert( "您还没有填写发票信息，不能提交！" );
			return false;
		}

		var contact = $("txt_contact").value;
		var contacttel = $("txt_contacttel").value;
		if(contacttel=="") {
			alert("联系电话必须填写！");
			return;
		}
		
		//如果建议开票税额大于0
		if(g_j_tax>0){
			var diff = Number(g_tax_sum - g_j_tax);
			if (diff==0){
				note = "当您点击确认后发票信息将提交给对帐人员。\r\n数据一但提交不能更改，请仔细核对！\r\n如果您确认数据无误，请点击确定提交！";
			}else{
				if( diff < 0 ){
					note = "系统建议开票税额为："+financialNum.dataToText(g_j_tax)+"\r\n您录入的发票税额为："+financialNum.dataToText(g_tax_sum)+"\r\n低于建议税额:"+financialNum.dataToText(-diff)+"元。  \r\n数据一但提交不能更改，请仔细核对！\r\n确定要提交么？";
				}else{
					note = "系统建议开票税额为："+financialNum.dataToText(g_j_tax)+"\r\n您录入的发票税额为："+financialNum.dataToText(g_tax_sum)+"\r\n高于建议税额:"+financialNum.dataToText(diff)+"元。  \r\n数据一但提交不能更改，请仔细核对！\r\n确定要提交么？";
				}
			}
		}else{
			var diff = Number(g_bleamt_sum -g_j_taxableamt );
			if (diff==0){
				note = "当您点击确认后发票信息将提交给对帐人员。\r\n数据一但提交不能更改，请仔细核对！\r\n如果您确认数据无误，请点击确定提交！";
			}else{
				if( diff < 0 ){
					note = "系统建议开票金额为："+financialNum.dataToText(g_j_taxableamt)+"\r\n您录入的发票金额为："+financialNum.dataToText(g_bleamt_sum)+"\r\n低于建议金额:"+financialNum.dataToText(-diff)+"元。  \r\n数据一但提交不能更改，请仔细核对！\r\n确定要提交么？";
				}else{
					note = "系统建议开票金额为："+financialNum.dataToText(g_j_taxableamt)+"\r\n您录入的发票金额为："+financialNum.dataToText(g_bleamt_sum)+"\r\n高于建议金额:"+financialNum.dataToText(diff)+"元。  \r\n数据一但提交不能更改，请仔细核对！\r\n确定要提交么？";
				}
			}
		}
		if(!confirm(note)) return false;
		setLoading(true,"更新中");
		var params = new Array();
		params.push( "operation=confirm" );
		params.push( "contacttel="+encodeURI(contacttel) );
		params.push( "contact="+encodeURI(contact) );
		params.push( "sheetid=" +sheetid  );
		params.push( "service=VenderInvoice" );
		var url = "../DaemonMain?" + params.join( "&" );
		_table = new AW.XML.Table;
		_table.setURL(url);
		_table.setTable("xdoc/xout/rowset");
		_table.setRows("row");
		_table.request();
		_table.response = function(text){
			setLoading(false);
			//alert(text.xml);
			_table.setXML(text);//读取服务器返回的xml信息
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( "提交失败："+_table.getErrNote() );
				return;
			}
			alert( "发票信息已经提交!请打印发票信息并与实物发票一并提交！" );
			//打印
			var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
			",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
	 		a = window.open( "print.jsp?cmid="+cmid+"&service="+service+"&sheetid="+sheetid, sheetid, attributeOfNewWnd );
	 		a.focus();
		};
	}
	
	//设置对象text
	function setText(idName,str){
		$(idName).innerText = str;
	}
	//设置对象class
	function setClass(idName, cName){
		$(idName).className = cName;
	}
	//对填入的发票号进行记忆
	function remberType(){
		var newType = $("invoicecode").value;
		if(Number(newType) != Number(g_invoicecode)){
			g_invoicecode = newType;
		}
		g_invoicetypeid = $("invoicetypeid").value;
	}

	function setInvoiceno(ctrl){
		var str=ctrl.value;
		ctrl.value=cookInvoiceno(str);
	}
	
	function cookInvoiceno(str){
		str = new String(str);
		var num=invoicelength-str.length;
		var add="";
		if(num<0){
			alert("输入长度超过"+invoicelength+"位");
			str="";
		}else if(num==0){
		}else{
			if(str != ""){
				for(var i=0;i<num;i++)
				{
					add+="0";
				}
			}
			str=add+str;
		}
		return str;
	}
	
	function checkDigitalCode( ctrl )
	{
		var str = ctrl.value;
		if( ! isDigitalCode( str ) ) {
			alert( "输入非法!" );
			ctrl.value="";
			ctrl.focus();
			return false;
		}
		return true;
	}
	function isDigitalCode( str )
	{
		var i = str.search( /[^0-9]/g );
		if( i == -1 ) return true;
		else return false;
	}
	function set_tax_value()
	{
		
		var taxrate = $("taxrate").value; //税率
		var taxamt 	= $("taxamt").value;  //税额
		var	taxableamt 	= $("taxableamt").value; //价额
		var taxamtvalue = $("taxamtvalue").value; //价税合计
		
		if ( taxrate == "" ){
			alert("没有选择税率");
		}else if( taxrate == "0" ){// 没有税额
			$("taxamt").readOnly = true;
			$("taxamt").disabled = true;
			$("taxableamt").readOnly = true;
			$("taxableamt").disabled = true;
			$("taxamtvalue").readOnly = false;
			$("taxamtvalue").disabled = false;
			$("taxamt").value = "0";
			$("taxableamt").value = taxamtvalue;
		}else {
			$("taxamt").readOnly = false;
			$("taxamt").disabled = false;
			$("taxableamt").readOnly = false;
			$("taxableamt").disabled = false;
			$("taxamtvalue").readOnly = true;
			$("taxamtvalue").disabled = true;
			$("taxableamt").value  = financialNum.dataToText(financialNum.textToValue(taxamt)*100/financialNum.textToValue(taxrate));
			$("taxamtvalue").value = financialNum.dataToText(financialNum.textToValue(taxamt)+financialNum.textToValue($("taxableamt").value));
		}
		if ($("invoicecode").value == ""){
			$("invoicecode").value = g_invoicecode;
			$("invoicetypeid").value=g_invoicetypeid;
		}
	}

	function set_value(){
		var taxrate = $("taxrate").value; //税率
		var taxamt 	= $("taxamt").value;  //税额
		var	taxableamt 	= $("taxableamt").value; //价额
		var taxamtvalue = $("taxamtvalue").value; //价税合计

		if ( taxrate == "" ){
			alert("没有选择税率");
		}else if( taxrate == "0" ){
			
		}else{
			$("taxamtvalue").value = financialNum.dataToText(financialNum.textToValue(taxamt)+financialNum.textToValue(taxableamt));
		}
	}

	/*
	 *数据批量导入 
	 * */
	 function validate(){
		if($("txt_source").value == ""){
			alert( "没有数据,请录入" );
			return;
		}
		var arr_name =  new Array ( 'invoiceno', 'invoicecode','invoicedate','taxrate','taxamt','taxablevalue','invoicetypeid' );
		var _array = text2Array(arr_name);
		if(_array.length==0) return;
		var x = arrayToXML(_array,arr_name);
		
		$("divExcel").style.display = "none";
		
		
		var params = new Array();
		params.push( "sheetid="+sheetid );
		params.push( "operation=batchCheckItem" );
		params.push( "service=VenderInvoice" );
		var url = "../DaemonMain?" + params.join( "&" );
		
		_table = new AW.XML.Table;
		_table.setRequestMethod('POST');
		_table.setRequestData(_table.getXMLContent(x));
		_table.setURL( url );
		_table.request();
		_table.response = function(text){
			//alert(text.xml);
			setLoading(false);
			_table.setXML( text );
			var htmlOut = _table.transformNode("../xsl/venderinvoice_validate.xsl");
			var code = _table.getXMLText("xdoc/xout/rowset/result");
			if( code != 'OK' ){	//处理xml中的错误消息
				htmlOut += "<br><input type='button' value='重新提交数据' onclick=\"redo()\"/>";
				$("divResult").innerHTML ="";
	            $("divResult").style.display = 'block';
	            $("divExcel").style.display = 'none';
			}else{
				htmlOut += "<br><input type='button' value='点击这里保存' onclick=\"saveInvoice()\" id=\"btn_save\"/>";
	            $("divResult").style.display = 'block';
	            $("divExcel").style.display = 'none';
			}
			$("divResult").innerHTML = "<br/>"+htmlOut;
			window.scrollBy(0,window.screen.availHeight);
		};
		setLoading(true);
	}
	
	//提交导入数据
	function saveInvoice(){
		var arr_name =  new Array ( 'invoiceno', 'invoicecode','invoicedate','taxrate','taxamt','taxablevalue','invoicetypeid' );
		var _array = text2Array(arr_name);
		if(_array.length==0) return;
		var x = arrayToXML(_array,arr_name);
		
		$("btn_save").disabled = true;
		
		
		var params = new Array();
		params.push( "sheetid="+sheetid );
		params.push( "operation=batchAddItem" );
		params.push( "service=VenderInvoice" );
		var url = "../DaemonMain?" + params.join( "&" );
		_table = new AW.XML.Table;
		_table.setRequestMethod('POST');
		_table.setRequestData(_table.getXMLContent(x));
		_table.setURL( url );
		_table.request();
		_table.response = function(text){
			//alert(text.xml);
			$("btn_save").disabled = false;
			setLoading(false);
			_table.setXML( text );
			if( _table.getErrCode() != 0 ){	//处理xml中的错误消息
				alert( "提交失败："+_table.getErrNote() );
				return;
			}
			alert("保存成功");
			redo();
			showInvoiceItem();
			window.scrollBy(0,window.screen.availHeight);
		};
		setLoading(true);
	}

	function text2Array(arr_name){
		var str=$("txt_source").value;
		return _upload_txt_to_matrix(str,arr_name.length,1000);
	}
	
	function redo(){
		$("divResult").innerHTML ="";
		$("divResult").style.display = 'none';
		$("divExcel").style.display = 'block';
		$("txt_source").value='';
	}

	function arrayToXML(_dataArray,columns){
		var table = new AW.XML.Table;
		table.setXML("");
		var doc = table.getXML("");
		var elmSet = doc.createElement("rowset");
		for(var i=0; i<_dataArray.length; i++){
			var elmRow = doc.createElement("row");
			for( var j=0; j<columns.length; j++ ){
				var elmTemp = doc.createElement(columns[j].replace("@",""));
				var text = _dataArray[i][j];
				text = (typeof(text)=='undefined')?"":text;
				elmTemp.appendChild(doc.createTextNode(text));
				elmRow.appendChild(elmTemp);
			}
			elmSet.appendChild(elmRow);
		}
		return elmSet;
	}
</script>
</head>

<body>
	<div id="div_tabs"></div>
	<div id="tag_invoice">
		<div id="paymenthead">
			<div style="width: 100%;" class="tableheader"></div>
			<div id="paymentheadinfo"></div>
		</div>
		<br />
		<div id="invoiceItemList">
			<div style="width: 100%;" class="tableheader">发票详情列表</div>
			<div id="invoiceItem"></div>
			<div style="width: 100%;" class="tableheader">
				建议开票金额:<span id="j_taxable_sum" class="green"></span>&nbsp; 税额:<span
					id="j_tax_sum" class="green"></span>&nbsp;&nbsp;&nbsp;&nbsp; 已开票金额:<span
					id="taxable_sum" class="green"></span>&nbsp; 税额：<span id="tax_sum"
					class="green"></span>&nbsp;&nbsp;&nbsp;&nbsp; 开票金额建议:<span
					id="taxable_margin" class=""></span>
			</div>
		</div>
		<br />
		<div id="invoiceInput">
			<input type="hidden" value="" id="seqno" /> <input type="hidden"
				value="" id="sheetid" />

			<div style="width: 100%;" class="tableheader">
				<span class="maintitle">发票单录入方式一：</span><span class="hilite">单张发票逐一录入</span>
				<font color="red">*</font> 红色星的项目请填写完整
			</div>

			<div style="width: 100%;" class="altbg1">
				<label style="width: 120px;">发票号：</label> <input name="invoiceno"
					type="text" id="invoiceno" title="输入格式只能为整数"
					onkeyup="checkDigitalCode(this)" onblur="setInvoiceno(this)"
					maxlength="8" /> &nbsp;&nbsp;&nbsp;&nbsp; <span class="note"><span
					class="red">*</span> (税票右上角的数字)</span>
			</div>

			<div style="width: 100%;" class="altbg1">
				<label style="width: 120px;">发票类型：</label> <input name="invoicecode"
					type="text" id="invoicecode" title="输入格式只能为整数"
					onkeyup="checkDigitalCode(this)" onblur="remberType(this)"
					maxlength="16" /> &nbsp;&nbsp;&nbsp;&nbsp; <span class="note"><span
					class="red">*</span> (税票左上角的数字)</span>
			</div>

			<div style="width: 100%;" class="altbg1">
				<label style="width: 120px;">发票日期：</label> <input name="invoicedate"
					type="text" id="invoicedate" onblur="checkDate(this)"
					title="简写格式可为：年,月,日.如:05,08,01.如果只输入一个数字，则默认为本年本月当天." />
				&nbsp;&nbsp;&nbsp;&nbsp; <span class="note"><span class="red">*</span>
					(发票上的开票日期)日期格式可以为2012.1.1表示2012年1月1日</span>
			</div>

			<div style="width: 100%;" class="altbg1">
				<label style="width: 120px;">发票种类：</label>
				<%=sel_invoicetype %>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <span class="note"><span
					class="red">*</span> 请根据开票实际情况选择</span>
			</div>

			<div style="width: 100%;" class="altbg1">
				<label style="width: 120px;">税率：</label> <select name="taxrate"
					id="taxrate" onchange="set_tax_value()">
					<option value=17>17.00%</option>
					<option value=14.94>14.94%</option>
					<option value=13>13.00%</option>
					<option value=6>6.00%</option>
					<option value=4>4.00%</option>
					<option value=3>3.00%</option>
					<option value=0>0.00%</option>
				</select>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<span class="note"><span class="red">*</span>
					农副产品请选择14.94税率；收据请选择0税率，其发票类型为55555；其它发票请选择对应税率</span>
			</div>

			<div style="width: 100%;" class="altbg1">
				<label style="width: 120px;">增值税额：</label> <input name="taxamt"
					type="text" id="taxamt" style="text-align: right;"
					onblur="set_tax_value()" /> &nbsp;&nbsp;&nbsp;&nbsp; <span
					class="note"><span class="red">*</span> 非零税率发票请直接填写税额</span>
			</div>

			<div style="width: 100%;" class="altbg1">
				<label style="width: 120px;">价额：</label> <input name="taxableamt"
					type="text" id="taxableamt" style="text-align: right;"
					onblur="set_value()" />
			</div>

			<div style="width: 100%;" class="altbg1">
				<label style="width: 120px;">价税合计：</label> <input name="taxamtvalue"
					type="text" id="taxamtvalue" readonly="readonly"
					style="text-align: right;" onblur="set_tax_value()" />
				&nbsp;&nbsp;&nbsp;&nbsp; <span class="note"><span class="red">*</span>
					零税率发票请直接填写价税合计</span>
			</div>

			<div style="width: 100%;" class="altbg1">
				<label style="width: 120px;">发票说明：</label> <input name="goodsdesc"
					type="text" id="goodsdesc"
					onkeypress="if(event.keyCode == 13){ button_add.click();invoiceno.focus();}"
					size="46" />
			</div>

			<div style="width: 100%;" class="altbg1">
				<input type="button" id="button_add" name="button_ok" value="保存"
					onclick="save_invoice_item()" style="float: left;" />
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="button" name="reset"
					onclick="formReset();" id="button_reset" value="清空"
					onblur="$('invoiceno').focus();" />
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="button"
					name="button_save" id="button_save" value="提交给对帐员"
					onclick="confirm_invoice()" /> <span class="red">请确认下面的联系人和联系电话是准确，再点“提交对帐员”，如果不是准确的，请到“<a
					href="../information/vender_information.jsp">供应商资料维护</a>”模块维护。
				</span>
				<div style="margin-top: 4px;">
					联系人：<input type="text" size="12" id="txt_contact"
						readonly="readonly" maxlength="10"
						style="border: none; border-bottom: 1px solid #000" /> 联系电话：<input
						type="text" size="20" readonly="readonly" id="txt_contacttel"
						style="border: none; border-bottom: 1px solid #000" />
				</div>
			</div>
		</div>
		<br />
		<div id="divExcel">
			<div style="width: 100%;" class="tableheader">
				<span class="maintitle">发票单录入方式二：</span> <span class="hilite">发票批量导入[把Excel中的内容复制到文本区即可]</span>
			</div>
			<div style="width: 100%;" class="altbg1">
				<div id="divExcel">
					<span class="blue">请按以下格式复制粘贴excel内容到下面框内，并点击验证数据:</span>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <span class="note">注意：非0%税率的发票请录入“税额”，“价税合计”，对于0%税率的发票或者收据，“税额”录入0，“价税合计”录入实际值。</span>
					<br /> <span class="note">发票种类是1-5数字，分别为：1 增值税专用发票；2
						增值税普通发票；3 普通商品销售发票；4 服务业发票；5 收据</span> <br />
					发票号&nbsp;&nbsp;&nbsp;&nbsp;|
					&nbsp;&nbsp;&nbsp;&nbsp;发票类型&nbsp;&nbsp;&nbsp;&nbsp;|
					&nbsp;&nbsp;&nbsp;&nbsp;发票日期&nbsp;&nbsp;&nbsp;&nbsp;|
					&nbsp;&nbsp;&nbsp;&nbsp;税率&nbsp;&nbsp;&nbsp;&nbsp;|
					&nbsp;&nbsp;&nbsp;&nbsp;税额&nbsp;&nbsp;&nbsp;&nbsp;|
					&nbsp;&nbsp;&nbsp;&nbsp;价税合计&nbsp;&nbsp;&nbsp;&nbsp;|
					&nbsp;&nbsp;&nbsp;&nbsp;发票种类&nbsp;&nbsp;&nbsp;&nbsp;| <br />
					<textarea rows="10" name="txt_source" id="txt_source"
						style="width: 98%;"></textarea>
					<br /> <br /> <input type="button" value="验证数据"
						onclick="validate()" id="btn_validate" />
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input
						type="button" value="清空数据"
						onclick="txt_source.value='';txt_source.focus()" id="btn_clear" />
					<br />
				</div>
			</div>
		</div>
		<div id="divResult"></div>
	</div>
</body>
</html>