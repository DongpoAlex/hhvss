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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>发票录入单</title>
<!-- ActiveWidgets stylesheet and scripts -->
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>

<style type="text/css">
</style>

<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/MainReportGrid.js"> </script>
<script language="javascript" src="../js/MainSheet.js"></script>
<script language="javascript" src="../js/MainHTML.js"> </script>
<script language="javascript" src="../js/common.js"> </script>

<script language="javascript" type="text/javascript">
	var s = new Sheet("VenderInvoice");
	var cmid = window.location.href.getQuery("cmid");

	var BU = new HTML("BU");
	var PayShop = new HTML("PayShop");

	var isVender = <%=token.isVender%>;
	
	window.onload = function(){
		s.init();
		s.allowPrint();
		s.disabledRead();
		s.disabledConfirm();

		BU_PayShop_init();

		s.show_catalogue=function(){
			var row_count = s._table4search.getCount();
			if(row_count==0){$("div1").innerHTML ="没有符合条件的数据";return;}
			s._grid = new AW.Grid.Extended;
			//初始化列
			s.initColModel();
			
			
			s._table4search.setColumns( s._cols );
			s._grid.setId('grid');
			s._grid.setColumnCount( s._cols.length );
			s._grid.setRowCount(row_count);
			s._grid.setHeaderText(s._colNames);
			s._grid.setFooterVisible(true);
			s._grid.setFooterText(s._footer);
			s._grid.setCellFormat(s._format);
			s._grid.setSelectionMode("multi-cell");
			//初始化列格式
			s.initFormat();
			//设置底角
			s.initFooterText();
			
			s._grid.setSelectorVisible(true);
			s._grid.setSelectorWidth(30);
			s._grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});

			s._grid.setCellModel(s._table4search);
			
			var obj_link = new AW.Templates.Link;
			obj_link.setStyle("color:blue;cursor:pointer;");
			obj_link.setEvent( "onclick",
				function(){
					var row = s._grid.getCurrentRow();
					s.offset = row;
					var sheetid = s._grid.getCellValue( 1,row );
					s.open_sheet_detail( sheetid );
				}
			);
			s._grid.setCellTemplate( obj_link, 1 ); 

			var obj_opt = new AW.Templates.Link;
			obj_opt.setStyle("color:blue;cursor:pointer;");
			obj_opt.setEvent( "onclick",
				function(){
					var row = s._grid.getCurrentRow();
					var sheetid = s._grid.getCellValue( 1, row );
					var refsheetid = s._grid.getCellValue( 3, row );
					editSheet(sheetid,refsheetid);
				}
			);
			
			var payFlagIdx = s.getColIdx("payflag");
			var invoiceFlagIdx = s.getColIdx("invoiceflag");
			for ( var i = 0; i < row_count; i++) {
				var flag=s._grid.getCellValue( 0,i );
				var payflag=s._grid.getCellValue( payFlagIdx,i );
				var invoiceflag=s._grid.getCellValue( invoiceFlagIdx,i );
				if(flag==0 && payflag==2 && invoiceflag==0 && isVender){
					s._grid.setCellTemplate( obj_opt, 0, i );
					s._grid.setCellText("编辑",0, i);
				}else{
					s._grid.setCellText("",0,i);
				}
			}
			var obj_link = new AW.Templates.Link;
			obj_link.setStyle("color:blue;cursor:pointer;");
			obj_link.setEvent( "onclick",
				function(){
					var row = s._grid.getCurrentRow();
					s.offset = row;
					var sheetid = s._grid.getCellValue( 3,row );
					s.open_win_show(3020301000,"Paymentsheet",sheetid);
				}
			);
			s._grid.setCellTemplate( obj_link, 3 ); 
			
			var row_total = s._table4search.getXMLNode("/xdoc/xout/rowset").getAttribute("row_total");
			var htmlCountInfo = "记录总数"+row_total+"行；当前显示"+row_count+"行。<br>";
			if(row_count<row_total){
				var htmlCountInfo = "记录总数"+row_total+"行；当前显示"+row_count+"行。请尝试修改查询条件查看未显示数据<br>";
			}
			$("div1").innerHTML = htmlCountInfo+s._grid.toString();
		};
	};

	function editSheet(sheetid,refsheetid){
		var attributeOfNewWnd = "status=yes,toolbar=no,menubar=no,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
		var url = "venderinvoice_edit.jsp?sheetid="+sheetid+"&refsheetid="+refsheetid+"&cmid="+cmid;
		a = window.open(url, sheetid,attributeOfNewWnd);
		a.focus();
	}
</script>
</head>

<body>
	<div id="divTitle"></div>
	<div id="div_tabs" style="width: 100%;"></div>
	<div id="div1"></div>
	<div id="div2" style="display: none;">
		<div id="div_navigator" style="margin: 4px;">
			<input type="button" value="上一单" onclick="s.sheet_navigate(-1)" /> <input
				type="button" value="下一单" onclick="s.sheet_navigate(1)" /> <input
				type="button" value="打印单据" onclick="s.open_win_print()"
				style="display: none" id="btn_print" /> <span id="offset_current"></span>
		</div>
		<div id="div_warning" class="warning"></div>
		<div id="div_sheetshow"></div>
	</div>
	<div id="div0" class="search_main">
		<table cellspacing="1" cellpadding="2">
			<tr class="search_main_head">
				<% if(!token.isVender){ %><td>供应商编码</td>
				<% } %>
				<td>状态</td>
				<td>BU</td>
				<td>结算主体</td>
				<td>单据号(可选)</td>
				<td>制单日期(可选)</td>
			</tr>
			<tr>
				<% if(!token.isVender){ %>
				<td><input type="text" id="txt_venderid" name="txt_parms"
					size="16" split="," alt="供应商编码" /></td>
				<% } %>
				<td><select id="txt_flag" name="txt_parms">
						<option value="" alt="">全部</option>
						<option value="0" alt="">新建/待提交</option>
						<option value="2" alt="">已提交/待确认</option>
						<option value="100" alt="">已确认</option>
						<option value="-100" alt="">已作废</option>
				</select></td>
				<td><span id="span_buid"></span></td>
				<td><span id="span_payshop"></span></td>
				<td><input type="text" id="txt_sheetid" name="txt_parms"
					size="16" /></td>
				<td>从<input type="text" size="10" id="txt_editdate_min"
					class="Wdate" onFocus="WdatePicker()" name="txt_parms" alt="最小制单日期" />
					到<input type="text" size="10" id="txt_editdate_max" class="Wdate"
					onFocus="WdatePicker()" name="txt_parms" alt="最大制单日期" />
				</td>
			</tr>
		</table>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
			<span id="div_button_new"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>