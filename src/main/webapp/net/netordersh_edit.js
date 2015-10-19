	var _table;
	var _array_u = [];
	var _columnNames = ["勾单","订单号","订货日期","送货时间","有效期(天)","物流模式","订货审批单号","取消日期","SKU数","商品箱数"  ];
	var _columns = ["checkbox","sheetid","orderdate","vdeliverdate","validdays","logistics","refsheetid","deadline","qty","pkgqty" ];
	var operatetype = "";
	var g_old = 0;
	//已预约的订单
	var _grid = new AW.UI.Grid;
	function init(order_serial,dccode){
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
			//减单
			if(operatetype =="1"){
				pkg = g_old-pkg;
			}else{
				pkg = pkg+g_old;
			}
			$("txt_pkgnum").value= pkg;
		};
		
		setLoading( true );
		var parms = new Array();
		parms.push("action=netordersh_list");
		parms.push("order_serial="+order_serial);
		parms.push("dccode="+dccode);
		var url  = "../DaemonNetOrder?"+parms.join('&');
		var table = new AW.XML.Table;
		table.setURL(url);
		table.request();
		table.response = function(text){
			table.setXML(text);
			var xcode = table.getErrCode();
			if( xcode != '0' ){//处理xml中的错误消息
				alert( xcode+table.getErrNote());
			}else{
				$('txt_order_serial').innerText=table.getXMLText( "/xdoc/xout/netnetordersh/row/order_serial");
				$('txt_dccode').innerText=table.getXMLText( "/xdoc/xout/netnetordersh/row/dccode");
				$('txt_logistics').value=table.getXMLText( "/xdoc/xout/netnetordersh/row/logistics" );
				$('txt_supplier_no').innerText=table.getXMLText( "/xdoc/xout/netnetordersh/row/supplier_no" );
				$('txt_vendername').innerText=table.getXMLText( "/xdoc/xout/netnetordersh/row/vendername" );
				$('txt_request_date').innerText=table.getXMLText( "/xdoc/xout/netnetordersh/row/request_date" );
				$('txt_start_time').innerText=table.getXMLText( "/xdoc/xout/netnetordersh/row/start_time" );
				$('txt_end_time').innerText=table.getXMLText( "/xdoc/xout/netnetordersh/row/end_time" );
				$('txt_note').innerText=table.getXMLText( "/xdoc/xout/netnetordersh/row/note" );
				$('txt_pkgnum').value=table.getXMLText( "/xdoc/xout/netnetordersh/row/temp1" );
				g_old = Number($("txt_pkgnum").value);
				$("txt_order_serial").disabled =true;
				$("txt_dccode").disabled =true;
				$("txt_logistics").disabled =true;
				$("txt_supplier_no").disabled =true;
				$("txt_vendername").disabled =true;
				$("txt_request_date").disabled =true;
				$("txt_start_time").disabled =true;
				$("txt_end_time").disabled =true;
			}
			setLoading( false );
		};
	}
	
	
	function addorderpo(){
		operatetype="";
		$("div_poitem").innerHTML ="清除grid数据";
		var dccode = $('txt_dccode').value;
		var supplier_no = $('txt_supplier_no').value;
		var logistics = $('txt_logistics').value;
		var request_date = $('txt_request_date').value;
		var params_u = autoParms();
		params_u.push("action=netsearchpo");
		params_u.push( "dccode="+dccode);
		params_u.push( "supplier_no="+supplier_no);
		params_u.push( "logistics="+logistics);
		params_u.push( "request_date="+request_date);
		var url = "../DaemonNetOrder?" + params_u.join('&');
		_table = new AW.XML.Table;
		_table.setURL( url );
		_table.request();
		_table.response 	= function(text){
			setLoading(false);
			_table.setTable("xdoc/xout/rowset");
			_table.setRows("row");
			_table.setXML( text );
			if( _table.getErrCode() != 0 ){	//处理xml中的错误消息
				alert(_table.getErrNote()) ;
				return;
			}
			initArrayU();
			show_catalogue();
		};
		setLoading( true );
		operatetype="0";
	}
	
	
	function delorderpo(){
		operatetype="";
		$("div_poitem").innerHTML ="清除grid数据";
		var order_serial=$('txt_order_serial').value;
		var dccode = $('txt_dccode').value;
		var logistics = $('txt_logistics').value;
		var params_s = autoParms();
		params_s.push("action=netorderyypo");
		params_s.push( "order_serial="+order_serial);
		params_s.push( "dccode="+dccode);
		params_s.push( "logistics="+logistics);
		var url = "../DaemonNetOrder?" + params_s.join('&');
		_table = new AW.XML.Table;
		_table.setURL( url );
		_table.request();
		_table.response 	= function(text){
			setLoading(false);
			_table.setTable("xdoc/xout/rowset");
			_table.setRows("row");
			_table.setXML( text );
			if( _table.getErrCode() != 0 ){	//处理xml中的错误消息
				alert(_table.getErrNote()) ;
				return;
			}
			initArrayU();
			show_catalogue();
		};
		setLoading( true );
		operatetype="1";
	}
	function initArrayU(){
		_array_u = [];
		for(var i=0; i<_table.getCount(); i++) {
			var arr = [];
			arr.push("");
			for ( var j = 0; j < _columns.length; j++) {
				arr.push(_table.getData(j,i));
			}
			_array_u.push(arr);
		}
	}
	
	function show_catalogue(){
		var row_count = _table.getCount();
		if(row_count==0){$("div_poitem").innerHTML ="没有符合条件的数据";return;}
		_grid.setRowCount( row_count );
		_grid.setCellText(_array_u);
		_grid.setSelectedRows([]);
		_grid.refresh();
		$("div_poitem").innerHTML = _grid.toString();
//		_grid.refresh();
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
	function save(){
		
		var order_serial=$('txt_order_serial').value;
		var dccode = $('txt_dccode').value;
		
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
		
		if(operatetype=="0"){
			var _table 	   = new AW.XML.Table;
			var params = [];
			params.push("action=netorderaddposave");
			params.push( "dccode="+dccode);
			params.push( "order_serial="+order_serial);
			params.push( "note="+$('txt_note').value);
			params.push( "logistics="+$('txt_logistics').value);
			var time = $('txt_start_time').value+','+$('txt_end_time').value;
			params.push( "time="+time);
			params.push( "request_date="+$('txt_request_date').value);
			params.push( "pkgnum="+$('txt_pkgnum').value);
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
					alert("预约加单修改成功!");
					window.close();
				}
			};
			setLoading( true );
		}else if(operatetype=="1"){
			var _table 	   = new AW.XML.Table;
			var params = [];
			params.push("action=netorderdelposave");
			params.push( "dccode="+dccode);
			params.push( "order_serial="+order_serial);
			params.push( "note="+$('txt_note').value);
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
					alert("预约减单修改成功!");
					window.close();
				}
			};
			setLoading( true );
		}
	}