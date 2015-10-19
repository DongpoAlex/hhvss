<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.myshop.basic.*"
	import="com.royalstone.vss.net.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="java.sql.*" import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid = 6000009;
	request.setCharacterEncoding("UTF-8");

	HttpSession session = request.getSession(false);
	if (session == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null)
		throw new PermissionException(PermissionException.LOGIN_PROMPT);

	//查询用户的权限.
	Permission perm = token.getPermission(moduleid);
	if (!perm.include(Permission.READ)) {
		throw new PermissionException("您未获得操作此模块的授权,请管理员联系.模块号:"+ moduleid);
	}
	
	SelNetDCshop shop = new SelNetDCshop(token,"全部");
	shop.setAttribute("id","txt_dccode");
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
<script language="javascript" src="../js/common.js"></script>
<script language="javascript" type="text/javascript">

function search(){
		setLoading( true );
		var parms = new Array();
		parms.push("action=netparamdate_list");
		
		if($("txt_dccode").value != undefined && $("txt_dccode").value!=""){
			parms.push("dccode="+$("txt_dccode").value);
		}
		if($("txt_logistics").value != undefined && $("txt_logistics").value!=""){
			parms.push("logistics="+$("txt_logistics").value);
		}
		var url  = "../DaemonNetOrderParam?"+parms.join('&');
		var table = new AW.XML.Table;
		table.setURL(url);
		table.setTable("xdoc/xout/netorderparamdate");
		table.setRows("row");
		table.request();
		table.response = function(text){
			table.setXML(text);
			var xcode = table.getErrCode();
			if( xcode != '0' ){//处理xml中的错误消息
				alert( xcode+table.getErrNote());
			}else{
				$("div_result").innerHTML = getGrid(table);
			}
			setLoading( false );
		};
	}
	
	function getGrid( table )
	{
		var row_count = table.getCount();
		if( row_count == 0 ) return "没有记录";
		var grid = new AW.UI.Grid;
	
		table.setColumns(["dccode", "logistics","monday","tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "note", "upper", "uppdate", "inputer", "inputdate","bbbb","aaa"]);	
		var columnNames = ["DC编码","物流模式","周一","周二","周三","周四","周五","周六","周日","备注","修改人","修改时间","录入人","录入时间","",""];	
		grid.setId("grid");
		grid.setColumnCount(columnNames.length);
		grid.setRowCount( row_count );	
		grid.setHeaderText(columnNames);
		grid.setSelectorVisible(true);
		grid.setSelectorWidth(30);
		grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
		
		grid.onCellClicked = function(event, column, row){
			var dccode = grid.getCellValue(0,row);
			var logistics =  grid.getCellValue(1,row);
			if(column==14){
					window.open("./netorderparadate_edit.jsp?dccode="+dccode+"&logistics="+logistics);
			}else if(column==15){
				    del(dccode,logistics);
			}
		};
		grid.setSelectionMode("single-row");
		grid.setCellModel(table);
		
		for( var i=0; i<row_count; i++ ){
			
			if( grid.getCellText(1,i)==1){
				lable ="直送";
				grid.setCellText( lable, 1, i);
			} if(grid.getCellText(1,i)==2){
				lable ="直通";
				grid.setCellText( lable, 1, i);
			}else if(grid.getCellText(1,i)==3){
				lable ="配送";
				grid.setCellText( lable, 1,i );
			}
			lable = ( grid.getCellText(2,i)=="Y" ) ? "是" : "否";
			grid.setCellText( lable,2, i );
			
			lable = ( grid.getCellText(3,i)=="Y" ) ? "是" : "否";
			grid.setCellText( lable,3, i );
			
			lable = ( grid.getCellText(4,i)=="Y" ) ? "是" : "否";
			grid.setCellText( lable,4, i );
			
			lable = ( grid.getCellText(5,i)=="Y" ) ? "是" : "否";
			grid.setCellText( lable,5, i );
			
			lable = ( grid.getCellText(6,i)=="Y" ) ? "是" : "否";
			grid.setCellText( lable,6, i );
			
			lable = ( grid.getCellText(7,i)=="Y" ) ? "是" : "否";
			grid.setCellText( lable,7, i );
			
			lable = ( grid.getCellText(8,i)=="Y" ) ? "是" : "否";
			grid.setCellText( lable,8, i );
			
		}
		
		grid.refresh();
		grid.setCellText('修改',14);
		grid.setCellText('删除',15);
		
		//数据统计信息
		var totalCount = table.getXMLText("xdoc/xout/netorderparamdate/totalCount");
		var htmlCountInfo = " 共显示"+row_count+"行。<br>";
		if(row_count<totalCount){
			var htmlCountInfo = "记录总数"+totalCount+"行；当前显示"+row_count+"行。请尝试修改查询条件查看未显示数据<br>";
		}
		return htmlCountInfo+grid.toString();
	}
	function del(dccode,logistics){
		if(!confirm("该操作将删除文字信息，确认删除？")){
			return;
		}
		var table = new AW.XML.Table;
		var parms = new Array();
		parms.push("action=deldate");
		parms.push("dccode="+dccode);
		parms.push("logistics="+logistics);
		setLoading(true);
		var url = "../DaemonNetOrderParam?"+parms.join('&');
		table.setURL(url);
		table.setTable("xdoc/xout/netorderparamdate");
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
				 search();
			}
		};
	}
	function add(){
		 window.open("./netorderparadate_add.jsp");
	}
</script>
<style>
.aw-column-0,.aw-column-1,.aw-column-2,.aw-column-3,.aw-column-4,.aw-column-5,.aw-column-6,.aw-column-7,.aw-column-8
	{
	width: 60px;
	cursor: pointer;
	text-align: center
}

.aw-column-10,.aw-column-12 {
	width: 40px;
	text-align: center;
}

.aw-column-9,.aw-column-11,.aw-column-13 {
	width: 120px;
	text-align: center;
}

.aw-column-14 {
	width: 60px;
	text-align: center;
	color: blue
}

.aw-column-15 {
	width: 60px;
	text-align: center;
	color: red
}
</style>
<title>参数设置查询</title>
</head>
<body>
	<table cellspacing="1" cellpadding="2" width="98%"
		class="tablecolorborder">
		<tr>
			<td class="altbg2">DC:<%=shop%>&nbsp;&nbsp;&nbsp;&nbsp; 物流模式:<select
				id="txt_logistics">
					<option>全部</option>
					<option value="1">直送</option>
					<option value="2">直通</option>
			</select>&nbsp;&nbsp;&nbsp;&nbsp; <input class="button" value="查找"
				type="button" onclick="search()" /> <input class="button"
				value="新增" type="button" onclick="add()" /></td>
		</TR>
	</table>
	<div id="div_result"></div>
	<div id="enlarge_images" style="position: absolute; z-index: 999"></div>
</body>
</html>