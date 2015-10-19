<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.SelBook"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid=8000017;
%>
<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );
	if ( !perm.include( Permission.READ ) ) 
		throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style>
.wrong {
	width: 220px;
	background-color: yellow;
	border-style: solid;
	border-color: red;
	border-width: 1px;
}

.warning {
	color: #F00;
	background-color: #FFF;
}

.ok {
	color: #060;
	background-color: #FFF;
}

.ok {
	
}

.loading1 {
	width: 15em;
	text-align: center;
	background: #FFA826;
	color: #FFF;
}
</style>
<link href="../css/aw_style.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../js/ajax.js" type="text/javascript"> </script>
<script language="javascript" src="../js/XErr.js" type="text/javascript"> </script>
<script language="javascript" src="../js/popup.js"
	type="text/javascript"> </script>
<script language="javascript" type="text/javascript">

function upload() {
if( txt_source.value == '')
return;
div_result.style.display = "none";
	try{
		setLoading(true);
		var arr_name = new Array ( 'venderid', 'ctid' );
		var url = "../DaemonUpload?sheetname=cert_export_vender&operation=upload";		
		var uploader  = new AjaxUploader( url, arr_name );
		uploader.text = txt_source.value;
		//对帐数据限制在 10000 条内
		uploader.rows_limit	= 10000;	
		uploader.island4req  	= island4req;
		uploader.reader.read 	= analyse_response;
		uploader.call();
	} catch( e ) {
		alert(e);
		setLoading( false );
	}
}

function analyse_response( text ){
	$("island4result").loadXML( text );
	var elm_err = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code != 0 ){
		alert(xerr.note);
		setLoading( false );
		return;
	} else {
		var complete = $("island4result").XMLDocument.selectSingleNode( "/xdoc/complete" );
		if( complete!=null && complete.text.length > 1 ){
			alert("部分数据上传成功");
			div_result.innerHTML = "下列数据没有成功添加,请检查系统数据：<br/>"+complete.text;
			div_result.style.display = "block";
		}else{
			alert("上传成功");
		}
		txt_source.value ='';
	}
	
	setLoading( false );
}

function download(){
	var parms = new Array();
	parms.push("operation=getExportVenderList");
	
	var url  = "../DaemonDownloadExcel?"+parms.join('&');
	
	window.location.href = url;
	
}

</script>

<xml id="island4req" />
<xml id="island4result" />
<title>导出供应商维护</title>
</head>

<body>
	<input type="button" id="btn_upload" value="导入数据" onclick="upload()" />&nbsp;&nbsp;
	<input type="button" value="清空数据"
		onclick="javascript:txt_source.value=''" />&nbsp;&nbsp;
	<input type="button" id="btn_download" value="下载已导入数据"
		onclick="download()" />
	<span>请在下面复制粘贴excel内容:</span>

	<br />
	<div id="divExcel">
		<br /> &nbsp;&nbsp;&nbsp;&nbsp;供应商编码&nbsp;&nbsp;&nbsp;&nbsp;|
		&nbsp;&nbsp;&nbsp;&nbsp;证照类型编码&nbsp;&nbsp;&nbsp;&nbsp;| <br />
		<textarea rows="10" cols="230" id="txt_source" name="txt_source"></textarea>
	</div>
	<div id="div_result"
		style="border: 1px solid #f00; display: none; padding: 6px; margin: 6px;"></div>
	<div>
		使用说明：<br /> 编辑一个EXCEL文档，按照，上面的字段顺序填写好数据，然后粘贴到文本框内。<br /> <font
			color="red">注意：每次最多上传10000行数据！</font>
	</div>
</body>
</html>
