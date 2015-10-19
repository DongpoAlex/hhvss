/*
 * 继承AW.Grid.Control类，实现可编辑表格功能
 * baibai
 * 2007-08-30
 */
AW.Grid.Editor = AW.Grid.Control.subclass();
AW.Grid.Editor.create = function(){
	var obj = this.prototype;
	obj._serial=0;//内部的序列号
	obj._dataArray = [];//存放修改的数据数组，包括新增和删除的
	obj._nodeNames  = [];//节点名称，返回的xml数据将以此作为节点名

	//设置节点名数组
	obj.setNodeNames=function(names){
		this._nodeNames = names;
	};
	//获得节点名数组
	obj.getNodeNames=function(names){
		return this._nodeNames;
	};
	//设置列值
	obj.setCellData=function(value,col,row){
		this.replaceData(value, col, row);
		this.setCellText(value, col, row);
		this.setCellValue(value, col, row);
	};
	//获得列值
	obj.getEditData=function(col,row){//获得_dataArray的值
		var data = this._dataArray.length<=row?null:this._dataArray[row][col];
		return data==null?'':data;
	};
	//获得编辑行数
	obj.getEditDataCount=function(){
		return this._dataArray.length;
	};
	//清除一行
	obj.clearRowData=function(row){
		if(row == null || row == 'undefined'){
			row = this.getCurrentRow();
		}
		for(var i=0;i<this._nodeNames.length;i++){
			this.setCellText('', i, row);
			this.setCellValue('', i, row);
		}
		this.delData(row);
	};
	//增加一行
	obj.insertRow = function(){
		this.addRow(++this._serial);
	};
	//删除一行
	obj.delRow = function(){
		var rowid = this.getCurrentRow();
		if(rowid>=0){
			this.deleteRow( rowid );
			this.delData( rowid );
		}
	};

	
	//返回html格式数据
	obj.toHTML = function(){
		var str ='<table border=1>';
		for( var i=0; i<this._dataArray.length; i++ )
		{
			str += '<tr>';
			str += '<td>index : ' + this._dataArray[i].index +'</td>' ;
			for( var j=0; j<this._nodeNames.length; j++ )
			{
				var text = this._dataArray[i][j];
				text = (typeof(text)=='undefined')?"":text;
				str += '<td>' + this._nodeNames[j] + ' : ' + text + '<td>';
			}
			str += '</tr>';
		}

		str += '</table>';
		return str;
	};
	
	//返回xml格式数据
	obj.toXML = function(){
		var table = new AW.XML.Table;
		table.setXML("");
		var doc = table.getXML("");
		var elmSet = doc.createElement("rowset");
		for(var i=0; i<this._dataArray.length; i++){
			var elmRow = doc.createElement("row");
			var r = doc.createAttribute("id");
			r.value=this._dataArray[i].index;
			if(r.value=='') continue;
			elmRow.setAttributeNode(r);
			for( var j=0; j<this._nodeNames.length; j++ ){
				var elmTemp = doc.createElement(this._nodeNames[j].replace("@",""));
				var text = this._dataArray[i][j];
				text = (typeof(text)=='undefined')?"":text;
				elmTemp.appendChild(doc.createTextNode(text));
				elmRow.appendChild(elmTemp);
			}
			elmSet.appendChild(elmRow);
		}

		return elmSet;
	};

	/**
	* 内部函数部分
	*/
	//（内部函数）
	obj.buildObject=function( index, values ){//构建一个对象，该对象以nodeNames作为成员,以及一个内部的index序号
		var o = new Object;
		o['index'] = index;
		for(var i=0; i<this._nodeNames.length; i++){
			o[this._nodeNames[i]] = values[i];
		}
		return o;
 	};
	//（内部函数）
 	obj.getIndex = function( rowid ){//根据grid的rowid获得内部唯一的序号
		for( var i=0; i<this._dataArray.length; i++ ){
			if( this._dataArray[i].index == rowid ){
				return i;
			}
		}
		return -1;
 	};
	//（内部函数）
 	obj.addData = function( index ){//添加数据到_dataArray
		var values = new Array(this._nodeNames.length);
		var new_obj = this.buildObject(index, values );
		this._dataArray.push(new_obj);
 	};
	//（内部函数）
 	obj.delData = function( rowid ){//将指定rowid的数据从_dataArray中清除
 		var i = this.getIndex( rowid );
		if (i == -1){}
		else{this._dataArray.splice(i, 1);}
 	};
	//（内部函数）
	obj.replaceData = function( value, col, row ){//替换数据
		var i = this.getIndex( row );
		if( i == -1 ) this.addData( row );
		i = this.getIndex( row );
		this._dataArray[ i ][ col ]= value;
	};
	//当输入的完成，赋值到dataArray
	obj.onCellEditEnded = function( text, col, row ){
		if(text!=null && text.length!=0)
			this.replaceData( text, col, row );
	};
	var setCellModel = obj.setCellModel;//重载设置grid设置数据table方法
	obj.setCellModel = function (model) {
		setCellModel.call(this, model);
		function dataToText(i, j) {
			var data = this.getCellData(i, j);
			var format = this.getCellFormat(i, j);
			return format ? format.dataToText(data) : data;
		}
		function dataToValue(i, j) {
			var data = this.getCellData(i, j);
			var format = this.getCellFormat(i, j);
			var rel = format ? format.dataToValue(data) : data;
			return rel;
		}
		this.setCellText(dataToText);
		this.setCellValue(dataToValue);
		for(var k=0;k<model.getCount();k++){
			for(var j=0;j<model._valuesPath.length;j++){
				this.replaceData(model.getData(j,k),j,k);
			}
			this._serial++;
		}
	};

	//自定义属性
	//行号
	obj.setSelector = function(){
		this.setSelectorVisible(true);
		this.setSelectorWidth(30);
		this.setSelectorText(function(i){return this.getRowPosition(i)+1;});
	};
};

function cookNodeDate(arr){
	var arr_rel = new Array();
	for ( var i = 0; i < arr.length; i++) {
		var name = "txt_"+arr[i];
		if(!$(name)) alert('找不到对象'+name);
		arr_rel.push($F(name));
	}
	return arr_rel;
}

//构建一个xml数据
//parms: xmlland 数据岛或document
//name 根节点名
function $XML(xmlland,name,nodeName,nodeData){
	//如果没有提供nodeData，则根据约定txt_name来自动取value
	if(nodeData==null || nodeData.length==0){
		nodeData = cookNodeDate(nodeName);
	}
	if(nodeName.length != nodeData.length){
		alert("提供的节点名与值不对应，不能生成相应的数据");
		return;
	}
	var elmSet = xmlland.createElement(name);
	for(var i=0; i<nodeName.length; i++) {
		var temp = xmlland.createElement(nodeName[i]);
		temp.appendChild(xmlland.createTextNode(nodeData[i]));
		elmSet.appendChild(temp);
	}
	return elmSet;
}