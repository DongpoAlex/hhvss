<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.util.sql.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	String cmid = request.getParameter("cmid");
	String moduleid = request.getParameter("moduleid");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title>CM EDIT</title>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/ReportGrid.js"> </script>
<script language="javascript" src="../js/EditGrid.js"> </script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<style type="text/css">
.aw-grid-control {
	height: 200px;
	width: 100%;
}

.aw-grid-row {
	height: 20px;
	border-bottom: 1px dashed #ccc;
	height: 24px;
}

.aw-grid-cell {
	border-right: 1px solid #eee;
}
</style>

<script language="javascript" type="text/javascript">
var grid = new AW.Grid.Editor;
var arr1 = ["字符型", "数字型", "财务型"];
var arr2 = ["不汇总","汇总"];
var arr3 = null;
window.onload= function(){
	//列名数组
	var columnNames = ["显示顺序","列名","列显示名","数据类型","显示宽","是否汇总","css样式","显示回调函数","备注"];
	var nodeNames = ["@seqno","@field","@name","@vtype","@width","@sum","@css","@render","@note"];
	grid.setId('grid');
	grid.setHeaderText( columnNames );
	grid.setNodeNames( nodeNames );
	grid.setColumnCount(columnNames.length);

	//显示行号
	grid.setSelector();

	//确认中validating (Enter)一般用与数据检测,return true 则不能确认此数据,无法编辑其它数据
	grid.onCellValidating = function(text, col, row){
	};
	grid.onCellValidated = function(text, col, row){
	};

	grid.setCellEditable(true);
	//grid.setCellEditable(false,1);
	
	$("grid4cmdetail").innerHTML = grid.toString();

	//如果cmid为0则根据moduleid 新建cm
	if($('txt_cmid').value==0){
		var moduleid=<%=moduleid%>;
		var cmid=moduleid*1000;
		alert("当前将为模块："+moduleid+"首次定义视图，保存后生效");
		$('txt_cmid').value = cmid;
		$('txt_moduleid').value = moduleid;
		//加载默认sql语句
		getSelSQLMap();
	}else{
		loadCM();
	}
};

function loadCM(){
	setLoading(true);
	var table = new AW.XML.Table;//new 一个table对象
	table.setParameter("operation","getCMInfo");
	table.setParameter("cmid",$F("txt_cmid"));
	table.setURL("../DaemonCM");//设置 url
	table.setAsync(true);//设置异步/同步模式，true为异步（默认）,false为同步
	table.setTable("xdoc/xout/result/colmodel");//设置table数据路径，也就是需显示的xml的xpath。注意该方法一定在table.setXML方法之前。
	table.request();//发送url请求
	table.response = function(text){//设置异步响应方法。当服务器返回信息将调用该方法。当设置为同步模式时跳过该方法。
		setLoading(false);
		table.setXML(text);//读取服务器返回的xml信息
		if( table.getErrCode() != '0' ){//处理xml中的错误消息
			alert( table.getErrNote() );
			return;
		}
		//alert(table.getXMLText("xdoc/xout/report/colmodel/col/@seqno"));
		$("txt_moduleid").innerText = table.getXMLText("xdoc/xout/result/moduleid");
		$("txt_note").innerText = table.getXMLText("xdoc/xout/result/note");
		$("txt_title").value = table.getXMLText("xdoc/xout/result/title");
		$("txt_footer").value = table.getXMLText("xdoc/xout/result/footer");
		$("txt_smid").value = table.getXMLText("xdoc/xout/result/smid");
		$("txt_smidhead").value = table.getXMLText("xdoc/xout/result/smidhead");
		$("txt_smidbody").value = table.getXMLText("xdoc/xout/result/smidbody");
		$("txt_xslview").value = table.getXMLText("xdoc/xout/result/xslview");
		$("txt_xslprint").value = table.getXMLText("xdoc/xout/result/xslprint");
		
		var cmid = table.getXMLText("xdoc/xout/result/cmid");
		if(cmid != $('txt_cmid').value){
			alert('注意：当前输入的模型号尚未定义，将读取默认模型'+cmid+'。保存操作时将会新增该模型。');
		}
		//初始化明细编辑grid
		table.setRows("col");
		grid.setRowCount(table.getCount());
		table.setColumns(grid.getNodeNames());
		grid.setCellModel(table);

		formatText(3,arr1);
		formatText(5,arr2);
		
		grid.refresh();
		getSelSQLMap();
	};
}

//根据模块ID获取SQL
function getSelSQLMap(){
	setLoading(true,'加载SQL语句');
	var moduleid=$("txt_moduleid").value;
	var table = new AW.XML.Table;//new 一个table对象
	table.setParameter("operation","getSelSQLMap");
	table.setParameter("moduleid",moduleid);
	table.setURL("../DaemonCM");
	table.request();//发送url请求
	table.response = function(text){
		setLoading(false);
		table.setXML(text);
		//alert(table.getXML().xml);
		if( table.getErrCode() != '0' ){//处理xml中的错误消息
			alert( table.getErrNote() );
			return;
		}
		//alert(table.getXMLNode("xdoc/xout/sqlmap").xml);
		$("sel_SQLMap").innerHTML = table.getXMLContent(table.getXMLNode("xdoc/xout/sqlmap"));
		$("sel_SQLMapHead").innerHTML = $("sel_SQLMap").innerHTML;
		$("sel_SQLMapBody").innerHTML = $("sel_SQLMap").innerHTML;
		if($F('txt_smid')==''){
			//如果没有指定sql取首个
			$('txt_smid').value=table.getXMLNode("xdoc/xout/sqlmap/select/option").getAttribute("value");
			$('txt_sqlstr').value = table.getXMLNode("xdoc/xout/sqlmap/select/option").getAttribute("sqlstr");
		}else{
			var e = $("sel_SQLMap").firstChild;
			var e1 = $("sel_SQLMapHead").firstChild;
			var e2 = $("sel_SQLMapBody").firstChild;
			e.value= $F('txt_smid');
			e1.value= $F('txt_smidhead');
			e2.value= $F('txt_smidbody');
			try {
				$('txt_sqlstr').value = e.options[e.selectedIndex].sqlstr;
				$('txt_sqlstrhead').value = e1.options[e1.selectedIndex].sqlstr;
				$('txt_sqlstrbody').value = e2.options[e2.selectedIndex].sqlstr;
			} catch (e) {
				// TODO: handle exception
			}
			
		}
		
		//加载sql语句成功后加载可选择的列
		loadSqlCols();
		
		grid.onCellClicked = function(e, col, row) {
			if(col==3) selUseValue(arr1,col,row);
			if(col==5) selUseValue(arr2,col,row);
			if(col==1) selUseText(arr3,col,row);
		};
	};
}

function loadSqlCols(){
	setLoading(true,'加载可选择的列');
	var table = new AW.XML.Table;//new 一个table对象
	table.setParameter("operation","getSQLCols");
	table.setParameter("smid",$F('txt_smid'));
	table.setURL("../DaemonCM");
	table.request();//发送url请求
	table.response = function(text){
		setLoading(false);
		table.setXML(text);
		//alert(table.getXML().xml);
		if( table.getErrCode() != '0' ){//处理xml中的错误消息
			alert( table.getErrNote() );
			return;
		}
		var ss = table.getXMLText("xdoc/xout");
		if(ss!='') arr3 = ss.split(',');
	};
}
//提交更新数据
function saveCM(){
	setLoading(true);
	var table = new AW.XML.Table;
	table.setURL("../DaemonCM?operation=updateCM&cmid="+$('txt_cmid').value);
	table.setRequestMethod('POST');
	table.setRequestData(table.getXMLContent(cookBodyData()));
	table.request();
	table.response = function (text) {
		setLoading(false);
		table.setXML(text);
		var xcode = table.getXMLText("xdoc/xerr/code");
		if (xcode != 0) {
			alert(xcode + table.getXMLText("xdoc/xerr/note"));
		} else {
			alert("保存成功！");
		}
	};
}
//生成提交信息
function cookBodyData(){
	var table = new AW.XML.Table;
	table.setXML("");
	var doc = table.getXML();
	var elmSet = doc.createElement("xdoc");
	
	//添加表头信息
	var nodeName =["cmid","note","title","footer","moduleid","smid","smidhead","smidbody","xslview","xslprint"];
	var nodeData =[$('txt_cmid').value,$("txt_note").value, $("txt_title").value, $("txt_footer").value,$("txt_moduleid").value,$("txt_smid").value,$("txt_smidhead").value,$("txt_smidbody").value,$("txt_xslview").value,$("txt_xslprint").value ];
	
	var elmHead = $XML(doc,"head",nodeName,nodeData);

	elmSet.appendChild(elmHead);
	//添加表体信息
	elmSet.appendChild(grid.toXML());
	//alert(elmSet.xml);
	return elmSet;
}

function addNewRow(){
	var rows = grid.getRowCount();
	grid.insertRow();
	grid.setCellData(rows+1,0,rows+1);
	grid.setCellText("字符型",3,rows+1);
	grid.setCellData(0,3,rows+1);
	grid.setCellData(100,4,rows+1);
	grid.setCellText("不汇总",5,rows+1);
	grid.setCellData(0,5,rows+1);
	grid.refresh();
}
function delRow(){
	grid.delRow();
	grid.refresh();
}

function newCM(){
	setLoading(true);
	var moduleid=$("txt_moduleid").value;
	alert("注意，新建的视图模型仅针对模块："+moduleid);
	var table = new AW.XML.Table;//new 一个table对象
	table.setParameter("operation","makeCMID");
	table.setParameter("moduleid",moduleid);
	table.setURL("../DaemonCM");
	table.request();//发送url请求
	table.response = function(text){
		setLoading(false);
		table.setXML(text);
		//alert(table.getXML().xml);
		if( table.getErrCode() != '0' ){//处理xml中的错误消息
			alert( table.getErrNote() );
			return;
		}
		$("txt_cmid").value = table.getXMLText("xdoc/xout/newcmid");
		//设置cmid并保存
		saveCM();
	};
}

function changeSQL(e){
	//alert(e.parentNode.id);
	if(e.parentNode.id == 'sel_SQLMap'){
		$('txt_sqlstr').value = e.options[e.selectedIndex].sqlstr;
		$('txt_smid').value   = e.value;
	}
	if(e.parentNode.id == 'sel_SQLMapHead'){
		$('txt_sqlstrhead').value = e.options[e.selectedIndex].sqlstr;
		$('txt_smidhead').value   = e.value;
	}
	if(e.parentNode.id == 'sel_SQLMapBody'){
		$('txt_sqlstrbody').value = e.options[e.selectedIndex].sqlstr;
		$('txt_smidbody').value   = e.value;
	}
	
}

function openSQLEdit(){
	//alert('此功能暂 不开放');
	//return;
	var smid=$("txt_smid").value;
	window.open("sqlMap.jsp?smid="+smid);
}

function selUseValue(arr,col,row){
	if(arr==null) return;
	var cbValueType = new AW.UI.Combo;
	cbValueType.setItemText(arr);
	cbValueType.setItemCount(arr.length);
	grid.setCellTemplate(cbValueType, col, row);
	grid.refresh();
	var curvalue = grid.getCellText(col,row);
	var i;
	for ( i = 0; i < arr.length; i++) {
		if(curvalue==arr[i]){
			break;
		}
	}
	cbValueType.setCurrentItem(i);
	cbValueType.focus();
	cbValueType.onControlDeactivated = function(event){
		grid.setCellTemplate("",col,row);
		grid.setCellData(cbValueType.getCurrentItem(),col,row);
		grid.setCellText(arr[cbValueType.getCurrentItem()],col,row);
	};
}

function selUseText(arr,col,row){
	var cbValueType = new AW.UI.Combo;
	cbValueType.setItemText(arr);
	cbValueType.setItemCount(arr.length);
	grid.setCellTemplate(cbValueType, col, row);
	grid.refresh();
	var curvalue = grid.getCellText(col,row);
	var i;
	for ( i = 0; i < arr.length; i++) {
		if(curvalue==arr[i]){
			break;
		}
	}
	
	cbValueType.setCurrentItem(i);
	
	cbValueType.focus();
	cbValueType.onControlDeactivated = function(event){
		grid.setCellTemplate("",col,row);
		grid.setCellData(arr[cbValueType.getCurrentItem()],col,row);
	};
}

function formatText(col,arr){
	var rows = grid.getRowCount();
	for( var i=0; i<rows; i++ ){
		grid.setCellText( arr[grid.getCellData(col,i)], col, i );
	}
}
</script>
</head>
<body>
	<h2>模型视图编辑器</h2>
	视图ID：
	<input type="text" id="txt_cmid" value="<%=cmid %>" readonly="readonly" />
	对应模块ID：
	<input type="text" id="txt_moduleid" value="" readonly="readonly" />
	<div>
		<input type="button" value="重新加载" onclick="loadCM()"> <input
			type="button" value="保存" onclick="saveCM()"> <input
			type="button" value="新建" onclick="newCM()">
	</div>
	<p></p>
	标题区域：
	<br>
	<textarea rows="3" style="width: 100%" id="txt_title"></textarea>

	<br> 列显示定义:
	<br>
	<input type="button" value="新增行" id="btn_addRow" onclick="addNewRow();">
	<input type="button" value="删除行" id="btn_deleteRow" onclick="delRow()">
	<div id="grid4cmdetail"></div>

	<br> 显示及打印格式定义→ 显示样式文件：
	<input type="text" id="txt_xslview" value="" /> 打印样式文件：
	<input type="text" id="txt_xslprint" value="" />

	<br> 注脚区域：
	<br>
	<textarea rows="3" style="width: 100%" id="txt_footer"></textarea>
	<p></p>
	备注：
	<textarea rows="3" style="width: 100%" id="txt_note"></textarea>
	<p></p>
	清单查询语句：
	<br>
	<textarea rows="3" style="width: 100%" id="txt_sqlstr"
		readonly="readonly"></textarea>
	SQLID：
	<input type="text" id="txt_smid" value="" readonly="readonly" />
	<span id="sel_SQLMap"></span>这里更改SQL语句或
	<a onclick="openSQLEdit()" href="javascript:void()">编辑</a>当前SQL
	<br> 表头查询语句：
	<textarea rows="3" style="width: 100%" id="txt_sqlstrhead"
		readonly="readonly"></textarea>
	SQLID：
	<input type="text" id="txt_smidhead" value="" readonly="readonly" />
	<span id="sel_SQLMapHead"></span>
	<br> 表体查询语句：
	<textarea rows="3" style="width: 100%" id="txt_sqlstrbody"
		readonly="readonly"></textarea>
	SQLID：
	<input type="text" id="txt_smidbody" value="" readonly="readonly" />
	<span id="sel_SQLMapBody"></span>


</body>
</html>

