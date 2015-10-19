/*
 * 继承AW.Grid.Control类，实现可编辑表格功能
 * 白剑
 * 2012-12-30
 * 增加对CongouV2JSON格式支持
 */
AW.Grid.Editor = AW.Grid.Control.subclass();
AW.Grid.Editor.create = function(){
	var obj = this.prototype;
	obj._dataArray = [];//存放修改的数据数组，包括新增、修改和删除的
	obj._nodeNames  = [];//节点名称(列名称)，返回的xml数据将以此作为节点名
	
	obj.deleteStyle = "color:red;text-decoration:line-through;font-style:italic;";
	obj.editStyle = "color:blue;";
	obj.addStyle = "color:green;";
	
	obj.isFirstSetValue = true;//首次赋值
	obj.isOnCellEdit = true;//表格录入值后自动设置编辑值
	
	obj.getNodeNames=function(names){
		return this._nodeNames;
	};
	//设置节点名数组
	obj.setNodeNames=function(names){
		//在数组尾增加_rowid,_sflag,_ssflag 内部标记列
		names.push("_rowid");
		names.push("_sflag");
		obj._nodeNames = names;
	};
	
	//设置列值
	obj.setCellData=function(value,col,row){
		//设置修改数据数组值
		if(typeof(row) !="undefined" && typeof(col)!="undefined"){
			this.setEditData(value, col, row);
			//表格值
			this.setCellText(value, col, row);
			this.setCellValue(value, col, row);
		}
	};
	
	//设置编辑数组值
	obj.setEditData = function( newValue, col, row ){//设置修改数据记录
		if(!this._dataArray[row]){
			this.newDataArray( row );
		}
		if(this.isFirstSetValue){
			this._dataArray[row][col] = newValue;
		}else{
			//非首次赋值，则检查值差异，设置编辑状态
			var oldValue = this.getEditData(col,row);
			this._dataArray[row][col] = newValue;
			if(oldValue==newValue) return;
			
			//如果_sfalg 不等于 A 或 D时，修改为M
			var colNum = this._nodeNames.length - 1;
			var flag = this.getEditData(colNum,row);
			if(flag=='D'){
				if(confirm("行已标记为删除！是否取消删除，继续编辑？")){
					this.setEditData('M',colNum,row);
					this.markStyle(row,this.editStyle);
				}else{
					
				}
			}else if(flag=='A'){
			}else{
				this.setEditData('M',colNum,row);
				this.markStyle(row,this.editStyle);
			}
		}
	};
	
	//获得编辑列值
	obj.getEditData=function(col,row){//获得_dataArray的值
		return this._dataArray[row]?this._dataArray[row][col]:null;
	};
	
	//获得编辑行数
	obj.getEditCount=function(){
		return this._dataArray.length;
	};
	
	//根据列名获取列序号
	obj.getColIdx = function(name){
		for( var i=0; i<this._nodeNames.length; i++ ){
			if(this._nodeNames[i] == name){
				return i;
			}
		}
		return -1;
	};
	
	//增加一行
	obj.newRow = function(){
		this.addRow();
		var rowid = this.getCurrentRow();
		var colNum = this._nodeNames.length - 1;
		this.setEditData('A',colNum,rowid);
		this.setEditData(rowid,colNum-1,rowid);
		this.markStyle(rowid,this.addStyle);
	};
	
	//标记删除一行
	obj.delRow = function(){
		var rowid = this.getCurrentRow();
		if(rowid>=0){
			//如果_sfalg 等于 A 则直接删除行，否则标记行_sfalg为D
			var colNum = this._nodeNames.length - 1;
			var flag = this.getEditData(colNum,rowid);
			if(flag=='A'){
				this.deleteRow(rowid);
				this.delDataArray(rowid);
			}else{
				this.setEditData('D',colNum,rowid);
				this.setEditData(rowid,colNum-1,rowid);
				this.markStyle(rowid,this.deleteStyle);
				this.refresh();
			}
		}
	};
	
	//
	obj.markStyle = function(rowid,style){
		this.getRowTemplate(rowid).setStyle(style);
		this.refresh();
	};
	
	//返回json格式数据
	obj.toJSON = function(isAll){
		var k =0;
		var data = [];
		var num = obj._nodeNames.length-1;
		for( var i=0; i<this._dataArray.length; i++ ){
			var flag = this._dataArray[i][num];
			if(!isAll && flag!='A' && flag!='D' && flag!='M'){
				continue;
			}
			data.push(this._dataArray[i]);
			k++;
		}
		
		var o = new Object();
		o["nodeNames"] = obj._nodeNames;
		o["rowCount"] = k;
		o["rowSet"] = data ;
		return o.toJSONString();
	};
	
	//返回html格式数据
	obj.toHTML = function(){
		var str ='<table border=1>';
		for( var i=0; i<this._dataArray.length; i++ )
		{
			str += '<tr>';
			str += '<td>rowid : ' + this._dataArray[i][this._dataArray[i].length-2] +'</td>' ;
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
			var rid = doc.createAttribute("rowid");
			rid.value=this._dataArray[i][this._dataArray[i].length-2];
			elmRow.setAttributeNode(rid);
			var sflag = doc.createAttribute("sflag");
			sflag.value = this._dataArray[i][this._dataArray[i].length-1];
			elmRow.setAttributeNode(sflag);
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
	obj.buildObject=function( rowid, values ){//构建一个对象，该对象以nodeNames作为成员
		var o = new Object;
		o['rowid'] = rowid;
		for(var i=0; i<this._nodeNames.length; i++){
			o[this._nodeNames[i]] = values[i];
		}
		return o;
 	};
	//（内部函数）
 	obj.newDataArray = function( rowid ){//新建一个行对象到_dataArray数组
		var values = new Array(this._nodeNames.length);
	//	var new_obj = this.buildObject(rowid, values );
		this._dataArray.push(values);
 	};
	//（内部函数）
 	obj.delDataArray = function( rowid ){//将指定rowid的数据从_dataArray中清除
 		this._dataArray.splice(rowid, 1);
 	};
	//当编辑值输入的完成，赋值到dataArray
	obj.onCellEditEnded = function( textValue, col, row ){
		if(this.isOnCellEdit){
			this.setEditData( textValue, col, row );
		}
	};
	//overwrite
	var setCellModel = obj.setCellModel;//重载设置grid设置数据table方法
	obj.setCellModel = function (model) {
		//初始化
		obj._dataArray = [];
		
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
		this.isFirstSetValue = true;
		for(var k=0;k<model.getCount();k++){
			for(var j=0;j<model._valuesPath.length;j++){
				this.setEditData(model.getData(j,k),j,k);
			}
			var colNum = this._nodeNames.length - 1;
			this.setEditData(k,colNum-1,k);
			
			//清理上次的样式
			this.getRowTemplate(k).setStyle("color:none;text-decoration:none;font-style:none;");
		}
		this.isFirstSetValue = false;
	};

	//自定义属性
	//行号
	obj.setSelector = function(){
		this.setSelectorVisible(true);
		this.setSelectorWidth(30);
		this.setSelectorText(function(i){return this.getRowPosition(i)+1;});
	};
};