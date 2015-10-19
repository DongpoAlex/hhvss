var _table4search;
var stopdate;
var ordertime;
var isyesps;
var orderkfts;
var vendtype= "";
var g_dzsupply = 0;
var g_yssupply = 0;
var g_supply = 0;
var g_pkgnum = 0;
var g_skunum = 0;
var _array_u = [];
var _columnNames = ["勾单","订单号","订货日期","送货期","有效期(天)","物流模式","订货审批单号","截至日期","SKU数","商品箱数","送货门店编码" ];
var _columns = ["checkbox","sheetid","orderdate","vdeliverdate","validdays","logistics","refsheetid","deadline","qty","pkgqty","destshopid"];
var _grid = new AW.UI.Grid;
window.onload=function(){
	_grid.setId( "selectedList" );
	_grid.setColumnCount( _columnNames.length );
	_grid.setHeaderText(_columnNames);
	_grid.setSelectionMode("multi-row-marker");
	_grid.onSelectedRowsChanged = function(ids){
	};
	_grid.setSelectorVisible(true);
	_grid.setSelectorWidth(40);
	_grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
	
	_grid.onSelectedRowsChanged = function(idxs){
		var pkg = 0;
		var sku = 0;
		for ( var i = 0; i < idxs.length; i++) {
			//箱数 SKU
			sku += Number(_grid.getCellValue(8, idxs[i]));
			pkg += Number(_grid.getCellValue(9, idxs[i]));
		}
		$("txt_pkgnum").value= pkg;
	};
	
	_grid.onCellClicked = function(event, column, row) {
		var po_no = _grid.getCellValue(1, row);
		var logistics = _grid.getCellValue(5, row);
		var refsheetid = _grid.getCellValue(6, row);
		if (column == 1) {
			if(logistics=='直送'){
				window.open("./purchase.jsp?sheetid=" + po_no);
			}else{
				window.open("./purchasechk.jsp?sheetid=" + refsheetid);
			}
		}
	};
};

function getOrderTime(){
	var dccode = $('txt_dccode').value;
	var logistics = $('txt_logistics').value;
	$('txt_request_date').value="";
	setLoading(true);
	var parms = new Array();
	parms.push("action=selnetordertime");
	parms.push( "dccode="+dccode );
	parms.push( "logistics="+logistics );
	var url = "../DaemonNetOrder?" + parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text) {
		setLoading(false);
		table.setXML(text);
		var xcode = table.getErrCode();
		if (xcode != '0') {
			alert(xcode + table.getErrNote());
		} else {
			var nodes = table.getXML().selectNodes("/xdoc/xout/select/option");
			var select =  document.getElementById("txt_time");
			select.length=0;
			for(var index=0; index<nodes.length; index++) {
				var ctid = nodes[index].getAttribute('value');
				var ctname = nodes[index].getAttribute('ctname');
				var option = new Option(ctname,ctid);
				select.options.add(option);
			}
			$('txt_time').value="";
			
			//设置可预约日期
			var minDate = table.getXMLText("/xdoc/xout/dateList/minDate");
			var maxDate = table.getXMLText("/xdoc/xout/dateList/maxDate");
			
			var expDates = null;
			var nodes = table.getXMLNodes("/xdoc/xout/dateList/expDate");
			if(nodes.length > 0 ){
				expDates = [];
				for ( var i = 0; i < nodes.length; i++) {
					expDates.push(table.getNodeText(nodes[i]));
				}
			}
			
			nodes =  table.getXMLNodes("/xdoc/xout/dateList/disabledDays");
			var disabledDays =null;
			if(nodes.length > 0 ){
				disabledDays = [];
				for ( var i = 0; i < nodes.length; i++) {
					disabledDays.push(table.getNodeText(nodes[i]));
				}
			}
			
			$('txt_request_date').onfocus = function(){
				WdatePicker({minDate:minDate,maxDate:maxDate,disabledDays:disabledDays,disabledDates:expDates});
			};
		}
	};
}
//清除输入数据
function initText(){
	txt_dccode.value="";
	txt_logistics.value="";
	txt_request_date.value="";
	txt_time.value="";
	txt_note.value="";
	$('sp_venderlist').innerText='';
	$("div_poitem").innerHTML="";
}
//查看有效订单
function searchpo(){
	
	var dccode = $('txt_dccode').value;
	if (dccode == "" ) {
		alert("DC编码必须填写！");
		$("txt_dccode").focus();
		return;
	}
	
	var logistics = $('txt_logistics').value;
	if (logistics == "" ) {
		alert("物流方式必须填写！");
		$("txt_logistics").focus();
		return;
	}
	var request_date = $('txt_request_date').value;
	if (request_date == "" ) {
		alert("预约日期必须填写！");
		$("txt_request_date").focus();
		return;
	}

	var time = $('txt_time').value;
	if (time == "" ) {
		alert("预约时间段必须填写！");
		$("txt_time").focus();
		return;
	}
	
	var params = autoParms();
	params.push("action=netsearchpo");
	params.push( "dccode="+dccode);
	params.push( "logistics="+logistics);
	params.push( "request_date="+request_date);
	var url = "../DaemonNetOrder?" + params.join('&');
	_table4search = new AW.XML.Table;
	_table4search.setURL( url );
	_table4search.request();
	_table4search.response 	= function(text){
		setLoading(false);
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
function initArrayU(){
	_array_u=[];
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
	if(row_count==0){$("div_poitem").innerHTML ="没有可操作的订单";return;}
	_grid.setRowCount( row_count );
	_grid.setCellText(_array_u);
	_grid.setSelectedRows([]);
	_grid.refresh();
	$("div_poitem").innerHTML = _grid.toString();
}
//预约保存
function netordersave(){
	var dccode = $('txt_dccode').value;
	if (dccode == "" ) {
		alert("DC编码必须填写！");
		$("txt_dccode").focus();
		return;
	}
	var logistics = $('txt_logistics').value;
	if (logistics == "" ) {
		alert("物流方式必须填写！");
		$("txt_logistics").focus();
		return;
	}
	var request_date = $('txt_request_date').value;
	if (request_date == "" ) {
		alert("预约日期必须填写！");
		$("txt_request_date").focus();
		return;
	}
	var time = $('txt_time').value;
	if (time == "" ) {
		alert("预约时间段必须填写！");
		$("txt_time").focus();
		return;
	}
	
	//得到勾选订单数据
	var tmp_a =  _grid.getSelectedRows();
	if(tmp_a.length<1){
		alert("没有勾选单据,请点击查看有效菜单按钮勾选单据！");
		return;
	}
	var pkgnum = $('txt_pkgnum').value;
	if (pkgnum == "" ) {
		alert("实际送货箱数必须填写！");
		$("txt_pkgnum").focus();
		return;
	}else{
		if(!isNum(pkgnum)){
			alert("实际送货箱数必须是大于0整数！");
			$("txt_pkgnum").focus();
			return;
		}else{
			if(pkgnum<=0){
				alert("实际送货箱数必须大于0！");
				$("txt_pkgnum").focus();
			}
		}
	}
	
	var _array_s = []; //已勾选单据数组
	for ( var i = 0; i < tmp_a.length; i++) {
		_array_s.push(_array_u[tmp_a[i]]);
	}
	
	//检查是否操作最晚操作时间
	 checklastdate(dccode,logistics,request_date,time,_array_s);
}

function arrayToXML(_dataArray,columns){
	var table = new AW.XML.Table;
	table.setXML("");
	var doc = table.getXML("");
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

function checkDateIsvalid(){
	$("div_poitem").innerHTML ="";
	$("sp_venderlist").innerHTML ="";
	$('txt_time').value="";
	var dccode = $('txt_dccode').value;
	if (dccode == "" ) {
		alert("DC编码必须填写！,请先选择DC编码！");
		$('txt_request_date').value="";
		$("txt_dccode").focus();
		return;
	}
	var logistics = $('txt_logistics').value;
	if (logistics == "" ) {
		alert("物流方式必须填写！");
		$('txt_request_date').value="";
		$("txt_logistics").focus();
		return;
	}
	
	//判断预约日期是否小于等于操作当天
	var request_date = $('txt_request_date').value;
	var mydate = new Date();
	var currentdate = mydate.getFullYear()*10000+(mydate.getMonth()+1)*100+mydate.getDate();
	var requestdate = parseInt((request_date.replace('-','')).replace('-',''));
	if(requestdate<=currentdate){
		alert("请从明天开始进行预约送货!");
		$('txt_request_date').value="";
		$('txt_request_date').focus();
		return;
	}
	//参数设置信息判断
	checkpara(dccode,request_date,logistics);
	//searchpo();
}

/**
 * 检查预约操作的时候是否在规定的时间内
 * @param dccode
 * @param logistics
 * @param request_date
 * @param time
 * @param _array_s
 */
function checklastdate(dccode,logistics,request_date,time,_array_s){
	/*
	setLoading( true,'检查操作时间' );
	var parms = new Array();
	parms.push("action=netparam_lastdate");
	parms.push("dccode="+dccode);
	var url  = "../DaemonNetOrderParam?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
		table.setXML(text);
		//setLoading( false );
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
			setLoading( false );
		}else{
			var lastdate = table.getXMLText("/xdoc/xout/netparamlastdate/row/orderlastdate");
			var curentdate =  table.getXMLText("/xdoc/xerr/time");
			curentdate = curentdate.substr(11,19);
			var currenttime = parseInt(curentdate.replace(':',""),10);
			var lasttime = parseInt(lastdate.replace(':',""),10);
			if(currenttime>lasttime){
				alert(lastdate+"后不再接受预约，请第二天提前预约!!");
				setLoading( false );
				return;
			}
			
			//判断选择的订单是否直送订单或大宗供应商的订单，如果是，判断预约送货结束时间点是否在配置的“直送订单或大宗供应商预约送货时间点”之前
			checknetordersuplly(dccode,logistics,request_date,time,_array_s);
		}
	};*/
	checknetordersuplly(dccode,logistics,request_date,time,_array_s);
}

//参数设置信息判断
function checkpara(dccode,request_date,logistics){
	setLoading( true );
	var parms = new Array();
	parms.push("action=netparam_stopdate");
	parms.push("dccode="+dccode);
	parms.push("request_date="+request_date);
	var url  = "../DaemonNetOrder?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
		table.setXML(text);
		setLoading( false );
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( table.getErrNote() );
			return;
		}
		stopdate = table.getXMLText("/xdoc/xout/netparamstopdate/row/stoporderdate");
		ordertime = table.getXMLText("/xdoc/xout/netparamstopdate/row/ordertime");
		isyesps = table.getXMLText("/xdoc/xout/netparamstopdate/row/isyesps");
		orderkfts = table.getXMLText("/xdoc/xout/netparamstopdate/row/orderkfts");
		
		//判断预约日期是否被暂停
		/*
		var v_stopdate = stopdate.split(",");
		var requestdate = parseInt((request_date.replace('-','')).replace('-',''));
		for(var i=0;i< v_stopdate.length;i++){
			var vv_stopdate = parseInt((v_stopdate[i].replace('-','')).replace('-',''));
			if(requestdate==vv_stopdate){
				alert("此预约日期已被暂停！请重新选择预约日期！");
				$('txt_request_date').value="";
				$('txt_request_date').focus();
				return;
			}
		}
		
		//判断预约日期是否在开放限制天数之内
		var mydate = new Date();
		var vdate =new Date(mydate.getFullYear(),mydate.getMonth(),mydate.getDate()+parseInt(orderkfts)); 
		var vcurrentdate = vdate.getFullYear()*10000+(vdate.getMonth()+1)*100+vdate.getDate();
		if(requestdate>vcurrentdate){
			alert("此预约日期已超过开放限制天数！只能预约在"+orderkfts+"天内！！");
			$('txt_request_date').value="";
			$('txt_request_date').focus();
			return;
		}
		//判断是否为可预约日期
		checkkyydate(dccode,request_date,logistics);
		*/
	};
}

//判断是否为可预约日期
function checkkyydate(dccode,request_date,logistics){
	setLoading( true );
	var parms = new Array();
	parms.push("action=netparamdate");
	parms.push("dccode="+dccode);
	parms.push("logistics="+logistics);
	var url  = "../DaemonNetOrder?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
		table.setXML(text);
		setLoading( false );
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( table.getErrNote() );
			return;
		}
		var monday = table.getXMLText("/xdoc/xout/netparamdate/row/monday");
		var tuesday = table.getXMLText("/xdoc/xout/netparamdate/row/tuesday");
		var wednesday = table.getXMLText("/xdoc/xout/netparamdate/row/wednesday");
		var thursday = table.getXMLText("/xdoc/xout/netparamdate/row/thursday");
		var friday = table.getXMLText("/xdoc/xout/netparamdate/row/friday");
		var saturday = table.getXMLText("/xdoc/xout/netparamdate/row/saturday");
		var sunday = table.getXMLText("/xdoc/xout/netparamdate/row/sunday");
		
		var date= new Date(Date.parse(request_date.replace(/-/g,   "/"))); 
		var dd = date.getDay(); 
		if(dd==0 && sunday=="N"){
			alert("此预约日期星期天，不为可预约日！");
			$('txt_request_date').value="";
			$('txt_request_date').focus();
			return;
		}else if(dd==1 && monday=="N"){
			alert("此预约日期星期一，不为可预约日！");
			$('txt_request_date').value="";
			$('txt_request_date').focus();
			return;
		}else if(dd==2 && tuesday=="N"){
			alert("此预约日期星期二，不为可预约日！");
			$('txt_request_date').value="";
			$('txt_request_date').focus();
			return;
		}else if(dd==3 && wednesday=="N"){
			alert("此预约日期星期三，不为可预约日！");
			$('txt_request_date').value="";
			$('txt_request_date').focus();
			return;
		}else if(dd==4 && thursday=="N"){
			alert("此预约日期星期四，不为可预约日！");
			$('txt_request_date').value="";
			$('txt_request_date').focus();
			return;
		}else if(dd==5 && friday=="N"){
			alert("此预约日期星期五，不为可预约日！");
			$('txt_request_date').value="";
			$('txt_request_date').focus();
			return;
		}else if(dd==6 && saturday=="N"){
			alert("此预约日期星期六,不为可预约日！");
			$('txt_request_date').value="";
			$('txt_request_date').focus();
			return;
		}
	};
}

function checknetordersuplly(dccode,logistics,request_date,time,_array_s){
	setLoading( true,'检查是否大宗供应商' );
	var parms_sup = new Array();
	parms_sup.push("action=netorder_suplly");
	parms_sup.push("dccode="+dccode);
	var url  = "../DaemonNetOrder?"+parms_sup.join('&');
	var table_sup = new AW.XML.Table;
	table_sup.setURL(url);
	table_sup.request();
	table_sup.response = function(text){
		//setLoading( false );
		table_sup.setXML(text);
		var xcode = table_sup.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table_sup.getErrNote());
			setLoading( false );
			return;
		}
		var vendtype = table_sup.getXMLText("/xdoc/xout/netordersuplly/row/vendtype");
		var ary = time.split(",");
		var endtime = ary[1];
		var v_endtime=endtime.replace(':','');
		var v_ordertime=ordertime.replace(':','');
		var vtime = parseInt(v_endtime,10);
		var vordertime = parseInt(v_ordertime,10);
		if(vendtype == "0"){
			if(vtime >= vordertime){
				alert("大宗供应商预约送货时间在"+ordertime+"之前,请第二天提前预约!!");
				setLoading( false );
				return;
			}
	   }else if(logistics == "1"){
			if(vtime >= vordertime){
				alert("直送订单预约送货时间在"+ordertime+"之前,请第二天提前预约!!");
				setLoading( false );
				return;
			}
		 }
		//对预约时间段的SKU数、箱数、供应商个数、大宗供应商预约个数 和综合验收供应商个数进行判断	
		 checkorderpo(vendtype,dccode,logistics,request_date,time,_array_s);		 
	};
}

//判断供应商在当天是否存在此物流模式
function  checklogistics(dccode,logistics,request_date,time,_array_s){
	setLoading( true,'检查订单状态' );
	var parms_logis = new Array();
	parms_logis.push("action=netorder_logistics");
	parms_logis.push("dccode="+dccode);
	parms_logis.push("logistics="+logistics);
	parms_logis.push("request_date="+request_date);
	var url  = "../DaemonNetOrder?"+parms_logis.join('&');
	var table_logis = new AW.XML.Table;
	table_logis.setURL(url);
	table_logis.request();
	table_logis.response = function(text){
		//setLoading( false );
		table_logis.setXML(text);
		var xcode = table_logis.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table_logis.getErrNote());
			setLoading( false );
			return;
		}
			var vorder_serial = table_logis.getXMLText("/xdoc/xout/netorderlogistics/row/order_serial");
			var vdccode = table_logis.getXMLText("/xdoc/xout/netorderlogistics/row/dccode");
			var vlogistics = table_logis.getXMLText("/xdoc/xout/netorderlogistics/row/logistics");
			if(logistics==vlogistics){
				alert("此物流模式已在流水号："+vorder_serial+"里预约！！不允许再预约此预约日期。");
				setLoading( false );
				return;
			}
			//物流模式为直通时，需要判断参数里是否可以在配送日预约。如果否，则根据配送日来预约，不在配送日范围内，不允许预约
			checkpstime(dccode,logistics,request_date,time,_array_s);
	};
}
function checkpstime(dccode,logistics,request_date,time,_array_s){
		setLoading( true ,'检查预约时间段');
		var dd = _array_s;
		var params = [];
		for ( var i = 0; i < dd.length; i++) {
			var yy = dd[i];
			params.push(yy[11]);
		}
		    var _table  = new AW.XML.Table;
			params.push("action=netpsdate");
			params.push("dccode="+dccode);
			var url = "../DaemonNetOrder?" + params.join('&');
			_table = new AW.XML.Table;
			_table.setRequestMethod('POST');
			var x = arrayToXML(_array_s,_columns);
			_table.setRequestData(_table.getXMLContent(x));
			_table.setURL( url );
			_table.request();
			_table.response = function(text){
				//setLoading(false);
				_table.setXML( text );
				if( _table.getErrCode() != 0 ){	//处理xml中的错误消息
					alert(_table.getErrNote()) ;
					setLoading(false);
					return;
				}else{
					if(logistics=="2"){
						if(isyesps=="N"){
							var date= new Date(Date.parse(request_date.replace(/-/g,   "/"))); 
							var dd = date.getDay(); 
							var nodes = _table.getXML().selectNodes("/xdoc/xout/select/psdate");
							for(var index=0; index<nodes.length; index++) {
							//	var dccode = nodes[index].getAttribute('dccode');
								var shopid = nodes[index].getAttribute('shopid');
								//暂时未判断一个送货门店设置多个配送日的情况
								var pszq = nodes[index].getAttribute('pszq');
								if(pszq!=dd){
									alert("直通订单必须在配送日送货!!请重新勾选订单！");
									setLoading( false );
									return;
								}
							}
						}
					}
								
							//查出配送日进行比较，看预约日期是否在配送日范围内。如不在直接RETURN;
//								if(logistics=="2"){
//									if(isyesps=="N"){
//										var date= new Date(Date.parse(request_date.replace(/-/g,   "/"))); 
//										var dd = date.getDay(); 
//										var nodes = _table.getXML().selectNodes("/xdoc/xout/select/psdate");
//										
//										var shopid="";
//										var pszq=[];
//										for(var index=0; index<nodes.length; index++) {
//											if(shopid == nodes[index].getAttribute('shopid')){
//												//处理一个DC，送货门店对应多个配送日期
//												pszq.push(nodes[index].getAttribute('pszq'));
//												
//											}else{
//												for (i=0;i<pszq.length;i++){
//													dd = pszq[i];
//												}
//												
//												if(pszq != 0 && pszq!=dd ){
//													alert("直通订单必须在配送日送货!!请重新勾选订单！");
//													return;
//												}
//												
//												//处理一个DC，送货门店对应一个配送日期
//												shopid = nodes[index].getAttribute('shopid');
//												pszq = nodes[index].getAttribute('pszq');
//												if(pszq != 0 && pszq!=dd){
//													alert("直通订单必须在配送日送货!!请重新勾选订单！");
//													return;
//												}
//											}
//										}
//								}
//						}
				}
				//判断赠品订单和非赠品订单不能同时预约(结算方式=88)
			    checkpo(dccode,logistics,request_date,time,_array_s);	
		};
}
//判断赠品订单和非赠品订单不能同时预约(结算方式=88)
function checkpo(dccode,logistics,request_date,time,_array_s){
	setLoading( true,'赠品订单检验' );
	var dd = _array_s;
	var params = [];
	for ( var i = 0; i < dd.length; i++) {
		var yy = dd[i];
		params.push(yy[1]);
	}
	    var _table  = new AW.XML.Table;
		params.push("action=netordercheckpo");
		var url = "../DaemonNetOrder?" + params.join('&');
		_table = new AW.XML.Table;
		_table.setRequestMethod('POST');
		var x = arrayToXML(_array_s,_columns);
		_table.setRequestData(_table.getXMLContent(x));
		_table.setURL( url );
		_table.request();
		_table.response = function(text){
			//setLoading(false);
			_table.setXML( text );
			if( _table.getErrCode() != 0 ){	//处理xml中的错误消息
				alert(_table.getErrNote()) ;
				setLoading(false);
				return;
			}else{
				var fzp = 0;var zp = 0;
				var nodes = _table.getXML().selectNodes("/xdoc/xout/select/checkpo");
				for(var index=0; index<nodes.length; index++) {
					var sheetid = nodes[index].getAttribute('sheetid');
					var paytypeid = nodes[index].getAttribute('paytypeid');
					if(paytypeid == "88"){
						zp += 1;
					}else{
						fzp += 1;
					}
				}
				if(zp>0 && fzp>0){
					alert("赠品订单和非赠品订单不能同时预约!!");
					setLoading(false);
					return;
				}
			}
			//预约单保存
			 saveorder(dccode,logistics,request_date,time,_array_s);
		};
}

function saveorder(dccode,logistics,request_date,time,_array_s){
			//保存预约单据
		    var _table  = new AW.XML.Table;
			var params = [];
			params.push("action=netordervendersave");
			params.push( "dccode="+dccode);
			params.push( "logistics="+logistics);
			params.push( "request_date="+request_date);
			params.push( "time="+time);
			params.push( "note="+$("txt_note").value);
			params.push( "pkgnum="+$("txt_pkgnum").value);
			var url = "../DaemonNetOrder?" + params.join('&');
			_table = new AW.XML.Table;
			_table.setRequestMethod('POST');
			var x = arrayToXML(_array_s,_columns);
			_table.setRequestData(_table.getXMLContent(x));
			_table.setURL( url );
			_table.request();
			_table.response = function(text){
				setLoading(false);
				_table.setXML( text );
				if( _table.getErrCode() != 0 ){	//处理xml中的错误消息
					alert(_table.getErrNote()) ;
					return;
				}else{
					var orderserial = _table.getXMLText("xdoc/xout/result/orderserial");
					alert("预约流水号："+orderserial+"已成功");
					initText();
				}
			};
			setLoading( true ,'保存中……');
};

//时间段改变时触发事件
function queryt(){
	$("div_poitem").innerHTML ="";
	var dccode = $('txt_dccode').value;
	if (dccode == "" ) {
		alert("DC编码必须填写！");
		$("txt_dccode").focus();
		$('txt_time').value="";
		return;
	}
	var logistics = $('txt_logistics').value;
	if (logistics == "" ) {
		alert("物流方式必须填写！");
		$("txt_logistics").focus();
		$('txt_time').value="";
		return;
	}
	var request_date = $('txt_request_date').value;
	if (request_date == "" ) {
		alert("预约日期必须填写！");
		$("txt_request_date").focus();
		$('txt_time').value="";
		return;
	}

	var time = $('txt_time').value;
	if (time == "" ) {
		alert("预约时间段必须填写！");
		$("txt_time").focus();
		return;
	}
	//查看还可以预约的信息
	querytime(dccode,logistics,request_date,time);
	
	//在预约时段开放上，先开放十二点前的所有时间段，只有上午时段预约满了之后才可以预约下午时段，下午时段需要逐一时段开放
	querykftime(dccode,logistics,request_date,time);
	
	//searchpo();
}

function querykftime(dccode,logistics,request_date,time){
		var arytime = time.split(",");
		var endtime = arytime[1];
		var v_endtime=endtime.replace(':','');
		var vtime = parseInt(v_endtime,10);
		if(vtime>1200){
			alert("请先预约上午时段！上午时段预约满额后，再预约下午时段！");
		}
}
//查看还可以预约的信息
function querytime(dccode,logistics,request_date,time){
	setLoading( true,'查询预约情况' );
	var parms_t = new Array();
	parms_t.push("action=netorderqk");
	parms_t.push("dccode="+dccode);
	parms_t.push("logistics="+logistics);
	parms_t.push("requestdate="+request_date);
	parms_t.push("time="+time);
	var url  = "../DaemonNetOrder?"+parms_t.join('&');
	var table_t = new AW.XML.Table;
	table_t.setURL(url);
	table_t.request();
	table_t.response = function(text){
		setLoading( false );
		table_t.setXML(text);
		var xcode = table_t.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			$('sp_venderlist').innerText='';
			alert( xcode+table_t.getErrNote());
			return;
		}
			g_dzsupply = Number(table_t.getXMLText("/xdoc/xout/netorderqk/row/maxdzsupply"));
			g_yssupply = Number(table_t.getXMLText("/xdoc/xout/netorderqk/row/maxyssupply"));
			g_supply = Number(table_t.getXMLText("/xdoc/xout/netorderqk/row/maxsupply"));
			g_pkgnum = Number(table_t.getXMLText("/xdoc/xout/netorderqk/row/maxxs"));
			g_skunum = Number(table_t.getXMLText("/xdoc/xout/netorderqk/row/maxsku"));
			var str = "此时段还可以预约：供应商："+g_supply+" 个；大宗供应商："+g_dzsupply+" 个；综合验收供应商："+g_yssupply+" 个；商品箱数："+g_pkgnum+"；SKU个数："+ g_skunum;
			
			if( g_pkgnum==0 || g_skunum==0){
				str = "此时段已满额，请尝试其它时段";
			}
			
			$('sp_venderlist').innerText=str;
	};
}

//对预约时间段的SKU数、箱数、供应商个数、大宗供应商预约个数 和综合验收供应商个数进行判断	
function  checkorderpo(vendtype,dccode,logistics,request_date,time,_array_s){
	var v_dzsupplynum=0;
	var v_yssupplynum=0;
	var v_supplynum=0;
	var v_sku=0;
	var v_pkg=0;
	
	if(vendtype=="0"&& g_dzsupply>0){
		v_dzsupplynum = g_dzsupply-1;
	}else if(vendtype=="1" && g_yssupply>0){
		v_yssupplynum = g_yssupply-1;
	}else if(vendtype=="" && g_supply>0){
		v_supplynum = g_supply -1;
	}
	var dd = _array_s;
	var params = [];
	var vsku=0;
	var vpkg=0;
	var vspkg =$('txt_pkgnum').value;//实际送货箱数 
	var checkflag=true;
	var checkorderfalg = false;
	
	//当供应商选择的订单全部在预约送货日到期，则不进行SKU数、箱数、供应商个数、大宗供应商预约个数 和综合验收供应商个数限制的判断,如果
	for ( var i = 0; i < dd.length; i++) {
		        var yy = dd[i];
				var orderdate = yy[2];
				var validdays = yy[4];
				var vorderdate = parseInt((orderdate.replace('-','')).replace('-',''))+parseInt(validdays);
				var requestdate = parseInt((request_date.replace('-','')).replace('-',''));
				if(vorderdate==requestdate){
					checkflag=false;
				}else{
					checkorderfalg = true;
					vsku +=  parseInt(yy[8]);
					vpkg +=  parseInt(yy[9]);
				}
	}
	if(g_skunum>0){
		v_sku = g_skunum-vsku;
	}
	//核减实际送货箱数
	vspkg==''?vpkg:Number(vspkg);
	if(g_pkgnum>0){
		v_pkg = g_pkgnum-vspkg;
	}
	if( v_dzsupplynum<0 && checkorderfalg){
		alert("该时段预约量已满停止预约！请更换时间段进行预约！");
		setLoading( false );
		return;
	}
	if( v_yssupplynum<0 && checkorderfalg){
		alert("该时段预约量已满停止预约！请更换时间段进行预约！");
		setLoading( false );
		return;
	}
	if(v_supplynum<0 && checkorderfalg){
		alert("该时段预约量已满停止预约！请更换时间段进行预约！");
		setLoading( false );
		return;
	}
	if(v_sku<0 && checkorderfalg){
		alert("该时段预约量已满停止预约！请更换时间段进行预约！");
		setLoading( false );
		return;
	}
	if(v_pkg<0 && checkorderfalg){
		alert("该时段预约量已满停止预约！请更换时间段进行预约！");
		setLoading( false );
		return;
	}
	
	//判断供应商在当天是否存在此物流模式的预约
	 checklogistics(dccode,logistics,request_date,time,_array_s);	
}