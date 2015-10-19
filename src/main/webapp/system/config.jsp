<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.sql.*" import="com.royalstone.security.*"
	import="com.royalstone.util.*" import="com.royalstone.util.daemon.*"
	import="com.royalstone.vss.basic.Vender"
	import="com.royalstone.myshop.component.*"
	errorPage="../errorpage/errorpage.jsp"%>

<%
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );

final int moduleid = 3010123;
Permission perm = token.getPermission( moduleid );
if ( !perm.include( Permission.READ ) ) 
	throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>配置项</title>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" src="../js/json.js"> </script>
<script language="javascript" src="../js/EditGrid-1.1.js"> </script>
<script language="javascript" src="../js/Date.js" type="text/javascript"> </script>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<style>
.aw-grid-control {
	height: 100%;
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

.aw-column-0 {
	width: 150px;
}

.aw-column-1 {
	width: 200px;
}

.aw-column-2 {
	width: 240px;
}
</style>
<script language="javascript" type="text/javascript">
		var grid = new AW.Grid.Editor;
		var clazz = "Config";
			//列名数组
			var columnNames = ["配置键","配置值","备注","是否可编辑"];
			var nodeNames = ["valuekey","keyvalue","note","editflag"];
			
			window.onload= function(){
			grid.setId('grid');
			grid.setHeaderText( columnNames );
			grid.setNodeNames( nodeNames );
			grid.setColumnCount(columnNames.length);
			grid.setSelector();
			
			//确认中validating (Enter)一般用于数据检测,return true 则不能确认此数据,无法编辑其它数据
			grid.onCellValidating = function(text, col, row){
				var editflag = grid.getCellValue(3,row);
				if(editflag != 'Y'){
					alert("该项不允许编辑");
					return true;
				}
			};
			grid.setCellEditable(false);
			grid.setCellEditable(true,1);
			grid.setCellEditable(true,2);
			$("div_detail").innerHTML = grid.toString();
			show();
		};

		function update(){
			setLoading(true);
			var table = new AW.XML.Table;
			var reqData = grid.toJSON();
			table.setURL("../DaemonMain?clazz="+clazz+"&isbu=true&operation=update");
			table.setRequestMethod('POST');
			table.setRequestData(reqData);
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

		function show(){
			setLoading(true);
			var table = new AW.XML.Table;
			table.setURL("../DaemonMain?clazz="+clazz+"&isbu=true&operation=getList");
			table.setTable("xdoc/xout/rowset");
			table.request();
			table.response = function (text) {
				setLoading(false);
				table.setXML(text);
				var xcode = table.getXMLText("xdoc/xerr/code");
				if (xcode != 0) {
					alert(xcode + table.getXMLText("xdoc/xerr/note"));
				} else {
					grid.setRowCount(table.getCount());
					table.setColumns(grid.getNodeNames());
					grid.setCellModel(table);
					grid.refresh();
				}
			};
		}
	</script>
</head>
<body>
	<input type="button" name="" value="保存" onclick="update()">
	<div id="div_detail"></div>
</body>
</html>
