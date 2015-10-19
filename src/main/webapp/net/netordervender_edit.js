	var _table;
	var g_dzsupply = 0; //大宗供应商
	var g_yssupply = 0;//综合验收
	var g_supply = 0;
	var g_pkgnum = 0;
	var g_skunum = 0;
	var g_old = 0;
	var vendtype="";
	
	var _array_u = [];
	var _columnNames = ["勾单","订单号","订货日期","送货时间","有效期(天)","物流模式","订货审批单号","取消日期","SKU数","商品箱数","送货门店"];
	var _columns = ["checkbox","sheetid","orderdate","vdeliverdate","validdays","logistics","refsheetid","deadline","qty","pkgqty","destshopid"];
	var operatetype = "";
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
				$('txt_order_serial').value=table.getXMLText( "/xdoc/xout/netnetordersh/row/order_serial");
				$('txt_dccode').value=table.getXMLText( "/xdoc/xout/netnetordersh/row/dccode");
				$('txt_logistics').value=table.getXMLText( "/xdoc/xout/netnetordersh/row/logistics" );
				$('txt_request_date').value=table.getXMLText( "/xdoc/xout/netnetordersh/row/request_date" );
				$('txt_start_time').value=table.getXMLText( "/xdoc/xout/netnetordersh/row/start_time" );
				$('txt_end_time').value=table.getXMLText( "/xdoc/xout/netnetordersh/row/end_time" );
				$('txt_note').value=table.getXMLText( "/xdoc/xout/netnetordersh/row/note" );
				$('txt_pkgnum').value=table.getXMLText( "/xdoc/xout/netnetordersh/row/temp1" );
				g_old = Number($("txt_pkgnum").value);
				$("txt_order_serial").disabled =true;
				$("txt_dccode").disabled =true;
				$("txt_logistics").disabled =true;
				$("txt_logistics").disabled =true;
				$("txt_request_date").disabled =true;
				$("txt_start_time").disabled =true;
				$("txt_end_time").disabled =true;
			}
			setLoading( false );
		};
	}
	
	
	function addorderpovender(){
		operatetype="0";
		$("div_poitem").innerHTML ="";
		var dccode = $('txt_dccode').value;
		var logistics = $('txt_logistics').value;
		var request_date = $('txt_request_date').value;
		var params_u = autoParms();
		params_u.push("action=netsearchpo");
		params_u.push( "dccode="+dccode);
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
	}
	
	
	function delorderpovender(){
		operatetype="1";
		$("div_poitem").innerHTML ="";
		var order_serial= $('txt_order_serial').value;
		var dccode       = $('txt_dccode').value;
		var logistics      = $('txt_logistics').value;
		var params_s    = autoParms();
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
		if(row_count==0){$("div_poitem").innerHTML ="没有可操作的订单";return;}
		_grid.setRowCount( row_count );
		_grid.setCellText(_array_u);
		_grid.setSelectedRows([]);
		_grid.refresh();
		$("div_poitem").innerHTML = _grid.toString();
		
		
		var time = $('txt_start_time').value+","+$('txt_end_time').value;
		var dccode = $('txt_dccode').value;
		var logistics = $('txt_logistics').value;
		var request_date = $('txt_request_date').value;
		
		querytime(dccode,logistics,request_date,time);
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
		
		var order_serial=$('txt_order_serial').value;
		var dccode = $('txt_dccode').value;
		var logistics = $('txt_logistics').value;
		var request_date = $('txt_request_date').value;
		var time = $('txt_start_time').value+','+$('txt_end_time').value;
		var note =$('txt_note').value;
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
				// 加单操作检查
				checknetordersuplly(dccode,order_serial,logistics,request_date,time,note,_array_s);	
		}else if(operatetype=="1"){
				var _table 	   = new AW.XML.Table;
				var params = [];
				params.push("action=netorderdelpovendersave");
				params.push( "dccode="+dccode);
				params.push( "order_serial="+order_serial);
				params.push( "note="+note);
				params.push( "logistics="+$('txt_logistics').value);
				params.push( "pkgnum="+$('txt_pkgnum').value);
				params.push( "request_date="+$('txt_request_date').value);
				
				var time = $('txt_start_time').value+','+$('txt_end_time').value;
				params.push( "time="+time);
				var url = "../DaemonNetOrder?" + params.join('&');
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
	function checknetordersuplly(dccode,order_serial,logistics,request_date,time,note,_array_s){
		setLoading( true );
		var parms_sup = new Array();
		parms_sup.push("action=netorder_suplly");
		parms_sup.push("dccode="+dccode);
		var url  = "../DaemonNetOrder?"+parms_sup.join('&');
		var table_sup = new AW.XML.Table;
		table_sup.setURL(url);
		table_sup.request();
		table_sup.response = function(text){
			setLoading( false );
			table_sup.setXML(text);
			var xcode = table_sup.getErrCode();
			if( xcode != '0' ){//处理xml中的错误消息
				alert( xcode+table_sup.getErrNote());
				return;
			}
				var vendtype = table_sup.getXMLText("/xdoc/xout/netordersuplly/row/vendtype");
				//对预约时间段的SKU数、箱数、供应商个数、大宗供应商预约个数 和综合验收供应商个数进行判断	
				 checkorderpo(vendtype,dccode,order_serial,logistics,request_date,time,note,_array_s);		 
		};
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
				
				if(g_pkgnum==0 || g_skunum==0){
					str = "此时段已满额，请尝试其它时段";
				}
				
				$('sp_venderlist').innerText=str;
		};
	}
	
	//对预约时间段的SKU数、箱数、供应商个数、大宗供应商预约个数 和综合验收供应商个数进行判断	
	function  checkorderpo(vendtype,dccode,order_serial,logistics,request_date,time,note,_array_s){
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
		var vpkg=0; //订单箱数
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

		//物流模式为直通时，需要判断参数里是否可以在配送日预约。如果否，则根据配送日来预约，不在配送日范围内，不允许预约
		checkpstime(dccode,order_serial,logistics,request_date,time,note,_array_s);
	}

	function checkpstime(dccode,order_serial,logistics,request_date,time,note,_array_s){
		/*
		if(logistics=="2"){
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
					setLoading(false);
					_table.setXML( text );
					if( _table.getErrCode() != 0 ){	//处理xml中的错误消息
						alert(_table.getErrNote()) ;
						return;
					}else{
						//查出配送日进行比较，看预约日期是否在配送日范围内。如不在直接RETURN;
							if(logistics=="2"){
								var date= new Date(Date.parse(request_date.replace(/-/g,   "/"))); 
								var dd = date.getDay(); 
								var nodes = _table.getXML().selectNodes("/xdoc/xout/select/psdate");
								for(var index=0; index<nodes.length; index++) {
									var vdccode = nodes[index].getAttribute('dccode');
									var shopid = nodes[index].getAttribute('shopid');
									//暂时未判断一个送货门店设置多个配送日的情况
									var pszq = nodes[index].getAttribute('pszq');
									if(pszq!=dd){
										alert("直通订单必须在配送日送货!!请重新勾选订单！");
										return;
									}
								}
							}
					//判断赠品订单和非赠品订单不能同时预约(结算方式=88)
				    checkpo(dccode,order_serial,logistics,request_date,time,note,_array_s);
					}
			};
		}else{
			 checkpo(dccode,order_serial,logistics,request_date,time,note,_array_s);
		}
		setLoading( true );
		*/
		
		checkpo(dccode,order_serial,logistics,request_date,time,note,_array_s);
}
	
	//判断赠品订单和非赠品订单不能同时预约(结算方式=88)
	function checkpo(dccode,order_serial,logistics,request_date,time,note,_array_s){
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
				setLoading(false);
				_table.setXML( text );
				if( _table.getErrCode() != 0 ){	//处理xml中的错误消息
					alert(_table.getErrNote()) ;
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
						return;
					}
					//预约单保存
					 saveorder(dccode,order_serial,logistics,request_date,time,note,_array_s);
				}
			};
			setLoading( true );
	}
	
	function saveorder(dccode,order_serial,logistics,request_date,time,note,_array_s){
		var _table  = new AW.XML.Table;
			var params = [];
			params.push("action=netorderaddpovendersave");
			params.push( "dccode="+dccode);
			params.push( "order_serial="+order_serial);
			params.push( "note="+note);
			params.push( "logistics="+$('txt_logistics').value);
			params.push( "pkgnum="+$('txt_pkgnum').value);
			params.push( "request_date="+$('txt_request_date').value);
			var time = $('txt_start_time').value+','+$('txt_end_time').value;
			params.push( "time="+time);
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
	}
	