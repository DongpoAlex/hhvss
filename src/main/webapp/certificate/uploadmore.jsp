<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.certificate.bean.Config"
	errorPage="../errorpage/errorpage.jsp"%>
<%
	request.setCharacterEncoding("UTF-8");
	HttpSession session = request.getSession( false ); 
	if (session == null) throw new PermissionException("您尚未登录,或已超时.");
	Token token = (Token) session.getAttribute("TOKEN");
	if (token == null) throw new PermissionException("您尚未登录,或已超时.");

	String sheetid = request.getParameter("sheetid");
	String seqno   = request.getParameter("seqno");
	String type    = request.getParameter("type");
	String dis_addimg = "none";
	if("3".equals(type) || "4".equals(type) || "2".equals(type)){
		dis_addimg = "";
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="./css.css" rel="stylesheet" type="text/css" />
<style type="text/css" media="all">
* {
	margin: 0;
	padding: 0;
}

img {
	border: none;
}

ul {
	list-style-type: none;
}

ul li {
	padding: 2px 4px;
}

body {
	font-family: 宋体, 黑体, verdana, Arial;
	font-size: 12px;
	color: #333;
	background: #DDDDDD;
	padding: 0;
}

.box {
	border: 1px solid #CCC;
	background: #FFF;
	padding: 8px;
	margin: 5px;
	clear: both;
}

.title {
	background: #F0F0F0;
	padding: 5px;
	font-weight: bold;
}

.tooltip {
	background: #F0F0F0;
	border-color: #bbb;
}

.tooltip h1 {
	color: #A8DF00;
	font-family: 微软雅黑, 黑体, 宋体, verdana, Arial;
}

.titlebutton {
	float: right;
}

.uploading {
	background: #FFF;
	color: #444;
	text-align: left;
	width: 500px;
	padding: 4px;
}

.imgshow {
	float: left;
}

.up {
	float: left;
	margin-top: 80px;
}

.imgshow image {
	border: 1px solid #ddd;
	margin: 2px;
	padding: 1px;
	display: inline;
	width: 100px;
	height: 100px;
}

.uploadcontrol {
	margin: 4px 0;
	border-bottom: 1px solid #F0F0F0;
	height: 105px;
}
</style>
<script language="javascript" src="../js/Date.js" type="text/javascript"> </script>
<script language="javascript" src="../AW/runtime/lib/aw.js"
	type="text/javascript"> </script>
<script language="javascript" src="./common.js" type="text/javascript"> </script>
<script type="text/javascript">
function oninit(){
	//读取图片信息
	setLoading(true);
	var parms = new Array();
	parms.push("action=getImageList");
	parms.push("sheetid=<%=sheetid%>");
	parms.push("seqno=<%=seqno%>");
	var url  = "../DaemonCertificate?"+parms.join('&');
	var table = new AW.XML.Table;
	table.setURL(url);
	table.request();
	table.response = function(text){
		setLoading(false);
		table.setXML(text);
		var xcode = table.getErrCode();
		if( xcode != '0' ){//处理xml中的错误消息
			alert( xcode+table.getErrNote());
		}else{
			//判断图片个数
			var node_row 	= table.getXML().selectNodes( "/xdoc/xout/img/row" );
			var rows = node_row.length;
			var imgsrc = "./images/noimg.gif";
			if(rows==0){
				//没有图片,创建一个上传
				uploadcreate();
			}else{
				//取得最大的一個圖片id
				var maxid = 0;
				for(var i=0;i<rows;i++){
					var imgseqno = Number(node_row[i].selectSingleNode("imgseqno").text);
					maxid=(imgseqno>maxid)?imgseqno:maxid;
				}
				//創建上傳控件
				for(var i=0;i<maxid;i++){
					uploadcreate();
				}
				for(var i=0;i<rows;i++){
					var imgseqno = node_row[i].selectSingleNode("imgseqno").text;
					var filename = node_row[i].selectSingleNode("imgfile").text;
					$("imgshow"+imgseqno).innerHTML = "<a target='_blank' href='../DaemonImgDownLoad?filename="+filename+"'><img src='../DaemonImgDownLoad?filename="+filename+"&tt="+new Date().getTime()+"'/></a>";
					$('msg'+imgseqno).innerHTML="←点击替换图片。"; 
				}
			}
		}
	}
}

var currentItemID = 0;  //用于存放共有多少个上传控件了
//创建一个上传控件
var uploadcreate = function(itemid){
    currentItemID ++;
    if(itemid == null)
    {
        itemid = currentItemID;
    }  
    var strContent = "<div class='uploadcontrol'>";
    strContent += "<div id='imgshow"+itemid+"' class='imgshow'><img src='./images/noimg.gif'></div>";
    strContent += "<div class='up'>";
    strContent += "<form action='../DaemonImgUpLoad?seqno=<%=seqno%>&sheetid=<%=sheetid%>&imgseqno="+itemid+"' id='form"+itemid+"' enctype='multipart/form-data' method='post' target='hidden_frame"+itemid+"'>";
    strContent += "<span style='float:left'>第 "+itemid+" 张图片：";
    strContent += "<input type='file' id='file"+itemid+"' name='file' onchange='checkUp("+itemid+")'></span>";
   // strContent += "<input type='button' onclick='checkUp("+itemid+")' value='上传' />";
   	strContent += "<span style='float:left;margin-left:6px;' id='msg"+itemid+"'>←点击新增图片</span>";
	strContent += "<iframe name='hidden_frame"+itemid+"' id='hidden_frame"+itemid+"' style='display: none' height='0' width='0'></iframe>";
    strContent += "</div>";
    strContent += "</div>";
    var item = document.createElement("item");
	item.innerHTML=strContent;
    $('uploadbox').appendChild(item);
};

function callback(itemid,filename,msg){
	itemid =Number(itemid);
	if(msg=='OK'){
		$("file"+itemid).outerHTML = $("file"+itemid).outerHTML;
		$("imgshow"+itemid).innerHTML = "<a target='_blank' href='../DaemonImgDownLoad?filename="+filename+"'><img src='../DaemonImgDownLoad?filename="+filename+"&tt="+new Date().getTime()+"'/></a>"; 
		$('msg'+itemid).innerHTML="←点击替换图片。";
		$('file'+itemid).style.display="";
		if(currentItemID==1){
			var html = "<img onclick='openImg(this)' border='1' width='100' height='100' id='uploadIMG' src='../DaemonImgDownLoad?filename="+filename+"&tt="+new Date().getTime()+"'>";
		}else{
			var html = "<img border='1' width='100' height='100' id='uploadIMG' src='./images/imgmore.jpg'>";
		}
		window.dialogArguments.divuploadIMG.innerHTML=html;
	}else{
		$("file"+itemid).outerHTML = $("file"+itemid).outerHTML;
		$('file'+itemid).style.display="";
		$('msg'+itemid).innerHTML="";
		alert(decodeURI(msg));
	}
}


function checkUp(id){
	var fname = $('file'+id).value;
	if( fname == ""){
		alert("请选择需上传的文件！");
		return false;
	}
	var extlist = ".gif.jpg.jpeg.GIF.JPG.JPEG";
	if(extlist.indexOf(getExt(fname))==-1){
		alert("文件格式不支持！");
		return false;
	}else{
		try{
			$('msg'+id).innerHTML="图片正在上传，请稍后……";
			$('file'+id).style.display="none";
		}catch(e){
			alert(e)
		}
	}
	$('form'+id).submit();
}

function getExt(sUrl)
{
        var arrList = sUrl.split(".");
        return arrList[arrList.length-1];
}

//是否显示上传后的图片
	var isshowpic = true;  
	var uploadshowpic = function(el){
	    isshowpic = !(isshowpic);
	    if(isshowpic)
	    {
	    	$('showtop').innerHTML = "图片显示关闭";
	    	$('showend').innerHTML = "图片显示关闭";
	    }
	    else
	    {
	    	$('showtop').innerHTML = "图片显示开启";
	    	$('showend').innerHTML = "图片显示开启";
	    }
	};
</script>
</head>
<body onload="oninit();">
	<a name="#top"></a>
	<div id="tipbox" class="box tooltip">
		<a href="#" onclick="window.close()">[关闭]</a>
		<div class="content">
			<h1>证照图片维护</h1>
		</div>
	</div>
	<div class="tooltip box">
		<a href="#bottom" onclick="uploadcreate();"
			style="display: <%=dis_addimg %>">添加一张图片</a> <a id="showtop" href="#"
			onclick="uploadshowpic();">图片显示关闭</a>
	</div>
	<div id="uploadbox" class="box"></div>
	<div class="tooltip box">
		<a href="#bottom" onclick="uploadcreate();"
			style="display: <%=dis_addimg %>">添加一张图片</a> <a id="showend" href="#"
			onclick="uploadshowpic();">图片显示关闭</a>
	</div>

	<a name="#bottom"></a>
</body>
</html>