<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.certificate.bean.Config"
	errorPage="../errorpage/errorpage.jsp"%>
<%
request.setCharacterEncoding( "UTF-8" );
HttpSession session = request.getSession( false );
if( session == null ) throw new PermissionException( "您尚未登录,或已超时." );
Token token = ( Token ) session.getAttribute( "TOKEN" );
if( token == null ) throw new PermissionException( "您尚未登录,或已超时." );

	request.setCharacterEncoding( "UTF-8" );
	String err	  = request.getParameter("err");
	if(err != null && err.length()>0){
		err   = java.net.URLDecoder.decode(err, "UTF-8");
	}
	
	String filename = request.getParameter("filename");
	String id = request.getParameter("id");
	String type = request.getParameter("type");
	String sheetid = request.getParameter("sheetid");
	String btn_none = request.getParameter("btn_none");
	String operation = request.getParameter("operation");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style type="text/css">
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
}

form {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
}
</style>
<link href="./css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<script type="text/javascript">

function checkUp(){
	var fname = $('sfilename').value;
	if( fname == ""){
		alert("请选择需上传的文件！");
		return false;
	}
	var extlist = ".gif.jpg.jpeg.doc";
	if(extlist.indexOf(getExt(fname))==-1){
		alert("文件格式不支持！");
		return false;
	}else{
	}
	
	formUpload.submit();
}

function showImg(e){
	var extlist = ".gif.jpg.jpeg.doc";
	if(extlist.indexOf(getExt(e.value))==-1){
		window.parent.document.getElementById('uploadIMG').src="./images/errext.gif";
	}else{
		if(".doc".indexOf(getExt(e.value))!=-1){
			window.parent.document.getElementById('uploadIMG').src="./images/doc.gif";
		}else{
			window.parent.document.getElementById('uploadIMG').src=e.value;
		}
	}
}

//保存表体，根据option参数区分新建还是修改
function saveItem(imgUpdate){
	//证照种类
	var ctid = window.parent.document.getElementById('txt_ctid');
	ctid = ctid==null?"":ctid.value;
	//证照名称
	var cname = window.parent.document.getElementById('txt_cname').value;
	//证照编码
	var certificateID = window.parent.document.getElementById('txt_certificateid').value;
	//有效期
	var expiryDate = window.parent.document.getElementById('txt_expirydate').value;
	var yearDate = window.parent.document.getElementById('txt_yeardate').value;
	//商品条码
	var barcodeid = window.parent.document.getElementById('txt_barcodeid').value;
	//商品名称
	var goodsname = window.parent.document.getElementById('txt_goodsname').value;
	//备注
	var note = window.parent.document.getElementById('txt_note').value;
	
	if(<%=type%>==1){
		if(ctid==''){alert("请选择证照类型");return;}
		if(certificateID==''){alert("请填写证照编码");return;}
		if(expiryDate==''){alert("请填写有效期终止日");return;}
		var tmp = window.parent.document.getElementById('txt_yeardate');
		var tmps = window.parent.document.getElementById('input_yeardate');
		if(tmps.style.display!="none" && tmp.value==""){alert("请填写下次年审日期");return;}
	}else if(<%=type%>==2){
		if(ctid==''){alert("请选择证照类型");return;}
		if(certificateID==''){alert("请填写证照编码");return;}
		if(expiryDate==''){alert("请填写有效期终止日");return;}
		var tmp = window.parent.document.getElementById('txt_yeardate');
		var tmps = window.parent.document.getElementById('input_yeardate');
		if(tmps.style.display!="none" && tmp.value==""){alert("请填写下次年审日期");return;}
	}else if(<%=type%>==3){
		if(ctid==''){alert("请选择证照类型");return;}
		if(certificateID==''){alert("请填写证照编码");return;}
		if(expiryDate==''){alert("请填写有效期终止日");return;}
		if(barcodeid==''){alert("请填写对应商品条码");return;}
		if(goodsname==''){alert("商品名称未正确获取");return;}
	}else if(<%=type%>==4){
		if(ctid==''){alert("请选择证照类型");return;}
		if(certificateID==''){alert("请填写证照编码");return;}
		if(expiryDate==''){alert("请填写有效期终止日");return;}
		if(barcodeid==''){alert("请填写对应商品条码");return;}
		if(goodsname==''){alert("请填写对应商品名称");return;}
	}else{
		alert("未定义的证照类型:<%=type%>");return;
	}
	
	var parms = new Array();
	parms.push( "imgUpdate="+imgUpdate );
	parms.push( "action=editItem" );
	parms.push( "operation=<%=operation%>" );
	parms.push( "sheetid=<%=sheetid%>" );
	parms.push( "seqno=<%=id%>" );
	parms.push( "goodsName="+encodeURIComponent(goodsname) );
	parms.push( "certificateName="+encodeURIComponent(cname) );
	parms.push( "barcodeid="+barcodeid );
	parms.push( "certificateID="+certificateID );
	parms.push( "note="+encodeURIComponent(note) );
	parms.push("ctid="+ctid);
	parms.push( "expiryDate="+expiryDate );
	parms.push( "yearDate="+yearDate );
	parms.push( "imgFile=<%=filename%>" );
	var url = "../DaemonCertificate?"+parms.join( '&' );
	alert((url));
	var ajax = new AW.XML.Table;
	ajax.setURL((url));
	ajax.request();
	ajax.response = function(text){
		ajax.setXML(text);
		var errCode= ajax.getErrCode();
		if(errCode==0){
			alert('保存成功');
			//移除g_cts对应的元素。
			lockInput();
			if("<%=operation%>"=="edittxt"){
				window.location.replace("./upload.jsp?filename=<%=filename%>&err=OK&btn_none=save&id=<%=id %>&type=<%=type %>&sheetid=<%=sheetid%>");
			}
			$("save_btn").style.display="none";
			$("edit_btn").style.display="";
			
		}else{
			alert(ajax.getErrNote());
		}
	}
}

function initEditItem(){
	var ctid = window.parent.document.getElementById('txt_ctid');
	//证照名称
	var cname = window.parent.document.getElementById('txt_cname');
	//证照编码
	var certificateID = window.parent.document.getElementById('txt_certificateid');
	//有效期
	var expiryDate = window.parent.document.getElementById('txt_expirydate');
	var yearDate = window.parent.document.getElementById('txt_yeardate');
	//商品条码
	var barcodeid = window.parent.document.getElementById('txt_barcodeid');
	//商品名称
	var goodsname = window.parent.document.getElementById('txt_goodsname');
	if(ctid!=null) ctid.disabled=false;
	if(cname!=null) cname.disabled=false;
	if(certificateID!=null) certificateID.disabled=false;
	if(expiryDate!=null) expiryDate.disabled=false;
	if(yearDate!=null) yearDate.disabled=false;
	if(barcodeid!=null) barcodeid.disabled=false;
	if(<%=type%>==4){
		if(goodsname!=null) goodsname.disabled=false;
		barcodeid.onchange=function(){};
	}
	window.location.replace("./upload.jsp?operation=edittxt&filename=<%=filename%>&btn_none=save&id=<%=id %>&type=<%=type %>&sheetid=<%=sheetid%>");
}


function lockInput(){
	//
	var ctid = window.parent.document.getElementById('txt_ctid');
	//证照名称
	var cname = window.parent.document.getElementById('txt_cname');
	//证照编码
	var certificateID = window.parent.document.getElementById('txt_certificateid');
	//有效期
	var expiryDate = window.parent.document.getElementById('txt_expirydate');
	var yearDate = window.parent.document.getElementById('txt_yeardate');
	//商品条码
	var barcodeid = window.parent.document.getElementById('txt_barcodeid');
	//商品名称
	var goodsname = window.parent.document.getElementById('txt_goodsname');
	
	if(ctid!=null) ctid.disabled="disabled";
	if(cname!=null) cname.disabled="disabled";
	if(certificateID!=null) certificateID.disabled="disabled";
	if(expiryDate!=null) expiryDate.disabled="disabled";
	if(yearDate!=null) yearDate.disabled="disabled";
	if(barcodeid!=null) barcodeid.disabled="disabled";
	if(goodsname!=null) goodsname.disabled="disabled";
}

function getExt(sUrl)
{
        var arrList = sUrl.split(".");
        return arrList[arrList.length-1];
}


</script>
</head>
<body>
	<% if(filename==null|| filename.equals("null")) {%>
	<form id="formUpload" enctype="multipart/form-data"
		action="../DaemonImgUpLoad?btn_none=edit&seqno=<%=id %>&type=<%=type %>&sheetid=<%=sheetid %>"
		method="post">
		<input id="sfilename" name="sfilename" type="file"
			onchange="showImg(this)" /> &nbsp; <input type="button"
			id="btnsubmit" onclick="checkUp()" value="上传" /> &nbsp;&nbsp;文件最大<%=Config.getInstance(token).getFileMaxSize()%>K
		| <a href="uploadmore.jsp?sheetid=<%=sheetid%>&id=<%=id%>"
			target="_blank">多张图片上传</a>
	</form>
	<%}else if(operation!=null && "edittxt".equals(operation)){
		%>
	<form id="formUpload" enctype="multipart/form-data"
		action="../DaemonImgUpLoad?btn_none=edit&id=<%=id %>&type=<%=type %>&sheetid=<%=sheetid %>"
		method="post">
		<input value="保存" onclick="saveItem(false)" type="button" /> | <input
			id="sfilename" name="sfilename" type="file" onchange="showImg(this)" />
		&nbsp; <input type="button" id="btnsubmit" onclick="checkUp()"
			value="上传" /> &nbsp;&nbsp;文件最大<%=Config.getInstance(token).getFileMaxSize()%>K
		| <a href="uploadmore.jsp?sheetid=<%=sheetid%>&id=<%=id%>"
			target="_blank">多张图片上传</a>
	</form>
	<%
		}else{
			if("OK".equals(err)){
				out.println("已上传");
				out.println("<input class=button id=save_btn name=save_btn value=保存 type=button onclick=saveItem(true)  ");
				if("save".equals(btn_none)){
					out.print("style='display:none'");
				}
				out.println(" />");
				out.println("<input class=button id=edit_btn name=save_btn value=修改 type=button onclick=initEditItem()  ");
				if("edit".equals(btn_none)){
					out.print("style='display:none'");
				}
				out.println(" />");
			
			}else{
				out.println("错误："+err+"<input value='重传' type='button' onclick=\"window.location.href='./upload.jsp?id="+id+"&sheetid="+sheetid+"&type="+type+"'\">");
			}
	  	} %>
</body>
</html>