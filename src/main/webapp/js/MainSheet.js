/**
 * @param service
 * @return
 */
var Sheet = function(service){
	//
	this.enRead=true,this.enConfirm=true;
	this.enSearch=true,this.enExcel=true;
	this.arr_sheetid = null;
	this.current_sheetid = null;
	this.offset = 0;
	this.tag_grp 	   = new AW.UI.Tabs;
	this._table 	   = new AW.XML.Table;
	this._table4search = new AW.XML.Table;
	this._table4detail = new AW.XML.Table;
	this._table4confirm = new AW.XML.Table;
	this._btn_excel = null;
	this._btn_search= null;
	this.cmid = 0;
	
	var _self = this;
	this._cols = [],//列 对应返回的英文数据列数组
	this._colNames = [],//列对应的显示名称 必须与colID一一对应
	this._colIdx = [],//显示列序列号
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
	this.disabledExcel = function(){
		this.enExcel = false;
	};
	this.disabledSearch = function(){
		this.enSearch = false;
	};
	
	this.init = function(){
		this.cmid = window.location.href.getQuery("cmid");
		if(this.enSearch){
			this._btn_search = new AW.UI.Button;
			this._btn_search.setControlText( "查询" );
			this._btn_search.setId( "btnsearch" );
			this._btn_search.setControlImage( "search" );	
			this._btn_search.onClick = function(){_self.search_sheet();};
			$('div_button_search').innerHTML=this._btn_search;
		}
		if(this.enExcel){
			this._btn_excel = new AW.UI.Button;
			this._btn_excel.setControlText( "导出" );
			this._btn_excel.setId( "btnexcel" );
			this._btn_excel.setControlImage( "excel" );	
			this._btn_excel.onClick = function(){_self.excel_sheet();};
			$('div_button_excel').innerHTML=this._btn_excel;
		}
		
		var params = [];
		params.push("operation=cminit");
		params.push("cmid="+this.cmid);
		params.push("service="+service);
		var url = "../DaemonMain?"+params.join("&");
		setLoading(this._loadMark);
		this._table.setURL(url);
		this._table.setTable("xdoc/xout/rowset");
		this._table.setRows("row");
		this._table.request();
		this._table.response = function(text){
			setLoading(false);
			_self._table.setXML(text);//读取服务器返回的xml信息
			if( _self._table.getErrCode() != '0' ){//处理xml中的错误消息
				alert( _self._table.getErrNote() );
				return;
			}
			_self.toHTML();
			
			//自动加载状态查询
			var status = window.location.href.getQuery("g_status");
			if(status!=null){
				if( status != -1 ){
					$("txt_status").value = status;
					_self.search_sheet();
				}
			}
			_self.install_tags();
		};
	};
	
	this.toHTML=function(){
		$("divTitle").innerHTML = this._table.getXMLText("xdoc/xout/rowset/title");
		$("divFooter").innerHTML = this._table.getXMLText("xdoc/xout/rowset/footer");
		var strhtml="";
		var nodes = this._table.getXMLNodes("xdoc/xout/rowset/search/input");
		for ( var i = 0; i < nodes.length; i++) {
			var col = nodes[i];
			var field = col.getAttribute("field");
			var name = col.getAttribute("name");
			var vtype = col.getAttribute("vtype");
			var width = col.getAttribute("width");
			var css = col.getAttribute("css");
			if(vtype=0) vtype="text";
			strhtml+="<span>"+name+":<input name='searchInput' id='txt_"+field+"' type='"+vtype+"'/></span>&nbsp;&nbsp;";
		}
		//$("divSearch").innerHTML=strhtml;
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
	 	window.open( "./print.jsp?cmid="+this.cmid+"&service="+service+"&sheetid="+this.current_sheetid, this.current_sheetid, attributeOfNewWnd );		
	};
	
	this.open_win_show = function(cmid,cls,sheetid)
	{	
		if(sheetid==null || sheetid=="")
			return;
		var attributeOfNewWnd = "status=yes,toolbar=no,menubar=yes,resizable=1,scrollbars=1,left=0,top=0,fullscreen=0"+
			",width=" +(window.screen.width-10)+",height="+(window.screen.height-80);
	 	window.open( "./show.jsp?cmid="+cmid+"&service="+cls+"&sheetid="+sheetid, sheetid, attributeOfNewWnd );		
	};
	
	//初始化列模板
	this.initColModel = function(){
		var cols = this._table4search.getXMLNodes("xdoc/xout/rowset/colmodel/col");
		var colsidx = this._table4search.getXMLText("xdoc/xout/rowset/colmodel/colidx");
		this._colIdx = eval(colsidx);
		if(cols.length==0){alert("读取列定义失败!");return;}
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
			this._colModel[i] = new ColModel(vtype==null?0:vtype,width==null?0:width,css==null?"":css,render==null?"":render);
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
			var vtype = Number(this._colModel[i]["vtype"]);
			var width = this._colModel[i]["width"];
			var css = this._colModel[i]["css"];
			var r = this._colModel[i]["render"];
			if(vtype==0||vtype==null){this._format[i]=str;}
			else if(vtype==1){this._format[i]=num;css+="color:blue;text-align:right;";}
			else if(vtype==2){this._format[i]=money;css+="color:blue;text-align:right;";}
			//设置宽度
			this._grid.setColumnWidth(width,i);
			//设置列样式
			if(css!=''){
				this._grid.getCellTemplate(i).setStyle(css);
			}
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
	
	this.search_sheet = function(){
		if(!this.check()){return;}
		
		var url = "../DaemonMain?cmid="+this.cmid+"&operation=search&service="+service+"&" + this.cookParms().join( '&' );
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
			var node_sheetid 	= _self._table4search.getXMLNodes( "/xdoc/xout/rowset/row/sheetid" );
			for ( var i = 0; i < node_sheetid.length; i++) {
				var node = node_sheetid[i];
				_self.arr_sheetid.push(_self._table4search.getNodeText(node));
			}
			if( _self.arr_sheetid.length >0 ) _self.current_sheetid = _self.arr_sheetid[0];
			
			_self.show_catalogue();
		};
		setLoading( true );
	};
	this.initGrid = function(){
		
	};
	//表格处理后过程
	this.endInitGrid = function(){
		
	};
	this.show_catalogue = function(){
		var row_count = this._table4search.getCount();
		if(row_count==0){$("div1").innerHTML ="没有符合条件的数据";return;}
		this._grid = new AW.Grid.Extended;
		this.initGrid();
		//初始化列
		this.initColModel();
		this._grid.setId('sheet_cat_grid');
		this._table4search.setColumns( this._cols );
		this._grid.setColumnIndices(this._colIdx);
		this._grid.setColumnCount( this._colIdx.length );
		this._grid.setRowCount(row_count);
		this._grid.setHeaderText(this._colNames);
		this._grid.setCellFormat(this._format);
		this._grid.setSelectionMode("multi-cell");
		
		//初始化列格式
		this.initFormat();
		//设置底角
		this._grid.setFooterVisible(true);
		this.initFooterText();
		
		this._grid.setSelectorVisible(true);
		this._grid.setSelectorWidth(30);
		this._grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
	
		var obj_link = new AW.Templates.Link;
		obj_link.setStyle("color:blue;cursor:pointer;");
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
		this.endInitGrid();
		$("div1").innerHTML = htmlCountInfo+this._grid.toString();
	};
	
	this.load_sheet_detail = function(sheetid){
		if(sheetid==null) return;
		var url = "../DaemonMain?cmid="+this.cmid+"&operation=show&service="+service+"&sheetid=" + sheetid;
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
	
	this.excel_sheet = function(){
		if(!this.check()){return;}
		var url = "../DaemonMainDownload?operation=cmexcel&cmid="+this.cmid+"&service="+service+"&" + this.cookParms().join( '&' );
		window.location.href = url;
	};
	
	this.doRead = function(sheetid){
		var url = "../DaemonMain?cmid="+this.cmid+"&operation=doRead&service="+service+"&sheetid=" + sheetid;
		this._table4confirm.setURL( url );
		this._table4confirm.request();
	};
	
	this.doConfirm = function( sheetid )
	{
		var url = "../DaemonMain?cmid="+this.cmid+"&operation=doConfirm&service="+service+"&sheetid=" + sheetid;
		this._table4confirm.setURL( url );
		this._table4confirm.request();
	};
	
	//根据列名获取列顺序
	this.getColIdx = function(colname){
		var res = -1;
		for ( var i = 0; i < this._cols.length; i++) {
			if(colname==this._cols[i]){
				res=i;
				break;
			}
		}
		return res;
	};
	
	//验证查询条件，可重载
	this.check = function(){
		var rel = autoCheck();
		return rel;
	};
	
	//组成查询条件，可重载
	this.cookParms = function(){
		var parms = autoParms();
		return parms;
	};
};

function ColModel(vtype,width,css,render){
	this.vtype = vtype;
	this.width = width;
	this.css = css;
	this.render = render;
}