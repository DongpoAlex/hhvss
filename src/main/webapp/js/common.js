/**
 * JS工具类
 * 功能一些表单的验证
 * 作者：白剑
 * 最后更新2010-9-5
 * @version 2.01
 */


//财务格式
//dataToText(v)将普通小数转成财务格式；textToValue(v) 将财务格式转成普通小数
var financialNum = new AW.Formats.Number;
financialNum.setTextFormat("#,##0.00");

/**
 *  获得地址栏制定的参数
 */
String.prototype.getQuery = function(name)
{
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var r = this.substr(this.indexOf("\?")+1).match(reg);
	if (r!=null) return unescape(r[2]); return null;
};

// 包含文件 用法,JS里动态添加js或css：
function $import(path, type, title) {
    var s, i;
    if (type == "js") {
        var ss = document.getElementsByTagName("script");
        for (i = 0; i < ss.length; i++) {
            if (ss[i].src && ss[i].src.indexOf(path) != -1) {
                return;
            }
        }
        s = document.createElement("script");
        s.type = "text/javascript";
        s.src = path;
    } else {
        if (type == "css") {
            var ls = document.getElementsByTagName("link");
            for (i = 0; i < ls.length; i++) {
                if (ls[i].href && ls[i].href.indexOf(path) != -1) {
                    return;
                }
            }
            s = document.createElement("link");
            s.rel = "alternate stylesheet";
            s.type = "text/css";
            s.href = path;
            s.title = title;
            s.disabled = false;
        } else {
            return;
        }
    }
    var head = document.getElementsByTagName("head")[0];
    head.appendChild(s);
}



	//设定到时之后执行什么动作
function doTimeout(DoType, Action, Times) {
    if (Times === "") {
        Times = 1;
    }
    if (typeof (Times) != "number") {
        Times = 1;
    }
    switch (DoType.toLowerCase()) {
      case "go":
        window.setTimeout("window.location='" + Action + "'", Times);
        break;
      case "alert":
        window.setTimeout("alert('" + Action + "')", Times);
        break;
      case "js":
        window.setTimeout("'" + Action.toString() + "'", Times);
        break;
      default:
        alert("Nothing will do!");
        break;
    }
}



	//根据ID返回对象
function $(emid) {
    var elements = new Array();
    for (var i = 0; i < arguments.length; i++) {
        var element = arguments[i];
        if (typeof element == "string") {
            element = document.getElementById(element);
        }
        if (arguments.length == 1) {
            return element;
        }
        elements.push(element);
    }
    return elements;
}



//根据ID获取表单的值，调用$()方法
function $F(emid) {
    return $(emid).value;
}


//新建一个HTML对象
function $new(tag) {
    return document.createElement(tag);
}
//创建一个文本对象
function $newText(content){
	return document.createTextNode(content);
}

//删除一个对向
function $D(emid){
	return $(emid).removeNode(true);
}

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

//设置表单不可操作
function setForm(formID,s)
{
    var form=$(formID);
    for(var i=0;i<form.elements.length; i++)
    {
        if(s.toLowerCase()=="yes"){
            form.elements[i].disabled = false;
            form.elements[i].className = "abled";
        }else{
            form.elements[i].disabled = true;
            form.elements[i].className = "disabled";
        }
    }
}

//设置对象的可见，可操作属性
function setElement(emid, s) {
    s = s.toLowerCase();
    var e = $(emid);
    if ((typeof (e) != "object") || (e == null)) {
        return false;
    }
    switch (s) {
      case "yes":
      	if(e.disabled!=null || typeof(e.disabled)!='undefine'){
        	e.disabled = false;
      	}
        break;
      case "no":
      	if(e.disabled!=null || typeof(e.disabled)!='undefine'){
        	e.disabled = true;
      	}
        break;
      case "readonly":
      	if(e.readonly!=null || typeof(e.readonly)!='undefine'){
        	e.readonly = true;
      	}
      	break;
      case "canwrite":
      	if(e.readonly!=null || typeof(e.readonly)!='undefine'){
        	e.readonly = false;
      	}
      	break;
      case "show":
        e.style.display = "block";
        break;
      case "hide":
        e.style.display = "none";
        break;
    }
}

//过滤一些字符//未完成
function strFiltrate(str){
	str = str.replace(/\</g,"&lt;");
	str = str.replace(/\>/g,"&gt;");
	str = str.replace(/\&/g,"&amp;");
	str = str.replace(/\'/g,"&#39;");
	str = str.replace(/\"/g,"&#34;");
	str = str.replace(/\[w,W]here/g,"$1h&#101;re");
	return str;
}

//将编码后的加号替换为html格式
function escape2(str) {
    return escape(str).replace(/\+/g, "%2b");
}

	//显示提示文字，并获焦点
	//表单成员ID,消息显示框ID,显示信息,指定宽度
function Focus(FormName, FormInfoName, MSG) {
    var obj = $(FormName);
    var Info = $(FormInfoName);
	var Width =0;
    if (obj != null) {
        obj.focus();
    }
    if (Info != null) {
        Info.innerHTML = MSG;
        Info.className = "InputError Focus";
		/*Width = MSG.length * FONT_SIZE+12;
        if (IsNum(Width) && (Width != 0)) {
            Info.style.width = Width + "px";
        }*/
    }
    return (false);
}

	//显示提示文字
function Warning(emid, MSG) {
    var obj = $(emid);
	var Width =0;
    if (obj != null) {
        obj.innerHTML = MSG;
        obj.className = "Warning";
		/*Width = MSG.length * FONT_SIZE+12;
        if (IsNum(Width) && (Width != 0)) {
            Info.style.width = Width + "px";
        }*/
    }
}



//设置输入框的状态
function setInput(emid, emid2, status) {
    Element.removeClassName(emid, "InputNO");
    Element.removeClassName(emid, "InputYES");
    if (status.toLowerCase() == "ok") {
        Element.addClassName(emid, "InputYES");
        $(emid2).className = "InputTextOK";
        $(emid2).innerHTML = " 格式正确!";
        return true;
    } else {
        Element.addClassName(emid, "InputNO");
        Element.addClassName(emid2, "InputTextNO");
        return false;
    }
}

//清除CSS,参数IsClearContent表示是否清除内容,1表示轻空
function clearCss(FormName, IsClearContent) {
    if (FormName == "") {
        return;
    }
    var obj = $(FormName);
    if (obj != null) {
        obj.className = "";
    }
    if (IsClearContent == "1") {
        obj.innerHTML = "";
    }
}

	//禁止一些键
function disableKeyDown() {
    if ((window.event.altKey) && ((window.event.keyCode == 37) || (window.event.keyCode == 39))) {
        event.returnValue = false;
    }
    if (event.keyCode == 116) { //F5
        event.keyCode = 0;
        event.returnValue = false;
    }
    if (event.keyCode == 122) { //F11
        event.keyCode = 0;
        event.returnValue = false;
    }
    if ((event.ctrlKey) && (event.keyCode == 7)) { //Ctrl+n
        event.returnValue = false;
    }
    if ((event.shiftKey) && (event.keyCode == 121)) { //shift+F10
        event.returnValue = false;
    }
}



//去掉前后空格
function Trim(str) {
  return str.replace(/(^\s*)|(\s*$)/g, "");
}
	///检查长度范围
function LimitLen(theValue, Min, Max) {
    theValue = Trim(theValue);
    if (theValue == "") {
        return false;
    }
    if ((theValue.length < Min) || (theValue.length > Max)) {
        return false;
    } else {
        return true;
    }
}
	//是否用户注册允许的字符
	//字母数字开头，允许3-16字节，允许字母数字下划线中线
function isUserIDChar(str) {
    var reg = /^[a-zA-Z0-9][a-zA-Z0-9_-]{0,15}$/;
    if (!reg.test(str)) {
        return false;
    } else {
        return true;
    }
}

	//判断是否正确EMAIL
function isEmail(val) {
    var mail = /^[_\.0-9a-z-]+@([0-9a-z][0-9a-z-]+\.){1,4}[a-z]{2,3}$/i;
    if (!mail.test(val)) {
        return (false);
    } else {
        return (true);
    }
}


	//判断是否数字
function isNum(val) {
    var intStr = /^\d+$/;
    if (!intStr.test(val)) {
        return (false);
    } else {
        return (true);
    }
}



	//是否IP地址

	//还不能正确判断第一个为0
function isIP(str) {
    var re = /^([01]?\d{1,2}|2[0-4]\d|25[0-5])(\.([01]?\d{1,2}|2[0-4]\d|25[0-5])){3}$/;
    if (re.test(str)) {
        return true;
    } else {
        return false;
    }
}


	//是否中文
function isCnChar(str) {
    var reg = /^[\u4E00-\u9FA5]+$/;
    if (!reg.test(str)) {
        return false;
    }
    return true;
}

//是否身份证
function isIDCard (str)
{
	var isIDCard1 = new Object();
	var isIDCard2 = new Object();

	//身份证正则表达式(15位)
	isIDCard1=/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$/;

	//身份证正则表达式(18位)
	isIDCard2=/^[1-9]\d{5}[1-9]\d{3}((0[1-9])|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{4}$/;

	//验证身份证，如果正确，返回true
	if (isIDCard1.test(str)||isIDCard2.test(str)){
		return true;
	}
	return false;
}



	//是否英文
function isEnChar(str) {
    var reg = /^[a-zA-Z]+$/;
    if (!reg.test(str)) {
        return false;
    }
    return true;
}



//是否双字节（包括中文）
function isDoubleChar(str) {
    var reg = /^[^\x00-\xff]+$/;
    if (!reg.test(str)) {
        return false;
    }
    return true;
}



	//是否包含中文
function isHasCnChar(str) {
    var reg = /[^\x00-\xff]/;
    if (reg.test(str)) {
        return true;
    }
    return false;
}



	//是否密码问题
function isPwdQuestion(str) {
    var reg = /^([\u4E00-\u9FA5]|[0-9a-zA-Z ])+$/;
    if (!reg.test(str)) {
        return false;
    }
    return true;
}


/*有效的电话号码（包括固定电话，传真，移动电话）
要求：
  　　(1)电话号码由数字、"("、")"和"-"构成
  　　(2)电话号码为3到8位
  　　(3)如果电话号码中包含有区号，那么区号为三位或四位
  　　(4)区号用"("、")"或"-"和其他部分隔开
  　　(5)移动电话号码为11或12位，如果为12位,那么第一位为0
  　　(6)11位移动电话号码的第一位和第二位为"13"
  　　(7)12位移动电话号码的第二位和第三位为"13"
  　　根据这几条规则，可以与出以下正则表达式：
  　　(^[0-9]{3,4}\-[0-9]{3,8}$)|(^[0-9]{3,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)|(^0{0,1}13[0-9]{9}$)
*/

function isTel(str){
	var reg = /(^[0-9]{3,4}\-[0-9]{3,8}$)|(^[0-9]{3,8}\-[0-9]{1,9}$)|(^[0-9]{3,4}\-[0-9]{3,8}\-[0-9]{1,9}$)|(^[0-9]{3,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)|(^[0-9]{11}$)/;
	if(!reg.test(str)){
		return false;
	}else{
		return true;
	}
}

//判断是否有效手机号码
function isMobileTel(str) {
    var reg = /^[0-9]{11}$/;
    if (!reg.test(str)) {
        return false;
    }
    return true;
}

//邮政编码
function isZip(str){
	var reg = /(^[0-9]{6}$)/;
    if (!reg.test(str)) {
        return false;
    }else{
    	return true;
	}
}
//匹配网址
function isURL(val)
{
	//var reg = /^(http|ftp|mailto|news|mms|rtsp)\:\/\/[0-9a-zA-Z]([0-9a-zA-Z\-]+\.)+[a-zA-Z]{2,4}?/
	var reg = /^http:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$/;
	if(reg.test(val)){
		return true;
	} else	{
		return false;
	}
}
//QQQ
function isQQ(val){
	var reg = /^[1-9]\d{4,9}$/;
	if(reg.test(val)){
		return true;
	}else{
		return false;
	}
}
	//是否为空
function isNullOrEmpty(str) {
    if (Trim(str) == "" || str === null) {
        return true;
    }else{
		return false;
	}
}



//验证给定的日期是否合法   ,参数格式要求：yyyy-mm-dd 可以根据情况更改正则表达式
function isDate(oStartDate) {

	    //对日期格式进行验证 要求为1900-2007年  格式为 yyyy-mm-dd 并且可以正常转换成正确的日期
    var pat_hd = /^((19\d{2})|(200[0-7]{1}))-((0[1-9]{1})|(1[0-2]{1}))-((0[1-9]{1})|([1-2]{1}[0-9]{1})|(3[0-1]{1}))$/;
    try {
        if (!pat_hd.test(oStartDate)) {
            return false;
        }
        var arr_hd = oStartDate.split("-");
        var dateTmp;
        dateTmp = new Date(arr_hd[0], parseFloat(arr_hd[1]) - 1, parseFloat(arr_hd[2]));
        if (dateTmp.getFullYear() != parseFloat(arr_hd[0]) || dateTmp.getMonth() != parseFloat(arr_hd[1]) - 1 || dateTmp.getDate() != parseFloat(arr_hd[2])) {
            return false;
        }
    }
    catch (ex) {
        if (ex.description) {
            return false;
        } else {
            return false;
        }
    }
    return true;
}


function setLoading(_display, _text) {
	var e = $("loading");
	var m = $("overlay");
	if (e == null) {
		e = document.createElement("div");
		e.id = "loading";
		e.className = "loading";
		document.body.appendChild(e);
		m = document.createElement("div");
		m.id = "overlay";
		document.body.appendChild(m);
	}
	m.style.display = _display ? "block" : "none";
	e.style.display = _display ? "block" : "none";
	e.innerHTML = (_text == null) ? "Loading ......" : _text;
	e.style.left = (window.screen.width) * 0.4;
	e.style.top = (window.screen.height) * 0.4;
	m.style.height = document.body.clientHeight;
	m.style.width = document.body.clientWidth;
	m.innerHTML = "<iframe src=\"\" id=\"ddd\" width=\"100%\" height=\"100%\" scrolling=no align=\"middle\" border=\"0\" frameborder=\"0\"></iframe>";
}

//导入转换函数
function _upload_txt_to_matrix ( text, field_number, lines_limit )
{
	var str4err = "";
	var arr_lines = text.split( "\n", lines_limit+1 );
	
	if(arr_lines.length >lines_limit )  throw "导入的数据不能超过" + lines_limit + "条!" ;

	// windows 内换行符为"\r\n", 需要去掉最后一位.
	for( var i=0; i<arr_lines.length; i++ ) {
		var n = arr_lines[i].length;
		arr_lines[i] = arr_lines[i].replace("\r", "");
	}
	var arr_matrix = new Array( );
	for( var i=0; i<arr_lines.length; i++ ) {
		var line = new String ( arr_lines[i] );
		// 忽略空白行.
		if( line == "" ) continue;
		var arr = line.split( "\t" );
		arr_matrix[ i ] = arr;
		if( arr.length < field_number ) 
		{
		   str4err += "第"+(i+1)+"行仅有 " + arr.length + " 列: " + line+"\n";	
		}
		
		for( var k=0; k<arr.length; k++)
		{
		   if (arr[k]=="") str4err += "第"+(i+1)+"行第"+(k+1)+"列无值: " + line+"\n";
		}

	}
	if( str4err != "" ){
		alert(str4err);
		return [];
	}
	return arr_matrix;
}

//显示门店清单
function showShopList(objid){
	var divid = "div"+objid;
	var e = $(divid);
	if(e==null){
		setLoading(true);
		var url = "../DaemonBranchGroup?rander="+objid;
		var table = new AW.XML.Table;
		table.setURL( url );
		table.request();
		table.response = function( text ){
			setLoading(false);
			table.setXML(text);
				e = document.createElement("div");
				e.id=divid;
				e.className="selbranchgroup";
				e.innerHTML = "<div><input type='button' value='确定' onclick='SelBranchGroupHidden(\""+divid+"\")'><br/>提示：可拖拉多选或按住ctrl多选<br/>"+table.getXMLContent()+"</div>";
				document.body.appendChild(e);
				e.style.display="block";
				$(objid+"_groupid").size = 20;
		};
	}else{
		e.style.display="block";
	}
}
//该方法仅显示非关店状态门店
function showShopListUnclose(objid){
	var divid = "div"+objid;
	var e = $(divid);
	if(e==null){
		setLoading(true);
		var url = "../DaemonBranchGroup?rander="+objid+"&unclose=true";
		var table = new AW.XML.Table;
		table.setURL( url );
		table.request();
		table.response = function( xml ){
			setLoading(false);
			table.setXML(xml);
				e = document.createElement("div");
				e.id=divid;
				e.className="selbranchgroup";
				e.innerHTML = "<div><input type='button' value='确定' onclick='SelBranchGroupHidden(\""+divid+"\")'><br/>提示：可拖拉多选或按住ctrl多选<br/>"+table.getXMLContent(xml)+"</div>";
				document.body.appendChild(e);
				e.style.display="block";
				$(objid+"_groupid").size = 20;
		};
	}else{
		e.style.display="block";
	}
}
//显示第三方物流店
function showShopListThridDC(objid){
	var divid = "div"+objid;
	var e = $(divid);
	if(e==null){
		setLoading(true);
		var url = "../DaemonBranchGroup?rander="+objid+"&thridDC=true";
		var table = new AW.XML.Table;
		table.setURL( url );
		table.request();
		table.response = function( xml ){
			setLoading(false);
			table.setXML(xml);
				e = document.createElement("div");
				e.id=divid;
				e.className="selbranchgroup";
				e.innerHTML = "<div><input type='button' value='确定' onclick='SelBranchGroupHidden(\""+divid+"\")'><br/>提示：可拖拉多选或按住ctrl多选<br/>"+table.getXMLContent(xml)+"</div>";
				document.body.appendChild(e);
				e.style.display="block";
				$(objid+"_groupid").size = 20;
		};
	}else{
		e.style.display="block";
	}
}
function SelBranchGroupHidden(divid){
	$(divid).style.display="none";
}
//将选择的门店输入到门店input
function onSelBranchGroupChange(objid){
	var n = 0;
	var tmpshopid = new Array();
	var opt = $(objid+"_groupid").options;
	for(var i=0; i<opt.length; i++)
		if(opt[i].selected)		
		{
			tmpshopid[n] = opt[i].value;
			n++;
		}
		
	$(objid).value = tmpshopid.join(",");
}

function onSelBranchGroupDBClick(objid){
	var n = 0;
	var tmpshopid = new Array();
	var opt = $(objid+"_groupid").options;
	for(var i=0; i<opt.length; i++)
		if(opt[i].selected)		
		{
			tmpshopid[n] = opt[i].value;
			n++;
		}
		
	$(objid).value = tmpshopid.join(",");
	SelBranchGroupHidden("div"+objid);
}

//自动生成查询参数
function autoParms(){
	var parms = new Array();
	var elms = document.getElementsByName("txt_parms");
	for ( var i = 0; i < elms.length; i++) {
		var elm = elms[i];
		var v = elm.value;
		var ii = elm.id;
		if(v && ii){
			var p = ii.replace("txt_", "");
			var sp = elm.split;
			if(sp){
				var arr = v.split(sp);
				for(var j=0; j<arr.length; j++){
					parms.push( p+"="+encodeURI(arr[j]));
				}
			}else{
				parms.push(p+"="+encodeURI(v));
			}
		}
	}
	return parms;
}

//自动验证参数 notnull
function autoCheck(){
	var elms = document.getElementsByName("txt_parms");
	for ( var i = 0; i < elms.length; i++) {
		var elm = elms[i];
		var v = elm.value;
		var notnull = elm.getAttribute("notnull");
		if(notnull && notnull=='notnull' && v==''){
			alert(elm.alt + "不能为空！");
			elm.focus();
			return false;
		}
	}
	return true;
}


var doPrint = function(){
	$("div_print").style.display="none";
	window.print();
};

/**
 * bu payshop 联动控件加载
 * @return
 */
function BU_PayShop_init(){
	var params = ['attribute={"id":"txt_buid","name":"txt_parms","onchange":"BU.onload()"}','showAll=true'];
	BU.toHTML("span_buid",params);
	BU.onload = function(){
		var buid = $("txt_buid").value;
		params = ['attribute={"id":"txt_payshopid","name":"txt_parms"}','showAll=true',"buid="+buid];
		PayShop.toHTML("span_payshop",params);
	};
}

function  BUinit(){
	var params = ['attribute={"id":"txt_buid","name":"txt_parms","onchange":"BU.onload()"}','showAll=false'];
	BU.toHTML("span_buid",params);
}


/**
 * cookie 操作公共方法
 */
function  setCookie(name,value)  
{  
     var Days = 360;   //此cookie将被保存360天  
     var exp = new Date(); 
     exp.setTime(exp.getTime() + Days*24*60*60*1000);  
     document.cookie = name + "=" + escape(value) 
		+ ";expires=" + exp.toGMTString();  
 }  
 function getCookie(name)  
 {  
     var arr,reg=new RegExp("(^|)"+name+"=([^;]*)(;|$)");  
     if(arr=document.cookie.match(reg))
	return unescape(arr[2]);  
     else
	return null;  
 }  
 function delCookie(name)  
 {  
     var exp = new Date();  
     exp.setTime(exp.getTime() - 1);  
     var cval=getCookie(name);  
     if(cval!=null) {
	document.cookie= name + "="+cval
		+";expires="+exp.toGMTString();
	}
 }