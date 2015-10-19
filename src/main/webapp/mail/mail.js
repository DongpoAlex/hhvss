/**
 * 全局变量
 */
var g_loginid = "<%=loginid%>";
var g_mailid = 0;
var g_sender = "";
var g_title  = "";
var g_fileid = 0;
var g_content= "";
var g_filename= "";
var g_sendtime= "";
var g_receiptor01="";
var g_receiptor02="";
var g_arr_mailid = null;
var g_backid = -1;

var newMailCount=0;

function init(){
	menu_click($("a_receive"));
	setNewMailCount();
}

//菜单点击事件,根据对象name调用相应事件
function menu_click( obj ){
	menu_focus( obj.id );
	content_focus( obj.name );
	g_mailid = 0;
}
//锁定点邮件内容显示焦点
function content_focus( name ){
	var elm_content = $("mail_conent").childNodes;
	for ( var i=0; i<elm_content.length; i++){
		if ( elm_content[i].name == name ){
			elm_content[i].style.display = "block";
		}else{
			elm_content[i].style.display = "none";
		}
	}
	
	if( $("list_"+name).innerHTML == '' ) eval("load_"+name)();

	if( name == "new"){
		getDefPayer();
	}
}

//锁定菜单焦点
function menu_focus( id ){
	var elm_ul = document.getElementsByTagName("ul");
	var elm_a = elm_ul[0].getElementsByTagName("a");
	for ( var i=0; i<elm_a.length; i++){
		if ( elm_a[i].id == id ){
			elm_a[i].className = "current";
		}else{
			elm_a[i].className = "";
		}
	}
}
//发送新邮件
function load_new(){
	alert("no implitment");
}

function setNewMailCount(){
	if( newMailCount > 0 ) $("mail_new_count").innerText = "[ "+newMailCount+" ]";
	else $("mail_new_count").innerText = '';
}

//格式化邮件状态	
function prseStatus(grid){
	
	for(var i=0; i<grid.getRowCount(); i++) {
		//状态
		var text = grid.getCellValue(5,i)==0?'新邮件':'已阅读';
		grid.setCellText(text,5,i);
		//附件
		text = grid.getCellValue(6,i)>0?'有附件':'无附件';
		grid.setCellText(text,6,i);
		//回复
		text = grid.getCellValue(8,i)==-1?'未回复':'已回复';
		grid.setCellText(text,8,i);
	}
}

function prseStatus4Recv(grid){
	
	for(var i=0; i<grid.getRowCount(); i++) {
		//附件
		text = grid.getCellValue(5,i)>0?'有附件':'无附件';
		grid.setCellText(text,5,i);
		//回复
		text = grid.getCellValue(9,i)==-1?'未回复':'已回复';
		grid.setCellText(text,9,i);
	}
}

//读取收件箱内容
function load_receive(params){
	setLoading( true, "正在收信" );
	params = (params==undefined)?"":params;
	var url  = "../DaemonMail?operation=browse&type=101"+params;
	
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/browseMail");
	table.request();
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			$("list_receive").innerHTML = get_receive_grid(table);
		}
		setLoading( false );
	};
}

//格式化数据岛，返回收件箱grid
function get_receive_grid( table )
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	var grid = new AW.UI.Grid;

	table.setColumns(["","mailid","sender","title","recetime","status","fileid","readtime","ifback","backtime"]);	
	var columnNames = ["查看","邮件id","发件人","邮件标题","收信时间","状态","附件","查阅时间","是否回复","回复时间"];	
	grid.setId( "receive_grid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );
	grid.setHeaderText(columnNames);
	grid.onCellClicked = function(event, column, row){
		var mid = grid.getCellValue(1,row);
		if( g_mailid != mid ){
			g_mailid = mid;		
			read_receive(mid);
			if( grid.getCellValue(5,row) == 0 ){
				grid.setCellText( '已阅读', 5, row );
				newMailCount--;
				setNewMailCount();
			}
		}
	};
	grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	grid.setCellText('查看',0);
	//grid.sort(4,'descending');
	//grid.setCellImage("search",0);
	//grid.setCellTemplate(new AW.Templates.Image, 0);
	
	prseStatus(grid);
	
	newMailCount=0;
	for(var i=0; i<row_count; i++){
		if( grid.getCellValue(5,i) == 0 ) {
			newMailCount++;
		}
	}
	setNewMailCount();
	return grid.toString();
}
//阅读收件箱邮件详细
function read_receive(mid){
setLoading(true);
	var url  = "../DaemonMail?operation=read&type=100&mailid="+mid;	
	var ajax = new AW.XML.Table;
	ajax.setURL(url);
	ajax.request();
	ajax.response = function( text ){
		ajax.setXML(text);
		var xcode = ajax.getErrCode();
		if( xcode != '0' ){
			$("content_receive").innerText = xcode+ajax.getErrNote();
			return false;
		}
		var htmlOut = ajax.transformNode("format4mail.xsl");
		$("content_receive").innerHTML = htmlOut ;
		$("tool_receive").innerHTML = " <input type='button' value='回复' onclick='reMail("+mid+")' ></input> "+
		" <span class='space'></span> "+
		" <input type='button' value='转发' onclick='transMail()' ></input> "+
		" <span class='space'></span> "+
		" <input type='button' value='删除' onclick='delMail(\"ReceiptMail\",0,"+ mid +")' ></input> ";
		
		initGV(ajax);
		setLoading(false);
	};
}
//===============================================


//读取发件箱内容
function load_send(params){
	setLoading( true );
	params = (params==undefined)?"":params;
	var url  = "../DaemonMail?operation=browse&type=102"+params;	
	
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/browseMail");
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			$("list_send").innerHTML = get_send_grid(table);
		}
		setLoading( false );
	};
	table.request();
}


//格式化数据岛，返回发件箱grid
function get_send_grid(table)
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	var grid = new AW.UI.Grid;

	table.setColumns(["","mailid","sender","title","sendtime","fileid","receiptor01","receiptor02","readtime","ifback","backtime"]);	
	var columnNames = ["查看","邮件id","发件人","邮件标题","发送时间","附件","收件人","抄送到","收件人阅读时间","回复状态","回复时间"];	
	grid.setId( "send_grid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	grid.onCellClicked = function(event, column, row){
		var mid = grid.getCellValue(1,row);
		if( g_mailid != mid ){
			g_mailid = mid;
			read_send(mid);
		}
	};
	grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	grid.sort (4,'descending');
	grid.setCellText("查看",0);
	
	prseStatus4Recv(grid);
	
	return grid.toString();
}
//阅读发件箱邮件详细
function read_send(mid){
setLoading(true);
	var url  = "../DaemonMail?operation=read&type=0&mailid="+mid;
	var ajax = new AW.XML.Table;	
	ajax.setURL(url);
	ajax.request();
	ajax.response = function( text ){
		ajax.setXML(text);
		var xcode = ajax.getErrCode();
		if( xcode != '0' ){$("content_send").innerText = xcode+ajax.getErrNote();return false;}
		var htmlOut = ajax.transformNode("format4mail.xsl");
		$("content_send").innerHTML = htmlOut ;
		$("tool_send").innerHTML = 
				" <input type='button' value='转发' onclick='transMail()' ></input> "+
				" <span class='space'></span> "+
				" <input type='button' value='删除' onclick='delMail(\"SendMail\",0,"+ mid +")' ></input> ";
		initGV(ajax);
		setLoading(false);
	};
}
//==================================================================================================
//读取草稿箱内容
function load_draft(){
	setLoading( true );
	var url  = "../DaemonMail?operation=browse&type=103";	
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/browseMail");
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			$("list_draft").innerHTML = get_draft_grid(table);
		}
		setLoading( false );
	};
	table.request();
}

//格式化数据岛，返回草稿箱grid
function get_draft_grid(table)
{
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	var grid = new AW.UI.Grid;

	table.setColumns(["","mailid","sender","title","sendtime","fileid"]);	
	var columnNames = ["查看","邮件id","发件人","邮件标题","保存时间","附件"];	
	grid.setId( "draft_grid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );
	grid.setHeaderText(columnNames);
	grid.onCellClicked = function(event, column, row){
		var mid = grid.getCellValue(1,row);
		if( g_mailid != mid ){
			g_mailid = mid;
			read_draft(mid);
		}
	};
	grid.setSelectionMode("single-row");
	grid.setCellModel(table);
	grid.sort (4,'descending');
	grid.setCellText("查看",0);
	return grid.toString();
}
//阅读草稿箱邮件详细
function read_draft(mid){
setLoading(true);
	var url  = "../DaemonMail?operation=read&type=0&mailid="+mid;
	var ajax = new AW.XML.Table;
	ajax.setURL(url);
	ajax.request();
	ajax.response = function( text ){
		ajax.setXML(text);
		var xcode = ajax.getErrCode();
		if( xcode != '0' ){$("content_draft").innerText = xcode+ajax.getErrNote();return false;}
		var htmlOut = ajax.transformNode("format4mail.xsl");
		$("content_draft").innerHTML = htmlOut ;
		$("tool_draft").innerHTML = " <input type='button' value='发送' onclick='transMail()' ></input> "+
		" <span class='space'></span> "+
		" <input type='button' value='删除' onclick='delMail(\"DraftMail\",0,"+ mid +")' ></input> ";
		initGV(ajax);
		setLoading(false);
	};
}
//========================================================================================
//读取垃圾箱内容
function load_recycle(){
	setLoading( true );
	var url  = "../DaemonMail?operation=browse&type=104";	
	var table = new AW.XML.Table;
	table.setURL(url);
	table.setTable("xdoc/xout/browseMail");
	table.response = function(text){
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			$("list_recycle").innerHTML = get_recycle_grid(table);
		}
		setLoading( false );
	};
	table.request();
}

//显示垃圾箱内容
function show_recycle( text ){
	$("island4result").loadXML( text );
	$("list_recycle").innerHTML = get_recycle_grid();
	setLoading( false );
}

//格式化数据岛，返回垃圾箱grid
function get_recycle_grid(table)
{
	//错误检查
	var row_count = table.getCount();
	if( row_count == 0 ) return "没有记录";
	var grid = new AW.UI.Grid;
	table.setColumns(["aaa","mailid","sender","title","sendtime","fileid","type"]);	
	var columnNames = ["选中","邮件id","发件人","消息标题","保存时间","附件"];	
	grid.setId( "recycle_grid" );
	grid.setColumnCount(columnNames.length);
	grid.setRowCount( row_count );	
	grid.setHeaderText(columnNames);
	grid.onCellClicked = function(event, column, row){
		var mid = grid.getCellValue(1,row);
		var type = grid.getCellValue(6,row);
		if( g_mailid != mid ){
			g_mailid = mid;
			read_recycle(mid, type);
		}
	};
	grid.setSelectionMode("multi-row-marker");
	grid.onSelectedRowsChanged = function(arrayOfRowIndices){
		g_arr_mailid = new Array();
		for( var i=0; i<arrayOfRowIndices.length; i++){
			g_arr_mailid.push( grid.getCellValue(1, arrayOfRowIndices[i]) );
		}
		return true;
    };
	grid.setCellModel(table);
	return grid.toString();
}
//阅读垃圾箱邮件详细
function read_recycle(mid,type){
setLoading(true);

	var url  = "../DaemonMail?operation=read&type=0&mailid="+mid;
	var ajax = new AW.XML.Table;
	ajax.setURL(url);
	ajax.request();
	ajax.response = function( text ){
		ajax.setXML(text);
		var xcode = ajax.getErrCode();
		if( xcode != '0' ){$("content_recycle").innerText = xcode+ajax.getErrNote();return false;}
		var htmlOut = ajax.transformNode("format4mail.xsl");
		
		$("content_recycle").innerHTML = htmlOut ;
		$("tool_recycle").innerHTML = " <input type='button' value='恢&nbsp;&nbsp;复' onclick='delMail(\"Recover\",\""+type+"\","+ mid +")' ></input> "+
		" <span class='space'></span> "+
		" <input type='button' value='删除当前' onclick='delMail(\"DelCompletely\",\""+type+"\","+ mid +")' ></input> " +
		" <span class='space'></span> "+
		" <input type='button' value='删除选中' onclick='delArrMail(\"DelCompletely\",\""+type+"\")' ></input> ";
		
		setLoading(false);
	};
}
//===============================================================================
//下载文件
function load_file( fid ){
	var attributeOfNewWnd = "status=no,toolbar=no,menubar=no,resizable=no,scrollbars=no,width=200,height=100";
	var url = "../DaemonFileDownload?fileid="+fid;
	var cwin = window.open("../blank.htm","",attributeOfNewWnd);
	
	cwin.document.write("<html>");
	cwin.document.write("<head><title>文件下载页面</title></head>");
	cwin.document.write("<body>");
	cwin.document.write("");
	cwin.document.write("<a href='javascript:down()'>点击这里下载文件：<br/>"+g_filename+"</a>");
	cwin.document.write("<script>");
	cwin.document.write("function down(){window.location.href='"+url+"';}");
	cwin.document.write("</script>");
	cwin.document.write("</body>");
	cwin.document.write("</html>");
	return false;
}
//批量删除垃圾箱邮件
function delArrMail(operation, type){
	if( g_arr_mailid != null && g_arr_mailid.length >0 ){
		var mid = g_arr_mailid.join(',');
		delMail(operation, type, mid);
		g_arr_mailid = null;
	}else{
		alert("批量删除邮件之前，请选中要删除的邮件");
	}
}
/**
 *删除消息
 */
 
function delMail(operation,type,mid){
	var url="../DaemonMail?";
	url+="&operation=move";
	url+="&action="+operation;
	url+="&type="+type;
	url+="&mailid="+mid;
	var ajax = new AW.XML.Table;
	ajax.setURL(url);
	ajax.request();
	ajax.response = function( text ){
		ajax.setXML(text);
		var xcode = ajax.getErrCode();
		if( xcode != '0' ){
			alert(xcode+ajax.getErrNote());
		}
		else{
			if( operation=="DelCompletely" ){//彻底删除
				alert('删除成功');
				clearContent("recycle");
				load_recycle();
			}else if( operation=='SendMail' ){//发件箱删除
				alert('已移动到回收站');
				clearContent("send");
				clearList("recycle");
				load_send("");
			}else if( operation=='ReceiptMail' ){//收件箱删除
				alert('已移动到回收站');
				clearContent("receive");
				clearList("recycle");
				load_receive("");
			}else if( operation=='DraftMail' ){//草稿箱删除
				alert('已移动到回收站');
				clearContent("draft");
				clearList("recycle");
				load_draft();
			}else if( operation=="Recover" ){//恢复
				if( type=='send' ){
					alert('成功恢复到发件箱');
					clearList("send");
				}else if( type=='receipt' ){
					alert('成功恢复到收件箱');
					clearList("receive");
				}
				clearContent("recycle");
				load_recycle();
			}
		}
	};
}

function delAllMail( operation ){
	if( !confirm("该操作将处理列表中所有邮件,请确认!") ) return;
	
	var url="../DaemonMail?";
	url+="&operation=allmove";
	var ajax = new AW.XML.Table;
	url+="&action="+operation;
	ajax.setURL(url);
	ajax.request();
	ajax.response = function( text ){
		ajax.setXML(text);
		var xcode = ajax.getErrCode();
		if( xcode != '0' ){
			alert(xcode+ajax.getErrNote());
		}
		else{
			if( operation=="DelCompletely" ){//彻底删除
				alert('已清空回收站');
				clearContent("recycle");
				load_recycle();
			}else if( operation=='SendMail' ){//发件箱删除
				alert('已全部移动到回收站');
				clearContent("send");
				clearList("recycle");
				load_send("");
			}else if( operation=='ReceiptMail' ){//收件箱删除
				alert('已全部移动到回收站');
				clearContent("receive");
				clearList("recycle");
				load_receive("");
			}else if( operation=='DraftMail' ){//草稿箱删除
				alert('已全部移动到回收站');
				clearContent("draft");
				clearList("recycle");
				load_draft();
			}else if( operation=="Recover" ){//恢复
				alert('回收站内邮件已全部恢复');
				clearList("send");
				clearList("receive");
				clearContent("recycle");
				load_recycle();
			}
		}
	};
}
//清除显示的邮件内容
function clearContent(id){
	var _content = "content_"+id;
	var _tool	 = "tool_"+id;
	$( _content ).innerHTML = '';
	$( _tool ).innerHTML = '';
}
//清除显示的邮件列表
function clearList(id){
	var _list = "list_"+id;
	$( _list ).innerHTML = '';
}
//将填写的邮件内容格式化为xml格式
function packMailData( operation, ajax ){
	var receiptor = $("txt_receiptor").value.trim();
	var cc		  = $("txt_cc").value.trim();
	var title	  = $("txt_title").value.trim();
	var fileid	  = 0;
	if( window.frames[0].document.getElementById("up_fileid") &&
		window.frames[0].document.getElementById("up_fileid").value != ''
	 ){
		fileid = window.frames[0].document.getElementById("up_fileid").value;
	}
	var content   = $("txt_content").value;
	
	if( operation == 'NewMail' && ( receiptor == '' || title == '' || content == '') ){
		alert("请将邮件内容填写完整再发送");
		return false;
	}else{
		var arr = receiptor.split(",");
		if( arr.length >1 ){
			alert("收件人只能填写一位，如需发送给多人请填写到抄送，以逗号格开");
			return false;
		}
	}
	
	ajax.setXML("");
	var doc = ajax.getXML();
	var elm_root = doc.selectSingleNode("/") ;		
	var elm_xdoc = doc.createElement("xdoc");
	var elm_xparam = doc.createElement("xparam");
		
	var elm_receiptor=doc.createElement("receiptor");	
	elm_receiptor.appendChild( doc.createTextNode( receiptor ));
	elm_xparam.appendChild( elm_receiptor );
	
	var elm_cc=doc.createElement("cc");	
	elm_cc.appendChild( doc.createTextNode( cc ));
	elm_xparam.appendChild( elm_cc );
	
	var elm_title=doc.createElement("title");	
	elm_title.appendChild( doc.createTextNode( title ));
	elm_xparam.appendChild( elm_title );
	
	var elm_attachment=doc.createElement("fileid");	
	elm_attachment.appendChild( doc.createTextNode( fileid ));
	elm_xparam.appendChild( elm_attachment );
	
	var elm_operation=doc.createElement("operation");	
	elm_operation.appendChild( doc.createTextNode( operation ));
	elm_xparam.appendChild( elm_operation );
		
	var elm_content=doc.createElement("content");	
	elm_content.appendChild( doc.createTextNode(content));	
	elm_xparam.appendChild( elm_content );
	
	var elm_content=doc.createElement("backid");	
	elm_content.appendChild( doc.createTextNode(g_backid));	
	elm_xparam.appendChild( elm_content );
		
	elm_xdoc.appendChild( elm_xparam );
	elm_root.appendChild(elm_xdoc);
	
	g_backid=-1;

	return true;
}
//发送邮件
function SendMail(operation){
	var ajax = new AW.XML.Table;
	if( packMailData(operation,ajax) ){
		setLoading(true,"正在发送");
		$("btnMailSend").disabled = true;
		var url = "../DaemonMail?operation=build";
		ajax.setURL(url);
		ajax.setRequestMethod('POST');
		var str  = '<?xml version="1.0" encoding="UTF-8" ?>\n';
		str += ajax.getXMLContent();
		ajax.setRequestData(str);
		ajax.request();
		ajax.response = function( text ){
			ajax.setXML(text);
			var xcode = ajax.getErrCode();
			if( xcode != '0' ){
				alert(xcode+ajax.getErrNote());
			}else{
				if( operation == 'NewMail'){
					alert("发送成功！");
					clearContent("send");
					clearList("send");
				}else{
					alert("已保存到草稿!");
					clearContent("draft");
					clearList("draft");
				}
				resetSend();
			}
			$("btnMailSend").disabled = false;
			setLoading(false);
		};
		
	}
}

//重置发件箱
function resetSend(){
	$("txt_receiptor").value = '';
	$("txt_cc").value = '';
	$("txt_title").value = '';
	$("txt_content").value = '';
	window.frames[0].location = "./upload.jsp";
}

//读取邮件内容到全局变量
function initGV(ajax){
	g_sender = ajax.getXMLText( "/xdoc/xout/readMail/sender" );
	g_sendtime = ajax.getXMLText( "/xdoc/xout/readMail/sendtime" );
	g_receiptor01 = ajax.getXMLText( "/xdoc/xout/readMail/receiptor01" );
	g_receiptor02 = ajax.getXMLText( "/xdoc/xout/readMail/receiptor02" );
	g_title = ajax.getXMLText( "/xdoc/xout/readMail/title" );
	if(ajax.getXMLText( "/xdoc/xout/readMail/fileid" ) != '' )	g_fileid = ajax.getXMLText( "/xdoc/xout/readMail/fileid" );
	else g_fileid=0;
	g_content = ajax.getXMLText( "/xdoc/xout/readMail/mailBody" );
	g_filename = ajax.getXMLText( "/xdoc/xout/readMail/filename" );
}

//转发当前邮件
function transMail(){
	resetSend();
	$("txt_title").value = "[转发]"+g_title;
	$("txt_content").value ="\r\n\r\n"
							+"---------------------------------------------------------------------------------------------------------------"
							+"\r\n发件人："+ g_sender 
							+"\r\n发送时间："+g_sendtime
							+"\r\n收件人："+g_receiptor01
							+"\r\n抄送："+g_receiptor02
							+"\r\n主题："+g_title
							+"\r\n\r\n"+g_content;
	if( g_fileid != 0){
		var url = "./upload.jsp?fileid=" + g_fileid+"&info="+encodeURI(g_filename);
		window.frames[0].location = url;
	}
	menu_click( $("a_new") );
}

//回复当前邮件
function reMail( mid ){
	resetSend();
	g_backid = mid;
	$("txt_receiptor").value = g_sender;
	$("txt_title").value ="[回复]"+ g_title;
	$("txt_content").value ="\r\n\r\n"
							+"---------------------------------------------------------------------------------------------------------------"
							+"\r\n发件人："+ g_sender 
							+"\r\n发送时间："+g_sendtime
							+"\r\n收件人："+g_receiptor01
							+"\r\n抄送："+g_receiptor02
							+"\r\n主题："+g_title
							+"\r\n\r\n"+g_content;
	menu_click( $("a_new") );
}

//每隔多少毫秒自动收一次信
window.setInterval('initMail()',120000);

function initMail(){
	load_receive("");
}

//邮件列表的收起
function drag(obj){
	var p = obj.parentNode;
	var pname = p.name;
	var preNode = obj.previousSibling;
	if(obj.className == 'drag_up'){
		obj.className = 'drag_down';
		obj.title = "收缩邮件列表";
		preNode.style.height = '20%';
	}else{
		obj.className = 'drag_up';
		obj.title = "展开邮件列表";
		preNode.style.height = '40%';
	}
}

//点击通讯录写邮件
function mailByAddr( str ){
	menu_click( $("a_new") );
	$("txt_receiptor").value = str;
}

//取得默认对帐员
function getDefPayer(){
	if( $("defaultPayer").innerHTML == '' ){
		$("defaultPayer").innerHTML == "正在读取默认配置……";
		var ajax = new AW.XML.Table;
		var url = "../DaemonMail?operation=default";	
		ajax.setURL(url);
		ajax.request();
		ajax.response = function( text ){
			ajax.setXML(text);
			var xcode = ajax.getErrCode();
			if( xcode != '0' ){
				alert(xcode+ajax.getErrNote());
			}else{
				var defPayer = ajax.getXMLText("xdoc/xout/defReceiptor");
				if( defPayer == 'undefine'){
					$("defaultPayer").innerHTML = "";
				}else{
					$("defaultPayer").innerHTML = "您的默认对帐员是:"+defPayer+"。<br/>如果您有结算方面的问题或请求，请写信给这个地址："+defPayer;
				}
			}
		};
	}
}


function search_receive(){
	var params = "&sender="+$F("search_sender")+"&min_receivetime="+$F("search_minreceivedate")+"&max_receivetime="+$F("search_maxreceivedate");
	load_receive(params);
}

function search_send(){
	var params = "&receiver="+$F("search_receiver")+"&min_sendtime="+$F("search_minsenddate")+"&max_sendtime="+$F("search_maxsenddate");
	load_send(params);
}