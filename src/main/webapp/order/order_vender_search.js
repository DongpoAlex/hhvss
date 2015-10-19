var arr_sheetid 		= null;
var current_sheetid 	= null;
var row_selected		= null;
var print_selected		= null;

var g_logistics=0;
var g_orderid = new Array();

var btn_search = new AW.UI.Button;
btn_search.setControlText( "查询" );
btn_search.setId( "btn_search" );
btn_search.setControlImage( "search" );
btn_search.onClick = search_by_parms;

var tag_grp = new AW.UI.Tabs;
var table4search = new AW.XML.Table;
var table4detail = new AW.XML.Table;
var table4confirm = new AW.XML.Table;
var table4confirm2 = new AW.XML.Table;
var table4shopprint = new AW.XML.Table;

window.onload = function(){
	$('div_button_search').innerHTML=btn_search;
	install_tag();
	tag_grp.setSelectedItems( [0] );
	
	var obj = $("txt_status");
	if( g_status != -1 ){
		obj.value=g_status;
		search_by_parms();
	}
};

function init_data(){
	arr_sheetid = null;
	current_sheetid 	= null;
	row_selected		= null;
	print_selected		= null;
	div_cat.innerHTML = "";
	div_sheethead.innerHTML = "";
	div_confirm4all.innerHTML = "";
	div_shop_catalotue.innerHTML = "";
}
//装载卡片
function install_tag()
{
	tag_grp.setId( "tag_grp" );
	tag_grp.setItemText( [ "查询","单据目录", "单据明细", "批量处理", "" ] );
	tag_grp.setItemCount( 5 );
	tag_grp.onSelectedItemsChanged = change_tag;
	div_tag.innerHTML = tag_grp.toString();
}
//卡片切换事件
function change_tag( idx ){
	var div_id =[ "div_search","div_cat", "div_detail", "div_confirm", "div_printbyshop" ];
	for (var i=0; i<5; i++){
		if (idx == i ) 
			$(div_id[i]).style.display = "block";
		else
			$(div_id[i]).style.display = "none";
	}
	//调用的方法
	switch ( Number(idx) ) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			load_purchase();
			break;
		case 3:
			show_confirm_catalotue();
			break;
		case 4:
			show_shop_catalotue();
			break;
		default:
	}
}

function search_by_sheetid(){
	var str = txt_sheetid.value;
	str = str.replace( / +/g, ',' );		// 把空格换成逗号
	str = str.replace( /,+/g, ',' );		// 把多个逗号换成一个逗号
	if( str == 0 ) { alert( "未输入单据号!" ); return; }
	var arr_id = str.split ( ',' );
	
	var parms = new Array();
	for( var i=0; i<arr_id.length; i++ ) {
		var s = arr_id[ i ];
		if( s != '' ) parms.push( "sheetid=" + s );
	};
	
	search_sheet( parms );
}
//按条件过滤查询
function search_by_parms(){
	var parms = cook_search_prams();
	search_sheet( parms );
}

function search_sheet( parms ){
	parms.push("clazz=Purchasechk");
	parms.push("operation=search");
	var url = "../DaemonSheet?" + parms.join( '&' );
	table4search.setURL( url );
	table4search.request();
	table4search.response 	= analyse_search;
	
	setLoading(true);
	tag_grp.setSelectedItems( [1] );
	init_data();
}

//拼url
function cook_search_prams(){
	var parms = new Array();
	if( $F('txt_releasedate_min')  != '' )  parms.push( "releasedate_min="   + $F('txt_releasedate_min'));
	if( $F('txt_releasedate_max')  != '' )  parms.push( "releasedate_max="   + $F('txt_releasedate_max'));
	if( $F('txt_orderdate_min')  != '' )   	parms.push( "orderdate_min="   + $F('txt_orderdate_min'));
	if( $F('txt_orderdate_max')  != '' )   	parms.push( "orderdate_max="   + $F('txt_orderdate_max'));
	if( $F('txt_logistics')  != '' )    	parms.push( "logistics="    + $F('txt_logistics') );
	var status = $F('txt_status');
	if(status!=''){
		if(status==7||status==99){
			parms.push( "flag="    + status );
		}else{
			parms.push( "status="    + status );
		}
	}
	
	//添加根据单据号过滤
	add_sheetid_prams(parms);
	return parms;
}

function add_sheetid_prams(parms){
	var str = $F('txt_sheetid');
	if(str!='') {
		str = str.replace( / +/g, ',' );		// 把空格换成逗号
		str = str.replace( /,+/g, ',' );		// 把多个逗号换成一个逗号
		var arr_id = str.split ( ',' );
		for( var i=0; i<arr_id.length; i++ ) {
			var s = arr_id[ i ];
			if( s != '' ) parms.push( "sheetid=" + s );
		};
	}
	var str = $F('txt_sheetid_purchase');
	if(str!='') {
		str = str.replace( / +/g, ',' );		// 把空格换成逗号
		str = str.replace( /,+/g, ',' );		// 把多个逗号换成一个逗号
		var arr_id = str.split ( ',' );
		for( var i=0; i<arr_id.length; i++ ) {
			var s = arr_id[ i ];
			if( s != '' ) parms.push( "sheetid_purchase=" + s );
		};
	}
	
	var str = $F('txt_shopid');
	if(str!='') {
		str = str.replace( / +/g, ',' );		// 把空格换成逗号
		str = str.replace( /,+/g, ',' );		// 把多个逗号换成一个逗号
		var arr_id = str.split ( ',' );
		for( var i=0; i<arr_id.length; i++ ) {
			var s = arr_id[ i ];
			if( s != '' ) parms.push( "destshopid=" + s );
		};
	}
}

//处理查询结果
function analyse_search( text ){
	setLoading(false);
	table4search.setTable("xdoc/xout/rowset");
	table4search.setXML( text );
	if( table4search.getErrCode() != 0 ){	//处理xml中的错误消息
		div_cat.innerHTML = table4search.getErrNote() ;
		return;
	}
	
	//输出到页面
	div_cat.innerHTML = result_grid();
	
	//记录所有单据号到数组
	var node_sheetid 	= table4search.getXMLNodes( "/xdoc/xout/rowset/row/sheetid" );
	arr_sheetid = new Array();
	for ( var i = 0; i < node_sheetid.length; i++) {
		var node = node_sheetid[i];
		arr_sheetid.push(table4search.getNodeText(node));
	}
	if( arr_sheetid !=null && arr_sheetid.length >0 ) current_sheetid = arr_sheetid[0];
}
//将查询结果转换为grid
function result_grid(){

	table4search.setColumns([ "sheetid", "status", "purchasetype", "validdays", "orderdate", "majorname", "logistics", "note", "releasedate" ] );
	var columnNames = [ "订货审批单号", "状态", "补货标识", "有效期（天）", "订货日期", 
		"课类", "物流模式", "备注", "上传时间" ];
	
	var grid = new AW.UI.Grid;
	var row_count = table4search.getCount();
	grid.setId( "grid_cat" );
	grid.setColumnCount( columnNames.length );
	grid.setRowCount( row_count );	
	grid.setHeaderText( columnNames );
	
	grid.setSelectorVisible(true);
	grid.setSelectorWidth(30);
	grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
	
	var grid_link = new AW.Templates.Link;
	grid_link.setEvent( "onclick",
		function(){
			var sheetid = grid.getCellValue( 0, grid.getCurrentRow() );
			open_sheet_detail( sheetid );
		}
	);	
	grid.setCellTemplate( grid_link, 0 ); 	
	grid.setCellModel(table4search);
	//grid.sort(0,"descending");		// 目录按单据号逆序排列
	var sumcount = table4search.getXMLNode("/xdoc/xout/rowset").getAttribute("row_total");
	return "查询结果：总计："+sumcount+"行， 显示："+row_count+"行"+grid.toString();
}

//显示单据明细
function open_sheet_detail ( sheetid )
{
	current_sheetid = sheetid;
	tag_grp.setSelectedItems( [2] );
}

//加载明细
function load_purchase(){
	if( current_sheetid == null || current_sheetid.length == 0 ) return ;
	var url = "../DaemonSheet?operation=show&clazz=Purchasechk&sheetid=" + current_sheetid;
	table4detail.setURL( url );
	table4detail.request();
	table4detail.response = function( text ){
		table4detail.setXML(text);
		div_sheethead.innerHTML = table4detail.transformNode( format_show_xsl );
		setLoading(false);
		//修改单据状态为已阅读
		if(table4detail.getXMLText("xdoc/xout/sheet/head/row/status")==0){
			read_already( current_sheetid );
		}
		//记录送货模式和订货通知单号
		g_logistics = table4detail.getXMLText( "xdoc/xout/sheet/head/row/logistics" );
		var node_row 	= table4detail.getXMLNodes( "xdoc/xout/sheet/body/row/sheetid" );
		g_orderid = new Array();
		for ( var i = 0; i < node_row.length; i++) {
			var node = node_row[i];
			g_orderid.push(table4detail.getNodeText(node));
		}
	};
	setLoading(true);
}

// 浏览单据
function sheet_navigate ( step )
{
	var offset = 0;
	if( arr_sheetid == null || arr_sheetid.length == 0 ) { alert( "目录是空的!" ); return false; }
	for( var i = 0; i<arr_sheetid.length; i++ ) {
		if( current_sheetid == arr_sheetid[ i ] ) {
			offset = i;
			break;
		}
	}
	
	offset += step;
	if( offset < 0 ) { alert ( "已经是第一份单据!" ); return false; }
	if( offset >= arr_sheetid.length ) { alert ( "已经是最后一份单据!" ); return false; }
	//显示位置
	offset_current.innerText = "当前第：" + (offset+1) + "单，共有：" + arr_sheetid.length + "单";	
	var sheetid = arr_sheetid[ offset ];
	open_sheet_detail( sheetid );
}

// 门店打印时浏览单据
function shop_sheet_navigate ( step )
{
	var offset = 0;
	if( arr_sheetid == null || arr_sheetid.length == 0 ) { alert( "目录是空的!" ); return false; }
	for( var i = 0; i<arr_sheetid.length; i++ ) {
		if( current_sheetid == arr_sheetid[ i ] ) {
			offset = i;
			break;
		}
	}
	
	offset += step;
	if( offset < 0 ) { alert ( "已经是第一份单据!" ); return false; }
	if( offset >= arr_sheetid.length ) { alert ( "已经是最后一份单据!" ); return false; }
	//显示位置
	shop_offset_current.innerText = "当前第：" + (offset+1) + "单，共有：" + arr_sheetid.length + "单";	
	current_sheetid = arr_sheetid[ offset ];
	show_shop_catalotue();
}

//单份订单的确认
function confirm_sheet()
{
	var sheetid = current_sheetid;
	var url = "../DaemonSheet?operation=doConfirm&clazz=Purchasechk&sheetid=" + sheetid;
	table4confirm.setURL( url );
	table4confirm.request();
	table4confirm.response = function( text ){
		table4confirm.setXML( text );
		if( table4confirm.getErrCode() != 0 ){
			alert( "单据"+sheetid+"确认失败" );
		}else{
			alert( "单据"+sheetid+"确认成功" );
		}
	};
}

//打开一个新窗口,显示打印视图
function open_win_print()
{
	//打印订单后，更改单据状态为确认订单
	if(table4detail.getXMLText("xdoc/xout/sheet/head/row/status")<10){
		var url = "../DaemonSheet?operation=doConfirm&clazz=Purchasechk&sheetid=" + current_sheetid;
		table4confirm2.setURL( url );
		table4confirm2.request();
	}
	
	//直送订单直接打开订货通知单打印界面
	if(g_logistics==1){
		var url = "purchase_print.jsp?sheetid=" + g_orderid.join(",");
		var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
		",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
	 	window.open( url, current_sheetid,attributeOfNewWnd);	
	}else{
		var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
			",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
	
	 	window.open( "order_print.jsp?sheetid="+current_sheetid, current_sheetid,attributeOfNewWnd);		
	}
}
function downloadsheet(){
	var url="../DaemonDownloadExcel?sheetid="+current_sheetid+"&operation=order_sheet";
	window.location.href = url;
}


//批量处理
function show_confirm_catalotue()
{
	var row_count;
	row_count = table4search.getCount();
	if( row_count == 0 ) return;
	
	row_selected = new Array();
	
	table4search.setColumns([ "abc", "sheetid", "status", "validdays", "orderdate", "majorname", "logistics", "note", "releasetime" ] );
	var columnNames = [ "", "订货审批单号", "状态", "有效期（天）", "订货日期", 
		"课类", "物流模式", "备注", "上传时间" ];

	var grid = new AW.UI.Grid;
	grid.setId( "grid_con" );
	grid.setColumnCount( columnNames.length );
	grid.setRowCount( row_count );
	grid.setHeaderText( columnNames );
	grid.setSelectionMode("multi-row-marker");
	grid.onSelectedRowsChanged = function(ids){
		for(var i=0; i<ids.length; i++) {
			if(grid.getCellValue(6,ids[i])=='直送'){
				grid.setSelectedRows(row_selected);
				alert("直送订单不支持批量打印，请转到【单据明细】打印");
				return true;
			}
		}
       	row_selected = ids;
		return true;
    };
    	
	grid.setCellModel(table4search);
	grid.sort(6,"descending");		// 目录按单据号逆序排列
	$("div_confirm4all").innerHTML = grid.toString();
}

//多份单据打印
function print_more(){
	var array_selected = new Array();
	for(var i=0;i<row_selected.length;i++)
	{
		array_selected.push( arr_sheetid[row_selected[i]] );
	}
	if( array_selected.length == 0 ){
		alert( "请选择要打印的单据!" );
		return;
	}

	var url = "order_print.jsp?sheetid=" + array_selected.join(",");
	
	var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
	",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
 	window.open( url, "batch_print",attributeOfNewWnd);	
}

//显示按门店打印
function show_shop_catalotue()
{
	if( current_sheetid == null ){	return;	}
	var parms = new Array();
	parms.push( "refsheetid="+current_sheetid );
	parms.push( "operation=search" );
	parms.push( "clazz=Purchase" );
	var url = "../DaemonSheet?" + parms.join( '&' );
	
	table4shopprint.setURL( url );
	table4shopprint.response = shopprint_grid;
	table4shopprint.request();
	
	setLoading(true);
}

function shopprint_grid( text ){
	setLoading(false);
	table4shopprint.setTable("xdoc/xout/rowset");
	table4shopprint.setXML( text );
	if( table4search.getErrCode() != 0 ){	//处理xml中的错误消息
		div_shop_catalotue.innerHTML = table4search.getErrNote() ;
		return;
	}
	
	var row_count;
	row_count = table4shopprint.getCount();
	if( row_count == 0 ) return;
	
	print_selected = new Array();
	
	table4shopprint.setColumns( [ "index","refsheetid","sheetid", "shopid", "shopname", 
				 "logisticsname", "orderdate", "deadline", "editor", "checker" ] );
			
	var columnNames = [ "","订货审批单号","订货通知单号", "门店", "门店名称",
					 "送货方式", "订货日期", "截止日期", "编辑人", "审核人" ];

	var grid = new AW.UI.Grid;
	grid.setId( "grid_print" );
	grid.setColumnCount( columnNames.length );
	grid.setRowCount( row_count );
	grid.setHeaderText( columnNames );
	grid.setSelectionMode("multi-row-marker");
	grid.onSelectedRowsChanged = function(arrayOfRowIndices){
       	print_selected = arrayOfRowIndices;
		return true;
    };
    	
	grid.setCellModel(table4shopprint);
	//grid.sort(1,"descending");		// 目录按单据号逆序排列
	grid.setFooterText(["","查询结果:"+row_count+"条"]);
	grid.setFooterVisible(true);
	div_shop_catalotue.innerHTML = grid.toString();
}

//多份订单据打印
function print_all(){
	if( print_selected == null || print_selected.length == 0 ) {
		alert( "请选择要打印的单据!" );
		return;
	}
	
	var array_selected = new Array();
	for(var i=0;i<print_selected.length;i++)
	{
		array_selected.push( table4shopprint.getData(2,print_selected[i]) );
	}
	if( array_selected.length == 0 ){
		alert( "请选择要打印的单据!" );
		return;
	}

	var url = "purchase_print.jsp?sheetid=" + array_selected.join(",");
	
	var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
	",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
 	window.open( url, "batch_print",attributeOfNewWnd);	
}

//修改订单状态为已阅读
function read_already( sheetid )
{
	var url = "../DaemonSheet?operation=doRead&clazz=Purchasechk&sheetid=" + sheetid;
	
	table4confirm.setURL( url );
	table4confirm.response = function(text){
		table4confirm.setXML( text );
		if( table4confirm.getErrCode() != 0 ){
			window.status = "单据"+current_sheetid+"已阅读" ;
		}
	};
	table4confirm.request();
}