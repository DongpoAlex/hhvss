<%@page contentType="text/html;charset=UTF-8" session="false"
	import="java.lang.*" import="org.jdom.*"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.myshop.component.SelBook"
	errorPage="../errorpage/errorpage.jsp"%>

<%
	final int moduleid=3020102;
%>

<%
	request.setCharacterEncoding( "UTF-8" );

	HttpSession session = request.getSession( false );
	if( session == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	Token token = ( Token ) session.getAttribute( "TOKEN" );
	if( token == null ) throw new PermissionException( PermissionException.LOGIN_PROMPT );
	
	String venderid=token.getBusinessid();
	
	//查询用户的权限.
	Permission perm = token.getPermission( moduleid );
	if ( !perm.include( Permission.READ ) ) 
		throw new PermissionException( "您未获得操作此模块的授权,请管理员联系. 模块号:" + moduleid );
%>


<%
	SelBook sel_book = new SelBook(token);
	sel_book.setAttribute( "name", "txt_bookno" );
	sel_book.setAttribute( "id", "txt_bookno" );
	sel_book.setAttribute( "onchange", "check_bookno()" );
	
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
<script src="../AW/runtime/lib/aw.js" type="text/javascript"></script>

<script language="javascript" type="text/javascript">
var g_bookno;
var g_venderid="<%=venderid%>";
var g_prepayflag=0;
var g_majorid="";
function init(){
	data_input.style.display = "block";
	data_result.style.display = "none";
	divResult.innerHTML = "";
	getVenderInfo();
}

/**
 *以异步方式获取供应商信息，判断是否为预付款供应商
 */
function getVenderInfo( )
{
		vender_info.innerText="";			
		var url 		= "../DaemonVender";
		url += "?venderid=" + g_venderid;
		var courier 		= new AjaxCourier( url );
		courier.island4req  	= null;
		courier.reader.read 	= analyseReply4Vender;
		courier.call();	
}
/**
 *显示供应商信息
 */
function analyseReply4Vender( text ){
	
        g_prepayflag=0;
        island4reply.loadXML( text );        
	var elm_err = island4reply.XMLDocument.selectSingleNode("/xdoc/xerr");
          var xerr = parseXErr(elm_err);
          if( xerr.code != "0"){          
            if( xerr.code != "100" )
              {				
	       alert( xerr.toString() );
	       return;
	      }
	      else{
	    alert("无此供应商");	    
	    return;
	    }
          }
          else{
          var elm_prepayflag	= island4reply.XMLDocument.selectSingleNode( "/xdoc/xout/vender/prepayflag" );         
          if(elm_prepayflag.text!=0) {txt_majorid.readOnly=false;g_prepayflag=elm_prepayflag.text;vender_info.innerHTML+='(预付款供应商)';} 
          else{
          txt_majorid.value="";
          txt_majorid.readOnly=true;         
          }

	  }	
}
function load_major(id){

	span_majorname.innerHTML = "";
	if( "" == id ) return false;

	var url = "../DaemonSGroup?majorid=" + id;
	island4major.async = false;
	island4major.load( url );
	
	span_majorname.innerHTML += island4major.transformNode( format4major.documentElement );
	
	var elm_err 	= island4major.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr = parseXErr( elm_err );
	if ( xerr.code == "0" ){
		g_majorid=id;
		window.status = xerr.toString();
		return true;
	} else {
		g_majorid="";
		
		span_majorname.innerText =  xerr.note ;
		txt_majorid.value="";
		return false;
	}
}
function upload() {
	try{
	if( getBookno() == '' ){ return;}
	if( g_majorid==""&&g_prepayflag!=0) {alert("预付款供应商请输入课类号");return;}
	data_input.style.display = "none";
	data_result.style.display = "block";
	setLoading(true);
	
	var arr_name = new Array ( 'noteno', 'notevalue' );
	g_bookno=getBookno();
	var url = "../DaemonUpload?sheetname=liquidation&operation=check_upload&bookno="+g_bookno+"&majorid="+g_majorid;		
	var uploader  = new AjaxUploader( url, arr_name );
	
	uploader.text = txt_source.value;
	
	//对帐数据限制在 5000 条内
	uploader.rows_limit	= 5000;	
	uploader.island4req  	= island4req;
	uploader.reader.read 	= analyse_response;
	
	uploader.call();
	}catch(e){
		alert(e);
		setLoading( false );
	}
	
}

function analyse_response( text ){
	
	setLoading( false );
	island4result.loadXML( text );
	var elm_err = island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );
	var xerr 	= parseXErr( elm_err );
	if( xerr.code != "0" ){
		set_illegalinfo();
		btn_return.value = "返回";
	}
	else{
		set_llegalinfo();
	}
	divResult.innerHTML = island4result.transformNode( format4list.documentElement );		
}

function Validate(){

	
}
function getMajorid(){
	
}
function getBookno (){
	return txt_bookno.value;
}

function sendResult(){

	try{
		setLoading( true );
		btn_create.disabled=true;
		g_bookno=getBookno();
		var arr_name = new Array ( 'noteno', 'notevalue' );
		var url = "../DaemonUpload?sheetname=liquidation&operation=create_sheet&bookno="+g_bookno+"&majorid="+g_majorid;
		
			var uploader  = new AjaxUploader( url, arr_name );
		uploader.text = txt_source.value;
		uploader.rows_limit	= 5000;		
		uploader.island4req  	= island4req;
		uploader.reader.read 	= analyseReply;
		uploader.call();

	}catch(e){
		alert(e);
		setLoading( false );
	}
}


function analyseReply(text)
{
	 setLoading( false );
	 island4result.loadXML(text);
	 var elm_err = island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );	
	 var xerr 	= parseXErr( elm_err );	 
	 if( xerr.code =="0" ) {
		 var elm_sheetid	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout/sheetid" );
		 div_info.className='loading1';
		 div_info.style.width='35em';
		 div_info.innerHTML='已成功生成对帐申请单，单据号为：'+elm_sheetid.text;
		 div_info.style.display="block";
		 txt_source.value='';
		 btn_return.value = "录入下一单";
	 }
	 else{
	 	alert("生成单据失败："+xerr.note);
	 	div_info.style.display="none";	 
	 }	 	 
}

function check_bookno(){

	var bookno=getBookno();
	if( bookno != ""){
		setLoading(true);
		var url = "../DaemonUpload?sheetname=liquidation&operation=check_bookno&bookno="+bookno ;
		var courier = new AjaxCourier( url );
		courier.island4req  	= island4req;
		courier.reader.read 	= function(text){
			setLoading(false);
			island4result.loadXML(text);
			var elm_err 	= island4result.XMLDocument.selectSingleNode( "/xdoc/xerr" );	
		 	var xerr 	= parseXErr( elm_err );
		 	if( xerr.code =="0" ) {
				var elm_rel	= island4result.XMLDocument.selectSingleNode( "/xdoc/xout" );
				if( elm_rel != null ){
					var rel = elm_rel.text;
					if( rel=='PASS'){
						btn_upload.disabled=( txt_source.value!="" )?false:true;
					}else{
						alert("该公司业务单据暂不能对帐，请联系采购!");
						txt_bookno.value = '';
					}
				}else{
					alert("该公司业务单据暂不能对帐，请联系采购!");
					txt_bookno.value = '';
				}
			}else{
				alert("失败："+xerr.note);
			}
		} ;
		courier.call();
	}

}

function set_illegalinfo()
{
	div_info.style.display="none";
	btn_create.disabled=true;		
}

function set_llegalinfo()
{
	div_info.style.display="none";
	btn_create.disabled=false;
}
</script>

<xml id="island4req"> <xout /></xml>
<xml id="island4result" />
<xml id="island4major" />
<xml id="island4reply" />
<xml id="format4list" src="upload_validate.xsl" />
<xml id="format4major" src="format4major.xsl" />
</head>

<body onLoad="init()">
	<br />
	<br />
	<div id="data_input">
		<span>请选择分公司</span><font color=#ff9900>*</font>
		<%=sel_book%>&nbsp;&nbsp;&nbsp;<span id="vender_info"></span> <br />
		<br /> <span>课类号(预付款供应商填写)</span><input type="text" id="txt_majorid"
			value="" onchange="load_major(this.value)" readOnly="true">&nbsp;&nbsp;&nbsp;<span
			id="span_majorname"></span> <br /> <br /> <input type="button"
			id="btn_upload" value="导入数据" onClick="upload()" disabled="disabled" />&nbsp;&nbsp;<input
			type="button" value="清空数据" onclick="javascript:txt_source.value=''" />&nbsp;&nbsp;<span>请在下面复制粘贴excel内容，并检查数据:</span>
		<br />
		<div id="divExcel">
			<br /> &nbsp;&nbsp;&nbsp;&nbsp;单据号&nbsp;&nbsp;&nbsp;&nbsp;|
			&nbsp;&nbsp;&nbsp;&nbsp;单据金额&nbsp;&nbsp;&nbsp;&nbsp;| <br />
			<textarea rows="10" cols="80" id="txt_source" name="txt_source"
				onpropertychange="check_bookno()">
</textarea>
		</div>
		<br /> 注意：1、单据号中的字母全部为大写，请填写正确，否则不能通过系统验证。 <br></br> 2、每次上传最多5000行数据。
	</div>

	<br />

	<div id="data_result">
		<input type="button" id="btn_create" value="生成单据"
			onclick="sendResult()" disabled="disabled" /> <input type="button"
			id="btn_return" value="返回" onClick="init()" /> <br /> <br />
		<div id="div_info" class=wrong style="display: none;"></div>
		<br />
		<div id="divResult"></div>
	</div>
</body>
</html>
