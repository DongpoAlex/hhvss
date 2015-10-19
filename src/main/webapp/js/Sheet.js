/**
 * @param clazz
 * @return
 */
var Sheet = function(clazz){
	//
	this.enRead=true,this.enConfirm=true;
	this.arr_sheetid = null;
	this.current_sheetid = null;
	this.offset = 0;
	this.tag_grp 	 = new AW.UI.Tabs;
	this._table4search = new AW.XML.Table;
	this._table4detail = new AW.XML.Table;
	this._table4confirm = new AW.XML.Table;
	this._btn_search = new AW.UI.Button;
	this.cmid = 0;
	
	var _self = this;
	this._cols = [],//列 对应返回的英文数据列数组
	this._colNames = [],//列对应的显示名称 必须与colID一一对应
	this._footer = [],
	this._format = [],
	this._colModel = [],
	this._loadMark = true,
	this._grid = null;
	
	this.disabledRead = function(){
		this.enRead = false;
	};
	
	this.disabledConfirm = function(){
		this.enConfirm = false;
	};
	
	this.init = function(){
		this.cmid   = window.location.href.getQuery("cmid");
		
		this._btn_search.setControlText( "查询" );
		this._btn_search.setId( "btncheck" );
		this._btn_search.setControlImage( "search" );	
		this._btn_search.onClick = function(){_self.search_sheet();};
		$('div_button_search').innerHTML=this._btn_search;
		
		var status = window.location.href.getQuery("g_status");
		if(status!=null){
			obj = $("txt_status");
			if( status != -1 ){
				obj.value = status;
				this.search_sheet();
			}
		}
		this.install_tags();
	};
	
	this.sheet_navigate = function(step){
		if( this.arr_sheetid == null || this.arr_sheetid.length == 0 ) { alert( "目录是空的!" ); return false; }
		for( var i = 0; i<this.arr_sheetid.length; i++ ) {
			if( this.current_sheetid == this.arr_sheetid[ i ] ) {
				this.offset = i;
				break;
			}
		}
		
		this.offset += step;
		if( this.offset < 0 ) { alert ( "已经是第一份单据!" ); return false; }
		if( this.offset >= this.arr_sheetid.length ) { alert ( "已经是最后一份单据!" ); return false; }
		this.open_sheet_detail( this.arr_sheetid[this.offset] );
	};
	
	this.allowPrint = function(){
		$("btn_print").style.display = "";
	};
	
	this.open_win_print = function()
	{	
		if(this.enConfirm && this._table4detail.getXMLText("xdoc/xout/sheet/head/row/status")<10){
			this.doConfirm(this.current_sheetid);
		}
		var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
			",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
	 	window.open( "../page/print.jsp?cmid="+this.cmid+"&clazz="+clazz+"&sheetid="+this.current_sheetid, this.current_sheetid, attributeOfNewWnd );		
	};
	
	//初始化列模板
	this.initColModel = function(){
		var cols = this._table4search.getXML().selectNodes("xdoc/xout/rowset/colmodel/col");
		if(cols==null){alert("读取列定义失败!");return;}
		for ( var i = 0; i < cols.length; i++) {
			var col = cols[i];
			var field = col.getAttribute("field");
			var name = col.getAttribute("name");
			var vtype = col.getAttribute("vtype");
			var width = col.getAttribute("width");
			var css = col.getAttribute("css");
			var sum = col.getAttribute("sum");
			var render = col.getAttribute("render");
			this._cols[i] = field;
			this._colNames[i] = name;
			this._footer[i] = sum;
			this._colModel[i] = new ColModel(vtype,width,css,render);
		}
	};
	
	this.initFooterText = function(){
		var num = new AW.Formats.Number;
		num.setTextFormat( "#,###.##" );
		for ( var i = 0; i < this._footer.length; i++) {
			var f = this._footer[i];
			if(f==1){this._footer[i]=num.dataToText(this._table4search.getCellSum(i));}else{this._footer[i]="";}
		}
	};
	
	this.initFormat = function(){
		var str = new AW.Formats.String;
		var num = new AW.Formats.Number;
		var money = new AW.Formats.Number;
		money.setTextFormat( "#,###.##" );
		for ( var i = 0; i < this._colModel.length; i++) {
			var vtype = this._colModel[i]["vtype"];
			var width = this._colModel[i]["width"];
			var css = this._colModel[i]["css"];
			var r = this._colModel[i]["render"];
			
			if(vtype==0||vtype==null){this._format[i]=str;}
			else if(vtype==1){this._format[i]=num;}
			else if(vtype==2){this._format[i]=money;}
			//设置宽度
			width!=null?this._grid.setColumnWidth(width,i):null;
			//设置列样式
			this._grid.getCellTemplate(i).setStyle(css);
			//设置数据转义
//			if(r!=null){
//				this._grid.setCellData(eval(r)('admin'),1);
//				this._grid.getCellTemplate(i).setCellValue(eval(r)('admin'));
//			}
		}
	};
	
	
	this.install_tags = function(){
		var tags = [ "查询条件", "单据目录", "单据明细" ];
		this.tag_grp.setId( "tag_grp" );
		this.tag_grp.setItemText(tags);
		this.tag_grp.setItemCount(tags.length );
		this.tag_grp.setSelectedItems( [0] );
		$('div_tabs').innerHTML=this.tag_grp.toString();
		
		this.tag_grp.onSelectedItemsChanged = function( idx ) {
			enactive(idx);
			if( idx == 2 ) {_self.load_sheet_detail( _self.current_sheetid );}
		};
	};
	
	var enactive = function(idx){
		for ( var i = 0; i < 3; i++) {
			if(idx==i){
				$('div'+i).style.display='block';
			}else{
				$('div'+i).style.display='none';
			}
		}
	};
	
	//验证查询条件，可重载
	var check = function(){
		var rel = autoCheck();
		return rel;
	};
	
	//组成查询条件，可重载
	var cookParms = function(){
		var parms = autoParms();
		return parms;
	};
	
	this.search_sheet = function(){
		if(!check()){return;}
		
		var url = "../DaemonSheet?cmid="+this.cmid+"&operation=search&clazz="+clazz+"&" + cookParms().join( '&' );
		this._table4search.setURL( url );
		this._table4search.request();
		this._table4search.response 	= function(text){
			setLoading(false);
			_self.tag_grp.setSelectedItems( [1] );
			_self._table4search.setTable("xdoc/xout/rowset");
			_self._table4search.setRows("row");
			_self._table4search.setXML( text );
			if( _self._table4search.getErrCode() != 0 ){	//处理xml中的错误消息
				$("div1").innerHTML = _self._table4search.getErrNote() ;
				return;
			}
			//记录所有单据号到数组
			_self.arr_sheetid = new Array();
			var node_sheetid 	= _self._table4search.getXML().selectNodes( "/xdoc/xout/rowset/row/sheetid" );
			var next_node = node_sheetid.nextNode();
			while( next_node != null ) {
				_self.arr_sheetid.push(next_node.text);
				next_node = node_sheetid.nextNode();
			}
			if( _self.arr_sheetid.length >0 ) _self.current_sheetid = _self.arr_sheetid[0];
			
			_self.show_catalogue();
		};
		setLoading( true );
	};
	
	this.show_catalogue = function(){
		var row_count = this._table4search.getCount();
		if(row_count==0){$("div1").innerHTML ="没有符合条件的数据";return;}
		this._grid = new AW.Grid.Extended;
		//初始化列
		this.initColModel();
		//初始化列格式
		this.initFormat();
		
		this._table4search.setColumns( this._cols );
		this._grid.setId('grid');
		this._grid.setColumnCount( this._cols.length );
		this._grid.setRowCount(row_count);
		this._grid.setHeaderText(this._colNames);
		this._grid.setFooterVisible(true);
		this._grid.setFooterText(this._footer);
		this._grid.setCellFormat(this._format);
		this._grid.setSelectionMode("multi-cell");
		//设置底角
		this.initFooterText();
		
		this._grid.setSelectorVisible(true);
		this._grid.setSelectorWidth(30);
		this._grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
	
		var obj_link = new AW.Templates.Link;
		obj_link.setEvent( "onclick",
			function(){
				var row = _self._grid.getCurrentRow();
				_self.offset = row;
				var sheetid = _self._grid.getCellValue( 0,row );
				_self.open_sheet_detail( sheetid );
			}
		);
		this._grid.setCellTemplate( obj_link, 0 ); 
		
		this._grid.setCellModel(this._table4search);
		var row_total = this._table4search.getXMLNode("/xdoc/xout/rowset").getAttribute("row_total");
		var htmlCountInfo = "记录总数"+row_total+"行；当前显示"+row_count+"行。<br>";
		if(row_count<row_total){
			var htmlCountInfo = "记录总数"+row_total+"行；当前显示"+row_count+"行。请尝试修改查询条件查看未显示数据<br>";
		}
		$("div1").innerHTML = htmlCountInfo+this._grid.toString();
	};
	
	this.load_sheet_detail = function(sheetid){
		var url = "../DaemonSheet?cmid="+this.cmid+"&operation=show&clazz="+clazz+"&sheetid=" + sheetid;
		this._table4detail.setURL( url );
		this._table4detail.request();
		this._table4detail.response = function(text){
			setLoading(false);
			_self._table4detail.setXML(text);
			var xsl = _self._table4detail.getXMLNode("/xdoc/xout/sheet").getAttribute("xsl");
			$("div_sheetshow").innerHTML = _self._table4detail.transformNode( xsl );
			
			//修改单据状态为已阅读
			if(_self.enRead && _self._table4detail.getXMLText("xdoc/xout/sheet/head/row/status")==0){
				_self.doRead( _self.current_sheetid );
			}
			
		};
		setLoading(true);
	};
	
	this.open_sheet_detail = function(sheetid){
		this.current_sheetid = sheetid;
		this.tag_grp.setSelectedItems( [2] );
		$("offset_current").innerText = "当前第：" + (Number(this.offset)+1) + "单，共有：" + this.arr_sheetid.length + "单";
	};
	
	this.doRead = function(sheetid){
		var url = "../DaemonSheet?cmid="+this.cmid+"&operation=doRead&clazz="+clazz+"&sheetid=" + sheetid;
		this._table4confirm.setURL( url );
		this._table4confirm.request();
	};
	
	this.doConfirm = function( sheetid )
	{
		var url = "../DaemonSheet?cmid="+this.cmid+"&operation=doConfirm&clazz="+clazz+"&sheetid=" + sheetid;
		this._table4confirm.setURL( url );
		this._table4confirm.request();
	};
};

function ColModel(vtype,width,css,render){
	this.vtype = vtype;
	this.width = width;
	this.css = css;
	this.render = render;
}