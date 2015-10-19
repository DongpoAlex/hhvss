<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="com.royalstone.security.*"
	import="com.royalstone.vss.net.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );

SelNetDCshop shop = new SelNetDCshop(token,"请选择");
shop.setAttribute("id","txt_dccode");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css"></link>
<link href="../certificate/css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" src="../js/EditGrid.js"> </script>
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
<title>大宗或验收供应商维护</title>
<script language="javascript" type="text/javascript">
		var grid = new AW.Grid.Editor;
		window.onload= function(){
			$("txt_dccode").disabled =false;
			$("txt_venderid").disabled =false;
			$("txt_vendername").disabled =false;
			search();
		};
		function search(){
			
			divMain.innerHTML = "请稍候 ..." ;
			setLoading(true);
			var table = new AW.XML.Table;
			var parms = new Array();
			parms.push("action=list_vender");
			if($("txt_dccode").value != undefined && $("txt_dccode").value!=""){
				parms.push("dccode="+$("txt_dccode").value);
			}
			if($("txt_venderid").value != undefined && $("txt_venderid").value!=""){
				parms.push("vendcode="+$("txt_venderid").value);
			}
			
			var url = "../DaemonNetLargerVender?"+parms.join('&');
			table.setURL(url);
			table.setTable("xdoc/xout/netlargevender");
			table.setRows("row");
			table.request();
			table.response = function (text) {
				setLoading(false);
				table.setXML(text);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					table.setColumns([ "dccode","vendername","vendcode","vendtype","operatertype","note","inputer","inputdate","isvalid","upper","uppdate"] );
					var columnNames = [ "DC编码","供应商名称","供应商编码","供应商类型","操作类型", "备注","录入人", "录入时间","是否有效","修改人","修改时间" ];
					
					var row_count = table.getCount();
					if(row_count==0){
						$('divMain').innerHTML="没有记录";
					}
					grid.setId( "grid_cat" );
					grid.setColumnCount( columnNames.length );
					grid.setRowCount( row_count );	
					grid.setHeaderText( columnNames );
					grid.onCellClicked = function(e, col, row) {
						//加载到编辑区
						var curvalue = grid.getCellValue(0,row);
						$("txt_dccode").value=curvalue;						
						$("txt_dccode").disabled =true;
						
						curvalue = grid.getCellValue(2,row);
						$("txt_venderid").value=curvalue;
						$("txt_venderid").disabled =true;
						
						curvalue = grid.getCellValue(1,row);
						$("txt_vendername").value=curvalue;
						$("txt_vendername").disabled =true;
						
						curvalue = grid.getCellValue(3,row);
						$("txt_vendertype").value=curvalue;
						
						curvalue = grid.getCellValue(5,row);
						$("txt_note").value=curvalue;
						
					};
					
					grid.setCellModel(table);
					for( var i=0; i<row_count; i++ ){
						var lable = ( grid.getCellText(3,i)==1 ) ? "综合验收供应商" : "大宗供应商";
						grid.setCellText( lable, 3, i );
						
						if( grid.getCellText(4,i)==0){
							lable ="增加";
							grid.setCellText( lable, 4, i);
						} if(grid.getCellText(4,i)==1){
							lable ="删除";
							grid.setCellText( lable, 4, i);
						}else if(grid.getCellText(4,i)==2){
							lable ="修改";
							grid.setCellText( lable, 4,i );
						}
						
						lable = ( grid.getCellText(8,i)=="Y" ) ? "是" : "否";
						grid.setCellText( lable,8, i );
					}
					
					grid.refresh();
					var sumcount = table.getXMLNode("/xdoc/xout/netlargevender").getAttribute("rows");
					$('divMain').innerHTML= "查询结果：总计："+sumcount+"行， 显示："+row_count+"行"+grid.toString();
				}
			};
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
		function save(){
			var dccode = $("txt_dccode").value;
			if(dccode==""){
				alert("DC编码必须填写！");
				$("txt_dccode").focus();
				return;
			}
			var venderid = $("txt_venderid").value;
			if(venderid==""){
				alert("供应商编码必须填写！");
				$("txt_venderid").focus();
				return;
			}
			var vendertype = $("txt_vendertype").value;
			if(vendertype==""){
				alert("请选择供应商类型！");
				$("txt_vendertype").focus();
				return;
			}
			
			var parms = new Array();
			parms.push("action=list_vender");
			if($("txt_dccode").value != undefined && $("txt_dccode").value!=""){
				parms.push("dccode="+$("txt_dccode").value);
			}
			if($("txt_venderid").value != undefined && $("txt_venderid").value!=""){
				parms.push("vendcode="+$("txt_venderid").value);
			}
			var url = "../DaemonNetLargerVender?"+parms.join('&');
			var table = new AW.XML.Table;
			table.setURL(url);
			table.setTable("xdoc/xout/netlargevender");
			table.setRows("row");
			table.request();
			table.response = function(text){
				table.setXML(text);
				var xcode = table.getErrCode();
				if( xcode != '0' ){//处理xml中的错误消息
					alert( xcode+table.getErrNote());
				}else{
					var row_count = table.getCount();
						if( row_count > 0 ){
							if(!confirm("此DC供应商已定义，是否修改。请确认？")){
								return;
							}
							var table_upd = new AW.XML.Table;
							var parms_upd = new Array();
							parms_upd.push("action=upd");
							parms_upd.push("dccode="+dccode);
							parms_upd.push("vendcode="+venderid);
							parms_upd.push("vendertype="+vendertype);
							parms_upd.push("note="+$("txt_note").value);
							setLoading( true );
							var url = "../DaemonNetLargerVender?"+parms_upd.join('&');
							table_upd.setURL(url);
							table_upd.setTable("xdoc/xout/netlargevender");
							table_upd.setRows("row");
							table_upd.request();
							table_upd.response = function (text) {
								setLoading(false);
								table_upd.setXML(text);
								var xcode = table_upd.getXMLText("xdoc/xerr/code");
								if (xcode != 0) {
									alert(xcode + table_upd.getXMLText("xdoc/xerr/note"));
								} else {
									alert("修改成功！");
									add();
									search();
								}
							};
					}else{
							var table_add = new AW.XML.Table;
							var parms_add = new Array();
							parms_add.push("action=save");
							parms_add.push("dccode="+dccode);
							parms_add.push("vendcode="+venderid);
							parms_add.push("vendertype="+vendertype);
							parms_add.push("note="+$("txt_note").value);
							setLoading(true);
							var url = "../DaemonNetLargerVender?"+parms_add.join('&');
							table_add.setURL(url);
							table_add.setTable("xdoc/xout/netlargevender");
							table_add.setRows("row");
							table_add.request();
							table_add.response = function (text) {
								setLoading(false);
								table_add.setXML(text);
								var xcode = table_add.getXMLText("xdoc/xerr/code");
								if (xcode != 0) {
									alert(xcode + table_add.getXMLText("xdoc/xerr/note"));
								} else {
									alert("保存成功！");
									add();
									search();
								}
							};
					}
				}
			};
		}
		function add(){
			$("txt_dccode").disabled =false;
			$("txt_venderid").disabled =false;
			$("txt_vendername").disabled =false;
			$("txt_dccode").value="";
			$("txt_venderid").value="";
			$("txt_vendername").value="";
			$("txt_vendertype").value="";
			$("txt_note").value="";
			$("txt_dccode").focus();
			
		}
		function del(){
			if(!confirm("该操作将删除文字信息吗，确认删除？")){
				return;
			}
			var table = new AW.XML.Table;
			var parms = new Array();
			parms.push("action=del");
			parms.push("dccode="+$("txt_dccode").value);
			parms.push("vendcode="+$("txt_venderid").value);
			setLoading(true);
			var url = "../DaemonNetLargerVender?"+parms.join('&');
			table.setURL(url);
			table.setTable("xdoc/xout/netlargevender");
			table.setRows("row");
			table.request();
			table.response = function (text) {
				setLoading(false);
				table.setXML(text);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					alert("删除成功！");
					add();
					search();
				}
			};
			
		}
</script>
</head>
<body>
	<div style="margin: 9px;"></div>
	<div id="div_input" style="padding: 9px; border: #ccc 1px solid;">
		DC:
		<%=shop%>&nbsp;&nbsp; 供应商编码:<input type="text" id="txt_venderid"
			size="10" onchange="checkVender(this.value)">&nbsp;&nbsp;
		供应商名称:<input type="text" id="txt_vendername" size="30"
			readonly="readonly">&nbsp;&nbsp; 供应商类型:<select
			id="txt_vendertype">
			<option value="0">大宗供应商</option>
			<option value="1">综合验收供应商</option>
		</select>&nbsp;&nbsp; 备注:<input type="text" id="txt_note" value="" size="40">&nbsp;&nbsp;
		<BR>
	</div>
	<div>
		<input type="button" value="按DC编码和供应商查询" id="btn_search"
			onclick="search()">&nbsp;&nbsp; <input type="button"
			value="新增" id="btn_add" onclick="add()">&nbsp;&nbsp; <input
			type="button" value="保存" id="btn_save" onclick="save()">&nbsp;&nbsp;
		<input type="button" value="删除" id="btn_del" onclick="del()">&nbsp;&nbsp;
		<BR>
	</div>
	<div id="divMain"></div>
</body>
</html>
