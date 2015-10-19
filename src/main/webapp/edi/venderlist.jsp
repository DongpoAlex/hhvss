<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>edi供应商维护</title>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"
	type="text/javascript"></script>
<script language="javascript" src="../js/common.js"
	type="text/javascript"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/EditGrid.js"> </script>
<script language="javascript" src="../js/common.js"> </script>

<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<style>
.aw-grid-control {
	height: 80%;
	width: 100%;
	border: 1px solid #333;
}

.aw-grid-row {
	height: 20px;
	border-bottom: 1px dashed #ccc;
	height: 24px;
}

.aw-grid-cell {
	border-right: 1px solid #eee;
}

.aw-column-1 {
	width: 200px;
}

.disabled {
	border: none;
	border-bottom: 1px solid #000;
}

.abled {
	background-color: #efe;
}
</style>
<script language="javascript" type="text/javascript">
		var grid = new AW.Grid.Editor;
		window.onload= function(){
			search();
		};
		function searchByVenderid(){
			search($("txt_venderid").value);
		}
		function search(venderid){
			setLoading(true);
			var table = new AW.XML.Table;
			var parms = new Array();
			parms.push("operation=search");
			if(venderid != undefined && venderid!=""){
				parms.push("venderid="+venderid);
			}
			var url = "../DaemonEDI?"+parms.join('&');
			table.setURL(url);
			table.setTable("xdoc/xout/rowset");
			table.setRows("row");
			table.request();
			table.response = function (text) {
				setLoading(false);
				table.setXML(text);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					table.setColumns([ "venderid","vendername","purchase","pstartdate","receipt","rstartdate","checking","cstartdate" ] );
					var columnNames = [ "供应商编码","供应商","导出订单","订单生效日期", "导出验收单","验收生效日期", "导出对账","对账生效日期" ];
					
					var row_count = table.getCount();
					if(row_count==0){
						$('div_list').innerHTML="没有记录";
					}
					grid.setId( "grid_cat" );
					grid.setColumnCount( columnNames.length );
					grid.setRowCount( row_count );	
					grid.setHeaderText( columnNames );
					grid.onCellClicked = function(e, col, row) {
						//加载到编辑区
						var curvalue = grid.getCellValue(0,row);
						$("txt_venderid").value=curvalue;

						curvalue = grid.getCellValue(1,row);
						$("txt_vendername").value=curvalue;
						
						curvalue = grid.getCellValue(2,row);
						$("txt_purchase").checked=curvalue==1;

						curvalue = grid.getCellValue(3,row);
						$("txt_pstartTouchDate").value=curvalue;
						
						curvalue = grid.getCellValue(4,row);
						$("txt_receipt").checked=curvalue==1;

						curvalue = grid.getCellValue(5,row);
						$("txt_rstartTouchDate").value=curvalue;
						
						curvalue = grid.getCellValue(6,row);
						$("txt_checking").checked=curvalue==1;
						
						curvalue = grid.getCellValue(7,row);
						$("txt_cstartTouchDate").value=curvalue;
					};
					
					grid.setCellModel(table);
					for( var i=0; i<row_count; i++ ){
						var lable = ( grid.getCellText(2,i)==1 ) ? "是" : "否";
						grid.setCellText( lable, 2, i );
						lable = ( grid.getCellText(4,i)==1 ) ? "是" : "否";
						grid.setCellText( lable, 4, i );
						lable = ( grid.getCellText(6,i)==1 ) ? "是" : "否";
						grid.setCellText( lable, 6, i );
					}
					
					grid.refresh();
					var sumcount = table.getXMLNode("/xdoc/xout/rowset").getAttribute("rows");
					$('div_list').innerHTML= "查询结果：总计："+sumcount+"行， 显示："+row_count+"行"+grid.toString();
				}
			};
		}

		function save(){
			var venderid = $("txt_venderid").value;
			if(venderid==""){
				alert("供应商编码必须填写！");
				return;
			}
			
			var table = new AW.XML.Table;
			var parms = new Array();
			parms.push("operation=save");
			parms.push("venderid="+venderid);
			var ischecked = false;  
			if($("txt_purchase").checked){
				if($("txt_pstartTouchDate").value==""){
					alert("启用订单导出,启用日期必须填写！");
					return;
				}
				parms.push("seqs=1,"+$("txt_pstartTouchDate").value);
				ischecked=true;
			}
			if($("txt_receipt").checked){
				if($("txt_rstartTouchDate").value==""){
					alert("启用验收单导出,启用日期必须填写！");
					return;
				}
				parms.push("seqs=2,"+$("txt_rstartTouchDate").value);
				ischecked=true;
			}
			if($("txt_checking").checked){
				if($("txt_cstartTouchDate").value==""){
					alert("启用对账,启用日期必须填写！");
					return;
				}
				parms.push("seqs=3,"+$("txt_cstartTouchDate").value);
				ischecked=true;
			}

			if(!ischecked){
				if(!confirm("没有选择任何导出项目，将删除这个供应商的EDI导出，请确认！")){
					return;
				}
			}
			setLoading(true);
			var url = "../DaemonEDI?"+parms.join('&');
			table.setURL(url);
			table.setTable("xdoc/xout/rowset");
			table.setRows("row");
			table.request();
			table.response = function (text) {
				setLoading(false);
				table.setXML(text);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					alert("保存成功！");
					/*if(ischecked){
						search(venderid);
					}else{
						search();
					}*/
					search();
					add();
				}
			};

		}
		function add(){
			$("txt_venderid").value="";
			$("txt_vendername").value="";
			$("txt_venderid").focus();
			$("txt_purchase").checked=true;
			$("txt_receipt").checked=true;
			$("txt_checking").checked=true;
		}
		function checkVender(v){
			if( v != "" ){
				setLoading(true,'正在初始化供应商信息');
				var url 		= "../DaemonVender?venderid=" + v;
				var table = new AW.XML.Table;
				table.setURL(url);
				table.setTable("xdoc/xout/list");
				table.request();
				table.response = function(text){
					setLoading(false);
					table.setXML(text);
					var xcode = table.getErrCode();
					if( xcode != '0' ){//处理xml中的错误消息
						alert( xcode+table.getErrNote());
						$('txt_venderid').value = "";
						$('txt_vendername').innerText='';
					}else{
						$('txt_vendername').innerText=table.getXMLText( "/xdoc/xout/vender/vendername" );
					}
				};
			}else{
				$('txt_vendername').innerText='';
			}
		}
		</script>

</head>


<body>
	<div style="margin: 9px;"></div>
	<div id="div_input" style="padding: 9px; border: #ccc 1px solid;">
		供应商编码:<input type="text" id="txt_venderid" size="10"
			onchange="checkVender(this.value)">&nbsp;&nbsp; 供应商名称:<input
			type="text" id="txt_vendername" size="30" readonly="readonly">&nbsp;&nbsp;
		<br></br> <label for="txt_purchase">导出订单:<input
			type="checkbox" id="txt_purchase" style="border: none;" /></label>&nbsp;&nbsp;
		生效日期:<input type="text" id="txt_pstartTouchDate" class="Wdate"
			onFocus="WdatePicker()" name="txt_parms" notnull="notnull" alt="生效日期" />&nbsp;&nbsp;
		<label for="txt_receipt">导出验收单:<input type="checkbox"
			id="txt_receipt" style="border: none;" /></label>&nbsp;&nbsp; 生效日期:<input
			type="text" id="txt_rstartTouchDate" class="Wdate"
			onFocus="WdatePicker()" name="txt_parms" notnull="notnull" alt="生效日期" />&nbsp;&nbsp;
		<label for="txt_checking">导出对账:<input type="checkbox"
			id="txt_checking" style="border: none;" /></label>&nbsp;&nbsp; 生效日期:<input
			type="text" id="txt_cstartTouchDate" class="Wdate"
			onFocus="WdatePicker()" name="txt_parms" notnull="notnull" alt="生效日期" />&nbsp;&nbsp;
	</div>
	<div>
		<input type="button" value="新增" id="btn_search" onclick="add()">&nbsp;&nbsp;
		<input type="button" value="保存" id="btn_search" onclick="save()">&nbsp;&nbsp;
		<input type="button" value="根据供应商编码过滤" id="btn_search"
			onclick="searchByVenderid()">&nbsp;&nbsp; <input
			type="button" value="查看全部" id="btn_search" onclick="search()">&nbsp;&nbsp;
	</div>
	<div id="div_list"></div>
</body>
</html>
