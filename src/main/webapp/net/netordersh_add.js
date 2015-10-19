var _table4search;
var _array_u = [];
var _columnNames = ["勾单","订单号","订货日期","送货时间","有效期(天)","物流模式","订货审批单号","取消日期","SKU数","商品箱数"  ];
var _columns = ["checkbox","sheetid","orderdate","vdeliverdate","validdays","logistics","refsheetid","deadline","qty","pkgqty" ];
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
};

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
				$('sp_vendername').innerText='';
				alert( xcode+table.getErrNote());
			}else{
				$('sp_vendername').innerText=table.getXMLText( "/xdoc/xout/vender/vendername" );
				$('txt_venderid').disabled = true;
			}
		};
	}else{
		$('sp_vendername').innerText='';
	}
}

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
		}
	};
}
//清除输入数据
function initText(){
	txt_supplier_no.value="";
	$('sp_vendername').innerText='';
	txt_dccode.value="";
	txt_logistics.value="";
	txt_request_date.value="";
	txt_time.value="";
	txt_note.value="";
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
	var supplier_no = $('txt_supplier_no').value;
	if (supplier_no == "" ) {
		alert("供应商编码必须填写！");
		$("txt_supplier_no").focus();
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
	params.push( "supplier_no="+supplier_no);
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
	if(row_count==0){$("div_poitem").innerHTML ="没有符合条件的数据";return;}
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
	var supplier_no = $('txt_supplier_no').value;
	if (supplier_no == "" ) {
		alert("供应商编码必须填写！");
		$("txt_supplier_no").focus();
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
	
	var _array_s = []; //已勾选单据数组
	for ( var i = 0; i < tmp_a.length; i++) {
		_array_s.push(_array_u[tmp_a[i]]);
	}
	var _table 	   = new AW.XML.Table;
	var params = [];
	params.push("action=netordersave");
	params.push( "dccode="+dccode);
	params.push( "supplier_no="+supplier_no);
	params.push( "logistics="+logistics);
	params.push( "request_date="+request_date);
	params.push( "time="+time);
	params.push( "note="+$('txt_note').value);
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
		}
		var orderserial = _table.getXMLText("xdoc/xout/result/orderserial");
		alert("预约流水号："+orderserial+"已成功，将自动转到单据打印页面。");
		initText();
		
	};
	setLoading( true );
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
