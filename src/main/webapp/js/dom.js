/**
*@author:yzn
*Create on 2006-03-20
*/

/*****************************************XMLElement*****************************************************************/
/**
*Constructor of XMLElement
*通过该构造函数实例化一个XMLElement对象，利用该对象的toElement方法可以把数据转成一个XML结果集并返回
*@param doc 一个数据岛对象，由前台指定
*@param node_name 子结点名称集，前台应把名字集以一个数组形式传过来
*@param node_value 子结点数据集，前台应把数据集以一个数组形式传过来	
*@param set_name 表示结果集的XML节点名字
*/
function XMLElement(doc){
	 this.doc=doc;		 
}
/**
*方法
*根据XMLElement对象的信息转成一个XML结点并返回
*@return 一个XML节点
*/
XMLElement.prototype.toElement=function(node_name,node_value){ 
		if((typeof(node_name)!="undefined")&&(typeof(node_value)!="undefined")){
	  	var elm_return = this.doc.createElement(node_name);
		elm_return.appendChild( this.doc.createTextNode(node_value) );						
		return elm_return;}
		else{
		alert("节点名称及节点值不能为空");
		return false;
		}
}
/**
*方法
*根据XMLElement对象的信息转成XML结果集并返回
*@return 一个XML结点
*/
XMLElement.prototype.toRowSetElement=function(node_name,node_value,set_name){ 
	if((typeof(node_name)!="undefined")&&(typeof(node_value)!="undefined")&&(typeof(set_name)!="undefined")){	
	  var elm_return = this.doc.createElement(set_name);
	  for(var i=0;i<node_name.length;i++){  	
	  	var elm = this.doc.createElement( node_name[i] );
		elm.appendChild( this.doc.createTextNode( node_value[i] ) );
		elm_return.appendChild( elm );
		} 			
		return elm_return;}
	  else{
	  alert("节点名称及节点值不能为空");
	  return false;
		}
}
/**
*方法
*根据XMLElement对象的信息把传入的XML节点的子结点值进行转换并存到一个数组返回
*@return 包含XML子节点值的数组
*/
XMLElement.prototype.parseRowSetElement=function(elm_parse){
	if(typeof(elm_parse)=="object"){	
	var len = elm_parse.childNodes.length;
	var return_array=new Array(len);
	for(var i=0;i<len;i++)
	return_array[i]=elm_parse.childNodes.item(i).text;
	return return_array;}
	else{
	alert("传入的XML节点非法");
	return false;
	}
}
/********************************************XMLElement END************************************************************/

/**********************************************XErr*********************************************************************/
/**
*Constructor of XErr
*通过该构造函数实例化一个XErr对象，利用该对象的toString方法可以把错误信息以String形式返回
*/
function XErr( code, note, time ){
	this.code = code;
	this.note = note;
	this.time = time;
	this.toString 		= _toString_XErr;
	this.parseElement 	= parseXErr;			
}
/**
*方法
*解析XErr节点，并把信息以String形式返回
*/
function _toString_XErr(){
	return "" + this.code + ":" + this.note ;
}
/**
*方法
*解析XErr节点，返回一个新的XErr对象
*/
function parseXErr( elm_err ){
	var elm = elm_err.selectSingleNode("code");
	if( elm == null ) throw "Invalid data for xerr!";
	var code = elm.text;
	var elm = elm_err.selectSingleNode("note");
	if( elm == null ) throw "Invalid data for xerr!";
	var note = elm.text;
	var elm = elm_err.selectSingleNode("time");
	if( elm == null ) throw "Invalid data for xerr!";
	var time = elm.text;
	return new XErr ( code, note, time );	
}
/*****************************************XErr END*****************************************************************/

/**
*实现折叠表格
*@param start_id:行号的开始ID
*@param end_id：行号的结束ID
*/
function ToggleTableHeader(start_id,end_id){	
 	for(var i=start_id;i<=end_id;i++){ 
 	  var get_headerid='header'+i+'_toggle'; 	  
 	  var header_id=getCell(get_headerid);
 	 (header_id.style.display=='none')?header_id.style.display='block':header_id.style.display='none';  	   	 	 		  
	}
	
}

/**
*根据一个ID值确定一个唯一的网页元素
*@param id 对象的ID值
*/
function getCell(id){
   return document.all ? document.all(id) : document.getElementById ? document.getElementById(id) : document.layers ? document['NS_' + id].document.layers[0] : null
 }
/**
*接收对象的ID,返回该对象的左边距
*@param id 对象的ID值
*/ 
function getObjLeft(id){
	var obj=getCell(id);
	var left = obj.offsetLeft;
	while (obj.tagName != "BODY") {
		obj = obj.offsetParent;
		left += obj.offsetLeft;
	}
	return left;
}
/**
*接收对象的ID,返回该对象的上边距
*@param id 对象的ID值
*/ 
function getObjTop(id){	
	var obj=getCell(id);	
	var top = obj.offsetTop;
	while (obj.tagName != "BODY") {
		obj = obj.offsetParent;
		top += obj.offsetTop;
	}
	return top;
}
/**
*动态创建一个DIV层
*@param div_id 新建DIV的ID值
*@param get_left 可选参数，指定该DIV层的左边距
*@param get_top  可选参数，指定该DIV层的右边距
*get_left和get_top参数必须同时指定才能生效
*/
function CreateDivision(div_id,get_left,get_top){	
	var oDiv = document.createElement("DIV");
	with (oDiv) {
	   id =div_id;}				
	with (oDiv.style) {
	   if((typeof(get_left)!="undefined")&&(typeof(get_top)!="undefined")){ 
	   position = "absolute";
	   left=get_left;
	   top=get_top;}
		}	
	document.body.appendChild (oDiv);
	document.body.insertAdjacentElement("AfterBegin",oDiv);	
}
