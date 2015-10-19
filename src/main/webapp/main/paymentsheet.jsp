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
<title>结算单查询</title>

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link rel="stylesheet" href="../css/main.css" type="text/css" />
<style type="text/css">
.aw-column-0 {
	width: 140px;
}

.aw-column-1 {
	width: 50px;
}

.aw-column-2 {
	width: 50px;
}

.aw-column-3 {
	width: 60px;
}

.aw-column-4 {
	width: 60px;
}

.aw-column-5 {
	width: 60px;
}
</style>

<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/MainReportGrid.js"> </script>
<script language="javascript" src="../js/MainSheet.js"></script>
<script language="javascript" src="../js/MainHTML.js"> </script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" type="text/javascript">
	var service = "Paymentsheet";
	var s = new Sheet(service);
	var cmid = window.location.href.getQuery("cmid");

	var BU = new HTML("BU");
	var PayShop = new HTML("PayShop");
	
	window.onload = function(){
		s.init();
		//s.allowPrint();
		s.disabledRead();
		s.disabledConfirm();

		BU_PayShop_init();
	};

	var idx = 0;
	s.endInitGrid = function(){
		//单据号
		var obj_link = new AW.Templates.Link;
		obj_link.setStyle("color:blue;cursor:pointer;");
		obj_link.setEvent( "onclick",
			function(){
				var row = s._grid.getCurrentRow();
				s.offset = row;
				var sheetid = s._grid.getCellValue( 0,row );
				s.open_sheet_detail( sheetid );
			}
		);
		s._grid.setCellTemplate( obj_link, 0 ); 

		//表头打印
		var obj_headPrint = new AW.Templates.Link;
		obj_headPrint.setStyle("color:blue;cursor:pointer;");
		obj_headPrint.setEvent( "onclick",
			function(){
				var row = s._grid.getCurrentRow();
				s.offset = row;
				var sheetid = s._grid.getCellValue( 0,row );
				s.current_sheetid = sheetid;
				headPrint(sheetid);
			}
		);
		idx = s.getColIdx("headprint");
		s._grid.setCellTemplate( obj_headPrint, idx );
		s._grid.setCellText("打印",idx );
		
		//打印
		var obj_print = new AW.Templates.Link;
		obj_print.setStyle("color:blue;cursor:pointer;");
		obj_print.setEvent( "onclick",
			function(){
				var row = s._grid.getCurrentRow();
				s.offset = row;
				var sheetid = s._grid.getCellValue( 0,row );
				s.current_sheetid = sheetid;
				s.open_win_print();
			}
		);

		//导出
		var obj_excel = new AW.Templates.Link;
		obj_excel.setStyle("color:blue;cursor:pointer;");
		obj_excel.setEvent( "onclick",
				function(){
					var row = s._grid.getCurrentRow();
					s.offset = row;
					var sheetid = s._grid.getCellValue( 0,row );
					s.current_sheetid = sheetid;
					excel(sheetid);
				}
			);
		idx = s.getColIdx("excel");
		s._grid.setCellTemplate( obj_excel, idx );
		s._grid.setCellText("导出",idx);

		//收据打印
		var obj_print2 = new AW.Templates.Link;
		obj_print2.setStyle("color:blue;cursor:pointer;");
		obj_print2.setEvent( "onclick",
				function(){
					var row = s._grid.getCurrentRow();
					s.offset = row;
					var sheetid = s._grid.getCellValue( 0,row );
					s.current_sheetid = sheetid;
					sjPrint(sheetid);
				}
			);
		idx = s.getColIdx("print2");
		s._grid.setCellTemplate( obj_print2, idx );
		s._grid.setCellText("打印",idx);

		//开票申请
		var obj_ask = new AW.Templates.Link;
		obj_ask.setStyle("color:blue;cursor:pointer;");
		obj_ask.setEvent( "onclick",
			function(){
				var row = s._grid.getCurrentRow();
				s.offset = row;
				var sheetid = s._grid.getCellValue( 0,row );
				s.current_sheetid = sheetid;
				invoiceAsk(sheetid,row);
			}
		);

		var falgIdx = s.getColIdx("flag");
		var reqflagIdx = s.getColIdx("reqflag");
		var sjflagIdx = s.getColIdx("sjflag");
		var paytimesIdx = s.getColIdx("paytimes");
		var idxPrint = s.getColIdx("print");
		var idxAsk  =  s.getColIdx("ask");
		for ( var i = 0; i < s._table4search.getCount(); i++) {
			var flag=Number(s._grid.getCellValue( falgIdx,i ));
			var reqflag = Number(s._grid.getCellValue( reqflagIdx,i ));
			var sjflag = Number(s._grid.getCellValue( sjflagIdx,i ));
			var paytimes = Number(s._grid.getCellValue( paytimesIdx,i ));
			
			s._grid.setCellText(toFlagName(flag),falgIdx, i);
			s._grid.setCellText(toreqflag(reqflag),reqflagIdx, i);
			s._grid.setCellText(tosjflag(sjflag),sjflagIdx, i);

			if(flag==2){
				s._grid.setCellTemplate( obj_print, idxPrint,i );
				s._grid.setCellText("打印",idxPrint, i);
			}

			if(reqflag==0 && paytimes>0){
				s._grid.setCellTemplate( obj_ask, idxAsk,i );
				s._grid.setCellText("申请",idxAsk, i);
			}
		}
	};
	s.open_win_print = function()
	{	
		var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
			",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
	 	window.open( "./pnprint.jsp?cmid="+this.cmid+"&service="+service+"&sheetid="+this.current_sheetid, this.current_sheetid, attributeOfNewWnd );		
	};

	function excel(sheetid){
		var url = "../DaemonMainDownload?service=Paymentsheet&operation=excelSheet&sheetid="+sheetid;
		window.location.href = url;
	}

	function invoiceAsk(sheetid,row){
		var params = new Array();
		params.push( "sheetid="+sheetid );
		params.push( "operation=invoiceAsk" );
		params.push("service=Paymentsheet");
		var url = "../DaemonMain?" + params.join( "&" );

		setLoading(true,'开票申请提交……');
		_table 	   = new AW.XML.Table;
		_table.setURL(url);
		_table.setTable("xdoc/xout/result");
		_table.setRows("row");
		_table.request();
		_table.response = function(text){
			setLoading(false);
			//alert(text.xml);
			_table.setXML(text);//读取服务器返回的xml信息
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( _table.getErrNote() );
				return;
			}else{
				alert("申请成功");
				s._grid.setCellTemplate( "", 4,row );
				s._grid.setCellText("",4, row);
				var reqflagIdx = s.getColIdx("reqflag");
				s._grid.setCellText("已申请",reqflagIdx, row);
			}
		};
	}

	function sjPrint(sheetid){
		var params = new Array();
		params.push( "sheetid="+sheetid );
		params.push( "operation=setSJPrint" );
		params.push("service=Paymentsheet");
		var url = "../DaemonMain?" + params.join( "&" );

		setLoading(true,'收据打印记录……');
		_table 	   = new AW.XML.Table;
		_table.setURL(url);
		_table.setTable("xdoc/xout/result");
		_table.setRows("row");
		_table.request();
		_table.response = function(text){
			setLoading(false);
			_table.setXML(text);//读取服务器返回的xml信息
			//alert(_table.getXMLContent());
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( _table.getErrNote() );
				return;
			}else{
				var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
				",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
		 		a = window.open( "print.jsp?operation=sjPrint&cmid="+cmid+"&service="+service+"&sheetid="+sheetid, sheetid, attributeOfNewWnd );
		 		a.focus();
			}
		};
	}

	function headPrint(sheetid){
		var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
 		a = window.open( "print.jsp?operation=headPrint&cmid="+cmid+"&service="+service+"&sheetid="+sheetid, sheetid, attributeOfNewWnd );
 		a.focus();
	}
	
	
	function toFlagName(flag){
		switch (Number(flag)) {
		case 1:
			return "新建";
			break;
		case 2:
			return "制单审核";
			break;
		case 3:
			return "发票确认";
			break;
		case 10:
			return "审批";
			break;
		case 20:
			return "已支付";
			break;
		default:
			return "";
			break;
		}
	}

	function toreqflag(flag){
		switch (Number(flag)) {
		case -1:
			return "无需开票";
			break;
		case 0:
			return "未申请";
			break;
		case 1:
			return "已申请";
			break;
		case 2:
			return "已开票";
			break;
		default:
			return "";
			break;
		}
	}
	function tosjflag(flag){
		switch (Number(flag)) {
		case 0:
			return "未打印";
			break;
		case 1:
			return "已打印";
			break;
		default:
			return "";
			break;
		}
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
				<% if(!token.isVender){ %>
				<td>供应商编码</td>
				<td>开票申请状态</td>
				<td>开票申请日期范围</td>
				<% } %>
				<td>BU</td>
				<td>结算主体</td>
				<td>结算状态</td>
				<td>发票状态</td>
				<td>单据号(可选)</td>
				<td>计划付款日期(可选)</td>
			</tr>
			<tr>
				<% if(!token.isVender){ %>
				<td><input type="text" id="txt_venderid" name="txt_parms"
					size="16" split="," alt="供应商编码" /></td>
				<td><select name="txt_parms" id="txt_reqflag">
						<option value="">全部</option>
						<option value="0">未申请</option>
						<option value="1">已申请</option>
				</select></td>
				<td>从<input type="text" id="txt_reqdate_min" size="14"
					class="Wdate" onFocus="WdatePicker()" name="txt_parms"
					alt="最小发票申请日期" /> 到<input type="text" id="txt_reqdate_max"
					size="14" class="Wdate" onFocus="WdatePicker()" name="txt_parms"
					alt="最大发票申请日期" />
				</td>
				<% } %>
				<td><span id="span_buid"></span></td>
				<td><span id="span_payshop"></span></td>
				<td><select name="txt_parms" id="txt_flag">
						<option value="">全部</option>
						<option value="1">新建</option>
						<option value="2">制单审核</option>
						<option value="3">发票确认</option>
						<option value="10">审批</option>
						<option value="20">已支付</option>
				</select></td>
				<td><select name="txt_parms" id="txt_invoiceflag">
						<option value="">全部</option>
						<option value="0">待处理</option>
						<option value="1">已收票</option>
						<option value="2">已审核</option>
						<option value="3">已确认</option>
						<option value="4">已认证</option>
				</select></td>
				<td><input type="text" id="txt_sheetid" name="txt_parms"
					size="16" /></td>
				<td>从<input type="text" id="txt_date_min" size="14"
					class="Wdate" onFocus="WdatePicker()" name="txt_parms"
					alt="最小计划付款日期" /> 到<input type="text" id="txt_date_max" size="14"
					class="Wdate" onFocus="WdatePicker()" name="txt_parms"
					alt="最大计划付款日期" />
				</td>
			</tr>
		</table>
		<div class="search_button">
			<span id="div_button_search"></span> <span id="div_button_excel"></span>
		</div>
	</div>
	<div id="divReportGrid"></div>
	<div id="divFooter"></div>
</body>
</html>