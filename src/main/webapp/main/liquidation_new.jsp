<%@page contentType="text/html;charset=UTF-8" session="false"%>
<%@include file="../include/common.jsp"%>
<%@include file="../include/token.jsp"%>
<%@include file="../include/permission.jsp"%>
<%
    //用户的查询权限.
    moduleid = 3020305;
	token.checkPermission(moduleid,Permission.READ);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>对账申请单新建</title>

<!-- ActiveWidgets stylesheet and scripts -->
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="../css/main.css" rel="stylesheet" type="text/css"></link>

<style type="text/css">
.aw-grid-control {
	height: 65%;
	width: 98%;
	border: none;
}

.aw-grid-cell {
	border-right: 1px solid threedshadow;
}

.aw-grid-row {
	border-bottom: 1px solid threedlightshadow;
}

.aw-column-0 {
	width: 40px;
}

.aw-column-1 {
	width: 50px;
}

.aw-column-2 {
	width: 60px;
}

.aw-column-3 {
	width: 140px;
}

.aw-column-4 {
	width: 100px;
	text-align: right;
	color: blue;
}

.aw-grid-control .aw-rows-selected {
	color: #000;
	background: #cee
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
</style>

<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"></script>
<script language="javascript" src="../js/date/WdatePicker.js"></script>
<script language="javascript" src="../js/MainReportGrid.js"> </script>
<script language="javascript" src="../js/MainSheet.js"></script>
<script language="javascript" src="../js/MainHTML.js"> </script>
<script language="javascript" src="../js/common.js"> </script>
<script language="javascript" type="text/javascript">
	var service = 'Liquidation';
	var cmid   = window.location.href.getQuery("cmid");

	var BU = new HTML("BU");
	var PayShop = new HTML("PayShop");

	var g_isMajorVender;
	var g_majorid;
	var g_hqmajorid;
	var g_majorname;
	var g_buid;
	var g_payshopid;
	var g_payshopname;
	var g_maxamt=0;
	var g_minamt=0;
	var g_contracttype;
	var g_commitamt=0;
	var g_selectedamt = 0;
	var _tag_grp = new AW.UI.Tabs;
	var _table4search;
	var _grid_u = new AW.UI.Grid;
	var _grid_s = new AW.UI.Grid;

	var g_enabaleselectedamt = true;
	
	var _array_u = []; //待勾选单据数组
	var _array_s = []; //已勾选单据数组

	var _columnNames = ["勾选", "BUID", "单据类型","单据号","结算金额","业务日期","应结日期","备注" ];
	var _columns = ["checkbox","buid","sheettype","sheetid","unpaidamt","docdate","duedate","note"];
	
	window.onload = function(){
		init();
	};
	
	function init(){
		var money = new AW.Formats.Number;
		money.setTextFormat( "#,##0.00" );
		var str = new AW.Formats.String;
		var fmt = [str,str,str,str,money,str,str];
		
		_grid_u.setId( "unselectedList" );
		_grid_u.setColumnCount( _columnNames.length );
		_grid_u.setHeaderText(_columnNames);
		_grid_u.setSelectionMode("multi-row-marker");
		_grid_u.onSelectedRowsChanged = function(ids){
		};
		_grid_u.setSelectorVisible(true);
		_grid_u.setSelectorWidth(40);
		_grid_u.setSelectorText(function(i){return this.getRowPosition(i)+1;});
		_grid_u.setFooterVisible(true);
		_grid_u.setCellFormat(fmt);
		
		_grid_s.setId( "selectedList" );
		_grid_s.setColumnCount( _columnNames.length );
		_grid_s.setHeaderText(_columnNames);
		_grid_s.setSelectionMode("multi-row-marker");
		_grid_s.setSelectorVisible(true);
		_grid_s.setSelectorWidth(40);
		_grid_s.setSelectorText(function(i){return this.getRowPosition(i)+1;});
		_grid_s.setFooterVisible(true);
		_grid_s.setCellFormat(fmt);

		var isclicked = false;
		_grid_u.onRowSelectedChanged = function(selected, index){
			if(_array_u.length>0){
				if(g_enabaleselectedamt){
					if(isclicked){
						if(_array_u.length>index){
							if(selected){
								g_selectedamt += Number(_array_u[index][4]);
							}else{
								g_selectedamt -= Number(_array_u[index][4]);
							}
							setSelectedAmtMessage();
						}
						isclicked=false;
					}else{
						isclicked=true;
					}
				}
			}
		};
		
		majorid();

		var params = ['attribute={"id":"txt_buid0","name":"txt_parms","onchange":"BU.onload()"}',''];
		BU.toHTML("span_buid",params);
		BU.onload = function(){
			var buid = $("txt_buid0").value;
			params = ['attribute={"id":"txt_payshopid0","name":"txt_parms","onchange":"getLQCount()"}',"buid="+buid];
			PayShop.toHTML("span_payshop",params);
		};
		PayShop.onload = function(){
			getLQCount();
		};
	};
	
	function getLQCount(buid,payshopid){
		var buid = $("txt_buid0").value;
		var payshopid = $("txt_payshopid0").value;
		
		//获取可对账次数
		var params = [];
		params.push("operation=getVenderLQCount");
		params.push("buid="+buid);
		params.push("payshopid="+payshopid);
		params.push("service=LQConfig");
		var url = "../DaemonMain?"+params.join("&");
		setLoading(true,'正在查询可对账次数……');
		var _table 	   = new AW.XML.Table;
		_table.setURL(url);
		_table.setTable("xdoc/xout/result");
		_table.setRows("row");
		_table.request();
		_table.response = function(text){
			setLoading(false);
			//alert(_table.getXMLContent(text));
			_table.setXML(text);//读取服务器返回的xml信息
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( _table.getErrNote() );
				return;
			}
			var count = Number(_table.getXMLText("xdoc/xout/result"));
			var msg = "";
			if(count<=0){
				$('btn_setup0').style.display="none";
				msg = "贵司本月当前对账次数剩余0次，暂不能提交对账申请，请于次月1日再提交或联系对账员!";
			}else if(count == 999){
				$('btn_setup0').style.display="block";
			}else{
				$('btn_setup0').style.display="block";
				msg = "贵司本月当前对账次数剩余 <span style='font-size:26px;'>"+count+"</span> 次！";
			}
			$('msg_setup0').innerHTML=msg;
		};
	}

	function setSelectedAmtMessage(){
		$('selectedamt').innerText=financialNum.dataToText(g_selectedamt);
	}


	//确认是否预付款供应商，需录入课类编码
	function majorid(){
		var params = [];
		params.push("operation=isMajorVender");
		params.push("payshopid="+g_payshopid);
		params.push("service="+service);
		var url = "../DaemonMain?"+params.join("&");

		setLoading(true,'正在查询是否预付款……');
		var _table 	   = new AW.XML.Table;
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
			g_isMajorVender = _table.getXMLText("xdoc/xout/result/isMajorVender");
			if(g_isMajorVender=='true'){
				$("setup01").style.display="block";
			}
		};
	}

	//选择结算主体
	function setup0(){
		var opt = $("txt_payshopid0").options;
		for ( var i = 0; i < opt.length; i++) {
			var o = opt[i];
			if(o.selected){
				if(o.value==""){
					alert("请选择一个结算主体");
					return;
				}
				g_payshopid = o.value;
				g_payshopname = o.getAttribute("alt");
				g_buid  = o.getAttribute("buid");
				break;
			}
		}
		//检查是否输入课类
		if(g_isMajorVender=='true'){
			if(g_majorid==''){
				alert("您是预付款供应商，必须录入本次对账课类编码方可继续。");
				e.value="";
				return;
			}
		}else{
			g_majorid = -1;
			g_hqmajorid = -1;
			g_majorname = "无课类";
		}
		checkPayshopStatus();
	}
	
	//检查payshop是否可用，未冻结
	function checkPayshopStatus(){
		var params = [];
		params.push("operation=checkPayshopStatus");
		params.push("payshopid="+g_payshopid);
		params.push("service="+service);
		var url = "../DaemonMain?"+params.join("&");

		setLoading(true,'正在查询结算主体状态……');
		var _table 	   = new AW.XML.Table;
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
			var isHas = _table.getXMLText("xdoc/xout/result/isHas");
			var isFreeze = _table.getXMLText("xdoc/xout/result/isFreeze");
			var isProssing = _table.getXMLText("xdoc/xout/result/isProssing");
			var lqSheetid = _table.getXMLText("xdoc/xout/result/lqSheetid");
			var pnSheetid = _table.getXMLText("xdoc/xout/result/pnSheetid");
			g_maxamt = Number(_table.getXMLText("xdoc/xout/result/maxAmt"));
			g_minamt = Number(_table.getXMLText("xdoc/xout/result/minAmt"));
			g_contracttype = Number(_table.getXMLText("xdoc/xout/result/contracttype"));
			var payableAmt = Number(_table.getXMLText("xdoc/xout/result/payableAmt"));

			if(g_contracttype!=1){
				$("txt_maxmemo").innerText = "建议最高勾单金额";
			}
			
			if(isHas=='false'){
				alert("该结算主体无业务发生，请退出！");
				return;
			}
			
			if(isFreeze=='true'){
				alert("该结算主体暂停结算，请与采购联系！");
				return;
			}
			
			if(payableAmt<0){
				alert("该结算主体余额不足，暂停结算！");
				return;
			}
			
			if(isProssing=='true'){
				if(lqSheetid!=''){
					alert("您已在该结算主体提交对账申请，单号："+lqSheetid+"处理后才能再次对账，请稍后再提交！");
				}
				if(pnSheetid!=''){
					alert("该结算主体存在新建状态结算单"+pnSheetid+"，请联系对账员处理后才能再次对账！");
				}
				return;
			}
			
			if(g_maxamt<0 || g_minamt>g_maxamt){
				alert("该结算主体余额异常，暂停对账。请与对账员联系！");
				return;
			}
			
			setup1();
			//锁定payshopid查询条件
			$('txt_buid').value=g_buid;
			$('txt_payshopid').value=g_payshopid;
			$('txt_payshopname').value=g_payshopname;

			$('txt_majorid').value=g_majorid;
			$('txt_hqmajorid').value=g_hqmajorid;
			$('txt_majorname').value=g_majorname;
			//设置可勾单金额提示
			setAmtMessage();
		};
	}
	//选择单据
	function setup1(){
		$("setup0").style.display="none";
		$("setup1").style.display="block";
		var _btn_search = new AW.UI.Button;
		_btn_search.setControlText( "查询" );
		_btn_search.setId( "btnsearch" );
		_btn_search.setControlImage( "search" );	
		_btn_search.onClick = function(){search_sheet();};
		$('div_button_search').innerHTML=_btn_search;

		var _btn_excel = new AW.UI.Button;
		_btn_excel.setControlText( "导出" );
		_btn_excel.setId( "btnexcel" );
		_btn_excel.setControlImage( "excel" );	
		_btn_excel.onClick = function(){excel();};
		$('div_button_excel').innerHTML=_btn_excel;
		
		intTags();
	}

	function setAmtMessage(){
		$('txt_maxamt').innerText=financialNum.dataToText(g_maxamt);
		$('txt_minamt').innerText=financialNum.dataToText(g_minamt);
		$('txt_selectedamt').innerText=financialNum.dataToText(g_commitamt);
		g_selectedamt=0;
		setSelectedAmtMessage();
	}

	function intTags(){
		var tags = [ "可勾选单据", "已勾选单据","批量导入" ];
		
		_tag_grp.setId( "tag_grp" );
		_tag_grp.setItemText(tags);
		_tag_grp.setItemCount(tags.length );
		_tag_grp.setSelectedItems( [0] );
		_tag_grp.onSelectedItemsChanged = function( idx ) {
			if(idx==0){
				$('div_unselectedList').style.display='block';
				$('div_selectedList').style.display='none';
				$('div_upload').style.display='none';
			}else if(idx==1){
				$('div_selectedList').style.display='block';
				$('div_unselectedList').style.display='none';
				$('div_upload').style.display='none';
			}else{
				$('div_selectedList').style.display='none';
				$('div_unselectedList').style.display='none';
				$('div_upload').style.display='block';
			}
		};
		$('div_tabs').innerHTML=_tag_grp.toString();
	}

	//加载可勾选单据
	function search_sheet(){
		var params = autoParms();
		params.push("service="+service);
		params.push("operation=getCanSelectedSheet");
		var url = "../DaemonMain?"+params.join("&");
		//alert(url);
		_table4search = new AW.XML.Table;
		_table4search.setURL( url );
		_table4search.request();
		_table4search.response 	= function(text){
			//alert(text.xml);
			setLoading(false);
			_tag_grp.setSelectedItems( [0] );
			_table4search.setTable("xdoc/xout/rowset");
			_table4search.setRows("row");
			_table4search.setXML( text );
			if( _table4search.getErrCode() != 0 ){	//处理xml中的错误消息
				alert(_table4search.getErrNote()) ;
				return;
			}
			initArrayU();
			show_catalogue();
		};
		setLoading( true );
	}

	function excel(){
		var params = autoParms();
		params.push("service="+service);
		params.push("operation=excelCanSelectedSheetFilter");
		var url = "../DaemonMainDownload?"+params.join("&");
		window.location.href = url;
	}

	//将xml数据转换为array数组
	function initArrayU(){
		_array_u = [];
		for(var i=0; i<_table4search.getCount(); i++) {
			var arr = [];
			arr.push("");
			for ( var j = 0; j < _columns.length; j++) {
				arr.push(_table4search.getData(j,i));
			}
			_array_u.push(arr);
		}
	}
	function show_catalogue(){
		var row_count = _table4search.getCount();
		if(row_count==0){$("div_unselectedList").innerHTML ="没有符合条件的数据";return;}
		
		
		//_table4search.setColumns(_columns);
		_grid_u.setRowCount( row_count );
		
		//_grid_u.setCellModel(_table4search);
		_grid_u.setSelectedRows([]);
		_grid_u.setCellData(_array_u);

		_grid_u.setFooterText(['合计:','','','',financialNum.dataToText(amtsum(_array_u)),'']);
		
		$("div_unselectedGrid").innerHTML = _grid_u.toString();
		_grid_u.refresh();
	}

	//确认勾选，移动到已勾选表格
	function gridUOK(){
		//获取已勾选的索引
		var tmp_a =  _grid_u.getSelectedRows();
		//必须从大到小排序。
		tmp_a.sort(sortNumber).reverse();
		var tmp_b = [];
		tmp_b = tmp_b.concat(tmp_a);
		
		//已选数据重复性检查,插入到_array_s
		for ( var i = 0;  i < _array_s.length; i++) {
			for ( var j = 0; j < tmp_a.length; j++) {
				var key_u = _array_u[tmp_a[j]][1]+"|"+_array_u[tmp_a[j]][2]+"|"+_array_u[tmp_a[j]][3];
				var key_s = _array_s[i][1]+"|"+_array_s[i][2]+"|"+_array_s[i][3];
				if(key_u == key_s){
					//从tmp_a中剔除
					tmp_a.splice(j,1);
					break;
				}
			}
		}
		//将勾选的数据添加到已勾选数组
		for ( var i = (tmp_a.length-1); i >= 0; i--) {
			_array_s.push(_array_u[tmp_a[i]]);
		}

		//将待勾选数组删除已勾选
		for ( var i = 0; i < tmp_b.length; i++) {
			_array_u.splice(tmp_b[i],1);
		}

		//刷新表格
		_grid_u.setSelectedRows([]);
		_grid_u.setRowCount( _array_u.length );
		_grid_u.setCellData(_array_u);
		_grid_u.setFooterText(['合计:','','','',financialNum.dataToText(amtsum(_array_u)),'']);
		

		_grid_s.setSelectedRows([]);
		_grid_s.setRowCount( _array_s.length );
		_grid_s.setCellData(_array_s);
		var amt = amtsum(_array_s);
		_grid_s.setFooterText(['合计:','','','',financialNum.dataToText(amt),'']);
		

		$("div_unselectedGrid").innerHTML = _grid_u.toString();
		$("div_selectedGrid").innerHTML   = _grid_s.toString();
		_grid_s.refresh();
		_grid_u.refresh();
		
		_tag_grp.setSelectedItems( [1] );

		//已选单据值
		g_commitamt = amt;
		setAmtMessage();
	}

	function amtsum(arr){
		var sum = 0;
		for ( var i = 0; i < arr.length; i++) {
			sum += Number(arr[i][4]);
		}
		return sum;
	}
	function gridAll(){
		g_enabaleselectedamt=false;
		_grid_u.setSelectedRows(function selectAll() {a=new Array; for(i=0;i<_grid_u._rowCount;i++) a.push(i); return a;});
		g_enabaleselectedamt=true;
		cookSelectedamt();
	}
	
	function gridUnAll(){
		g_enabaleselectedamt=false;
		var b =  _grid_u.getSelectedRows();
		_grid_u.setSelectedRows(function UnSelectAll() {
			var a=new Array; 
			for(i=0;i<_grid_u._rowCount;i++){
				a.push(i);
				for(j=0;j<b.length;j++) {
					if(b[j]==i){
						a.pop();
					}
				}
			} 
			return a;
		});
		g_enabaleselectedamt=true;
		cookSelectedamt();
	}

	function cookSelectedamt(){
		var tmp_a =  _grid_u.getSelectedRows();
		g_selectedamt = 0;
		for(var i=0;i<tmp_a.length;i++){
			g_selectedamt += Number(_array_u[tmp_a[i]][4]);
		}
		setSelectedAmtMessage();
	}
	
	function SgridAll(){
		_grid_s.setSelectedRows(function selectAll() {a=new Array; for(i=0;i<_grid_s._rowCount;i++) a.push(i); return a;});
	}
	
	function SgridUnAll(){
		var b =  _grid_s.getSelectedRows();
		_grid_s.setSelectedRows(function UnSelectAll() {
			var a=new Array; 
			for(i=0;i<_grid_s._rowCount;i++){
				a.push(i);
				for(j=0;j<b.length;j++) {
					if(b[j]==i){
						a.pop();
					}
				}
			} 
			return a;
		});
	}
	

	function sortNumber(a,b)
	{
		return a - b;
	}
	
	function gridReturn(){
		//获取已勾选的索引
		var tmp_a =  _grid_s.getSelectedRows();
		//必须从大到小排序。
		tmp_a.sort(sortNumber).reverse();
		var tmp_b = [];
		tmp_b = tmp_b.concat(tmp_a);
		
		//将勾选的数据添加到待选数组
		for ( var i = 0; i < tmp_a.length; i++) {
			_array_u.push(_array_s[tmp_a[i]]);
		}
		//将已勾选数组删除已勾选
		for ( var i = 0; i < tmp_b.length; i++) {
			_array_s.splice(tmp_b[i],1);
		}

		//刷新表格
		_grid_u.setSelectedRows([]);
		_grid_u.setRowCount( _array_u.length );
		_grid_u.setCellData(_array_u);
		_grid_u.setFooterText(['合计:','','','',financialNum.dataToText(amtsum(_array_u)),'']);
		

		_grid_s.setSelectedRows([]);
		_grid_s.setRowCount( _array_s.length );
		_grid_s.setCellData(_array_s);
		var sum = amtsum(_array_s);
		_grid_s.setFooterText(['合计:','','','',financialNum.dataToText(sum),'']);
		

		$("div_unselectedGrid").innerHTML = _grid_u.toString();
		$("div_selectedGrid").innerHTML   = _grid_s.toString();
		_grid_s.refresh();
		_grid_u.refresh();
		
		_tag_grp.setSelectedItems( [1] );

		//已选单据值
		g_commitamt = sum;
		setAmtMessage();
	}


	function gridCommit(){
		//校验勾选金额 g_maxamt,g_minamt,g_commitamt
		//精度强制为2位小数
		g_commitamt = Number(g_commitamt.toFixed(2));
		if(g_commitamt>g_maxamt){
			alert("本次对账最高限额："+financialNum.dataToText(g_maxamt)+"，实际提交金额"+financialNum.dataToText(g_commitamt)+"\r\n勾选的单据过高，请去掉部分单据！");
			return;
		}
		
		if(g_commitamt<g_minamt){
			alert("本次对账最低限额："+financialNum.dataToText(g_minamt)+"，实际提交金额"+financialNum.dataToText(g_commitamt)+"\r\n勾选的单据金额不足，请尝试增加勾选单据");
			return;
		}
		
		if(!confirm("本次共勾选[ "+financialNum.dataToText(g_commitamt)+" ]金额结算,请确认。")){
			return;
		}
		var params = [];
		params.push("service="+service);
		params.push("operation=newSheet");
		params.push("payshopid="+g_payshopid);
		params.push("buid="+g_buid);
		params.push("majorid="+g_majorid);
		params.push("hqmajorid="+g_hqmajorid);
		var url = "../DaemonMain?"+params.join("&");
		
		var x = arrayToXML(_array_s,_columns);
		_table = new AW.XML.Table;
		_table.setRequestMethod('POST');
		_table.setRequestData(_table.getXMLContent(x));
		_table.setURL( url );
		_table.request();
		_table.response = function(text){
			//alert(text.xml);
			setLoading(false);
			_table.setXML( text );
			if( _table.getErrCode() != 0 ){	//处理xml中的错误消息
				alert(_table.getErrNote()) ;
				redo();
				return;
			}
			var sheetid = _table.getXMLText("xdoc/xout/result/sheetid");
			alert("对账申请单据："+sheetid+"已生成，30分钟后可以查询对账结果。");
			window.close();
	 		//window.location.href = "print.jsp?cmid="+cmid+"&service="+service+"&sheetid="+sheetid;
			/*
			var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
			",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
	 		a = window.open( "print.jsp?cmid="+cmid+"&service="+service+"&sheetid="+sheetid, sheetid, attributeOfNewWnd );
	 		a.focus();
	 		window.close();
	 		*/
		};
		setLoading( true ,'正在提交数据，请稍后……');
	}

	function load_major(e){
		$('majorname').innerHTML = "";
		if( "" == e.value ) return false;
		
		var params = new Array();
		params.push( "categoryid="+e.value );
		params.push( "operation=getCategoryByVenderID" );
		params.push( "service=Category" );
		var url = "../DaemonMain?" + params.join( "&" );
		var _table = new AW.XML.Table;
		_table.setURL(url);
		_table.request();
		_table.response = function(text){
			setLoading(false);
			//alert(text.xml);
			_table.setXML(text);//读取服务器返回的xml信息
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( _table.getErrNote() );
				g_majorid="";
				g_hqmajorid="";
				g_majorname="";
				e.value="";
				return;
			}
			g_majorid = _table.getXMLText("xdoc/xout/result/row/categoryid");
			g_hqmajorid = _table.getXMLText("xdoc/xout/result/row/hqcategoryid");
			g_majorname = _table.getXMLText("xdoc/xout/result/row/categoryname");
			$('majorname').innerHTML = g_majorname;
		};
		setLoading(true);
	}

	function validate(){
		if($("txt_source").value == ""){
			alert( "没有数据,请录入" );
			return;
		}
		var arr_name =  new Array ( 'buid','sheettype','sheetid' );
		var _array = text2Array(arr_name);
		if(_array.length==0) return;
		var x = arrayToXML(_array,arr_name);
		$("divExcel").style.display = "none";

		var params = new Array();
		params.push("payshopid="+g_payshopid);
		params.push("buid="+g_buid);
		params.push("majorid="+g_majorid);
		params.push("hqmajorid="+g_hqmajorid);
		params.push( "operation=batchCheck" );
		params.push( "service="+service );
		var url = "../DaemonMain?" + params.join( "&" );
		
		_table = new AW.XML.Table;
		_table.setTable("/xdoc/xout/rowset");
		_table.setRows("row");
		_table.setRequestMethod('POST');
		_table.setRequestData(_table.getXMLContent(x));
		_table.setURL( url );
		_table.request();
		_table.response = function(text){
			//alert(text.xml);
			setLoading(false);
			_table.setXML( text );
			var htmlOut = _table.transformNode("../xsl/liquidation_validate.xsl");
			var code = _table.getXMLText("xdoc/xout/rowset/result");
			if( code != 'OK' ){	//处理xml中的错误消息
				htmlOut += "<br><input type='button' value='重新提交数据' onclick=\"redo()\"/>";
				$("divResult").innerHTML ="";
	            $("divResult").style.display = 'block';
	            $("divExcel").style.display = 'none';
			}else{
				htmlOut += "<br><input type='button' value='提交对账' onclick=\"uploadcommit()\" id=\"btn_save\" class=\"button\" /> <input type='button' value='重新提交数据' onclick=\"redo()\" class=\"button\"/>";
	            $("divResult").style.display = 'block';
	            $("divExcel").style.display = 'none';
			}
			$("divResult").innerHTML = "<br/>"+htmlOut;

			window.scrollBy(0,window.screen.availHeight);
		};
		setLoading(true);
		
	}
	function uploadcommit(){
		//将数据加载到数组 _array_s
		
		//清空数组
		_array_s = [];
		var sumamt = 0;
		var nodes = _table.getXMLNodes( "/xdoc/xout/rowset/row" );
		for ( var i = 0; i < nodes.length; i++) {
			var node = nodes[i];
			var buid= _table.getXMLText("buid",node);
			var sheetid= _table.getXMLText("sheetid",node);
			var sheettype= _table.getXMLText("sheettype",node);
			var unpaidamt= _table.getXMLText("unpaidamt",node);
			
			var u = [];
			u.push("");
			u.push(buid);
			u.push(sheettype);
			u.push(sheetid);
			u.push(unpaidamt);
			u.push("");
			u.push("");
			_array_s.push(u);
			sumamt += Number(unpaidamt);
		}
		//合计金额用作验证金额范围
		g_commitamt = sumamt;
		gridCommit();
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
		var doc = table._xml;
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

	function text2Array(arr_name){
		var str=$("txt_source").value;
		return _upload_txt_to_matrix(str,arr_name.length,5000);
	}
	
</script>
</head>

<body>
	<div id="divTitle">对账申请单新建</div>

	<div id="setup0" style="display: block;">
		<h3 style="display: none;" id="setup01">
			您是预付款供应商，请先输入本次对账课类编码：<input type="text" size="6"
				onchange="load_major(this)" /><span id="majorname"></span>
		</h3>
		<h3>
			选择结算区域：<span id="span_buid"></span>
		</h3>
		<h3>
			选择结算主体：<span id="span_payshop"></span>
		</h3>
		<input type="button" id="btn_setup0"
			value="下&nbsp;&nbsp;一&nbsp;&nbsp;步" onclick="setup0()" class="button"
			style="display: none;"></input>
		<div id="msg_setup0"
			style="font-size: 16px; color: #630; font-weight: bold;"></div>
		<hr style="" />
		<div style="font-size: 16px; color: #330; font-weight: bold;">
			提示：<br /> 为了规范和引导供应商的对账结算工作，我司拟根据贵我双方合同约定的结算方式实行必要的、合理的每月对账提交次数的限制。<br />
			若贵司合同结算方式属于月结型的，则贵司每月对账提交次数为1次；若属于日结型的，则每月对账提交次数为4次（若当前对账次数默认为1次，可联系对账员进行修正）。谢谢！<br />
		</div>
	</div>
	<div id="setup1" style="display: none;">
		<div style="display: none;">
			提示：&nbsp;&nbsp; <span id="txt_maxmemo">最高勾单金额</span>：<span
				id="txt_maxamt" style="font-weight: bold; color: blue;"></span>&nbsp;&nbsp;&nbsp;&nbsp;
			<span id="txt_minmemo">最低勾单金额</span>：<span id="txt_minamt"
				style="font-weight: bold; color: blue;"></span>&nbsp;&nbsp;&nbsp;&nbsp;
			已勾选金额：<span id="txt_selectedamt"
				style="font-weight: bold; color: blue;"></span>
		</div>
		<div id="div0" class="search_main">
			<table cellspacing="1" cellpadding="2">
				<tr class="search_main_head">
					<td>结算主体</td>
					<td>课类</td>
					<td>单据号(可选，逗号分割)</td>
					<td>发生日期(可选)</td>
				</tr>
				<tr>
					<td><input type="text" id="txt_buid" name="txt_parms" size="1"
						readonly="readonly" /><input type="text" id="txt_payshopid"
						name="txt_parms" size="4" readonly="readonly" /><input
						type="text" id="txt_payshopname" name="txt_parms" size="10"
						readonly="readonly" /></td>
					<td><input type="text" id="txt_majorid" name="txt_parms"
						size="1" readonly="readonly" /><input type="text"
						id="txt_majorname" name="txt_parms" size="10" readonly="readonly" /><input
						type="hidden" id="txt_hqmajorid" name="txt_parms" size="1"
						readonly="readonly" /></td>
					<td><input type="text" id="txt_sheetid" name="txt_parms"
						split="," size="20" /></td>
					<td>从<input type="text" size="14" id="txt_docdate_min"
						class="Wdate" onFocus="WdatePicker()" name="txt_parms"
						alt="最小发生日期" /> 到<input type="text" size="14"
						id="txt_docdate_max" class="Wdate" onFocus="WdatePicker()"
						name="txt_parms" alt="最小发生日期" />
					</td>
				</tr>
			</table>
			<div class="search_button">
				<span id="div_button_search"></span> <span id="div_button_excel"></span>
			</div>
		</div>
		<div id="div_tabs" style="width: 100%;"></div>
		<div id="div_unselectedList" style="display: none;">
			<input type="button" onclick="gridAll()"
				value="全&nbsp;&nbsp;&nbsp;&nbsp;选" class="button"></input> <input
				type="button" onclick="gridUnAll()"
				value="反&nbsp;&nbsp;&nbsp;&nbsp;选" class="button"></input> <input
				type="button" onclick="gridUOK()" value="确&nbsp;&nbsp;&nbsp;&nbsp;认"
				class="button"></input> <span id="selectedamt"></span>
			<div id="div_unselectedGrid"></div>
		</div>
		<div id="div_selectedList" style="display: none;">
			<input type="button" onclick="SgridAll()"
				value="全&nbsp;&nbsp;&nbsp;&nbsp;选" class="button"></input> <input
				type="button" onclick="SgridUnAll()"
				value="反&nbsp;&nbsp;&nbsp;&nbsp;选" class="button"></input> <input
				type="button" onclick="gridReturn()" value="删除勾选" class="buttonred"></input>
			<input type="button" onclick="gridCommit()" value="对账提交"
				class="button"></input>
			<div id="div_selectedGrid"></div>
		</div>
		<div id="div_upload" style="display: none;">
			<div id="divExcel">
				&nbsp;&nbsp;&nbsp;&nbsp;BUID&nbsp;&nbsp;&nbsp;&nbsp;|
				&nbsp;&nbsp;&nbsp;&nbsp;单据类型&nbsp;&nbsp;&nbsp;&nbsp;|
				&nbsp;&nbsp;&nbsp;&nbsp;单据编码&nbsp;&nbsp;&nbsp;&nbsp;<br />
				<textarea rows="10" cols="80" id="txt_source" name="txt_source"></textarea>
				<br /> <input type="button" onclick="validate()" value="校验数据"
					class="button"></input> <br /> 注意：1、单据号中的字母全部为大写，请填写正确，否则不能通过系统验证。
				<br></br> 2、每次导入最多5000行数据。
			</div>
			<div id="divResult"></div>
		</div>

	</div>
</body>
</html>