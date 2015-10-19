/*
 * 封装AW.Grid.Control类，实现可编辑表格
 * baijian
 */

var formatMoney = new AW.Formats.Number;
formatMoney.setTextFormat("#,##0.00");
var formatStr = new AW.Formats.String;
var formatNum = new AW.Formats.Number;
var EditGrid = function(service){
	this.enSearch=true;
	this.enExcel=true;
	this.enSave = true;
	this.enAdd = true;
	this.enDel = true;
	this.cmid = -1;
	
	this._btn_search;
	this._btn_excel;
	this._btn_save;
	this._btn_add;
	this._btn_del;
	
	var _self = this; //全局指向
	/*内部私有变量*/
	_cols = [],//列 对应返回的英文数据列数组
	_colNames = [],//列对应的显示名称 必须与colID一一对应
	_footer = [],//表底
	_format = [],//表格显示格式
	_colModel = [],//列模型
	_colIdx = [],//显示列序列号
	_editCellIdx = [], //可编辑列序列号
	_loadMark = true,
	this._grid = null,
	this._table = new AW.XML.Table;
		
	this.disabledExcel = function(){
		this.enExcel = false;
	};
	this.disabledSearch = function(){
		this.enSearch = false;
	};
	this.disabledSave = function(){
		this.enSave = false;
	};
	this.disabledAdd = function(){
		this.enAdd = false;
	};
	this.disabledDel = function(){
		this.enDel = false;
	};
	
	this.setEditCellIdx = function(idxs){
			_editCellIdx = idxs;
	};
	
	this.init=function(){
		this.cmid = window.location.href.getQuery("cmid");
		if(this.enSearch){
			this._btn_search = new AW.UI.Button;
			this._btn_search.setControlText( "查询" );
			this._btn_search.setId( "btnsearch" );
			this._btn_search.setControlImage( "search" );	
			this._btn_search.onClick = function(){_self.search();};
			$('div_button_search').innerHTML=this._btn_search;
		}
		if(this.enExcel){
			this._btn_excel = new AW.UI.Button;
			this._btn_excel.setControlText( "导出" );
			this._btn_excel.setId( "btnexcel" );
			this._btn_excel.setControlImage( "excel" );	
			this._btn_excel.onClick = function(){_self.excel();};
			$('div_button_excel').innerHTML=this._btn_excel;
		}
		if(this.enSave){
			this._btn_save = new AW.UI.Button;
			this._btn_save.setControlText( "保存" );
			this._btn_save.setId( "btnsave" );
			this._btn_save.setControlImage( "save" );	
			this._btn_save.onClick = function(){_self.save();};
			$('div_button_save').innerHTML=this._btn_save;
		}
		var params = [];
		params.push("operation=cminit");
		params.push("cmid="+this.cmid);
		params.push("service="+service);
		var url = "../DaemonMain?"+params.join("&");
		
		setLoading(_loadMark);
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
		};
	};
	
	//以html格式插入到摸个页面标签中，一般是div
	var renderTo=function(id,str){
		var e=document.getElementById(id);
		if(e==null){
			if(!(e=document.getElementById("divReportGrid"))){
				e=document.createElement('div');
				e.setAttribute("id", "divReportGrid");
				document.body.appendChild(e);
			}
		}
		e.innerHTML = str;
	};
	
	//初始化列模板
	this.initColModel = function(){
		var cols = this._table.getXMLNodes("xdoc/xout/rowset/colmodel/col");
		var colsidx = this._table.getXMLText("xdoc/xout/rowset/colmodel/colidx");
		_colIdx = eval(colsidx);
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
			_cols[i] = field;
			_colNames[i] = name;
			_footer[i] = sum;
			_colModel[i] = new ColModel(vtype,width,css,render);
		}
	};
	
	this.initFooterText = function(){
		for ( var i = 0; i < _footer.length; i++) {
			var f = _footer[i];
			if(f==1){_footer[i]=formatMoney.dataToText(this._table.getCellSum(i));}else{_footer[i]="";}
		}
	};
	
	this.initFormat = function(){
		for ( var i = 0; i < _colModel.length; i++) {
			var vtype = _colModel[i]["vtype"];
			var width = _colModel[i]["width"];
			var css = _colModel[i]["css"];
			var r = _colModel[i]["render"];
			
			if(vtype==0||vtype==null){_format[i]=formatStr;}
			else if(vtype==1){_format[i]=formatNum;}
			else if(vtype==2){_format[i]=formatMoney;}
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
		//初始化表格
		this.initGrid();
		renderTo( null,this._grid.toString());
	};
	
	this.initGrid = function(rowCount){
		this._grid = new AW.Grid.Editor;
		this.initColModel();
		this.initFormat();
		this._grid.setId('editor_list_grid');
		this._table.setColumns(_cols);
		this._grid.setColumnIndices(_colIdx);
		this._grid.setColumnCount( _colIdx.length );
		this._grid.setRowCount(rowCount);
		this._grid.setHeaderText(_colNames);
		this._grid.setFooterVisible(true);
		this._grid.setFooterText(_footer);
		this._grid.setCellFormat(_format);
		this._grid.setSelectionMode("multi-cell");
		this._grid.setNodeNames(_cols);
		this._grid.setCellModel(this._table);
		this._grid.setSelector();
		this.initFooterText();
		for ( var i = 0; i < _editCellIdx.length; i++) {
			this._grid.setCellEditable(true,_editCellIdx[i]);
		};
		
		this.endInitGrid();
	};
	
	//表格处理后过程
	this.endInitGrid = function(){
	};
	
	this.load=function(url,render){
		setLoading(_loadMark);
		this._table.setURL(url);
		this._table.setTable("xdoc/xout/rowset");
		this._table.setRows("row");
		this._table.request();
		this._table.response = function(text){
			setLoading(false);
			_self._table.setXML(text);//读取服务器返回的xml信息
			if( _self._table.getErrCode() != '0' ){//处理xml中的错误消息
				renderTo(render,_self._table.getErrNote());
				return;
			}
			
			//读取记录总数
			var totalCount = _self._table.getXMLNode("xdoc/xout/rowset").getAttribute("row_total");
			var count = _self._table.getCount();
			
			//数据统计信息
			var htmlCountInfo = "记录总数"+totalCount+"行；当前显示"+count+"行。<br>";
			if(count<totalCount){
				var htmlCountInfo = "记录总数"+totalCount+"行；当前显示"+count+"行。请尝试修改查询条件查看未显示数据<br>";
			}
			
			_self.initGrid(count);
			renderTo( render,htmlCountInfo +_self._grid.toString());
		};
	};
	
	this.excel=function(){
		if(!this.check()){return;}
		var url = "../DaemonMainDownload?operation=cmexcel&cmid="+this.cmid+"&service="+service+"&" + this.cookParms().join( '&' );
		window.location.href = url;
	};
	
	this.search=function(){
		if(!this.check()){return;}
		var url = "../DaemonMain?cmid="+this.cmid+"&operation=search&service="+service+"&" + this.cookParms().join( '&' );
		this.load(url);
	};
	
	this.save=function(){
		var reqData = grid.toJSON();
		var jo = JSON.parse(reqData);
		if(jo.rowCount ==0){
			alert("没有数据被更新");
			return;
		}
		
		if(!this.check()){return;}
		setLoading(true);
		var table = new AW.XML.Table;
		table.setURL("../DaemonMain?service="+service+"&operation=update");
		table.setRequestMethod('POST');
		table.setRequestData(reqData);
		table.request();
		table.response = function (text) {
			setLoading(false);
			table.setXML(text);
			var xcode = table.getXMLText("xdoc/xerr/code");
			if (xcode != 0) {
				alert(xcode + table.getXMLText("xdoc/xerr/note"));
			} else {
				alert("保存成功！");
				_self.search();
			}
		};
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
};

function ColModel(vtype,width,css,render){
	this.vtype = vtype;
	this.width = width;
	this.css = css;
	this.render = render;
}

function render(value){
	if(value=='admin'){
		return "管理员";
	}
	return value;
}
