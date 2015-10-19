<%@page contentType="text/html;charset=UTF-8" session="false"%>
<%@include file="../include/common.jsp"%>
<%@include file="../include/token.jsp"%>
<%@include file="../include/permission.jsp"%>
<%
    //用户的查询权限.
	token.checkPermission(moduleid,Permission.READ);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>对账次数控制</title>
<script src="../AW/runtime/lib/aw.js" type="text/javascript"
	type="text/javascript"></script>
<script language="javascript" src="../js/MainHTML.js"> </script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" src="../js/json.js"> </script>
<script language="javascript" src="../js/EditGrid-1.1.js"> </script>
<script language="javascript" src="../js/MainEditGrid.js"> </script>
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>
<style>
.aw-grid-control {
	height: 70%;
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
</style>
<script language="javascript" type="text/javascript">
		var grid = new AW.Grid.Editor;
		var service = "LQConfig";
		var editGrid = new EditGrid(service);
		var BU = new HTML("BU");
		var PayShop = new HTML("PayShop");
		window.onload= function(){
			editGrid.setEditCellIdx([7,9]);
			BU_PayShop_init();
			editGrid.init();
			editGrid.endInitGrid = function(){
				var rowCount = editGrid._grid.getRowCount();
				if(rowCount){
					for ( var i = 0; i < rowCount; i++) {
						var lable = ( editGrid._grid.getCellText(6,i)=='Y' ) ? "是" : "否";
						editGrid._grid.setCellText( lable, 6, i );
						var lable = ( editGrid._grid.getCellText(11,i)=='Y' ) ? "是" : "否";
						editGrid._grid.setCellText( lable, 11, i );
					}
				}
				
				var rowCombo = new AW.Templates.Combo; 
				editGrid._grid.setCellTemplate(rowCombo, 6); 
				editGrid._grid.setCellTemplate(rowCombo, 11); 
				editGrid._grid.setPopupTemplate(function(col, row){
			        var grid = this;  
			        var list = new AW.UI.List;  
			        list.setItemText(["是", "否"]);
			        list.setItemValue(["Y", "N"]);
			        list.setItemCount(2);  
			        list.onItemClicked = function(event, i){  
			            var text = this.getItemText(i); 
			            var value = this.getItemValue(i);
			            grid.isOnCellEdit = false; //手动设置编辑值，禁止表格自动设置编辑值
			            grid.setCellData(value, col, row);  
			            grid.setCellText(text, col, row);  
			            grid.getCellTemplate(col, row).hidePopup();  
			            grid.isOnCellEdit = true;
			        };
			        return list;  
			    }); 
			};
		};
		
		function toText(value){
			if(value=='Y'){
				return '是';
			}else{
				return '否';
			}
		}

		function update(){
			setLoading(true);
			var table = new AW.XML.Table;
			var reqData = grid.toJSON();
			table.setURL("../DaemonMain?service="+service+"&isbu=true&operation=update");
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
					alert(10)
					editGrid.search();
					alert("保存成功！");
				}
			};
		}

	</script>
</head>
<body>
	<div id="divTitle"></div>
	<div id="divSearch" class="search_main">
		<div class="search_parms">
			BU:<span id="span_buid"></span>
		</div>
		<div class="search_parms">
			结算主体:<span id="span_payshop"></span>
		</div>
		<div class="search_parms">
			供应商编码: <input type="text" id="txt_venderid" name="txt_parms"
				split="," alt="供应商编码" />
		</div>
		<div class="search_parms">
			供应商名称（模糊）: <input type="text" id="txt_vendername" name="txt_parms"
				alt="供应商名称" />
		</div>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
			<span id="div_button_save"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>
