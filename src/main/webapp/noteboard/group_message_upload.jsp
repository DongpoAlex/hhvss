<%@page contentType="text/html;charset=utf-8" session="false"
	errorPage="../errorpage/errorpage.jsp" import="java.lang.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"%>

<%

	final int moduleid=5030101;
%>
<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );
	if ( !perm.include( Permission.INSERT ) ) 
		throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<style>
.divmess {
	background-color: #EEFFF3;
	color: #006600;
	padding: 5px;
	border: 1px solid #006600;
	width: 200px;
	display: none;
	position: absolute;
	z-index: auto;
	background-image: url(../img/loading.gif);
	background-repeat: no-repeat;
	background-position: 2px 6px;
	padding-left: 26px;
}

#divTitle {
	font: bold 18px "楷体_GB2312";
	text-align: center;
}
</style>
<xml id="island4sendGroup" />
<xml id="island4result" />
<script language="javascript" src="../js/ajax.js"> </script>
<script language="javascript" src="../js/XErr.js"> </script>
<script language="javascript" src="../js/Date.js"> </script>
<script language="javascript" src="../js/Number.js"> </script>

<!-- ActiveWidgets stylesheet and scripts -->
<script src="../AW/runtime/lib/aw.js"></script>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<script language="javascript">
extendDate();
extendNumber();
function $(id){
	return document.getElementById(id);
}
function setLoading( on ,text ){
	if(on)
		divloading.style.display = "block";
	else
		divloading.style.display = "none";
	divloading.innerHTML = "数据读取中,请稍候……";
	divloading.style.left = (window.screen.availWidth) / 2 -200;
	divloading.style.top = (window.screen.availHeight) / 2 -200;
	if( text != null ){
		divloading.innerHTML = text +",请等候……";
	}
}
//群发功能

function sendGroup(){
	set_visible("div_sendGroup");
	set_loadinginfo(false);
	initGroupText();
}

//发送邮件信息
function sendGroupMail(){
	if( checkData() ){
		if(initData()){
			setLoading(true, "正在上传数据");
			var mailTitle = txt_sendGroupTitle.value;
			var maildate = txt_sendGroupDate.value;
			var mailContent = txt_content.value;
			var parms = new Array();
			parms.push( "title="+encodeURIComponent(mailTitle) );
			parms.push( "expiredate="+maildate );
			parms.push( "operation=send" );
			var url = ("../DaemonVenderMsg?" + parms.join( "&" ));
			var courier = new AjaxCourier( url);
			courier.island4req  	= island4sendGroup;
			courier.reader.read 	= function(text){
				island4result.loadXML( text );
				var elm_err = island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
				var xerr = parseXErr( elm_err );
				if ( xerr.code == "0" ){
					alert("发送成功");
					initGroupText();
				}else{
					alert("发送失败:"+xerr.note);
					initGroupText();
				}
				setLoading(false);
			};
			courier.call();
		}
	}
}


function initData(){
	setLoading(true , "正在分析邮件内容");
	var txt_data = txt_sendGroupData.value;
	var arr_lines = txt_data.split("\n");
	//检查行数
	if ( arr_lines.length < 2 ){
		alert("请导入邮件内容！");
		setLoading(false);
		return false;
	}
	// windows"\r\n"
	//将每行后的\r\n去掉
	for (var i=0; i<arr_lines.length; i++){
		var n = arr_lines[i].length;
		arr_lines[i] = arr_lines[i].substring(0, n-1);
	}
	
	var arr_matrix = new Array( );

	//将数据组成矩阵，并对数据长度检查
	for( var i=0; i<arr_lines.length; i++ ) {
		var line = new String ( arr_lines[i] );
		if( line == "" ) continue;
		var arr = line.split( "\t" );
		if( arr.length <2 ){
			alert("第 "+(i+1)+" 行的列太少了");
			setLoading(false);
			return false;
		}else if(arr.length >21){
			alert("第 "+(i+1)+" 行的列太多了，系统将自动截取多余的列");
		}
		arr_matrix[ i ] = arr;
	}
//////////////////////////////////////////////////
	island4sendGroup.loadXML("");
	var doc = island4sendGroup.XMLDocument;
	var elm_root = doc.selectSingleNode("/");
	var elm_xdoc = doc.createElement( "xdoc" );
	var elm_set  = doc.createElement( "mailData" );
	var elm_content = doc.createElement( "content" );
	var mailContent = txt_content.value;
	elm_content.appendChild( doc.createTextNode( mailContent ) );
	elm_root.appendChild( elm_xdoc );
	elm_xdoc.appendChild( elm_set );
	elm_xdoc.appendChild( elm_content );
/////////////////////////////////////////////////
	//组合表体部分
	for( var i=0; i<arr_matrix.length && 21; i++ ) {
		var elm_row = doc.createElement( "row" );
		elm_set.appendChild( elm_row );
		for( var j=0; j<arr_matrix[i].length; j++ ) {
			var elm;
			if( j == 0 ){ 
				elm = doc.createElement( "venderid" );
			}else{
				elm = doc.createElement( "field"+j );
			}
			elm.appendChild( doc.createTextNode( arr_matrix[i][j] ) );
			elm_row.appendChild( elm );
		}
	}
	setLoading(false);
	return true;
}

function initGroupText(){
	txt_sendGroupData.value = "";
	txt_sendGroupTitle.value = "";
	txt_sendGroupDate.value = "";
	
}

function checkData(){
	if ( txt_sendGroupData.value == "" || txt_sendGroupTitle.value == "" || txt_sendGroupDate.value == "" ){
		alert("请填写完整信息");
		return false;
	}else{
		return true;
	}

    if(txt_content.legth() >500){
    	alert("消息说明太长，请删减。");
		return false;
    }
}
/*
end by baijian
*/
</script>
</head>
<body>
	<div id="divTitle">群消息发送器</div>

	<hr size="1" />
	<div id="div_sendGroup">
		<div style="margin-bottom: 10px;">
			消息标题：<input type="text" size="60" maxlength="30"
				id="txt_sendGroupTitle"></input>
		</div>
		<div style="margin-bottom: 10px;">
			消息说明：
			<textarea cols="60" rows="3" id="txt_content"></textarea>
			(最多500字)
		</div>
		<div style="margin-bottom: 10px;">
			有效期至：<input type="text" size="20" id="txt_sendGroupDate"
				onblur="checkDate(this)"></input>
		</div>
		<div style="margin-bottom: 10px;">
			注意：第一行第一列字段为"title"则改行作为各列标题（所有收件人都能看到） <br />
			供应商编号&nbsp;&nbsp;|&nbsp;&nbsp;消息内容段 1－20 &nbsp;&nbsp; <span
				style="color: #999">(请在Excel表格中编辑好邮件内容后，粘贴在下面。)</span><br />
			<textarea cols="110" rows="10" id="txt_sendGroupData"></textarea>
			<br /> <br /> <input type="button" onclick="sendGroupMail()"
				value="发送" /> <input type="button"
				onclick="txt_sendGroupData.value=''" value="清空" />
		</div>
	</div>

	<div id="divloading" class="divmess"></div>
</body>
</html>