<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.SelBook"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	final int moduleid=5010103;
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


<?xml version="1.0" encoding="UTF-8"?>
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
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>

<script language="javascript" type="text/javascript">

function upload() {
	if( txt_source.value == '')
	return;
	div_result.style.display = "none";
	try{
		setLoading(true);
		var arr_name = new Array ( 'loginid', 'flag', 'value' );
		var url = "../DaemonMail?operation=environment";		
		var uploader  = new AjaxUploader( url, arr_name );
		uploader.text = txt_source.value;
		//对帐数据限制在 5000 条内
		uploader.rows_limit	= 5000;	
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
		return xerr.note;
	} else {
		var complete = $("island4result").XMLDocument.selectSingleNode( "/xdoc/xout/complete" )
		if( complete!=null && complete.text.length > 1 ){
			alert("部分数据修改成功");
			div_result.innerHTML = "下列用户数据没有成功修改,请检查系统中是否有这些用户：<br/>"+complete.text;
			div_result.style.display = "block";
		}else{
			alert("修改成功");
		}
		txt_source.value='';
	}
	setLoading( false );
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
		<br /> &nbsp;&nbsp;&nbsp;&nbsp;供应商编码&nbsp;&nbsp;&nbsp;&nbsp;|
		&nbsp;&nbsp;&nbsp;&nbsp;默认属性&nbsp;&nbsp;&nbsp;&nbsp;|
		&nbsp;&nbsp;&nbsp;&nbsp;默认值&nbsp;&nbsp;&nbsp;&nbsp;| <br />
		<textarea rows="10" cols="80" id="txt_source" name="txt_source"></textarea>
	</div>

	<div id="div_result"
		style="border: 1px solid #f00; display: none; padding: 6px; margin: 6px;"></div>
	<div>
		使用说明：<br /> 编辑一个EXCEL文档，按照，供应商编码，默认对象，默认值的顺序填写好数据，然后粘贴到文本框内。<br />
		最多一次只能导入5000条数据，数据超过1000条可能需要较长的处理时间，请耐心等待。<br /> <br />
		例如：要指定一批供应商的默认对帐员。<br /> EX0537 defaultPayer p001<br /> EY1200
		defaultPayer p002<br /> <br /> <b>默认对帐员是：defaultPayer
			默认采购员是：defaultBuyer 请不要弄错。</b>
	</div>
</body>
</html>
