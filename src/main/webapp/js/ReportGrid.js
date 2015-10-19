/*
 * 封装AW.Grid.Control类，实现模块化报表类
 * baijian
 * 2010-01-30
 * <xdoc>
 * 	<xout>
 * 		<rowset>
	      <colmodel>
	        <col id="mailid" name="邮件id" type="number" width="50" css="color:blue;text-align:right;" sum="true" ></col>
	        <col id="sender" name="发件人" type="string" css="" sum="false"></col>
	        <col id="sendtime" name="发送时间" type="date" width="200" css="color:red;" sum=""></col>
	        <col id="receiptor01" name="收信人" type="string" css="" sum="" render="render"></col>
	      </colmodel>
 		  <data>
		  </data>
 * 		</rowset>
 * 	</xout>
 * </xdoc>
 */

var formatMoney = new AW.Formats.Number;
formatMoney.setTextFormat("#,##0.00");
var formatStr = new AW.Formats.String;
var formatNum = new AW.Formats.Number;

var ReportGrid = function(){
	var _self = this, 
		_cols = [],//列 对应返回的英文数据列数组
		_colNames = [],//列对应的显示名称 必须与colID一一对应
		_footer = [],
		_format = [],
		_colModel = [],
		_loadMark = true,
		_grid = null,
		_table = new AW.XML.Table;
		
	//设置列数组
	this.setCols=function(cols){
		_cols = cols;
	};
	this.setColNames=function(names){
		_colNames = names;
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
	var initColModel = function(){
		var cols = _table.getXML().selectNodes("xdoc/xout/rowset/colmodel/col");
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
			_cols[i] = field;
			_colNames[i] = name;
			_footer[i] = sum;
			_colModel[i] = new ColModel(vtype,width,css,render);
			
		}
	};
	
	var initFooterText = function(){
		for ( var i = 0; i < _footer.length; i++) {
			var f = _footer[i];
			if(f==1){_footer[i]=formatMoney.dataToText(_table.getCellSum(i));}else{_footer[i]="";}
		}
	};
	
	var initFormat = function(){
		for ( var i = 0; i < _colModel.length; i++) {
			var vtype = _colModel[i]["vtype"];
			var width = _colModel[i]["width"];
			var css = _colModel[i]["css"];
			var r = _colModel[i]["render"];
			
			if(vtype==0||vtype==null){_format[i]=formatStr;}
			else if(vtype==1){_format[i]=formatNum; css+="color:blue;text-align:right;";}
			else if(vtype==2){_format[i]=formatMoney;css+="color:blue;text-align:right;";}
			//设置宽度
			width!=null?_grid.setColumnWidth(width,i):null;
			//设置列样式
			_grid.getCellTemplate(i).setStyle(css);
			//设置数据转义
//			if(r!=null){
//				_grid.setCellData(eval(r)('admin'),1);
//				_grid.getCellTemplate(i).setCellValue(eval(r)('admin'));
//			}
		}
	};
	var toHTML=function(){
		$("divTitle").innerHTML = _table.getXMLText("xdoc/xout/rowset/title");
		$("divFooter").innerHTML = _table.getXMLText("xdoc/xout/rowset/footer");
		var strhtml="";
		var cols = _table.getXML().selectNodes("xdoc/xout/rowset/search/input");
		for ( var i = 0; i < cols.length; i++) {
			var col = cols[i];
			var field = col.getAttribute("field");
			var name = col.getAttribute("name");
			var vtype = col.getAttribute("vtype");
			var width = col.getAttribute("width");
			var css = col.getAttribute("css");
			if(vtype=0) vtype="text";
			strhtml+="<span>"+name+":<input name='searchInput' id='txt_"+field+"' type='"+vtype+"'/></span>&nbsp;&nbsp;";
		}
		strhtml +="<input type='button' onclick='search()' value='查询'>";
		//$("divSearch").innerHTML=strhtml;
	};
	this.load=function(url,render){
		setLoading(_loadMark);
		_grid = new AW.Grid.Extended;
		_table.setURL(url);
		_table.setTable("xdoc/xout/rowset");
		_table.setRows("row");
		_table.request();
		_table.response = function(text){
			setLoading(false);
			_table.setXML(text);//读取服务器返回的xml信息
			if( _table.getErrCode() != '0' ){//处理xml中的错误消息
				renderTo(render,_table.getErrNote());
				return;
			}
			//读取记录总数
			var totalCount = _table.getXMLText("xdoc/xout/rowset/totalCount");
			var count = _table.getCount();
			//数据统计信息
			var htmlCountInfo = "记录总数"+totalCount+"行；当前显示"+count+"行。<br>";
			if(count<totalCount){
				var htmlCountInfo = "记录总数"+totalCount+"行；当前显示"+count+"行。请尝试修改查询条件查看未显示数据<br>";
			}
			
			//初始化列
			initColModel();
			//初始化列格式
			initFormat();
			_table.setColumns(_cols);
			_grid.setColumnCount( _cols.length );
			_grid.setRowCount(count);
			_grid.setHeaderText(_colNames);
			_grid.setFooterVisible(true);
			_grid.setFooterText(_footer);
			_grid.setCellFormat(_format);
			_grid.setSelectionMode("multi-cell");
			_grid.setCellModel(_table);
			
			_grid.setSelectorVisible(true);
			_grid.setSelectorWidth(30);
			_grid.setSelectorText(function(i){return this.getRowPosition(i)+1;});
			
			//设置底角
			initFooterText();
			renderTo( render,htmlCountInfo +_grid.toString());
		};
	};
	
	this.initHTML=function(url){
		setLoading(_loadMark);
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
			toHTML();
		};
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
