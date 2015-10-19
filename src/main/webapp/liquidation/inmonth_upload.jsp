<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.SelBook"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid=3020213;
%>
<%
	request.setCharacterEncoding( "UTF-8" );
	
	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
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

function upload(){
	try{
		setLoading(true);
		island4req.loadXML( "" );
		var Arr_FieldName = new Array( );
		Arr_FieldName.push("monthid");
		Arr_FieldName.push("venderid");
		Arr_FieldName.push("vendername");
		Arr_FieldName.push("goodsid");
		Arr_FieldName.push("goodsname");
		Arr_FieldName.push("qty");
		Arr_FieldName.push("taxcost");
		Arr_FieldName.push("cost");
		
		var uploader = new AjaxMatrix( "../DaemonUpload?sheetname=inmonth&operation=upload",Arr_FieldName,txt_source.value);
		uploader.rows_limit	= 10000;
		uploader.island4req = island4req;
		uploader.reader.read = analyseSave;	
		uploader.call();
	} catch( e ) {
		alert(e);
		setLoading( false );
	}
}
function analyseSave(text){
	setLoading( false );
	$("island4result").loadXML( text );
	var elm_err = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code != 0 ){
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
} 

</script>

<xml id="island4req" />
<xml id="island4result" />

</head>

<body>
	<input type="button" id="btn_upload" value="导入数据" onclick="upload()" />&nbsp;&nbsp;
	<input type="button" value="清空数据"
		onclick="javascript:txt_source.value=''" />&nbsp;&nbsp;
	<span>请在下面复制粘贴excel内容:</span>

	<br />
	<div id="divExcel">
		<br /> &nbsp;&nbsp;&nbsp;&nbsp;年月&nbsp;&nbsp;&nbsp;&nbsp;|
		&nbsp;&nbsp;&nbsp;&nbsp;供应商编码&nbsp;&nbsp;&nbsp;&nbsp;|
		&nbsp;&nbsp;&nbsp;&nbsp;供应商名称&nbsp;&nbsp;&nbsp;&nbsp;|
		&nbsp;&nbsp;&nbsp;&nbsp;商品编号&nbsp;&nbsp;&nbsp;&nbsp;|
		&nbsp;&nbsp;&nbsp;&nbsp;商品名称&nbsp;&nbsp;&nbsp;&nbsp;|
		&nbsp;&nbsp;&nbsp;&nbsp;数量&nbsp;&nbsp;&nbsp;&nbsp;|
		&nbsp;&nbsp;&nbsp;&nbsp;含税进价金额&nbsp;&nbsp;&nbsp;&nbsp;|
		&nbsp;&nbsp;&nbsp;&nbsp;进价金额&nbsp;&nbsp;&nbsp;&nbsp;| <br />
		<textarea rows="10" cols="230" id="txt_source" name="txt_source"></textarea>
	</div>
	<div id="div_result"
		style="border: 1px solid #f00; display: none; padding: 6px; margin: 6px;"></div>
	<div>
		使用说明：<br /> 编辑一个EXCEL文档，按照，上面的字段顺序填写好数据，然后粘贴到文本框内。<br /> <font
			color="red">注意：请在导入数据的时候把满足率的百分号去掉！每次对多上传10000行数据！</font>
	</div>
</body>
</html>
