<%@page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.email.*" errorPage="../errorpage/errorpage.jsp"%>

<%
%>
<?xml version="1.0" encoding="UTF-8"?>
<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title>对帐单查询</title>
<script src="./faw.js" type="text/javascript"></script>

<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<style type="text/css">
.aw-grid-control {
	height: 76%;
	width: 100%;
	border: none;
	font: menu;
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
	width: 130px;
	cursor: pointer;
}
</style>

<script language="javascript" type="text/javascript">

function show_catalogue()
{
	var table = new AW.XML.Table;

	table.setURL("../DaemonMail?focus=read&type=0&mailid=9");
	table.setAsync(false);
	table.request();
	
	
	alert( table.getErrNote() );
	

	var rows = table.getCount();
	
	table.setColumns(["sheetid","bookname","editdate","refsheetid","lognote","venderid"]);
	var columnNames=[ "单据号", "分公司", "制单日期", "付款单编码", "备注", "供应商编码"];
	
	var grid = new AW.UI.Extended;
	grid.setId( "myGrid" );
	grid.setColumnCount( columnNames.length );
	grid.setRowCount( rows );	
	grid.setHeaderText( columnNames );	
	grid.setCellModel(table);


	$("div_report").innerHTML = table.transformNode("format4mail.xsl");
}


function loadData(){

	var table = new AW.XML.Table;//new 一个table对象
	table.setURL("../DaemonMail?operation=browse");//设置 url
	table.setAsync(true);//设置异步/同步模式，true为异步（默认）,false为同步
	table.setTable("xdoc/xout/BrowseMail");//设置table数据路径，也就是需显示的xml的xpath。注意该方法一定在table.setXML方法之前。
	table.request();//发送url请求
	table.response = function(text){//设置异步响应方法。当服务器返回信息将调用该方法。当设置为同步模式时跳过该方法。
	
		table.setXML(text);//读取服务器返回的xml信息
		
		if( table.getErrCode() != '0' ){//处理xml中的错误消息
			alert( table.getErrNote() );
			return;
		}
		
		var rows = table.getCount();//得到有效数据行数。
		
		if( rows == 0 ) alert("该请求没有有效数据");
		
		table.setColumns(["mailid","title","sender","sendtime","fileid","readcount"]);//设置table中需要显示的列名，需与xml中的名一致,大小写敏感。
		
		var columnNames = ["邮件ID","邮件标题","发件人","发送时间","附件","阅读次数"];
		//一下设置表格
		var grid = new AW.Grid.Extended;
		grid.setColumnCount( columnNames.length );//列数目
		grid.setRowCount( rows );//行数
		grid.setHeaderText( columnNames );//列标题
		
		var sum_readcount = table.getCellSum(5);//统计第六列和，如果是非Number则返回NaN
		
		grid.setFooterVisible(true);//显示表格底
		grid.setFooterText(["","查询结果:"+rows+"条","","",""+sum_readcount]);//表底显示内容
		
		grid.setCellModel(table);//设置表格数据源
		
		$("div_report").innerHTML = grid.toString();
	};
	
	
}



function cookAddr(e){
	var obj=document.getElementById("txt_sel_addr");
	var arr_addr = new Array();
	for(var i=0;i<obj.length;i++){
		if(obj.options[i].selected){
			var tmp = obj.options[i].value
			arr_addr.push(tmp);
		}
	}
	
	document.getElementById("txt_addr").value = arr_addr.join(",");
}
</script>

</head>
<body onload="show_catalogue()">
	<input id="txt_addr" type="text" />
	<div id="div_report"></div>
</body>
</html>
